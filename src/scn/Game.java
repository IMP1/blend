package scn;

import java.awt.Color;
import java.awt.event.KeyEvent;

import cls.Person;

public class Game extends Scene implements jog.Network.ClientEventHandler {
	
	public final static String BEGIN_MESSAGE = "begin";
	public final static String READY_MESSAGE = "ready!";
	
	protected jog.Network.Client network; 
	protected java.util.ArrayList<Person> people;
	protected int me = -1;
	protected boolean started = false;
	protected boolean ready = false;
	protected boolean initialisedPopulation = false;
	protected int identifiedPopulation = 0;
	protected int locatedPopulation = 0;
	protected boolean identifiedMe = false;

	public Game() {
		super();
	}

	public void setClient(jog.Network.Client c) {
		network = c;
	}
	
	@Override
	public void start() {
		people = new java.util.ArrayList<Person>();
	}

	@Override
	public void onMessage(String message) {
		if (!started){
			onSetupMessage(message);
		} else {
			onGameMessage(message);
		}
	}
	
	protected void onSetupMessage(String message) {
		if (!ready) {
			if (message.startsWith("There are: ")) {
				int n = Integer.parseInt(message.substring(11));
				people.clear();
				for (int i = 0; i < n; i ++) {
					people.add(new Person(i, 0, 0, new Color(128, 128, 128)));
				}
				initialisedPopulation = true;
			} else if (message.startsWith("You are: ")) {
				me = Integer.parseInt(message.substring(9));
				identifiedMe = true;
			} else if (message.matches("\\d is: .+")) {
				int id = Integer.parseInt(message.split(" ")[0]);
				String colour = message.split(": ")[1];
				colour = colour.substring(1, colour.length() - 1); // Remove parentheses.
				int r = Integer.parseInt(colour.split(", ")[0]);
				int g = Integer.parseInt(colour.split(", ")[1]);
				int b = Integer.parseInt(colour.split(", ")[2]);
				people.get(id).setColour(r, g, b);
				identifiedPopulation ++;
			} else if (message.matches("\\d is at: .+")) {
				int id = Integer.parseInt(message.split(" ")[0]);
				String pos = message.split(": ")[1];
				pos = pos.substring(1, pos.length() - 1); // Remove parentheses.
				int x = Integer.parseInt(pos.split(", ")[0]);
				int y = Integer.parseInt(pos.split(", ")[1]);
				people.get(id).setPosition(x, y);
				locatedPopulation ++;
			}
			if (initialisedPopulation && identifiedMe && identifiedPopulation == people.size() && locatedPopulation == people.size()) {
				ready = true;
				network.send(READY_MESSAGE);
			}
		}
		if (message.equals(BEGIN_MESSAGE)) {
			started = true;
		}
	}
	
	protected void onGameMessage(String message) {
		if (message.matches("\\d is at: .+")) {
			int id = Integer.parseInt(message.split(" ")[0]);
			String pos = message.split(": ")[1];
			pos = pos.substring(1, pos.length() - 1); // Remove parentheses.
			int x = Integer.parseInt(pos.split(", ")[0]);
			int y = Integer.parseInt(pos.split(", ")[1]);
			people.get(id).setPosition(x, y);
		}
	}

	@Override
	public void update(double dt) {
		if (!started) return;
		double dx = 0, dy = 0;
		if (jog.Input.isKeyDown(KeyEvent.VK_W)) {
			dy -= dt * Person.MOVE_SPEED;
		}
		if (jog.Input.isKeyDown(KeyEvent.VK_A)) {
			dx -= dt * Person.MOVE_SPEED;
		}
		if (jog.Input.isKeyDown(KeyEvent.VK_S)) {
			dy += dt * Person.MOVE_SPEED;
		}
		if (jog.Input.isKeyDown(KeyEvent.VK_D)) {
			dx += dt * Person.MOVE_SPEED;
		}
		if (dx != 0 || dy != 0) {
			network.send("<" + dx + ", " + dy + ">");
		}
		for (Person p : people) {
			p.update(dt);
		}
	}

	@Override
	public void draw() {
		if (!started) {
			jog.Graphics.printCentred("Waiting for game to begin...", jog.Window.getWidth()/2, 0);
		}
		int y = 0;
		for (Person p : people) {
			if (p.id == me) {
				p.drawMe();
			} else {
				p.draw();
			}
			jog.Graphics.print("Person #" + y + " (" + p.getX() + ", " + p.getY() + ")", 0, 32 + y * 24);
			y ++;
		}
	}

}
