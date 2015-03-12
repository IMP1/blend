package run;

import java.awt.event.KeyEvent;

public class Main implements jog.Event.EventHandler {
	
	public static final String TITLE = "Blend";
	public static final int WIDTH = 960;
	public static final int HEIGHT = 640;
	public static final double MAX_DT = 1.0 / 15;
	
	private scn.Scene currentScene;

	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		
		jog.Window.initialise(WIDTH, HEIGHT, TITLE);
		jog.Input.initialise();
		jog.Event.setHandler(this);
		jog.Graphics.initialise();
		
		load();
		gameLoop();
		close();
		System.exit(0);
	}
	
	private void gameLoop() {
		long lastTick = System.nanoTime();
		while (jog.Window.isOpen()) {
			try { Thread.sleep(4); } catch (Exception e) {}; // pause a bit so that we don't choke the system
			jog.Event.pump();
			
			double deltaTime = (double)(System.nanoTime() - lastTick) / 1_000_000_000.0;
			lastTick = System.nanoTime();
			// Update multiple times rather than with a dangerously large delta-time
			while (deltaTime > MAX_DT) {
				System.out.println("[Game] dt > " + MAX_DT + " (" + deltaTime + ").");
				update(MAX_DT);
				deltaTime -= MAX_DT;
			}
			if (deltaTime > 0)
				update(deltaTime);
			
			jog.Graphics.clear();
			jog.Graphics.setColour(255, 255, 255, 255);
			draw();
		}
	}
	
	private void load() {
		setScene(new scn.Title(this));
	}
	
	private void close() {
		System.out.println("Closed successfully.");
	}
	
	public void setScene(scn.Scene newScene) {
		synchronized (this) {
			if (currentScene != null) currentScene.close();
			currentScene = newScene;
			currentScene.start();
		}
	}
	
	
	/*
	 * -------------------------------------------------
	 * The following just pass on handlers to the scene.
	 * -------------------------------------------------
	 */
	
	
	private void update(double dt) {
		synchronized (this) {
			currentScene.update(dt);
		}
	}
	
	private void draw() {
		currentScene.draw();
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseKey) {
		currentScene.mousePressed(mouseX, mouseY, mouseKey);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseKey) {
		currentScene.mouseReleased(mouseX, mouseY, mouseKey);
	}

	@Override
	public void keyPressed(int key) {
		currentScene.keyPressed(key);
	}

	@Override
	public void keyReleased(int key) {
		currentScene.keyReleased(key);
	}

	@Override
	public void focus(boolean gained) {
		
	}

	@Override
	public void mouseFocus(boolean gained) {
		
	}

	@Override
	public void resize(int oldWidth, int oldHeight) {
		
	}

	@Override
	public void mouseMoved(int x, int y) {
		
	}

	@Override
	public boolean quit() {
		if (jog.Input.isKeyDown(KeyEvent.VK_SHIFT)) {
			return true;
		}
		return false;
	}

	@Override
	public void mouseScrolled(int x, int y, int scroll) {
		
	}

}
