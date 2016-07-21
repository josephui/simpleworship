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

public class Section {
  private final String name;
  private final ArrayList<Subsection> subsections;
  private Section prevSection;
  private Section nextSection;
  
  public Section (String name, List<Subsection> subsections) {
    this.name        = name;
    this.subsections = new ArrayList(subsections);
  }
  
  public String getName () {
    return name;
  }
  
  public List<Subsection> getSubsections () {
    return Collections.unmodifiableList(subsections);
  }
  
  public void setPrevSection (Section s) {
    prevSection = s;
  }
  
  public void setNextSection (Section s) {
    nextSection = s;
  }
  
  public Section getPrevSection () {
    return prevSection;
  }
  
  public Section getNextSection () {
    return nextSection;
  }
  
  public String toConsoleString () {
    String retString = "\nNAME { " + name + " }";
    
    for (Subsection s : subsections) {
      retString += "\nSUBSECTION { " + s.toConsoleString().replaceAll("\n", "\n\t") + "\n}";
    }
    
    return retString;
  }
}
