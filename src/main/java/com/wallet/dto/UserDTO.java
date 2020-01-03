package com.wallet.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

	private Long id;
	@Email(message="Email inválido")
	private String email;
	@Length(min=3, max= 50, message="O nome deve conter entre 3 e 50 caracteres")
	private String name;
	@NotNull
	@Length(min=6, message="A senha deve conter no mínimo 6 caracteres")
	private String password;
	@NotNull(message = "Informe uma role de acesso")
	@Pattern(regexp="^(ROLE_ADMIN|ROLE_USER)$", message = "Para a role de acesso somente são aceitos os valores ROLE_ADMIN ou ROLE_USER")
	private String role;
}
