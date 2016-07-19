package com.gmail.josephui.simpleworship2.display;

import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * @xToSelf Thread-safe
 * @author Joseph Hui <josephui@gmail.com>
 */
public class MainSplitPane extends JSplitPane {
  private static final MainSplitPane instance;
  
  static {
    instance = new MainSplitPane();
  }
  
  public static MainSplitPane getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
 
  private volatile MainTabbedPane topTabbedPane;
  private volatile JTabbedPane bottomTabbedPane;
  private LyricsPreviewPanel bottomPreviewPanel;
  
  private MainSplitPane () {
    super(VERTICAL_SPLIT, true);
    
    setBorder(null);
    setOneTouchExpandable(true);
    setTopComponent(new JLabel());
    setBottomComponent(new JLabel());
    
    setDividerLocation(0.5d);
    setResizeWeight(0.5d);
    
  }
  
  public void setTopPane (MainTabbedPane pane) {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    if (topTabbedPane == null) {
      setTopComponent(topTabbedPane = pane);

      setDividerLocation(0.5d);
      setResizeWeight(0.5d);
    }
  }
  
  public void setBottomPanel (LyricsPreviewPanel lyricsPreviewPanel, String panelName) {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    if (bottomTabbedPane == null) {
      setBottomComponent(bottomTabbedPane = new JTabbedPane () {{
        setBorder(null);
      }});
      OptionPanel.getInstance().enableButtons();
    } else {
      bottomTabbedPane.removeAll();
    }
    
    bottomTabbedPane.add(bottomPreviewPanel = lyricsPreviewPanel, panelName);
    
    setDividerLocation(0.5d);
    setResizeWeight(0.5d);
  }
  
  public void showCurrentPreviewPanel () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    bottomPreviewPanel.showCurrentPreviewPanel();
  }
}
