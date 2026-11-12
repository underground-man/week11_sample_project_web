package edu.handong.web11.controller;

import edu.handong.web11.domain.Attachment;
import edu.handong.web11.repository.AttachmentRepository;
import edu.handong.web11.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;


import java.util.Set;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {


    private static final Set <String> PREVIEWABLE_IMAGE_TYPES = Set.of("image/jpeg","image/png","image/gif");


    private final AttachmentRepository attachments;
    private final FileStorageService storage;
    public AttachmentController(AttachmentRepository attachments, FileStorageService storage) {
        this.attachments = attachments; this.storage = storage;
    }
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {

        //해당 ID가 없으면 404HTTP 오류 발생


        Attachment file = attachments.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "첨부파일이 없습니다."));

        //UUID 저장명으로 실제파일을 불러온다.
        Resource resource = storage.load(file);

        //서버의 UUID 저장명이 아닌 사용자가 업로드한 원본 파일명을 사용한다
        String disposition = ContentDisposition.attachment()
            .filename(file.getOriginalFilename(), StandardCharsets.UTF_8)
            .build().toString();

        //상태코드, 파일형식, 다운로드 파일명과 실제 파일 데이터를 HTTP 응답으로 구성한다.
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
            .body(resource);
    }
    // STUDENT TODO: GET /attachments/{id}/view (image preview).
    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> view(@PathVariable Long id){
        Attachment file = attachments.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"첨부파일이 없습니다."));

        if (!PREVIEWABLE_IMAGE_TYPES.contains(file.getContentType())){
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"이미지 파일만 미리보기 할 수 있습니다.");
        }

        Resource resource = storage.load(file);

        MediaType mediaType = MediaType.parseMediaType(file.getContentType());



        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }
}
