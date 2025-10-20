package game;

import game.entities.Bullet;
import game.entities.Enemy;
import game.entities.Enemy.EnemyType;
import game.entities.Player;
import game.managers.GraphicsManager;
import game.managers.InputManager;
import game.managers.ResourceManager;
import static game.entities.Player.WeaponType.*;

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
import java.awt.RenderingHints;
import java.util.concurrent.CopyOnWriteArrayList;
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
	
	private double delta = 0;
	
	private GameStatus status;
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
	private final CopyOnWriteArrayList<Bullet> playerBullets;
	private final CopyOnWriteArrayList<Bullet> enemiesBullets;
	
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
			resources.getImage("background-deep-ocean.png"), Color.BLACK,
			resources
		);
		this.input = new InputManager();
		
		this.random = new Random();
		
		this.status = GameStatus.MAIN_MENU;
		this.startTime = System.nanoTime();
		
		this.player = new Player(
			new Vec2D(screenWidth / 2, screenHeight - 100),
			resources.getImage("player-idle-2.png")
		);
		this.player.moveX(-this.player.spriteShape.width / 2);
		this.player.spriteDirection = new Vec2D(0, -1);
		this.player.collisionShape.grow(
			- this.player.collisionShape.width / 3,
			- this.player.collisionShape.height / 3
		);
		
		this.enemies = new CopyOnWriteArrayList<>();
		this.playerBullets = new CopyOnWriteArrayList<>();
		this.enemiesBullets = new CopyOnWriteArrayList<>();
		
		dummyEnemy = new Enemy(
			new Vec2D(screenWidth, screenHeight),
			resources.getImage("enemy-1.png"),
			EnemyType.STRAIGHT_NONE,
			graphics.screenSize
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
	
	@Override
	public void run() {
		final double TARGET_FPS = 60;
		final double INTERVAL = 1e9 / TARGET_FPS;
		long lastFrameTime = System.nanoTime();
		
		while (gameThread != null) {
			long now = System.nanoTime();
			this.delta = (now - lastFrameTime) / 1e9;
			lastFrameTime = now;
			
			update();
			repaint();
			long sleepTime = newSleepTime(lastFrameTime, INTERVAL);
			
			if (sleepTime > 0) {
				try { Thread.sleep(sleepTime); }
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	private long newSleepTime(long lastFrameTime, double INTERVAL) {
		double temp = lastFrameTime - System.nanoTime() + INTERVAL;
		return (long)(temp / 1e6);
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
		
		// Se apagar essa linha o programa não funciona System.out.println(2);
		
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
				case DEFAULT -> 
					playerBullets.add(Bullet.newDefaultBullet(
						bulletPos,
						resources.getImage("bullet-default.png")
					));
				case SHOTGUN ->
					Collections.addAll(
						playerBullets,
						Bullet.newShotgunBullets(
							bulletPos,
							resources.getImage("bullet-shotgun.png")
						)
					);
				case BLAST ->
					playerBullets.add(Bullet.newBlastBullet(
						bulletPos,
						resources.getImage("bullet-blast.png")
					));
					
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

		EnemyType enemyType = getEnemyTypeForWave();
		Enemy enemy = new Enemy(pos, resources.getImage(imgName), enemyType, graphics.screenSize);
		enemy.speed = 200 + wave * 5;

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
	private Enemy.EnemyType getEnemyTypeForWave() {
		float rand = random.nextFloat();

		if (wave >= 7 && rand < 0.2f) { return EnemyType.SIDE_SHOOT; }
		else if (wave >= 5 && rand < 0.3f) { return EnemyType.STRAIGHT_SHOOT; }
		else if (wave >= 3 && rand < 0.4f) { return EnemyType.WAVE_NONE; }
		else { return EnemyType.STRAIGHT_NONE; }
	}
	
	public void update() {
		if (status == GameStatus.MAIN_MENU) {
			graphics.updateBackground(delta);
		}
		if (status != GameStatus.RUNNING) {
			return;
		}
		
		resources.updateGifs(delta);
		graphics.updateBackground(delta);
		
		handlePlayerInput();
		
		for (Enemy enemy : enemies) {
			ArrayList<Bullet> enemyBullets = enemy.attackPattern.attack(
				enemy, resources.getImage("bullet-shotgun.png"), delta
			);
			enemiesBullets.addAll(enemyBullets);
		}
		    
		// Remove os que sairam da tela
		playerBullets.removeIf(bullet -> {
			bullet.update(delta);
			return bullet.bottom() < 0; 
		});
		enemiesBullets.removeIf(bullet -> {
			bullet.update(delta);
			return bullet.top() > screenHeight; 
		});
		enemies.removeIf(enemy -> {
			enemy.update(delta);
			return enemy.top() > screenHeight;
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
		ArrayList<Bullet> playerBulletsToRemove = new ArrayList<>();
		ArrayList<Bullet> enemiesBulletsToRemove = new ArrayList<>();
		ArrayList<Enemy> enemiesToRemove = new ArrayList<>();
		
		for (Bullet bullet : playerBullets) { for (Enemy enemy : enemies) {
			if (enemy.collidesWith(bullet) && !enemy.isInvincible()) {
				if (enemy.takeDamage(bullet.damage)) {
					enemiesToRemove.add(enemy);
				}
				resources.playSound("explosion.wav");
				resources.startExplosion(enemy.getCenter());

				score += 10;
				if(best <= score){ best = score; }
				
				if (++bullet.enemiesPierced >= bullet.life) {
					playerBulletsToRemove.add(bullet);
					break;
				}
			}
		}}

		for (Enemy enemy : enemies) {
			if (player.collidesWith(enemy)) {
				if (!player.isInvincible()) {
					resources.startExplosion(player.getCenter());
					if (player.takeDamage(1)) {
						status = GameStatus.GAME_OVER;
					}
					resources.playSound("explosion.wav");
				}
			}
		}
		for (Bullet bullet : enemiesBullets) {
			if (player.collidesWith(bullet)) {
				if (!player.isInvincible()) {
					resources.startExplosion(player.getCenter());
					if (player.takeDamage(1)) {
						status = GameStatus.GAME_OVER;
					}
					resources.playSound("explosion.wav");
					enemiesBulletsToRemove.add(bullet);
				}
			}
		}

		playerBullets.removeAll(playerBulletsToRemove);
		enemiesBullets.removeAll(enemiesBulletsToRemove);
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
		
		// Desenhar objetos e parte da HUD
		graphics.drawObjects(g2, new ArrayList(playerBullets));
		graphics.drawObjects(g2, new ArrayList(enemiesBullets));
		graphics.drawObjects(g2, new ArrayList(enemies));
		graphics.drawObject(g2, player);
		if (status == GameStatus.RUNNING) { resources.drawGifs(g2); }
		graphics.drawRays(g2);
		graphics.drawWeapons(g2, screenVec, player, resources);
		graphics.drawLifes(g2, player, resources);
		
		// Desenha pontuação
		g2.setColor(Color.WHITE);
		g2.setFont(resources.getFont("photonico.ttf", Font.PLAIN, 32));
		String scoreText = Integer.toString(score);
		int textWidth = g2.getFontMetrics().stringWidth(scoreText);
		g2.drawString(scoreText, (screenWidth - textWidth) / 2, 40);
		
		// Desenha tempo de jogo e wave
		g2.setColor(Color.LIGHT_GRAY);
		g2.setFont(resources.getFont("photonico.ttf", Font.BOLD, 16));
		String waveText = getTime() + (" • WAVE " + Integer.toString(wave));
		int waveTextHeight = g2.getFontMetrics().getHeight();
		int waveTextWidth = g2.getFontMetrics().stringWidth(waveText);
		g2.drawString(waveText, (screenWidth - waveTextWidth) / 2, 40 + waveTextHeight);
		g2.setColor(Color.WHITE);
		
		// Menu de pause
		if (status == GameStatus.PAUSED) {
			graphics.drawPauseMenu(g2, menuSelectedOptionIndex);
			graphics.displayHighScore(g2, best);
		} else if (status == GameStatus.GAME_OVER) {
			graphics.drawGameOverMenu(g2, menuSelectedOptionIndex);
			graphics.displayHighScore(g2, best);
		}
		
		g2.dispose();
	}

	private void resetGame() {
		score = 0;
		player.collisionShape.x = screenWidth / 2 - player.collisionShape.width;
		playerBullets.clear();
		enemies.clear();
		resources.clearAllGifs();
		player.resetLife();
		player.iFrameSecondsReverse = 0;
		player.iFrameSeconds = 0;
		startTime = System.nanoTime();
		pauseStartedTime = 0;
		totalPauseTime = 0;
		waveTime = WAVE_DEFAULT_DURATION;
		wave = 0;
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
