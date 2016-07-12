package com.gmail.josephui.simpleworship2.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class Section {
  private final String name;
  private final ArrayList<Subsection> subsections;
  
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
  
  public String toConsoleString () {
    String retString = "\nNAME { " + name + " }";
    
    for (Subsection s : subsections) {
      retString += "\nSUBSECTION { " + s.toConsoleString().replaceAll("\n", "\n\t") + "\n}";
    }
    
    return retString;
  }
}
