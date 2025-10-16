package game.managers;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Set;

public class InputManager {
	// KeyEvent.VK_tecla é um inteiro internamente
	private HashMap<Integer, Boolean> state = new HashMap<>();
	// Set<Integer> para que multiplas teclas representem a mesma ação
	private HashMap<String, Set<Integer>> keyBindings = new HashMap<>();
	
	public InputManager() {
		// Jogador
		keyBindings.put("moveLeft", Set.of(
			KeyEvent.VK_A, KeyEvent.VK_LEFT
		));
		keyBindings.put("moveRight", Set.of(
			KeyEvent.VK_D, KeyEvent.VK_RIGHT
		));
		keyBindings.put("shoot", Set.of(KeyEvent.VK_SPACE));
		
		// Mudar armas
		keyBindings.put("weapon1", Set.of(
			KeyEvent.VK_1, KeyEvent.VK_Z, KeyEvent.VK_P
		));
		keyBindings.put("weapon2", Set.of(
			KeyEvent.VK_2, KeyEvent.VK_X, KeyEvent.VK_O
		));
		keyBindings.put("weapon3", Set.of(
			KeyEvent.VK_3, KeyEvent.VK_C, KeyEvent.VK_I
		));
		
		// Menu
		keyBindings.put("menu", Set.of(
			KeyEvent.VK_ESCAPE, KeyEvent.VK_P
		));
		keyBindings.put("up", Set.of(
			KeyEvent.VK_W, KeyEvent.VK_UP
		));
		keyBindings.put("down", Set.of(
			KeyEvent.VK_S, KeyEvent.VK_DOWN
		));
		keyBindings.put("confirm", Set.of(
			KeyEvent.VK_ENTER
		));
		
		for (Set<Integer> keys : keyBindings.values()) {
			for (int key : keys) {
				state.put(key, false);
			}
		}
	}

	public void keyPressed(int keyCode) {
		state.replace(keyCode, true);
	}
	public void keyReleased(int keyCode) {
		state.replace(keyCode, false);
	}
	
	public boolean isActionPressed(String action) {
		Set<Integer> keys = keyBindings.get(action);
		if (keys != null) {
			for (int key : keys) {
				if (state.get(key)) {
					return true;
				}
			}
		}
			
		return false;
	}
}
