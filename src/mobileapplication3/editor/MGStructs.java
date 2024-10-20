package mobileapplication3.editor;

import java.io.DataInputStream;
import java.io.IOException;

import mobileapplication3.editor.elements.Element;
import mobileapplication3.platform.FileUtils;
import mobileapplication3.platform.Logger;
import mobileapplication3.platform.Utils;

public class MGStructs {
	public static Element[] readMGStruct(String path) {
        return readMGStruct(FileUtils.fileToDataInputStream(path));
    }
    
    public static Element[] readMGStruct(DataInputStream dis) {
        try {
            short fileVer = dis.readShort();
            Logger.log("mgstruct v" + fileVer);
            short elementsCount = dis.readShort();
            Logger.log("elements count: " + elementsCount);
            Element[] elements = new Element[elementsCount];
            for (int i = 0; i < elementsCount; i++) {
                elements[i] = readNextElement(dis);
                if (elements[i] == null) {
                    Logger.log("got null. stopping read");
                    return elements;
                }
            }
            return elements;
        } catch (NullPointerException ex) {
        	Logger.log("readMGStruct: caught NPE. nothing to read");
        	return null;
        } catch (IOException ex) {
            Logger.log(ex);
            return null;
        }
    }
    
    public static Element readNextElement(DataInputStream is) {
        Logger.log("reading next element...");
        try {
            short id = is.readShort();
            System.out.print("id" + id + " ");
            if (id == 0) {
                Logger.log("id0 is EOF mark. stopping");
                return null;
            }
            Element element = Element.createTypedInstance(id);
            if (element == null) {
                return null;
            }
            
            int argsCount = element.getArgsCount();
            short[] args = new short[argsCount];
            for (int i = 0; i < argsCount; i++) {
                args[i] = is.readShort();
            }
            
            Logger.log(Utils.shortArrayToString(args));
            
            element.setArgs(args);
            return element;
        } catch (IOException ex) {
            Logger.log(ex);
            return null;
        }
    }
}
