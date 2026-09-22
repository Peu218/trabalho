public class ChaveApi {

    private String token;
    private String plano;
    private int limiteRequisicoes;
    private int requisicoesRealizadas;
    private boolean ativa;

    public ChaveApi(String token, String plano, int limiteRequisicoes) {
        this.token = token;
        this.plano = plano;
        this.limiteRequisicoes = limiteRequisicoes;
        this.ativa = true;
        this.requisicoesRealizadas = 0;
    }

    public void registrarChamada() {
        if (!ativa) {
            throw new IllegalStateException("Acesso negado: Chave inativa.");
        }
        if (requisicoesRealizadas >= limiteRequisicoes) {
            throw new IllegalStateException("Acesso negado: Limite de requisições excedido.");
        }
        requisicoesRealizadas++;
    }

    public void fazerUpgrade(String novoPlano, int novoLimite) {
        if (novoLimite < limiteRequisicoes) {
            throw new IllegalArgumentException("Upgrade inválido: o novo limite não pode ser menor que o limite atual.");
        }
        this.plano = novoPlano;
        this.limiteRequisicoes = novoLimite;
    }

    public void resetarCiclo() {
        requisicoesRealizadas = 0;
    }

    public void bloquearChave() {
        ativa = false;
    }

    public void desbloquearChave() {
        ativa = true;
    }

    public String getToken() {
        return token;
    }

    public String getPlano() {
        return plano;
    }

    public int getLimiteRequisicoes() {
        return limiteRequisicoes;
    }

    public int getRequisicoesRealizadas() {
        return requisicoesRealizadas;
    }

    public boolean isAtiva() {
        return ativa;
    }
}