package com.alibou.book.auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RegistrationRequest {

  @NotEmpty(message="Nombre es requerido")
  @NotBlank(message="Nombre es requerido")
  private String firstname;
  @NotEmpty(message="Apellido es requerido")
  @NotBlank(message="Apellido es requerido")
  private String lastname;
  @Email(message="Email no tiene el formato correcto")
  @NotEmpty(message="Email es requerido")
  @NotBlank(message="Email es requerido")
  private String email;
  @NotEmpty(message="Contraseña es requerido")
  @NotBlank(message="Contraseña es requerido")
  @Size(min = 8, message = "Contraseña debe tener 8 caracteres como minimo")
  private String password;

}
