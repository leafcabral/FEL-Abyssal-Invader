
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
	
	public void drawGenericMenu(
			Graphics2D g2, String title,
			MenuOption options[], int selectedIndex
	) {
		g2.setColor(new Color(0, 0, 0, 180));
		g2.fillRect(0, 0, (int)screenSize.x, (int)screenSize.y);
		
		// Altura é a mesma
		Vec2D optionSize = new Vec2D(0, 35);
		
		Vec2D menuSize = new Vec2D(
			200, options.length * optionSize.y + 80
		);
		Vec2D menuPos = new Vec2D(
			screenCenter.x - menuSize.x / 2,
			screenCenter.y - menuSize.y / 2
		);
		
		g2.setColor(new Color(60, 60, 60));
		g2.fillRect(
			(int)menuPos.x, (int)menuPos.y,
			(int)menuSize.x, (int)menuSize.y
		);
		g2.setColor(Color.WHITE);
		g2.drawRect(
			(int)menuPos.x, (int)menuPos.y,
			(int)menuSize.x, (int)menuSize.y
		);
		
		g2.setFont(new Font("Arial", Font.BOLD, 20));
		int titleWidth = g2.getFontMetrics().stringWidth(title);
		g2.drawString(title, (screenSize.x - titleWidth) / 2, menuPos.y + 30);
		
		g2.setFont(new Font("Arial", Font.PLAIN, 16));
		for (int i = 0; i < options.length; i++) {
			MenuOption option = options[i];
			
			optionSize.x = g2.getFontMetrics().stringWidth(
				option.name()
			);
			Vec2D optionPos = new Vec2D(
				screenCenter.x - optionSize.x/2,
				(int)menuPos.y + i*optionSize.y + 70
			);
			
			if (i == selectedIndex) {
				g2.setColor(Color.WHITE);
				g2.drawString(">", optionSize.x - 10, optionPos.y);
			} else {
				g2.setColor(Color.LIGHT_GRAY);
			}
			
			g2.drawString(option.name(), optionPos.x, optionPos.x);
		}
	}
	
	public void drawPauseMenu(Graphics2D g2, int selectedIndex) {
		drawGenericMenu(g2, "Game Paused", new MenuOption[]{
			MenuOption.RESUME, MenuOption.QUIT
		}, selectedIndex);
	}
	
	public void drawGameOverMenu(Graphics2D g2, int selectedIndex) {
		drawGenericMenu(g2, "Game Over", new MenuOption[]{
			MenuOption.RESTART, MenuOption.QUIT
		}, selectedIndex);
	}
}
