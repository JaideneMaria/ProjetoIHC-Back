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
    
    private String nomeAluno;
    
    private String matricula;
    
    private String curso;
    
    private String periodo;
    
    private String turnoCurso;
    
    private String emailAluno;
    
    private String cpfAluno;
    
    private String anexo; // Caminho ou nome do arquivo anexado
    
    
    private LocalDate dataInicio;
    
    private LocalDate dataFim;
    
    private String motivo;
    
    private String status;
    
    private LocalDateTime dataSolicitacao;

    public SolicitacaoAbono builder(){
        
        return SolicitacaoAbono.builder()
            .nomeAluno(nomeAluno)
            .matricula(matricula)
            .curso(curso)
            .periodo(periodo)
            .turnoCurso(turnoCurso)
            .emailAluno(emailAluno)
            .emailAluno(emailAluno)
            .cpfAluno(cpfAluno)
            .dataInicio(dataInicio)
            .dataFim(dataFim)
            .motivo(motivo)
            .status(status)
            .dataSolicitacao(dataSolicitacao)
            .build();
    }
}
