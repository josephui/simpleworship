/**
 * This file is part of SimpleWorship.
 * Copyright (C) 2016 Joseph Hui
 * 
 * SimpleWorship is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SimpleWorship is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SimpleWorship.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gmail.josephui.simpleworship2.display;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
      BottomPanel.getInstance().enableButtons();
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

    if (bottomPreviewPanel != null) {
      bottomPreviewPanel.showCurrentPreviewPanel();
    }
  }
  
  public void repaintPreviewPanels () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    MainTabbedPane.getInstance().repaint();
      
    if (bottomPreviewPanel != null) {
      bottomPreviewPanel.repaint();
    }
  }
  
  public boolean arePanelsEmpty () {
    return topTabbedPane == null && bottomTabbedPane == null;
  }
}
