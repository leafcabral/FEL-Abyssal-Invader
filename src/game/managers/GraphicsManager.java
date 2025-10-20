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

	public Color goldenColor = new Color(184, 134, 11);
	
	private class Ray {
		Vec2D head = new Vec2D();
		float width;
		float opacity;
		float currentOpacity = 0;
		float speed;
		boolean isFadingIn = true;
		
		final float length = screenSize.x * 2;
		final static float ANGLE = (float)Math.PI * 0.25f; // 45º para baixo

		private int section = -1;
		
		Ray() { randomize(); }

		void randomize() {
			this.width = 50 + random.nextFloat(100);
			this.opacity = 0.01f + random.nextFloat(0.04f);
			this.speed = 50;
			
			while (true) {
				this.head.x = screenSize.x + 20;
				this.head.y = screenSize.y/4 - random.nextFloat(1.5f*screenSize.y);
				
				boolean collidesWithOther = false;
				for (Ray other : rays) {
					float distance = Math.abs(this.head.y - other.head.y);
					float totalWidth = (this.width+other.width)/2;
					
					if (other != this && distance < totalWidth + 20) {
						collidesWithOther = true;
						break;
					}
				}
				if (!collidesWithOther) { break; }
			}
			
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
			
			g2.setStroke(new BasicStroke(width));
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
		
		for (int i = 0; i < 4; i++) {
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

	public void drawObject(Graphics2D g2, Entity obj) {
		obj.draw(g2);
	}
	public void drawObjects(Graphics2D g2, Entity objs[]) {
		for (Entity obj : objs) {
			obj.draw(g2);
		}
	}
	public void drawObjects(Graphics2D g2, ArrayList<Entity> objs) {
		for (Entity obj : objs) {
			obj.draw(g2);
		}
	}

	public void drawWeapons(Graphics2D g2, Vec2D screenSize,
			Player player, ResourceManager resources) {
		int width = 80;
		int height = 40;
		int spacing = 20;
		int margin = 20;
		int borderPadding = 2;

		int totalHeight = (height + spacing) * 3 - spacing;
		Vec2D currentPos = new Vec2D(
			screenSize.x - width - margin,
			screenSize.y - totalHeight - margin
		);

		AlphaComposite cooldownAC = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 0.3f
		);
		AlphaComposite defaultAC = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 1.0f
		);

		String iconNames[] = {
			"bullet-default.png",
			"bullet-shotgun.png",
			"bullet-blast.png",
		};
		for (int i = 0; i < 3; i++) {
			if (player.getCurrentWeapon().ordinal() == i) {
				g2.setStroke(new BasicStroke(2));
				g2.setColor(goldenColor);
				
				g2.drawRect(
					(int)currentPos.x-borderPadding, (int)currentPos.y-borderPadding,
					width+borderPadding, height+borderPadding
				);
			}
			g2.setStroke(new BasicStroke(1));
			g2.setColor(Color.WHITE);

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
		
		for (int i = 0; i < player.life; i++) {
			g2.drawImage(full, posx, posy, sizex, sizey, null);
			posx += incrementx;
		}
		g2.setComposite(emptyAC);
		for (int i = player.life; i < player.maxLife; i++) {
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
		BufferedImage title = resources.getImage("title.png");
		String subtitle = "Abyssal Invader";
		String controls[] = {
			" ↑↓ para interagir com o menu",
			"ENTER para confirmar opção",
			"A/← e D/→ para se mexer",
			"SPACE para atirar",
			"1/2/3 para alternar entre armas"
		};
		MenuOption options[] = mainMenuOptions;
		
		int titleX = (int)(screenCenter.x - title.getWidth() / 2) + 10;
		int titleY = (int)(screenCenter.y - title.getHeight() / 2) - 225;
		g2.drawImage(title, titleX, titleY, null);
			
		g2.setColor(Color.WHITE);
		g2.setFont(resources.getFont("steamwreck.ttf", Font.PLAIN, 70));
		int subtitleWidth = g2.getFontMetrics().stringWidth(subtitle);
		int subtitleHeight = g2.getFontMetrics().getHeight();
		int subtitleX = titleX + (title.getWidth() - subtitleWidth) / 2 - 10;
		int subtitleY = 2*titleY + (title.getHeight() - subtitleHeight) + 62;
		g2.drawString(subtitle, subtitleX, subtitleY);
		
		g2.setFont(resources.getFont("photonico.ttf", Font.PLAIN, 40));
		Vec2D optionSize = new Vec2D(0, 22);
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
		
		g2.setColor(Color.LIGHT_GRAY);
		g2.setFont(resources.getFont("photonico.ttf", Font.PLAIN, 16));
		int controlsInitialPosY = 650;
		for (int i = 0; i < controls.length; i++) {
			String current = controls[i];
			int width = g2.getFontMetrics().stringWidth(current);
			int posx = (int)screenCenter.x - width/2;
			int posy = controlsInitialPosY + i*32;
			
			g2.drawString(current, posx, posy);
		}
	}
	
	public void displayHighScore(Graphics2D g2, int highScore) {
		g2.setColor(goldenColor);
		g2.setFont(resources.getFont("steamwreck.ttf", Font.ITALIC, 32));
		
		String text = "High Score: " + highScore;
		int titleWidth = g2.getFontMetrics().stringWidth(text);
		g2.drawString(text, (screenSize.x - titleWidth) / 2, 300);
		
		g2.setColor(Color.WHITE);
	}
}
