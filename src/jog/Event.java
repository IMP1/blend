package jog;

import java.util.ArrayList;;

public class Event {
	
	private static Object monitor = new Object();
	protected static ArrayList<BaseEvent> eventQueueBuffered = new ArrayList<BaseEvent>();
	private static BaseEvent[] queue = new BaseEvent[0];
	private static EventHandler handler = null;
	
	public static void setHandler(EventHandler newHandler) {
		handler = newHandler;
	}
	
	private static class BaseEvent {
		private final EventType type;
		private final Object[] params;
		protected BaseEvent(EventType type, Object... params) {
			this.type = type;
			this.params = params;
		}
	}
	
	protected enum EventType {
		FOCUS,
		MOUSE_FOCUS,
		KEY_PRESSED,
		KEY_RELASED,
		MOUSE_MOVED,
		MOUSE_SCROLLED,
		MOUSE_PRESSED,
		MOUSE_RELEASED,
		QUIT,
	}
	
	public interface EventHandler {
		public void focus(boolean gained);
		public void mouseFocus(boolean gained);
		public void resize(int oldWidth, int oldHeight);
		public void keyPressed(int key);
		public void keyReleased(int key);
		public void mouseMoved(int x, int y);
		public void mouseScrolled(int x, int y, int scroll);
		public void mousePressed(int x, int y, int key);
		public void mouseReleased(int x, int y, int key);
		public boolean quit();
	}
	
	public static void push(EventType type, Object... params) {
		synchronized (monitor) {
			eventQueueBuffered.add(new BaseEvent(type, params));
		}
	}
	
	public static void pump() {
		synchronized (monitor) {
			queue = eventQueueBuffered.toArray(new BaseEvent[0]);
			eventQueueBuffered.clear();
		}
		for (int i = 0; i < queue.length; i ++) {
			poll(queue[i]);
		}
	}
	
	public static void poll(BaseEvent e) {
		if (handler != null) {
			switch(e.type) {
				case FOCUS:
					handler.focus((boolean)e.params[0]);
					break;
				case MOUSE_FOCUS:
					handler.mouseFocus((boolean)e.params[0]);
					break;
				case KEY_PRESSED:
					handler.keyPressed((int)e.params[0]);
					break;
				case KEY_RELASED:
					handler.keyReleased((int)e.params[0]);
					break;
				case MOUSE_MOVED:
					handler.mouseMoved((int)e.params[0], (int)e.params[1]);
					break;
				case MOUSE_SCROLLED:
					handler.mouseScrolled((int)e.params[0], (int)e.params[1], (int)e.params[2]);
					break;
				case MOUSE_PRESSED:
					handler.mousePressed((int)e.params[0], (int)e.params[1], (int)e.params[2]);
					break;
				case MOUSE_RELEASED:
					handler.mouseReleased((int)e.params[0], (int)e.params[1], (int)e.params[2]);
					break;
				case QUIT:
					boolean abortQuitting = handler.quit();
					if (abortQuitting) {
						Window.abortClosing();
					} else {
						Window.close();
					}
					break;
				default:
					System.err.println("[Event] Unrecognised event: " + e.toString());
					break;
			}
		}
	}

}
