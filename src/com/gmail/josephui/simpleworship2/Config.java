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

import com.gmail.josephui.simpleworship2.event.ConfigChangeEvent;
import com.gmail.josephui.simpleworship2.event.ConfigChangeListener;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Config {
  private static volatile String configPath;
  private static final Properties config;
  private static final ConcurrentHashMap<String, LinkedList<ConfigChangeListener>> keyToListenerMap;
  
  static {
    config = new Properties();
    keyToListenerMap = new ConcurrentHashMap<>();
  }
  
  public static void setConfigPath (String configPath) {
    Config.configPath = configPath;
  }
  
  public static void load () throws IOException {
    FileReader reader = null;
    
    try {
      config.load(reader = new FileReader(configPath));
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }
  
  public static void store () throws IOException {
    PrintWriter writer = null;
    
    try {
      config.store(writer = new PrintWriter(configPath), null);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }
  
  public static Object get (String key) {
    return config.get(key);
  }
  
  public static String getString (String key) {
    Object o = config.get(key);
    
    if (o instanceof String) {
      return (String)o;
    }
    
    return null;
  }
  
  public static Integer getInteger (String key) {
    String s = getString(key);
    
    if (s == null) {
      return null;
    }
    
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }
  
  public static Double getDouble (String key) {
    String s = getString(key);
    
    if (s == null) {
      return null;
    }
    
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }
  
  public static void addConfigChangeListener (String[] keys, ConfigChangeListener listener) {
    for(String key : keys) {
      if (keyToListenerMap.get(key) == null) {
        keyToListenerMap.put(key, new LinkedList());
      }
      
      keyToListenerMap.get(key).add(listener);
    }
  }
  
  public static void putAndStore (String key, Object value) throws IOException {
    put(key, value);
    
    store();
  }
  
  public static void putAndStore (Map<String, Object> map) throws IOException {
    put(map);
    
    store();
  }
  
  public static void put (String key, Object value) {
    ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
    
    map.put(key, value);
    
    put(map);
  }
  
  public static void put (Map<String, Object> map) {
    Set<String> keys = map.keySet();
    
    for (String key : keys) {
      config.put(key, map.get(key).toString());
    }
    
    fireListeners(map);
  }
  
  private static void fireListeners (Map<String, Object> map) {
    Set<String> keys = map.keySet();
    HashSet<ConfigChangeListener> listeners = new HashSet<>();
    
    for (String key : keys) {
      LinkedList<ConfigChangeListener> list = keyToListenerMap.get(key);
      
      if (list != null) {
        listeners.addAll(list);
      }
    }
    
    for (ConfigChangeListener listener : listeners) {
      listener.configChanged(new ConfigChangeEvent(map));
    }
  }
  
  /*--------------------------------------------------------------------------*/
  
  private Config () {}
}
