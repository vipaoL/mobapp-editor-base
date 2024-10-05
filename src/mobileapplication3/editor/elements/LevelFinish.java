package mobileapplication3.editor.elements;

import mobileapplication3.platform.Mathh;
import mobileapplication3.platform.ui.Graphics;

public class LevelFinish extends Element {
	
	// *############	"*" - (anchorX;anchorY)
	// #     @     #	"@" - (x;y)
	// #############
	
	private short x, y, l, thickness = 20, angle;
	private short anchorX, anchorY;

	public PlacementStep[] getPlacementSteps() {
		return new PlacementStep[] {
			new PlacementStep() {
				public void place(short pointX, short pointY) {
					setAnchorPoint(pointX, pointY);
				}

				public String getName() {
					return "Move";
				}

				public String getCurrentStepInfo() {
					return "x=" + anchorX + "y=" + anchorY;
				}
			},
			new PlacementStep() {
				public void place(short pointX, short pointY) {
                    short dx = (short) (pointX - anchorX);
                    short dy = (short) (pointY - anchorY);
                    l = (short) calcDistance(dx, dy);
                    angle = (short) Mathh.arctg(dx, dy);
                    calcCenterPoint();
				}

				public String getName() {
					return "Change length and angle";
				}

				public String getCurrentStepInfo() {
					return "l=" + l + "angle=" + angle;
				}
			}
		};
	}

	public PlacementStep[] getExtraEditingSteps() {
		return new PlacementStep[0];
	}

	public void paint(Graphics g, int zoomOut, int offsetX, int offsetY, boolean drawThickness) {
		int dx = (int) (l * Mathh.cos(angle) / 1000);
        int dy = (int) (l * Mathh.sin(angle) / 1000);
        g.setColor(0, 255, 0);
		g.drawLine(
				xToPX(x - dx/2, zoomOut, offsetX),
                yToPX(y - dy/2, zoomOut, offsetY),
                xToPX(x + dx/2, zoomOut, offsetX),
                yToPX(y + dy/2, zoomOut, offsetY),
                thickness,
                zoomOut,
                drawThickness,
                true,
                false,
                false);
	}
	
	private void setCenterPoint(short x, short y) {
		if (x == this.x && y == this.y) {
			return;
		}
		this.x = x;
		this.y = y;
		calcAnchorPoint();
	}
	
	private void setAnchorPoint(short x, short y) {
		if (x == anchorX && y == anchorY) {
			return;
		}
		anchorX = x;
		anchorY = y;
		calcCenterPoint();
	}
	
	private void calcCenterPoint() {
		x = (short) (anchorX + l * Mathh.cos(angle) / 2000 + thickness * Mathh.cos(angle + 90) / 2000);
		y = (short) (anchorY + l * Mathh.sin(angle) / 2000 + thickness * Mathh.sin(angle + 90) / 2000);
	}
	
	private void calcAnchorPoint() {
		anchorX = (short) (x - l * Mathh.cos(angle) / 2000 - thickness * Mathh.cos(angle + 90) / 2000);
		anchorY = (short) (y - l * Mathh.sin(angle) / 2000 - thickness * Mathh.sin(angle + 90) / 2000);
	}

	public Element setArgs(short[] args) {
		l = args[2];
		thickness = args[3];
		angle = args[4];
		setCenterPoint(args[0], args[1]);
		return this;
	}

	public short[] getArgsValues() {
		return new short[] {x, y, l, thickness, angle};
	}

	public Argument[] getArgs() {
    	return new Argument[] {
    			new Argument("X") {
					public void setValue(short value) {
						x = value;
					}

					public short getValue() {
						return x;
					}
    			},
    			new Argument("Y") {
					public void setValue(short value) {
						y = value;
					}

					public short getValue() {
						return y;
					}
    			},
    			new Argument("L") {
					public void setValue(short value) {
						l = value;
					}

					public short getValue() {
						return l;
					}
					
					public short getMinValue() {
						return 0;
					}
    			},
    			new Argument("Thickness") {
					public void setValue(short value) {
						thickness = value;
					}

					public short getValue() {
						return thickness;
					}
					
					public short getMinValue() {
						return 0;
					}
					
					public short getMaxValue() {
						return (short) (l/2);
					}
    			},
    			new Argument("Angle", true) {
					public void setValue(short value) {
						angle = value;
					}

					public short getValue() {
						return angle;
					}
					
					public short getMinValue() {
						return 0;
					}
					
					public short getMaxValue() {
						return 360;
					}
    			}
    	};
    }

	public short getID() {
		return LEVEL_FINISH;
	}

	public int getStepsToPlace() {
		return stepsToPlace[getID()];
	}

	public String getName() {
		return "Level Finish";
	}

	public void move(short dx, short dy) {
		x += dx;
		y += dy;
	}
	
	private short[] getCornerPoint(int i) {
		// -- +-
		// -+ ++
		int m1, m2;
		if (i == 0) {
			m1 = m2 = -1;
		} else if (i == 1) {
			m1 = 1;
			m2 = -1;
		} else if (i == 2) {
			m1 = m2 = 1;
		} else {
			m1 = -1;
			m2 = 1;
		}
		
		return new short[] {
				(short) (x + m1 * l * Mathh.cos(angle) / 2000 + m2 * thickness * Mathh.cos(angle + 90) / 2000),
				(short) (y + m1 * l * Mathh.sin(angle) / 2000 + m2 * thickness * Mathh.sin(angle + 90) / 2000)
		};
	}

	public short[] getStartPoint() {
		return getCornerPoint(((angle+90)%360 < 180) ? 0 : 2);
	}

	public short[] getEndPoint() {
		return getCornerPoint(((angle+90)%360 < 180) ? 1 : 3);
	}

	public boolean isBody() {
		return true;
	}

	public void recalcCalculatedArgs() {
		calcAnchorPoint();
	}

}