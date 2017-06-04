/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package kraken.obstacles.types;

import java.io.Serializable;
import kraken.graphic.Couleur;
import kraken.utils.Vec2RO;
import kraken.utils.Vec2RW;

/**
 * Obstacle rectangulaire de notre robot
 * 
 * @author pf
 *
 */

public class ObstacleRobot extends ObstacleRectangular implements Serializable
{
	private static final long serialVersionUID = -8994485842050904808L;

	public ObstacleRobot(int demieLargeurNonDeploye, int demieLongueurArriere, int demieLongueurAvant)
	{
		super(new Vec2RW());
		c = Couleur.ROBOT.couleur;
		l = Couleur.ROBOT.l;
		centreGeometrique = new Vec2RW();
		coinBasGaucheRotate = new Vec2RW();
		coinHautGaucheRotate = new Vec2RW();
		coinBasDroiteRotate = new Vec2RW();
		coinHautDroiteRotate = new Vec2RW();

		int a = demieLargeurNonDeploye;
		int b = demieLargeurNonDeploye;
		int c = demieLongueurAvant;
		int d = demieLongueurArriere;

		coinBasGauche = new Vec2RW(-d, -a);
		coinHautGauche = new Vec2RW(-d, b);
		coinBasDroite = new Vec2RW(c, -a);
		coinHautDroite = new Vec2RW(c, b);

		demieDiagonale = Math.sqrt((a + b) * (a + b) / 4 + (c + d) * (c + d) / 4);
		centreGeometrique = new Vec2RW();
	}

	/**
	 * Mise à jour de l'obstacle
	 * 
	 * @param position
	 * @param orientation
	 * @param robot
	 * @return
	 */
	public ObstacleRectangular update(Vec2RO position, double orientation)
	{
		position.copy(this.position);
		this.angle = orientation;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		this.angle = orientation;
		convertitVersRepereTable(coinBasGauche, coinBasGaucheRotate);
		convertitVersRepereTable(coinHautGauche, coinHautGaucheRotate);
		convertitVersRepereTable(coinBasDroite, coinBasDroiteRotate);
		convertitVersRepereTable(coinHautDroite, coinHautDroiteRotate);
		coinBasDroiteRotate.copy(centreGeometrique);
		centreGeometrique = centreGeometrique.plus(coinHautGaucheRotate).scalar(0.5);

		if(printAllObstacles)
			synchronized(buffer)
			{
				buffer.notify();
			}

		return this;
	}

	/**
	 * Met à jour cet obstacle
	 * 
	 * @param obstacle
	 */
	public void copy(ObstacleRobot obstacle)
	{
		obstacle.update(position, angle);
	}
}
