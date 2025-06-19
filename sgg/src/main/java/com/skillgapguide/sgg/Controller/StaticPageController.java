package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.StaticPageDTO;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/pages")
public class StaticPageController {
    @Autowired
    private StaticPageService pageService;
    @GetMapping("/getHomePage")
    public Response<?> getContentHome(){
        return new Response<>(EHttpStatus.OK,pageService.getContent("Home"));
    }
    @PostMapping("/updateHomePage")
    public Response<?> updateContentHome(@RequestBody StaticPageDTO dto){
        return new Response<>(EHttpStatus.OK,pageService.updateContent(dto,"Home"));
    }
    @GetMapping("/getAboutUs")
    public Response<?> getContentAboutUs(){
        return new Response<>(EHttpStatus.OK,pageService.getContent("AboutUs"));
    }
    @PostMapping("/updateAboutUs")
    public Response<?> updateContentAboutUs(@RequestBody StaticPageDTO dto){
        return new Response<>(EHttpStatus.OK,pageService.updateContent(dto,"AboutUs"));
    }
    @GetMapping("/getSocialLink")
    public Response<?> getContentSocialLink(){
        return new Response<>(EHttpStatus.OK,pageService.getContent("SocialLink"));
    }
    @PostMapping("/updateSocialLink")
    public Response<?> updateContentSocialLink(@RequestBody StaticPageDTO dto){
        return new Response<>(EHttpStatus.OK,pageService.updateContent(dto,"SocialLink"));
    }
    @GetMapping("/getPrivacy")
    public Response<?> getContentPrivacy(){
        return new Response<>(EHttpStatus.OK,pageService.getContent("Privacy"));
    }
    @PostMapping("/updatePrivacy")
    public Response<?> updateContentPrivacy(@RequestBody StaticPageDTO dto){
        return new Response<>(EHttpStatus.OK,pageService.updateContent(dto,"Privacy"));
    }
    @GetMapping("/getTermsOfService")
    public Response<?> getContentTerms(){
        return new Response<>(EHttpStatus.OK,pageService.getContent("Terms"));
    }
    @PostMapping("/updateTermsOfService")
    public Response<?> updateContentTermsOfService(@RequestBody StaticPageDTO dto){
        return new Response<>(EHttpStatus.OK,pageService.updateContent(dto,"Terms"));
    }
}
