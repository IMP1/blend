package cls;

import java.awt.Color;

public class Person {
	
	public static final int MOVE_SPEED = 128;
	public static final int BOB_HEIGHT = 4;
	public static final int BOB_SPEED = (int)(2 * Math.PI);

	public static final jog.Image IMAGE = new jog.Image("gfx/person.png");
	
	public final int id;
	private double realX, realY;
	private int x, y;
	private Color colour;
	private double bobTimer;
	
	public Person(int id, int x, int y, Color colour) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.realX = x;
		this.realY = y;
		bobTimer = Math.random() * 2 * Math.PI;
		setColour(colour);
	}
	
	public void setColour(Color colour) {
		this.colour = colour;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	
	public void setColour(int r, int g, int b) {
		setColour(new Color(r, g, b));
	}

	public void move(double dx, double dy) {
		realX += dx;
		realY += dy;
		if ((int)realX != x) {
			x = (int)realX;
		}
		if ((int)realY != y) {
			y = (int)realY;
		}
	}
	
	public void update(double dt) {
		bobTimer += dt * BOB_SPEED;
	}
	
	private void drawImage() {
		int bobY = (int)(Math.sin(bobTimer) * BOB_HEIGHT);
		jog.Graphics.draw(IMAGE, x + IMAGE.getWidth() / 2, y + IMAGE.getWidth() + bobY);
	}
	
	public void draw() {
		jog.Graphics.setColour(colour);
		drawImage();
	}
	
	public void drawMe() {
		jog.Graphics.setColour(255, 167, 26);
		drawImage();
	}
	
}
