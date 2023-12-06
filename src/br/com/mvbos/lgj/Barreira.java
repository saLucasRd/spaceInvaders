package br.com.mvbos.lgj;

import br.com.mvbos.lgj.base.Elemento;

import java.awt.*;

public class Barreira extends Elemento {

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    private int vida = 2;

    public Barreira() {
        setLargura(60);
        setAltura(20);
    }

    @Override
    public void atualiza() {
    }

    @Override
    public void desenha(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.fillRect(getPx() + getLargura() / 5, 5, 2, 2);
        g.setStroke(new BasicStroke(2));

        g.fillRect(getPx(), getPy(), getLargura(), getAltura());

    }

}
