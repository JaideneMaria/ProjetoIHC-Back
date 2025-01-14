package br.com.ifpe.ProjetoIHC_Back.api;


import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.SolicitacaoAbono;
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
    
    private String anexo; // Caminho ou nome do arquivo anexado
    
    
    private LocalDate inicioFalta;
    
    private LocalDate fimFalta;
    
    private String motivo;
    
    private String status;
    
    private LocalDateTime dataSolicitacao;

    public SolicitacaoAbono builder(){
        
        return SolicitacaoAbono.builder()
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
            .status(status)
            .dataSolicitacao(dataSolicitacao)
            .build();
    }
}
