package scn;

import java.awt.Color;
import java.awt.event.KeyEvent;

public class Setup extends Scene {
	
	private jog.Network.Server server;
	private jog.Network.Client client;
	private scn.HostGame nextScene;

	@Override
	public void start() {
		nextScene = new scn.HostGame();
		server = jog.Network.newServer(1337, nextScene);
		client = jog.Network.newClient("", 1337, nextScene);
		nextScene.setServer(server);
		nextScene.setClient(client);
	}

	@Override
	public void draw() {
		jog.Graphics.printCentred("Setting up a server", jog.Window.getWidth()/2, 0);
		
		jog.Graphics.printCentred("Server IP Address:", jog.Window.getWidth()/2, 128);
		jog.Graphics.printCentred(server.getAddress(), jog.Window.getWidth()/2, 160);
		
		jog.Graphics.printCentred("Connected Clients:", jog.Window.getWidth()/2, 256);
		for (int i = 0; i < server.getClients().length; i ++) {
			jog.Graphics.printCentred(server.getClients()[i], jog.Window.getWidth()/2, 282 + (i * 24));
		}
	}

	@Override
	public void keyReleased(int key) {
		if (key == KeyEvent.VK_ESCAPE) {
			client.quit();
			server.quit();
			SceneManager.changeScene(new scn.Title());
		}
		if (key == KeyEvent.VK_SPACE && server.getClients().length > 0) {
			SceneManager.changeScene(nextScene);
			Color[] playerColours = new Color[3];
			playerColours[0] = new Color(192, 32, 32);
			playerColours[1] = new Color(32, 32, 192);
			playerColours[2] = new Color(192, 192, 32);
			int mapWidth = jog.Window.getWidth();
			int mapHeight = jog.Window.getHeight();
			nextScene.setupGame(server.getClients().length, 4, playerColours, mapWidth, mapHeight);
		}
	}

}
