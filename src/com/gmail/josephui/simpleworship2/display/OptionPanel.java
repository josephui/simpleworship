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

import com.gmail.josephui.simpleworship2.Config;
import com.gmail.josephui.simpleworship2.Main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public final class OptionPanel extends JPanel {
  private static final OptionPanel instance;
  
  static {
    instance = new OptionPanel();
  }
  
  public static OptionPanel getInstance () {
    return instance;
  }
  
  private static class ButtonLabel extends JLabel {
    final Border etchedBorder;
    final Border raisedBorder;
    final Border loweredBorder;

    final AtomicBoolean isToggled;
    final AtomicBoolean isDisabled;

    final Color onBackground;
    final Color onForeground;
    final Color offBackground;
    final Color offForeground;
      
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
  
  private JComboBox<GraphicsDevice> displaySelectionComboBox;
  private JLabel cpuLabel;
  private JLabel memLabel;
  private ButtonLabel blackButton;
  private ButtonLabel clearButton;
  private ButtonLabel liveButton;
  
  private OptionPanel () {
    setBorder(null);
    setLayout(new BorderLayout());
    
    add(displaySelectionComboBox = new JComboBox<GraphicsDevice> (MainFrame.getGraphicsDevices()) {{
      setSelectedItem(MainFrame.getSelectedGraphicsDevice());
      addActionListener(new ActionListener () {
        @Override
        public void actionPerformed(ActionEvent ae) {
          final int index = getSelectedIndex();
          
          try {
            Config.putAndStore("display_device", getSelectedIndex() + "");
          } catch (IOException ioe) {
            System.err.println("Unable to store `display_device` property after put");
            ioe.printStackTrace();
          }
          
          SwingUtilities.invokeLater(new Runnable () {
            @Override
            public void run() {
              ProgramWindow.getInstance().setGraphicsDevice(getItemAt(index));
              MainSplitPane.getInstance().repaintPreviewPanels();
            }
          });
        }
      });
    }}, BorderLayout.WEST);
    add(new JPanel () {{
      //
    }}, BorderLayout.CENTER);
    add(new JPanel () {{
      setBorder(null);
      setLayout(new GridLayout(1, 3));

      add(blackButton = new ButtonLabel("Black", Color.BLACK, Color.WHITE) {{
        addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked (MouseEvent e) {
            ProgramWindow.getInstance().setBlackToggle(isBlack());
          }
        });
      }});
      add(clearButton = new ButtonLabel("Clear", Color.DARK_GRAY, Color.WHITE) {{
        addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked (MouseEvent e) {
            ProgramWindow.getInstance().setClearToggle(isClear());
          }
        });
      }});
      add(liveButton = new ButtonLabel("Live", Color.RED, Color.WHITE) {{
        addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked (MouseEvent e) {
            ProgramWindow.getInstance().setLiveToggle(isLive());
          }
        });
      }});
    }}, BorderLayout.EAST);
  }
  
  public void enableButtons () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

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
