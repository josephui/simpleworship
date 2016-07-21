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

import com.gmail.josephui.simpleworship2.display.event.LyricsSelectionEvent;
import com.gmail.josephui.simpleworship2.display.event.LyricsSelectionListener;
import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.gmail.josephui.simpleworship2.models.Section;
import com.gmail.josephui.simpleworship2.models.Subsection;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LyricsPanel extends JPanel {
  private final LyricsPreviewPanel thisLyricsPreviewPanel;
  private final ArrayList<SectionPanel> sectionPanels;
  private final ArrayList<LyricsSelectionListener> lyricsSelectionListeners;
  
  public LyricsPanel (Lyrics lyrics, LyricsPreviewPanel lyricsPreviewPanel) {
    thisLyricsPreviewPanel = lyricsPreviewPanel;
    
    lyricsSelectionListeners = new ArrayList();
    sectionPanels = new ArrayList(lyrics.getSections().size());
    
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
    int index = 0;
    for (Section section : lyrics.getSections()) {
      SectionPanel sp = new SectionPanel(section, index++);
      
      sectionPanels.add(sp);
      add(sp);
    }
  }
  
  public void selectSubsection (int sectionIndex, int subsectionIndex) {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }
    
    SectionPanel.SubsectionList list = sectionPanels.get(sectionIndex).subsectionLists[0];
    list.setSelectedIndex(subsectionIndex);
    fireListSelectionListeners(list);
  }
  
  public void selectFirstSubsection () {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    if (!sectionPanels.isEmpty()) {
      SectionPanel panel = sectionPanels.get(0);
      if (panel.subsectionLists.length > 0) {
        SectionPanel.SubsectionList list = panel.subsectionLists[0];
        if (list.getModel().getSize() > 0) {
          list.setSelectedIndex(0);
          fireListSelectionListeners(list);
        }
      }
    }
  }
  
  private void fireListSelectionListeners (SectionPanel.SubsectionList list) {
    ListSelectionListener[] listeners = list.getListSelectionListeners();
      if (listeners.length > 0) {
        // So hackytimes
        listeners[0].valueChanged(new ListSelectionEvent(this, 0, 0, true));
      }
  }
  
  public void addLyricsSelectionListener (LyricsSelectionListener listener) {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    lyricsSelectionListeners.add(listener);
  }
  
  void updateSubsectionLists (Section selectedSection, SectionPanel.SubsectionList originList, int index) {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException ("Not invoked from eventDispatchThread");
    }

    for (SectionPanel sectionPanel : sectionPanels) {
      for (SectionPanel.SubsectionList subsectionList : sectionPanel.subsectionLists) {
        if (subsectionList == originList) {
          continue;
        }
        if (sectionPanel.section == selectedSection) {
          if (originList == null) {
            sectionPanel.subsectionLists[0].requestFocus();
          }
          subsectionList.setSelectedIndex(index);
        } else {
          subsectionList.clearSelection();
        }
      }
    }
  }
  
  class SectionPanel extends JPanel {
    final Section section;
    final int sectionIndex;
    final SubsectionList[] subsectionLists;
    
    SectionPanel (Section section, int sectionIndex) {
      this.section = section;
      this.sectionIndex = sectionIndex;
      
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
      
      setLayout(new GridLayout(1, 2));
      
      for (SubsectionList list : subsectionLists) {
        add(list);
      }
      
      setMaximumSize(new Dimension(getMaximumSize().width, getPreferredSize().height));
    }
    
    class SubsectionList extends JList {
        
      SubsectionList (Object[] list, ComponentOrientation co) {
        super(list);
        setComponentOrientation(co);

        setBorder(null);
        setVisibleRowCount(section.getSubsections().size());
        
        addListSelectionListener(new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
              fireListeners(e.getSource(), SubsectionList.this.getSelectedIndex());
            }
          }
        });
        
        addKeyListener(new KeyAdapter () {
          @Override
          public void keyPressed(KeyEvent e) {
            int index = SubsectionList.this.getSelectedIndex();
            switch (e.getKeyCode()) {
              case KeyEvent.VK_UP:
                fireListeners(e.getSource(), index-1);
                break;
              case KeyEvent.VK_DOWN:
                fireListeners(e.getSource(), index+1);
                break;
              case KeyEvent.VK_ENTER:
                if (thisLyricsPreviewPanel != null) {
                  thisLyricsPreviewPanel.setAsProgram(sectionIndex, index);
                }
                break;
            }
          }
        });
        
        addMouseListener(new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
              if (e.getClickCount() == 2 && thisLyricsPreviewPanel != null) {
                thisLyricsPreviewPanel.setAsProgram(sectionIndex, SubsectionList.this.getSelectedIndex());
              }
            }
        });
        
        setCellRenderer(new DefaultListCellRenderer() {
          @Override
          public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
              return super.getListCellRendererComponent(list, value, index, isSelected, false);
          }
        });
      }
      
      void fireListeners (Object source, int index) {
        Section s = section;
        int i = index;
        SubsectionList pointer = SubsectionList.this;
        
        if (index < 0) {
          if (section.getPrevSection() == null) {
            return;
          }
          
          s = section.getPrevSection();
          i = s.getSubsections().size()-1;
          pointer = null;
        } else if (index >= section.getSubsections().size()) {
          if (section.getNextSection() == null) {
            return;
          }
          
          s = section.getNextSection();
          i = 0;
          pointer = null;
        }
        
        updateSubsectionLists(s, pointer, i);

        for (LyricsSelectionListener listener : lyricsSelectionListeners) {
            listener.selectionChanged(new LyricsSelectionEvent(s, i, s.getSubsections().get(i)));
        }
      }
    }
  }
}
