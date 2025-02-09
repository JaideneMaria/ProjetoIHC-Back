package br.com.ifpe.ProjetoIHC_Back.api.solicitacaoAbono;

import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.SolicitacaoAbono;
import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.StatusSolicitacao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAbonoRequest {

    private String nome;

    private String matricula;

    private String curso;

    private String periodo;

    private String turno;

    private String email;

    private String cpf;

    private String anexo; 

    private LocalDate inicioFalta;

    private LocalDate fimFalta;

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

