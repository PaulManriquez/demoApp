package com.demoApp.demoApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserForm {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 255, message = "El apellido no puede exceder 255 caracteres")
    private String lastName;

    @NotBlank(message = "El username es obligatorio")
    @Size(max = 255, message = "El username no puede exceder 255 caracteres")
    private String username;

    @Size(max = 15, message = "El telefono no puede exceder 15 caracteres")
    private String phone;

    @NotBlank(message = "El correo electronico es obligatorio")
    @Email(message = "El correo electronico no tiene un formato valido")
    @Size(max = 100, message = "El correo electronico no puede exceder 100 caracteres")
    private String email;

    @Size(max = 255, message = "La contrasena no puede exceder 255 caracteres")
    private String password;

    @NotNull(message = "El status es obligatorio")
    private Boolean status = true;

    private Set<Integer> roleIds;
}

