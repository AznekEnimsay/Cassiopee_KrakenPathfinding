/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.graphic;

import javax.imageio.ImageIO;
import javax.swing.*;
import pfg.config.Config;
import pfg.graphic.printable.BackgroundGrid;
import pfg.graphic.printable.BackgroundImage;
import pfg.graphic.printable.Layer;
import pfg.kraken.display.ColoredPrintable;
import pfg.kraken.display.Display;
import pfg.kraken.display.Printable;
import pfg.kraken.struct.XY;
import pfg.kraken.struct.XY_RW;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.PriorityQueue;

/**
 * Interface graphique
 * 
 * @author pf
 *
 */

public class GraphicPanel extends JPanel implements Display
{

	/**
	 * Couleurs surtout utilisées pour le dstarlite
	 * 
	 * @author pf
	 *
	 */

	private static final long serialVersionUID = 1L;
	private GraphicDisplay buffer;

	private int sizeX, sizeY;
	private double zoom;
	private XY defaultCenter;
	private XY center;
	private XY deltaBasGauche, deltaHautDroite;
	private String backgroundPath;
	private XY_RW coinBasGaucheEcran = new XY_RW();
	private XY_RW coinHautDroiteEcran = new XY_RW();
	private double sizeXUnitaryZoom, sizeYUnitaryZoom;

	public GraphicPanel(XY defaultCenter, XY center, Config config, GraphicDisplay buffer)
	{
		this.defaultCenter = defaultCenter;
		this.center = center;
		this.buffer = buffer;
		backgroundPath = config.getString(ConfigInfoGraphic.BACKGROUND_PATH);
		boolean afficheFond = !backgroundPath.isEmpty();
		boolean afficheGrid = config.getBoolean(ConfigInfoGraphic.DISPLAY_GRID);

		sizeXUnitaryZoom = config.getInt(ConfigInfoGraphic.SIZE_X_WITH_UNITARY_ZOOM);
		sizeYUnitaryZoom = config.getInt(ConfigInfoGraphic.SIZE_Y_WITH_UNITARY_ZOOM);
		
		if(afficheFond)
		{
			try
			{
				Image image;
				InputStream is = getClass().getResourceAsStream(backgroundPath);
				if(is != null)
					image = ImageIO.read(is);
				else
					image = ImageIO.read(new File(backgroundPath));
				sizeX = image.getWidth(this); // on ajuste la taille de la fenêtre à l'image
				sizeY = image.getHeight(this);
				buffer.addPrintable(new BackgroundImage(image), null, Layer.IMAGE_BACKGROUND.layer);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			sizeX = config.getInt(ConfigInfoGraphic.SIZE_X_WINDOW);
			sizeY = config.getInt(ConfigInfoGraphic.SIZE_Y_WINDOW);
		}

		if(afficheGrid)
			buffer.addPrintable(new BackgroundGrid(), new Color(200,200,200), Layer.IMAGE_BACKGROUND.layer);
		setPreferredSize(new Dimension(sizeX, sizeY));
		
		setZoom(config.getDouble(ConfigInfoGraphic.DEFAULT_ZOOM));
	}
	
	/**
	 * zoom de la fenêtre. Si 0, aucun zoom. Sinon, zoom + focus sur le robot
	 * @param zoom
	 */
	public void setZoom(double zoom)
	{
		this.zoom = zoom;
		if(zoom == 0)
		{
			coinBasGaucheEcran = new XY_RW(-sizeXUnitaryZoom / 2 + defaultCenter.getX(), -sizeYUnitaryZoom / 2 + defaultCenter.getY());
			coinHautDroiteEcran = new XY_RW(sizeXUnitaryZoom / 2 + defaultCenter.getX(), sizeYUnitaryZoom / 2 + defaultCenter.getY());			
		}
		else
		{
			double deltaX, deltaY;
			deltaX = sizeXUnitaryZoom / (2*zoom);
			deltaY = sizeYUnitaryZoom / (2*zoom);
			deltaBasGauche = new XY(-deltaX, -deltaY);
			deltaHautDroite = new XY(deltaX, deltaY);
		}
			
	}
	
	public GraphicDisplay getPrintBuffer()
	{
		return buffer;
	}

	public XY getCurrentTopRightCorner()
	{
		return coinHautDroiteEcran;
	}
	
	public XY getCurrentBottomLeftCorner()
	{
		return coinBasGaucheEcran;
	}
	
	public int distanceXtoWindow(int dist)
	{
		return (int) (dist * sizeX / (coinHautDroiteEcran.getX() - coinBasGaucheEcran.getX()));
	}

	public int distanceYtoWindow(int dist)
	{
		return (int) (dist * sizeY / (coinHautDroiteEcran.getY() - coinBasGaucheEcran.getY()));
	}

	public int XtoWindow(double x)
	{
		return (int) ((x - coinBasGaucheEcran.getX()) * sizeX / (coinHautDroiteEcran.getX() - coinBasGaucheEcran.getX()));
	}

	public int YtoWindow(double y)
	{
		return (int) ((coinHautDroiteEcran.getY() - y) * sizeY / (coinHautDroiteEcran.getY() - coinBasGaucheEcran.getY()));
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		g.clearRect(0, 0, sizeX, sizeY);
		if(zoom != 0 && center != null)
		{
			XY positionRobot = new XY(center.getX(), center.getY());
			XY currentCenter = positionRobot;
//			Vec2RO currentCenter = new Vec2RO((int)(positionRobot.getX() / petitDeltaX) * petitDeltaX, (int)(positionRobot.getY() / petitDeltaY) * petitDeltaY);

			currentCenter.copy(coinBasGaucheEcran);
			coinBasGaucheEcran.plus(deltaBasGauche);
			currentCenter.copy(coinHautDroiteEcran);
			coinHautDroiteEcran.plus(deltaHautDroite);
		}
		buffer.print(g, this);
	}

	@Override
	public void refresh()
	{
		buffer.refresh();
	}

	@Override
	public void updatePrintable(PriorityQueue<ColoredPrintable> l)
	{
		buffer.updatePrintable(l);
	}

	@Override
	public void clearTemporaryPrintables()
	{
		buffer.clearTemporaryPrintables();
	}

	@Override
	public void addTemporaryPrintable(Printable o, Color c, int layer)
	{
		buffer.addTemporaryPrintable(o, c, layer);
	}

	@Override
	public void addPrintable(Printable o, Color c, int layer)
	{
		buffer.addPrintable(o, c, layer);
	}

	@Override
	public boolean removePrintable(Printable o)
	{
		return buffer.removePrintable(o);
	}
}
