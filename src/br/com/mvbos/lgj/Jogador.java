package br.com.mvbos.lgj;

public class Jogador {
    private int rank;
    private String nome;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getNome() {
        return nome;
    }

    public static void setNome(String nome) {
        this.nome = nome;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    private int pontos;

}
