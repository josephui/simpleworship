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
        }
      }
    });
  }
  
  public LyricsPreviewPanel addLyrics (Lyrics lyrics) {
    LyricsPreviewPanel panel = new LyricsPreviewPanel(lyrics, MainFrame.getFonts());
    add(lyrics.getTitle(), panel);
    
    return panel;
  }
}
