/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.graphic.printable;

import java.awt.Graphics;
import java.io.Serializable;

import pfg.kraken.display.Display;
import pfg.kraken.display.Printable;
import pfg.kraken.struct.XY;

/**
 * Un segment affichable
 * 
 * @author pf
 *
 */

public class Segment implements Printable, Serializable
{
	private static final long serialVersionUID = 3887897521575363643L;
	public XY a, b;

	public Segment(XY a, XY b)
	{
		this.a = a.clone();
		this.b = b.clone();
	}

	@Override
	public void print(Graphics g, Display f)
	{
		g.drawLine(f.XtoWindow(a.getX()), f.YtoWindow(a.getY()), f.XtoWindow(b.getX()), f.YtoWindow(b.getY()));
	}
	@Override
	public String toString()
	{
		return "Segment entre " + a + " et " + b;
	}

}
