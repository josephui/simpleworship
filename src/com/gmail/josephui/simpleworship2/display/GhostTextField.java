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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class GhostTextField extends JTextField {
  private volatile String ghostText;
  
  public GhostTextField (String ghostText) {
    this.ghostText = ghostText;
    
    setBorder(BorderFactory.createEtchedBorder());
  }
  
  @Override
  public void setText (String text) {
    super.setText(text);
    
    setCaretPosition(0);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (ghostText != null && getText().isEmpty()) {
      g.setColor(Color.GRAY);

      FontMetrics metrics   = g.getFontMetrics();
      Rectangle2D rectangle = metrics.getStringBounds(ghostText, g);

      int x = (int)Math.round((getWidth() - rectangle.getWidth()) / 2);
      int y = (int)Math.round((getHeight() - rectangle.getHeight()) / 2) + metrics.getAscent();
      
      g.drawString(ghostText, x, y);
    }
  }
}
