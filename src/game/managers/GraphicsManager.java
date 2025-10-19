package game.managers;

import game.utils.Vec2D;
import game.entities.*;
import game.entities.Player.WeaponType;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/* Responsável por:
- Dsenhar os objetos
- Desenhar os Menus
- Desenhar a HUD
- Desenhar linhas CRT
*/
public class GraphicsManager {
	public enum MenuOption {
		RESUME,
		RESTART,
		QUIT,
                START
	}

	private ResourceManager resources;
	
	public final Vec2D screenSize;
	public final Vec2D screenCenter;

	private final BufferedImage backgroundImage;
	private final Vec2D bgTrueSize;
	private final Color backgroundColor;
	private float backgroundOffset = 0;
	private final float backgroundSpeed = 100;

	public MenuOption pausedOptions[] = {
		MenuOption.RESUME, MenuOption.QUIT
	};
	public MenuOption gameOverOptions[] = {
		MenuOption.RESTART, MenuOption.QUIT
	};
	public MenuOption mainMenuOptions[] = {
		MenuOption.START, MenuOption.QUIT
	};

	private class Ray {
		Vec2D head = new Vec2D();
		float length;
		float width;
		float opacity;
		float currentOpacity = 0;
		float speed;
		boolean isFadingIn = true;
		 // 45º para baixo
		private final static float ANGLE = (float)Math.PI * 0.25f;

		private int section = -1;
		
		Ray() { randomize(); }

		void randomize() {
			
			this.head.x = screenSize.x + 10;
			this.head.y = screenSize.y/4 - random.nextFloat(1.5f*screenSize.y);
			this.length = screenSize.x * 2;
			
			this.width = 50 + random.nextFloat(50);
			this.opacity = 0.02f + random.nextFloat(0.07f);
			this.speed = 50;
			
			this.currentOpacity = 0;
			this.isFadingIn = true;
		}

		void update(double delta) {
			head.y += speed * delta;
			
			if (isFadingIn) {
				currentOpacity += delta*0.05f;
				if (currentOpacity >= opacity) {
					isFadingIn = false;
				}
			} else if (head.y > screenSize.y*0.7f) {
				currentOpacity -= delta * 0.05f;
				if (currentOpacity <= 0) {
					randomize();
				}
			}
		}
		
