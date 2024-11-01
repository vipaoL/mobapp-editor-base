/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileapplication3.editor;

import mobileapplication3.editor.elements.Element;
import mobileapplication3.platform.Mathh;
import mobileapplication3.platform.Platform;
import mobileapplication3.platform.ui.Graphics;
import mobileapplication3.platform.ui.RootContainer;
import mobileapplication3.ui.Keys;

/**
 *
 * @author vipaol
 */
public class EditorCanvas extends StructureViewerComponent {
	public static final int MODE_STRUCTURE = EditorUI.MODE_STRUCTURE, MODE_LEVEL = EditorUI.MODE_LEVEL;
	
	private static int zoomoutThresholdMacroMode = 200;
    private StructureBuilder structureBuilder;
    private Car car = new Car();
    private int colBg = 0x000000;
    private int cursorX, cursorY = 0;
    private int keyRepeats = 0;
    public int selectedElement = 0;
    private int editMode;
    
    PointerHandler pointerHandler = new PointerHandler();

    public EditorCanvas(StructureBuilder structureBuilder) {
    	super(structureBuilder.getElementsAsArray());
        this.structureBuilder = structureBuilder;
        editMode = structureBuilder.getMode();
    }
    
    public void onPaint(Graphics g, int x0, int y0, int w, int h, boolean forceInactive) {
    	if (editMode == MODE_STRUCTURE) {
    		car.drawCar(g, x0, y0);
    	}
        setElements(structureBuilder.getElementsAsArray());
        super.onPaint(g, x0, y0, w, h, forceInactive);
        if (editMode == MODE_STRUCTURE) {
        	drawStartPoint(g, x0, y0);
        }
        drawCursor(g, x0, y0);
        if (structureBuilder.placingNow != null) {
            g.setColor(0xaaffaa);
            g.drawString(structureBuilder.getPlacingInfo(), 0, 0, 0);
        }
    }
    
    public void drawBg(Graphics g, int x0, int y0, int w, int h, boolean isActive) {
    	g.setColor(colBg);
        g.fillRect(x0, y0, w, h);
        if (zoomOut < 200) {
            g.setColor(0x000077);
            int step = 1000/zoomOut;
            int gridOffsetY = x0 + (h/2) % step;
            int gridOffsetX = y0 + (w/2) % step;
            for (int y = gridOffsetY; y < h; y+=step) {
                g.drawLine(0, y, w, y);
            }
            for (int x = gridOffsetX; x < w; x+=step) {
                g.drawLine(x, 0, x, h);
            }
        }
    }
    
    private void drawCursor(Graphics g, int x0, int y0) {
        int x = x0 + xToPX(cursorX);
        int y = y0 + yToPX(cursorY);
        int r = 2;
        g.setColor(0x22aa22);
        g.drawArc(x - r, y - r, r*2, r*2, 0, 360);
        g.drawString(cursorX + " " + cursorY, x, y + r, Graphics.TOP | Graphics.LEFT);
    }
    
    protected void drawElements(Graphics g, int x0, int y0, Element[] elements) {
        for (int i = 0; i < elements.length; i++) {
        	try {
	        	elements[i].paint(g, zoomOut, x0 + offsetX, y0 + offsetY, zoomOut > zoomoutThresholdMacroMode, i == selectedElement);
        	} catch (Exception ex) { }
        }
    }
    
    private void drawStartPoint(Graphics g, int x0, int y0) {
        int d = 2;
        g.setColor(0x00ff00);
        g.fillRect(x0 + xToPX(0) - d, y0 + yToPX(0) - d, d*2, d*2);
    }

    public void onSetBounds(int x0, int y0, int w, int h) {
    	// enable macro if line thickness is greater than 1/16 of the smaller side of the screen
    	// thickness * 1000 / zoomOut >= minSide/16
    	// thickness * 16000 / minSide >= zoomOut
    	// threshold = thickness * 16000 / minSide
    	zoomoutThresholdMacroMode = Element.LINE_THICKNESS * 16000 / Math.min(w, h);
    	if (!isSizeSet()) {
    		zoomOut = Mathh.constrain(MIN_ZOOM_OUT, 4000000 / w, MAX_ZOOM_OUT);
    	}
        recalcOffset();
    }
    
