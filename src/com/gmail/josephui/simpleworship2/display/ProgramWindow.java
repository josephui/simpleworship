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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public final class ProgramWindow extends JWindow {
  private static final ProgramWindow instance;
  
  static {
    instance = new ProgramWindow();
  }
  
  public static ProgramWindow getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  private final JPanel contentPanel;
  
  private volatile GraphicsDevice graphicsDevice;
  
  private volatile BufferedImage backgroundImage;
  private volatile BufferedImage lyricsImage;
  
  private volatile boolean isBlackToggled;
  private volatile boolean isClearToggled;
  private volatile boolean isLiveToggled;
  
  private ProgramWindow () {
    setContentPane(contentPanel = new JPanel () {
      @Override
      protected void paintComponent (Graphics g) {
        super.paintComponent(g);
        
        if (isBlackToggled) {
          g.setColor(Color.BLACK);
          g.fillRect(0, 0, getWidth(), getHeight());
          return;
        }
        
        if (backgroundImage != null) {
          g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        if (isClearToggled) {
          return;
        }
        
        if (lyricsImage != null) {
          g.drawImage(lyricsImage, 0, 0, getWidth(), getHeight(), this);
        }
      }
    });
  }
  
  public BufferedImage getBackgroundImage () {
    return backgroundImage;
  }
  
  public void setGraphicsDevice (GraphicsDevice graphicsDevice) {
    this.graphicsDevice = graphicsDevice;
    
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }
    
    Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
    
    setLocation(bounds.x, bounds.y);
    setSize(bounds.width, bounds.height);
  }
  
  public void setLyricsImage (BufferedImage lyricsImage) {
    this.lyricsImage = lyricsImage;
    repaint();
  }
  
  public void setBackgroundColor (Color color) {
      BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
      Graphics2D g = image.createGraphics();
      g.setColor(color);
      g.fillRect(0, 0 , image.getWidth(), image.getHeight());
      
      setBackgroundImage(image);
  }
  
  public void setBackgroundImage (BufferedImage backgroundImage) {
    this.backgroundImage = backgroundImage;
    repaint();
  }
  
  public void setBlackToggle (boolean isBlackToggled) {
    this.isBlackToggled = isBlackToggled;
    repaint();
  }
  
  public void setClearToggle (boolean isClearToggled) {
    this.isClearToggled = isClearToggled;
    repaint();
  }
  
  public void setLiveToggle (final boolean isLiveToggled) {
    this.isLiveToggled = isLiveToggled;
    
    MainSplitPane.getInstance().showCurrentPreviewPanel();
    
    if (isLiveToggled) {
      repaint();
    }
    
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }
    
    SwingUtilities.invokeLater(new Runnable () {
      @Override
      public void run() {
        setVisible(isLiveToggled);
      }
    });
  }
}
