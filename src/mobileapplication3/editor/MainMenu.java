package mobileapplication3.editor;

import mobileapplication3.editor.elements.Element;
import mobileapplication3.platform.ui.RootContainer;
import mobileapplication3.ui.AbstractButtonSet;
import mobileapplication3.ui.BackButton;
import mobileapplication3.ui.Button;
import mobileapplication3.ui.ButtonCol;
import mobileapplication3.ui.Container;
import mobileapplication3.ui.IPopupFeedback;
import mobileapplication3.ui.IUIComponent;
import mobileapplication3.ui.TextComponent;
import mobileapplication3.ui.UIComponent;

public class MainMenu extends Container {
	private TextComponent title;
	private UIComponent logo;
	private ButtonCol buttons;
	private boolean isAutoSaveEnabled = true;
	private static boolean autoSaveCheckDone = false;

	public void init() {
		isAutoSaveEnabled = EditorSettings.getAutoSaveEnabled(true);
		super.init();
		checkAutoSaveStorage();
	}

	public MainMenu(IPopupFeedback parent) {
		final MainMenu inst = this;
		title = new TextComponent("Mobapp Editor");
		logo = About.getAppLogo();
		buttons = new ButtonCol(new Button[] {
				new Button("Structure editor") {
					public void buttonPressed() {
						showPopup(new StructureEditorMenu(inst));
					}
				},
				new Button("Level editor") {
					public void buttonPressed() {
						showPopup(new LevelEditorMenu(inst));
					}
				},
				new Button("Settings") {
					public void buttonPressed() {
						showPopup(new SettingsUI(inst));
					}
				},
				new BackButton(parent)
		});
		setComponents(new IUIComponent[]{title, logo, buttons});
	}

	protected void onSetBounds(int x0, int y0, int w, int h) {
		int margin = w/16;
        title
                .setSize(w, TextComponent.HEIGHT_AUTO)
                .setPos(x0, y0, TOP | LEFT);
        buttons
				.setIsSelectionEnabled(true)
		        .setButtonsBgPadding(w/128)
		        .setSize(w/2, AbstractButtonSet.H_AUTO)
		        .setPos(x0 + w/2, y0 + h - margin, BOTTOM | HCENTER);
        
        int logoSide = Math.min((buttons.getWidth() + w) / 2, buttons.getTopY() - title.getBottomY() - margin);
        if (logoSide < 1) {
        	logoSide = 1;
        }
        logo
        		.setSize(logoSide, logoSide)
        		.setPos(x0 + w/2, (buttons.getTopY() + title.getBottomY()) / 2, VCENTER | HCENTER);
	}

	private void checkAutoSaveStorage() {
		if (!isAutoSaveEnabled || autoSaveCheckDone) {
			return;
		}

		try {
			Element[] elements = AutoSave.autoSaveRead(AutoSave.STRUCTURE);
			int id = AutoSave.STRUCTURE;
			if (elements == null) {
				elements = AutoSave.autoSaveRead(AutoSave.LEVEL);
				id = AutoSave.LEVEL;
			}
			if (elements != null && elements.length > 2) {
				final Element[] finalElements = elements;
				final int finalId = id;
				showPopup(new AutoSave(this, finalElements) {
					public void onRestore() {
						close();
						RootContainer.setRootUIComponent(new EditorUI(finalId, finalElements, null));
					};

					public void onDelete() {
						AutoSave.deleteAutoSave(finalId);
						close();
					};
				});
			}
		} catch (Exception ignored) { }

		autoSaveCheckDone = true;
	}
}
