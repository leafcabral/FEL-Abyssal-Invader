package game;

import game.entities.Bullet;
import game.entities.Enemy;
import game.entities.Player;
import game.managers.GraphicsManager;
import game.managers.InputManager;
import game.managers.ResourceManager;
import static game.entities.Player.WeaponType.*;
import game.entities.patterns.MovementPattern;


import game.utils.Vec2D;
import java.awt.AlphaComposite;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.concurrent.CopyOnWriteArrayList;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;

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
	private int best = 0;
	private int wave = 1;
	private final float WAVE_DEFAULT_DURATION = 30f;
	private float waveTime = WAVE_DEFAULT_DURATION;
	private long startTime;
	private long pauseStartedTime = 0, totalPauseTime = 0;
	private String waveTimeText = "";

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
			resources.getImage("background.png"), Color.BLACK,
			resources
		);
		this.input = new InputManager();
		
		this.random = new Random();
		
		this.status = GameStatus.MAIN_MENU;
		this.lastFrameTime = Instant.now();
		this.startTime = System.nanoTime();
		
		this.player = new Player(
			new Vec2D(screenWidth / 2, screenHeight - 100),
			resources.getImage("player-idle.png"),
			resources.getImage("player-moving-1.png"),
			resources.getImage("player-moving-2.png")
		);
		this.player.moveX(-this.player.spriteShape.width / 2);
		this.player.spriteDirection = new Vec2D(0, -1);
		this.player.collisionShape.grow(
			- this.player.collisionShape.width / 3,
			- this.player.collisionShape.height / 3
		);
		
		this.enemies = new CopyOnWriteArrayList<>();
		this.bullets = new CopyOnWriteArrayList<>();
		
		dummyEnemy = new Enemy(
			new Vec2D(screenWidth, screenHeight),
			resources.getImage("enemy-1.png"),
			MovementPattern.newStraight(100)
		);
		dummyBullet = Bullet.newDefaultBullet(
			new Vec2D(screenWidth, screenHeight),
			resources.getImage("bullet-default.png")
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
		while (gameThread != null) {
			updateDelta();
			update();
			repaint();
		}
	}

	private void handlePlayerInput() {
		if (input.isActionPressed("moveLeft")) {
			player.movementDirection.x = -1;
		} else if (input.isActionPressed("moveRight")) {
			player.movementDirection.x = 1;
		} else {
			player.movementDirection.x = 0;
		}		
		
		player.update(delta);
		
		// Se apagar essa linha o programa não funciona
		System.out.println(player.getVelocity().x);
		
		// Se fora, coloca pra dentro
		if (player.right() > screenWidth) {
			player.moveX(screenWidth - player.right());
		} else if (player.left() < 0) {
			player.moveX(0 - player.left());
		}
		
		if (input.isActionPressed("shoot") && player.canShoot()) {
			Vec2D bulletPos = new Vec2D(
				player.getCenter().x - dummyBullet.spriteShape.width / 2,
				player.collisionShape.y
			);
			switch (player.getCurrentWeapon()) {
				case DEFAULT:
					bullets.add(Bullet.newDefaultBullet(
						bulletPos,
						resources.getImage("bullet-default.png")
					));
					break;
				case SHOTGUN:
					Collections.addAll(
						bullets,
						Bullet.newShotgunBullets(
							bulletPos,
							resources.getImage("bullet-shotgun.png")
						)
					);
					break;
				case BLAST:
					bullets.add(Bullet.newBlastBullet(
						bulletPos,
						resources.getImage("bullet-blast.png")
					));
					break;
					
			}
			resources.playSound("shot.wav");
			player.resetShootTimer();
		}
	}
	
	private void spawnEnemy() {
		Vec2D pos = new Vec2D();
		pos.x = random.nextInt(screenWidth - (int)dummyEnemy.spriteShape.width);
		pos.y = -(int)dummyEnemy.spriteShape.height;
		String imgName = "enemy-" + (random.nextInt(6) + 1) + ".png";

		MovementPattern pattern = MovementPattern.newStraight(100);
		float movementType = random.nextFloat();
		if (movementType <= 0.7f) {
			pattern = MovementPattern.newStraight(100);
		} else {
			pattern = MovementPattern.newWave(50, 1f, 2f);
		}
		Enemy enemy = new Enemy(pos, resources.getImage(imgName), pattern);

		boolean canSpawn = true;
		for (Enemy enemyListed : enemies) {
			if (enemy.collisionShape.intersects(enemyListed.collisionShape)) {
				canSpawn = false;
				break;
			}
		}

		if (canSpawn) {
			enemies.add(enemy);
		}
	}
	
	public void update() {
		if (status == GameStatus.MAIN_MENU) {
			graphics.updateBackground(delta);
		}
		if (status != GameStatus.RUNNING) {
			return;
		}
		
		graphics.updateBackground(delta);
		handlePlayerInput();
		
		// Remove os que sairam da tela
		bullets.removeIf(bullet -> {
			bullet.update(delta);
			return bullet.collisionShape.y < 0; 
		});
		enemies.removeIf(enemy -> {
			enemy.update(delta);
			return enemy.collisionShape.y > screenHeight;
		});
		
		// Cria novo inimigo
		if ((nextSpawnTime -= delta) <= 0) {
			spawnEnemy();
			// Entre 0.5 a 1.5 segundos
			nextSpawnTime = random.nextFloat() + 0.5f;
		}

		waveTime -= delta;

		if (waveTime <= 0) {
			wave++;
			waveTime = WAVE_DEFAULT_DURATION;
  		}

		checkCollisions();
	}

	private void checkCollisions() {
		ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
		ArrayList<Enemy> enemiesToRemove = new ArrayList<>();
		
		for (Bullet bullet : bullets) {
			for (Enemy enemy : enemies) {
				if (bullet.collisionShape.intersects(enemy.collisionShape)) {
					bulletsToRemove.add(bullet);
					enemiesToRemove.add(enemy);
					resources.playSound("explosion.wav");
					score += 10;
					if(best<=score){
						best=score;
					}
					break;
				}
			}
		}

		for (Enemy enemy : enemies) {
			if (player.collisionShape.intersects(enemy.collisionShape)) {
				if (player.takeDamage()) {
					System.out.println("Fim de Jogo!");
					status = GameStatus.GAME_OVER;
				}
				else {
					enemiesToRemove.add(enemy);
				}
				resources.playSound("explosion.wav");
			}
		}

		bullets.removeAll(bulletsToRemove);
		enemies.removeAll(enemiesToRemove);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Vec2D screenVec = new Vec2D(screenWidth, screenHeight);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		// Desenhar fundo
		graphics.drawBackground(g2);

		// Menu principal
		if (status == GameStatus.MAIN_MENU) {
			g2.setColor(Color.BLACK);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2.fillRect(0, 0, screenWidth, screenHeight);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
			graphics.drawMainMenu(g2, menuSelectedOptionIndex);
			return;
		}
		
		// Desenhar jogo
		g2.setColor(Color.WHITE);

		graphics.drawObjects(g2, new ArrayList(bullets));
		graphics.drawObjects(g2, new ArrayList(enemies));
		graphics.drawObject(g2, player);
		
		g2.setColor(Color.WHITE);
		g2.setFont(resources.getFont("photonico.ttf", Font.PLAIN, 32));
		String scoreText = Integer.toString(score);
		int textWidth = g2.getFontMetrics().stringWidth(scoreText);
		g2.drawString(scoreText, (screenWidth - textWidth) / 2, 40);
		
		g2.setColor(Color.LIGHT_GRAY);
		g2.setFont(resources.getFont("photonico.ttf", Font.BOLD, 16));
		String waveText = getTime() + (" • WAVE " + Integer.toString(wave));
		int waveTextHeight = g2.getFontMetrics().getHeight();
		int waveTextWidth = g2.getFontMetrics().stringWidth(waveText);
		g2.drawString(waveText, (screenWidth - waveTextWidth) / 2, 40 + waveTextHeight);
		g2.setColor(Color.WHITE);
		
		graphics.drawWeapons(g2, screenVec, player, resources);
		graphics.drawLifes(g2, player, resources);

		// Menu de pause
		if (status == GameStatus.PAUSED) {
			graphics.drawPauseMenu(g2, menuSelectedOptionIndex);
			graphics.displayHighScore(g2, best);
		} else if (status == GameStatus.GAME_OVER) {
			graphics.drawGameOverMenu(g2, menuSelectedOptionIndex);
		}

		g2.dispose();
	}

	private void resetGame() {
		score = 0;
		player.collisionShape.x = screenWidth / 2 - player.collisionShape.width;
		bullets.clear();
		enemies.clear();
		player.resetLife();
		startTime = System.nanoTime();
		pauseStartedTime = 0;
		totalPauseTime = 0;
		waveTime = WAVE_DEFAULT_DURATION;
		status = GameStatus.RUNNING;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		input.keyPressed(e.getKeyCode());
		
		// Só deve checar quando clicar, e não todo frame
		handleMenuInput();
		
		// Mudar armas
		if (input.isActionPressed("weapon1")) {
			player.switchWeapon(Player.WeaponType.DEFAULT);
		} else if (input.isActionPressed("weapon2")) {
			player.switchWeapon(Player.WeaponType.SHOTGUN); 
		} else if (input.isActionPressed("weapon3")) {
			player.switchWeapon(Player.WeaponType.BLAST);
		}
	}

	private void handleMenuInput() {
		if (input.isActionPressed("menu")) {
			if (status == GameStatus.RUNNING) {
				status = GameStatus.PAUSED;
				menuSelectedOptionIndex = 0;
				pauseStartedTime = System.nanoTime();
			}
			else if (status == GameStatus.PAUSED) {
				status = GameStatus.RUNNING;
				totalPauseTime += System.nanoTime() - pauseStartedTime;
				pauseStartedTime = 0;
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
			if (status != GameStatus.RUNNING) {
				GraphicsManager.MenuOption option = GraphicsManager.MenuOption.RESUME;
				
				switch (status) {
					case MAIN_MENU -> option = graphics.mainMenuOptions[menuSelectedOptionIndex];
					case PAUSED -> option = graphics.pausedOptions[menuSelectedOptionIndex];
					case GAME_OVER -> option = graphics.gameOverOptions[menuSelectedOptionIndex];
				}
				switch (option) {
					case START -> {
						resetGame();
					}
					case RESUME -> {
						status = GameStatus.RUNNING;
						totalPauseTime += System.nanoTime() - pauseStartedTime;
						pauseStartedTime = 0;
					}
					case RESTART -> resetGame();
					case QUIT -> System.exit(0);
				}
				
				menuSelectedOptionIndex = 0;
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		input.keyReleased(e.getKeyCode());
	}

	public String getTime() {
		if (status != GameStatus.RUNNING) { return waveTimeText; }

		long secondsTotal = (totalPauseTime > 0) ? ((System.nanoTime() -  startTime) - totalPauseTime) / 1_000_000_000 : (System.nanoTime() - startTime) / 1_000_000_000;
		long minutes = (secondsTotal / 60) % 60;
		long seconds = secondsTotal % 60;

		waveTimeText = String.format("%02d:%02d", minutes, seconds);

		return waveTimeText;
	}

}
