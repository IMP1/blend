package scn;

import java.awt.Color;
import java.awt.event.KeyEvent;

import run.Main;

public class Setup extends Scene {
	
	private jog.Network.Server server;
	private jog.Network.Client client;
	private scn.HostGame nextScene;

	public Setup(Main main) {
		super(main);
	}

	@Override
	public void start() {
		nextScene = new scn.HostGame(main);
		server = jog.Network.newServer(1337, nextScene);
		client = jog.Network.newClient("", 1337, nextScene);
		nextScene.setServer(server);
		nextScene.setClient(client);
	}

	@Override
	public void update(double dt) {
		
	}

	@Override
	public void draw() {
		jog.Graphics.printCentred("Setting up a server", 0, 0, jog.Window.getWidth());
		
		jog.Graphics.printCentred("Server IP Address:", 0, 128, jog.Window.getWidth());
		jog.Graphics.printCentred(server.getAddress(), 0, 160, jog.Window.getWidth());
		
		jog.Graphics.printCentred("Connected Clients:", 0, 256, jog.Window.getWidth());
		for (int i = 0; i < server.getClients().length; i ++) {
			jog.Graphics.printCentred(server.getClients()[i], 0, 282 + (i * 24), jog.Window.getWidth());
		}
	}

	@Override
	public void close() {
		
	}

	@Override
	public void keyPressed(int key) {}

	@Override
	public void keyReleased(int key) {
		if (key == KeyEvent.VK_ESCAPE) {
			client.quit();
			server.quit();
			main.setScene(new Title(main));
		}
		if (key == KeyEvent.VK_SPACE && server.getClients().length > 0) {
			main.setScene(nextScene);
			Color[] playerColours = new Color[3];
			playerColours[0] = new Color(192, 32, 32);
			playerColours[1] = new Color(32, 32, 192);
			playerColours[2] = new Color(192, 192, 32);
			int mapWidth = jog.Window.getWidth();
			int mapHeight = jog.Window.getHeight();
			nextScene.setupGame(server.getClients().length, 8, playerColours, mapWidth, mapHeight);
		}
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseKey) {}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseKey) {}

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
