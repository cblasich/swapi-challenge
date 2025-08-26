package com.starwarsapp.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class UserRegisterDTO {

    @NotBlank(message = "Debe ingresar nombre de usuario")
    @NotNull(message = "Debe enviar nombre de usuario")
    @Size(min = 4, message = "Debe tener al menos 4 caracteres")
    private String username;

    @Email(message = "Debe ser un email válido")
    @NotNull(message = "Debe enviar email")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @NotNull(message = "Debe enviar contraseña")
    @NotBlank(message = "Debe ingresar contraseña")
    private String password;

    @NotNull(message = "Debe enviar roles")
    @Size(min = 1, message = "Debe asignar al menos un rol al usuario")
    private List<@NotBlank(message = "El nombre del rol no puede estar vacío") String> roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
