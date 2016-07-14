package com.gmail.josephui.simpleworship2.display.event;

import com.gmail.josephui.simpleworship2.display.PreviewPanel;
import com.gmail.josephui.simpleworship2.models.Subsection;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class LyricsSelectionEvent extends ListSelectionEvent {
    private Subsection subsection;
    
    public LyricsSelectionEvent (Object source, int index, Subsection subsection) {
        super(source, index, index, true);
        this.subsection = subsection;
    }
    
    public Subsection getSubsection () {
        return subsection;
    }
}
