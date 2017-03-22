/*
Copyright (C) 2013-2017 Pierre-François Gimenez

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package threads;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import config.Config;
import config.ConfigInfo;
import container.Container;
import container.dependances.GUIClass;
import graphic.ExternalPrintBuffer;
import graphic.Fenetre;
import utils.Log;

/**
 * Thread du serveur d'affichage
 * @author pf
 *
 */

public class ThreadPrintServer extends ThreadService implements GUIClass
{
	
	/**
	 * Thread qui envoie les données au socket donné en paramètre du constructeur
	 * @author pf
	 *
	 */
	private class ThreadSocket implements GUIClass, Runnable
	{
	
		protected Log log;
		private ExternalPrintBuffer buffer;
		private Socket socket;
		private int nb;
		
		public ThreadSocket(Log log, ExternalPrintBuffer buffer, Socket socket, int nb)
		{
			this.log = log;
			this.buffer = buffer;
			this.socket = socket;
			this.nb = nb;
		}
	
		@Override
		public void run()
		{
			Thread.currentThread().setName(getClass().getSimpleName()+"-"+nb);
			log.debug("Connexion d'un client au serveur d'affichage");
			try {
				while(true)
				{
					ObjectOutputStream out = null;
					out = new ObjectOutputStream(socket.getOutputStream());
					
					buffer.send(out);
					Thread.sleep(200); // on met à jour toutes les 200ms
				}
			} catch (InterruptedException | IOException e) {
				log.debug("Arrêt de "+Thread.currentThread().getName());
			}
		}
	
	}

	protected Log log;
	private ExternalPrintBuffer buffer;
	private boolean external;
	private int nbConnexions = 0;
	private List<Thread> threads = new ArrayList<Thread>();

	public ThreadPrintServer(Log log, ExternalPrintBuffer buffer, Config config)
	{
		this.log = log;
		this.buffer = buffer;
		external = config.getBoolean(ConfigInfo.GRAPHIC_EXTERNAL);
	}

	@Override
	public void run()
	{
		Thread.currentThread().setName(getClass().getSimpleName());
		log.debug("Démarrage de "+Thread.currentThread().getName());
		ServerSocket ssocket = null;
		try {
			if(!external)
			{
				log.debug(getClass().getSimpleName()+" annulé ("+ConfigInfo.GRAPHIC_EXTERNAL+" = "+external+")");
				while(true)
					Thread.sleep(10000);
			}
			
			ssocket = new ServerSocket(133742);
			while(true)
			{
				Thread t = new Thread(new ThreadSocket(log, buffer, ssocket.accept(), nbConnexions++));
				t.start();
				threads.add(t);
			}
		} catch (InterruptedException | IOException e) {
			if(ssocket != null)
				try {
					ssocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
			/*
			 * On arrête tous les threads de socket en cours
			 */
			for(Thread t : threads)
				t.interrupt();
			log.debug("Arrêt de "+Thread.currentThread().getName());
		}
	}

}
