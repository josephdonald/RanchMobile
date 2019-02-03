package com.cursoandroid.ranchmobile.classes;

public class DadosEstacao {

    private int idDadosEstacao, fkEstacao;
    private float umidade1, umidade2, umidade3, temperatura_solo, tensao_bateria;
    private String tempo_leitura;

    public DadosEstacao() {


    }

    public int getIdDadosEstacao() {
        return idDadosEstacao;
    }

    public void setIdDadosEstacao(int idDadosEstacao) {
        this.idDadosEstacao = idDadosEstacao;
    }

    public float getUmidade1() {
        return umidade1;
    }

    public void setUmidade1(float umidade1) {
        this.umidade1 = umidade1;
    }

    public float getUmidade2() {
        return umidade2;
    }

    public void setUmidade2(float umidade2) {
        this.umidade2 = umidade2;
    }

    public float getUmidade3() {
        return umidade3;
    }

    public void setUmidade3(float umidade3) {
        this.umidade3 = umidade3;
    }

    public float getTemperatura_solo() {
        return temperatura_solo;
    }

    public void setTemperatura_solo(float temperatura_solo) {
        this.temperatura_solo = temperatura_solo;
    }

    public float getTensao_bateria() {
        return tensao_bateria;
    }

    public void setTensao_bateria(float tensao_bateria) {
        this.tensao_bateria = tensao_bateria;
    }

    public String getTempo_leitura() {
        return tempo_leitura;
    }

    public void setTempo_leitura(String tempo_leitura) {
        this.tempo_leitura = tempo_leitura;
    }

    public int getFkEstacao() {
        return fkEstacao;
    }

    public void setFkEstacao(int fkEstacao) {
        this.fkEstacao = fkEstacao;
    }
}
