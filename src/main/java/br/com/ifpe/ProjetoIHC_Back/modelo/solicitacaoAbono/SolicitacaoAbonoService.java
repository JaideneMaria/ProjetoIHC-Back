package br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono;

import br.com.ifpe.ProjetoIHC_Back.modelo.googleDrive.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitacaoAbonoService {

    @Autowired
    private SolicitacaoAbonoRepository solicitacaoAbonoRepository;

    @Autowired
    private GoogleDriveService googleDriveService;

    
    //Salva uma nova solicitação de abono
    @Transactional
    public SolicitacaoAbono salvar(SolicitacaoAbono solicitacaoAbono) {
        solicitacaoAbono.setHabilitado(Boolean.TRUE);
        return solicitacaoAbonoRepository.save(solicitacaoAbono);
    }

    
    //Lista todas as solicitações de abono
    @Transactional
    public List<SolicitacaoAbono> listar() {
        return solicitacaoAbonoRepository.findAll();
    }

    //Busca uma solicitação pelo ID
    
    @Transactional
    public SolicitacaoAbono buscarPorId(Long id) {
        Optional<SolicitacaoAbono> solicitacao = solicitacaoAbonoRepository.findById(id);
        return solicitacao.orElse(null); // Retorna null caso não encontre
    }

    //Atualiza uma solicitação de abono existente
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
        return null; 
    }

    //Exclui logicamente uma solicitação de abono
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

    //Coloca uma solicitação "Em análise" se estiver "Pendente"
    @Transactional
    public SolicitacaoAbono atenderSolicitacao(Long id) {   
        Optional<SolicitacaoAbono> solicitacaoOptional = solicitacaoAbonoRepository.findById(id);

        if (solicitacaoOptional.isPresent()) {
            SolicitacaoAbono solicitacao = solicitacaoOptional.get();

            // Só altera para "Em análise" se o status atual for "Pendente"
            if (solicitacao.getStatus() == StatusSolicitacao.PENDENTE) {
                solicitacao.setStatus(StatusSolicitacao.EM_ANALISE);
                return solicitacaoAbonoRepository.save(solicitacao);
            }
            // Retorna a própria solicitação sem alteração
            return solicitacao;
        } else {
            throw new RuntimeException("Solicitação não encontrada com o ID: " + id);
        }
    }

    //Conclui a solicitação com o status "Deferido" ou "Indeferido"
    @Transactional
    public SolicitacaoAbono concluirSolicitacao(Long id, String novoStatus, String justificativa) {
        Optional<SolicitacaoAbono> solicitacaoOptional = solicitacaoAbonoRepository.findById(id);

        if (solicitacaoOptional.isPresent()) {
            SolicitacaoAbono solicitacao = solicitacaoOptional.get();

            // Só permite concluir se o status for "Em análise"
            if (solicitacao.getStatus() != StatusSolicitacao.EM_ANALISE) {
                throw new IllegalStateException("Somente solicitações 'Em análise' podem ser concluídas.");
            }

            // Valida entrada obrigatória
            if (novoStatus == null || justificativa == null || justificativa.isBlank()) {
                throw new IllegalArgumentException("Status e justificativa são obrigatórios para concluir a solicitação.");
            }

            // Converte a String para Enum e define o status
            try {
                StatusSolicitacao statusEnum = StatusSolicitacao.valueOf(novoStatus.toUpperCase());
                solicitacao.setStatus(statusEnum);
                solicitacao.setObservacoes(justificativa);

                return solicitacaoAbonoRepository.save(solicitacao);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Status inválido. Use um dos valores: PENDENTE, EM_ANALISE, DEFERIDO, INDEFERIDO.");
            }
        } else {
            throw new RuntimeException("Solicitação não encontrada com o ID: " + id);
        }
    }
}