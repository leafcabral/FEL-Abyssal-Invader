// Importa a biblioteca do Swing para criar a janela do jogo.
import javax.swing.JFrame;

// A classe principal que roda o jogo.
public class Main {
	public static void main(String[] args) {
		// 1. Cria a janela do jogo.
		JFrame frame = new JFrame("FEL: The Space Invader");
		
		// 2. Cria o painel principal, onde o jogo será desenhado.
		GamePanel gamePanel = new GamePanel();

		// 3. Adiciona o painel do jogo à janela.
		frame.add(gamePanel);
		
		// 4. Ajusta o tamanho da janela para o tamanho preferido do painel do jogo.
		frame.pack();
		
		// 5. Configura a janela para fechar o programa quando o botão 'X' for clicado.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// 6. Centraliza a janela na tela do computador.
		frame.setLocationRelativeTo(null);
		
		// 7. Impede que o usuário mude o tamanho da janela.
		frame.setResizable(false);
		
		// 8. Torna a janela visível.
		frame.setVisible(true);

		// 9. Inicia o "motor" do jogo, que fará a nave se mover e os inimigos aparecerem.
		gamePanel.startGameThread();
	}
}