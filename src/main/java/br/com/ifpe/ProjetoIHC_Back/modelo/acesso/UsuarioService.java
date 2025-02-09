package br.com.ifpe.ProjetoIHC_Back.modelo.acesso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.ifpe.ProjetoIHC_Back.modelo.seguranca.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public UsuarioService(UsuarioRepository userRepository, AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.repository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario authenticate(String username, String password) {
        Usuario usuario = repository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }
    
        return usuario;
    }
    

    @Transactional
    public Usuario findByUsername(String username) {

        return repository.findByUsername(username).get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return repository.findByUsername(username).get();
    }

    @Transactional
    public Usuario save(Usuario user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setHabilitado(Boolean.TRUE);
        return repository.save(user);
    }

     public Usuario obterUsuarioLogado(HttpServletRequest request) {

        Usuario usuarioLogado = null;
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null) {

            String jwt = authHeader.substring(7);
            String userEmail = jwtService.extractUsername(jwt);
            usuarioLogado = findByUsername(userEmail);
            return usuarioLogado;
        }

        return usuarioLogado;
    }
}
