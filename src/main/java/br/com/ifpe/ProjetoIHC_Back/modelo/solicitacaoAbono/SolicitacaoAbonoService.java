package br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono;

import br.com.ifpe.ProjetoIHC_Back.modelo.googleDrive.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
public class SolicitacaoAbonoService {

    @Autowired
    private SolicitacaoAbonoRepository solicitacaoAbonoRepository;

    @Autowired
    private GoogleDriveService googleDriveService;

    /**
     * Salva uma nova solicitação de abono
     *
     * @param solicitacaoAbono - Entidade com os dados da solicitação
     * @return Salva e retorna a solicitação no banco de dados
     */
    @Transactional
    public SolicitacaoAbono salvar(SolicitacaoAbono solicitacaoAbono) {
        // Define a solicitação como habilitada ao salvar
        solicitacaoAbono.setHabilitado(Boolean.TRUE);
        return solicitacaoAbonoRepository.save(solicitacaoAbono);
    }

    /**
     * Busca uma solicitação pelo ID
     *
     * @param id - ID da solicitação
     * @return A entidade correspondente ao ID informado
     */
    @Transactional
    public SolicitacaoAbono buscarPorId(Long id) {
        Optional<SolicitacaoAbono> solicitacao = solicitacaoAbonoRepository.findById(id);
        return solicitacao.orElse(null); // Retorna null caso não encontre
    }

    /**
     * Atualiza uma solicitação de abono existente
     *
     * @param id                - ID da solicitação a ser atualizada
     * @param solicitacaoAtualizada - Dados atualizados da solicitação
     * @return A entidade salva no banco de dados
     */
    @Transactional
    public SolicitacaoAbono atualizar(Long id, SolicitacaoAbono solicitacaoAtualizada) {
        if (solicitacaoAbonoRepository.existsById(id)) {
            SolicitacaoAbono solicitacaoExistente = solicitacaoAbonoRepository.findById(id).get();
            solicitacaoAtualizada.setId(id);

            // Preserva o estado de habilitação
            solicitacaoAtualizada.setHabilitado(solicitacaoExistente.getHabilitado());

            // Mantém o anexo (via Google Drive)
            if (solicitacaoAtualizada.getAnexo() == null) {
                solicitacaoAtualizada.setAnexo(solicitacaoExistente.getAnexo());
            }

            return solicitacaoAbonoRepository.save(solicitacaoAtualizada);
        }
        return null; // Caso a solicitação não exista
    }

    /**
     * Exclui logicamente uma solicitação de abono
     * Esta operação apenas desabilita a solicitação no banco
     *
     * @param id - ID da solicitação a ser desabilitada
     */
    @Transactional
    public void deletar(Long id) {
        Optional<SolicitacaoAbono> solicitacao = solicitacaoAbonoRepository.findById(id);
        if (solicitacao.isPresent()) {
            SolicitacaoAbono solicitacaoAbono = solicitacao.get();
            solicitacaoAbono.setHabilitado(Boolean.FALSE);
            solicitacaoAbonoRepository.save(solicitacaoAbono);
        }
    }

    /**
     * Fazer upload de um anexo ao Google Drive e associar à solicitação
     *
     * @param solicitacao - Solicitação de abono
     * @param filePath    - Caminho do arquivo a ser enviado
     * @param fileName    - Nome original do arquivo
     * @param contentType - Tipo MIME do arquivo
     * @throws Exception em caso de falha no upload ao Google Drive
     */
    @Transactional
    public void enviarArquivo(SolicitacaoAbono solicitacao, String filePath, String fileName, String contentType) throws Exception {
        try {
            String urlAnexo = googleDriveService.enviarArquivo(filePath, fileName, contentType);
            solicitacao.setAnexo(urlAnexo);
            solicitacaoAbonoRepository.save(solicitacao);
        } catch (Exception e) {
            throw new Exception("Erro ao fazer upload do anexo: " + e.getMessage());
        }
    }
}