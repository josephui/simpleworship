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
import com.gmail.josephui.simpleworship2.Constants;
import com.gmail.josephui.simpleworship2.Main;
import com.gmail.josephui.simpleworship2.event.ConfigChangeEvent;
import com.gmail.josephui.simpleworship2.event.ConfigChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontFormatException;
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
  
  private static volatile Font[] fonts;
  
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
    
    reloadFonts();
    Config.addConfigChangeListener(new String[] {
      "font1_file",
      "font1_size",
      "font2_file",
      "font2_size",
    }, new ConfigChangeListener () {
      @Override
      public void configChanged(ConfigChangeEvent e) {
        reloadFonts();
        
        MainFrame.getInstance().repaint();
        ProgramWindow.getInstance().repaint();
      }
    });
    
    instance = new MainFrame();
  }
  
  public static GraphicsDevice[] getGraphicsDevices () {
    return localGraphicsEnvironment.getScreenDevices();
  }
  
  public static void reloadFonts () {
    String font1Path = Config.getString("font1_file");
    String font2Path = Config.getString("font2_file");
    
    int font1Type = font1Path.endsWith(".ttf") ? Font.TRUETYPE_FONT : Font.TYPE1_FONT;
    int font2Type = font2Path.endsWith(".ttf") ? Font.TRUETYPE_FONT : Font.TYPE1_FONT;
    
    File font1 = new File(font1Path);
    File font2 = new File(font2Path);
    
    try {
      fonts = new Font[] {
        Font.createFont(font1Type, font1).deriveFont(Font.BOLD, Integer.parseInt(Config.getString("font1_size"))),
        Font.createFont(font2Type, font2).deriveFont(Font.BOLD, Integer.parseInt(Config.getString("font2_size"))),
      };
    } catch (IOException | FontFormatException e) {
      JOptionPane.showMessageDialog(instance, "Font failed to load: " + e, Constants.SOFTWARE_NAME, JOptionPane.WARNING_MESSAGE);
      e.printStackTrace();
    }
  }
  
  public static Font[] getFonts () {
    return fonts;
  }
  
  private static Object background;
  
  public static Object reloadAndGetBackgroundObject () {
    background = null;
    return getBackgroundObject();
  }
  
  public static Object getBackgroundObject () {
      if (background != null) {
          return background;
      }
      
      String backgroundString = Config.getString("background");
      
      try {
          int bgRgb = Integer.decode(backgroundString);
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
    ProgramWindow.getInstance().reloadBackground();
    
    setContentPane(new JPanel() {{
      setLayout(new BorderLayout());
      
      MainSplitPane splitPane   = MainSplitPane.getInstance();
      MainTabbedPane tabbedPane = MainTabbedPane.getInstance();
      BottomPanel optionPane    = BottomPanel.getInstance();
      
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
                ConfigPanel configPanel = new ConfigPanel();
                
                JOptionPane.showOptionDialog(
                  MainFrame.this,
                  configPanel,
                  "Settings",
                  JOptionPane.DEFAULT_OPTION,
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  new JButton[] {configPanel.getOkayButton()},
                  configPanel.getOkayButton()
                );
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
