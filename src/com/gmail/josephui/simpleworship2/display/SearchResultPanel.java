package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.gmail.josephui.simpleworship2.models.Search;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.FocusManager;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class SearchResultPanel extends JPanel{
  private static final SearchResultPanel instance;
  
  static {
    instance = new SearchResultPanel();
  }
  
  public static SearchResultPanel getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  /*
  
          
    addMouseListener(new MouseAdapter () {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (SearchField.getInstance().isFocusOwner()) {
            SearchField.getInstance().transferFocus();
        }
      }
    });
  */
  private final JList lyricsList;
  private final JScrollPane scrollPane;
  
  private SearchResultPanel () {
    setLayout(null);
    
    add(scrollPane = new JScrollPane(lyricsList  = new JList() {
      {
        addMouseListener(new MouseAdapter () {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (e.getComponent() instanceof JList) {
                JList list = (JList)e.getComponent();
                int i = list.locationToIndex(e.getPoint());
                list.setSelectedIndex(i);
                
                openSelectedLyrics();
            }
            /*
            e.translatePoint(getX(), getY());

            for (MouseListener listener : instance.getMouseListeners()) {
              listener.mouseClicked(e);
            }*/
          }
        });

        setCellRenderer(new DefaultListCellRenderer() {
          {
            setOpaque(false);
          }
          
          @Override
          public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String s = value.toString();
            setText(s);
            
            if (isSelected) {
                setBackground(Color.LIGHT_GRAY);
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.GRAY);
                setForeground(Color.LIGHT_GRAY);
            }
            setHorizontalAlignment(CENTER);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(false);
            return this;
          }

          @Override
          protected void paintComponent(Graphics g) {
            g.setColor(new Color(0, 0, 0, 128));

            Insets insets = getInsets();
            g.fillRect(
              insets.left,
              insets.top,
              getWidth() - insets.left - insets.right, 
              getHeight() - insets.top - insets.bottom
            );

            super.paintComponent(g);
          }
        });

        setOpaque(false);
      }
    }) {{
        setBorder(null);
        setOpaque(false);
        
        getViewport().setOpaque(false);
    }});
    
    SearchField.getInstance().getDocument().addDocumentListener(new DocumentListener() {
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
    
    setOpaque(false);
  }
  
  public void openSelectedLyrics () {
        SearchField.getInstance().setText("");
        SearchField.getInstance().transferFocus();
        
        LyricsPreviewPanel panel = MainTabbedPane.getInstance().addLyrics(getCurrentlySelectedLyrics());
        
        MainTabbedPane.getInstance().setSelectedComponent(panel);
  }
  
  @Override
  public void addMouseListener(MouseListener listener) {
      //lyricsList.addMouseListener(listener);
      super.addMouseListener(listener);
  }
  
  @Override
  public void invalidate () {
    updateLyricsList();
    
    super.invalidate();
  }
  
  public void scrollUp () {
    int i = lyricsList.getSelectedIndex() - 1;
    if (i >= 0) {
      lyricsList.setSelectedIndex(i);
    }
  }
  
  public void scrollDown () {
    int i = lyricsList.getSelectedIndex() + 1;
    if (i < lyricsList.getModel().getSize()) {
      lyricsList.setSelectedIndex(i);
    }
  }
  
  public Lyrics getCurrentlySelectedLyrics () {
    return (Lyrics)lyricsList.getModel().getElementAt(lyricsList.getSelectedIndex());
  }
  
  private void updateLyricsList () {
    List<Lyrics> searchResults = Search.findLyrics(SearchField.getInstance().getText());
    Object[] searchResultsArray = searchResults.toArray();
    
    lyricsList.setListData(searchResultsArray);
    
    if (!searchResults.isEmpty()) {
      lyricsList.setSelectedIndex(0);
    }
    
    lyricsList.setVisibleRowCount(Math.max(searchResultsArray.length, 20));
    
    int widthBuffer = SearchField.getInstance().getWidth() / 20;
    scrollPane.setSize(SearchField.getInstance().getWidth() - 2*widthBuffer, scrollPane.getPreferredSize().height);
    scrollPane.setLocation(SearchField.getInstance().getX() + widthBuffer, SearchField.getInstance().getHeight());
  }
}

/**
 * Search shows Title in bold, 2nd line shows file path
 * results split by JSeperator
 * 
 * Search bar is a JTextField
 * Result is a JList flexsize under the textField on top of the rest of the program
 * 
 * paintComponent on glasspane
 */