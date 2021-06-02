/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.graphic;

import pfg.log.Log;

/**
 * S'occupe d'enregistrer la vidéo
 * 
 * @author pf
 *
 */

public class ThreadSaveVideo extends Thread
{
	protected Log log;
	private GraphicDisplay buffer;

	public class ThreadSaveVideoOnShutdown extends Thread
	{
		@Override
		public void run()
		{
			Thread.currentThread().setName(getClass().getSimpleName());
			buffer.destructor();
		}
	}
	
	public ThreadSaveVideo(Log log, GraphicDisplay buffer)
	{
		this.log = log;
		this.buffer = buffer;
		Runtime.getRuntime().addShutdownHook(new ThreadSaveVideoOnShutdown());
		setDaemon(true);
	}

	@Override
	public void run()
	{
		Thread.currentThread().setName(getClass().getSimpleName());
//		log.write("Démarrage de " + Thread.currentThread().getName(), Subject.DUMMY);
		try
		{
			while(true)
			{
				Thread.sleep(100);
				buffer.saveState();
			}
		}
		catch(InterruptedException e)
		{
//			log.write("Arrêt de " + Thread.currentThread().getName(), Subject.DUMMY);
			Thread.currentThread().interrupt();
		}
	}

}