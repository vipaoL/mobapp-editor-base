/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileapplication3.editor;

import mobileapplication3.platform.ui.RootContainer;
import mobileapplication3.ui.AbstractPopupPage;
import mobileapplication3.ui.Button;
import mobileapplication3.ui.ButtonCol;
import mobileapplication3.ui.IUIComponent;

/**
 *
 * @author vipaol
 */
public class EditorQuickMenu extends AbstractPopupPage {
    EditorUI parent;

    public EditorQuickMenu(EditorUI parent) {
        super("Menu", parent);
        this.parent = parent;
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

    protected IUIComponent initAndGetPageContent() {
        ButtonCol buttons = (ButtonCol) new ButtonCol()
                .enableScrolling(true, false)
                .enableAnimations(false)
                .trimHeight(true)
                .setButtons(getButtons());
        
        return buttons;
    }

    private Button[] getButtons() {
        final EditorQuickMenu inst = this;
        boolean gameIncluded = false;
        try {
            Class.forName("mobileapplication3.game.GameplayCanvas");
            gameIncluded = true;
        } catch (ClassNotFoundException ignored) { }

        Button levelTestButton = new Button("Open this level in the game") {
            public void buttonPressed() {
                RootContainer.setRootUIComponent(new mobileapplication3.game.GameplayCanvas(parent).loadLevel(parent.getData()));
            }
        }.setIsActive(gameIncluded);

        Button settingsButton = new Button("Settings") {
            public void buttonPressed() {
                showPopup(new SettingsUI(inst));
            }
        };
        Button aboutButton = new Button("About") {
            public void buttonPressed() {
                showPopup(new About(inst));
            }
        };
        Button exitButton = new Button("Open Menu") {
            public void buttonPressed() {
                RootContainer.setRootUIComponent(new MainMenu(RootContainer.getInst()));
            }
        };

        Button[] buttons = null;
        if (parent.getMode() == EditorUI.MODE_STRUCTURE) {
            buttons = new Button[]{
                settingsButton,
                aboutButton,
                exitButton
            };
        } else if (parent.getMode() == EditorUI.MODE_LEVEL) {
            buttons = new Button[]{
                levelTestButton,
                settingsButton,
                aboutButton,
                exitButton
            };
        }
        return buttons;
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
