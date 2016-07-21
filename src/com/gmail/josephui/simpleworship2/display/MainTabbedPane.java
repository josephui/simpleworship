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

import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.stackoverflow.questions60269.DnDTabbedPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public final class MainTabbedPane extends DnDTabbedPane{
  private static final Color SELECTED_TAB_FOREGROUND = UIManager.getLookAndFeelDefaults().getColor("TabbedPane.selectedTabTitleNormalColor");
  
  private static final MainTabbedPane instance;
  private static volatile LyricsPreviewPanel currentPanel;
  
  static {
    System.out.println("SELECTED_TAB_FOREGROUND: " + SELECTED_TAB_FOREGROUND);
    instance = new MainTabbedPane();
  }
  
  public static MainTabbedPane getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  private MainTabbedPane () {
    setBorder(null);
    setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
      
    addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        Component c = getSelectedComponent();
        if (c instanceof LyricsPreviewPanel) {
          // Set tab in this index back to normal
          int oldIndex = indexOfComponent(currentPanel);
          if (oldIndex != -1) {
            Component c2 = getTabComponentAt(oldIndex);
            if (c2 instanceof ClosableTabPanel) {
              ClosableTabPanel p = (ClosableTabPanel)c2;
              p.setAsSelected(false);
            }
          }
          
          currentPanel = (LyricsPreviewPanel)c;
          currentPanel.selectFirstSubsection();
          
          int newIndex = indexOfComponent(currentPanel);
          if (newIndex != -1) {
            Component c2 = getTabComponentAt(newIndex);
            if (c2 instanceof ClosableTabPanel) {
              ClosableTabPanel p = (ClosableTabPanel)c2;
              p.setAsSelected(true);
            }
          }
        }
      }
    });
  }
  
  public LyricsPreviewPanel addLyrics (Lyrics lyrics) {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    LyricsPreviewPanel panel = new LyricsPreviewPanel(lyrics, MainFrame.getFonts());
    insertTab(lyrics.getTitle(), null, panel, null, getTabCount());
    
    return panel;
  }
  
  @Override
  public void insertTab (String title, Icon icon, Component component, String tip, int index) {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }
    
    super.insertTab(title, icon, component, tip, index);
    
    Color foreground = UIManager.getLookAndFeelDefaults().getColor("TabbedPane.nonSelectedTabTitleNormalColor");
    System.out.println("foreground: " + foreground);
    setTabComponentAt(index, new ClosableTabPanel(foreground));
  }
  
  class ClosableTabPanel extends JPanel {
    final Color foreground;
    final JLabel label;
    final ClosableTabButton button;
    ClosableTabPanel (final Color foreground) {
      setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      setOpaque(false);
      
      this.foreground = foreground;
      
      add(label = new JLabel () {
        @Override
        public String getText () {
          int index = MainTabbedPane.this.indexOfTabComponent(ClosableTabPanel.this);
          
          if (index != -1) {
            return getTitleAt(index);
          }
          
          return null;
        }
      });
      
      add(button = new ClosableTabButton (foreground));
      
      setAsSelected(true);
    }
    
    void setAsSelected (boolean isSelected) {
      Color c = (isSelected) ? SELECTED_TAB_FOREGROUND : foreground;
      
      label.setForeground(c);
      button.setForeground(c);
    }
    
    class ClosableTabButton extends JButton {
      ClosableTabButton(Color foreground) {
        setText(" \u00D7 ");                                                                                           
        setHorizontalTextPosition(CENTER);                                                                             
        setVerticalTextPosition(CENTER);                                                                               
        setHorizontalAlignment(CENTER);                                                                                
        setVerticalAlignment(CENTER);                                                                                  
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,3,0,0), BorderFactory.createEtchedBorder()));                                                                                                           
        setBorderPainted(false);                                                                                       
        setFocusable(false);                                                                                           
        setRolloverEnabled(false);                                                                                     
        setContentAreaFilled(false);                                                                                   
        setFocusPainted(false);      
        
        addMouseListener(new MouseAdapter () {
          @Override
          public void mouseEntered(MouseEvent e) {
            Component c = e.getComponent();
            if (c instanceof ClosableTabButton) {
              ClosableTabButton button = (ClosableTabButton)c;
              button.setBorderPainted(true);
            }
          }

          @Override
          public void mouseExited(MouseEvent e) {
            Component c = e.getComponent();
            if (c instanceof ClosableTabButton) {
              ClosableTabButton button = (ClosableTabButton)c;
              button.setBorderPainted(false);
            }
          }
        });
        
        addActionListener(new ActionListener () {
          @Override
          public void actionPerformed(ActionEvent e) {
            int index = MainTabbedPane.this.indexOfTabComponent(ClosableTabPanel.this);
            System.out.println(index);
            if (index != -1) {
              MainTabbedPane.this.remove(index);
            }
          }
        });
      }
    }
  }
}
