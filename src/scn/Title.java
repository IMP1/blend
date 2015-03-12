package scn;

import java.awt.event.KeyEvent;

import run.Main;

public class Title extends Scene implements jog.Network.ClientEventHandler {
	
	private static jog.Image background = new jog.Image("gfx/titlebackground.png");

	private jog.Network.Client client;
	
	public Title(Main main) {
		super(main);
	}

	@Override
	public void start() {
		
	}

	@Override
	public void update(double dt) {
		
	}

	@Override
	public void draw() {
		jog.Graphics.draw(background, 0, 0);
	}

	@Override
	public void close() {
		
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseKey) {}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseKey) {}

	@Override
	public void keyPressed(int key) {}

	@Override
	public void keyReleased(int key) {
		if (key == KeyEvent.VK_1){
			main.setScene(new Setup(main));
		} else if (key == KeyEvent.VK_2) {
			scn.Game nextScene = new scn.Game(main);
			client = jog.Network.newClient("", 1337, nextScene);
			nextScene.setClient(client);
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
