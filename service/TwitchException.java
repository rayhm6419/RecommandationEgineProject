package com.laioffer.twitch.service;

//帮助找出error在哪，如果是twitch server出现异常
public class TwitchException extends RuntimeException{
    public TwitchException(String errorMessage){
        super(errorMessage);
    }

}
