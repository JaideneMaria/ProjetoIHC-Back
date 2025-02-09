package br.com.ifpe.ProjetoIHC_Back.modelo.estudante;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Usuario;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {
    public List<Estudante> findByUsuario(Usuario usuario);

}