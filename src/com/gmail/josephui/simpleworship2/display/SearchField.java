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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class SearchField extends GhostTextField {
  private static final String GHOST_TEXT = "Search Lyrics by Title or Content";
  
  private static final SearchField instance;
  
  static {
    instance = new SearchField();
  }
  
  public static SearchField getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  private final SearchResultPanel searchResultPanel;
  
  private SearchField () {
    super(GHOST_TEXT);
    
    searchResultPanel = SearchResultPanel.getInstance();
    
    // Toggling of the Search Results
    addFocusListener(new FocusListener () {
      @Override
      public void focusGained(FocusEvent fe) {
        if (!SwingUtilities.isEventDispatchThread()) {
          throw new RuntimeException ("Not invoked from eventDispatchThread");
        }
        
        MainFrame.getInstance().resetGlassPane();
        SearchResultPanel.getInstance().setVisible(true);
      }

      @Override
      public void focusLost(FocusEvent fe) {
        if (!SwingUtilities.isEventDispatchThread()) {
          throw new RuntimeException ("Not invoked from eventDispatchThread");
        }
        
        SearchResultPanel.getInstance().setVisible(false);
      }
    });
    
    getDocument().addDocumentListener(new DocumentListener() {
      
      @Override
      public void insertUpdate(DocumentEvent e) {
        searchResultPanel.updateLyricsList();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        searchResultPanel.updateLyricsList();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        searchResultPanel.updateLyricsList();
      }
    });
    
    // Scrolling through the Search Results
    addKeyListener(new KeyAdapter () {
      @Override
      public void keyReleased(KeyEvent e) {
        if (!SwingUtilities.isEventDispatchThread()) {
          throw new RuntimeException ("Not invoked from eventDispatchThread");
        }
        
        switch (e.getKeyCode()) {
          case KeyEvent.VK_UP:
            searchResultPanel.scrollUp();
            break;
          case KeyEvent.VK_DOWN:
            searchResultPanel.scrollDown();
            break;
        }
      }
    });
    
    // Opening the Lyrics on the current index in the Search Results
    addActionListener(new ActionListener () {
      @Override
      public void actionPerformed(ActionEvent ae) {
        if (!SwingUtilities.isEventDispatchThread()) {
          throw new RuntimeException ("Not invoked from eventDispatchThread");
        }
        
        searchResultPanel.openSelectedLyrics();
      }
    });
  }
  
  public void resetTextAndTransferFocus () {
    setText("");
    transferFocus();
  }
}