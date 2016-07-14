package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.Main;
import com.gmail.josephui.simpleworship2.models.Subsection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class PreviewPanel extends JPanel{
  private BufferedImage buffer;
  private BufferedImage background;
  
  private JWindow renderedWindow;
  private JLabel renderedLabel;
  
  private JLabel previewLabel;
  
  private Subsection currentSubsection;
  static int counter = 0;
  int instanceId;
  public PreviewPanel () {instanceId = counter++;
    setLayout(new BorderLayout());
    
    buffer     = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
    background = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
    
    renderedWindow = new JWindow() {{
      setContentPane(renderedLabel = new JLabel() {
        @Override
        protected void paintComponent (Graphics g) {
          super.paintComponent(g);
          
          g.drawImage(background, 0, 0, null);
          g.drawImage(buffer, 0, 0, null);
        }
      });
    }};

    add(previewLabel = new JLabel() {
      @Override
      protected void paintComponent (Graphics g) {
        super.paintComponent(g);

        BufferedImage renderedImage = getRenderedImage();
        int renderedWidthTimesPreviewHeight = renderedImage.getWidth() * getHeight();
        int previewWidthTimesRenderedHeight = getWidth() * renderedImage.getHeight();
        
        int newWidth  = getWidth();
        int newHeight = getHeight();
        int newX = 0;
        int newY = 0;
        
        if (renderedWidthTimesPreviewHeight < previewWidthTimesRenderedHeight) {
            // If original height is bigger, we keep newHeight and scale newWidth down
            newWidth = getHeight() * renderedImage.getWidth() / renderedImage.getHeight();
            newX     = (getWidth() - newWidth) / 2;
        } else if (renderedWidthTimesPreviewHeight > previewWidthTimesRenderedHeight) {
            newHeight = getWidth() * renderedImage.getHeight() / renderedImage.getWidth();
            newY      = (getHeight() - newHeight) / 2;
        }
        
        Image scaledImage = getRenderedImage().getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
        g.drawImage(scaledImage, newX, newY, null);
        
        int marginLeft   = (int)(newWidth * Double.parseDouble(Main.getProperty("margin_left")));
        int marginTop    = (int)(newHeight * Double.parseDouble(Main.getProperty("margin_top")));
        int marginRight  = (int)(newWidth * Double.parseDouble(Main.getProperty("margin_right")));
        int marginBottom = (int)(newHeight * Double.parseDouble(Main.getProperty("margin_bottom")));
        
        int x = newX + marginLeft;
        int y = newY + marginTop;
        int w = getWidth() - 2*newX - marginLeft - marginRight;
        int h = getHeight() - 2*newY - marginTop - marginBottom;
        
        g.setColor(Color.RED);
        g.drawRect(x, y, w, h);
      }
    }, BorderLayout.CENTER);
  }
  
  private BufferedImage getRenderedImage () {
    BufferedImage img = new BufferedImage(renderedLabel.getPreferredSize().width, renderedLabel.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB_PRE);
    Graphics2D g = img.createGraphics();
    g.setColor(previewLabel.getForeground());
    g.setFont(previewLabel.getFont());

    SwingUtilities.paintComponent(g, renderedLabel, previewLabel, 0, 0, img.getWidth(), img.getHeight());

    return img.getSubimage(0, 0, img.getWidth(), img.getHeight());
  }
  
  public void setRenderedDevice (GraphicsDevice device) {
    Rectangle bounds = device.getDefaultConfiguration().getBounds();

    renderedWindow.setLocation(bounds.x, bounds.y);
    renderedWindow.setSize(bounds.width, bounds.height);
    renderedLabel.setPreferredSize(new Dimension(bounds.width, bounds.height));
    previewLabel.setPreferredSize(new Dimension(bounds.width / 4, bounds.height / 4));
    buffer     = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB_PRE);
    background = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB_PRE);
  }
  
  public void setRenderedWindowVisible (boolean b) {
    renderedWindow.setVisible(b);
  }
  
  public void setRenderedWindowBackground (Image img) {
    background.flush();
    
    Graphics2D g = (Graphics2D)background.getGraphics();
    g.drawImage(img, 0, 0, background.getWidth(), background.getHeight(), null);
  }
  
  public void setRenderedWindowBackground (Color c) {
    background.flush();
    
    Graphics2D g = (Graphics2D)background.getGraphics();
    g.setColor(c);
    g.fillRect(0, 0, background.getWidth(), background.getHeight());;
  }
  
  public void setSubsection (Subsection subsection, Font[] fonts) {
    currentSubsection = subsection;
      
    buffer.flush();
    
    List<String>[] allHalves = subsection.getAllHalves();
    
    for (int i = 0; i < allHalves.length; i++) {
      int y = (int)(renderedWindow.getHeight() * Double.parseDouble(Main.getProperty("margin_top")));
      
      Graphics2D g = buffer.createGraphics();

      g.setFont(fonts[i]);
      g.setColor(Color.GREEN);
      FontMetrics metrics = g.getFontMetrics();

      for (int j = 0; j < allHalves[i].size(); j++) {
        String line = allHalves[i].get((i == 0) ? j : allHalves[i].size() - j - 1);
        
        y += (i == 0) ? metrics.getAscent() : metrics.getDescent();

        Rectangle2D rectangle = metrics.getStringBounds(line, g);
        
        int w = (int)rectangle.getWidth();
        int x = (renderedWindow.getWidth() - w) / 2;
        
        // If it's the bottom lyrics, we need to invert y
        g.drawString(line, x, (i == 0) ? y : renderedWindow.getHeight() - y);
        //System.out.println("Drawing string `" + line + "` at (" + x + ", " + ((i == 0) ? y : renderedWindow.getHeight() - y) + ")");
        
        y += (i == 0) ? metrics.getDescent() : metrics.getAscent();
      }
    }
  }
  
  public String toString() {
      return instanceId + ": " + ((currentSubsection == null) ? "<No Subsection>" : currentSubsection.toConsoleString());
  }
}
