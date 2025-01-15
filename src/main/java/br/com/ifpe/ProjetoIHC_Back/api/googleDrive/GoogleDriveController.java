package br.com.ifpe.ProjetoIHC_Back.api.googleDrive;


import com.google.api.services.drive.model.File;

import br.com.ifpe.ProjetoIHC_Back.modelo.googleDrive.GoogleDriveService;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;


import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/drive") // Base do caminho para o controlador
public class GoogleDriveController {

    private static final Logger LOGGER = Logger.getLogger(GoogleDriveController.class.getName());

    @Autowired
    private GoogleDriveService googleDriveService;

    // Método para fazer upload de um arquivo ao Google Drive
    @PostMapping("/upload")
public ResponseEntity<String> enviarArquivo(@RequestParam("caminhoArquivo") String caminhoArquivo,
                                            @RequestParam("nomeArquivo") String nomeArquivo,
                                            @RequestParam("tipoMime") String tipoMime) {
    try {
        String urlArquivo = googleDriveService.enviarArquivo(caminhoArquivo, nomeArquivo, tipoMime);
        return ResponseEntity.ok("Arquivo enviado com sucesso. Acesse aqui: " + urlArquivo);
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Erro ao enviar arquivo", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar arquivo.");
    }
}


    // Método para listar arquivos no Drive (retorna ID e nome)
    @GetMapping("/arquivos")
    public ResponseEntity<List<File>> listarArquivos() {
        try {
            List<File> arquivos = googleDriveService.listarArquivos();
            return ResponseEntity.ok(arquivos);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar arquivos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/arquivos/{fileId}")
    public ResponseEntity<File> listarArquivoPorId(@PathVariable("fileId") String fileId) {
        try {
            File arquivo = googleDriveService.listarArquivoPorId(fileId);
            return ResponseEntity.ok(arquivo);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar arquivo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Método para fazer download de um arquivo pelo fileId
    @GetMapping("/download/{fileId}")
    public void baixarArquivo(@PathVariable("fileId") String fileId, HttpServletResponse response) {
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=arquivo_" + fileId);
            googleDriveService.baixarArquivo(fileId, response.getOutputStream());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao baixar o arquivo", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Método para visualizar um arquivo diretamente no navegador pelo fileId
    @GetMapping("/visualizar/{fileId}")
    public void visualizarArquivo(@PathVariable("fileId") String fileId, HttpServletResponse resposta) {
        try {
            resposta.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            googleDriveService.baixarArquivo(fileId, resposta.getOutputStream());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao exibir arquivo", e);
            resposta.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
