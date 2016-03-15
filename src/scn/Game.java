package scn;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import cls.Person;

public class Game extends Scene implements jog.Network.ClientEventHandler {
	
	public final static String BEGIN_MESSAGE = "begin";
	public final static String READY_MESSAGE = "ready!";
	
	// Network Variables
	protected jog.Network.Client network; 
	protected boolean started = false;
	protected boolean ready = false;
	protected boolean initialisedPopulation = false;
	protected int identifiedPopulation = 0;
	protected int locatedPopulation = 0;
	protected boolean identifiedMe = false;
	
	// Game Variables
	protected java.util.ArrayList<Person> people;
	protected int me = -1;
	protected Person target;
	protected lib.Camera camera;

	public void setClient(jog.Network.Client c) {
		network = c;
	}
	
	@Override
	public void start() {
		people = new java.util.ArrayList<Person>();
		camera = new lib.Camera();
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
	public void mousePressed(int mouseX, int mouseY, int mouseKey) {
		if (mouseKey == MouseEvent.BUTTON1) {
			double mx = camera.getWorldX(mouseX);
			double my = camera.getWorldY(mouseY);
			System.out.printf("Button pressed at (%f, %f).\n", mx, my);
			Person p = getPersonAt(mx, my);
			if (p != null && p.id != me) {
				target = p;
				System.out.printf("Target is now %d.\n", target.id);
			}
		}
	}
	
	private Person getPersonAt(double x, double y) {
		for (Person p : people) {
			double dx = x - p.getX();
			double dy = y - p.getY() - Person.IMAGE.getHeight() / 2;
			if (Math.abs(dx) < Person.IMAGE.getWidth() / 1.5 && Math.abs(dy) < Person.IMAGE.getHeight() / 1.5) {
				return p;
			}
		}
		return null;
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
		camera.centreOn(people.get(me).getX(), people.get(me).getY());
	}

	@Override
	public void draw() {
		if (!started) {
			jog.Graphics.printCentred("Waiting for game to begin...", jog.Window.getWidth()/2, 0);
		}
		camera.set();
		int y = 0;
		for (Person p : people) {
			if (p.id == me) {
				p.drawAsMe();
			} else if (p.equals(target)) {
				p.drawAsTarget();
			} else {
				p.draw();
			}
			jog.Graphics.print("Person #" + y + " (" + p.getX() + ", " + p.getY() + ")", 0, 32 + y * 24);
			y ++;
		}
		camera.unset();
	}

}
