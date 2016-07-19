package com.gmail.josephui.simpleworship2;

import com.gmail.josephui.simpleworship2.display.MainFrame;
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

/**
 *
 * @author Joseph Hui <josephui@gmail.com>
 */
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
  
  private static void loadConfigProperties (String configPath) throws IOException {
    FileInputStream input = null;
    
    try {
      config.load(input = new FileInputStream(configPath));
    } finally {
      if (input != null) {
        input.close();
      }
    }
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
  }
  
  public static String getProperty (String name) {
    return config.getProperty(name);
  }
  
  private static Object background;
          
  public static Object getBackground () {
      if (background != null) {
          return background;
      }
      
      String backgroundString = getProperty("background");
      
      try {
          int bgRgb = Integer.decode(backgroundString);
          return background = new Color(bgRgb);
      } catch (NumberFormatException e) {
          try {
            return background = ImageIO.read(new File(backgroundString));
          } catch (IOException ioe) {
              ioe.printStackTrace();
              return null;
          }
      }
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
    loadConfigProperties(configPath);
    
    String lyricsDirPath = getProperty("lyrics_dir_path");
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
      }
    });
  }
}
