package com.example.proyecto.dto;

import lombok.Data;
@Data
public class AuthResponseDto {
    private String token;
    public AuthResponseDto() {}
    public AuthResponseDto(String token) { this.token = token; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}