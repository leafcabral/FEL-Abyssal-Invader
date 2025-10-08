import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage; // Importa para trabalhar com imagens.
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO; // Importa para carregar imagens.

import java.util.ArrayList;
import java.util.Random;

public class Enemy {
	public int x, y, width, height, speed;
	public double minDistance;
	public String name;
	private BufferedImage enemyImage; // Variável para armazenar a imagem do inimigo.
	private static BufferedImage explosionGif;

	public ArrayList<Enemy> enemies = new ArrayList<>();
	
	private static Random random;

	public Enemy(int x, int y, int width, int height, int speed) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.minDistance = Math.pow(Math.max(width, height) * 3, 2);

		if (random == null) { this.random = new Random(); }
		String names[] = {"alein1.png", "alein2.png", "alein3.png", "alein4.png"};
		
		// Tenta carregar a imagem do inimigo.
		try {
			this.name = "res/" + names[random.nextInt(4)];
			this.enemyImage = ImageIO.read(new File(name));
		} catch (IOException e) {
			System.err.println("Erro ao carregar a imagem do inimigo: " + this.name);
			e.printStackTrace();
			// Se a imagem não carregar, 'enemyImage' será null.
		}

		enemies.add(this);
	}

	public void update() {
		y += speed;
	}

	// O método 'draw' agora desenha a imagem do inimigo em vez de um quadrado.
	public void draw(Graphics2D g2) {
		if (enemyImage != null) {
			// Desenha a imagem do inimigo.
			g2.drawImage(enemyImage, x, y, width, height, null);
			/*
			if (!this.enemies.isEmpty()) {
				for (Enemy enemy : this.enemies) {
					double distance = (enemy.x - x) + (enemy.y - y);

					while (distance <= this.minDistance) {
						this.x = (this.minDistance > 800) ? this.x-- : this.x++;
					}
				}
				g2.drawImage(enemyImage, x, y, width, height, null);
			} else {
				g2.drawImage(enemyImage, x, y, width, height, null);
			}
			*/
		} else {
			// Se a imagem não for encontrada, desenha um quadrado vermelho como alternativa.
			g2.setColor(Color.RED);
			g2.fillRect(x, y, width, height);
		}
	}
	
	public void explosion(Graphics2D g2) {
		if (explosionGif != null) {
			g2.drawImage(explosionGif, x, y, width, height, null);	
		}
		else {
			g2.setColor(Color.ORANGE);
			g2.fillOval(x, y, width, height);
		}
	}
}