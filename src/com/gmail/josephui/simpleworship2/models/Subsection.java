package com.gmail.josephui.simpleworship2.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class Subsection {
  private final ArrayList<String> firstHalf;
  private final ArrayList<String> secondHalf;
  
  private static String getHalfToString (ArrayList<String> half) {
    String retString = "HALF { ";
    
    for (String s : half) {
      retString += "\n\t" + s;
    }
    
    return retString + "\n}";
  }
  
  public Subsection (List<String> firstHalf, List<String> secondHalf) {
    this.firstHalf  = new ArrayList(firstHalf);
    this.secondHalf = new ArrayList(secondHalf);
  }
  
  public List<String> getFirstHalf () {
    return Collections.unmodifiableList(firstHalf);
  }
  
  public List<String> getSecondHalf () {
    return Collections.unmodifiableList(secondHalf);
  }
  
  public List<String>[] getAllHalves () {
    return new List[] {
      getFirstHalf(),
      getSecondHalf()
    };
  }
  
  public String toConsoleString () {
    return "\nFIRST " + getHalfToString(firstHalf) + "\n----------\nSECOND " + getHalfToString(secondHalf);
  }
}
