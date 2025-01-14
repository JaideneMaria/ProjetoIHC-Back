package br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoAbonoRepository extends JpaRepository<SolicitacaoAbono, Long> {
    
}
