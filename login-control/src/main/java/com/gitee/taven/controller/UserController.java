package com.gitee.taven.controller;

import com.gitee.taven.pojo.ApiResult;
import com.gitee.taven.pojo.CurrentUser;
import com.gitee.taven.pojo.UserBO;
import com.gitee.taven.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public ApiResult login(@RequestBody UserBO userBO) {
    	//这里要判断登录的类型是使用用户名和密码还是使用token
    	//是用户名和密码登录，先验证通过后再找到用户名对应的token，删掉之前的token，再返回新生成的的token
    	//如果是token登录，直接验证在redis中是否存在会话
        return new ApiResult(200, "登录成功", userService.buildUserInfo(userBO));
    }

    @GetMapping("user/info")
    public ApiResult info() {
    	System.out.println("user info ");
        return new ApiResult(200, null, CurrentUser.get());
    }

    @PostMapping("logout")
    public ApiResult logout(@RequestHeader("Authorization") String jwt) {
        userService.logout(jwt);
        return new ApiResult(200, "成功", null);
    }

}

