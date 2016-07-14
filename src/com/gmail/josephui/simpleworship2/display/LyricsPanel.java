package com.gmail.josephui.simpleworship2.display;

import com.gmail.josephui.simpleworship2.display.event.LyricsSelectionEvent;
import com.gmail.josephui.simpleworship2.display.event.LyricsSelectionListener;
import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.gmail.josephui.simpleworship2.models.Section;
import com.gmail.josephui.simpleworship2.models.Subsection;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class LyricsPanel extends JPanel {
  private final Lyrics lyrics;
  private ArrayList<SectionPanel> sectionPanels;
  private ArrayList<LyricsSelectionListener> lyricsSelectionListeners;
  
  public LyricsPanel (Lyrics lyrics) {
    this.lyrics = lyrics;
    
    lyricsSelectionListeners = new ArrayList();
    sectionPanels = new ArrayList(lyrics.getSections().size());
    
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
    for (Section section : lyrics.getSections()) {
      SectionPanel sp = new SectionPanel(section);
      
      sectionPanels.add(sp);
      add(sp);
    }
  }
  
  public void addLyricsSelectionListener (LyricsSelectionListener listener) {
      lyricsSelectionListeners.add(listener);
  }
  
  void updateSubsectionLists (Section selectedSection, SectionPanel.SubsectionList selectedSubsection) {
    for (SectionPanel sectionPanel : sectionPanels) {
        for (SectionPanel.SubsectionList subsectionList : sectionPanel.subsectionLists) {
            if (subsectionList == selectedSubsection) {
              continue;
            }
            
            if (sectionPanel.section == selectedSection) {
                subsectionList.setSelectedIndex(selectedSubsection.getSelectedIndex());
            } else {
                subsectionList.clearSelection();
            }
        }
    }
  }
  
  class SectionPanel extends JPanel {
    final Section section;
    final SubsectionList[] subsectionLists;
    
    SectionPanel (Section section) {
      this.section = section;
      
      String[] firstHalfs  = new String[section.getSubsections().size()];
      String[] secondHalfs = new String[section.getSubsections().size()];
      
      int i = 0;
      for (Subsection subsection : section.getSubsections()) {
        firstHalfs[i] = subsection.getFirstHalfAsHtml();
        secondHalfs[i++] = subsection.getSecondHalfAsHtml();
      }
      
      subsectionLists = new SubsectionList[] {
        new SubsectionList(firstHalfs, ComponentOrientation.LEFT_TO_RIGHT),
        new SubsectionList(secondHalfs, ComponentOrientation.RIGHT_TO_LEFT)
      };
      
      setBorder(
        BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder(section.getName()),
          BorderFactory.createEtchedBorder()
        )
      );
      
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      
      for (SubsectionList list : subsectionLists) {
        add(new JScrollPane(list) {{
          setBorder(null);
        }});
      }
      
      setMaximumSize(new Dimension(getMaximumSize().width, getPreferredSize().height));
    }
    
    class SubsectionList extends JList {
      SubsectionList self;
        
      SubsectionList (Object[] list, ComponentOrientation co) {
        super(list);
        setComponentOrientation(co);
        
        self = this;

        setBorder(null);
        setVisibleRowCount(section.getSubsections().size());
        
        addListSelectionListener(new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                updateSubsectionLists(section, self);
                
                for (LyricsSelectionListener listener : lyricsSelectionListeners) {
                    listener.selectionChanged(new LyricsSelectionEvent(e.getSource(), self.getSelectedIndex(), section.getSubsections().get(self.getSelectedIndex())));
                }
            }
          }
        });
      }
    }
  }
}
