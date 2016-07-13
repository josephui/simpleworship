package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.Main;
import static com.gmail.josephui.simpleworship2.Main.getProperty;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class MainFrame extends JFrame{
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
    
    fonts = new Font[] {
      new Font(Main.getProperty("font1_file"), Font.BOLD, Integer.parseInt(Main.getProperty("font1_size"))),
      new Font(Main.getProperty("font2_file"), Font.BOLD, Integer.parseInt(Main.getProperty("font2_size")))
    };
    
    instance = new MainFrame();
  }
  
  public static Font[] getFonts () {
    return fonts;
  }
  
  public static MainFrame getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  @Override
  public void paint(Graphics g) {
    super.paint(g);

    //setMinimumSize(getPreferredSize());
  }
  private MainFrame () {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setTitle(Main.APPLICATION_NAME);
    
    //
    //setContentPane(SearchPanel.getInstance());
    
    /*
    PreviewPanel.getInstance().setRenderedDevice(localGraphicsEnvironment.getScreenDevices()[0]);
    PreviewPanel.getInstance().setSubsection(Main.getAllLyrics().get(1).getSections().get(0).getSubsections().get(0), fonts);
    try {
      PreviewPanel.getInstance().setRenderedWindowBackground(ImageIO.read(new File("test-img.png")));
    } catch (Exception e) {}
    
    PreviewPanel.getInstance().setRenderedWindowVisible(true);
    
    setContentPane(PreviewPanel.getInstance());
    */
    
    setContentPane(new JScrollPane(new LyricsPanel(Main.getAllLyrics().get(1))));
    
    pack();
    setLocationRelativeTo(null);
    
    setExtendedState(MAXIMIZED_BOTH);
    setVisible(true);
  }
  
}
