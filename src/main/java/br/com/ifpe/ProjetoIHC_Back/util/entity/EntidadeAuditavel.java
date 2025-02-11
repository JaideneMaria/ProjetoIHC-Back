package br.com.ifpe.ProjetoIHC_Back.util.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EntidadeAuditavel extends EntidadeNegocio {
    
    @JsonIgnore
    @Version  
    private Long versao;

    @JsonIgnore
    @CreatedDate 
    private LocalDate dataCriacao;

    @JsonIgnore
    @LastModifiedDate 
    private LocalDate dataUltimaModificacao;

    @JsonIgnore
    @Column 
    private Usuario criadoPor; 

        
    @JsonIgnore
    @Column  
    private Long ultimaModificacaoPor; // Id do usuário que fez a última alteração

}
