package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.Main;
import java.awt.BorderLayout;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class MainFrame extends JFrame {
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
  
  @Override
  public void paint(Graphics g) {
    super.paint(g);

    //setMinimumSize(getPreferredSize());
  }
  
  LyricsPreviewPanel lyricsPreviewPanel;
  
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
    
    final SearchResultPanel searchResultPanel = SearchResultPanel.getInstance();
    resetGlassPane();
    
    addComponentListener(new ComponentAdapter () {
      @Override
      public void componentResized(ComponentEvent ce) {
        searchResultPanel.updateLyricsList();
      }
    });
    
    ProgramWindow.getInstance().setGraphicsDevice(MainFrame.getSelectedGraphicsDevice());
    
    setSize(1024, 768);
    setLocationRelativeTo(null);
    
    //setExtendedState(MAXIMIZED_BOTH);
  }
  
  // Terrible hack because whoever wrote DnDTabbedPane stole my GlassPane
  public void resetGlassPane () {
    setGlassPane(SearchResultPanel.getInstance());
  }
}
