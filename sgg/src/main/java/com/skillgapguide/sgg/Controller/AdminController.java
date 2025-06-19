package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.UserListRequest;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @GetMapping("/getAllUser")
    public Response<?> getAllUserList(@ModelAttribute UserListRequest userFilterRequest){
        return new Response<>(EHttpStatus.OK, userService.getAllUser(userFilterRequest));
    }
    @GetMapping("/getUserDetail/{email}")
    public Response<?> getUserDetail(@PathVariable String email){
        return new Response<>(EHttpStatus.OK, userService.getUserDetail(email));
    }
    @PostMapping("/disableAccount/{email}")
    public  Response<?> disableAcc(@PathVariable String email){
        return new Response<>(EHttpStatus.OK,userService.disableAccount(email));
    }
    @PostMapping("/enableAccount/{email}")
    public  Response<?> enableAcc(@PathVariable String email){
        return new Response<>(EHttpStatus.OK,userService.enableAccount(email));
    }
}
