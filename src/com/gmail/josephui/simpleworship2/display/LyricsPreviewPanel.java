package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.display.event.LyricsSelectionEvent;
import com.gmail.josephui.simpleworship2.display.event.LyricsSelectionListener;
import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.gmail.josephui.simpleworship2.models.Section;
import com.gmail.josephui.simpleworship2.models.Subsection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

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
    this(lyrics, fonts, false);
  }
  
  public LyricsPreviewPanel (Lyrics lyrics, final Font[] fonts, final boolean isProgram) {
    thisLyricsPreviewPanel = this;
    this.isProgram = isProgram;
    
    subsectionToPanelMap = new ConcurrentHashMap();
    this.lyrics = lyrics;
    this.fonts = fonts;
    
    for (Section section : lyrics.getSections()) {
      for (Subsection subsection : section.getSubsections()) {
        subsectionToPanelMap.put(subsection, new PreviewPanel(subsection, fonts, isProgram ? PreviewPanel.PROGRAM_TAG : PreviewPanel.PREVIEW_TAG));
      }
    }
    
    setLayout(new BorderLayout());
    
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
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

      if (!isProgram) {
        MainTabbedPane parent = (MainTabbedPane)getParent();
        LyricsPreviewPanel copy = new LyricsPreviewPanel(lyrics, fonts, true);
        //copy.setRenderDevice(currentRenderDevice);
        copy.selectFirstSubsection();
        //copy.setDisplayTag("Program", tagFont, new Color(0xFF0000), tagForegroundColor);
        //copy.isProgram = true;
        
        copy.lyricsPanel.selectSubsection(sectionIndex, subsectionIndex);
        copy.currentPreviewPanel.setAsCurrentPreviewPanel();

        SearchField.getInstance().setEnabled(false);
        MainSplitPane.getInstance().setBottomPanel(copy, lyrics.getTitle());
        SearchField.getInstance().setEnabled(true);
      }
  }
  
  public void showCurrentPreviewPanel () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    if (currentPreviewPanel != null) {
      currentPreviewPanel.setAsCurrentPreviewPanel();
    }
  }
  
  public void selectFirstSubsection () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    lyricsPanel.selectFirstSubsection();
  }
}
