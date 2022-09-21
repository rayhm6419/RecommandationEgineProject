package com.laioffer.twitch.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.twitch.entity.response.LoginResponseBody;
import com.laioffer.twitch.entity.response.request.LoginRequestBody;
import com.laioffer.twitch.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(@RequestBody LoginRequestBody requestBody, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstname = loginService.verifyLogin(requestBody.getUserId(), requestBody.getPassword()); //数据库自动检查login和注册的userid是否match

        // Create a new session for the user if user ID and password are correct, otherwise return Unauthorized error.
        if (!firstname.isEmpty()) {
            // Create a new session, put user ID as an attribute into the session object, and set the expiration time to 600 seconds.
            HttpSession session = request.getSession();
            session.setAttribute("user_id", requestBody.getUserId()); //登陆过后保存在session里
            session.setMaxInactiveInterval(600); //设置保存的时间

            LoginResponseBody loginResponseBody = new LoginResponseBody(requestBody.getUserId(), firstname);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(new ObjectMapper().writeValueAsString(loginResponseBody));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
        }
    }
}

