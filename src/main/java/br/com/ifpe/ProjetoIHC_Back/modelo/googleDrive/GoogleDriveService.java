package br.com.ifpe.ProjetoIHC_Back.modelo.googleDrive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import com.google.api.services.drive.model.Permission;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class GoogleDriveService {

    private static final Logger LOGGER = Logger.getLogger(GoogleDriveService.class.getName());
    
    @Value("${google.credentials.path}")
    private String caminhoCredenciais;
    
    private final ResourceLoader carregadorRecursos;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private Drive servicoDrive;

    public GoogleDriveService(ResourceLoader carregadorRecursos) {
        this.carregadorRecursos = carregadorRecursos;
    }

    @PostConstruct
    public void inicializar() throws IOException, GeneralSecurityException {
        LOGGER.info("Inicializando o serviço do Google Drive...");
        servicoDrive = criarServicoDrive();
        LOGGER.info("Serviço do Google Drive inicializado com sucesso.");
    }

    private Drive criarServicoDrive() throws IOException, GeneralSecurityException {
        try (InputStream inputStream = carregadorRecursos.getResource("classpath:" + caminhoCredenciais).getInputStream()) {
            GoogleCredentials credenciais = GoogleCredentials.fromStream(inputStream)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive"));
            LOGGER.info("Credenciais do Google carregadas com sucesso.");
            
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credenciais))
                    .setApplicationName("ProjetoIHC")
                    .build();
        }
    }

    
    public void configurarPermissao(String fileId) throws Exception {
        // Cria uma permissão para permitir download público
        Permission permissao = new Permission();
        permissao.setType("anyone"); // Qualquer pessoa
        permissao.setRole("reader"); // Apenas leitura
        
        // Adiciona a permissão ao arquivo
        servicoDrive.permissions().create(fileId, permissao).execute();
    }


    public String enviarArquivo(String caminhoArquivo, String nomeArquivo, String tipoMime) throws Exception {
        LOGGER.log(Level.INFO, "Enviando arquivo: {0}, Tipo MIME: {1}", new Object[]{nomeArquivo, tipoMime});
        
        File metadadosArquivo = new File();
        metadadosArquivo.setName(nomeArquivo);

        java.io.File arquivo = new java.io.File(caminhoArquivo);
        FileContent conteudoMidia = new FileContent(tipoMime, arquivo);

        File arquivoEnviado = servicoDrive.files().create(metadadosArquivo, conteudoMidia)
                .setFields("id")
                .execute();

        LOGGER.log(Level.INFO, "Arquivo enviado com sucesso. ID do arquivo: {0}", arquivoEnviado.getId());
        configurarPermissao(arquivoEnviado.getId());
        return "https://drive.google.com/uc?id=" + arquivoEnviado.getId();
    }

    public List<File> listarArquivos() throws IOException {
        LOGGER.info("Listando arquivos do Google Drive...");

        FileList resultado = servicoDrive.files().list()
                .setPageSize(20) // Ajuste conforme necessário
                .setFields("files(id, name)")
                .execute();

        return resultado.getFiles();
    }

    public File listarArquivoPorId(String fileId) throws IOException {
        LOGGER.log(Level.INFO, "Buscando arquivo com ID: {0}", fileId);
        return servicoDrive.files().get(fileId)
                .setFields("id, name, mimeType, createdTime, size")
                .execute();
    }

    public void baixarArquivo(String fileId, OutputStream fluxoSaida) throws IOException {
        try {
            LOGGER.log(Level.INFO, "Baixando arquivo com ID: {0}", fileId);
            // Recupera o arquivo da API com configuração para download total
            servicoDrive.files().get(fileId).executeMediaAndDownloadTo(fluxoSaida);
            LOGGER.info("Arquivo baixado com sucesso.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao baixar o arquivo com ID: " + fileId, e);
            throw new IOException("Erro ao baixar o arquivo do Google Drive.", e);
        }
    }
}