package jog;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public abstract class Input {
//	
//	public enum MouseKey {
//		L(MouseEvent.BUTTON1);
//		public boolean is() {
//			return 
//		}
//		private final int key;
//		private MouseKey(int key) {
//			this.key = key;
//		}
//	}

	private static class ListenerKeyboard implements KeyListener {
		
		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			keysDown[e.getKeyCode()] = true;
			int key = e.getKeyCode();
			jog.Event.push(Event.EventType.KEY_PRESSED, key);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			keysDown[e.getKeyCode()] = false;
			int key = e.getKeyCode();
			jog.Event.push(Event.EventType.KEY_RELASED, key);
		}

	}
	
	private static class ListenerMouse implements MouseListener, MouseMotionListener, MouseWheelListener {
		
		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			mouseDown[e.getButton()] = true;
			int button = e.getButton();
			int x = e.getX();
			int y = e.getY();
			jog.Event.push(Event.EventType.MOUSE_PRESSED, x, y, button);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mouseDown[e.getButton()] = false;
			int button = e.getButton();
			int x = e.getX();
			int y = e.getY();
			jog.Event.push(Event.EventType.MOUSE_RELEASED, x, y, button);
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			mouseOver = true;
			jog.Event.push(Event.EventType.MOUSE_FOCUS, true);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			mouseOver = false;
			jog.Event.push(Event.EventType.MOUSE_FOCUS, false);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			jog.Event.push(Event.EventType.MOUSE_MOVED, x, y);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int x = e.getX();
			int y = e.getX();
			int scroll = -e.getWheelRotation();
			jog.Event.push(Event.EventType.MOUSE_SCROLLED, x, y, scroll);
		}

	}
	
	private static boolean[] keysDown;
	private static boolean[] mouseDown;
	private static boolean mouseOver;
	private static ListenerKeyboard keyListener;
	private static ListenerMouse mouseListener;
	
	public static void initialise() {
		keysDown = new boolean[256];
		mouseDown = new boolean[8];
		keyListener = new ListenerKeyboard();
		mouseListener = new ListenerMouse();
		Window.setKeyboardListener(keyListener);
		Window.setMouseListener(mouseListener);
		Window.setMouseMotionListener(mouseListener);
		Window.setMouseWheelListener(mouseListener);
	}

	public static boolean isKeyDown(int key) {
		return keysDown[key];
	}
	
	public static boolean isMouseDown(int key) {
		return mouseDown[key];
	}
	
	public static boolean isMouseOver() {
		return mouseOver;
	}
	
	public static int getMouseX() {
		return Window.getMouseX();
	}
	
	public static int getMouseY() {
		return Window.getMouseY();
	}

}
