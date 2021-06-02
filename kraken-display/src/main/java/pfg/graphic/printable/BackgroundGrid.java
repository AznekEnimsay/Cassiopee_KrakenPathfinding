/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.graphic.printable;

import java.awt.Graphics;

import pfg.kraken.display.Display;
import pfg.kraken.display.Printable;
import pfg.kraken.struct.XY;

/**
 * A background grid
 * @author pf
 *
 */

public class BackgroundGrid implements Printable
{
	private static final long serialVersionUID = 1422929627673510227L;
	
	@Override
	public void print(Graphics g, Display f)
	{
		XY basGauche = f.getCurrentBottomLeftCorner();
		XY hautDroite = f.getCurrentTopRightCorner();
		
		double pasX = Math.pow(10, Math.floor(Math.log10(hautDroite.getX() - basGauche.getX()))-1);
		double startX = Math.ceil(basGauche.getX() / pasX) * pasX;
		int i = 0;
		while(startX + i*pasX <= hautDroite.getX())
		{
			g.drawLine(f.XtoWindow(startX + i*pasX), f.YtoWindow(basGauche.getY()), f.XtoWindow(startX + i*pasX), f.YtoWindow(hautDroite.getY()));
			i++;
		}

		double pasY = Math.pow(10, Math.floor(Math.log10(hautDroite.getY() - basGauche.getY()))-1);
		double startY = Math.ceil(basGauche.getY() / pasY) * pasY;
		i = 0;
		while(startY + i*pasY <= hautDroite.getY())
		{
			g.drawLine(f.XtoWindow(basGauche.getX()), f.YtoWindow(startY + i*pasY), f.XtoWindow(hautDroite.getX()), f.YtoWindow(startY + i*pasY));
			i++;
		}
	}
}
