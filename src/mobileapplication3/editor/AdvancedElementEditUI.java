package mobileapplication3.editor;

import mobileapplication3.editor.elements.Element;
import mobileapplication3.platform.Property;
import mobileapplication3.ui.AbstractPopupPage;
import mobileapplication3.ui.Button;
import mobileapplication3.ui.ButtonComponent;
import mobileapplication3.ui.IPopupFeedback;
import mobileapplication3.ui.IUIComponent;
import mobileapplication3.ui.List;
import mobileapplication3.ui.Slider;

public class AdvancedElementEditUI extends AbstractPopupPage {
	
	private Element element;
	private IUIComponent[] rows;
	private List list;
	private StructureBuilder sb;

	public AdvancedElementEditUI(Element element, StructureBuilder sb, IPopupFeedback parent) {
		super("Advanced edit: " + element.getName(), parent);
		this.element = element;
		this.sb = sb;
	}

	protected Button[] getActionButtons() {
		final short[] argsUnmodified = element.getArgsValues();
        return new Button[] {
            new Button("OK") {
                public void buttonPressed() {
                	element.recalcCalculatedArgs();
                	if (element.getID() != Element.END_POINT) {
                		sb.recalcEndPoint();
                	}
                    close();
                }
            },
            new Button("Cancel") {
                public void buttonPressed() {
                	element.setArgs(argsUnmodified);
                    close();
                }
            }
        };
    }

	protected IUIComponent initAndGetPageContent() {
		list = new List() {
			public final void onSetBounds(int x0, int y0, int w, int h) {
				setElementsPadding(getElemH()/16);
				super.onSetBounds(x0, y0, w, h);
			}
		};
		
		refreshList();
		
		list.enableScrolling(true, false)
            .trimHeight(true)
            .setIsSelectionEnabled(true)
            .setIsSelectionVisible(true);
		return list;
	}
	
	private void refreshList() {
		Property[] args = element.getArgs();
		rows = new IUIComponent[args.length + 1];
		for (int i = 0; i < args.length; i++) {
			rows[i] = new Slider(args[i]);
		}
		rows[args.length] = new ButtonComponent(new Button ("Refresh this list") {
			public void buttonPressed() {
				element.recalcCalculatedArgs();
				refreshList();
			}
		});
		list.setElements(rows);
	}

}