    public boolean canBeFocused() {
        return true;
    }

    public boolean handlePointerPressed(int x, int y) {
        pointerHandler.handlePointerPressed(x, y);
        return true;
    }

    public boolean handlePointerClicked(int x, int y) {
        pointerHandler.dragged = false;
        pointerHandler.handlePointerClicked(x, y);
        return true;
    }

    public boolean handlePointerDragged(int x, int y) {
        pointerHandler.handlePointerDragged(x, y);
        return true;
    }

    public boolean handleKeyPressed(int keyCode, int count) {
        count = Math.max(count - 4, 1);
        if (count > 10) {
            count = 10;
        }
        
        int minStep = count * count;
        int step = Math.max(minStep, zoomOut / 1000);
		switch (RootContainer.getAction(keyCode)) {
            case Keys.FIRE:
                structureBuilder.handleNextPoint((short) cursorX, (short) cursorY, false);
                break;
            default:
            	if (!moveCursorByKeyboard(keyCode, step)) {
                	return false;
                }
        }
        pointerHandler.onCursorMove();
        keyRepeats = 0;
        return true;
    }
    
    public boolean handleKeyRepeated(int keyCode, int pressedCount) {
        int minStep = Math.max(zoomOut / 100, 1);
        int a = keyRepeats;
        int step = (minStep + a) * pressedCount;
        if (!moveCursorByKeyboard(keyCode, step)) {
        	return false;
        }
        pointerHandler.onCursorMove();
        keyRepeats = Math.min(100, keyRepeats + 1);
        return true;
    }
    
    private boolean moveCursorByKeyboard(int keyCode, int step) {
    	switch (RootContainer.getAction(keyCode)) {
	        case Keys.UP:
	            cursorY -= step;
	            break;
	        case Keys.DOWN:
	            cursorY += step;
	            break;
	        case Keys.LEFT:
	            cursorX -= step;
	            break;
	        case Keys.RIGHT:
	            cursorX += step;
	            break;
	        default:
	            switch (keyCode) {
	                case Keys.KEY_NUM1:
	                	cursorX -= step;
	                    cursorY -= step;
	                    break;
	                case Keys.KEY_NUM3:
	                	cursorX += step;
	                    cursorY -= step;
	                    break;
	                case Keys.KEY_NUM7:
	                	cursorX -= step;
	                    cursorY += step;
	                    break;
	                case Keys.KEY_NUM9:
	                	cursorX += step;
	                    cursorY += step;
	                    break;
	                default:
	                    return false;
	            }
	    }
    	return true;
    }

    public int getCursorX() {
        return cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }
    
    class Car {
        int carbodyLength = 240;
        int carbodyHeight = 40;
        int wr = 40;
        int carX = 0 - (carbodyLength / 2 - wr);
        int carY = 0 - wr / 2 * 3 - 2;
    
