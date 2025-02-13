package br.com.ifpe.ProjetoIHC_Back.api.solicitacaoAbono;

import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.SolicitacaoAbono;
import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.StatusSolicitacao;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAbonoRequest {

    @NotBlank(message = "Inserir o nome é obrigatório.")
    @Length(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "Inserir a matrícula é obrigatória.")
    @Length(max = 30, message = "A matrícula deve ter no máximo 30 caracteres.")
    private String matricula;

    @NotBlank(message = "Inserir o curso é obrigatório.")
    @Length(max = 50, message = "O nome do curso deve ter no máximo 50 caracteres.")
    private String curso;

    @NotBlank(message = "Inserir o período é obrigatório.")
    @Length(max = 2, message = "O período deve ter no máximo 2 caracteres.")
    private String periodo;

    @NotBlank(message = "Inserir o turno é obrigatório.")
    @Length(max = 2, message = "O turno deve ter no máximo 2 caracteres.")
    private String turno;

    @Email
    private String email;

    @CPF
    @NotBlank(message = "Inserir o CPF é obrigatório.")
    private String cpf;

    @NotBlank(message = "Inserir o anexo é obrigatório")
    private String anexo; 

    @NotNull(message = "Inserir a data inicial é obrigatório")
    private LocalDate inicioFalta;

    @NotNull(message = "Inserir a data final é obrigatório")
    private LocalDate fimFalta;

    @NotNull(message = "Inserir o motivo é obrigatório")
    private String motivo;

    private String status;


    private LocalDateTime dataSolicitacao;

    public SolicitacaoAbono builder() {
    return SolicitacaoAbono
        .builder()
        .nome(nome)
        .matricula(matricula)
        .curso(curso)
        .periodo(periodo)
        .turno(turno)
        .email(email)
        .email(email)
        .cpf(cpf)
        .inicioFalta(inicioFalta)
        .fimFalta(fimFalta)
        .motivo(motivo)
        .status(convertStringToStatusSolicitacao(status))
        .dataSolicitacao(dataSolicitacao)
        .build();
    }

    private StatusSolicitacao convertStringToStatusSolicitacao(String status) {
        if (status != null) {
            try {
                return StatusSolicitacao.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Status inválido: " + status);
            }
        }
        return StatusSolicitacao.PENDENTE; 
    }
}

