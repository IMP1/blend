package scn;

import java.awt.Color;

import cls.Person;

import run.Main;

public class HostGame extends Game implements jog.Network.ServerEventHandler {
	
	private jog.Network.Server server;
	private int clientsReady = 0;
	private int playerCount;
	private int[] playerIDs;
	private java.util.ArrayList<Person> serverPeople;

	public HostGame(Main main) {
		super(main);
	}
	
	@Override
	public void update(double dt) {
		super.update(dt);
		updateNPCs(dt);
	}
	
	private void updateNPCs(double dt) {
		
	}
	
	public void setupGame(int playerCount, int populationCount, Color[] playerColours, int mapWidth, int mapHeight) {
		//------------------------------
		// These will passed from Setup as parameters TODO
		this.playerCount = playerCount;
		//------------------------------
		server.broadcast("There are: " + playerCount * populationCount);
		serverPeople = new java.util.ArrayList<Person>();
		for (int playerType = 0; playerType < playerCount; playerType ++) {
			for (int i = 0; i < populationCount; i ++) {
				int id = playerType * populationCount + i;
				Color c = playerColours[playerType];
				int pw = Person.IMAGE.getWidth();
				int ph = Person.IMAGE.getHeight();
				int x = (int)(Math.random() * (mapWidth - pw * 2)) + pw;
				int y = (int)(Math.random() * (mapHeight - ph * 4)) + ph;
				serverPeople.add(new Person(id, x, y, c));
				server.broadcast("" + id + " is: (" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")");
				server.broadcast("" + id + " is at: (" + x + ", " + y + ")");
			}
		}
		playerIDs = new int[playerCount];
		for (int i = 0; i < playerCount; i ++ ) {
			int id = i * populationCount + (int)(Math.random() * populationCount);
			server.send(server.getClients()[i], "You are: " + id);
			playerIDs[i] = id;
		}
	}
	
	public void setServer(jog.Network.Server s) {
		server = s;
	}

	@Override
	public void onMessage(String sender, String message) {
		if (message.equals("ready!")) {
			clientsReady += 1;
			System.out.println("\t<server> " + clientsReady + " clients ready.");
			if (clientsReady == playerCount) {
				server.broadcast("begin");
			}
		}
	}

	@Override
	public void onConnect(String address) {
		
	}

	@Override
	public void onDisconnect(String address) {
		
	}

}
