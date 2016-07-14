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
  
  private static String getHalfAsHtml (ArrayList<String> half, boolean isFirst) {
      String retString = (isFirst) ? "<html><p align=\"left\">" : "<html><p align=\"right\">";
      
      for (String s : half) {
          retString += s + "<br/>";
      }
      
      return retString + "</p></html>";
  }
  
  public Subsection (List<String> firstHalf, List<String> secondHalf) {
    this.firstHalf  = new ArrayList(firstHalf);
    this.secondHalf = new ArrayList(secondHalf);
  }
  
  public List<String> getFirstHalf () {
    return Collections.unmodifiableList(firstHalf);
  }
  
  public String getFirstHalfAsHtml () {
    return getHalfAsHtml(firstHalf, true);
  }
  
  public List<String> getSecondHalf () {
    return Collections.unmodifiableList(secondHalf);
  }
  
  public String getSecondHalfAsHtml () {
    return getHalfAsHtml(secondHalf, false);
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
