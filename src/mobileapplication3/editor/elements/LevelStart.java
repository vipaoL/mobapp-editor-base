/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileapplication3.editor.elements;

import mobileapplication3.platform.Property;
import mobileapplication3.platform.ui.Graphics;

/**
 *
 * @author vipaol
 */
public class LevelStart extends Element {
    
    private short x, y;
    
    public PlacementStep[] getPlacementSteps() {
        return new PlacementStep[] {
            new PlacementStep() {
                public void place(short pointX, short pointY) {
                    x = pointX;
                    y = pointY;
                }

                public String getName() {
                    return "Move";
                }
                
                public String getCurrentStepInfo() {
					return "x=" + x + " y=" + y;
				}
            }
        };
    }

    public PlacementStep[] getExtraEditingSteps() {
        return new PlacementStep[0];
    }
    
    public void paint(Graphics g, int zoomOut, int offsetX, int offsetY, boolean drawThickness) {
    	int r = 3;
        int prevColor = g.getColor();
        g.setColor(0x00ff00);
        g.fillArc(xToPX(x, zoomOut, offsetX) - r, yToPX(y, zoomOut, offsetY) - r, r*2, r*2, 0, 360);
        g.setColor(prevColor);
    }

    public Element setArgs(short[] args) {
        x = args[0];
        y = args[1];
        return this;
    }
    
    public short[] getArgsValues() {
        short[] args = {x, y};
        return args;
    }
    
    public Property[] getArgs() {
    	return new Property[] {
    			new Property("X") {
					public void setValue(short value) {
						x = value;
					}

					public short getValue() {
						return x;
					}
    			},
    			new Property("Y") {
					public void setValue(short value) {
						y = value;
					}

					public short getValue() {
						return y;
					}
    			}
    	};
    }

    public short getID() {
        return Element.LEVEL_START;
    }

    public int getStepsToPlace() {
        return STEPS_TO_PLACE[getID()];
    }
    
    public String getName() {
        return "Level Start";
    }
    
    public void move(short dx, short dy) {
    	x += dx;
    	y += dy;
    }
    
    public short[] getStartPoint() {
    	return new short[] {x, y};
    }
    
    public short[] getEndPoint() {
    	return new short[] {x, y};
    }

	public boolean isBody() {
		return false;
	}

	public void recalcCalculatedArgs() { }
	
	int carbodyLength = 240;
    int carbodyHeight = 40;
    int wr = 40;
    int carX = 0 - (carbodyLength / 2 - wr);
    int carY = 0 - wr / 2 * 3 - 2;
    
}
