import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Atividade de revisão — Conceitos fundamentais de Orientação a Objetos
 *
 * Sistema de vendas de consoles de uma loja, evoluído em 5 etapas:
 *   1) Encapsulamento
 *   2) Construtores
 *   3) Interface e Composição
 *   4) Herança (usada de forma apropriada)
 *   5) Polimorfismo e Extensibilidade (OCP)
 */

// ===================== PARTE 1 — Encapsulamento =====================
// (1.1) Atributos privados: ninguém de fora altera nome/tipo/preco livremente.
// (1.2) Somente getters. Não há setters de propósito (ver reflexão abaixo).
//
// ===================== PARTE 2 — Construtores =====================
// (2.1) Construtor exige nome, tipo e preco e valida os valores, de modo que
//       todo Console já nasce em um estado válido.
//
// Obs.: a partir da Parte 3 a hierarquia de IConsole substitui o uso do
// Console dentro da Loja, mas a classe é mantida para registrar as Partes 1 e 2.
class Console {
    private final String nome;
    private final String tipo;   // "nintendo", "playstation" ou "portatil"
    private final double preco;

    public Console(String nome, String tipo, double preco) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do console é obrigatório.");
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo do console é obrigatório.");
        }
        if (preco <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        this.nome = nome;
        this.tipo = tipo;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPreco() {
        return preco;
    }
}
/*
 * Por que não criar setters aqui?
 * Porque um setter reabriria a porta que o encapsulamento fechou: qualquer
 * código poderia voltar a colocar o objeto em um estado inválido (ex.: preço
 * negativo) depois de criado. Sem setters, o objeto é imutável: os dados são
 * validados uma única vez, no construtor, e qualquer mudança de preço deve
 * passar por uma regra de negócio explícita, e não por uma atribuição solta.
 *
 * PERGUNTA DE CHECAGEM (Parte 1): o Main que fazia nintendo.nome = "..." ainda
 * compila depois de tornar os atributos privados?
 * Não. Fora da classe Console, o acesso direto a um atributo privado gera erro
 * de compilação ("nome has private access in Console"). Por isso o Main precisou
 * mudar: em vez de atribuir campo a campo, os objetos passam a ser criados pelo
 * construtor (Parte 2) e lidos pelos getters.
 *
 * PERGUNTA DE CHECAGEM (Parte 2): ainda é possível criar um Console sem informar
 * o preco? O que acontece com new Console("Nintendo Switch", "nintendo")?
 * Não é mais possível. Essa chamada nem compila: "constructor Console in class
 * Console cannot be applied to given types" (esperava String, String, double e
 * recebeu só String, String). Ao declarar um construtor, o Java deixa de gerar o
 * construtor padrão sem argumentos, então o objeto "vazio" também deixa de
 * existir. Além disso, mesmo informando o preço, o construtor rejeita valores
 * menores ou iguais a zero com IllegalArgumentException.
 */

// ===================== PARTE 3 — Interface e Composição =====================

// (3.1) Contrato comum a todos os consoles vendidos pela loja.
interface IConsole {
    void ligar();

    double calcularPreco();

    String getNome();
}

// (3.2) Dados que todo console tem em comum (reaproveitados por composição).
class DadosConsole {
    private final String nome;
    private final double precoBase;

    public DadosConsole(String nome, double precoBase) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do console é obrigatório.");
        }
        if (precoBase <= 0) {
            throw new IllegalArgumentException("O preço base deve ser maior que zero.");
        }
        this.nome = nome;
        this.precoBase = precoBase;
    }

    public String getNome() {
        return nome;
    }

    public double getPrecoBase() {
        return precoBase;
    }
}

// (3.3) e (3.4) Nintendo: TEM UM DadosConsole e delega a ele nome/precoBase.
// A mensagem de ligar() e o percentual (10%) agora vivem na própria classe.
class Nintendo implements IConsole {
    private static final double ACRESCIMO = 0.10;

    private final DadosConsole dados;

    public Nintendo(DadosConsole dados) {
        this.dados = dados;
    }

