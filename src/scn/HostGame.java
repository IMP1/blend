package scn;

import java.awt.Color;

import cls.Person;

public class HostGame extends Game implements jog.Network.ServerEventHandler {
	
	public final static double MESSAGE_RATE = 0.01;
	
	private jog.Network.Server server;
	private int clientsReady = 0;
	private int playerCount;
	private int[] playerIDs;
	private java.util.HashMap<String, Person> clientPersons;
	private java.util.ArrayList<Person> serverPeople;
	private boolean moved = false;
	private double messageTimer = 0;

	public HostGame() {
		super();
	}
	
	@Override
	public void update(double dt) {
		super.update(dt);
		updateNPCs(dt);
		messageTimer += dt;
		if (messageTimer >= MESSAGE_RATE) {
			messageTimer -= MESSAGE_RATE;
			if (moved) {
				broadcastPositions();
			}
		}
	}
	
	private void updateNPCs(double dt) {
		// moved = true;
	}
	
	private void broadcastPositions() {
		for (Person p : serverPeople) {
			server.broadcast("" + p.id + " is at: (" + p.getX() + ", " + p.getY() + ")");
		}
		moved = false;
	}
	
	public void setupGame(int playerCount, int populationCount, Color[] playerColours, int mapWidth, int mapHeight) {
		this.playerCount = playerCount;
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
//				System.out.printf("%d: @(%d, %d) (%d, %d, %d)\n", id, x, y, c.getRed(), c.getGreen(), c.getBlue());
			}
		}
		clientPersons = new java.util.HashMap<String, Person>();
		playerIDs = new int[playerCount];
		for (int i = 0; i < playerCount; i ++ ) {
			int id = i * populationCount + (int)(Math.random() * populationCount);
			server.send(server.getClients()[i], "You are: " + id);
			playerIDs[i] = id;
			clientPersons.put(server.getClients()[i], serverPeople.get(id));
		}
	}
	
	public void setServer(jog.Network.Server s) {
		server = s;
	}

	@Override
	public void onMessage(String sender, String message) {
		if (message.equals(READY_MESSAGE)) {
			clientsReady += 1;
//			System.out.println("\t<server> " + clientsReady + " clients ready.");
			if (clientsReady == playerCount) {
				server.broadcast(BEGIN_MESSAGE);
			}
		} else if (started) {
			if (message.matches(MOVE_PATTERN)) {
				String x = message.split(", ")[0];
				x = x.substring(1);
				String y = message.split(", ")[1];
				y = y.substring(0, y.length()-2);
				double dx = Double.parseDouble(x);
				double dy = Double.parseDouble(y);
				clientPersons.get(sender).move(dx, dy);
				moved = true;
			}
			if (message.matches(UNTARGET_PATTERN)) {
				String id = message.substring(1, message.length() - 1);
				int i = getClientID(Integer.parseInt(id));
				if (i > -1) {
					System.out.println("untargetting player " + i + " " + server.getClients()[i]);
					server.send(server.getClients()[i], DECREMENT_TARGETS_MESSAGE);
				}
			}
			if (message.matches(TARGET_PATTERN)) {
				String id = message.substring(1, message.length() - 1);
				int i = getClientID(Integer.parseInt(id));
				if (i > -1) {
					System.out.println("targetting player " + i + " " + server.getClients()[i]);
					server.send(server.getClients()[i], INCREMENT_TARGETS_MESSAGE);
				}
			}
			if (message.equals(ATTACK_MESSAGE)) {
				System.out.println("pew pew");
			}
		}
	}
	
	private int getClientID(int playerID) {
		for (int i = 0; i < playerIDs.length; i ++) {
			if (playerIDs[i] == playerID) return i;
		}
		return -1;
	}

	@Override
	public void onConnect(String address) {
		
	}

	@Override
	public void onDisconnect(String address) {
		
	}

}

