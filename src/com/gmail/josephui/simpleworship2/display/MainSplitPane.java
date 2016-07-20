package com.gmail.josephui.simpleworship2.display;

import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @xToSelf Thread-safe
 * @author Joseph Hui <josephui@gmail.com>
 */
public final class MainSplitPane extends JSplitPane {
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
  private final JLabel emptyLabel;
  
  private MainSplitPane () {
    super(VERTICAL_SPLIT, true);
    
    setBorder(null);
    setOneTouchExpandable(true);
    setTopComponent(emptyLabel = new JLabel());
    setBottomComponent(new JLabel());
    
    setDividerLocation(0.5d);
    setResizeWeight(0.5d);
  }
  
  private void refresh () {  
    setDividerLocation(0.5d);
    setResizeWeight(0.5d);
    
    revalidate();
    repaint();
  }
  
  public void setTopPane (MainTabbedPane pane) {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    if (topTabbedPane == null) {
      setTopComponent(topTabbedPane = pane);
      topTabbedPane.revalidate();
      topTabbedPane.repaint();
      
      topTabbedPane.addChangeListener(new ChangeListener () {
        @Override
        public void stateChanged(ChangeEvent e) {
          if(topTabbedPane.getTabCount() == 0) {
            topTabbedPane.removeChangeListener(this);
            
            remove(topTabbedPane);
            topTabbedPane = null;
            setTopComponent(emptyLabel);
            
            refresh();
          }
        }
      });

      refresh();
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
    bottomTabbedPane.revalidate();
    bottomTabbedPane.repaint();
    
    refresh();
  }
  
  public void showCurrentPreviewPanel () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    bottomPreviewPanel.showCurrentPreviewPanel();
  }
  
  public void repaintPreviewPanels () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

      MainTabbedPane.getInstance().repaint();
      bottomPreviewPanel.repaint();
  }
}