        void drawCar(Graphics g, int x0, int y0) {
        	if (zoomOut < zoomoutThresholdMacroMode) {
        		return;
        	}
            g.setColor(0x444444);
            g.drawRect(x0 + xToPX(carX - carbodyLength / 2),
                    y0 + yToPX(carY - carbodyHeight / 2),
                    carbodyLength*1000/zoomOut,
                    carbodyHeight*1000/zoomOut);
            int lwX = x0 + xToPX(carX - (carbodyLength / 2 - wr));
            int lwY = y0 + yToPX(carY + wr / 2);
            int rwX = x0 + xToPX(carX + (carbodyLength / 2 - wr));
            int rwY = y0 + yToPX(carY + wr / 2);
            
            int wrScaled = wr * 1000 / zoomOut;
            g.setColor(colBg);
            g.fillArc(lwX - wrScaled, lwY - wrScaled, wrScaled*2, wrScaled*2, 0, 360);
            g.fillArc(rwX - wrScaled, rwY - wrScaled, wrScaled*2, wrScaled*2, 0, 360);
            g.setColor(0x444444);
            g.drawArc(lwX - wrScaled, lwY - wrScaled, wrScaled*2, wrScaled*2, 0, 360);
            g.drawArc(rwX - wrScaled, rwY - wrScaled, wrScaled*2, wrScaled*2, 0, 360);
            
            int lineEndX = carX - carbodyLength / 2 - wr / 2;
            int lineStartX = lineEndX - wr;
            int lineY = carY + carbodyHeight / 3;
            g.drawLine(x0 + xToPX(lineStartX), y0 + yToPX(lineY), x0 + xToPX(lineEndX), y0 + yToPX(lineY));
            lineStartX += carbodyHeight / 3;
            lineEndX += carbodyHeight / 3;
            lineY += carbodyHeight / 3;
            g.drawLine(x0 + xToPX(lineStartX), y0 + yToPX(lineY), x0 + xToPX(lineEndX), y0 + yToPX(lineY));
            lineStartX -= carbodyHeight * 2 / 3;
            lineEndX -= carbodyHeight * 2 / 3;
            lineY -= carbodyHeight * 2 / 3;
            g.drawLine(x0 + xToPX(lineStartX), y0 + yToPX(lineY), x0 + xToPX(lineEndX), y0 + yToPX(lineY));
        }
    }
    
    class PointerHandler {
        public boolean dragged = false;
        int pressedX, pressedY;
        int prevCursorX, prevCursorY;
        int prevOffsetX, prevOffsetY;
        int lastCursorX = 0;
        int lastCursorY = 0;
        
        void handlePointerPressed(int x, int y) {
            if (!isVisible) {
                return;
            }
            
            pressedX = x;
            pressedY = y;
            prevOffsetX = offsetX;
            prevOffsetY = offsetY;
            prevCursorX = cursorX;
            prevCursorY = cursorY;

            dragged = false;
        }

        void handlePointerDragged(int x, int y) {
            if (!isVisible) {
                return;
            }
            
            int dx = x - pressedX;
            int dy = y - pressedY;
            
            lastCursorX = cursorX;
            lastCursorY = cursorY;
            cursorX = prevCursorX + dx * zoomOut / 1000;
            cursorY = prevCursorY + dy * zoomOut / 1000;
            
            onCursorMove();
            dragged = dragged || (dx != 0 || dy != 0);
        }

        void handlePointerClicked(int x, int y) {
            if (!isVisible) {
                return;
            }
            
            if (!dragged) {
                structureBuilder.handleNextPoint((short) cursorX, (short) cursorY, false);
            }
        }
        
        void onCursorMove() {
            if (zoomOut < 500 && (lastCursorX != cursorX || lastCursorY != cursorY)) {
                Platform.vibrate(1);
            }

            recalcOffset();

            structureBuilder.handleNextPoint((short) cursorX, (short) cursorY, true);
        }
    }
    
    void zoomIn() {
        int newZoomOut = zoomOut / 2;
        if (newZoomOut > MIN_ZOOM_OUT) {
            zoomOut = newZoomOut;
        }
        recalcOffset();
    }
    
    void zoomOut() {
        int newZoomOut = zoomOut * 2;
        if (newZoomOut < MAX_ZOOM_OUT) {
            zoomOut = newZoomOut;
        }
        recalcOffset();
    }
    
    void recalcOffset() {
        offsetX = w/2 - cursorX * 1000 / zoomOut;
        offsetY = h/2 - cursorY * 1000 / zoomOut;
    }
    
    public int xToPX(int c) {
        return c * 1000 / zoomOut + offsetX;
    }

    public int yToPX(int c) {
        return c * 1000 / zoomOut + offsetY;
    }
}
