package com.gmail.josephui.simpleworship2.file;

import com.gmail.josephui.simpleworship2.models.Lyrics;
import com.gmail.josephui.simpleworship2.models.Section;
import com.gmail.josephui.simpleworship2.models.Subsection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
public class LyricsParser {
  public static final String LYRICS_FILE_EXTENTION = ".txt";
  private static final String SECTION_REGEX = "^\\[.*\\]$";
  
  private final File lyricsFile;
  
  public LyricsParser (File lyricsFile) throws IOException {
    this.lyricsFile = lyricsFile;
  }
  
  /**
   * First line: Song title
   * Subsequent lines:
   * [Brackets] - Name of a song section, e.g. [Chorus]
   * Empty line - Break between two subsections in a section
   * ---------- - Divider between two fonts, e.g. English, Chinese (Parser only checks for two --)
   * 
   * Sample Lyrics file:
   * Agnus Dei
   * [Verse]
   * Alleluia, Alleluia
   * For the Lord God Almighty Reigns
   * --------------------------------
   * SOME CHINESE LYRICS HERE
   * SOME MORE CHINESE LYRICS HERE
   * 
   * Alleluia, Alleluia
   * For the Lord God Almighty Reigns
   * Alleluia
   * --------------------------------
   * SOME CHINESE LYRICS HERE
   * SOME MORE CHINESE LYRICS HERE
   * EVEN MORE CHINESE LYRICS HERE
   * 
   * [Chorus]
   * Holy, holy
   * are You Lord God Almighty
   * Worthy is the Lamb
   * Worthy is the Lamb
   * --------------------------------
   * SOME CHINESE LYRICS HERE
   * SOME MORE CHINESE LYRICS HERE
   * EVEN MORE CHINESE LYRICS HERE
   * FINAL CHINESE LYRICS HERE
   * 
   * You are holy, holy 
   * are You Lord God Almighty
   * Worthy is the Lamb
   * Worthy is the Lamb 
   * Amen
   * --------------------------------
   * SOME CHINESE LYRICS HERE
   * SOME MORE CHINESE LYRICS HERE
   * EVEN MORE CHINESE LYRICS HERE
   * ENDLESSLY MORE CHINESE LYRICS HERE
   * FINAL CHINESE LYRICS HERE
   */
  public Lyrics parseLyrics () throws IOException {
    BufferedReader in = null;
    
    try {
      in = new BufferedReader(
        new InputStreamReader(
          new FileInputStream(lyricsFile),
          "UTF-16"
        )
      );

      String title = in.readLine();

      if (title == null) {
        throw new IllegalArgumentException("Corrupted Lyrics: No title");
      }

      String input = in.readLine();

      if (input == null) {
        throw new IllegalArgumentException("Corrupted Lyrics: No content");
      }

      if (!input.isEmpty() && !input.matches(SECTION_REGEX)) {
        title += "\n" + input;
        input = in.readLine();
      }

      LinkedList<Section> sections       = new LinkedList();
      LinkedList<Subsection> subsections = new LinkedList();

      Stack<String> buffer = new Stack();

      do {
        buffer.push(input);
      } while ((input = in.readLine()) != null);

      LinkedList<String>[] halfSubsections = new LinkedList[] {
        new LinkedList(), new LinkedList()
      };

      int currentSubsectionIndex = 1;

      while (!buffer.empty()) {
        if (buffer.peek().matches(SECTION_REGEX) || buffer.peek().isEmpty()) {
          if (halfSubsections[0].isEmpty()) {
            LinkedList<String> temp = halfSubsections[0];
            halfSubsections[0] = halfSubsections[1];
            halfSubsections[1] = temp;
          }

          if (!halfSubsections[0].isEmpty()) {
            subsections.addFirst(new Subsection(halfSubsections[0], halfSubsections[1]));

            halfSubsections[0].clear();
            halfSubsections[1].clear();

            currentSubsectionIndex = 1;
          }

          if (!buffer.peek().isEmpty()) {
            String name = buffer.peek().substring(1, buffer.peek().length()-1);
            Section thisSection = new Section(name, subsections);
            
            if (!sections.isEmpty()) {
              thisSection.setNextSection(sections.getFirst());
              sections.getFirst().setPrevSection(thisSection);
            }
            
            sections.addFirst(thisSection);

            subsections.clear();
          }
        } else if (buffer.peek().startsWith("--")) {
          if (currentSubsectionIndex == 0) {
            throw new IllegalArgumentException ("Corrupted Lyrics: Cannot have three halfs in a subsection");
          }

          currentSubsectionIndex--;
        } else {
          halfSubsections[currentSubsectionIndex].addFirst(buffer.peek());
        }

        buffer.pop();
      }

      return new Lyrics(title, sections);
      
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }
}
