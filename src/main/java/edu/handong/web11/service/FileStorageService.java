package edu.handong.web11.service;

import edu.handong.web11.domain.Attachment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Set<String> ALLOWED = Set.of("jpg", "jpeg", "png", "gif", "pdf", "txt");
    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir}") String path) throws IOException {
        uploadDir = Path.of(path).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);
    }

    public Attachment store(MultipartFile file) {

        // 빈파일은 업로드 거부 하는 VALIDITY같은거
        if (file.isEmpty()) throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");

        // 업로드시 파일명을 변수에 저장
        String original = file.getOriginalFilename();
        //다시 파일명이 없는지, 혹은 문자 포함시 거부
        if (original == null || original.isBlank() || original.contains("/") || original.contains("\\") || original.contains(".."))
            throw new IllegalArgumentException("잘못된 파일 이름입니다.");
        // 지나치게 긴 파일명으로 인한 문제 방지
        if (original.length() > 240) throw new IllegalArgumentException("파일 이름이 너무 깁니다.");
        //마지막 점으 기준으로 확장자 추출
        int dot = original.lastIndexOf('.');
        //확장자를 JPG,jpg등 대소문자 구분을 없애 검사한다
        String ext = dot < 0 ? "" : original.substring(dot + 1).toLowerCase(Locale.ROOT);
        // 이상한 파일형식 거부
        if (!ALLOWED.contains(ext)) throw new IllegalArgumentException("허용 형식: jpg, jpeg, png, gif, pdf, txt");
        //사이즈가 너무 크면 거부
        if (file.getSize() > 10L * 1024 * 1024) throw new IllegalArgumentException("파일은 10MB 이하만 가능합니다.");
        //같은 워본 파일명이 업로드 되어도 충돌하지 않도록 UUID 저장명을 생성한다.
        String stored = UUID.randomUUID() + "." + ext;

        //그 긴거...
        Path target = uploadDir.resolve(stored).normalize();
        if (!target.startsWith(uploadDir)) throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        try (InputStream input = file.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
            // Teaching sample: MIME type is inferred by extension, not trusted from request headers.
            String contentType = switch (ext) {
                case "jpg", "jpeg" -> "image/jpeg";
                case "png" -> "image/png";
                case "gif" -> "image/gif";
                case "pdf" -> "application/pdf";
                default -> "text/plain";
            };
            return new Attachment(original, stored, contentType, file.getSize());
        } catch (IOException e) {
            try {
                Files.deleteIfExists(target);
            } catch (IOException ignored) {
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 실패", e);
        }
    }

    public Resource load(Attachment attachment) {
        Path target = uploadDir.resolve(attachment.getStoredFilename()).normalize();
        if (!target.startsWith(uploadDir) || !Files.isRegularFile(target))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일이 없습니다.");
        return new PathResource(target);
    }

    public void deleteIfExists(Attachment attachment) {
        try {
            Files.deleteIfExists(uploadDir.resolve(attachment.getStoredFilename()).normalize());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 실패", e);
        }
    }
}