		void draw(Graphics2D g2) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.currentOpacity));
			g2.setColor(Color.WHITE);
			
			float tailX = head.x - length * (float)Math.cos(ANGLE);
			float tailY = head.y + length * (float)Math.sin(ANGLE);
			
			g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.drawLine(
				(int)head.x, (int)head.y,
				(int)tailX, (int)tailY
			);
			
		}
	}
	private final ArrayList<Ray> rays = new ArrayList<>();
	private final Random random = new Random();
	
	
	public GraphicsManager(
			Vec2D screenSize,
			BufferedImage backgroundImage, Color backgroundColor,
			ResourceManager resources
	) {
		this.screenSize = screenSize;
		this.screenCenter = screenSize.multiply(0.5f);
		this.backgroundImage = backgroundImage;
		this.backgroundColor = backgroundColor;

		if (backgroundImage != null) {
			float bgHeight = backgroundImage.getHeight();
			float bgWidth = backgroundImage.getWidth();
			
			float aspectRatio = bgHeight / bgWidth;
			
			this.bgTrueSize = new Vec2D(
				screenSize.x,
				(float)screenSize.x * aspectRatio
			);
		} else {
			this.bgTrueSize = new Vec2D(screenSize);
		}
		this.resources = resources;
		
		for (int i = 0; i < 6; i++) {
			rays.add(new Ray());
		}
	}

	public void updateBackground(double delta) {
		backgroundOffset += backgroundSpeed * delta;

		if (backgroundOffset >= bgTrueSize.y) {
			backgroundOffset = 0;
		}
		
		for (Ray ray : rays) {
			ray.update(delta);
		}
	}

	public void drawBackground(Graphics2D g2) {
		if (backgroundImage == null) {
			g2.setColor(backgroundColor);
			g2.fillRect(0, 0, (int)screenSize.x, (int)screenSize.y);
			return;
		}

		g2.drawImage(
			backgroundImage,
			0, (int) backgroundOffset,
			(int)bgTrueSize.x, (int)bgTrueSize.y,
			null
		);
		if (backgroundOffset > 0) {
			g2.drawImage(
				backgroundImage,
				0, (int)backgroundOffset - (int)bgTrueSize.y,
				(int)bgTrueSize.x, (int)bgTrueSize.y,
				null
			);
		}
	}
	
	public void drawRays(Graphics2D g2) {
		for (Ray ray : rays) {
			ray.draw(g2);
		}
		g2.setStroke(new BasicStroke(1));
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}

	public void drawObject(Graphics2D g2, GameObject obj) {
		obj.draw(g2);
	}
	public void drawObjects(Graphics2D g2, GameObject objs[]) {
		for (GameObject obj : objs) {
			obj.draw(g2);
		}
	}
	public void drawObjects(Graphics2D g2, ArrayList<GameObject> objs) {
		for (GameObject obj : objs) {
			obj.draw(g2);
		}
	}

	public void drawWeapons(Graphics2D g2, Vec2D screenSize,
			Player player, ResourceManager resources) {
		int width = 40;
		int height = width;
		int spacing = 20;
		int margin = 20;

		int totalHeight = (height + spacing) * 3 - spacing;
		Vec2D currentPos = new Vec2D(
			screenSize.x - width - margin,
			screenSize.y - totalHeight - margin
		);

		g2.setColor(Color.WHITE);
		AlphaComposite cooldownAC = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 0.3f
		);
		AlphaComposite defaultAC = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 1.0f
		);

		String iconNames[] = {
			"bullet-default-icon.png",
			"bullet-shotgun-icon.png",
			"bullet-blast-icon.png",
		};
		for (int i = 0; i < 3; i++) {
			if (player.getCurrentWeapon().ordinal() == i) {
				g2.drawRect(
					(int)currentPos.x, (int)currentPos.y,
					width, height
				);
			}

			g2.drawImage(
				resources.getImage(iconNames[i]),
				(int)currentPos.x, (int)currentPos.y,
				width, height,
				null
			);

			g2.setComposite(cooldownAC);
			float scale = player.getWeaponCooldownProgress(WeaponType.values()[i]);
			int actualY = (int)currentPos.y + height - (int)(height*scale);
			g2.fillRect(
				(int)currentPos.x, actualY,
				width, (int)(height*scale)
			);

			currentPos.y += height + spacing;
			g2.setComposite(defaultAC);
		}
	}

	public void drawLifes(Graphics2D g2, Player player, ResourceManager resources) {
		BufferedImage full = resources.getImage("heart-full.png");
		BufferedImage empty = resources.getImage("heart-empty.png");
		
		int posx = 10;
		int posy = 15;
		int sizex = 45;
		int sizey = sizex;
		int incrementx = sizex+5;
		
		AlphaComposite emptyAC = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 0.5f
		);
		AlphaComposite fullAC = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 1.0f
		);
		
		for (int i = 0; i < player.getCurrentLife(); i++) {
			g2.drawImage(full, posx, posy, sizex, sizey, null);
			posx += incrementx;
		}
		g2.setComposite(emptyAC);
		for (int i = player.getCurrentLife(); i < player.getMaxLife(); i++) {
			g2.drawImage(empty, posx, posy, sizex, sizey, null);
			posx += incrementx;
		}
		
		
		g2.setComposite(fullAC);
	}

	public void drawGenericMenu(
			Graphics2D g2, String title,
			MenuOption options[], int selectedIndex
	) {
		g2.setColor(new Color(0, 0, 0, 180));
		g2.fillRect(0, 0, (int)screenSize.x, (int)screenSize.y);

		// Altura é a mesma
		Vec2D optionSize = new Vec2D(0, 22);
                
		Vec2D menuSize = new Vec2D(
			200, options.length * (optionSize.y+10) + 60
		);
		Vec2D menuPos = new Vec2D(
			screenCenter.x - menuSize.x / 2,
			screenCenter.y - menuSize.y / 2
		);


		g2.setColor(Color.WHITE);
		g2.setFont(resources.getFont("photonico.ttf", Font.BOLD, 32));
		g2.setFont(resources.getFont("steamwreck.ttf", Font.PLAIN, 64));
		int titleWidth = g2.getFontMetrics().stringWidth(title);
		g2.drawString(title, (screenSize.x - titleWidth) / 2, menuPos.y + 25);

		g2.setFont(resources.getFont("photonico.ttf", Font.PLAIN, 20));
		for (int i = 0; i < options.length; i++) {
			MenuOption option = options[i];

			String optionName = option.name();
			if (i == selectedIndex) {
				g2.setColor(Color.WHITE);
				optionName = "> " + optionName + " <";
			} else {
				g2.setColor(Color.LIGHT_GRAY);
			}
			
			optionSize.x = g2.getFontMetrics().stringWidth(
				optionName //option.name()
			);
			Vec2D optionPos = new Vec2D(
				screenCenter.x - optionSize.x/2,
				screenCenter.y + 25 + i*(optionSize.y + 25)
			);


			g2.drawString(optionName, optionPos.x, optionPos.y);
		}
	}

	public void drawPauseMenu(Graphics2D g2, int selectedIndex) {
		drawGenericMenu(g2, "Game Paused", pausedOptions, selectedIndex);
	}

	public void drawGameOverMenu(Graphics2D g2, int selectedIndex) {
		drawGenericMenu(g2, "Game Over", gameOverOptions, selectedIndex);
	}
        
	public void drawMainMenu(Graphics2D g2, int selectedIndex) {
		MenuOption options[] = mainMenuOptions;

		g2.setColor(Color.WHITE);

		Vec2D optionSize = new Vec2D(0, 22);
                
		Vec2D menuSize = new Vec2D(
			screenSize.x, screenSize.y
		);
		Vec2D menuPos = new Vec2D(
			screenCenter.x - menuSize.x / 2,
			screenCenter.y - menuSize.y / 2
		);

		g2.setColor(new Color(184, 134, 11));
		g2.setFont(resources.getFont("steamwreck.ttf", Font.ITALIC, 100));
		int titleWidth = g2.getFontMetrics().stringWidth("FEL");
		g2.drawString("FEL", (screenSize.x - titleWidth) / 2, menuPos.y + 200);

		g2.setColor(Color.WHITE);
		g2.setFont(resources.getFont("steamwreck.ttf", Font.PLAIN, 60));
		titleWidth = g2.getFontMetrics().stringWidth("The Space Invader");
		g2.drawString("The Space Invader", (screenSize.x - titleWidth) / 2, menuPos.y + 260);

		g2.setFont(resources.getFont("photonico.ttf", Font.PLAIN, 40));
		for (int i = 0; i < options.length; i++) {
			MenuOption option = options[i];

			String optionName = option.name();
			if (i == selectedIndex) {
				g2.setColor(Color.WHITE);
				optionName = "> " + optionName + " <";
			} else {
				g2.setColor(Color.LIGHT_GRAY);
			}
			
			optionSize.x = g2.getFontMetrics().stringWidth(
				optionName //option.name()
			);
			Vec2D optionPos = new Vec2D(
				screenCenter.x - optionSize.x/2,
				screenCenter.y + 25 + i*(optionSize.y + 25)
			);


			g2.drawString(optionName, optionPos.x, optionPos.y);
		}
	}
	
	public void displayHighScore(Graphics2D g2, int highScore) {
		g2.setColor(new Color(184, 134, 11));
		g2.setFont(resources.getFont("steamwreck.ttf", Font.ITALIC, 32));
		
		String text = "High Score: " + highScore;
		int titleWidth = g2.getFontMetrics().stringWidth(text);
		g2.drawString(text, (screenSize.x - titleWidth) / 2, 250);
		
		g2.setColor(Color.WHITE);
	}
}
