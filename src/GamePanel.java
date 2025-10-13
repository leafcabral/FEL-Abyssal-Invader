import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.Random;
import java.awt.Font;
import java.awt.image.BufferedImage; // Importa para trabalhar com imagens.
import java.util.concurrent.CopyOnWriteArrayList;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import javax.swing.JButton;

public class GamePanel extends JPanel implements Runnable, KeyListener {
	public enum GameStatus {
		MAIN_MENU,
		GAME_OVER,
		PAUSED,
		RUNNING
	}
	
	private final int screenWidth = 800;
	private final int screenHeight = 800;
	
	private GameStatus status;
	private Instant lastFrameTime;
	private float delta = 0;
	private int score = 0;

	private Thread gameThread;
	private final Random random;
	
	private final Enemy dummyEnemy;
	private final Bullet dummyBullet;
	
	private final Player player;
	private final CopyOnWriteArrayList<Enemy> enemies;
	private final CopyOnWriteArrayList<Bullet> bullets;
	
	private final ResourceManager resources;
	private final GraphicsManager graphics;
	private final InputManager input;
	
	private float nextSpawnTime = 0;
	private int menuSelectedOptionIndex = 0;
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.BLACK);
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.addKeyListener(this);

		this.resources = new ResourceManager(true);
		this.graphics = new GraphicsManager(
			new Vec2D(screenWidth, screenHeight),
			resources.getImage("background1"),
			Color.BLACK
		);
		this.input = new InputManager();
		
		this.random = new Random();
		
		this.status = GameStatus.MAIN_MENU;
		this.lastFrameTime = Instant.now();
		
		this.player = new Player(
			new Vec2D(screenWidth / 2, screenHeight - 100),
			resources.getImage("player")
		);
		this.player.pos.x -= this.player.size.x/2;
		this.enemies = new CopyOnWriteArrayList<>();
		this.bullets = new CopyOnWriteArrayList<>();
		
		dummyEnemy = new Enemy(
			new Vec2D(screenWidth, screenHeight),
			resources.getImage("alien1")
		);
		dummyBullet = Bullet.newDefaultBullet(
			new Vec2D(screenWidth, screenHeight),
			resources.getImage("bullet1")
		);
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
		
		if (input.isActionPressed("moveLeft")) {
			player.pos.x -= playerVelocity;
		}
		if (input.isActionPressed("moveRight")) {
			player.pos.x += playerVelocity;
		}
		// Se fora, coloca pra dentro
		player.pos.x = Math.max(0, Math.min(
			player.pos.x, screenWidth - player.size.x)
		);
		
		if (input.isActionPressed("shoot")) {
			if (player.canShoot()) {
				bullets.add(Bullet.newDefaultBullet(
					player.getCenter().add(
						new Vec2D(0, -player.size.y)
					),
					resources.getImage("bullet1")
				));
				resources.playSound("shot");
				
				player.resetShootTimer();
			}
		}
		
		// TODO: Adicionar mudança de armas
	}
	
	private void spawnEnemy() {
		Vec2D pos = new Vec2D();
		pos.x = random.nextInt(screenWidth - (int)dummyEnemy.size.x);
		pos.y = -(int)dummyEnemy.size.y;
		String imgName = "alien" + (random.nextInt(4) + 1);
		
		enemies.add(new Enemy(pos, resources.getImage(imgName)));
	}
	
	public void update() {
		if (status != GameStatus.RUNNING) {
			return;
		}

		handlePlayerInput();
		player.update(delta);
		
		// Remove os que sairam da tela
		bullets.removeIf(bullet -> {
			bullet.update(delta);
			return bullet.pos.y < 0; 
		});
		enemies.removeIf(enemy -> {
			enemy.update(delta);
			return enemy.pos.y > screenHeight;
		});
		
		// Cria novo inimigo
		if ((nextSpawnTime -= delta) <= 0) {
			spawnEnemy();
			// Entre 0.5 a 1.5 segundos
			nextSpawnTime = random.nextFloat() + 0.5f;
		}

		checkCollisions();
	}

	private void checkCollisions() {
		ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
		ArrayList<Enemy> enemiesToRemove = new ArrayList<>();
		
		for (Bullet bullet : bullets) {
			for (Enemy enemy : enemies) {
				if (bullet.collides(enemy)) {
					bulletsToRemove.add(bullet);
					enemiesToRemove.add(enemy);
					resources.playSound("explosion");
					score += 10;
					break;
				}
			}
		}

		for (Enemy enemy : enemies) {
			if (player.collides(enemy)) {
				if (player.takeDamage()) {
					System.out.println("Fim de Jogo!");
					status = GameStatus.GAME_OVER;
				}
				else {
					enemiesToRemove.add(enemy);
				}
				resources.playSound("explosion");
			}
		}

		bullets.removeAll(bulletsToRemove);
		enemies.removeAll(enemiesToRemove);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Desenhar fundo
		graphics.drawBackground(g2);

		// Desenhar jogo
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 20));
		g2.drawString("Score: " + score, 10, 25);

		g2.setFont(new Font("Arial", Font.PLAIN, 15));
		String exitText = "Pressione ESC para sair";
		int exitTextWidth = g2.getFontMetrics().stringWidth(exitText);
		g2.drawString(exitText, screenWidth - exitTextWidth - 10, 20);

		graphics.drawObjects(g2, new ArrayList(bullets));
		graphics.drawObjects(g2, new ArrayList(enemies));
		graphics.drawObject(g2, player);

		// Menu de pause
		if (status == GameStatus.PAUSED) {
			graphics.drawPauseMenu(g2, menuSelectedOptionIndex);
		} else if (status == GameStatus.GAME_OVER) {
			graphics.drawGameOverMenu(g2, menuSelectedOptionIndex);
		}

		g2.dispose();
	}

	private void resetGame() {
		score = 0;
		player.pos.x = screenWidth / 2 - 25;
		bullets.clear();
		enemies.clear();
		status = GameStatus.RUNNING;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		input.keyPressed(e.getKeyCode());
		
		// Só deve checar quando clicar, e não todo frame
		handleMenuInput();
	}

	private void handleMenuInput() {
		if (input.isActionPressed("menu")) {
			if (status == GameStatus.RUNNING) {
				status = GameStatus.PAUSED;
				menuSelectedOptionIndex = 0;
			}
			else if (status == GameStatus.PAUSED) {
				status = GameStatus.RUNNING;
			}
		}
		
		// menuSelectedOptionIndex
		if (input.isActionPressed("up")) {
			menuSelectedOptionIndex++;
			menuSelectedOptionIndex	%= 2;
			if (menuSelectedOptionIndex < 0) {
				menuSelectedOptionIndex *= -1;
			}
		}
		if (input.isActionPressed("down")) {
			menuSelectedOptionIndex--;
			menuSelectedOptionIndex	%= -2;
			if (menuSelectedOptionIndex < 0) {
				menuSelectedOptionIndex *= -1;
			}
		}
		
		if (input.isActionPressed("confirm")) {
			GraphicsManager.MenuOption option = GraphicsManager.MenuOption.RESUME;
			
			switch (status) {
				case GameStatus.PAUSED -> option = graphics.pausedOptions[menuSelectedOptionIndex];
				case GameStatus.GAME_OVER -> option = graphics.gameOverOptions[menuSelectedOptionIndex];
			}
			switch (option) {
				case GraphicsManager.MenuOption.RESUME -> status = GameStatus.RUNNING;
				case GraphicsManager.MenuOption.RESTART -> resetGame();
				case GraphicsManager.MenuOption.QUIT -> System.exit(0);
			}
			
			menuSelectedOptionIndex = 0;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		input.keyReleased(e.getKeyCode());
	}
}