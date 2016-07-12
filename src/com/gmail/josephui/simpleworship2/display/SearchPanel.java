package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.gmail.josephui.simpleworship2.models.Search;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.FocusManager;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class SearchPanel extends JPanel{
  private static final SearchPanel instance;
  
  static {
    instance = new SearchPanel();
  }
  
  public static SearchPanel getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  
  private final JTextField searchField;
  private final JList lyricsList;
  
  private SearchPanel () {
    setLayout(new BorderLayout());
    
    add(searchField = new JTextField() {
      {
        getDocument().addDocumentListener(new DocumentListener() {
          @Override
          public void insertUpdate(DocumentEvent e) {
            updateLyricsList();
          }

          @Override
          public void removeUpdate(DocumentEvent e) {
            updateLyricsList();
          }

          @Override
          public void changedUpdate(DocumentEvent e) {
            updateLyricsList();
          }
        });
      }
      
      static final String GHOST_TEXT = "Search Lyrics by Title or Content";
      
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
    }, BorderLayout.NORTH);
    
    add(lyricsList  = new JList() {{
    }}, BorderLayout.CENTER);
    
    updateLyricsList();
  }
  
  private void updateLyricsList () {
    List<Lyrics> searchResults = Search.findLyrics(searchField.getText());
    
    lyricsList.setListData(searchResults.toArray());
  }
}
