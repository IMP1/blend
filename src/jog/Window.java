package jog;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public abstract class Window {
	
	public enum WindowMode {
		WINDOWED,
		BORDERLESS_WINDOWED,
		FULLSCREEN,
		BORDERLESS_FULLSCREEN,
	}
	
	private static JFrame window;
	private static boolean open = false;
	protected static Canvas canvas;
	protected static double scaleX = 1;
	protected static double scaleY = 1;
	
	public static int getWidth() {
		return window.getWidth() - window.getInsets().left - window.getInsets().right;
	}
	
	public static int getHeight() {
		return window.getHeight() - window.getInsets().bottom - window.getInsets().top;
	}
	
	public static DisplayMode[] getDisplayModes() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayModes();
	}
	
	public static Rectangle getScreenSize() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		return gd.getDefaultConfiguration().getBounds();
	}
	
	public static int getPositionX() {
		return window.getX();
	}
	
	public static int getPositionY() {
		return window.getY();
	}
	
	public static void setPosition(int x, int y) {
		window.setLocation(x, y);
	}
	
	protected static int getMouseX() { 
		try {
			return window.getMousePosition().x - window.getInsets().left;
		} catch (NullPointerException e) {
			return -1;
		}
	}
	
	protected static int getMouseY() {
		try {
			return window.getMousePosition().y - window.getInsets().top;
		} catch (NullPointerException e) {
			return -1;
		}
	}
	
	public static boolean isOpen() {
		return open;
	}
	
	public static void close() {
		open = false;
	}
	
	protected static void abortClosing() {
		if (!open) {
			open = true;
			System.out.println("aborted closing");
		}
	}
	
	public static void initialise(int width, int height, String title) {
		Window.initialise(width, height, title, WindowMode.WINDOWED);
	}
	public static void initialise(int width, int height, String title, WindowMode mode) {
		Dimension size;
		if (mode == WindowMode.BORDERLESS_FULLSCREEN || mode == WindowMode.FULLSCREEN) {
			scaleX = getScreenSize().getWidth() / width;
			scaleY = getScreenSize().getHeight() / height;
			size = new Dimension(getScreenSize().width, getScreenSize().height);
		} else {
			size = new Dimension(width, height);
		}
		
		canvas = new Canvas();
		canvas.setPreferredSize(size);
		
		window = new JFrame();
		window.setResizable(false);
		window.add(canvas);
		if (mode == WindowMode.BORDERLESS_FULLSCREEN || mode == WindowMode.BORDERLESS_WINDOWED) {
			window.setUndecorated(true);
		}
		window.pack();
		window.setTitle(title);
		window.setLocationRelativeTo(null);
		
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				jog.Event.push(Event.EventType.QUIT);
			}
			
		});
		window.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				jog.Event.push(Event.EventType.FOCUS, false);
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				jog.Event.push(Event.EventType.FOCUS, true);
			}
		});
		
		window.setVisible(true);
		
		canvas.createBufferStrategy(2);
		Window.open = true;
	}
	
	protected static void setKeyboardListener(KeyListener listener) {
		window.addKeyListener(listener);
		canvas.addKeyListener(listener);
	}
	
	protected static void setMouseListener(MouseListener listener) {
		window.addMouseListener(listener);
		canvas.addMouseListener(listener);
	}
	
	protected static void setMouseMotionListener(MouseMotionListener listener) {
		window.addMouseMotionListener(listener);
		canvas.addMouseMotionListener(listener);
	}
	
	protected static void setMouseWheelListener(MouseWheelListener listener) {
		window.addMouseWheelListener(listener);
		canvas.addMouseWheelListener(listener);
	}
	
	public static void setIcon(String filename) {
		ImageIcon img = new ImageIcon(Filesystem.getPath(filename));
		window.setIconImage(img.getImage());
	}
	
	public static void setMouseCursor() {
		setMouseCursor(Cursor.DEFAULT_CURSOR); 
	}
	
	public static void setMouseCursor(int cursorType) {
		Cursor cursor = Cursor.getPredefinedCursor(cursorType);
		window.setCursor(cursor);
	}

}
