/*Author Pertti Peramaa
Date March 25,2015
/Author Suvendu Paul
Date April 8,2015 -- Added Application name for the Java Log File.
/Author Pertti Peramaa
Date April 9,2015
 there is possibility that broker looses logger instance and moving Logger to class variables we can prevent it.
 by declaring LOG as null
 *
 * Copyright (c) 2015 IBM All rights reserved.
 *
 * Created on 26.3.2015
 *
 * <pre>
 * Date $Date: 2015/11/11 08:01:00 $
 * CVS History:
 * $Log: DC.java,v $
 * Revision 1.1  2015/11/11 08:01:00  suvendu.paul
 * Decompress JavaApp
 *
 * </pre>
 * 
 * @author $Author: suvendu.paul $
 * @version $Revision: 1.1 $
 *
 */
package decomp;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public enum DC {
	INSTANCE;
	/**
	 * Trace
	 * 
	 */
	public static enum Trace {
		ALL(Level.ALL), //
		FINE(Level.FINE), //
		FINER(Level.FINER), //
		FINEST(Level.FINEST), //
		OFF(Level.OFF), //
		INFO(Level.INFO), //
		SEVERE(Level.SEVERE), //
		WARNING(Level.WARNING); //
		private Level level;
		

		/**
		 * constructor
		 * 
		 * @param level
		 */
		private Trace(Level level) {
			this.level = level;
		}

		public static Level getLevel(String s) {
			try {
				return valueOf(s).level;
			} catch (Exception e) {
				return Level.SEVERE;
			}
		}
	}

	public final static String LNAME = "decompressing.message";
	private String dir = null;  
	private String lvl = null;  
	private String fname = null; 
	private String AppName= null;
	//private Logger LOG = null;
	
	
	/**
	 * constructor
	 */
	private DC() {
	}

	/**
	 * createLogger
	 * 
	 */
	public void createLogger() {
		if (fname == null && lvl != null && dir != null) {

			LogManager.getLogManager().reset();
			Logger LOG = Logger.getLogger(DC.LNAME);
			//LOG = Logger.getLogger(DC.LNAME);
			Level level = DC.Trace.getLevel(DC.INSTANCE.getTracelevel());
			fname = DC.INSTANCE.getRoot() + "/" + DC.INSTANCE.getAppName() + "_decompress_%g.log";
			
			Handler handler;
			try {
				handler = new FileHandler(fname, 1024 * 1024 * 10, 3, true);
			} catch (IOException e) {
				handler = new ConsoleHandler();
			}
			handler.setFormatter(new Custformat());
			handler.setLevel(level);
			LOG.addHandler(handler);
			LOG.setLevel(level);
			LOG.logp(Level.INFO,DC.LNAME,"DC","--Initialized " + lvl + " " + dir);
			
		}
	}

	/* *GETTERS SETTERS* */
	public String getRoot() {
		return dir;
	}

	public String getTracelevel() {
		return this.lvl;  
	}

   public String getDir(){
	   return dir;
   }
    
   public void setDir(String dir){
	   this.dir = dir ;   
   }

   public String getLvl(){
	   return lvl;
   }
   
   public void setLvl(String lvl){
	   this.lvl = lvl ;    
   }
   public String getAppName(){
	   return AppName;
   }
   
   public void setAppName(String AppName){
	   this.AppName = AppName ;    
   }
   
}
