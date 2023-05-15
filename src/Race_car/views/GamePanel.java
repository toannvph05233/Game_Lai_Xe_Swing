package Race_car.views;

import Race_car.models.Input;
import Race_car.models.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class GamePanel extends JPanel implements Runnable, MouseListener {

	private Thread thread;
	private Scene scene;
	public static int numberOfCollisions; // Số lượt va chạm
	private Font font;


	public GamePanel() {
		scene = new Scene(this);
		numberOfCollisions = 3; // Khởi tạo số lượt va chạm ban đầu

		font = new Font("Arial", Font.PLAIN, 18);
		addMouseListener(this);
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	private void update(float deltaTime) {
		Input.update(deltaTime);
		scene.update(deltaTime);
		repaint();
	}

	@Override
	public void run() {
		long period = 20;
		long beginTime = System.currentTimeMillis();
		long currentTime;
		float deltaTime = 0;
		while (true) {
			update(deltaTime);
			currentTime = System.currentTimeMillis();
			try {
				long sleepTime = period - currentTime + beginTime;
				if (sleepTime > 0) {
					thread.sleep(sleepTime);
				}

			} catch (InterruptedException ex) {}
			currentTime = System.currentTimeMillis();
			deltaTime = (float) (currentTime - beginTime) / 1000;
			beginTime = currentTime;
		}
	}

	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(scene.getRenderImage(), 0, 0, null);

		g2d.setFont(font);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.drawString("Collisions: " + numberOfCollisions, 10, 20);

		if (numberOfCollisions == 0) {
			g2d.setColor(Color.RED);
			Font font = new Font("Arial", Font.BOLD, 48);
			g2d.setFont(font);

			String gameOverText = "Game Over";
			FontMetrics fontMetrics = g2d.getFontMetrics();
			int textWidth = fontMetrics.stringWidth(gameOverText);
			int textHeight = fontMetrics.getHeight();

			int screenWidth = g2d.getClipBounds().width;
			int screenHeight = g2d.getClipBounds().height;

			int x = (screenWidth - textWidth) / 2;
			int y = (screenHeight - textHeight) / 2;

			g2d.drawString(gameOverText, x, y);
			g2d.setColor(Color.BLUE);
			g2d.fillRect(x+70, y+30, 100, 30); // Vẽ hình chữ nhật đại diện cho nút "Chơi lại"
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font("Arial", Font.PLAIN, 16));
			g2d.drawString("Chơi lại", 415, 360); // Vẽ văn bản trên nút

		}
	}

	private boolean isRestartButtonClicked(int mouseX, int mouseY) {
		java.awt.Rectangle buttonBounds = new Rectangle(395, 340, 100, 30);
		return buttonBounds.contains(mouseX, mouseY);
	}

	public static boolean reset = false;
	@Override
	public void mouseClicked(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		if (isRestartButtonClicked(mouseX, mouseY)) {
			numberOfCollisions = 3;
			reset = true;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}


}
