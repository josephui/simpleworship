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

package com.gmail.josephui.simpleworship2.display.event;

import com.gmail.josephui.simpleworship2.display.PreviewPanel;
import com.gmail.josephui.simpleworship2.models.Subsection;
import javax.swing.event.ListSelectionEvent;

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
