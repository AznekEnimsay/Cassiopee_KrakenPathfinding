/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.graphic.printable;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

import pfg.kraken.display.Display;
import pfg.kraken.display.Printable;
import pfg.kraken.struct.XY;
import pfg.kraken.struct.XY_RW;

/**
 * Un vecteur affichable
 * 
 * @author pf
 *
 */

public class Vector implements Printable, Serializable
{
	private static final long serialVersionUID = 3887897521575363643L;
	private XY a, b;
	private double orientation;
	private AffineTransform tx = new AffineTransform();
	private Polygon arrowHead = new Polygon();  
	
	public Vector(XY pos, double orientation)
	{
		a = pos.clone();
		b = new XY_RW(50, orientation, true).plus(a);
		this.orientation = orientation;
		arrowHead.addPoint(0,5);
		arrowHead.addPoint(-5,-5);
		arrowHead.addPoint(5,-5);
	}
	
	public void update(XY pos, double orientation)
	{
		a = pos.clone();
		b = new XY_RW(50, orientation, true).plus(a);
		this.orientation = orientation;
	}

	@Override
	public void print(Graphics g, Display f)
	{
		g.drawLine(f.XtoWindow(a.getX()), f.YtoWindow(a.getY()), f.XtoWindow(b.getX()), f.YtoWindow(b.getY()));
	    tx.setToIdentity();
	    tx.translate(f.XtoWindow(b.getX()), f.YtoWindow(b.getY()));
	    tx.rotate((-orientation-Math.PI/2d));  

	    Graphics2D g2d = (Graphics2D) g.create();
	    g2d.setTransform(tx);   
	    g2d.fill(arrowHead);
	    g2d.dispose();
	}

	@Override
	public String toString()
	{
		return "Vecteur entre " + a + " et " + b;
	}

}
