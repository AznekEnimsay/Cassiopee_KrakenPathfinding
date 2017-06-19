/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.kraken.dstarlite;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import pfg.graphic.Fenetre;
import pfg.graphic.printable.Layer;
import pfg.graphic.printable.Printable;
import pfg.kraken.utils.XY;

/**
 * Un point du navmesh
 * 
 * @author pf
 *
 */

public class NavmeshNode implements Printable, Serializable
{
	private static final long serialVersionUID = -6588410126587155794L;

	/**
	 * Attention ! Le repère de ce x,y est celui pour lequel x et y sont
	 * toujours positifs
	 */
	final int nb;
	final XY position;
	List<NavmeshEdge> edges = new ArrayList<NavmeshEdge>();
	List<NavmeshNode> neighbours = new ArrayList<NavmeshNode>();
	private static int nbStatic = 0;

	/**
	 * Construit à partir du hashCode
	 * 
	 * @param i
	 */
	NavmeshNode(XY position)
	{
		this.position = position;
		this.nb = nbStatic++;
	}

	@Override
	public int hashCode()
	{
		return nb;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof NavmeshNode && nb == o.hashCode();
	}
	
	@Override
	public void print(Graphics g, Fenetre f)
	{
//		g.fillOval(f.XtoWindow(p.getX()) - f.distanceXtoWindow((int) DISTANCE_ENTRE_DEUX_POINTS) / 2, f.YtoWindow(p.getY()) - f.distanceYtoWindow((int) DISTANCE_ENTRE_DEUX_POINTS) / 2, f.distanceXtoWindow((int) (DISTANCE_ENTRE_DEUX_POINTS * 0.7)), f.distanceYtoWindow((int) (DISTANCE_ENTRE_DEUX_POINTS * 0.7)));
	}

	@Override
	public String toString()
	{
		return "NavmeshNode at "+position;
	}

	@Override
	public int getLayer()
	{
		return Layer.FOREGROUND.ordinal();
	}

	public void updateNeighbours()
	{
		for(NavmeshEdge e : edges)
		{
			if(e.points[0] == this)
				neighbours.add(e.points[1]);
			else
				neighbours.add(e.points[0]);
		}	
	}
	
	public NavmeshNode[] getNeighbourhood()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
