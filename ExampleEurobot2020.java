/*
 * Copyright (C) 2013-2018 Pierre-François Gimenez
 * Distributed under the MIT License.
 */
package pfg.kraken_examples;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import pfg.graphic.DebugTool;
import pfg.graphic.printable.Layer;
import pfg.kraken.Kraken;
import pfg.kraken.KrakenParameters;
import pfg.kraken.SearchParameters;
import pfg.kraken.display.Display;
import pfg.kraken.exceptions.PathfindingException;
import pfg.kraken.obstacles.CircularObstacle;
import pfg.kraken.obstacles.Obstacle;
import pfg.kraken.obstacles.RectangularObstacle;
import pfg.kraken.struct.ItineraryPoint;
import pfg.kraken.struct.XY;
import pfg.kraken.struct.XYO;


/**
 * An example with the obstacles from the Eurobot 2020 and 2021 robotic competition
 * @author pf AznekEnimsay
 *
 */

public class ExampleEurobot2020
{

	public static void main(String[] args)
	{
		/*
		 * The obstacles of Eurobot 2020/2021
		 */
		List<Obstacle> obs = new ArrayList<Obstacle>(); //offciellement origine en haut à gauche. dans le code à la place de la girouette (en bas au milieu en X)

		/*Les bords de la table */
		obs.add(new RectangularObstacle(new XY(0, 0), 3000, 5));
		obs.add(new RectangularObstacle(new XY(-1500, 1000), 5, 2000));
		obs.add(new RectangularObstacle(new XY(1500, 1000), 5, 2000));
		obs.add(new RectangularObstacle(new XY(0, 2000), 3000, 5));

		obs.add(new RectangularObstacle(new XY(-611, 2000-80), 22, 150));// 889 1850 et 22m d'epaisseur
		obs.add(new RectangularObstacle(new XY(589, 2000-80), 22, 150)); // 2089 1850
		obs.add(new RectangularObstacle(new XY(-11, 2000-150), 22, 300)); // 1489 1700


		obs.add(new CircularObstacle(new XY(-110, 1950), 36));
	//	obs.add(new CircularObstacle(new XY(110, 1950), 36));

		obs.add(new CircularObstacle(new XY(-170, 1650), 36));
	//	obs.add(new CircularObstacle(new XY(170, 1650), 36));

		obs.add(new CircularObstacle(new XY(-230, 1200), 36));
	//	obs.add(new CircularObstacle(new XY(230, 1200), 36));

		obs.add(new CircularObstacle(new XY(-405, 800), 36));
	//	obs.add(new CircularObstacle(new XY(405, 800), 36));

		obs.add(new CircularObstacle(new XY(-430, 1650), 36));
	//	obs.add(new CircularObstacle(new XY(430, 1650), 36));

		obs.add(new CircularObstacle(new XY(-500, 1950), 36));
	//	obs.add(new CircularObstacle(new XY(500, 1950), 36));

		obs.add(new CircularObstacle(new XY(-555, 400), 36));
	//	obs.add(new CircularObstacle(new XY(555, 400), 36));

		obs.add(new CircularObstacle(new XY(-830, 100), 36));
	//	obs.add(new CircularObstacle(new XY(830, 100), 36));

		obs.add(new CircularObstacle(new XY(-1055, 515), 36));
	//	obs.add(new CircularObstacle(new XY(1055, 515), 36));

		obs.add(new CircularObstacle(new XY(-1055, 1085), 36));
	//	obs.add(new CircularObstacle(new XY(1055, 1085), 36));

		obs.add(new CircularObstacle(new XY(-1200, 400), 36));
	//	obs.add(new CircularObstacle(new XY(1200, 400), 36));

		obs.add(new CircularObstacle(new XY(-1200, 1200), 36));
	//	obs.add(new CircularObstacle(new XY(1200, 1200), 36));


		RectangularObstacle robot = new RectangularObstacle(106, 278, 105, 105);

		DebugTool debug = DebugTool.getDebugTool(new XY(0,1000), new XY(0, 1000), null, "kraken-examples.conf", "trajectory", "eurobot2020");
		Display display = debug.getDisplay();
		for(Obstacle o : obs)
			display.addPrintable(o, Color.BLACK, Layer.MIDDLE.layer);
		
		KrakenParameters kp = new KrakenParameters(robot, new XY(-1500,0), new XY(1500, 2000), "kraken-examples.conf", "eurobot2020", "trajectory"/*, "detailed"*/);
		kp.setFixedObstacles(obs);
		kp.setDisplay(display);
		Kraken kraken = new Kraken(kp);
		
		try
		{
			kraken.initializeNewSearch(new SearchParameters(new XYO(1210, 1400, Math.PI), new XYO(-125, 1690, Math.PI/2)));
			List<ItineraryPoint> path = kraken.search();
			
			for(ItineraryPoint p : path)
			{
				display.addPrintable(p, Color.BLACK, Layer.FOREGROUND.layer);
				System.out.println(p);
			}
			
			System.out.println(kraken.getTentaclesStatistics());

			display.refresh();
		}
		catch(PathfindingException e)
		{
			// Impossible
			e.printStackTrace();
		}
	}
}
