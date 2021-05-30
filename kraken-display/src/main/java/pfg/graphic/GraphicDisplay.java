/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.graphic;

import java.awt.Color;
import java.awt.Graphics;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.PriorityQueue;

import pfg.kraken.display.ColoredPrintable;
import pfg.kraken.display.Printable;
import pfg.kraken.struct.XY;

/**
 * Buffer de ce qu'il faut afficher
 * 
 * @author pf
 *
 */

class GraphicDisplay
{	
	private class ColoredPrintableComparator implements Comparator<ColoredPrintable>, Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(ColoredPrintable arg0, ColoredPrintable arg1)
		{
			return arg0.l - arg1.l;
		}
	}
	
	private PriorityQueue<ColoredPrintable> printables = new PriorityQueue<ColoredPrintable>(500, new ColoredPrintableComparator());
	private volatile boolean needRefresh = false, needSave = false;
	private TimestampedList sauvegarde;
	private WindowFrame f;
	private XY center;
	private String filename;
	
	public GraphicDisplay(XY defaultCenter, XY center)
	{
		this.center = center;
		sauvegarde = new TimestampedList(System.currentTimeMillis(), defaultCenter);
		filename = "videos/" + new SimpleDateFormat("dd-MM.HH:mm").format(new Date()) + ".dat";
	}
	
	void setWindowFrame(WindowFrame f)
	{
		this.f = f;
	}
	
	/**
	 * Force the refresh
	 */
	public void refresh()
	{
		if(f != null)
			f.refresh();
	}
	
	public synchronized void updatePrintable(PriorityQueue<ColoredPrintable> l)
	{
		printables = l;
		needRefresh = true;
		needSave = true;
	}
	
	/**
	 * Supprime tous les obstacles supprimables
	 * 
	 * @param c
	 */
	public synchronized void clearTemporaryPrintables()
	{
		Iterator<ColoredPrintable> iter = printables.iterator();
		while(iter.hasNext())
			if(iter.next().temporary)
				iter.remove();
		notify();
		needRefresh = true;
		needSave = true;
	}

	/**
	 * Ajoute un obstacle dans la liste des supprimables
	 * 
	 * @param o
	 */
	public synchronized void addTemporaryPrintable(Printable o, Color c, int layer)
	{
		printables.add(new ColoredPrintable(o, c, layer, true));
		notify();
		needRefresh = true;
		needSave = true;
	}

	/**
	 * Ajoute un obstacle
	 * 
	 * @param o
	 */
	public synchronized void addPrintable(Printable o, Color c, int layer)
	{
		printables.add(new ColoredPrintable(o, c, layer, false));
		notify();
		needRefresh = true;
		needSave = true;
	}

	/**
	 * Affiche tout
	 * 
	 * @param g
	 * @param f
	 * @param robot
	 */
	synchronized void print(Graphics g, GraphicPanel f)
	{
		needRefresh = false;
		Iterator<ColoredPrintable> iter = printables.iterator();
		while(iter.hasNext())
		{
			ColoredPrintable p = iter.next();
			p.print(g, f);
		}
	}
	
	/**
	 * Unregister a printable
	 * @param o
	 */
	public synchronized boolean removeColoredPrintable(ColoredPrintable o)
	{
		needSave = true;
		needRefresh = true;
		return printables.remove(o);
	}
	
	public synchronized boolean removePrintable(Printable o)
	{
		needSave = true;
		needRefresh = true;
		boolean out = false;
		Iterator<ColoredPrintable> iter = printables.iterator();
		while(iter.hasNext())
		{
			ColoredPrintable n = iter.next();
			if(n.p.equals(o))
			{
				out = true;
				iter.remove();
			}
		}
		return out;
	}

	boolean needRefresh()
	{
		return needRefresh; // si on a des plottables, on refresh quand même
	}

	/**
	 * Ajoute à la sauvegarde
	 * 
	 * @param out
	 * @throws IOException
	 */
	synchronized void saveState()
	{
		if(needSave)
			sauvegarde.add(center, printables);
		needSave = false;
		// TODO : plottables aussi !
	}

	/**
	 * Envoie en sérialisant les objets à afficher
	 * 
	 * @param out
	 * @throws IOException
	 */
	synchronized void send(ObjectOutputStream out) throws IOException
	{
		out.reset();
		out.writeObject(printables);
//		System.out.println("Envoi de "+printables.hashCode()+" "+printables.size());
//		for(ColoredPrintable c : printables)
//			System.out.println("  "+c);

		// TODO : plottables aussi !
		out.flush(); // on force l'envoi !
	}

	synchronized void destructor()
	{
		// On ne sauvegarde que s'il y a quelque chose à sauvegarder…
		if(!sauvegarde.isEmpty())
		{
			System.out.println("Sauvegarde de la vidéo en cours… ÇA PEUT PRENDRE DU TEMPS !");
	
			try
			{
				FileOutputStream fichier = null;
				try
				{
					fichier = new FileOutputStream(filename);
				}
				catch(FileNotFoundException e)
				{
					try
					{
						Runtime.getRuntime().exec("mkdir videos");
						try
						{
							Thread.sleep(50);
						}
						catch(InterruptedException e1)
						{
							e1.printStackTrace();
						}
						fichier = new FileOutputStream(filename);
					}
					catch(FileNotFoundException e1)
					{
						System.err.println("Erreur (1) lors de la création du fichier : " + e1);
						return;
					}
				}
	
				ObjectOutputStream file = new ObjectOutputStream(fichier);
				file.writeObject(sauvegarde);
				file.flush();
				file.close();
				Runtime.getRuntime().exec("cp "+filename+" videos/last.dat");
	
				System.out.println("Sauvegarde de la vidéo terminée");
			}
			catch(IOException e)
			{
				System.err.println("Erreur lors de la sauvegarde du buffer graphique ! " + e);
			}
		}
	}
}
