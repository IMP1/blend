package scn;

import java.awt.Color;
import java.awt.event.KeyEvent;

import cls.Person;

import run.Main;

public class Game extends Scene implements jog.Network.ClientEventHandler {
	
	protected jog.Network.Client client; 
	protected java.util.ArrayList<Person> people;
	protected int me = -1;
	protected boolean started = false;
	protected boolean ready = false;
	protected boolean initialisedPopulation = false;
	protected boolean identifiedPopulation = false;
	protected boolean identifiedMe = false;

	public Game(Main main) {
		super(main);
	}

	public void setClient(jog.Network.Client c) {
		client = c;
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
				identifiedPopulation = true;
			} else if (message.matches("\\d is at: .+")) {
				int id = Integer.parseInt(message.split(" ")[0]);
				String pos = message.split(": ")[1];
				pos = pos.substring(1, pos.length() - 1); // Remove parentheses.
				int x = Integer.parseInt(pos.split(", ")[0]);
				int y = Integer.parseInt(pos.split(", ")[1]);
				people.get(id).setPosition(x, y);
			}
			if (initialisedPopulation && identifiedMe && identifiedPopulation) {
				ready = true;
				System.out.println("\t<client> Letting the server know I'm ready...");
				client.send("ready!");
			}
		}
		if (message.equals("begin")) {
			started = true;
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
			client.send("<" + dx + ", " + dy + ">");
		}
		for (Person p : people) {
			p.update(dt);
		}
	}

	@Override
	public void draw() {
		if (!started) {
			jog.Graphics.printCentred("Waiting for game to begin...", 0, 0, jog.Window.getWidth());
		}
		int y = 0;
		for (Person p : people) {
			if (p.id == me) {
				p.drawMe();
			} else {
				p.draw();
			}
			jog.Graphics.print("Person #" + y + " (" + p.getX() + ", " + p.getY() + ")", 0, y * 32);
			y ++;
		}
		if (identifiedMe && !started) {
			
		}
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
	public void keyReleased(int key) {}

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