    @Override
    public String getNome() {
        return dados.getNome();
    }

    public double getPrecoBase() {
        return dados.getPrecoBase();
    }

    @Override
    public void ligar() {
        System.out.println("Nintendo ligado.");
    }

    @Override
    public double calcularPreco() {
        return getPrecoBase() * (1 + ACRESCIMO);
    }
}

// (3.3) e (3.4) Playstation: mesma ideia, com 20%.
// (4.1) O atributo "dados" é protected para que a subclasse consiga acessá-lo.
class Playstation implements IConsole {
    private static final double ACRESCIMO = 0.20;

    protected final DadosConsole dados;

    public Playstation(DadosConsole dados) {
        this.dados = dados;
    }

    @Override
    public String getNome() {
        return dados.getNome();
    }

    public double getPrecoBase() {
        return dados.getPrecoBase();
    }

    @Override
    public void ligar() {
        System.out.println("Playstation ligado.");
    }

    @Override
    public double calcularPreco() {
        return getPrecoBase() * (1 + ACRESCIMO);
    }
}
/*
 * REFLEXÃO (Parte 3): por que uma classe separada (DadosConsole) em vez de
 * colocar nome/precoBase direto em Nintendo e Playstation?
 * - Evita duplicação: os mesmos atributos, validações e getters não precisam
 *   ser repetidos em cada console. Se surgir um novo dado comum (ex.: fabricante),
 *   a mudança é feita em um único lugar.
 * - É composição ("tem um"), e não herança ("é um"): Nintendo não é um
 *   DadosConsole, ele TEM dados. Assim reaproveitamos código sem criar uma
 *   hierarquia artificial e sem acoplar as classes a uma superclasse.
 * - DadosConsole pode ser reaproveitada e testada isoladamente, e trocada por
 *   outra implementação sem mexer na lógica de preço de cada console.
 */

// ===================== PARTE 4 — Herança (usada de forma apropriada) =====================
// (4.1) PlaystationPortatil É UM Playstation, então herda em vez de repetir tudo.
class PlaystationPortatil extends Playstation {
    private static final double ACRESCIMO_PORTATIL = 0.15;

    public PlaystationPortatil(DadosConsole dados) {
        super(dados);
    }

    // (4.2) Mensagem específica da versão portátil.
    @Override
    public void ligar() {
        System.out.println("Playstation Portátil ligado.");
    }

    // (4.3) Percentual próprio (15% em vez dos 20% herdados).
    @Override
    public double calcularPreco() {
        return dados.getPrecoBase() * (1 + ACRESCIMO_PORTATIL);
    }
}
/*
 * REFLEXÃO (Parte 4): sobrescrever DOIS métodos é um problema?
 * Não. O número de métodos sobrescritos não é o que importa, e sim se a
 * subclasse continua respeitando o contrato da superclasse.
 * - ligar() continua ligando o console e calcularPreco() continua devolvendo
 *   o preço final de venda; só muda COMO o cálculo é feito (15% em vez de 20%).
 *   Quem usa um Playstation pode receber um PlaystationPortatil sem nenhuma
 *   surpresa: o Princípio de Substituição de Liskov (LSP) é respeitado.
 * - No caso de PlaystationPortatil.jogarDisco() lançando
 *   UnsupportedOperationException, o método RECUSA cumprir o que a superclasse
 *   promete. Quem chama jogarDisco() em um Playstation espera que ele jogue
 *   um disco; receber uma exceção quebra essa expectativa e obriga o cliente a
 *   checar o tipo concreto. Isso viola o LSP.
 * - Resumindo: mudar o cálculo é especializar o comportamento (ok); recusar a
 *   fazer o que o método promete é quebrar o contrato (violação do LSP).
 */

// ===================== PARTE 5 — Polimorfismo e Extensibilidade (OCP) =====================
// (5.4) Novo console adicionado SEM alterar nenhuma linha de Loja.
class Xbox implements IConsole {
    private static final double ACRESCIMO = 0.18;

