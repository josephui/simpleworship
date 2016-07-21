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

import com.gmail.josephui.simpleworship2.Constants;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class AboutPanel extends JPanel {
  private static final AboutPanel instance;
  
  static {
    instance = new AboutPanel();
  }
  
  public static AboutPanel getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  private AboutPanel () {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
    add(new HyperlinkLabel(Constants.SOFTWARE_NAME + " " + Constants.SOFTWARE_VERSION, null));
    add(new HyperlinkLabel(Constants.ABOUT_GITHUB, Constants.ABOUT_GITHUB_URI));
    //add(new JPanel() {{
    //  setLayout(new GridLayout(2, 1));
      
      add(new HassleFreeScrollPane(Constants.ABOUT_VERSE_NAME, new HyperlinkEditorPane(Constants.ABOUT_VERSE_CONTENT), false));
      add(new HassleFreeScrollPane(Constants.ABOUT_GPLV3_NAME, new HyperlinkEditorPane(Constants.ABOUT_GPLV3_CONTENT), true));
    //}});
  }
  
  @Override
  public Dimension getPreferredSize () {
    Dimension frameSize = MainFrame.getInstance().getSize();
    int preferredWidth  = Math.min(400, frameSize.width);
    int preferredHeight = Math.min(300, frameSize.height);
    return new Dimension(preferredWidth, preferredHeight);
  }
  
  static class HassleFreeScrollPane extends JScrollPane {
    HassleFreeScrollPane (String title, Component c, boolean enableScrollbar) {
      setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
      setViewportView(c);
      setAlignmentX(CENTER_ALIGNMENT);
      
      if (!enableScrollbar) {
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
      }
    }
  
    @Override
    public Dimension getPreferredSize () {
      Dimension preferredSize = super.getPreferredSize();
      Insets borderInsets = getBorder().getBorderInsets(this);
      int preferredWidth  = preferredSize.width + borderInsets.left + borderInsets.right;
      int preferredHeight = preferredSize.height + borderInsets.top + borderInsets.bottom;
      return new Dimension(preferredWidth, preferredHeight);
    }
  }
  
  static class HyperlinkEditorPane extends JEditorPane {
    HyperlinkEditorPane (String text) {
      setEditorKit(createEditorKitForContentType("text/html"));
      setText(text);
      setEditable(false);
      setOpaque(false);
      setCaretPosition(0);
      
      if (Desktop.isDesktopSupported()) {
        addHyperlinkListener(new HyperlinkListener() {
          @Override
          public void hyperlinkUpdate(HyperlinkEvent e) {
            if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
              try {
                Desktop.getDesktop().browse(e.getURL().toURI());
              } catch (URISyntaxException | IOException ioe) {
                ioe.printStackTrace();
              }
            }
          }
        });
      }
    }
  }
  
  static class HyperlinkLabel extends JLabel {
    HyperlinkLabel (String text, final String link) {
      setText(text);
      setHorizontalTextPosition(CENTER);
      setHorizontalAlignment(CENTER);
      setAlignmentX(CENTER_ALIGNMENT);
      setCursor(new Cursor(Cursor.HAND_CURSOR));
      
      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked (MouseEvent e) {
          try {
            Desktop.getDesktop().browse(new URI(link));
          } catch (URISyntaxException | IOException ioe) {
            ioe.printStackTrace();
          }
        }
      });
    }
  }
}
