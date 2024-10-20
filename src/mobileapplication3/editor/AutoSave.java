package mobileapplication3.editor;

import mobileapplication3.editor.elements.Element;
import mobileapplication3.platform.Platform;
import mobileapplication3.ui.AbstractPopupPage;
import mobileapplication3.ui.Button;
import mobileapplication3.ui.IPopupFeedback;
import mobileapplication3.ui.IUIComponent;

public abstract class AutoSave extends AbstractPopupPage {
	private final static String STORE_NAME_STRUCTURE_AUTOSAVE = "StructureAutoSave";
	private final static String STORE_NAME_LEVEL_AUTOSAVE = "LevelAutoSave";

	public final static int STRUCTURE = EditorUI.MODE_STRUCTURE;
	public final static int LEVEL = EditorUI.MODE_LEVEL;

	private Element[] elements;

	public AutoSave(IPopupFeedback parent, Element[] elements) {
		super("Some unsaved data can be recovered", parent);
		this.elements = elements;
	}
	
	public void init() {
		super.init();
        actionButtons.setIsSelectionEnabled(true);
        actionButtons.setIsSelectionVisible(true);
	}

	protected Button[] getActionButtons() {
		return new Button[] {
			new Button("Restore") {
				public void buttonPressed() {
					onRestore();
				}
			},
			new Button("Delete") {
				public void buttonPressed() {
					onDelete();
				}
			}.setBgColor(0x550000)
		};
	}

	protected IUIComponent initAndGetPageContent() {
		return new StructureViewerComponent(elements);
	}
	
	public abstract void onRestore();
	public abstract void onDelete();
	
	public static void autoSaveWrite(StructureBuilder data, int autoSaveID) throws Exception {
		switch (autoSaveID) {
			case STRUCTURE:
				Platform.storeShorts(data.asShortArray(), STORE_NAME_STRUCTURE_AUTOSAVE);
				break;
			case LEVEL:
				Platform.storeShorts(data.asShortArray(), STORE_NAME_LEVEL_AUTOSAVE);
				break;
		}
	}

	public static Element[] autoSaveRead(int autoSaveID) {
        switch (autoSaveID) {
            case STRUCTURE:
                return MGStructs.readMGStruct(Platform.readStore(STORE_NAME_STRUCTURE_AUTOSAVE));
			case LEVEL:
                return MGStructs.readMGStruct(Platform.readStore(STORE_NAME_LEVEL_AUTOSAVE));
			default:
				return null;
        }
	}

	public static void deleteAutoSave(int autoSaveID) {
        switch (autoSaveID) {
            case STRUCTURE:
                Platform.clearStore(STORE_NAME_STRUCTURE_AUTOSAVE);
                break;
			case LEVEL:
                Platform.clearStore(STORE_NAME_LEVEL_AUTOSAVE);
                break;
        }
	}

}
