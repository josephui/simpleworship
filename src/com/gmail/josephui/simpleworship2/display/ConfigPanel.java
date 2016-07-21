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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConfigPanel extends JPanel {
  private static final ConfigPanel instance;
  
  static {
    instance = new ConfigPanel();
  }
  
  public static ConfigPanel getInstance () {
    return instance;
  }
  
  /*--------------------------------------------------------------------------*/
  
  private final ChooseFontPanel font1Panel;
  private final ChooseFontPanel font2Panel;
  private final ChooseBackgroundPanel backgroundPanel;
  private final ChooseNumberPanel topMarginField;
  private final ChooseNumberPanel leftMarginField;
  private final ChooseNumberPanel bottomMarginField;
  private final ChooseNumberPanel rightMarginField;
  
  private ConfigPanel () {
    setLayout(new GridLayout(0, 1));
    
    Object backgroundObject = MainFrame.getBackgroundObject();
    Color backgroundColor = (backgroundObject instanceof Color) ? (Color)backgroundObject : null;
    
    add(font1Panel = new ChooseFontPanel ("Top Font", MainFrame.getFonts()[0]));
    add(font2Panel = new ChooseFontPanel ("Bottom Font", MainFrame.getFonts()[1]));
    add(backgroundPanel = new ChooseBackgroundPanel ("Background", backgroundColor, Config.getString("background")));
    add(topMarginField = new ChooseNumberPanel ("Top Margin", false, 0d, 1d, Config.getDouble("margin_top")));
    add(leftMarginField = new ChooseNumberPanel ("Left Margin", false, 0d, 1d, Config.getDouble("margin_left")));
    add(bottomMarginField = new ChooseNumberPanel ("Bottom Margin", false, 0d, 1d, Config.getDouble("margin_bottom")));
    add(rightMarginField = new ChooseNumberPanel ("Right Margin", false, 0d, 1d, Config.getDouble("margin_right")));
  }
  
  static class ChooseFontPanel extends JPanel {
    final JButton pathButton;
    final JTextField pathField;
    final JTextField sizeField;
    
    ChooseFontPanel (String name, Font defaultFont) {
      setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), name));
      setLayout(new BorderLayout());
      
      add(pathButton = new JButton() {{}}, BorderLayout.WEST);
      add(pathField = new JTextField() {{}}, BorderLayout.CENTER);
      add(sizeField = new JTextField() {{}}, BorderLayout.EAST);
    }
  }
  
  static class ChooseBackgroundPanel extends JPanel {
    final JColorChooser colorChooser;
    final JButton colorButton;
    final JTextField colorField;
    
    ChooseBackgroundPanel (String name, Color defaultColor, String defaultPath) {
      setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), name));
      setLayout(new BorderLayout());
      
      colorChooser = new JColorChooser();
      
      add(colorButton = new JButton() {{}}, BorderLayout.WEST);
      add(colorField = new JTextField() {{}}, BorderLayout.CENTER);
      
    }
  }
  
  static class ChooseNumberPanel extends JPanel {
    final boolean integeOnly;
    final Number minimumValue;
    final Number maximumValue;
    final JTextField numberField;
    
    ChooseNumberPanel (String name, boolean integeOnly, Number minimumValue, Number maximumValue, Number defaultValue) {
      setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), name));
      setLayout(new BorderLayout());
      
      this.integeOnly = integeOnly;
      this.minimumValue = minimumValue;
      this.maximumValue = maximumValue;
      
      add(numberField = new JTextField() {{}}, BorderLayout.CENTER);
    }
  }
}

/*
font1_file=resources/ariblk.ttf
font1_size=100
font1_color=0xFFFFFF
font2_file=resources/msjhbd.ttf
font2_size=100
font2_color=0xFFFFFF

#background=resources/test-img.png
background=0x7F7F7F
schedule=resources/schedule.txt
display_device=0
margin_top=0.05
margin_bottom=0.05
margin_left=0.025
margin_right=0.025

*/