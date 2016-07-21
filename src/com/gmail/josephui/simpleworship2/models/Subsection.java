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

public class Subsection {
  private final ArrayList<String> firstHalf;
  private final ArrayList<String> secondHalf;
  private final int maxLines;
  
  private static String getHalfToString (ArrayList<String> half) {
    String retString = "HALF { ";
    
    for (String s : half) {
      retString += "\n\t" + s;
    }
    
    return retString + "\n}";
  }
  
  private String getHalfAsHtml (ArrayList<String> half, boolean isFirst) {
      String retString = (isFirst) ? "<html><p align=\"left\">" : "<html><p align=\"right\">";
      
      int counter = 0;

      for (String s : half) {
          retString += s + "<br/>";
          counter++;
      }
      
      while (counter++ < maxLines) {
          retString += "<br/>";
      }
      
      return retString + "</p></html>";
  }
  
  public Subsection (List<String> firstHalf, List<String> secondHalf) {
    this.firstHalf  = new ArrayList(firstHalf);
    this.secondHalf = new ArrayList(secondHalf);
    
    maxLines = Math.max(firstHalf.size(), secondHalf.size());
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
