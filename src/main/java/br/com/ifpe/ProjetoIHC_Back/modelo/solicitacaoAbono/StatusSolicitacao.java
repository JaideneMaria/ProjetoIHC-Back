package br.com.ifpe.ProjetoIHC_Back.modelo.solicitacaoAbono;

public enum StatusSolicitacao {

    PENDENTE("Pendente"),
    EM_ANALISE("Em análise"),
    DEFERIDO("Deferido"),
    INDEFERIDO("Indeferido");

    private final String descricao;

    // Construtor
    StatusSolicitacao(String descricao) {
        this.descricao = descricao;
    }

    // Getter para acessar a descrição amigável (opcional)
    public String getDescricao() {
        return descricao;
    }
}
