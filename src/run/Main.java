package run;

public class Main extends Game {
	
	public static final String TITLE = "Blend";
	public static final int WIDTH = 960;
	public static final int HEIGHT = 640;
	public static final int MIN_FRAMES = 15;

	public static void main(String[] args) {
		Game.LOGGING = false;
		new Main();
	}
	
	public Main() {
		super(new scn.Title(), TITLE, WIDTH, HEIGHT, MIN_FRAMES);
	}
	
}
