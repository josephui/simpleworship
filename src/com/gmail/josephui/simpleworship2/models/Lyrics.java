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

package com.gmail.josephui.simpleworship2.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lyrics {
  private final String title;
  private final ArrayList<Section> sections;

  public Lyrics (String title, List<Section> sections) {
    this.title    = title;
    this.sections = new ArrayList(sections);
  }
  
  public String getTitle () {
    return title;
  }
  
  public List<Section> getSections () {
    return Collections.unmodifiableList(sections);
  }
  
  public String toConsoleString () {
    String retString = "TITLE { " + title + " }";
    
    for (Section s : sections) {
      retString += "\nSECTION { " + s.toConsoleString().replaceAll("\n", "\n\t") + "\n}";
    }
    
    return retString;
  }
  
  @Override
  public String toString () {
    return title.replace("\n", " / ");
  }
}
