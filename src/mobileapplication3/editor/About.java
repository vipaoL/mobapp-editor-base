/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileapplication3.editor;

import java.io.IOException;

import mobileapplication3.platform.Platform;
import mobileapplication3.platform.ui.Image;
import mobileapplication3.ui.AbstractPopupWindow;
import mobileapplication3.ui.Button;
import mobileapplication3.ui.ButtonCol;
import mobileapplication3.ui.Container;
import mobileapplication3.ui.IPopupFeedback;
import mobileapplication3.ui.IUIComponent;
import mobileapplication3.ui.ImageComponent;
import mobileapplication3.ui.TextComponent;
import mobileapplication3.ui.UIComponent;

/**
 *
 * @author vipaol
 */
public class About extends AbstractPopupWindow {
    private static final String LINK = "https://github.com/vipaoL/mobapp-editor";
    private static final String LINK_PREVIEW = "vipaoL/mobapp-editor";
    private static final String LINK2 = "https://t.me/mobapp_game";
    private static final String LINK2_PREVIEW = "@mobapp_game";

    public About(IPopupFeedback parent) {
        super("About", parent);
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
        Image logoImage = null;
        String errorMessage = null;

        try {
            logoImage = Image.createImage("/logo.png");
        } catch (IOException ex) {
            ex.printStackTrace();
            errorMessage = ex + " ";
            try {
                logoImage = Image.createImage("/icon.png");
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage += e;
            }
        }


        UIComponent logo;
        if (logoImage != null) {
            logo = new ImageComponent(logoImage);
            logo.setBgColor(COLOR_ACCENT_MUTED);
        } else {
            logo = new TextComponent("Here could be your ad. " + errorMessage);
            logo.setBgColor(COLOR_ACCENT_MUTED);
        }

        Button[] settingsButtons = new Button[]{
            new Button("Open GitHub " + LINK_PREVIEW) {
                public void buttonPressed() {
                    Platform.platformRequest(LINK);
                }
            }.setBgColor(COLOR_ACCENT),
            new Button("Open TG channel " + LINK2_PREVIEW) {
                public void buttonPressed() {
                    Platform.platformRequest(LINK2);
                }
            }.setBgColor(COLOR_ACCENT)
        };
        
        ButtonCol buttonsList = (ButtonCol) new ButtonCol()
                .enableScrolling(true, true)
                .enableAnimations(false)
                .trimHeight(true)
                .setButtons(settingsButtons);
        
        Container container = new Container() {
            @Override
            public void init() {
                setComponents(new IUIComponent[] {logo, buttonsList});
                setBgColor(COLOR_TRANSPARENT);
            }

            @Override
            protected void onSetBounds(int x0, int y0, int w, int h) {
                buttonsList.setButtonsBgPadding(margin/8).setSize(w, ButtonCol.H_AUTO).setPos(x0, y0 + h, BOTTOM | LEFT);
                int logoSide = Math.min(w, h - buttonsList.getHeight());
                logo.setSize(logoSide, logoSide).setPos(x0 + w/2, (buttonsList.getTopY() + y0) / 2, HCENTER | VCENTER);
            }
        };

        return container;
    }
    
}
