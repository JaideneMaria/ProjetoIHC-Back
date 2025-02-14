package br.com.ifpe.ProjetoIHC_Back.api.acesso;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Usuario;
import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.UsuarioService;
import br.com.ifpe.ProjetoIHC_Back.modelo.seguranca.JwtService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthenticationController {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthenticationController(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public Map<Object, Object> signin(@RequestBody AuthenticationRequest data) {
        // Autentica o usuário e recupera os dados
        Usuario authenticatedUser = usuarioService.authenticate(data.getEmail(), data.getSenha());

        // Gerar o token JWT
        String jwtToken = jwtService.generateToken(authenticatedUser);

        // Cria a resposta com os dados necessários
        Map<Object, Object> response = new HashMap<>();
        response.put("token", jwtToken);
        
        // Adiciona o nome do perfil, chamando toString() para obter o nome do perfil
        response.put("perfil", authenticatedUser.getRoles().get(0).toString()); // Usa o método toString() para obter o nome do perfil

        // Retorna a resposta com o token e o perfil
        return ResponseEntity.ok(response).getBody();
    }
}
