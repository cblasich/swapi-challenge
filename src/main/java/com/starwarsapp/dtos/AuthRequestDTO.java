package com.starwarsapp.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AuthRequestDTO {

    @NotNull(message = "Debe enviar nombre de usuario")
    @NotBlank(message = "Debe ingresar nombre de usuario")
    private String username;

    @NotNull(message = "Debe enviar contraseña")
    @NotBlank(message = "Debe ingresar la contraseña")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
