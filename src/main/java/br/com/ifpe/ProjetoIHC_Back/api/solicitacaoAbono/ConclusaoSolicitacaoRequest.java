package br.com.ifpe.ProjetoIHC_Back.api.solicitacaoAbono;

import lombok.Data;

@Data
public class ConclusaoSolicitacaoRequest {
    private String status;
    private String justificativa;
}

