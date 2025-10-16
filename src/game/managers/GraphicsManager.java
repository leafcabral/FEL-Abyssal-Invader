package game.managers;

import game.utils.Vec2D;
import game.entities.*;
import game.entities.Player.WeaponType;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
		QUIT
	}
	
	public final Vec2D screenSize;
	public final Vec2D screenCenter;
	
	private final BufferedImage backgroundImage;
	private final Color backgroundColor;
	
	public MenuOption pausedOptions[] = {
		MenuOption.RESUME, MenuOption.QUIT
	};
	public MenuOption gameOverOptions[] = {
		MenuOption.RESTART, MenuOption.QUIT
	};
	
	public GraphicsManager(
			Vec2D screenSize,
			BufferedImage backgroundImage, Color backgroundColor
	) {
		this.screenSize = screenSize;
		this.screenCenter = screenSize.multiply(0.5f);
		this.backgroundImage = backgroundImage;
		this.backgroundColor = backgroundColor;
	}
	
	public void drawBackground(Graphics2D g2) {
		if (backgroundImage != null) {
			g2.drawImage(
				backgroundImage, 0, 0,
				(int)screenSize.x, (int)screenSize.y, null
			);
		} else {
			g2.setColor(backgroundColor);
			g2.fillRect(0, 0, (int)screenSize.x, (int)screenSize.y);
		}
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
	
	public void drawWeapons(Graphics2D g2, Vec2D topleft,
			Player player, ResourceManager resources) {
		int width = 40;
		int height = width;
		
		Vec2D currentPos = new Vec2D(topleft);
		g2.setColor(Color.WHITE);
		AlphaComposite cooldownAC = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 0.3f
		);
		AlphaComposite defaultAC = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 1.0f
		);
	
		for (int i = 0; i < 3; i++) {
			if (player.getCurrentWeapon().ordinal() == i) {
				g2.drawRect(
					(int)currentPos.x, (int)topleft.y,
					width, height
				);
			}
			
			g2.drawImage(
				resources.getImage("bulletIcon" + (i+1)),
				(int)currentPos.x, (int)topleft.y,
				width, height,
				null
			);
			
			g2.setComposite(cooldownAC);
			float scale = player.getWeaponCooldownProgress(WeaponType.values()[i]);
			int actualY = (int)topleft.y + height - (int)(height*scale);
			g2.fillRect(
				(int)currentPos.x, actualY,
				width, (int)(height*scale)
			);
			
			currentPos.x += width + 20;
			g2.setComposite(defaultAC);
		}
	}
	
	private void drawMenuBox(Graphics2D g2, Vec2D pos, Vec2D size) {
		g2.setColor(new Color(60, 60, 60));
		g2.fillRect((int)pos.x, (int)pos.y,(int)size.x, (int)size.y);
		
		g2.setColor(Color.WHITE);
		g2.drawRect((int)pos.x, (int)pos.y,(int)size.x, (int)size.y);
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
		
		drawMenuBox(g2, menuPos, menuSize);
		
		g2.setFont(new Font("Arial", Font.BOLD, 20));
		int titleWidth = g2.getFontMetrics().stringWidth(title);
		g2.drawString(title, (screenSize.x - titleWidth) / 2, menuPos.y + 25);
		
		g2.setFont(new Font("Arial", Font.PLAIN, 16));
		for (int i = 0; i < options.length; i++) {
			MenuOption option = options[i];
			
			optionSize.x = g2.getFontMetrics().stringWidth(
				option.name()
			);
			Vec2D optionPos = new Vec2D(
				screenCenter.x - optionSize.x/2,
				(int)menuPos.y + 25 + i*(optionSize.y + 5) + 40
			);
			
			if (i == selectedIndex) {
				g2.setColor(Color.WHITE);
				g2.drawString(">", optionPos.x - 20, optionPos.y);
			} else {
				g2.setColor(Color.LIGHT_GRAY);
			}
			
			g2.drawString(option.name(), optionPos.x, optionPos.y);
		}
	}
	
	public void drawPauseMenu(Graphics2D g2, int selectedIndex) {
		drawGenericMenu(g2, "Game Paused", pausedOptions, selectedIndex);
	}
	
	public void drawGameOverMenu(Graphics2D g2, int selectedIndex) {
		drawGenericMenu(g2, "Game Over", gameOverOptions, selectedIndex);
	}
}
