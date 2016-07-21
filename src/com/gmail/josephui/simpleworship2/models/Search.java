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

import com.gmail.josephui.simpleworship2.Main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Search {
  private static final int MAX_NUM_CACHED_RESULTS = 10000;
  
  private static final ConcurrentHashMap<String, List<Lyrics>> cacheMap;
  private static final ConcurrentLinkedQueue<String> cacheQueue;
  
  static {
    cacheMap   = new ConcurrentHashMap();
    cacheQueue = new ConcurrentLinkedQueue();
  }
  
  private static List<Lyrics> addToCache (String text, List<Lyrics> result) {
    if (cacheQueue.size() >= MAX_NUM_CACHED_RESULTS) {
      cacheMap.remove(cacheQueue.poll());
    }
    
    cacheMap.put(text, result);
    cacheQueue.offer(text);
    
    return result;
  }
  
  private static List<Lyrics> checkCache (String text) {
    return cacheMap.get(text);
  }
  
  public static List<Lyrics> findLyrics (String text) {
    List<Lyrics> cachedResult = checkCache(text);
    
    if (cachedResult != null) {
      return cachedResult;
    }
    
    List<Lyrics> allLyrics = Main.getAllLyrics();
    String lowerCaseText = text.toLowerCase();
    
    List<Lyrics> matchBeginningLyrics = new LinkedList();
    List<Lyrics> matchElsewhereLyrics = new LinkedList();
    List<Lyrics> matchContentLyrics = new LinkedList();
    
    for (Lyrics lyrics : allLyrics) {
      if (lyrics.getTitle().toLowerCase().startsWith(lowerCaseText)) {
        matchBeginningLyrics.add(lyrics);
      } else if (lyrics.getTitle().toLowerCase().contains(lowerCaseText)) {
        matchElsewhereLyrics.add(lyrics);
      } else if (isTextInLyricsContent(lyrics, lowerCaseText)) {
        matchContentLyrics.add(lyrics);
      }
    }
    
    ArrayList<Lyrics> randomAccessList = new ArrayList(matchBeginningLyrics.size() + matchElsewhereLyrics.size() + matchContentLyrics.size());
    randomAccessList.addAll(matchBeginningLyrics);
    randomAccessList.addAll(matchElsewhereLyrics);
    randomAccessList.addAll(matchContentLyrics);
    
    return addToCache(text, Collections.unmodifiableList(randomAccessList));
  }
  
  private static boolean isTextInLyricsContent (Lyrics lyrics, String text) {
    for (Section section : lyrics.getSections()) {
      for (Subsection subsection : section.getSubsections()) {
        for (List<String> half : subsection.getAllHalves()) {
          for (String line : half) {
            if (line.toLowerCase().contains(text)) {
              return true;
            }
          }
        }
      }
    }
    
    return false;
  }
}
