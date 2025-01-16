package br.com.ifpe.ProjetoIHC_Back.api;

import br.com.ifpe.ProjetoIHC_Back.modelo.googleDrive.GoogleDriveService;
import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.SolicitacaoAbono;
import br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono.SolicitacaoAbonoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/api/solicitacoes-abono")
public class SolicitacaoAbonoController {

    @Autowired
    private SolicitacaoAbonoService solicitacaoAbonoService;

    @Autowired
    private GoogleDriveService googleDriveService;

    @PostMapping
    public ResponseEntity<String> criarSolicitacao(@ModelAttribute SolicitacaoAbono request,
                                                   @RequestParam("file") MultipartFile file) {
        try {
            // Salvar arquivo temporariamente
            String filePath = salvarArquivoTemporariamente(file);

            // Fazer upload para o Google Drive e obter o URL
            String fileUrl = googleDriveService.enviarArquivo(filePath, file.getOriginalFilename(), file.getContentType());

            // Salvar o URL do anexo na solicitação
            request.setAnexo(fileUrl);

            // Salvar solicitação no banco de dados
            solicitacaoAbonoService.salvar(request);

            return ResponseEntity.ok("Solicitação criada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar solicitação: " + e.getMessage());
        }
    }

    private String salvarArquivoTemporariamente(MultipartFile file) throws IOException {
        java.io.File tempFile = java.io.File.createTempFile("upload", file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile.getAbsolutePath();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoAbono> buscarSolicitacaoPorId(@PathVariable Long id) {
        SolicitacaoAbono solicitacao = solicitacaoAbonoService.buscarPorId(id);
        if (solicitacao != null) {
            return ResponseEntity.ok(solicitacao);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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