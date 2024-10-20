/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileapplication3.editor;

import java.io.IOException;
import java.util.Vector;

import mobileapplication3.editor.elements.Element;
import mobileapplication3.editor.elements.EndPoint;
import mobileapplication3.editor.elements.LevelStart;
import mobileapplication3.editor.elements.Element.PlacementStep;
import mobileapplication3.platform.FileUtils;

/**
 *
 * @author vipaol
 */

public abstract class StructureBuilder {
    
    public static final int PLACE_NOTHING = -1;
    public static final int MODE_STRUCTURE = EditorUI.MODE_STRUCTURE, MODE_LEVEL = EditorUI.MODE_LEVEL;
    private int mode = MODE_STRUCTURE;
    private Vector buffer;
    public Element placingNow;
    private NextPointHandler nextPointHandler;
    public boolean isEditing = false;
    private String path = null;
    
    public StructureBuilder(int mode) {
    	this.mode = mode;
    	buffer = new Vector();
    	if (mode == MODE_STRUCTURE) {
    		buffer.addElement(new EndPoint().setArgs(new short[]{0, 0}));
    	} else if (mode == MODE_LEVEL) {
    		buffer.addElement(new LevelStart().setArgs(new short[]{0, -200}));
    	}
	}
    
    public void place(short id, short x, short y) throws IllegalArgumentException {
    	if (placingNow != null) {
    		onUpdate();
    	}
    	isEditing = false;
        placingNow = Element.createTypedInstance(id);
        System.out.println("Placing " + id);
        nextPointHandler = new NextPointHandler();
        handleNextPoint(x, y, false);
        add(placingNow);
        handleNextPoint(x, y, true);
    }
    
    public void edit(Element e, int step) {
    	Element[] elements = getElementsAsArray();
    	for (int i = 0; i < elements.length; i++) {
			if (elements[i] != null && elements[i].equals(e)) {
				edit(i, step);
				break;
			}
			
		}
    }
    
    public void edit(int i, int step) {
    	placingNow = getElementsAsArray()[i];
    	nextPointHandler = new NextPointHandler(step);
    	isEditing = true;
    }
    
    public void handleNextPoint(short x, short y, boolean isPreview) {
        if (placingNow == null) {
            return;
        }
        
        nextPointHandler.showingPreview = isPreview;
        
        nextPointHandler.handleNextPoint(x, y);
        
        if (nextPointHandler.step == placingNow.getStepsToPlace() || isEditing) {
            if (!isPreview) {
            	if (placingNow.getID() != Element.END_POINT) {
            		recalcEndPoint();
            	}
                placingNow = null;
                nextPointHandler = null;
                onUpdate();
            }
        }
    }
    
    public void add(Element element) {
        if (element == null) {
            return;
        }
        
        if (element instanceof EndPoint) {
            return;
        }
        
        buffer.addElement(element);
    }
    
    public short[] asShortArray() {
    	int carriage = 0;
        // {file format version, count of elements, ...data..., eof mark}
        short[] data = new short[1 + 1 + getDataLengthInShorts() + 1];
        
        data[carriage] = 1;
        carriage++;
        data[carriage] = (short) getElementsCount();
        carriage++;
        
        for (int i = 0; i < getElementsCount(); i++) {
            Element element = (Element) buffer.elementAt(i);
            
            short[] elementArgs = element.getAsShortArray();
            for (int j = 0; j < elementArgs.length; j++) {
                data[carriage] = elementArgs[j];
                carriage++;
            }
        }
        
        data[carriage] = 0;
        return data;
    }

    public short[][] asShortArrays() {
        short[][] data = new short[getElementsCount()][];
        for (int i = 0; i < data.length; i++) {
            data[i] = ((Element) buffer.elementAt(i)).getAsShortArray();
        }
        return data;
    }
    
    public void saveToFile(String path) throws IOException, SecurityException {
        FileUtils.saveShortArrayToFile(asShortArray(), path);
    }
    
    public void loadFile(String path) {
        try {
            Element[] elements = MGStructs.readMGStruct(path);
            if (elements == null) {
                System.out.println("error: elements array is null");
                return;
            }
            setElements(elements);
            this.path = path;
            onUpdate();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getFilePath() {
        return path;
    }

    public void setElements(Element[] elements) {
    	buffer = new Vector();
    	for (int i = 0; i < elements.length; i++) {
            if (elements[i] != null) {
                buffer.addElement(elements[i]);
            } else {
                System.out.println("elements["+i+"] is null. skipping");
            }
        }
        onUpdate();
    }
    
    public void remove(int i) {
        if (buffer.elementAt(i) instanceof EndPoint || buffer.elementAt(i) instanceof LevelStart) {
            return;
        }
        
        boolean needToRecalcEndPoint = true;
//        try {
//            (EndPoint) buffer.elementAt(0)).getArgs()
//            needToRecalcEndPoint = ( == ((Element) buffer.elementAt(i)).getEndPoint();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        System.out.println("needToRecalcEndPoint=" + needToRecalcEndPoint);
        buffer.removeElementAt(i);
        if (needToRecalcEndPoint) {
            recalcEndPoint();
        }
        onUpdate();
    }
    
    public void remove(Element e) {
    	Element[] elements = getElementsAsArray();
    	for (int i = 0; i < elements.length; i++) {
			if (elements[i] != null && elements[i].equals(e)) {
				remove(i);
				break;
			}
			
		}
    }
    
    public void recalcEndPoint() {
    	if (mode == MODE_LEVEL) {
    		return;
    	}

    	Element[] elements = getElementsAsArray();
        EndPoint endPoint = (EndPoint) elements[0];
        endPoint.setArgs(EndPoint.findEndPoint(elements));
    }
    
    public Vector getElements() {
        return buffer;
    }
    
    public int getElementsCount() {
        return buffer.size();
    }
    
    public Element[] getElementsAsArray() {
        Element[] elements = new Element[getElementsCount()];
        for (int i = 0; i < getElementsCount(); i++) {
            elements[i] = (Element) buffer.elementAt(i);
        }
        return elements;
    }
    
    public int getDataLengthInShorts() {
        int l = 0;
        for (int i = 0; i < getElementsCount(); i++) {
            l += 1/*id*/ + ((Element) buffer.elementAt(i)).getArgsCount()/*args*/;
        }
        return l;
    }
    
    public String getPlacingInfo() {
    	if (nextPointHandler != null && placingNow != null) {
    		return nextPointHandler.getCurrentPlacementStep().getCurrentStepInfo();
    	} else {
    		return "";
    	}
    }
    
    public int getMode() {
    	return mode;
    }
    
    public abstract void onUpdate();
    
    private class NextPointHandler {
        public int step = 0;
        public boolean showingPreview = false;
        
        public NextPointHandler(int step) {
        	this.step = step;
		}
        
        public NextPointHandler() {
        	this(0);
		}
        
        void handleNextPoint(short x, short y) {
            try {
                getCurrentPlacementStep().place(x, y);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (!showingPreview) {
                step++;
            }
        }
        
        public PlacementStep getCurrentPlacementStep() {
        	return placingNow.getAllSteps()[step];
        }
        
    }
}
