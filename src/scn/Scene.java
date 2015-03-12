package scn;

public abstract class Scene implements jog.Event.EventHandler {

	protected run.Main main;
	
	protected Scene(run.Main main) {
		this.main = main;
	}
	
	abstract public void start();
	
	abstract public void update(double dt);
	
	abstract public void draw();
	
	abstract public void close();
	
	@Override
	abstract public void keyPressed(int key);
	
	@Override
	abstract public void keyReleased(int key);
	
	@Override
	abstract public void mousePressed(int mouseX, int mouseY, int mouseKey);
	
	@Override
	abstract public void mouseReleased(int mouseX, int mouseY, int mouseKey);

	@Override
	abstract public void mouseScrolled(int x, int y, int scroll);

	@Override
	abstract public void focus(boolean gained);

	@Override
	abstract public void mouseFocus(boolean gained);

	@Override
	abstract public void resize(int oldWidth, int oldHeight);

	@Override
	abstract public void mouseMoved(int x, int y);

	@Override
	abstract public boolean quit();
}
