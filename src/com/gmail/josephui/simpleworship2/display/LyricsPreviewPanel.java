package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.display.event.LyricsSelectionEvent;
import com.gmail.josephui.simpleworship2.display.event.LyricsSelectionListener;
import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.gmail.josephui.simpleworship2.models.Section;
import com.gmail.josephui.simpleworship2.models.Subsection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class LyricsPreviewPanel extends JPanel {
  private LyricsPanel lyricsPanel;
  private ConcurrentHashMap<Subsection, PreviewPanel> subsectionToPanelMap;
  private PreviewPanel currentPreviewPanel;
  private Font[] fonts;
  
  public LyricsPreviewPanel (Lyrics lyrics, Font[] fonts) {
    subsectionToPanelMap = new ConcurrentHashMap();
    this.fonts = fonts;
    
    for (Section section : lyrics.getSections()) {
      for (Subsection subsection : section.getSubsections()) {
        subsectionToPanelMap.put(subsection, new PreviewPanel());
      }
    }
    
    setLayout(new BorderLayout());
    
    add(lyricsPanel = new LyricsPanel(lyrics), BorderLayout.WEST);
    
    lyricsPanel.addLyricsSelectionListener(new LyricsSelectionListener () {
        @Override
        public void selectionChanged(LyricsSelectionEvent e) {
            //System.out.println("New lyrics is: " + e.getSubsection().toConsoleString());
            if (currentPreviewPanel != null) {
                remove(currentPreviewPanel);
            }
            
            add(
                //new JScrollPane(
                    currentPreviewPanel = subsectionToPanelMap.get(e.getSubsection())
                //)
                ,
                BorderLayout.CENTER
            );
            //System.out.println(currentPreviewPanel);
            //System.out.println(currentPreviewPanel.getPreferredSize());
            //currentPreviewPanel.setRenderedWindowVisible(true);
            revalidate();
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
  
  public void setRenderDevice (GraphicsDevice device) {
      for (Subsection subsection : subsectionToPanelMap.keySet()) {
          PreviewPanel previewPanel = subsectionToPanelMap.get(subsection);
          
          previewPanel.setRenderedDevice(device);
      /*    
              try {
      previewPanel.setRenderedWindowBackground(ImageIO.read(new File("test-img.png")));
    } catch (Exception e) {}
    */
          previewPanel.setRenderedWindowBackground(Color.GRAY);
          previewPanel.setSubsection(subsection, fonts);
      }
  }
}
