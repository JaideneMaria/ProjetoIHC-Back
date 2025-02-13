package br.com.ifpe.ProjetoIHC_Back.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Perfil;
import br.com.ifpe.ProjetoIHC_Back.modelo.seguranca.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(c -> c.disable())
            .authorizeHttpRequests(authorize -> authorize

                .requestMatchers(HttpMethod.POST, "/api/auth").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/servidor").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/estudante").permitAll()
                
                .requestMatchers(HttpMethod.GET, "/api/estudante").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/servidor").permitAll()
                .requestMatchers(HttpMethod.POST, "api/solicitacoes").permitAll()


                //PERMISSÕES ESPECÍFICAS DO ESTUDANTE
                .requestMatchers(HttpMethod.POST, "/api/solicitacoes/abono").hasAnyAuthority( 
                    Perfil.ROLE_ESTUDANTE // Cadastrar solicitação de Abono
                )
                
                //PERMISSÕES EM CONJUNTO DE ESTUDANTE E SERVIDOR
                .requestMatchers(HttpMethod.GET, "/api/solicitacoes").hasAnyAuthority( 
                    Perfil.ROLE_SERVIDOR, // Consultar todas as solicitações dos alunos
                    Perfil.ROLE_ESTUDANTE // Consultar apenas as suas solicitações
                )

                ////PERMISSÕES ESPECÍFICAS DO SERVIDOR
                .requestMatchers(HttpMethod.DELETE, "/api/solicitacoes/*").hasAnyAuthority(
                    Perfil.ROLE_SERVIDOR //Deletar uma solicitação pelo ID.
                )
                .requestMatchers(HttpMethod.GET, "/api/solicitacoes/*").hasAnyAuthority(
                    Perfil.ROLE_SERVIDOR //Consultar uma solicitação pelo ID.
                )
                .requestMatchers(HttpMethod.PATCH, "/api/solicitacoes/{id}/atender").hasAnyAuthority(
                    Perfil.ROLE_SERVIDOR // Alterar o status de uma solicitação para "Em análise"
                )
                .requestMatchers(HttpMethod.PATCH, "/api/solicitacoes/{id}/concluir").hasAnyAuthority(
                    Perfil.ROLE_SERVIDOR // Concluir uma solicitação com estado final (Deferido/Indeferido)
                )
                .requestMatchers(HttpMethod.POST, "/api/solicitacoes/filtrar").hasAnyAuthority(
                    Perfil.ROLE_SERVIDOR //Realizar o filtro das solicitações
                )

                .requestMatchers(HttpMethod.GET, "/api-docs/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/swagger-ui/*").permitAll()

                .anyRequest().authenticated()

            )
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )            
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
    
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);    
        return source;
    }
}

