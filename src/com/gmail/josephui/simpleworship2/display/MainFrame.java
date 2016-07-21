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
import java.awt.Component;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class MainFrame extends JFrame {
  private static final MainFrame instance;

  private static GraphicsEnvironment localGraphicsEnvironment;
  private static GraphicsDevice defaultScreenDevice;
  private static DisplayMode displayMode;
  
  private static Font[] fonts;
  
  static {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      
      Enumeration keys = UIManager.getDefaults().keys();
      while (keys.hasMoreElements()) {
        Object key = keys.nextElement();
        Object value = UIManager.get(key);
        if (value instanceof Font) {
          Font f = (Font)value;
          UIManager.put(key, f.deriveFont(Font.BOLD, f.getSize() * 1.2f));
        }
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
    
    localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    defaultScreenDevice      = localGraphicsEnvironment.getDefaultScreenDevice();
    displayMode              = defaultScreenDevice.getDisplayMode();
    
    for (GraphicsDevice device : localGraphicsEnvironment.getScreenDevices()) {
      System.out.println(device.toString());
    }
    
    fonts = new Font[] {
      new Font(Config.getString("font1_file"), Font.BOLD, Integer.parseInt(Config.getString("font1_size"))),
      new Font(Config.getString("font2_file"), Font.BOLD, Integer.parseInt(Config.getString("font2_size")))
    };
    
    instance = new MainFrame();
  }
  
  public static GraphicsDevice[] getGraphicsDevices () {
    return localGraphicsEnvironment.getScreenDevices();
  }
  
  public static Font[] getFonts () {
    return fonts;
  }
  
  private static Object background;
          
  public static Object getBackgroundObject () {
      if (background != null) {
          return background;
      }
      
      String backgroundString = Config.getString("background");
      
      try {
          int bgRgb = Integer.decode(backgroundString);
          System.out.println(Integer.toHexString(bgRgb));
          return background = new Color(bgRgb);
      } catch (NumberFormatException e) {
          try {
            return background = ImageIO.read(new File(backgroundString));
          } catch (IOException ioe) {
              ioe.printStackTrace();
              return null;
          }
      }
  }
  
  public static MainFrame getInstance () {
    return instance;
  }
  
  public static GraphicsDevice getSelectedGraphicsDevice () {
    int displayDeviceIndex = Integer.parseInt(Config.getString("display_device"));
    return localGraphicsEnvironment.getScreenDevices()[displayDeviceIndex];
  }
  
  /*--------------------------------------------------------------------------*/
  
  private MainFrame () {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setTitle(Main.APPLICATION_NAME);
    
    ProgramWindow.getInstance().setGraphicsDevice(MainFrame.getSelectedGraphicsDevice());
    
    Object backgroundObject = getBackgroundObject();
    if (backgroundObject instanceof Color) {
        ProgramWindow.getInstance().setBackgroundColor((Color)backgroundObject);
    } else if (backgroundObject instanceof BufferedImage) {
        ProgramWindow.getInstance().setBackgroundImage((BufferedImage)backgroundObject);
    }
    
    setContentPane(new JPanel() {{
      setLayout(new BorderLayout());
      
      MainSplitPane splitPane   = MainSplitPane.getInstance();
      MainTabbedPane tabbedPane = MainTabbedPane.getInstance();
      OptionPanel optionPane    = OptionPanel.getInstance();
      
      add(new JPanel() {{
        setLayout(new BorderLayout());
        add(SearchField.getInstance(), BorderLayout.CENTER);
        add(new JPanel () {{
          setLayout(new GridLayout(1, 2));
          
          add(new JButton() {{
            setText("Settings");
            addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(MainFrame.this, ConfigPanel.getInstance(), "Settings", JOptionPane.PLAIN_MESSAGE);
                // Pull in new thread to process
              }
            });
          }});
          
          add(new JButton() {{
            setText("About");
            addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(MainFrame.this, AboutPanel.getInstance(), "About", JOptionPane.PLAIN_MESSAGE);
              }
            });
          }});
        }}, BorderLayout.EAST);
      }}, BorderLayout.NORTH);
      
      
      add(splitPane, BorderLayout.CENTER);
      add(optionPane, BorderLayout.SOUTH);
    }});
    
    setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() {
        @Override
        protected boolean accept (Component c) {
          return c != SearchField.getInstance();
        }
    });
    
    setSize(1024, 768);
    setLocationRelativeTo(null);
    //setExtendedState(MAXIMIZED_BOTH);
  }
  
  @Override
  public void invalidate () {
    super.invalidate();
    
    SwingUtilities.invokeLater(new Runnable () {
      @Override
      public void run() {
        /*if (MainSplitPane.getInstance().arePanelsEmpty()) {
          SearchField.getInstance().requestFocusInWindow();
        }*/
        
        SearchResultPanel.getInstance().updateLyricsList();
        /*
        Dimension currentSize   = getSize();
        Dimension preferredSize = getPreferredSize();
        
        if (currentSize.width < preferredSize.width || currentSize.height < preferredSize.height) {
          int newWidth  = Math.max(currentSize.width, preferredSize.width);
          int newHeight = Math.max(currentSize.height, preferredSize.height);
        
          int screenWidth  = getSelectedGraphicsDevice().getDisplayMode().getWidth();
          int screenHeight = getSelectedGraphicsDevice().getDisplayMode().getHeight();
        
          newWidth  = Math.min(newWidth, screenWidth);
          newHeight = Math.min(newWidth, screenHeight);
          
          //setMinimumSize(getPreferredSize());
          setSize(newWidth, newHeight);
          
          revalidate();
          repaint();
          
          setLocationRelativeTo(null);
        }*/
      }
    });
  }
  
  // Terrible hack because whoever wrote DnDTabbedPane stole my GlassPane
  public void resetGlassPane () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }
    
    setGlassPane(SearchResultPanel.getInstance());
  }
}
