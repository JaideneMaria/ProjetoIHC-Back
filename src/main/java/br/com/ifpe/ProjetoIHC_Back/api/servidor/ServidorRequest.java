package br.com.ifpe.ProjetoIHC_Back.api.servidor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Perfil;
import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Usuario;
import br.com.ifpe.ProjetoIHC_Back.modelo.servidor.Servidor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServidorRequest {

    @NotBlank(message = "O e-mail é de preenchimento obrigatório")
    @Email(message = "O e-mail deve ser válido")
    private String username;

    @NotBlank(message = "A senha é de preenchimento obrigatório")
    private String password;

    public Usuario buildUsuario() {
        return Usuario.builder()
            .username(username)
            .password(password)
            .roles(Arrays.asList(new Perfil(Perfil.ROLE_SERVIDOR)))
            .build();
    }

    public Servidor build() {
        return Servidor.builder()
            .usuario(buildUsuario())
            .build();
        }   
}