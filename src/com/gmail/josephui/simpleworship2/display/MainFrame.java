package com.gmail.josephui.simpleworship2.display;

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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * @xToSelf Thread-safe
 * @author Joseph Hui <josephui@gmail.com>
 */
public final class MainFrame extends JFrame {
  private static final MainFrame instance;

  private static GraphicsEnvironment localGraphicsEnvironment;
  private static GraphicsDevice defaultScreenDevice;
  private static DisplayMode displayMode;
  
  private static Font[] fonts;
  
  static {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    defaultScreenDevice      = localGraphicsEnvironment.getDefaultScreenDevice();
    displayMode              = defaultScreenDevice.getDisplayMode();
    
    for (GraphicsDevice device : localGraphicsEnvironment.getScreenDevices()) {
      System.out.println(device.toString());
    }
    
    fonts = new Font[] {
      new Font(Main.getProperty("font1_file"), Font.BOLD, Integer.parseInt(Main.getProperty("font1_size"))),
      new Font(Main.getProperty("font2_file"), Font.BOLD, Integer.parseInt(Main.getProperty("font2_size")))
    };
    
    instance = new MainFrame();
  }
  
  public static GraphicsDevice[] getGraphicsDevices () {
    return localGraphicsEnvironment.getScreenDevices();
  }
  
  public static Font[] getFonts () {
    return fonts;
  }
  
  public static MainFrame getInstance () {
    return instance;
  }
  
  public static GraphicsDevice getSelectedGraphicsDevice () {
    int displayDeviceIndex = Integer.parseInt(Main.getProperty("display_device"));
    return localGraphicsEnvironment.getScreenDevices()[displayDeviceIndex];
  }
  
  /*--------------------------------------------------------------------------*/
  
  private MainFrame () {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setTitle(Main.APPLICATION_NAME);
    
    setContentPane(new JPanel() {{
      setLayout(new BorderLayout());
      
      MainSplitPane splitPane   = MainSplitPane.getInstance();
      MainTabbedPane tabbedPane = MainTabbedPane.getInstance();
      OptionPanel optionPane    = OptionPanel.getInstance();
      
      add(SearchField.getInstance(), BorderLayout.NORTH);
      add(splitPane, BorderLayout.CENTER);
      add(optionPane, BorderLayout.SOUTH);
    }});
    
    setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() {
        @Override
        protected boolean accept (Component c) {
            return c != SearchField.getInstance();
        }
    });
    
    resetGlassPane();
    
    ProgramWindow.getInstance().setGraphicsDevice(MainFrame.getSelectedGraphicsDevice());
    
    Object background = Main.getBackground();
    if (background instanceof Color) {
        ProgramWindow.getInstance().setBackgroundColor((Color)background);
    } else if (background instanceof BufferedImage) {
        ProgramWindow.getInstance().setBackgroundImage((BufferedImage)background);
    }
    
    
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
