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
import com.gmail.josephui.simpleworship2.models.Search;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public final class SearchResultPanel extends JPanel {
  private static final SearchResultPanel instance;
  
  static {
    instance = new SearchResultPanel();
  }
  
  public static SearchResultPanel getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  private final JList<Lyrics> lyricsList;
  private final JScrollPane scrollPane;
  
  private SearchResultPanel () {
    setLayout(null);
    
    add(scrollPane = new JScrollPane(lyricsList  = new JList<Lyrics>() {{
      addMouseListener(new MouseAdapter () {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException ("Not invoked from eventDispatchThread");
          }
          
          if (e.getComponent() instanceof JList) {
            JList list = (JList)e.getComponent();
            int index = list.locationToIndex(e.getPoint());
            list.setSelectedIndex(index);

            openSelectedLyrics();
          }
        }
      });

      setCellRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
          setText(value.toString());

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
          g.setColor(new Color(0, 0, 0, 255*3/4));

          Insets insets = getInsets();
          
          int x = insets.left;
          int y = insets.top;
          int w = getWidth() - insets.left - insets.right;
          int h = getHeight() - insets.top - insets.bottom;
          
          g.fillRect(x, y, w, h);

          super.paintComponent(g);
        }
      });

      setOpaque(false);
    }}) {{
        setBorder(null);
        setOpaque(false);
        
        getViewport().setOpaque(false);
    }});
    
    setOpaque(false);
  }
  
  public void openSelectedLyrics () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    SearchField.getInstance().resetTextAndTransferFocus();
    
    final MainTabbedPane mainTabbedPane = MainTabbedPane.getInstance();
    final Lyrics currentlySelectedLyrics = lyricsList.getModel().getElementAt(lyricsList.getSelectedIndex());
    
    SwingUtilities.invokeLater(new Runnable () {
      @Override
      public void run() {
        LyricsPreviewPanel lyricsPreviewPanel = mainTabbedPane.addLyrics(currentlySelectedLyrics);
        mainTabbedPane.setSelectedComponent(lyricsPreviewPanel);
        MainSplitPane.getInstance().setTopPane(mainTabbedPane);
        MainFrame.getInstance().revalidate();
        MainFrame.getInstance().repaint();
      }
    });
  }
  
  public void scrollUp () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }
    
    int i = lyricsList.getSelectedIndex() - 1;
    if (i >= 0) {
      lyricsList.setSelectedIndex(i);
    }
  }
  
  public void scrollDown () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }
    
    int i = lyricsList.getSelectedIndex() + 1;
    if (i < lyricsList.getModel().getSize()) {
      lyricsList.setSelectedIndex(i);
    }
  }
  
  public void updateLyricsList () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }
    
    if (isVisible()) {
      List<Lyrics> searchResults = Search.findLyrics(SearchField.getInstance().getText());
      Lyrics[] searchResultsArray = searchResults.toArray(new Lyrics[searchResults.size()]);

      lyricsList.setListData(searchResultsArray);

      if (!searchResults.isEmpty()) {
        lyricsList.setSelectedIndex(0);
      }

      lyricsList.setVisibleRowCount(Math.min(searchResultsArray.length, 20));
    }
    
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
