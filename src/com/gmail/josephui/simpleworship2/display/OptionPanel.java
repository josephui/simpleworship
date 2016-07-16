package com.gmail.josephui.simpleworship2.display;

import com.stackoverflow.questions60269.DnDTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class OptionPanel extends JPanel {
  private static final OptionPanel instance;
  
  static {
    instance = new OptionPanel();
  }
  
  public static OptionPanel getInstance () {
    return instance;
  }
  
  private static class ButtonLabel extends JLabel {
      Border etchedBorder;
      Border raisedBorder;
      Border loweredBorder;
      
      AtomicBoolean isToggled;
      AtomicBoolean isDisabled;
      
      Color onBackground;
      Color onForeground;
      Color offBackground;
      Color offForeground;
      
      ButtonLabel (String text, Color background, Color foreground) {
          etchedBorder  = BorderFactory.createEtchedBorder();
          raisedBorder  = BorderFactory.createRaisedBevelBorder();
          loweredBorder = BorderFactory.createLoweredBevelBorder();
          
          isToggled = new AtomicBoolean(false);
          isDisabled = new AtomicBoolean(true);
          
          offBackground = getBackground();
          offForeground = getForeground();
          
          onBackground = background;
          onForeground = foreground;
          
          setBorder(raisedBorder);
          setHorizontalAlignment(CENTER);
          setForeground(Color.LIGHT_GRAY);
          setOpaque(true);
          
          setText(" " + text + " ");
          
          addMouseListener(new MouseAdapter() {
              @Override
              public void mouseExited (MouseEvent e) {
                  if (isDisabled.get()) {
                      return;
                  }
                  
                  if (isToggled.get()) {
                      setBorder(etchedBorder);
                  } else {
                      setBorder(raisedBorder);
                  }
              }
              
              @Override
              public void mousePressed (MouseEvent e) {
                  if (isDisabled.get()) {
                      return;
                  }
                  
                  setBorder(loweredBorder);
              }
              
              @Override
              public void mouseClicked (MouseEvent e) {
                  if (isDisabled.get()) {
                      return;
                  }
                  
                  isToggled.set(!isToggled.get());
                  PreviewPanel.repaintRenderedWindow();
                  
                  if (isToggled.get()) {
                      setBorder(etchedBorder);
                      setBackground(onBackground);
                      setForeground(onForeground);
                  } else {
                      setBorder(raisedBorder);
                      setBackground(offBackground);
                      setForeground(offForeground);
                  }
              }
          });
      }
  }
  
  /*--------------------------------------------------------------------------*/
  
  private JComboBox displaySelectionComboBox;
  private JLabel cpuLabel;
  private JLabel memLabel;
  private ButtonLabel blackButton;
  private ButtonLabel clearButton;
  private ButtonLabel liveButton;
  
  private OptionPanel () {
    setBorder(null);
    setLayout(new BorderLayout());
    
    add(displaySelectionComboBox = new JComboBox () {{
    }}, BorderLayout.WEST);
    add(new JPanel () {{
        // Add labels later
    }}, BorderLayout.CENTER);
    add(new JPanel () {{
        setBorder(null);
        setLayout(new GridLayout(1, 3));
        
        add(blackButton = new ButtonLabel("Black", Color.BLACK, Color.WHITE) {{
        }});
        add(clearButton = new ButtonLabel("Clear", Color.DARK_GRAY, Color.WHITE) {{
        }});
        add(liveButton = new ButtonLabel("Live", Color.RED, Color.WHITE) {{
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked (MouseEvent e) {
                    PreviewPanel.setRenderedWindowVisible(isLive());
                }
            });
        }});
    }}, BorderLayout.EAST);
  }
  
  public void enableButtons () {
      blackButton.isDisabled.set(false);
      clearButton.isDisabled.set(false);
      liveButton.isDisabled.set(false);
      blackButton.setForeground(blackButton.offForeground);
      clearButton.setForeground(clearButton.offForeground);
      liveButton.setForeground(liveButton.offForeground);
  }
  
  public boolean isBlack () {
      return blackButton.isToggled.get();
  }
  
  public boolean isClear () {
      return clearButton.isToggled.get();
  }
  
  public boolean isLive () {
      return liveButton.isToggled.get();
  }
}
