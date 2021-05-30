package pfg.graphic;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.PriorityQueue;

import pfg.config.Config;
import pfg.kraken.display.ColoredPrintable;
import pfg.log.Log;

public class ThreadPrintClient extends Thread
{
	protected Log log;
	private GraphicDisplay buffer;
	private String hostname;
	private Config config;
	
	public ThreadPrintClient(Log log, GraphicDisplay buffer, Config config)
	{
		this.log = log;
		this.buffer = buffer;
		this.config = config;
		setDaemon(true);
	}
	
	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}
	
	@Override
	public void run()
	{
		try {
			assert hostname != null;
			InetAddress rpiAdresse = null;
			boolean loop = false;
			System.out.println("Démarrage du client d'affichage");
			try
			{
				String[] s = hostname.split("\\."); // on découpe avec les points
				if(s.length == 4) // une adresse ip, probablement
				{
					System.out.println("Recherche du serveur à partir de son adresse ip : " + hostname);
					byte[] addr = new byte[4];
					for(int j = 0; j < 4; j++)
						addr[j] = Byte.parseByte(s[j]);
					rpiAdresse = InetAddress.getByAddress(addr);
				}
				else // le nom du serveur, probablement
				{
					System.out.println("Recherche du serveur à partir de son nom : " + hostname);
					rpiAdresse = InetAddress.getByName(hostname);
				}
			}
			catch(UnknownHostException e)
			{
				System.err.println("La recherche du serveur a échoué ! " + e);
				return;
			}
	
			Socket socket = null;
			do
			{
	
				boolean ko;
				System.out.println("Tentative de connexion…");
	
				do
				{
					try
					{
						socket = new Socket(rpiAdresse, config.getInt(ConfigInfoGraphic.GRAPHIC_SERVER_PORT_NUMBER));
						socket.setTcpNoDelay(true);
						ko = false;
					}
					catch(IOException e)
					{
						Thread.sleep(100); // on attend un peu avant de
											// réessayer
						ko = true;
					}
				} while(ko);
	
				System.out.println("Connexion réussie !");
				ObjectInputStream in;
				try
				{
					in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
				}
				catch(IOException e)
				{
					System.err.println("Le serveur a coupé la connexion : " + e);
					continue; // on relance la recherche
				}
	
				try
				{
					while(true)
					{
						@SuppressWarnings("unchecked")
						PriorityQueue<ColoredPrintable> tab = (PriorityQueue<ColoredPrintable>) in.readObject();
//						System.out.println("Réception de "+tab.hashCode()+" "+tab.size());
//						for(ColoredPrintable c : tab)
//							System.out.println("  "+c);
						buffer.updatePrintable(tab);
						buffer.refresh();
					}
				}
				catch(IOException e)
				{
					System.err.println("Le serveur a coupé la connexion : " + e);
//					e.printStackTrace();
				}
				catch(ClassNotFoundException e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						in.close();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
	
			} while(loop);
	
			try
			{
				socket.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Arrêt du client d'affichage");
		}
		catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
}
