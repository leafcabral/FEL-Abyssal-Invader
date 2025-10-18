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
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.awt.Font;
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
	private String waveTimeText;

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
			resources.getImage("background.png"),
			Color.BLACK
		);
		this.input = new InputManager();
		
		this.random = new Random();
		
		this.status = GameStatus.MAIN_MENU;
		this.lastFrameTime = Instant.now();
		
		this.player = new Player(
			new Vec2D(screenWidth / 2, screenHeight - 100),
			resources.getImage("player-idle.png"),
			resources.getImage("player-moving-1.png"),
			resources.getImage("player-moving-2.png")
		);
		this.player.position.x -= this.player.sprite.size.x / 2;
		this.player.iFrameDelay = 1f;
		this.enemies = new CopyOnWriteArrayList<>();
		this.bullets = new CopyOnWriteArrayList<>();
		
		dummyEnemy = new Enemy(
			new Vec2D(screenWidth, screenHeight),
			resources.getImage("enemy-1.png"),
			MovementPattern.newStraight(100),
			1
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
		this.status = GameStatus.RUNNING;
		this.startTime = System.nanoTime();
		
		while (gameThread != null) {
			updateDelta();
			update();
			repaint();
		}
	}

	private void handlePlayerProcess() {
		
		if (input.isActionPressed("moveLeft")) {
			player.direction.x = -1;
		} else if (input.isActionPressed("moveRight")) {
			player.direction.x = 1;
		} else {
			player.direction.x = 0;
		}
		
		player._process(delta);
		
		// Se fora, coloca pra dentro
		player.position.x = Math.max(0, Math.min(
			player.position.x, screenWidth - player.sprite.size.x)
		);
		
		if (input.isActionPressed("shoot") && player.canShoot()) {
			Vec2D bulletPos = new Vec2D(
				player.sprite.getCenter().x - dummyBullet.sprite.size.x / 2,
				player.position.y - dummyBullet.sprite.size.y + 15
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
		pos.x = random.nextInt(screenWidth - (int)dummyEnemy.sprite.size.x);
		pos.y = -(int)dummyEnemy.sprite.size.y;
		String imgName = "enemy-" + (random.nextInt(6) + 1) + ".png";
		boolean canSpawn = true;

		MovementPattern pattern = MovementPattern.newStraight(100);
		float movementType = random.nextFloat();
		if (movementType <= 0.7f) {
			pattern = MovementPattern.newStraight(100);
		} else {
			pattern = MovementPattern.newWave(50, 1f, 3f);
		}
		Enemy enemy = new Enemy(pos, resources.getImage(imgName), pattern, 1);

		for (Enemy enemyListed : enemies) {
			if (enemy.collidesWith(enemyListed)) {
				canSpawn = false;
				break;
			}
		}
		
		enemy.iFrameDelay = 0.5f;
	
		if (canSpawn) { enemies.add(enemy); }
		else { spawnEnemy(); }
	}
	
	public void update() {
		if (status != GameStatus.RUNNING) {
			return;
		}
		
		graphics.updateBackground(delta);
		handlePlayerProcess();
		
		// Remove os que sairam da tela
		bullets.removeIf(bullet -> {
			bullet._process(delta);
			return bullet.position.y < 0; 
		});
		enemies.removeIf(enemy -> {
			enemy._process(delta);
			return enemy.position.y > screenHeight;
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
				if (bullet.collidesWith(enemy)) {
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
			if (player.collidesWith(enemy)) {
				if (player.takeDamage(1)) {
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

		// Desenhar fundo
		graphics.drawBackground(g2);

		// Desenhar jogo
		g2.setColor(Color.WHITE);

		graphics.drawObjects(g2, new ArrayList(bullets));
		graphics.drawObjects(g2, new ArrayList(enemies));
		graphics.drawObject(g2, player);
		
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 32));
		String scoreText = Integer.toString(score);
		String bestr = Integer.toString(best);
		String bestText = "BEST:"+bestr;
		int textWidth = g2.getFontMetrics().stringWidth(scoreText);
		g2.drawString(scoreText, (screenWidth - textWidth) / 2, 40);
		int bestWidth = g2.getFontMetrics().stringWidth(bestText);
		g2.drawString(bestText, (screenWidth-bestWidth-20),40);
		g2.setFont(new Font("Arial", Font.BOLD, 12));
		String waveText = getTime() + ("WAVE " + Integer.toString(wave));
		int waveTextHeight = g2.getFontMetrics().getHeight();
		g2.drawString(waveText, (screenWidth - textWidth) / 2, 40 + waveTextHeight);

		graphics.drawWeapons(g2, screenVec, player, resources);
		graphics.drawLifes(g2, player, resources);

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
		player.position.x = screenWidth / 2 - 25;
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
					case GameStatus.PAUSED -> option = graphics.pausedOptions[menuSelectedOptionIndex];
					case GameStatus.GAME_OVER -> option = graphics.gameOverOptions[menuSelectedOptionIndex];
				}
				switch (option) {
					case GraphicsManager.MenuOption.RESUME -> { status = GameStatus.RUNNING; totalPauseTime += System.nanoTime() - pauseStartedTime; pauseStartedTime = 0; }
					case GraphicsManager.MenuOption.RESTART -> resetGame();
					case GraphicsManager.MenuOption.QUIT -> System.exit(0);
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
		if (status != GameStatus.RUNNING) return waveTimeText;

		long secondsTotal = (totalPauseTime > 0) ? ((System.nanoTime() -  startTime) - totalPauseTime) / 1_000_000_000 : (System.nanoTime() - startTime) / 1_000_000_000;
		long minutes = (secondsTotal / 60) % 60;
		long seconds = secondsTotal % 60;

		waveTimeText = String.format("%02d:%02d", minutes, seconds) + " ";

		return waveTimeText;
	}

}
