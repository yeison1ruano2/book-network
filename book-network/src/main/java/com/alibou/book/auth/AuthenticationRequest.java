package com.alibou.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

  @Email(message="Email no tiene el formato correcto")
  @NotEmpty(message="Email es requerido")
  @NotBlank(message="Email es requerido")
  private String email;
  @NotEmpty(message="Contraseña es requerido")
  @NotBlank(message="Contraseña es requerido")
  @Size(min = 8, message = "Contraseña debe tener 8 caracteres como minimo")
  private String password;
}
