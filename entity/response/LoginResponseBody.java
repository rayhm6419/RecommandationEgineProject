package com.laioffer.twitch.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;

//成功登陆或者没成功登陆的一些response
public class LoginResponseBody {
    @JsonProperty("user_id")
    private final String userId;

    @JsonProperty("name")
    private final String name;

    public LoginResponseBody(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

}
