package br.com.ifpe.ProjetoIHC_Back.api.servidor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.ifpe.ProjetoIHC_Back.modelo.servidor.Servidor;
import br.com.ifpe.ProjetoIHC_Back.modelo.servidor.ServidorService;

@RestController
@RequestMapping("/api/servidor")
@CrossOrigin
public class ServidorController {

    @Autowired
    private ServidorService servidorService;

    @PostMapping
    public ResponseEntity<Servidor> save(@RequestBody ServidorRequest servidorRequest) {
        Servidor servidor = servidorRequest.build();
        Servidor servidorSalvo = servidorService.save(servidor);
        return new ResponseEntity<>(servidorSalvo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Servidor>> listarTodos() {
        List<Servidor> servidores = servidorService.findAll();
        return ResponseEntity.ok(servidores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servidor> findById(@PathVariable Long id) {
        Servidor servidor = servidorService.findById(id);
        return servidor != null ? ResponseEntity.ok(servidor) : ResponseEntity.notFound().build();
    }
}