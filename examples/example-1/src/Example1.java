/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pfg.graphic.WindowFrame;
import pfg.kraken.ColorKraken;
import pfg.kraken.Kraken;
import pfg.kraken.exceptions.PathfindingException;
import pfg.kraken.obstacles.Obstacle;
import pfg.kraken.obstacles.RectangularObstacle;
import pfg.kraken.astar.TentacularAStar;
import pfg.kraken.robot.ItineraryPoint;
import pfg.kraken.utils.XY;
import pfg.kraken.utils.XYO;
import pfg.kraken.utils.XY_RW;


/**
 * Minimalist example code
 * @author pf
 *
 */

public class Example1
{

	public static void main(String[] args)
	{
		/*
		 * The list of fixed, permanent obstacles
		 */
		List<Obstacle> obs = new ArrayList<Obstacle>();
		obs.add(new RectangularObstacle(new XY_RW(400,200), 200, 200, ColorKraken.BLACK.color, ColorKraken.BLACK.layer));
		
		/*
		 * Getting Kraken (a singleton).
		 */
		Kraken kraken = Kraken.getKraken(obs);
		
		/*
		 * The graphic display (optional)
		 */
		WindowFrame frame = kraken.getWindowFrame();
		
		/*
		 * The pathfinder itself.
		 */
		TentacularAStar astar = kraken.getAStar();
		try
		{
			/*
			 * The pathfinding is split in two steps :
			 * - the initialization
			 * - the actual path searching
			 */
			
			/*
			 * We search a new path from the point (0,0) with orientation 0 to the point (1000, 1000).
			 */
			astar.initializeNewSearch(new XYO(0, 0, 0), new XY(1000, 1000));
			
			/*
			 * The pathfinder returns a list of ItineraryPoint, which contains all the cinematic information that described follow the path
			 */
			LinkedList<ItineraryPoint> path = astar.search();
			
			/*
			 * For this example, we just print the trajectory points
			 */
			for(ItineraryPoint p : path)
				System.out.println(p);
			
			/*
			 * Refresh the window frame.
			 */
			frame.refresh();
		}
		catch(PathfindingException e)
		{
			/*
			 * This exception is thrown when no path is found
			 */
			e.printStackTrace();
		}
		finally 
		{
			/*
			 * You are expected to destroy Kraken properly
			 */
			kraken.destructor();
		}
	}
}
