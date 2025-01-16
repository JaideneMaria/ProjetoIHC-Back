package br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import br.com.ifpe.ProjetoIHC_Back.util.entity.EntidadeAuditavel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitacaoAbono")
@SQLRestriction("habilitado = true")

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolicitacaoAbono extends EntidadeAuditavel {
    
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private String matricula;

    @Column(nullable = false, length = 100)
    private String curso;

    @Column(nullable = false, length = 2)
    private String periodo;

    @Column(nullable = false, length = 5)
    private String turno;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(nullable = false, length = 255)
    private String anexo; // Caminho ou nome do arquivo anexado

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate inicioFalta;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fimFalta;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column
    private String observacoes;
    

    // Define valores padrão antes de persistir no banco
    @PrePersist
    public void prePersist() {
        this.status = "Pendente";
        this.dataSolicitacao = LocalDateTime.now();
        
        
    }
    


    


    
}
