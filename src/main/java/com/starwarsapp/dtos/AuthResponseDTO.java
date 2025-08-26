package com.starwarsapp.dtos;

public class AuthResponseDTO {

    private String username;
    private String token;

    public AuthResponseDTO(String token, String username) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
