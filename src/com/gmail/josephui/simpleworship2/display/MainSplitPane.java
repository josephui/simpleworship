package com.gmail.josephui.simpleworship2.display;

import com.stackoverflow.questions60269.DnDTabbedPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

/**
 *
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
 
  private MainTabbedPane top;
  
  private JTabbedPane bottomTabbedPane;
  private LyricsPreviewPanel bottom;
  
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
    if (top == null) {
      setTopComponent(top = pane);

      setDividerLocation(0.5d);
      setResizeWeight(0.5d);
    }
  }
  
  public void setBottomPanel (LyricsPreviewPanel panel, String panelName) {
    if (bottomTabbedPane == null) {
      setBottomComponent(bottomTabbedPane = new JTabbedPane () {{
        setBorder(null);
      }});
      OptionPanel.getInstance().enableButtons();
    } else {
      bottomTabbedPane.removeAll();
    }
    
    bottomTabbedPane.add(bottom = panel, panelName);
    
    setDividerLocation(0.5d);
    setResizeWeight(0.5d);
  }
  
  public void setRenderDevice (GraphicsDevice device) {
    if (bottom != null) {
      bottom.setRenderDevice(device);
    }
    
    if (top != null) {
      for (Component c : top.getComponents()) {
        if (c instanceof LyricsPreviewPanel) {
          LyricsPreviewPanel p = (LyricsPreviewPanel)c;
          p.setRenderDevice(device);
        }
      }
    }
  }
  
  /*
    private static LyricsPreviewPanel bottomPanel;
    
    public static void setBottomPanel (LyricsPreviewPanel panel) {
        bottomPanel = panel;
    }
    
    public static LyricsPreviewPanel getBottomPanel () {
        return bottomPanel;
    }
    
    private LyricsPreviewPanel topPanel;
    
    public MainSplitPane () {
        this(null, null);
    }
    
    public MainSplitPane (LyricsPreviewPanel topPanel) {
        this(topPanel, null);
    }
    
    public MainSplitPane (LyricsPreviewPanel top, LyricsPreviewPanel bottom) {
        super(VERTICAL_SPLIT, true);
        
        setBorder(null);
        setOneTouchExpandable(true);
        
        setTopPanel(top);
        
        if (bottom == null) {
            setBottomComponent(new JLabel());
        } else {
            setBottomPanel(bottom);
            setBottomComponent(bottomPanel);
        }
        
        addComponentListener(new ComponentAdapter () {
            @Override
            public void componentShown(ComponentEvent e) {
                if (bottomPanel != null && getBottomComponent() != bottomPanel) {
                    setBottomComponent(bottomPanel);
                }
            }
        });
        
        setDividerLocation(0.5d);
        setResizeWeight(0.5d);
    }
    
    @Override
    public void setBottomComponent (Component c) {
        // Make renderedWindow not visible;
        super.setBottomComponent(c);
        setDividerLocation(0.5d);
        setResizeWeight(0.5d);
        
        if (c instanceof LyricsPreviewPanel) {
            bottomPanel = (LyricsPreviewPanel)c;
        }
    }
    
    public void setTopPanel (LyricsPreviewPanel panel) {
        setTopComponent(topPanel = panel);
    }
    
    public LyricsPreviewPanel getTopPanel () {
        return topPanel;
    }*/
}
