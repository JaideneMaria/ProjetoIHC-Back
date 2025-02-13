    package br.com.ifpe.ProjetoIHC_Back.api.solicitacaoAbono;

    import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.SolicitacaoAbono;
    import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.SolicitacaoAbonoService;
    import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.StatusSolicitacao;
    import jakarta.servlet.http.HttpServletRequest;
    import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Perfil;
    import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.Usuario;
    import br.com.ifpe.ProjetoIHC_Back.modelo.acesso.UsuarioService;
    import br.com.ifpe.ProjetoIHC_Back.modelo.googleDrive.GoogleDriveService;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.util.List;

    import java.util.logging.Level;
    import java.util.logging.Logger;

    @RestController
    @CrossOrigin
    @RequestMapping("/api/solicitacoes") 
    public class SolicitacaoAbonoController {

        private static final Logger LOGGER = Logger.getLogger(SolicitacaoAbonoController.class.getName());

        @Autowired
        private SolicitacaoAbonoService solicitacaoAbonoService;

        @Autowired
        private GoogleDriveService googleDriveService;

        @Autowired
        private UsuarioService usuarioService;

    
        // Criar uma nova solicitação de abono
        @PostMapping("/abono")
        public ResponseEntity<String> criarSolicitacao(
                @ModelAttribute SolicitacaoAbono request,
                HttpServletRequest servletRequest,
                @RequestParam("file") MultipartFile file) {
            String filePath = null;
            try {
                Usuario usuarioLogado = usuarioService.obterUsuarioLogado(servletRequest);
                request.setCriadoPor(usuarioLogado); 

                filePath = salvarArquivoTemporariamente(file);
                String fileUrl = googleDriveService.enviarArquivo(filePath, file.getOriginalFilename(), file.getContentType());
                request.setAnexo(fileUrl);
                solicitacaoAbonoService.salvar(request);

                return ResponseEntity.ok("Solicitação criada com sucesso!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar solicitação: " + e.getMessage());
            } finally {
                if (filePath != null) {
                    deletarArquivoTemporariamente(filePath);
                }
            }
        }


        private String salvarArquivoTemporariamente(MultipartFile file) throws IOException {
            Path tempDir = Files.createTempDirectory("uploads");
            File tempFile = File.createTempFile("upload_", file.getOriginalFilename(), tempDir.toFile());
            file.transferTo(tempFile);
            return tempFile.getAbsolutePath();
        }

        private void deletarArquivoTemporariamente(String filePath) {
            try {
                Files.deleteIfExists(Path.of(filePath));
            } catch (IOException e) {
                System.err.println("Falha ao deletar arquivo temporário: " + e.getMessage());
            }
        }

        // Lista todas as solicitações de acordo com o usuário, se servidor exibe todas
        @GetMapping
        public ResponseEntity<List<SolicitacaoAbono>> listarSolicitacoes(HttpServletRequest request) {
            try {
                // Obtém o usuário logado a partir da requisição
                Usuario usuarioLogado = usuarioService.obterUsuarioLogado(request);
                List<SolicitacaoAbono> solicitacoes;

                // Verifica o perfil do usuário logado
                boolean isEstudante = usuarioLogado.getRoles().stream()
                                                    .anyMatch(role -> role.getNome().equals(Perfil.ROLE_ESTUDANTE));

                if (isEstudante) {
                    // Estudante vê somente suas próprias solicitações
                    solicitacoes = solicitacaoAbonoService.listarPorUsuario(usuarioLogado);
                } else {
                    // Servidor tem acesso a todas
                    solicitacoes = solicitacaoAbonoService.listar();
                }

                // Retorna a resposta com a lista de solicitações
                return ResponseEntity.ok(solicitacoes);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao listar solicitações", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        // Buscar uma solicitação específica por ID
        @GetMapping("/{id}")
        public ResponseEntity<SolicitacaoAbono> buscarSolicitacaoPorId(@PathVariable Long id) {
            SolicitacaoAbono solicitacao = solicitacaoAbonoService.buscarPorId(id);
            if (solicitacao != null) {
                return ResponseEntity.ok(solicitacao);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Deletar uma solicitação de abono
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deletarSolicitacao(@PathVariable Long id) {
            try {
                solicitacaoAbonoService.deletar(id);
                return ResponseEntity.ok("Solicitação deletada com sucesso!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar solicitação: " + e.getMessage());
            }
        }

        // Atualizar uma solicitação de abono
        // @PutMapping("/{id}")
        // public ResponseEntity<String> atualizarSolicitacao(@PathVariable Long id, @RequestBody SolicitacaoAbono solicitacaoAtualizada) {
        //     try {
        //         solicitacaoAbonoService.atualizar(id, solicitacaoAtualizada);
        //         return ResponseEntity.ok("Solicitação atualizada com sucesso!");
        //     } catch (Exception e) {
        //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar solicitação: " + e.getMessage());
        //     }
        // }

        // Alterar o status de uma solicitação para "Em análise"
        @PatchMapping("/{id}/atender")
        public ResponseEntity<String> atenderSolicitacao(@PathVariable Long id) {
            try {
                solicitacaoAbonoService.atenderSolicitacao(id);
                return ResponseEntity.ok("Solicitação atendida e alterada para 'Em análise'.");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atender solicitação: " + e.getMessage());
            }
        }

        // Concluir uma solicitação com estado final (Deferido/Indeferido)
        @PatchMapping("/{id}/concluir")
        public ResponseEntity<String> concluirSolicitacao(
            @PathVariable Long id,
            @RequestBody ConclusaoSolicitacaoRequest request) { // Mudei para @RequestBody
        try {
            String status = request.getStatus();
            String justificativa = request.getJustificativa();

            solicitacaoAbonoService.concluirSolicitacao(id, status, justificativa);
            return ResponseEntity.ok("Solicitação concluída com o status: " + status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro de validação: Um ou mais parâmetros são inválidos. Detalhe: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                "Erro de estado: A solicitação não está em um status que permita ser finalizada. Detalhe: " + e.getMessage()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                "Erro: A solicitação com o ID " + id + " não foi encontrada no sistema."
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                "Erro ao concluir solicitação: Um erro inesperado ocorreu. Por favor, tente novamente. Detalhe: " + e.getMessage()
            );
            }
        }

        //Realizar o filtro das solicitações
        @PostMapping("/filtrar")
        public ResponseEntity<List<SolicitacaoAbono>> filtrar(
            @RequestParam(required = false) StatusSolicitacao status) {
            try {
                List<SolicitacaoAbono> solicitacoes = solicitacaoAbonoService.filtrar(status);
                return ResponseEntity.ok(solicitacoes);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }


    }