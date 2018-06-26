package com.example.gyun_home.gyuntalk.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {

    public Map<String,Boolean> users = new HashMap<>(); // 채팅방 유저들
    public Map<String,Comment> comments = new HashMap<>(); //채팅방의 내용

    public static class Comment{
        public String uid;
        public String message;
        public Object timestamp;
    }

   /* private String uid;
    private String destinationuid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDestinationuid() {
        return destinationuid;
    }

    public void setDestinationuid(String destinationuid) {
        this.destinationuid = destinationuid;
    }*/
}
