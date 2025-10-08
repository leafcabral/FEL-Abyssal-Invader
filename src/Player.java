import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage; // Importa para trabalhar com imagens.
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO; // Importa para carregar imagens.

public class Player {
	public int x, y, width, height, speed;
	public static final String name = "res/player1.png";
	private BufferedImage playerImage; // Variável para armazenar a imagem da nave.

	public Player(int x, int y, int width, int height, int speed) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = speed;

		// Tenta carregar a imagem da nave do arquivo "res/player.png".
		try {
			this.playerImage = ImageIO.read(new File(name));
		} catch (IOException e) {
			System.err.println("Erro ao carregar a imagem da nave: " + name);
			e.printStackTrace();
			// Se a imagem não carregar, 'playerImage' será null, e o draw usará um quadrado.
		}
	}

	public void draw(Graphics2D g2) {
		// Se a imagem foi carregada com sucesso, desenha a imagem.
		if (playerImage != null) {
			g2.drawImage(playerImage, x, y, width, height, null);
		} else {
			// Caso contrário (se houver erro no carregamento da imagem), desenha um quadrado azul.
			g2.setColor(Color.BLUE);
			g2.fillRect(x, y, width, height);
		}
	}
}