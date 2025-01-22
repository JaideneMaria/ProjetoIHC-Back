package br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.ifpe.ProjetoIHC_Back.util.entity.EntidadeAuditavel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitacaoAbono")
@SQLRestriction("habilitado = true") // Considera habilitados no banco
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
    private String anexo;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate inicioFalta;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fimFalta;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacao status;

    @PrePersist
    public void prePersist() {
        this.status = StatusSolicitacao.PENDENTE; // Define status inicial como "PENDENTE"
        this.dataSolicitacao = LocalDateTime.now(); // Data de criação
    }

    /**
     * Gera uma descrição amigável do status para o frontend via JSON.
     * @return Uma descrição humanizada do status.
     */
    @JsonProperty("statusDescricao") // Define o nome do campo na resposta JSON
    public String getStatusDescription() {
        return this.status.getDescricao(); // Utiliza o método do Enum StatusSolicitacao
    }
}