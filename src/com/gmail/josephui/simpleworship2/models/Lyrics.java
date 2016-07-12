package com.gmail.josephui.simpleworship2.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
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
