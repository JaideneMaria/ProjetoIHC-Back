package br.com.ifpe.ProjetoIHC_Back.api.acesso;

import java.util.HashMap;
import java.util.Map;

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
    
    private UsuarioService usuarioService;

    public AuthenticationController(JwtService jwtService, UsuarioService usuarioService) {

        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public Map<Object, Object> signin(@RequestBody AuthenticationRequest data) {
    
        Usuario authenticatedUser = usuarioService.authenticate(data.getEmail(), data.getSenha());

        String jwtToken = jwtService.generateToken(authenticatedUser);

        Map<Object, Object> loginResponse = new HashMap<>();
        loginResponse.put("email", authenticatedUser.getUsername());
        loginResponse.put("token", jwtToken);
        loginResponse.put("tokenExpiresIn", jwtService.getExpirationTime());

        return loginResponse;
    }    
}
