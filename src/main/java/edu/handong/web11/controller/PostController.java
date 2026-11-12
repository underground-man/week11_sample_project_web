package edu.handong.web11.controller;

import edu.handong.web11.domain.Post;
import edu.handong.web11.dto.PostForm;
import edu.handong.web11.service.PostService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class PostController {
    private final PostService posts;

    public PostController(PostService posts) {
        this.posts = posts;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String list(Model model) {
        model.addAttribute("posts", posts.all());
        return "posts/list";
    }

    @GetMapping("/posts/new")
    public String form(Model model) {
        model.addAttribute("postForm", new PostForm());
        return "posts/form";
    }



    @PostMapping("/posts")
    public String create(
            //FORM을 입력값으로 받고 POSTFORM객체에 바인딩하고 검증한다.
            @Valid @ModelAttribute("postForm") PostForm form,
            //오류를 전달받기 위한 변수
            BindingResult errors, @RequestParam(name = "files", required = false) List<MultipartFile> files) {

        // 검증 실패시 저장하지 않고 작성화면을 반환한다.
        if (errors.hasErrors()) return "posts/form";


        //삼항 연산자를 사용해서 파일 선택되지 않은 경우 NULL, 선택될시 게시글정보, 파일목록을 반환하고 저장된 게시글 ID를 SERVICE에 건낸다
        Long id = posts.create(form, files == null ? List.of() : files);
        //저장결과를 보여주기 위해 게시글 상세 화면으로 이동한다.
        return "redirect:/posts/" + id;
    }

    @GetMapping("/posts/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("post", posts.get(id));
        return "posts/detail";
    }

    @GetMapping("/posts/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Post post = posts.get(id);
        PostForm form = new PostForm();
        form.setTitle(post.getTitle());
        form.setContent(post.getContent());
        model.addAttribute("postForm", form);
        model.addAttribute("postId", id);
        return "posts/form";
    }

    @PostMapping("/posts/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("postForm") PostForm form, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("postId", id);
            return "posts/form";
        }
        posts.update(id, form);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable Long id) {
        posts.delete(id);
        return "redirect:/posts";
    }
}
