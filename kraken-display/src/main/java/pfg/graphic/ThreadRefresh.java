/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.graphic;

import pfg.graphic.GraphicDisplay;
import pfg.log.Log;

/**
 * S'occupe de la mise à jour graphique
 * 
 * @author pf
 *
 */

public class ThreadRefresh extends Thread
{

	protected Log log;
	private WindowFrame fenetre;
	private GraphicDisplay buffer;
	private volatile int period;

	public ThreadRefresh(Log log, GraphicDisplay buffer, WindowFrame fenetre)
	{
		this.fenetre = fenetre;
		this.log = log;
		this.buffer = buffer;
		setFrequency(50);
		setDaemon(true);
	}
	
	public synchronized void setFrequency(double frequency)
	{
		period = (int) Math.round(1000/frequency);
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
				synchronized(buffer)
				{
					if(!buffer.needRefresh())
						buffer.wait();
				}
				fenetre.refresh();
				Thread.sleep(period);
			}
		}
		catch(InterruptedException e)
		{
//			System.out.println("Arrêt de " + Thread.currentThread().getName());
			Thread.currentThread().interrupt();
		}
		catch(Exception e)
		{
//			log.write("Arrêt inattendu de " + Thread.currentThread().getName() + " : " + e, Subject.DUMMY);
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

}
