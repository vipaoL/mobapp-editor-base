package mobileapplication3.editor;

import java.io.IOException;

import mobileapplication3.platform.FileUtils;
import mobileapplication3.platform.ui.Font;
import mobileapplication3.ui.AbstractPopupWindow;
import mobileapplication3.ui.BackButton;
import mobileapplication3.ui.Button;
import mobileapplication3.ui.ButtonCol;
import mobileapplication3.ui.ButtonComponent;
import mobileapplication3.ui.ButtonRow;
import mobileapplication3.ui.ButtonStub;
import mobileapplication3.ui.Grid;
import mobileapplication3.ui.IPopupFeedback;
import mobileapplication3.ui.IUIComponent;
import mobileapplication3.ui.Keys;
import mobileapplication3.ui.Switch;
import mobileapplication3.ui.TextComponent;
import mobileapplication3.ui.UIComponent;

public abstract class AbstractEditorMenu extends AbstractPopupWindow {
	
	private final static int LAYOUT_MINIMIZED = 1, LAYOUT_LIST_OF_NAMES = 2, LAYOUT_GRID = 3;

	private int layout = LAYOUT_MINIMIZED;
	private TextComponent title;
	private ButtonCol buttons;
	private Grid grid = null;
	private ButtonRow backButtonComponent;
	String[] files = null;
	private final IPopupFeedback parent;

	public AbstractEditorMenu(final IPopupFeedback parent, String titleStr) {
		super(parent);
        this.parent = parent;

		title = new TextComponent(titleStr);
		//title.setFontSize(Font.SIZE_LARGE);
		buttons = new ButtonCol();

		switch (EditorSettings.getWhatToLoadAutomatically()) {
			case EditorSettings.OPTION_ALWAYS_LOAD_NONE:
				setLayout(LAYOUT_MINIMIZED);
				break;
			case EditorSettings.OPTION_ALWAYS_LOAD_LIST:
				setLayout(LAYOUT_LIST_OF_NAMES);
				break;
			case EditorSettings.OPTION_ALWAYS_LOAD_THUMBNAILS:
				setLayout(LAYOUT_GRID);
				break;
		}
	}

	public void setLayout(int layout) {
		this.layout = layout;

		final Button createButton = new Button("Create new") {
			public void buttonPressed() {
				createNew();
			}
		}.setBindedKeyCode(Keys.KEY_NUM1);

		final Switch alwaysShowListSwitch = new Switch("Always show list") {
			public void setValue(boolean value) {
				EditorSettings.setWhatToLoadAutomatically(value ? EditorSettings.OPTION_ALWAYS_LOAD_LIST : EditorSettings.OPTION_ALWAYS_LOAD_NONE);
			}

			public boolean getValue() {
				return EditorSettings.getWhatToLoadAutomatically() >= EditorSettings.OPTION_ALWAYS_LOAD_LIST;
			}
		};

		final Switch alwaysShowGridSwitch = new Switch("Always show thumbnails") {
			public void setValue(boolean value) {
				EditorSettings.setWhatToLoadAutomatically(value ? EditorSettings.OPTION_ALWAYS_LOAD_THUMBNAILS : EditorSettings.OPTION_ALWAYS_LOAD_LIST);
			}

			public boolean getValue() {
				return EditorSettings.getWhatToLoadAutomatically() >= EditorSettings.OPTION_ALWAYS_LOAD_THUMBNAILS;
			}
		};

		final Button showGridButton = new Button("Show thumbnails") {
			public void buttonPressed() {
				setLayout(LAYOUT_GRID);
			}
		};

		final BackButton backButton = new BackButton(parent);

		switch (layout) {
			case LAYOUT_MINIMIZED:
				buttons.setButtons(new Button[] {
						createButton,
						new Button("Open") {
							public void buttonPressed() {
								setLayout(LAYOUT_LIST_OF_NAMES);
							}
						},
						new ButtonStub(),
						new BackButton(parent)
				});
				buttons.enableScrolling(true, false).setIsSelectionEnabled(true);
				setComponents(new IUIComponent[]{title, buttons});
				break;
			case LAYOUT_LIST_OF_NAMES:
				Button[] fileButtons = getList();
				int topExtraButtons = 3;
				int bottomExtraButtons = 1;
				Button[] btns = new Button[topExtraButtons + fileButtons.length + bottomExtraButtons];
				btns[0] = createButton;
				btns[1] = alwaysShowListSwitch;
				btns[2] = showGridButton;
				System.arraycopy(fileButtons, 0, btns, topExtraButtons, fileButtons.length);
				btns[btns.length - 1] = backButton;
				buttons.setButtons(btns);
				setComponents(new IUIComponent[]{title, buttons});
				break;
			case LAYOUT_GRID:
				UIComponent[] thumbnails = getGridContent();
				int topExtraCells = 2;
				UIComponent[] cells = new UIComponent[topExtraCells + thumbnails.length];
				cells[0] = new ButtonComponent(createButton);
				cells[1] = new ButtonComponent(alwaysShowGridSwitch);
				for (int i = 0; i < thumbnails.length; i++) {
					cells[topExtraCells + i] = thumbnails[i];
				}
				grid = new Grid(cells);
				backButtonComponent = new ButtonRow(new Button[]{backButton}).bindToSoftButtons();
				setComponents(new IUIComponent[]{title, grid, backButtonComponent});
				break;
		}
	}

	protected void onSetBounds(int x0, int y0, int w, int h) {
		title
		        .setSize(w, TextComponent.HEIGHT_AUTO)
		        .setPos(x0, y0, TOP | LEFT);
//		buttons
//				.setIsSelectionEnabled(true)
//		        .setButtonsBgPadding(w/128)
//		        .setSize(w/2, AbstractButtonSet.H_AUTO)
//		        .setPos(x0 + w/2, (title.getBottomY() + y0 + h) * 2 / 3, VCENTER | HCENTER);
		switch (layout) {
			case LAYOUT_MINIMIZED:
			    buttons
			    		.trimHeight(true)
			    		.setIsSelectionEnabled(true)
			            .setButtonsBgPadding(w/128)
			            .setSize(w/2, (h - title.getBottomY()))
			            .setPos(x0 + w/2, y0 + h, BOTTOM | HCENTER);
			    break;
			case LAYOUT_LIST_OF_NAMES:
			    buttons
			    		.setIsSelectionEnabled(true)
			            .setButtonsBgPadding(w/128)
			            .setSize(w, h - title.getBottomY())
			            .setPos(x0 + w/2, y0 + h, BOTTOM | HCENTER);
				break;
			case LAYOUT_GRID:
				backButtonComponent
						.setSize(w, ButtonRow.H_AUTO)
						.setPos(x0 + w/2, y0 + h, HCENTER | BOTTOM);
				grid
						.setCols(Math.max(1,w/Font.getDefaultFontHeight()/10))//.setCols(3)
						.setElementsPadding(w/128)
						.setSize(w, backButtonComponent.getTopY() - title.getBottomY())
						.setPos(x0 + w/2, title.getBottomY(), HCENTER | TOP);
				break;
			default:
				break;
		}
        
	}
	
	protected String[] listFiles(String path) throws IOException {
		if (files == null) {
			files = FileUtils.list(path);
		}
		return files;
	}
	
	protected abstract Button[] getList();
	protected abstract UIComponent[] getGridContent();
	protected abstract void createNew();
}