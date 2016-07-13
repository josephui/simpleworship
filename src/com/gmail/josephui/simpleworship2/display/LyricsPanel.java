package com.gmail.josephui.simpleworship2.display;

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

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class LyricsPanel extends JPanel{
  private final Lyrics lyrics;
  private ArrayList<SectionPanel> sectionPanels;
  
  public LyricsPanel (Lyrics lyrics) {
    this.lyrics = lyrics;
    
    
    sectionPanels = new ArrayList(lyrics.getSections().size());
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
    for (Section section : lyrics.getSections()) {
      SectionPanel sp = new SectionPanel(section);
      
      sectionPanels.add(sp);
      add(sp);
    }
  }
  
  private int maxPreferredWidth = 0;
  
  class SectionPanel extends JPanel {
    final Section section;
    final JList[] subsectionLists;
    
    SectionPanel (Section section) {
      this.section = section;
      
      List<String>[] firstHalfs  = new List[section.getSubsections().size()];
      List<String>[] secondHalfs = new List[section.getSubsections().size()];
      
      int i = 0;
      for (Subsection subsection : section.getSubsections()) {
        firstHalfs[i] = subsection.getFirstHalf();
        secondHalfs[i++] = subsection.getSecondHalf();
      }
      
      subsectionLists = new JList[] {
        new JList(firstHalfs),
        new JList(secondHalfs)
      };
      
      subsectionLists[1].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
      
      setBorder(
        BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder(section.getName()),
          BorderFactory.createEtchedBorder()
        )
      );
      
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      
      for (JList list : subsectionLists) {
        list.setBorder(null);
        list.setVisibleRowCount(section.getSubsections().size());
        
        add(new JScrollPane(list) {{
          setBorder(null);
        }});
      }
      System.out.println(getPreferredSize());
      System.out.println();
      setMaximumSize(new Dimension(getMaximumSize().width, getPreferredSize().height));
    }
  }
}
