/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileapplication3.editor;

import mobileapplication3.platform.Platform;
import mobileapplication3.ui.AbstractPopupWindow;
import mobileapplication3.ui.Button;
import mobileapplication3.ui.ButtonCol;
import mobileapplication3.ui.IPopupFeedback;
import mobileapplication3.ui.IUIComponent;

/**
 *
 * @author vipaol
 */
public class Menu extends AbstractPopupWindow {

    public Menu(IPopupFeedback parent) {
        super("Menu", parent);
    }

    protected Button[] getActionButtons() {
        return new Button[] {
            new Button("Back") {
                public void buttonPressed() {
                    close();
                }
            }
        };
    }
    
    public void init() {
    	super.init();
    }

    protected IUIComponent initAndGetPageContent() {
        final Menu inst = this;
        Button[] settingsButtons = new Button[]{
            new Button("Settings") {
                public void buttonPressed() {
                    showPopup(new SettingsUI(inst));
                }
            },
            new Button("About") {
                public void buttonPressed() {
                    showPopup(new About(inst));
                }
            },
            new Button("Exit") {
                public void buttonPressed() {
                    Platform.exit();
                }
            }.setBgColor(0x550000)
        };
        
        ButtonCol settingsList = (ButtonCol) new ButtonCol()
                .enableScrolling(true, false)
                .enableAnimations(false)
                .trimHeight(true)
                .setButtons(settingsButtons);
        
        return settingsList;
    }
    
    public void setPageContentBounds(IUIComponent pageContent, int x0, int y0, int w, int h) {
        if (pageContent != null) {
            ((ButtonCol) pageContent)
                    .setButtonsBgPadding(margin/8)
                    .setSize(w - margin*2, h - margin*2)
                    .setPos(x0 + w/2, y0 + h - margin, BOTTOM | HCENTER);
        }
    }
    
}
