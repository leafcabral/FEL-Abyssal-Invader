
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
	public final Vec2D screenSize;
	public final Vec2D screenCenter;
	
	public GraphicsManager(Vec2D screenSize) {
		this.screenSize = screenSize;
		this.screenCenter = screenSize.multiply(0.5f);
	}
	
	public void drawBackground(
			Graphics2D g2,
			BufferedImage img, Color fallbackColor
	) {
		if (img != null) {
			g2.drawImage(
				img, 0, 0,
				(int)screenSize.x, (int)screenSize.y, null
			);
		} else {
			g2.setColor(fallbackColor);
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
	public void drawObjects(
			Graphics2D g2, 
			ArrayList<GameObject> objs
	) {
		for (GameObject obj : objs) {
			obj.draw(g2);
		}
	}
	
	public void drawGenericMenu(
			Graphics2D g2, Vec2D screenSize,
			String title) {
		g2.setColor(new Color(0, 0, 0, 180));
		g2.fillRect(0, 0, (int)screenSize.x, (int)screenSize.y);
		
		Vec2D menuSize = new Vec2D(200, 200);
		Vec2D menuPos = new Vec2D(
			screenSize.x / 2 - menuSize.x / 2,
			screenSize.y / 2 - menuSize.y / 2
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
	}
	
	public static void drawPauseMenu(Graphics2D g2) {
		
	}
}
