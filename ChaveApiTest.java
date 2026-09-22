public class ChaveApiTest {

    private static int totalTestes = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        executar("teste1_criacaoDeChave", ChaveApiTest::teste1_criacaoDeChave);
        executar("teste2_registrarDentroDoLimite", ChaveApiTest::teste2_registrarDentroDoLimite);
        executar("teste3_limiteExcedido", ChaveApiTest::teste3_limiteExcedido);
        executar("teste4_chaveBloqueada", ChaveApiTest::teste4_chaveBloqueada);
        executar("teste5_upgradeValidoEInvalido", ChaveApiTest::teste5_upgradeValidoEInvalido);
        executar("teste6_resetarCiclo", ChaveApiTest::teste6_resetarCiclo);
        executar("teste7_desbloquearChave", ChaveApiTest::teste7_desbloquearChave);

        System.out.println();
        System.out.println("========================================");
        System.out.println("Total de testes: " + totalTestes + " | Falhas: " + falhas);
        System.out.println("========================================");

        if (falhas > 0) {
            System.exit(1);
        }
    }

    // 1. Criar uma chave corretamente
    private static void teste1_criacaoDeChave() {
        iniciarTeste("1. Criação de chave: ativa=true e requisicoesRealizadas=0");
        ChaveApi chave = new ChaveApi("ABC123", "Pro", 5);

        assertTrue(chave.isAtiva(), "A chave recém-criada deveria estar ativa");
        assertEquals(0, chave.getRequisicoesRealizadas(), "requisicoesRealizadas deveria começar em 0");
        assertEquals(5, chave.getLimiteRequisicoes(), "limiteRequisicoes deveria ser o valor informado no construtor");
    }

    // 2. Registrar chamadas dentro do limite
    private static void teste2_registrarDentroDoLimite() {
        iniciarTeste("2. Registrar chamadas dentro do limite incrementa o contador");
        ChaveApi chave = new ChaveApi("ABC123", "Pro", 3);

        chave.registrarChamada();
        assertEquals(1, chave.getRequisicoesRealizadas(), "Após 1 chamada, contador deveria ser 1");

        chave.registrarChamada();
        assertEquals(2, chave.getRequisicoesRealizadas(), "Após 2 chamadas, contador deveria ser 2");

        chave.registrarChamada();
        assertEquals(3, chave.getRequisicoesRealizadas(), "Após 3 chamadas, contador deveria ser 3");
    }

    // 3. Registrar chamada quando o limite já foi atingido
    private static void teste3_limiteExcedido() {
        iniciarTeste("3. Registrar chamada após limite atingido lança 'Acesso negado: Limite de requisições excedido.'");
        ChaveApi chave = new ChaveApi("ABC123", "Pro", 2);
        chave.registrarChamada();
        chave.registrarChamada(); // atinge o limite (2/2)

        try {
            chave.registrarChamada();
            falhar("Esperava-se uma exceção ao exceder o limite, mas nenhuma foi lançada");
        } catch (IllegalStateException e) {
            assertEquals("Acesso negado: Limite de requisições excedido.", e.getMessage(),
                    "Mensagem de exceção incorreta");
        }

        assertEquals(2, chave.getRequisicoesRealizadas(), "Contador não deveria passar do limite");
    }

    // 4. Bloquear a chave e tentar registrar uma chamada
    private static void teste4_chaveBloqueada() {
        iniciarTeste("4. Registrar chamada com chave bloqueada lança 'Acesso negado: Chave inativa.'");
        ChaveApi chave = new ChaveApi("ABC123", "Pro", 5);
        chave.bloquearChave();

        assertFalse(chave.isAtiva(), "A chave deveria estar inativa após bloquearChave()");

        try {
            chave.registrarChamada();
            falhar("Esperava-se uma exceção ao registrar chamada com chave bloqueada, mas nenhuma foi lançada");
        } catch (IllegalStateException e) {
            assertEquals("Acesso negado: Chave inativa.", e.getMessage(), "Mensagem de exceção incorreta");
        }
    }

    // 5. Upgrade válido e upgrade inválido
    private static void teste5_upgradeValidoEInvalido() {
        iniciarTeste("5. fazerUpgrade: limite maior/igual aceito, menor rejeitado");
        ChaveApi chave = new ChaveApi("ABC123", "Basic", 10);

        chave.fazerUpgrade("Pro", 20);
        assertEquals(20, chave.getLimiteRequisicoes(), "Upgrade para limite maior deveria ser aceito");
        assertEquals("Pro", chave.getPlano(), "Plano deveria ter sido atualizado");

        chave.fazerUpgrade("Pro", 20);
        assertEquals(20, chave.getLimiteRequisicoes(), "Upgrade para limite igual deveria ser aceito");

        try {
            chave.fazerUpgrade("Basic", 5);
            falhar("Esperava-se uma exceção ao tentar diminuir o limite, mas nenhuma foi lançada");
        } catch (IllegalArgumentException e) {
            // ok, comportamento esperado
        }
        assertEquals(20, chave.getLimiteRequisicoes(), "Limite não deveria mudar após upgrade inválido");
        assertEquals("Pro", chave.getPlano(), "Plano não deveria mudar após upgrade inválido");
    }

    // 6. resetarCiclo zera o contador
    private static void teste6_resetarCiclo() {
        iniciarTeste("6. resetarCiclo() zera requisicoesRealizadas");
        ChaveApi chave = new ChaveApi("ABC123", "Pro", 5);
        chave.registrarChamada();
        chave.registrarChamada();
        assertEquals(2, chave.getRequisicoesRealizadas(), "Contador deveria ser 2 antes do reset");

        chave.resetarCiclo();
        assertEquals(0, chave.getRequisicoesRealizadas(), "Contador deveria ser 0 após resetarCiclo()");
    }

    // 7. desbloquearChave permite novas chamadas
    private static void teste7_desbloquearChave() {
        iniciarTeste("7. desbloquearChave() permite registrar chamadas novamente");
        ChaveApi chave = new ChaveApi("ABC123", "Pro", 5);
        chave.bloquearChave();
        chave.desbloquearChave();

        assertTrue(chave.isAtiva(), "A chave deveria estar ativa após desbloquearChave()");
        chave.registrarChamada();
        assertEquals(1, chave.getRequisicoesRealizadas(), "Deveria registrar chamada normalmente após desbloquear");
    }

    // ---------- utilitários do runner ----------

    private static void executar(String nomeMetodo, Runnable teste) {
        try {
            teste.run();
        } catch (Throwable t) {
            falhas++;
            System.out.println("  -> ERRO INESPERADO em " + nomeMetodo + ": " + t);
        }
    }

    private static void iniciarTeste(String nome) {
        totalTestes++;
        System.out.println("[TESTE] " + nome);
    }

    private static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            falhar(mensagem + " (esperado: true, obtido: false)");
        }
    }

    private static void assertFalse(boolean condicao, String mensagem) {
        if (condicao) {
            falhar(mensagem + " (esperado: false, obtido: true)");
        }
    }

    private static void assertEquals(int esperado, int obtido, String mensagem) {
        if (esperado != obtido) {
            falhar(mensagem + " (esperado: " + esperado + ", obtido: " + obtido + ")");
        }
    }

    private static void assertEquals(String esperado, String obtido, String mensagem) {
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            falhar(mensagem + " (esperado: \"" + esperado + "\", obtido: \"" + obtido + "\")");
        }
    }

    private static void falhar(String mensagem) {
        falhas++;
        System.out.println("  -> FALHOU: " + mensagem);
    }
}