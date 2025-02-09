package br.com.ifpe.ProjetoIHC_Back.api.estudante;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Perfil;
import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Usuario;
import br.com.ifpe.ProjetoIHC_Back.modelo.estudante.Estudante;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstudanteRequest {

    @NotBlank(message = "O e-mail é de preenchimento obrigatório")
    @Email(message = "O e-mail deve ser válido")
    private String username;

    @NotBlank(message = "A senha é de preenchimento obrigatório")
    private String password;

    public Usuario buildUsuario() {
        return Usuario.builder()
            .username(username)
            .password(password)
            .roles(Arrays.asList(new Perfil(Perfil.ROLE_ESTUDANTE)))
            .build();
    }

    public Estudante build() {
        return Estudante.builder()
            .usuario(buildUsuario())
            .build();
        }   
}