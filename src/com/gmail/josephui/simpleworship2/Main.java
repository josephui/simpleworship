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

package com.gmail.josephui.simpleworship2;

import com.gmail.josephui.simpleworship2.display.MainFrame;
import com.gmail.josephui.simpleworship2.display.SearchField;
import com.gmail.josephui.simpleworship2.file.LyricsParser;
import com.gmail.josephui.simpleworship2.models.Lyrics;
import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class Main {
  public static final String APPLICATION_NAME        = "SimpleWorship2";
  public static final String DEFAULT_CONFIG_PATH     = "config.properties";
  public static final String DEFAULT_LYRICS_DIR_PATH = "lyrics";
  
  private static final Properties config;
  private static final ArrayList<Lyrics> allLyrics;
  
  static {
    config = new Properties();
    allLyrics = new ArrayList();
  }
  
  private static void loadLyrics (String lyricsDirPath) throws IOException {
    File lyricsDir = new File(lyricsDirPath);
    
    if (!lyricsDir.exists()) {
      throw new IllegalArgumentException("`" + lyricsDirPath + "` does not exists");
    }
    
    if (!lyricsDir.isDirectory()) {
      throw new IllegalArgumentException("`" + lyricsDirPath + "` is not a directory");
    }
    
    for (File file : lyricsDir.listFiles(new FileFilter () {
      @Override
      public boolean accept(File f) {
        return f.getPath().toLowerCase().endsWith(LyricsParser.LYRICS_FILE_EXTENTION);
      }
    })) {
      try {
        System.out.println("Parsing `" + file.getPath() + "`");
        
        Lyrics lyrics = new LyricsParser(file).parseLyrics();
        allLyrics.add(lyrics);
        
        //System.out.println(lyrics.toConsoleString());
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } catch (IllegalArgumentException iae) {
        iae.printStackTrace();
      }
    }
  }
  
  public static void setProperty (String name, String value) {
    config.setProperty(name, value);
    // Save using Writer to config file
  }
  
  public static List<Lyrics> getAllLyrics () {
    return Collections.unmodifiableList(allLyrics);
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws IOException {
    String configPath = DEFAULT_CONFIG_PATH;
    if (args.length > 0) {
      configPath = args[0];
    }
    Config.setConfigPath(configPath);
    Config.load();
    
    String lyricsDirPath = Config.getString("lyrics_dir_path");
    if (lyricsDirPath == null) {
      lyricsDirPath = DEFAULT_LYRICS_DIR_PATH;
    }
    loadLyrics(lyricsDirPath);
    
    new Main();
    //System.out.println(new LyricsParser(new File("C:\\sermons\\input2.txt")).parseLyrics());
  }
  
  /*--------------------------------------------------------------------------*/
  
  private Main () {
    SwingUtilities.invokeLater(new Runnable () {
      @Override
      public void run() {
        MainFrame.getInstance().setVisible(true);
        SearchField.getInstance().requestFocusInWindow();
      }
    });
  }
}
