/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.gmail.josephui.simpleworship2.models.Search;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class SearchField extends JTextField{
  private static final SearchField instance;
      
  private static final String GHOST_TEXT = "Search Lyrics by Title or Content";
  
  static {
    instance = new SearchField();
  }
  
  public static SearchField getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  private SearchField () {
    addFocusListener(new FocusListener () {
      @Override
      public void focusGained(FocusEvent fe) {
        SearchResultPanel.getInstance().setVisible(true);
      }

      @Override
      public void focusLost(FocusEvent fe) {
        SearchResultPanel.getInstance().setVisible(false);
      }
    });
    
    addKeyListener(new KeyAdapter () {
      @Override
      public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_UP:
            SearchResultPanel.getInstance().scrollUp();
            break;
          case KeyEvent.VK_DOWN:
            SearchResultPanel.getInstance().scrollDown();
            break;
        }
      }
    });
    
    addActionListener(new ActionListener () {
      @Override
      public void actionPerformed(ActionEvent ae) {
      }
    });
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (getText().isEmpty()) {
      Graphics2D g2 = (Graphics2D)g.create();
      g2.setColor(Color.GRAY);

      FontMetrics metrics = g2.getFontMetrics();
      Rectangle2D rectangle = metrics.getStringBounds(GHOST_TEXT, g);

      int x = (int)Math.round((getWidth() - rectangle.getWidth()) / 2);
      int y = (int)Math.round((getHeight() - rectangle.getHeight()) / 2) + metrics.getAscent();

      int minWidth  = Math.max(getPreferredSize().width, (int)rectangle.getWidth());
      int minHeight = Math.max(getPreferredSize().height, (int)rectangle.getHeight());
      setPreferredSize(new Dimension(minWidth, minHeight));

      g2.drawString(GHOST_TEXT, x, y);
      g2.dispose();
    }
  }
}