package br.com.ifpe.ProjetoIHC_Back.modelo.servidor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Perfil;
import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.PerfilRepository;
import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.UsuarioService;
import jakarta.transaction.Transactional;

@Service
public class ServidorService {

    @Autowired
    private ServidorRepository servidorRepository;
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PerfilRepository perfilUsuarioRepository;

    @Transactional
    public Servidor save(Servidor servidor) {

        usuarioService.save(servidor.getUsuario());
        for (Perfil perfil : servidor.getUsuario().getRoles()) {
           perfil.setHabilitado(Boolean.TRUE);
           perfilUsuarioRepository.save(perfil);
      }

        servidor.setHabilitado(true);
        Servidor servidorSalvo = servidorRepository.save(servidor);
        return servidorSalvo;
    }

    public Servidor findById(Long id) {
        return servidorRepository.findById(id).orElse(null);
    }

    public List<Servidor> findAll(){
        return servidorRepository.findAll();
    }
}