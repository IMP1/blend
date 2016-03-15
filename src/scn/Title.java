package scn;

import java.awt.event.KeyEvent;

public class Title extends Scene implements jog.Network.ClientEventHandler {
	
	private static jog.Image background = new jog.Image("gfx/titlebackground.png");
	private gui.WindowTitle window;

	public Title() {
		super();
	}

	@Override
	public void start() {
		window = new gui.WindowTitle(32, 32, 128, 128, 128, 64, 256, 128);
	}

	@Override
	public void update(double dt) {
		window.update();
	}

	@Override
	public void draw() {
		jog.Graphics.draw(background, 0, 0);
		window.draw();
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseKey) {
		window.mousePressed(mouseX, mouseY, mouseKey);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseKey) {
		window.mouseReleased(mouseX, mouseY, mouseKey);
	}

	@Override
	public void keyReleased(int key) {
		if (key == KeyEvent.VK_1){
			SceneManager.changeScene(new scn.Setup());
		} else if (key == KeyEvent.VK_2) {
			scn.Game nextScene = new scn.Game();
			nextScene.setClient(jog.Network.newClient("", 1337, nextScene));
			SceneManager.changeScene(nextScene);
		}
	}

	@Override
	public void onMessage(String message) {
		
	}

}
