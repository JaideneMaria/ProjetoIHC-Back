package br.com.ifpe.ProjetoIHC_Back.modelo.estudante;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Usuario;
import br.com.ifpe.ProjetoIHC_Back.util.entity.EntidadeAuditavel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Estudante")
@SQLRestriction("habilitado = true")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Estudante extends EntidadeAuditavel {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Usuario usuario;

    
}