    private final DadosConsole dados;

    public Xbox(DadosConsole dados) {
        this.dados = dados;
    }

    @Override
    public String getNome() {
        return dados.getNome();
    }

    public double getPrecoBase() {
        return dados.getPrecoBase();
    }

    @Override
    public void ligar() {
        System.out.println("Xbox ligado.");
    }

    @Override
    public double calcularPreco() {
        return getPrecoBase() * (1 + ACRESCIMO);
    }
}

class Loja {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    // (5.1) Recebe um IConsole e só chama ligar() e calcularPreco():
    // sem if/else e sem instanceof. Quem decide o comportamento é o polimorfismo.
    public void venderConsole(IConsole console) {
        console.ligar();
        double precoFinal = console.calcularPreco();
        System.out.println(console.getNome() + " -> Preço final: "
                + String.format(PT_BR, "R$ %.2f", precoFinal));
    }

    // (5.2) Vende todos os consoles de uma lista.
    public void venderVarios(List<IConsole> consoles) {
        for (IConsole console : consoles) {
            venderConsole(console);
        }
    }

    // (5.3) Soma o preço calculado de todos os consoles da lista.
    public double calcularFaturamentoTotal(List<IConsole> consoles) {
        double total = 0;
        for (IConsole console : consoles) {
            total += console.calcularPreco();
        }
        return total;
    }
}

public class Atividade_POO_Problema {

    public static void main(String[] args) {

        Locale ptBr = Locale.forLanguageTag("pt-BR");

        // ---- Parte 2 (2.2): consoles criados pelo construtor, já em estado válido ----
        System.out.println("=== Parte 2: Console criado pelo construtor ===");
        Console[] cadastro = {
                new Console("Nintendo Switch", "nintendo", 2000),
                new Console("Playstation 5", "playstation", 3000),
                new Console("Playstation Portátil", "portatil", 2500)
        };
        for (Console c : cadastro) {
            System.out.println(c.getNome() + " (" + c.getTipo() + ") -> "
                    + String.format(ptBr, "R$ %.2f", c.getPreco()));
        }

        // ---- Parte 5 (5.5): polimorfismo e extensibilidade ----
        Loja loja = new Loja();

        IConsole nintendo = new Nintendo(new DadosConsole("Nintendo Switch", 2000));
        IConsole playstation = new Playstation(new DadosConsole("Playstation 5", 3000));
        IConsole portatil = new PlaystationPortatil(new DadosConsole("Playstation Portátil", 2500));

        List<IConsole> consoles = new ArrayList<>();
        consoles.add(nintendo);
        consoles.add(playstation);
        consoles.add(portatil);

        System.out.println();
        System.out.println("=== Parte 5: venda com os consoles existentes ===");
        loja.venderVarios(consoles);
        System.out.println("Faturamento total: "
                + String.format(ptBr, "R$ %.2f", loja.calcularFaturamentoTotal(consoles)));

        // Adiciona um Xbox à lista e repete as chamadas, sem alterar a Loja.
        consoles.add(new Xbox(new DadosConsole("Xbox Series X", 3500)));

        System.out.println();
        System.out.println("=== Parte 5: venda depois de adicionar o Xbox ===");
        loja.venderVarios(consoles);
        System.out.println("Faturamento total: "
                + String.format(ptBr, "R$ %.2f", loja.calcularFaturamentoTotal(consoles)));

        /*
         * DESAFIO FINAL: o que precisou mudar em Loja para o Xbox funcionar?
         * Nada: nenhuma linha de Loja foi alterada. A Loja depende apenas da
         * abstração IConsole, e não de classes concretas; basta o Xbox
         * implementar a interface para ser vendido, somado no faturamento e
         * colocado em qualquer lista. Isso demonstra o Princípio Aberto/Fechado
         * (OCP): o sistema é ABERTO para extensão (novos consoles) e FECHADO
         * para modificação (código já testado da Loja permanece intacto). No
         * código original, cada novo console exigiria mais um "else if" dentro
         * de venderConsole().
         */
    }
}
