package com.example.proyecto.dto;

import com.example.proyecto.domain.entity.User;
import lombok.Data;
//@Data
public class AuthResponseDto {
//    private String token;
//    public AuthResponseDto() {}
//    public AuthResponseDto(String token) { this.token = token; };
//    public String getToken() { return token; }
//    public void setToken(String token) { this.token = token; }
    private String token;
    private Long id;

    public AuthResponseDto() {}

    public AuthResponseDto(String token, Long id) {
        this.token = token;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}