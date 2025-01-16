package br.com.ifpe.ProjetoIHC_Back.api;

import br.com.ifpe.ProjetoIHC_Back.modelo.googleDrive.GoogleDriveService;
import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.SolicitacaoAbono;
import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.SolicitacaoAbonoService;

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
@RequestMapping("/api/solicitacoes")
public class SolicitacaoAbonoController {

    private static final Logger LOGGER = Logger.getLogger(SolicitacaoAbonoController.class.getName());

    @Autowired
    private SolicitacaoAbonoService solicitacaoAbonoService;

    @Autowired
    private GoogleDriveService googleDriveService;

    @PostMapping
    public ResponseEntity<String> criarSolicitacao(
        @ModelAttribute SolicitacaoAbono request,
        @RequestParam("file") MultipartFile file
    ) {
        String filePath = null;

        try {
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
    //Listar todas solicitaçoes
    @GetMapping("/abono")
    public ResponseEntity<List<SolicitacaoAbono>> listarSolicitacoes() {
        try {
            List<SolicitacaoAbono> solicitacoes = solicitacaoAbonoService.listar();
            return ResponseEntity.ok(solicitacoes);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar solicitações", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoAbono> buscarSolicitacaoPorId(@PathVariable Long id) {
        SolicitacaoAbono solicitacao = solicitacaoAbonoService.buscarPorId(id);
        if (solicitacao != null) {
            return ResponseEntity.ok(solicitacao);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarSolicitacao(@PathVariable Long id) {
        try {
            solicitacaoAbonoService.deletar(id);
            return ResponseEntity.ok("Solicitação deletada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar solicitação: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarSolicitacao(@PathVariable Long id, @RequestBody SolicitacaoAbono solicitacaoAtualizada) {
        try {
            solicitacaoAbonoService.atualizar(id, solicitacaoAtualizada);
            return ResponseEntity.ok("Solicitação atualizada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar solicitação: " + e.getMessage());
        }
    }
}