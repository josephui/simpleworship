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

/**
 * @xToSelf Thread-safe
 * @author Joseph Hui <josephui@gmail.com>
 */
public class SearchField extends JTextField {
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

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (getText().isEmpty()) {
      g.setColor(Color.GRAY);

      FontMetrics metrics   = g.getFontMetrics();
      Rectangle2D rectangle = metrics.getStringBounds(GHOST_TEXT, g);

      int x = (int)Math.round((getWidth() - rectangle.getWidth()) / 2);
      int y = (int)Math.round((getHeight() - rectangle.getHeight()) / 2) + metrics.getAscent();
      
      g.drawString(GHOST_TEXT, x, y);
    }
  }
  
  public void resetTextAndTransferFocus () {
    setText("");
    transferFocus();
  }
}