package gui;

import java.awt.event.MouseEvent;

public abstract class Window {
	
	protected class Resize {
		boolean top, bottom, right, left;
		int x, y, deltaTop, deltaBottom, deltaLeft, deltaRight;
		boolean active;
		protected Resize() {
			top = false;
			bottom = false;
			left = false;
			right = false;
			x = 0;
			y = 0;
			deltaTop = 0;
			deltaBottom = 0;
			deltaLeft = 0;
			deltaRight = 0;
			active = false;
		}
		
	}
	
	protected class Drag {
		int x, y, dx, dy;
		boolean active;
		protected Drag() {
			x = 0;
			y = 0;
			dx = 0;
			dy = 0;
			active = false;
		}
	}
	
	public final static int TITLE_HEIGHT = 24;
	public final static int MIN_WIDTH = 64;
	public final static int MIN_HEIGHT = 64;
	public final static int RESIZE_LEEWAY = 4; 
	
	private int x;
	private int y;
	private int width;
	private int height;
	private int minimumWidth;
	private int maximumWidth;
	private int minimumHeight;
	private int maximumHeight;
	private String title = "Window";
	private Resize resize;
	private Drag drag;
	
	public Window(int x, int y, int width, int height) {
		this(x, y, width, height, MIN_WIDTH, MIN_HEIGHT, width, height);
	}
	
