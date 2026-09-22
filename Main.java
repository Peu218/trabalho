public class Main {

    public static void main(String[] args) {
        ChaveApi chave = new ChaveApi("ABC123", "Pro", 3);

        System.out.println("Chave criada: " + chave.getToken());
        System.out.println("Plano: " + chave.getPlano());
        System.out.println("Ativa: " + chave.isAtiva());
        System.out.println("Requisições realizadas: " + chave.getRequisicoesRealizadas());

        System.out.println();
        System.out.println("Registrando chamadas...");
        chave.registrarChamada();
        chave.registrarChamada();
        System.out.println("Requisições realizadas: " + chave.getRequisicoesRealizadas());

        System.out.println();
        System.out.println("Fazendo upgrade para Enterprise...");
        chave.fazerUpgrade("Enterprise", 10);
        System.out.println("Novo plano: " + chave.getPlano());
        System.out.println("Novo limite: " + chave.getLimiteRequisicoes());

        System.out.println();
        System.out.println("Registrando mais uma chamada...");
        chave.registrarChamada();
        System.out.println("Requisições realizadas: " + chave.getRequisicoesRealizadas());

        System.out.println();
        System.out.println("Bloqueando a chave...");
        chave.bloquearChave();
        System.out.println("Ativa: " + chave.isAtiva());

        System.out.println();
        System.out.println("Tentando registrar chamada com a chave bloqueada...");
        try {
            chave.registrarChamada();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Desbloqueando a chave...");
        chave.desbloquearChave();
        System.out.println("Ativa: " + chave.isAtiva());

        System.out.println();
        System.out.println("Resetando o ciclo...");
        chave.resetarCiclo();
        System.out.println("Requisições realizadas: " + chave.getRequisicoesRealizadas());
    }
}