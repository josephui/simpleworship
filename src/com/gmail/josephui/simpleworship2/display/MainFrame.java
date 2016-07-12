package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.Main;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
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
  
  static {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    defaultScreenDevice      = localGraphicsEnvironment.getDefaultScreenDevice();
    displayMode              = defaultScreenDevice.getDisplayMode();
    
    instance = new MainFrame();
  }
  
  public static MainFrame getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  @Override
  public void paint(Graphics g) {
    super.paint(g);

    setMinimumSize(getPreferredSize());
  }
  private MainFrame () {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setTitle(Main.APPLICATION_NAME);
    
    //
    setContentPane(SearchPanel.getInstance());
    
    pack();
    setLocationRelativeTo(null);
    
    setExtendedState(MAXIMIZED_BOTH);
    setVisible(true);
  }
  
}
