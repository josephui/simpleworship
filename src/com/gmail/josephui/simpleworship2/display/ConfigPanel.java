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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

public class ConfigPanel extends JPanel {
  private static volatile Icon openIcon;
  
  private static Icon getOpenIcon () {
    if (openIcon == null) {
      openIcon = UIManager.getIcon("Tree.openIcon");
    }
    
    return openIcon;
  }
  
  /*--------------------------------------------------------------------------*/
  
  private final ChooseFontPanel font1Panel;
  private final ChooseFontPanel font2Panel;
  private final ChooseBackgroundPanel backgroundPanel;
  private final ChooseNumberPanel topMarginField;
  private final ChooseNumberPanel leftMarginField;
  private final ChooseNumberPanel bottomMarginField;
  private final ChooseNumberPanel rightMarginField;
  
  private volatile JButton okayButton;
  
  public ConfigPanel () {
    setLayout(new GridLayout(0, 1));
    
    Object backgroundObject = MainFrame.getBackgroundObject();
    Color backgroundColor = (backgroundObject instanceof Color) ? (Color)backgroundObject : null;
    
    okayButton = new JButton("Ok") {{
      addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
          Window w = SwingUtilities.getWindowAncestor(okayButton);
          
          if (w != null) {
            font1Panel.run();
            font2Panel.run();
            backgroundPanel.run();
            topMarginField.run();
            leftMarginField.run();
            bottomMarginField.run();
            rightMarginField.run();
            
            w.setVisible(false);
          }
        }
      });
    }};
    
    add(font1Panel = new ChooseFontPanel ("Top Font", "font1_file", "font1_size"));
    add(font2Panel = new ChooseFontPanel ("Bottom Font", "font2_file", "font2_size"));
    add(backgroundPanel = new ChooseBackgroundPanel ("Background", "background"));
    add(new PairPanel (
      topMarginField    = new ChooseNumberPanel ("Top Margin", 0d, 1d, "margin_top"),
      bottomMarginField = new ChooseNumberPanel ("Bottom Margin", 0d, 1d, "margin_bottom")
    ));
    add(new PairPanel (
      leftMarginField  = new ChooseNumberPanel ("Left Margin", 0d, 1d, "margin_left"),
      rightMarginField = new ChooseNumberPanel ("Right Margin", 0d, 1d, "margin_right")
    ));
  }
  
  public JButton getOkayButton () {
    return okayButton;
  }
  
  public boolean isConfigValid () {
    return false;
  }
  
  static class PairPanel extends JPanel {
    PairPanel (Component c1, Component c2) {
      setLayout(new GridLayout(1, 2));
      add(c1);
      add(c2);
    }
  }
  
  static class PainlessButton extends JButton {
    PainlessButton (Icon icon) {
      setBorder(BorderFactory.createEtchedBorder());
      setIcon(getOpenIcon());
      setFocusable(false);
      setContentAreaFilled(false);
    }
        
    @Override
    public Dimension getPreferredSize () {
      return new Dimension(getHeight(), getHeight());
    }
  }
  
  static class ChooseFontPanel extends JPanel implements Runnable {
    static JFileChooser chooser;
    static {
      chooser = new JFileChooser() {{
        setFileFilter(new FileNameExtensionFilter("Font Files [.otf, .ttf]", "otf", "ttf"));
      }};
    }
    
    final JButton pathButton;
    final JTextField pathField;
    final JSpinner sizeSpinner;
    
    final String fontPathKey;
    final String fontSizeKey;
    final String originalPath;
    final int originalSize;
    
    ChooseFontPanel (String name, String fontPathKey, String fontSizeKey) {
      this.fontPathKey = fontPathKey;
      this.fontSizeKey = fontSizeKey;
      
      originalPath = Config.getString(fontPathKey);
      originalSize = Config.getInteger(fontSizeKey);
      
      setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), name));
      setLayout(new BorderLayout());
      
      final File file = new File(originalPath);
      
      add(pathButton = new PainlessButton(getOpenIcon()) {{
        addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent ae) {
            if (chooser.showOpenDialog(ChooseFontPanel.this.getParent()) == JFileChooser.APPROVE_OPTION) {
              pathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
          }
        });
      }}, BorderLayout.WEST);
      add(pathField = new GhostTextField("File path") {{
        setEditable(false);
        setText(file.getAbsolutePath());
      }}, BorderLayout.CENTER);
      add(sizeSpinner = new JSpinner() {
        {
          setModel(new SpinnerNumberModel(originalSize, 8, 800, 1));
          setEditor(new JSpinner.NumberEditor(this, "#.#"));
        }
        
        @Override
        public Dimension getPreferredSize () {
          return new Dimension(2 * getHeight(), getHeight());
        }
      }, BorderLayout.EAST);
    }

    @Override
    public void run() {
      if (!originalPath.equals(pathField.getText()) || originalSize != ((Integer)sizeSpinner.getValue())) {
        ConcurrentHashMap map = new ConcurrentHashMap();
        map.put(fontPathKey, pathField.getText());
        map.put(fontSizeKey, sizeSpinner.getValue());
        Config.put(map);
      }
    }
  }
  
  static class ChooseBackgroundPanel extends JPanel implements Runnable {
    static JFileChooser chooser;
    static AbstractColorChooserPanel colorChooserPanel;
    static {
      chooser = new JFileChooser() {{
        setFileFilter(new FileNameExtensionFilter("Image Files [.jpg, .jpeg, .png, .gif]", "jpg", "jpeg", "png", "gif"));
      }};
      colorChooserPanel = new JColorChooser().getChooserPanels()[0];
    }
    
    static String getColorString (Color c) {
      return "0x" + Integer.toHexString(c.getRGB() & 0xFFFFFF).toUpperCase();
    }
    
    final JButton pathButton;
    final JButton colorButton;
    final JTextField colorField;
    
    final String backgroundKey;
    
    final Color originalColor;
    final String originalPath;
    
    ChooseBackgroundPanel (String name, String backgroundKey) {
      this.backgroundKey = backgroundKey;
      
      String s = Config.getString(backgroundKey);
      
      Color _originalColor = null;
      String _originalPath = null;
      
      try {
        _originalColor = new Color(Integer.decode(s));
        _originalPath  = null;
      } catch (NumberFormatException nfe) {
        _originalColor = null;
        _originalPath  = s;
      } finally {
        originalColor = _originalColor;
        originalPath  = _originalPath;
      }
      
      setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), name));
      setLayout(new BorderLayout());
      
      add(pathButton = new PainlessButton(getOpenIcon()) {{
        addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent ae) {
            if (chooser.showOpenDialog(ChooseBackgroundPanel.this.getParent()) == JFileChooser.APPROVE_OPTION) {
              colorField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
          }
        });
      }}, BorderLayout.WEST);
      add(colorField = new GhostTextField("File path / Color hex") {{
        setEditable(false);
        if (originalColor != null) {
          colorChooserPanel.getColorSelectionModel().setSelectedColor(originalColor);
          setText(getColorString(originalColor));
        } else if (originalPath != null) {
          setText(originalPath);
        }
      }}, BorderLayout.CENTER);
      add(colorButton = new PainlessButton(null) {
        BufferedImage rainbow;
        
        {
          addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
              int returnValue = JOptionPane.showOptionDialog(
                ChooseBackgroundPanel.this.getParent(),
                colorChooserPanel,
                "Choose background color",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[] {"Ok"},
                null
              );
              
              if (returnValue == JOptionPane.OK_OPTION) {
                Color c = colorChooserPanel.getColorSelectionModel().getSelectedColor();
                if (c != null) {
                  colorField.setText(getColorString(c));
                }
              }
            }
          });
        }
        
        @Override
        protected void paintComponent (Graphics g2d) {
          super.paintComponent(g2d);
          
          int w = getWidth();
          int h = getHeight();
          
          if (rainbow == null || rainbow.getWidth() != w || rainbow.getHeight() != h) {
            rainbow = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            
            int[] arr = new int[w * h];
            int i = 0;
            
            for (int j = 0; j < h; j++) {
              int r = (j * 255) / (h - 1);
              for (int k = 0; k < w; k++) {
                int g = (k * 255) / (w - 1);
                int b = 128;
                arr[i++] = (r << 16) | (g << 8) | b;
              }
            }
            
            rainbow.setRGB(0, 0, w, h, arr, 0, w);
          }
          
          g2d.drawImage(rainbow, 0, 0, this);
        }
      }, BorderLayout.EAST);
    }

    @Override
    public void run() {
      int rgb = -1;
      
      try {
        rgb = Integer.decode(colorField.getText());
      } catch (NumberFormatException nfe) {}
      
      if ((originalPath != null && !originalPath.equals(colorField.getText())) ||
          (originalColor != null && (originalColor.getRGB() & 0xFFFFFF) != rgb)
      ) {
        Config.put(backgroundKey, colorField.getText());
      }
    }
  }
  
  static class ChooseNumberPanel extends JPanel implements Runnable {
    static final DecimalFormat decimalFormat;
    static {
      decimalFormat = new DecimalFormat("#.##");
    }
    
    final double minimumValue;
    final double maximumValue;
    final double gap;
    final JSlider numberSlider;
    final GhostTextField numberField;
    
    final String keyName;
    
    boolean isChanged;
    final double originalValue;
    
    ChooseNumberPanel (String name, final double minimumValue, final double maximumValue, final String keyName) {
      setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), name));
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      
      originalValue = Config.getDouble(keyName);
      
      this.keyName      = keyName;
      this.minimumValue = minimumValue;
      this.maximumValue = maximumValue;
      gap = maximumValue - minimumValue;
      
      add(numberSlider = new JSlider() {{
        setMinimum(normalize(minimumValue));
        setMaximum(normalize(maximumValue));
        setValue(normalize(originalValue));
        setPaintLabels(true);
        setPaintTrack(true);
        setAlignmentX(CENTER_ALIGNMENT);
        
        addChangeListener(new ChangeListener () {
          @Override
          public void stateChanged(ChangeEvent ce) {
            isChanged = true;
            setNumber(getValue());
          }
        });
      }});
      
      add(numberField = new GhostTextField(null) {{
        setColumns(5);
        setEditable(false);
        setOpaque(false);
        setAlignmentX(CENTER_ALIGNMENT);
      }});
      
      setNumber(normalize(originalValue));
    }
    
    int normalize (double d) {
      double offset = d - minimumValue;
      return (int)Math.round(offset * Integer.MAX_VALUE / gap);
    }
    
    double denormalize (int i) {
      double offset = gap / Integer.MAX_VALUE * i ;
      return minimumValue + offset;
    }
    
    void setNumber (int value) {
      numberField.setText(decimalFormat.format(100d * denormalize(value)) + "%");
    }
    
    double getValue () {
      return denormalize(numberSlider.getValue());
    }

    @Override
    public void run() {
      if (isChanged) {
        Config.put(keyName, getValue());
      }
    }
  }
}

//TODO: Release the streams from reading files for loading configs