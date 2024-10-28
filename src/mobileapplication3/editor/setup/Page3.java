package mobileapplication3.editor.setup;

import mobileapplication3.ui.Button;
import mobileapplication3.ui.IUIComponent;
import mobileapplication3.ui.TextComponent;

/**
 *
 * @author vipaol
 */
public class Page3 extends AbstractSetupWizardPage {
    
    private TextComponent text = new TextComponent();
    
    public Page3(Button[] buttons, SetupWizard.Feedback feedback) {
        super("What are levels?", buttons, feedback);
    }
    
    public void init() {
    	super.init();
    	actionButtons.setSelected(actionButtons.getButtonCount() - 1);
    }

    public void initOnFirstShow() {
        this.text.setText(
                "aaaaaaaaaaa"
                + "aaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaa"
                + "aaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaa.");
    }

    protected IUIComponent initAndGetPageContent() {
        return text;
    }
    
}
