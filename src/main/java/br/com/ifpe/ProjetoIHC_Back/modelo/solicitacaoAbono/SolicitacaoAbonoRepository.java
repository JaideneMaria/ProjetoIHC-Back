package br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Usuario;


@Repository
public interface SolicitacaoAbonoRepository extends JpaRepository<SolicitacaoAbono, Long> {
    @Query("SELECT s FROM SolicitacaoAbono s WHERE s.status = :status")
    List<SolicitacaoAbono> findByStatus(@Param("status") StatusSolicitacao status);

    @Query("SELECT s FROM SolicitacaoAbono s WHERE s.criadoPor = :usuario")
    List<SolicitacaoAbono> findByUsuario(@Param("usuario") Usuario usuario);


}

