package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.Main;
import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.stackoverflow.questions60269.DnDTabbedPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class MainTabbedPane extends DnDTabbedPane{
  private static final MainTabbedPane instance;
  
  static {
      /*
    UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
    UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
    
    UIManager.getLookAndFeelDefaults().put("TabbedPane.background", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.borderHightlight", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.contentArea", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.darkShadow", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.focus", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.foreground", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.highlight", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.light", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.selected", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.selectedForeground", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.selectHighlight", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.shadow", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.tabAreaBackground", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.unselectedBackground", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.unselectedTabBackground", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.unselectedTabForeground", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.unselectedTabHighlight", java.awt.Color.CYAN);
UIManager.getLookAndFeelDefaults().put("TabbedPane.unselectedTabShadow", java.awt.Color.CYAN);
    
    UIManager.getLookAndFeelDefaults().put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", null);
    UIManager.getLookAndFeelDefaults().put("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", null);
    UIManager.getLookAndFeelDefaults().put("TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", null);
    UIManager.getLookAndFeelDefaults().put("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", null);
    */
    instance = new MainTabbedPane();
  }
  
  public static MainTabbedPane getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  private MainTabbedPane () {
      setBorder(null);
      setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
      
    super.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        Component c = getSelectedComponent();
        if (c instanceof LyricsPreviewPanel) {
          LyricsPreviewPanel pane = (LyricsPreviewPanel)c;
          pane.selectFirstSubsection();
          /*
          LyricsPreviewPanel bottomPanel = MainSplitPane.getBottomPanel();
          if (bottomPanel != null) {
              //bottomPanel.selectFirstSubsection();
          }*/
        }
      }
    });
  }
  
  public LyricsPreviewPanel addLyrics (Lyrics lyrics) {
    LyricsPreviewPanel panel = new LyricsPreviewPanel(lyrics, MainFrame.getFonts());
    panel.setDisplayTag("Preview", new Font("Tahoma", Font.BOLD, 12), new Color(0x007f00), Color.WHITE);
    panel.setRenderDevice(MainFrame.getSelectedGraphicsDevice());
    
    add(lyrics.getTitle(), panel);
    
    return panel;
  }
}
//myTabbedPane.getBoundsAt( myTabbedPane.getSelectedIndex() )
