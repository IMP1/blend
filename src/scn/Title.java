package scn;

import java.awt.event.KeyEvent;

import run.Main;

public class Title extends Scene implements jog.Network.ClientEventHandler {
	
	private static jog.Image background = new jog.Image("gfx/titlebackground.png");
	private gui.WindowTitle window;

	public Title(Main main) {
		super(main);
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
	public void close() {
		
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
	public void keyPressed(int key) {}

	@Override
	public void keyReleased(int key) {
		if (key == KeyEvent.VK_1){
			main.setScene(new Setup(main));
		} else if (key == KeyEvent.VK_2) {
			scn.Game nextScene = new scn.Game(main);
			nextScene.setClient(jog.Network.newClient("", 1337, nextScene));
			main.setScene(nextScene);
		}
	}

	@Override
	public void onMessage(String message) {
		
	}

	@Override
	public void focus(boolean gained) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseFocus(boolean gained) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int oldWidth, int oldHeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean quit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mouseScrolled(int x, int y, int scroll) {
		// TODO Auto-generated method stub
		
	}

}
