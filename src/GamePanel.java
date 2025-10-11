import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.awt.Font;
import java.awt.image.BufferedImage; // Importa para trabalhar com imagens.
import java.util.concurrent.CopyOnWriteArrayList;

import java.time.Instant;
import java.time.Duration;

public class GamePanel extends JPanel implements Runnable, KeyListener {
	public enum GameStatus {
		MAIN_MENU,
		GAME_OVER,
		PAUSED,
		RUNNING
	}
	
	private final int screenWidth = 800;
	private final int screenHeight = 800;
	private final BufferedImage background;
	
	private GameStatus status;
	private Instant lastFrameTime;
	private float delta = 0;
	private int score = 0;

	private Thread gameThread;
	private final Random random;
	
	private final Player player;
	private final CopyOnWriteArrayList<Enemy> enemies;
	private final CopyOnWriteArrayList<Bullet> bullets;
	
	private final ImageManager imageManager;
	private final SoundManager soundManager;
	private final InputManager inputManager;
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.BLACK);
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.addKeyListener(this);

		this.soundManager = new SoundManager();
		this.imageManager = new ImageManager();
		this.inputManager = new InputManager();
		
		this.background = ImageManager.getImage("background1");
		this.random = new Random();
		
		this.status = GameStatus.MAIN_MENU;
		this.lastFrameTime = Instant.now();
		
		this.player = new Player(new Vec2D(
			screenWidth / 2, screenHeight - 100
		));
		this.player.pos.x -= this.player.size.x/2;
		this.enemies = new CopyOnWriteArrayList<>();
		this.bullets = new CopyOnWriteArrayList<>();
	}

	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	private void updateDelta() {
		Instant current = Instant.now();
		Duration diff = Duration.between(lastFrameTime, current);
		this.lastFrameTime = current;

		this.delta = diff.toNanos() / 1e9f;
	}
	
	@Override
	public void run() {
		this.status = GameStatus.RUNNING;
		
		while (gameThread != null) {
			updateDelta();
			update();
			repaint();
		}
	}

	private void handlePlayerInput() {
		float playerVelocity = player.speed * delta;
		
		if (inputManager.isActionPressed("moveLeft")) {
			player.pos.x -= playerVelocity;
		}
		if (inputManager.isActionPressed("moveRight")) {
			player.pos.x += playerVelocity;
		}
		// Se fora, coloca pra dentro
		player.pos.x = Math.max(0, Math.min(
			player.pos.x, screenWidth - player.size.x)
		);
		
		if (inputManager.isActionPressed("shoot")) {
			Bullet bullet = player.shoot();
			if (bullet != null) {
				bullets.add(bullet);
				soundManager.playSound("shot.wav");
			}
		}
		
		// TODO: Adicionar mudança de armas
	}
	
	public void update() {
		if (status != GameStatus.RUNNING) {
			return;
		}

		handlePlayerInput();
		player.update(delta);

		bullets.removeIf(bullet -> {
			bullet.update(delta);
			return bullet.pos.y < 0; 
		});
		enemies.removeIf(enemy -> {
			enemy.update(delta);
			return enemy.pos.y > screenHeight;
		});
		
		if (random.nextInt(100) < 2) {
			Vec2D pos = new Vec2D(
				random.nextInt(screenWidth - 50), -50
			);
			enemies.add(new Enemy(
				pos, "alien" + random.nextInt(4) + 1)
			);
		}

		checkCollisions();
	}

	private void checkCollisions() {
		Rectangle playerRect = new Rectangle(
			(int) player.pos.x,
			(int) player.pos.y,
			(int) player.size.x,
			(int) player.size.y
		);

		Iterator<Bullet> bulletIterator = bullets.iterator();
		while (bulletIterator.hasNext()) {
			Bullet bullet = bulletIterator.next();
			Rectangle bulletRect = new Rectangle(
				(int) bullet.pos.x,
				(int) bullet.pos.y,
				(int) bullet.size.x,
				(int) bullet.size.y
			);

			Iterator<Enemy> enemyIterator = enemies.iterator();
			while (enemyIterator.hasNext()) {
				Enemy enemy = enemyIterator.next();
				Rectangle enemyRect = new Rectangle(
					(int) enemy.pos.x,
					(int) enemy.pos.y,
					(int) enemy.size.x,
					(int) enemy.size.y
				);

				if (bulletRect.intersects(enemyRect)) {
					bulletIterator.remove(); 
					enemyIterator.remove();
					soundManager.playSound("explosion.wav");
					score += 10;
					break;
				}
			}
		}

		Iterator<Enemy> enemyIterator = enemies.iterator();
		while (enemyIterator.hasNext()) {
			Enemy enemy = enemyIterator.next();
			Rectangle enemyRect = new Rectangle(
				(int) enemy.pos.x,
				(int) enemy.pos.y,
				(int) enemy.size.x,
				(int) enemy.size.y
			);
			if (playerRect.intersects(enemyRect)) {
				soundManager.playSound("explosion.wav");
				System.out.println("Fim de Jogo!");
				status = GameStatus.GAME_OVER;
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

	   // Desenhar fundo
		if (background != null) {
			g2.drawImage(background, 0, 0, screenWidth, screenHeight, null);
		} else {
			// Se a imagem de fundo não carregar, preenche com preto.
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, screenWidth, screenHeight);
		}

		// Desenhar jogo
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 20));
		g2.drawString("Score: " + score, 10, 25);

		g2.setFont(new Font("Arial", Font.PLAIN, 15));
		String exitText = "Pressione ESC para sair";
		int exitTextWidth = g2.getFontMetrics().stringWidth(exitText);
		g2.drawString(exitText, screenWidth - exitTextWidth - 10, 20);

		player.draw(g2); 

		for (Bullet b : bullets) {
			b.draw(g2);
		}

		for (Enemy e : enemies) {
			e.draw(g2);
		}

		// Menu de pause
		if (status == GameStatus.PAUSED) {
			int menuWidth = 200;
			int menuHeight = 200;
			g2.fillRect(screenWidth/2 - menuWidth/2, screenHeight/2 - menuHeight/2, menuWidth, menuHeight);
		} else if (status == GameStatus.GAME_OVER) {
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Arial", Font.BOLD, 50));
			String gameOverText = "GAME OVER";
			int gameOverTextWidth = g2.getFontMetrics().stringWidth(gameOverText);
			g2.drawString(gameOverText, (screenWidth - gameOverTextWidth) / 2, screenHeight / 2 - 50);

			g2.setFont(new Font("Arial", Font.PLAIN, 30));
			String scoreText = "Pontuação Final: " + score;
			int scoreTextWidth = g2.getFontMetrics().stringWidth(scoreText);
			g2.drawString(scoreText, (screenWidth - scoreTextWidth) / 2, screenHeight / 2);
			
			String restartText = "Pressione ENTER para jogar novamente";
			int restartTextWidth = g2.getFontMetrics().stringWidth(restartText);
			g2.drawString(restartText, (screenWidth - restartTextWidth) / 2, screenHeight / 2 + 50);

		}

		g2.dispose();
	}

	private void resetGame() {
		score = 0;
		player.pos.x = screenWidth / 2 - 25;
		bullets.clear();
		enemies.clear();
		status = GameStatus.GAME_OVER;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		inputManager.keyPressed(e.getKeyCode());
		
		// Só deve checar quando clicar, e não todo frame
		if (inputManager.isActionPressed("menu")) {
			if (status == GameStatus.RUNNING) {
				status = GameStatus.PAUSED;
			}
			else if (status == GameStatus.PAUSED) {
				status = GameStatus.RUNNING;
			}
		}
		if (inputManager.isActionPressed("quit")) {
			if (status != GameStatus.RUNNING) {
				System.exit(0);
			}
		}
		if (inputManager.isActionPressed("restart")) {
			if (status == GameStatus.GAME_OVER) {
				resetGame();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		inputManager.keyReleased(e.getKeyCode());
	}
}