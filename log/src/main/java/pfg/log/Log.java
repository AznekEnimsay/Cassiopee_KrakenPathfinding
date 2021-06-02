/*
 * Copyright (C) 2013-2018% Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import pfg.config.Config;

/**
 * Service de log
 * 
 * @author pf
 *
 */

public class Log
{
	private static volatile boolean logClosed = false;
	private static volatile BufferedWriter writer = null;
	private static int totalInstancesNb = 0;
	private int instanceNb;
	private String name;
	private boolean save;
	private Config config;

	// Ecriture plus rapide sans appel à la pile d'exécution
	private boolean fastLog = false;
	private boolean stdoutLog;
	private Severity defaultSeverity;

	/**
	 * date du démarrage
	 */
	private static final long dateInitiale = System.currentTimeMillis();
	
	public Log(Severity defaultSeverity, String configFilename, String... configprofile)
	{
		this.defaultSeverity = defaultSeverity;
		
		config = new Config(ConfigInfoLog.values(), false, configFilename, configprofile);
		fastLog = config.getBoolean(ConfigInfoLog.FAST_LOG);
		stdoutLog = config.getBoolean(ConfigInfoLog.STDOUT_LOG);
		save = config.getBoolean(ConfigInfoLog.SAVE_LOG);
		instanceNb = totalInstancesNb++;
		StackTraceElement elem = Thread.currentThread().getStackTrace()[2];
		name = elem.getClassName().substring(elem.getClassName().lastIndexOf(".") + 1);
		if(save && writer == null)
		{
			Runtime.getRuntime().addShutdownHook(new ThreadCloseOnShutdown(this));
			try
			{
				writer = new BufferedWriter(new FileWriter("last.txt"));
			}
			catch(IOException e)
			{
				System.err.println("Erreur lors de la création du fichier : " + e);
			}
		}
	}
	
	public void write(String message, LogCategory categorie)
	{
		write_(message, defaultSeverity, categorie);
	}

	public void write(Object message, LogCategory categorie)
	{
		write_(message == null ? "null" : message.toString(), defaultSeverity, categorie);
	}

	public void write(Object message, Severity niveau, LogCategory categorie)
	{
		write_(message == null ? "null" : message.toString(), niveau, categorie);
	}
	
	public void write(String message, Severity niveau, LogCategory categorie)
	{
		write_(message, niveau, categorie);
	}
	
	/**
	 * Ce synchronized peut ralentir le programme, mais s'assure que les logs ne
	 * se chevauchent pas.
	 * 
	 * @param niveau
	 * @param message
	 * @param couleur
	 * @param ou
	 */
	private void write_(String message, Severity niveau, LogCategory categorie)
	{
		synchronized(Log.class)
		{
			if(!logClosed)
			{
				long date = System.currentTimeMillis() - dateInitiale;
	
				String affichage;
				if(fastLog)
					affichage = date + " "+ niveau + " " + categorie + " > " + message;
				else
				{
					StackTraceElement elem = Thread.currentThread().getStackTrace()[3];
					affichage = date + " "+ niveau + " " + categorie + " " + elem.getClassName().substring(elem.getClassName().lastIndexOf(".") + 1) + ":" + elem.getLineNumber() + " (" + Thread.currentThread().getName() + ") > " + message;
				}
	
				if(stdoutLog && (categorie.shouldPrint() || niveau.alwaysPrint()))
					System.out.println(affichage);
				if(save)
				{
					try
					{
						writer.write(name + " " +instanceNb + " " + categorie.getMask() + " " + affichage + "\n");
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Log closing ; save the log file if necessray.
	 */
	private void close()
	{
		assert !logClosed;
		assert writer != null;
		if(!logClosed && writer != null)
		{
			System.out.println("Sauvegarde du log");
			try
			{
				writer.flush();
				writer.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		logClosed = true;
	}

	public class ThreadCloseOnShutdown extends Thread
	{
		private Log log;

		public ThreadCloseOnShutdown(Log log)
		{
			this.log = log;
		}

		@Override
		public void run()
		{
			Thread.currentThread().setName(getClass().getSimpleName());
			log.close();
		}
	}

	public void displayOverriddenConfigValues() {
		config.printChangedValues();
	}
}
