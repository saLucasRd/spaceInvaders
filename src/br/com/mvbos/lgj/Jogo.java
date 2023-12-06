package br.com.mvbos.lgj;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;

import br.com.mvbos.lgj.Invader.Tipos;
import br.com.mvbos.lgj.base.Elemento;
import br.com.mvbos.lgj.base.Texto;
import br.com.mvbos.lgj.base.TocarSom;
import br.com.mvbos.lgj.base.Util;

public class Jogo extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int FPS = 1000 / 20;

	private static final int JANELA_ALTURA = 680;

	private static final int JANELA_LARGURA = 540;

	private JPanel tela;


	private Graphics2D g2d;

	private BufferedImage buffer;

	private boolean[] controleTecla = new boolean[5];

	public Jogo() throws LineUnavailableException, UnsupportedAudioFileException, IOException {

		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setaTecla(e.getKeyCode(), false);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				setaTecla(e.getKeyCode(), true);
			}
		});

		buffer = new BufferedImage(JANELA_LARGURA, JANELA_ALTURA, BufferedImage.TYPE_INT_RGB);

		g2d = buffer.createGraphics();

		tela = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(buffer, 0, 0, null);
			}
		};

		getContentPane().add(tela);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize(JANELA_LARGURA, JANELA_ALTURA);
		setVisible(true);

	}



	private void setaTecla(int tecla, boolean pressionada) {
		switch (tecla) {
		case KeyEvent.VK_UP:
			controleTecla[0] = pressionada;
			break;
		case KeyEvent.VK_DOWN:
			controleTecla[1] = pressionada;
			break;
		case KeyEvent.VK_LEFT:
			controleTecla[2] = pressionada;
			break;
		case KeyEvent.VK_RIGHT:
			controleTecla[3] = pressionada;
			break;
		case KeyEvent.VK_SPACE:
			controleTecla[4] = pressionada;
			break;
		}
	}

	// Elementos do jogo

	private int vidas = 1;

	// Desenharemos mais dois tanques na base da tela
	private Elemento vida = new Tanque();


	private int vidasBarreira1 = 7;
	private int vidasBarreira2 = 7;
	private int vidasBarreira3 = 7;


	private Elemento tiroTanque;

	private Elemento tiroChefe;

	private Elemento[] tiros = new Tiro[3];

	private Texto texto = new Texto();

	private Invader chefe;

	private Elemento tanque;
	private Elemento barreira;
	private Elemento barreira2;
	private Elemento barreira3;

	private Invader[][] invasores = new Invader[11][5];

	private Invader.Tipos[] tipoPorLinha = { Tipos.PEQUENO, Tipos.MEDIO, Tipos.MEDIO, Tipos.GRANDE, Tipos.GRANDE };

	//
	private int linhaBase = 60;

	// Controle do espacamento entre os inimigos e outros elementos
	private int espacamento = 15;

	// Contador de inimigos destruidos
	private int destruidos = 0;

	private int dir;

	private int totalInimigos;

	private int contadorEspera;

	boolean novaLinha;

	boolean moverInimigos;

	private int contador;

	private int pontos;
	public int getPontos() {
		return pontos;
	}

	private int level = 1;

	private Random rand = new Random();

	private void carregarJogo() {

		tanque = new Tanque();
		tanque.setVel(3);
		tanque.setAtivo(true);
		tanque.setPx(tela.getWidth() / 2 - tanque.getLargura() / 2);
		tanque.setPy(tela.getHeight() - tanque.getAltura() - linhaBase);


		// barreiras desenhadas
		barreira = new Barreira();
		barreira.setAtivo(true);
		barreira.setPx(130);
		barreira.setPy(tela.getHeight() - barreira.getAltura() - linhaBase - 40 );

		barreira2 = new Barreira();
		barreira2.setAtivo(true);
		barreira2.setPx(230);
		barreira2.setPy(tela.getHeight() - barreira2.getAltura() - linhaBase - 40 );

		barreira3 = new Barreira();
		barreira3.setAtivo(true);
		barreira3.setPx(330);
		barreira3.setPy(tela.getHeight() - barreira3.getAltura() - linhaBase - 40 );


		tiroTanque = new Tiro();
		tiroTanque.setVel(-15);

		chefe = new Invader(Invader.Tipos.CHEFE);

		tiroChefe = new Tiro(true);
		tiroChefe.setVel(20);
		tiroChefe.setAltura(15);

		for (int i = 0; i < tiros.length; i++) {
			tiros[i] = new Tiro(true);
		}

		for (int i = 0; i < invasores.length; i++) {
			for (int j = 0; j < invasores[i].length; j++) {
				Invader e = new Invader(tipoPorLinha[j]);

				e.setAtivo(true);

				e.setPx(i * e.getLargura() + (i + 1) * espacamento);
				e.setPy(j * e.getAltura() + j * espacamento + linhaBase);

				invasores[i][j] = e;
			}
		}

		dir = 1;

		totalInimigos = invasores.length * invasores[0].length;

		contadorEspera = totalInimigos / level;

	}

	public String nomeJogador() {
		String nome = JOptionPane.showInputDialog("Fim de Jogo ,Escreva seu nome");
		System.out.println(nome);
		return nome;
	}

	public void iniciarJogo() {
		TocarSom player = new TocarSom();
		long prxAtualizacao = 0;

		while (true) {
			if (System.currentTimeMillis() >= prxAtualizacao) {

				g2d.setColor(Color.BLACK);
				g2d.fillRect(0, 0, JANELA_LARGURA, JANELA_ALTURA);

				if (destruidos == totalInimigos) {
					destruidos = 0;
					level++;
					carregarJogo();

					continue;
				}

				if (contador > contadorEspera) {
					moverInimigos = true;
					contador = 0;
					contadorEspera = totalInimigos - destruidos - level * level;

				} else {
					contador++;
				}

				if (tanque.isAtivo()) {
					if (controleTecla[2]) {
						tanque.setPx(tanque.getPx() - tanque.getVel());

					} else if (controleTecla[3]) {
						tanque.setPx(tanque.getPx() + tanque.getVel());
					}
				}

				// Pressionou espaco, adiciona tiro
				if (controleTecla[4] && !tiroTanque.isAtivo()) {
					tiroTanque.setPx(tanque.getPx() + tanque.getLargura() / 2 - tiroTanque.getLargura() / 2);
					tiroTanque.setPy(tanque.getPy() - tiroTanque.getAltura());
					tiroTanque.setAtivo(true);
					player.play("sons/shoot.wav");
				}

				if (chefe.isAtivo()) {

					chefe.incPx(tanque.getVel() - 1);

					if (!tiroChefe.isAtivo() && Util.colideX(chefe, tanque)) {
						addTiroInimigo(chefe, tiroChefe);
						player.play("sons/ufo_lowpitch.wav");
					}

					if (chefe.getPx() > tela.getWidth()) {
						chefe.setAtivo(false);
					}
				}

				boolean colideBordas = false;

				// Percorrendo primeiro as linhas, de baixo para cima
				for (int j = invasores[0].length - 1; j >= 0; j--) {

					// Depois as colunas
					for (int i = 0; i < invasores.length; i++) {

						Invader inv = invasores[i][j];

						if (!inv.isAtivo()) {

							continue;

						}

						if (Util.colide(tiroTanque, inv)) {
							inv.setAtivo(false);
							tiroTanque.setAtivo(false);
							player.play("sons/invaderkilled.wav");
							destruidos++;
							pontos = pontos + inv.getPremio() * level;

							continue;
						}

						if (moverInimigos) {

							inv.atualiza();


							if (novaLinha) {
								inv.setPy(inv.getPy() + inv.getAltura() + espacamento);
							} else {
								inv.incPx(espacamento * dir);
							}

							if (!novaLinha && !colideBordas) {
								int pxEsq = inv.getPx() - espacamento;
								int pxDir = inv.getPx() + inv.getLargura() + espacamento;

								if (pxEsq <= 0 || pxDir >= tela.getWidth())
									colideBordas = true;
							}

							if (!tiros[0].isAtivo() && inv.getPx() < tanque.getPx()) {
								player.play("sons/invadershoot.wav");
								addTiroInimigo(inv, tiros[0]);

							} else if (!tiros[1].isAtivo() && inv.getPx() > tanque.getPx() && inv.getPx() < tanque.getPx() + tanque.getLargura()) {
								addTiroInimigo(inv, tiros[1]);

							} else if (!tiros[2].isAtivo() && inv.getPx() > tanque.getPx()) {
								addTiroInimigo(inv, tiros[2]);

							}

							if (!chefe.isAtivo() && rand.nextInt(500) == destruidos) {
								chefe.setPx(0);
								chefe.setAtivo(true);

							}

						}

						// Desenhe aqui se quiser economizar no loop.
						// e.desenha(g2d);

					}
				}

				if (moverInimigos && novaLinha) {
					dir *= -1;
					novaLinha = false;

				} else if (moverInimigos && colideBordas) {
					novaLinha = true;
				}

				moverInimigos = false;

				if (tiroTanque.isAtivo()) {
					tiroTanque.incPy(tiroTanque.getVel());

					if (Util.colide(tiroTanque, chefe)) {
						pontos = pontos + chefe.getPremio() * level;
						chefe.setAtivo(false);
						tiroTanque.setAtivo(false);

					} else if (tiroTanque.getPy() < 0) {
						tiroTanque.setAtivo(false);
					}

					tiroTanque.desenha(g2d);
				}

				if (tiroChefe.isAtivo()) {
					tiroChefe.incPy(tiroChefe.getVel());

					if (Util.colide(tiroChefe, tanque)) {

						vidas--;
						tiroChefe.setAtivo(false);

						} else if (tiroChefe.getPy() > tela.getHeight() - linhaBase - tiroChefe.getAltura()) {
						tiroChefe.setAtivo(false);
					} else
						tiroChefe.desenha(g2d);



				}

				for (int i = 0; i < tiros.length; i++) {
					if (tiros[i].isAtivo()) {
						tiros[i].incPy(+10);

						if (Util.colide(tiros[i], tanque)) {
							vidas--;
							player.play("sons/explosion.wav");
							tiros[i].setAtivo(false);

						} else if (Util.colide(tiros[i], barreira)) {
							vidasBarreira1--;
							player.play("sons/barrier.wav");

							if (vidasBarreira1 == 0) {
								player.play("sons/explosion.wav");
								barreira.setAtivo(false);
								barreira.setPx(-100);
							}
							tiros[i].setAtivo(false);



						} else if (Util.colide(tiros[i], barreira2)) {
							player.play("sons/barrier.wav");
							vidasBarreira2--;
							if (vidasBarreira2 == 0) {
								player.play("sons/explosion.wav");
								barreira2.setAtivo(false);
								barreira2.setPx(-100);
							}
							tiros[i].setAtivo(false);

						} else if (Util.colide(tiros[i], barreira3)) {
							vidasBarreira3--;
							player.play("sons/barrier.wav");
							if (vidasBarreira3 == 0) {
								player.play("sons/explosion.wav");
								barreira3.setAtivo(false);
								barreira3.setPx(-100);
							}
							tiros[i].setAtivo(false);


						} else if (tiros[i].getPy() > tela.getHeight() - linhaBase - tiros[i].getAltura())
							tiros[i].setAtivo(false);

						tiros[i].desenha(g2d);
					}
				}

				// Desenhe aqui para as naves ficarem acima dos tiros
				for (int i = 0; i < invasores.length; i++) {
					for (int j = 0; j < invasores[i].length; j++) {
						Invader e = invasores[i][j];
						e.desenha(g2d);
					}
				}

				tanque.atualiza();
				tanque.desenha(g2d);

				barreira.atualiza();
				barreira.desenha(g2d);

				barreira2.atualiza();
				barreira2.desenha(g2d);

				barreira3.atualiza();
				barreira3.atualiza();
				barreira3.desenha(g2d);

				chefe.atualiza();
				chefe.desenha(g2d);

				g2d.setColor(Color.WHITE);

				texto.desenha(g2d, String.valueOf(pontos), 10, 20);
				//texto.desenha(g2d, "Level " + level, tela.getWidth() - 100, 20);
				texto.desenha(g2d, String.valueOf(vidas), 10, tela.getHeight() - 10);

				// Linha base
				g2d.setColor(Color.GREEN);
				g2d.drawLine(0, tela.getHeight() - linhaBase, tela.getWidth(), tela.getHeight() - linhaBase);

				for (int i = 1; i < vidas; i++) {
					vida.setPx(i * vida.getLargura() + i * espacamento);
					vida.setPy(tela.getHeight() - vida.getAltura());

					vida.desenha(g2d);
				}


				// para ao loop do jogo
				if (vidas == 0 || totalInimigos == 0) {
					System.out.println("game over");
					nomeJogador();
					//new Scoreboard();
					System.out.println(pontos);
					break;
				}

				tela.repaint();

				prxAtualizacao = System.currentTimeMillis() + FPS;
			}
		}

	}

	public void addTiroInimigo(Elemento inimigo, Elemento tiro) {
		tiro.setAtivo(true);
		tiro.setPx(inimigo.getPx() + inimigo.getLargura() / 2 - tiro.getLargura() / 2);
		tiro.setPy(inimigo.getPy() + inimigo.getAltura());

	}

	public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
		Jogo jogo = new Jogo();
		Scoreboard score = new Scoreboard();
		//Jogador jogador = new Jogador();
		jogo.carregarJogo();
		jogo.iniciarJogo();


	}



}
