package br.com.ifpe.ProjetoIHC_Back.api.estudante;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Usuario;
import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.UsuarioService;
import br.com.ifpe.ProjetoIHC_Back.modelo.estudante.Estudante;
import br.com.ifpe.ProjetoIHC_Back.modelo.estudante.EstudanteService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/estudante")
@CrossOrigin
public class EstudanteController {

    @Autowired
    private EstudanteService estudanteService;
    
    @Autowired
    private UsuarioService usuarioService;

 
    @PostMapping
    public ResponseEntity<Estudante> save(@RequestBody EstudanteRequest estudanteRequest, HttpServletRequest request) {
        Estudante estudante = estudanteRequest.build();
        Estudante estudanteSalvo = estudanteService.save(estudante, usuarioService.obterUsuarioLogado(request));
        return new ResponseEntity<>(estudanteSalvo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estudante> findById(@PathVariable Long id) {
        Estudante estudante = estudanteService.findById(id);
        return estudante != null ? ResponseEntity.ok(estudante) : ResponseEntity.notFound().build();
    }
   

    @GetMapping
    public ResponseEntity<List<Estudante>> listarTodos(HttpServletRequest request) {
    Usuario usuarioLogado = usuarioService.obterUsuarioLogado(request);
    
    // Verifica se o usuário logado é um estudante
    List<Estudante> estudantes = estudanteService.findByUsuario(usuarioLogado);
    
    return ResponseEntity.ok(estudantes);
}

}