	public Window(int x, int y, int width, int height, int minWidth, int minHeight, int maxWidth, int maxHeight) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.minimumWidth = minWidth;
		this.minimumHeight = minHeight;
		this.maximumWidth = maxWidth;
		this.maximumHeight = maxHeight;
		this.resize = new Resize();
		this.drag = new Drag();
	}
	
	public void mousePressed(int mx, int my, int key) {
		if (!hover(mx, my)) return;
		if (key == MouseEvent.BUTTON1) {
			handleResizing(mx, my);
			handleMoving(mx, my);
		}
	}
	
	private boolean hover(int mx, int my) {
		return (mx >= x - RESIZE_LEEWAY || mx <= x + width + RESIZE_LEEWAY || my >= y - RESIZE_LEEWAY || my <= y + width + RESIZE_LEEWAY);
	}
	
	private boolean hoverResize(int mx, int my) {
		boolean top, bottom, left, right;
		left = (mx >= x - RESIZE_LEEWAY && mx <= x + RESIZE_LEEWAY);
		right = (mx >= x + width - RESIZE_LEEWAY && mx <= x + width + RESIZE_LEEWAY);
		top = (my >= y - RESIZE_LEEWAY && my <= y + RESIZE_LEEWAY);
		bottom = (my >= y + height - RESIZE_LEEWAY && my <= y + height + RESIZE_LEEWAY);
		return (left || right || top || bottom) && hover(mx, my);
	}
	
	private boolean hoverMove(int mx, int my) {
		return (mx >= x && mx <= x + width && my >= y && my <= y + TITLE_HEIGHT) && hover(mx, my);
	}
	
	private void handleResizing(int mx, int my) {
		if (resize.active || drag.active) return;
		boolean top = false, bottom = false, left = false, right = false;
		if (mx >= x - RESIZE_LEEWAY && mx <= x + RESIZE_LEEWAY) left = true;
		if (mx >= x + width - RESIZE_LEEWAY && mx <= x + width + RESIZE_LEEWAY) right = true;
		if (my >= y - RESIZE_LEEWAY && my <= y + RESIZE_LEEWAY) top = true;
		if (my >= y + height - RESIZE_LEEWAY && my <= y + height + RESIZE_LEEWAY) bottom = true;
		if (left || right || top || bottom) {
			resize.active = true;
			resize.left = left;
			resize.right = right;
			resize.top = top;
			resize.bottom = bottom;
			resize.x = mx;
			resize.y = my;
			resize.deltaBottom = resize.deltaTop = resize.deltaLeft = resize.deltaRight = 0; 
			jog.Window.setMouseCursor(getResizeCursor(mx, my));
		}
	}

	private void handleMoving(int mx, int my) {
		if (resize.active || drag.active) return;
		if (mx >= x && mx <= x + width && my >= y && my <= y + TITLE_HEIGHT) {
			drag.active = true;
			drag.x = mx;
			drag.y = my;
			drag.dx = drag.dy = 0;
			jog.Window.setMouseCursor(java.awt.Cursor.MOVE_CURSOR);
		}
	}
	
	private int getResizeCursor(int mx, int my) {
		if (mx <= x + RESIZE_LEEWAY) {
			if (my <= y + RESIZE_LEEWAY) {
				return java.awt.Cursor.NW_RESIZE_CURSOR;
			} else if (my >= y + height - RESIZE_LEEWAY) {
				return java.awt.Cursor.SW_RESIZE_CURSOR;
			} else {
				return java.awt.Cursor.W_RESIZE_CURSOR;
			}
		} else if (mx < x + width - RESIZE_LEEWAY) {
			if (my <= y + RESIZE_LEEWAY) {
				return java.awt.Cursor.N_RESIZE_CURSOR;
			} else {
				return java.awt.Cursor.S_RESIZE_CURSOR;
			}
		} else {
			if (my <= y + RESIZE_LEEWAY) {
				return java.awt.Cursor.NE_RESIZE_CURSOR;
			} else if (my >= y + height - RESIZE_LEEWAY) {
				return java.awt.Cursor.SE_RESIZE_CURSOR;
			} else {
				return java.awt.Cursor.E_RESIZE_CURSOR;
			}
		}
	}
	
	public void mouseReleased(int mx, int my, int key) {
		if (resize.active) {
			x += resize.deltaLeft;
			y += resize.deltaTop;
			width += resize.deltaRight - resize.deltaLeft;
			height += resize.deltaBottom - resize.deltaTop;
		} else if (drag.active) {
			x += drag.dx;
			y += drag.dy;
		}
		jog.Window.setMouseCursor();
		resize.active = false;
		drag.active = false;
	}
	
	public void update() {
		int mx = jog.Input.getMouseX();
		int my = jog.Input.getMouseY();
		if (resize.active) {
			if (resize.top && validHeight(my - resize.y, resize.deltaBottom)) {
				resize.deltaTop = my - resize.y;
			}
			if (resize.bottom && validHeight(resize.deltaTop, my - resize.y)) {
				resize.deltaBottom = my - resize.y;
			}
			if (resize.left && validWidth(mx - resize.x, resize.deltaRight)) {
				resize.deltaLeft = mx - resize.x;
			}
			if (resize.right && validWidth(resize.deltaLeft, mx - resize.x)) {
				resize.deltaRight = mx - resize.x;
			}
		} else if (drag.active) {
			drag.dx = mx - drag.x;
			drag.dy = my - drag.y;
		} else if (hoverResize(mx, my)) {
			jog.Window.setMouseCursor(getResizeCursor(mx, my));
		} else if (hoverMove(mx, my)) {
			jog.Window.setMouseCursor(java.awt.Cursor.MOVE_CURSOR);
		} else {
			jog.Window.setMouseCursor();
		}
	}
	
	private boolean validWidth(int changeLeft, int changeRight) {
		return width + changeRight - changeLeft >= minimumWidth &&
				width + changeRight - changeLeft <= maximumWidth;
	}
	
	private boolean validHeight(int changeTop, int changeBottom) {
		return height + changeBottom - changeTop >= minimumHeight &&
				height + changeBottom - changeTop <= maximumHeight;
	}
	
	public void draw() {
		int x = this.x;
		int y = this.y;
		int w = this.width;
		int h = this.height;
		if (resize.active) {
			x += resize.deltaLeft;
			y += resize.deltaTop;
			w += resize.deltaRight - resize.deltaLeft;
			h += resize.deltaBottom - resize.deltaTop;
		} else if (drag.active) {
			x += drag.dx;
			y += drag.dy;
		}
		jog.Graphics.setColour(0, 0, 0, 192);
		jog.Graphics.rectangle(false, x - 1, y - 1, w + 2, h + 2);
		jog.Graphics.rectangle(false, x, y, w + 2, h + 2);
		jog.Graphics.setColour(32, 32, 32);
		jog.Graphics.rectangle(true, x, y, w, h);
		jog.Graphics.setColour(255, 255, 255);
		jog.Graphics.rectangle(false, x, y, w, h);
		jog.Graphics.rectangle(false, x, y, w, TITLE_HEIGHT);
		jog.Graphics.print(title, x + 4, y + 4);
	}

}
