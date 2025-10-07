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
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO; // Importa para carregar imagens.

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private final int screenWidth = 800;
    private final int screenHeight = 600;
    private final int fps = 60;
    
    private Thread gameThread;
    private Player player;
    private ArrayList<Bullet> bullets;
    public ArrayList<Enemy> enemies;
    // private ArrayList<Pair<int,int>> explosion;;
    private SoundManager soundManager;
    private Random random;

    private int score;
    private boolean gameOver, paused;

    private boolean isLeftPressed, isRightPressed, isUpPressed, isDownPressed;
    private boolean isShooting;
    private long lastShotTime;
    private final long shootDelay = 200;

    // Adiciona a variável para a imagem de fundo.
    private BufferedImage backgroundImage;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK); // O background inicial pode ser preto, mas será coberto pela imagem.
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(this);

        this.player = new Player(screenWidth / 2 - 25, screenHeight - 100, 50, 50, 5);
        this.bullets = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.soundManager = new SoundManager();
        this.random = new Random();

        this.score = 0;
        this.gameOver = false;

        // Tenta carregar a imagem de fundo.
        try {
            this.backgroundImage = ImageIO.read(new File("res/background.jpg"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem de fundo: res/background.jpg");
            e.printStackTrace();
            // Se a imagem não carregar, 'backgroundImage' será null e o fundo ficará preto.
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / fps;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1000000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (gameOver) {
            return;
        }
        if (paused) {
            return;
        }

        if (isLeftPressed) player.x -= player.speed;
        if (isRightPressed) player.x += player.speed;
        if (isUpPressed) player.y -= player.speed;
        if (isDownPressed) player.y += player.speed;

        if (player.x < 0) player.x = 0;
        if (player.x > screenWidth - player.width) player.x = screenWidth - player.width;
        if (player.y < 0) player.y = 0;
        if (player.y > screenHeight - player.height) player.y = screenHeight - player.height;
        
        long currentTime = System.currentTimeMillis();
        if (isShooting && currentTime - lastShotTime > shootDelay) {
            bullets.add(new Bullet(player.x + player.width / 2 - 5, player.y, 10, 10, 7));
            soundManager.playSound("shot.wav");
            lastShotTime = currentTime;
        }

        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet b = bulletIterator.next();
            b.update();
            if (b.y < 0) {
                bulletIterator.remove();
            }
        }

        if (random.nextInt(100) < 2) { 
            enemies.add(new Enemy(random.nextInt(screenWidth - 50), -50, 50, 50, 3));
        }

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy e = enemyIterator.next();
            e.update();
            if (e.y > screenHeight) {
                enemyIterator.remove();
            }
        }
        
        checkCollisions();
    }

    private void checkCollisions() {
        Rectangle playerRect = new Rectangle(player.x, player.y, player.width, player.height);

        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            Rectangle bulletRect = new Rectangle(bullet.x, bullet.y, bullet.width, bullet.height);

            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                Rectangle enemyRect = new Rectangle(enemy.x, enemy.y, enemy.width, enemy.height);

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
            Rectangle enemyRect = new Rectangle(enemy.x, enemy.y, enemy.width, enemy.height);
            if (playerRect.intersects(enemyRect)) {
                soundManager.playSound("explosion.wav");
                System.out.println("Fim de Jogo!");
                gameOver = true;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

       // Desenhar fundo
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, screenWidth, screenHeight, null);
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
        if (paused) {
            int menuWidth = 200;
            int menuHeight = 200;
            g2.fillRect(screenWidth/2 - menuWidth/2, screenHeight/2 - menuHeight/2, menuWidth, menuHeight);
        } else if (gameOver) {
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
        player.x = screenWidth / 2 - 25;
        bullets.clear();
        enemies.clear();
        gameOver = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_ESCAPE) {
            if (!gameOver) this.paused = !this.paused;
        }
        if (code == KeyEvent.VK_Q) {
            if (gameOver || paused) System.exit(0);
        }

        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            isLeftPressed = true;
        }
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            isRightPressed = true;
        }
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            isUpPressed = true;
        }
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            isDownPressed = true;
        }
        if (code == KeyEvent.VK_SPACE) {
            isShooting = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            isLeftPressed = false;
        }
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            isRightPressed = false;
        }
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            isUpPressed = false;
        }
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            isDownPressed = false;
        }
        if (code == KeyEvent.VK_SPACE) {
            isShooting = false;
        }

        if (gameOver && code == KeyEvent.VK_ENTER) {
            resetGame();
        }
    }
}