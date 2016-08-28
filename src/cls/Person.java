package cls;

import java.awt.Color;

public class Person implements lib.Object2D {
	
	public static final int MOVE_SPEED = 128;
	public static final int BOB_HEIGHT = 4;
	public static final int BOB_SPEED = (int)(2 * Math.PI);

	private final static int BODY_RADIUS = 16;
	private final static int BODY_EXTRA_HEIGHT = 8;
	private final static int HEAD_RADIUS = 8;
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
	
	public int getX() { return x - IMAGE.getWidth() / 2; }
	public int getY() { return y - IMAGE.getHeight(); }
	
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
	
	private void drawBorder(int r, int g, int b) {
		Color c = jog.Graphics.getColour();
		jog.Graphics.setColour(r, g, b);
		drawImage(BODY_RADIUS + 2, HEAD_RADIUS + 2, BODY_EXTRA_HEIGHT + 2, BODY_RADIUS + 2);
		jog.Graphics.setColour(c);
	}
	
	private void drawImage(final int bodyRadius, final int headRadius, final int bodyHeight, final int bodyWidth) {
		int bobY = (int)(Math.sin(bobTimer) * BOB_HEIGHT);
		
		jog.Graphics.arc(true, x, y + bobY - BODY_EXTRA_HEIGHT, bodyRadius, 0, -Math.PI);
		jog.Graphics.rectangle(true, x - bodyWidth, y + bobY - BODY_EXTRA_HEIGHT, bodyRadius * 2, bodyHeight);
		jog.Graphics.circle(true, x, y + bobY - HEAD_RADIUS - BODY_RADIUS - BODY_EXTRA_HEIGHT, headRadius);

		// Debugging Positional Circle
		Color c = jog.Graphics.getColour();
		jog.Graphics.setColour(255, 0, 0);
		jog.Graphics.circle(true, x, y, 4);
		jog.Graphics.setColour(c);
	}
	
	public void draw() {
		jog.Graphics.setColour(colour);
		drawImage(BODY_RADIUS, HEAD_RADIUS, BODY_EXTRA_HEIGHT, BODY_RADIUS);
	}
	
	public void drawAsMe() {
		drawBorder(255, 255, 255);
		draw();
	}
	
	public void drawAsTarget() {
		drawBorder(255, 167, 26);
		draw();
	}
	
}
