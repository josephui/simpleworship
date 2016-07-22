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
import com.gmail.josephui.simpleworship2.models.Subsection;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PreviewPanel extends JPanel {
  private static final Font TAG_FONT  = new Font("Tahoma", Font.BOLD, 12);
  private static final int TAG_MARGIN = 5;
  private static final String PREVIEW_STRING = "Preview";
  private static final String PROGRAM_STRING = "Program";
  
  public static final BufferedImage PREVIEW_TAG;
  public static final BufferedImage PROGRAM_TAG;
  
  private static volatile PreviewPanel currentPreviewPanel;
  
  static {
    Canvas canvas = new Canvas();
    Graphics g = canvas.getGraphics();
    FontMetrics fontMetrics = canvas.getFontMetrics(TAG_FONT);
    
    Rectangle2D previewTagRectangle = fontMetrics.getStringBounds(PREVIEW_STRING, g);
    Rectangle2D programTagRectangle = fontMetrics.getStringBounds(PROGRAM_STRING, g);
    
    int previewTextWidth  = (int)Math.ceil(previewTagRectangle.getWidth());
    int previewTextHeight = (int)Math.ceil(previewTagRectangle.getHeight());
    int programTextWidth  = (int)Math.ceil(programTagRectangle.getWidth());
    int programTextHeight = (int)Math.ceil(programTagRectangle.getHeight());
    
    int previewTagWidth  = previewTextWidth + 2 * TAG_MARGIN;
    int previewTagHeight = previewTextHeight + 2 * TAG_MARGIN;
    int programTagWidth  = programTextWidth + 2 * TAG_MARGIN;
    int programTagHeight = programTextHeight + 2 * TAG_MARGIN;
    
    PREVIEW_TAG = new BufferedImage(previewTagWidth, previewTagHeight, BufferedImage.TYPE_INT_ARGB_PRE);
    PROGRAM_TAG = new BufferedImage(programTagWidth, programTagHeight, BufferedImage.TYPE_INT_ARGB_PRE);
    
    Graphics2D g2 = (Graphics2D)PREVIEW_TAG.getGraphics();
    g2.setColor(new Color(0x007f00));
    g2.fillRect(0, 0, previewTagWidth, previewTagHeight);
    g2.setColor(Color.WHITE);
    g2.setFont(TAG_FONT);
    g2.drawString(PREVIEW_STRING, TAG_MARGIN, TAG_MARGIN + fontMetrics.getAscent());
    
    Graphics2D g3 = (Graphics2D)PROGRAM_TAG.getGraphics();
    g3.setColor(Color.RED);
    g3.fillRect(0, 0, programTagWidth, programTagHeight);
    g3.setColor(Color.WHITE);
    g3.setFont(TAG_FONT);
    g3.drawString(PROGRAM_STRING, TAG_MARGIN, TAG_MARGIN + fontMetrics.getAscent());
  }
  
  /*--------------------------------------------------------------------------*/
  
  private volatile BufferedImage lyricsImage;
  
  private final Subsection subsection;
  private final BufferedImage tagImage;
  private volatile Font[] currentFonts;
  
  public PreviewPanel (Subsection subsection, BufferedImage tagImage) {
    this.subsection = subsection;
    this.tagImage = tagImage;
  }
  
  @Override
  protected void paintComponent (Graphics g) {
    super.paintComponent(g);
    
    BufferedImage backgroundImage = ProgramWindow.getInstance().getBackgroundImage();
    
    int programWidth  = ProgramWindow.getInstance().getWidth();
    int programHeight = ProgramWindow.getInstance().getHeight();
    
    int newWidth  = getWidth();
    int newHeight = getHeight();
    int newX = 0;
    int newY = 0;
      
    int renderedWidthTimesPreviewHeight = programWidth * getHeight();
    int previewWidthTimesRenderedHeight = getWidth() * programHeight;

    if (renderedWidthTimesPreviewHeight < previewWidthTimesRenderedHeight) {
        // If original height is bigger, we keep newHeight and scale newWidth down
        newWidth = getHeight() * programWidth / programHeight;
        newX     = (getWidth() - newWidth) / 2;
    } else if (renderedWidthTimesPreviewHeight > previewWidthTimesRenderedHeight) {
        newHeight = getWidth() * programHeight / programWidth;
        newY      = (getHeight() - newHeight) / 2;
    }

    if (backgroundImage != null) {
      Image scaledImage = backgroundImage.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
      g.drawImage(scaledImage, newX, newY, null);
    }
    
    if (lyricsImage == null || lyricsImage.getWidth() != programWidth || lyricsImage.getHeight() != programHeight || currentFonts != MainFrame.getFonts()) {
      currentFonts = MainFrame.getFonts();
      lyricsImage = new BufferedImage(programWidth, programHeight, BufferedImage.TYPE_INT_ARGB_PRE);
      
      List<String>[] allHalves = subsection.getAllHalves();
    
      for (int i = 0; i < allHalves.length; i++) {
        // i=0 means topHalf, i=1 means bottomHalf
        int y = (i == 0) ? (int)(programHeight * Config.getDouble("margin_top")) : (int)(programHeight * Config.getDouble("margin_bottom"));

        Graphics2D g2 = lyricsImage.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.setFont(currentFonts[i]);
        FontMetrics metrics = g2.getFontMetrics();
        //FontRenderContext frc = g2.getFontRenderContext();
        
        for (int j = 0; j < allHalves[i].size(); j++) {
          String line = allHalves[i].get((i == 0) ? j : allHalves[i].size() - j - 1);
          y += (i == 0) ? metrics.getAscent() : metrics.getDescent();

          FontRenderContext frc = g2.getFontRenderContext();
          TextLayout textTl = new TextLayout(line, currentFonts[i], frc);

          Rectangle2D rectangle = textTl.getBounds();

          int w = (int)rectangle.getWidth();
          int x = (programWidth - w) / 2;

          AffineTransform transform = g2.getTransform();
          transform.translate(x, (i == 0) ? y : programHeight - y);
          
          Shape outline = textTl.getOutline(transform);
          
          g2.setStroke(new BasicStroke(5f));
          g2.setColor(Color.BLACK);
          g2.draw(outline);
          
          g2.setColor(Color.WHITE);
          g2.fill(outline);
          
          // Offset stuff
          y += (i == 0) ? metrics.getDescent() : metrics.getAscent();
        }
        g2.dispose();
      }
      
      if (currentPreviewPanel == this) {
        setAsCurrentPreviewPanel();
      }
    }
    
    Image scaledImage = lyricsImage.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
    g.drawImage(scaledImage, newX, newY, null);
    
    // Now we draw the red margin rectangle
    int marginLeft   = (int)(newWidth * Config.getDouble("margin_left"));
    int marginTop    = (int)(newHeight * Config.getDouble("margin_top"));
    int marginRight  = (int)(newWidth * Config.getDouble("margin_right"));
    int marginBottom = (int)(newHeight * Config.getDouble("margin_bottom"));

    int x = newX + marginLeft;
    int y = newY + marginTop;
    int w = getWidth() - 2*newX - marginLeft - marginRight;
    int h = getHeight() - 2*newY - marginTop - marginBottom;

    g.setColor(Color.RED);
    g.drawRect(x, y, w, h);
    
    g.setColor(Color.DARK_GRAY);
    g.drawRect(newX, newY, newWidth, newHeight);

    g.drawImage(tagImage, newX, newY, null);
  }
  
  public void setAsCurrentPreviewPanel () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    currentPreviewPanel = this;
    
    SwingUtilities.invokeLater(new Runnable () {
      @Override
      public void run() {
        paintImmediately(0, 0, getWidth(), getHeight());
        ProgramWindow.getInstance().setLyricsImage(lyricsImage);
      }
    });
  }
}
