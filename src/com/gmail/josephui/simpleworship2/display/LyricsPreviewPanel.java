package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.Main;
import com.gmail.josephui.simpleworship2.display.event.LyricsSelectionEvent;
import com.gmail.josephui.simpleworship2.display.event.LyricsSelectionListener;
import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.gmail.josephui.simpleworship2.models.Section;
import com.gmail.josephui.simpleworship2.models.Subsection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class LyricsPreviewPanel extends JPanel {
  private LyricsPanel lyricsPanel;
  private Lyrics lyrics;
  private ConcurrentHashMap<Subsection, PreviewPanel> subsectionToPanelMap;
  
  private LyricsPreviewPanel thisLyricsPreviewPanel;
  private GraphicsDevice currentRenderDevice;
  private PreviewPanel currentPreviewPanel;
  private Font[] fonts;
  
  private Font tagFont;
  private Color tagForegroundColor;
  
  private boolean isProgram;
  
  public LyricsPreviewPanel (Lyrics lyrics, Font[] fonts) {
    thisLyricsPreviewPanel = this;
    isProgram = false;
    
    subsectionToPanelMap = new ConcurrentHashMap();
    this.lyrics = lyrics;
    this.fonts = fonts;
    
    for (Section section : lyrics.getSections()) {
      for (Subsection subsection : section.getSubsections()) {
        subsectionToPanelMap.put(subsection, new PreviewPanel());
      }
    }
    
    setLayout(new BorderLayout());
    
      /*add(new JScrollPane(sp) {{
        setBorder(null);
      }});*/
    add(new JScrollPane(lyricsPanel = new LyricsPanel(lyrics, thisLyricsPreviewPanel))  {
      {
        setBorder(null);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
      }
      
      @Override
      public Dimension getPreferredSize () {
          Dimension withoutScrollBars = super.getPreferredSize();
          return new Dimension(withoutScrollBars.width + getVerticalScrollBar().getPreferredSize().width,
            withoutScrollBars.height + getHorizontalScrollBar().getPreferredSize().height);
      }
    }, BorderLayout.WEST);
    
    lyricsPanel.addLyricsSelectionListener(new LyricsSelectionListener () {
      @Override
      public void selectionChanged(LyricsSelectionEvent e) {
        PreviewPanel oldPreviewPanel = currentPreviewPanel;
        
        if (oldPreviewPanel != null) {
          remove(oldPreviewPanel);
          
        }
            
        add(currentPreviewPanel = subsectionToPanelMap.get(e.getSubsection()), BorderLayout.CENTER);
        
        if (isProgram && OptionPanel.getInstance().isLive()) {
          currentPreviewPanel.setAsCurrentPreviewPanel();
          PreviewPanel.repaintRenderedWindow();
          //currentPreviewPanel.setRenderedWindowVisible(true);
          if (oldPreviewPanel != null) {
          //  oldPreviewPanel.setRenderedWindowVisible(false);
          }
        }
        
        currentPreviewPanel.revalidate();
        invalidate();
        repaint();
      }
    });
    
    lyricsPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          return;
        }
      }
    });
  }
  
  public void setAsProgram (int sectionIndex, int subsectionIndex) {
      if (!isProgram) {
        MainTabbedPane parent = (MainTabbedPane)getParent();
        LyricsPreviewPanel copy = new LyricsPreviewPanel(lyrics, fonts);
        copy.setRenderDevice(currentRenderDevice);
        copy.selectFirstSubsection();
        copy.setDisplayTag("Program", tagFont, new Color(0xFF0000), tagForegroundColor);
        copy.isProgram = true;
        
        copy.lyricsPanel.selectSubsection(sectionIndex, subsectionIndex);
        copy.currentPreviewPanel.setAsCurrentPreviewPanel();

        SearchField.getInstance().setEnabled(false);
        MainSplitPane.getInstance().setBottomPanel(copy, lyrics.getTitle());
        SearchField.getInstance().setEnabled(true);
      }
  }
  
  public void selectFirstSubsection () {
    lyricsPanel.selectFirstSubsection();
  }
  
  public void setRenderDevice (GraphicsDevice device) {
    currentRenderDevice = device;
      
    for (Subsection subsection : subsectionToPanelMap.keySet()) {
      PreviewPanel previewPanel = subsectionToPanelMap.get(subsection);
      
      previewPanel.setRenderedDevice(device);
    /*    
      try {
        previewPanel.setRenderedWindowBackground(ImageIO.read(new File("test-img.png")));
      } catch (Exception e) {}
    */
      Object background = Main.getBackground();
      
      if (background instanceof Color) {
          previewPanel.setRenderedWindowBackground((Color)background);
      } else if (background instanceof Image) {
          previewPanel.setRenderedWindowBackground((Image)background);
      }
      
      previewPanel.setSubsection(subsection, fonts);
    }
  }
  
  public void setDisplayTag (String text, Font font, Color backgroundColor, Color foregroundColor) {
    tagForegroundColor = foregroundColor;
    tagFont = font;
      
    for (PreviewPanel previewPanel : subsectionToPanelMap.values()) {
      previewPanel.setDisplayTag(text, font, backgroundColor, foregroundColor);
    }
  }
}
