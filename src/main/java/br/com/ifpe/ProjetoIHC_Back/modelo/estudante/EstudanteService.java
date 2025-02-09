package br.com.ifpe.ProjetoIHC_Back.modelo.estudante;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Perfil;
import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.PerfilRepository;
import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Usuario;
import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.UsuarioService;

import jakarta.transaction.Transactional;


@Service
public class EstudanteService {

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PerfilRepository perfilUsuarioRepository;


    @Transactional
    public Estudante save(Estudante estudante, Usuario usuarioLogado) {
        
        usuarioService.save(estudante.getUsuario());

        for (Perfil perfil : estudante.getUsuario().getRoles()) {
            perfil.setHabilitado(Boolean.TRUE);
            perfilUsuarioRepository.save(perfil);
        }
        estudante.setHabilitado(true);
        estudante.setCriadoPor(usuarioLogado);
        Estudante estudanteSalvo = estudanteRepository.save(estudante);
        return estudanteSalvo;
    }

    public Estudante findById(Long id) {
        return estudanteRepository.findById(id).orElse(null);
    }

    public List<Estudante> findAll() {
        return estudanteRepository.findAll();
    }

    public List<Estudante> findByUsuario(Usuario usuario) {
        return estudanteRepository.findByUsuario(usuario);
    }
    
}