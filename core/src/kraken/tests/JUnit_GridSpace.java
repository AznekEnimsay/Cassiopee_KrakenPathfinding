/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package kraken.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import kraken.pathfinding.dstarlite.gridspace.Direction;
import kraken.pathfinding.dstarlite.gridspace.GridSpace;
import kraken.pathfinding.dstarlite.gridspace.PointDirigeManager;
import kraken.pathfinding.dstarlite.gridspace.PointGridSpace;
import kraken.pathfinding.dstarlite.gridspace.PointGridSpaceManager;
import kraken.utils.XY;

/**
 * Tests unitaires de GridSpace
 * 
 * @author pf
 *
 */

public class JUnit_GridSpace extends JUnit_Test
{

	private GridSpace gridspace;
	private PointGridSpaceManager pointManager;
	private PointDirigeManager pointDManager;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		gridspace = injector.getService(GridSpace.class);
		pointManager = injector.getService(PointGridSpaceManager.class);
		pointDManager = injector.getService(PointDirigeManager.class);

	}

	@Test
	public void test_computeVec2() throws Exception
	{
		log.debug(pointManager.get(0).computeVec2());
		log.debug(pointManager.get(64).computeVec2());
		log.debug(pointManager.get(63).computeVec2());
		Assert.assertTrue(pointManager.get(0).computeVec2().equals(new XY(-1500, 0)));
	}

	@Test
	public void test_distance() throws Exception
	{
		Assert.assertEquals(1414, gridspace.distanceStatique(pointDManager.get(pointManager.get(new XY(0, 1000)), Direction.NE)));
		Assert.assertEquals(1000, gridspace.distanceStatique(pointDManager.get(pointManager.get(new XY(0, 1000)), Direction.N)));
		Assert.assertEquals(Integer.MAX_VALUE, gridspace.distanceStatique(pointDManager.get(pointManager.get(1), Direction.E)));
	}

	@Test
	public void test_distanceHeuristique() throws Exception
	{
		Assert.assertEquals(1000, pointManager.get(1).distanceOctile(pointManager.get(2)));
		Assert.assertEquals(1000, pointManager.get(1).distanceOctile(pointManager.get(PointGridSpace.NB_POINTS_POUR_TROIS_METRES + 1)));
		Assert.assertEquals(1414, pointManager.get(1).distanceOctile(pointManager.get(PointGridSpace.NB_POINTS_POUR_TROIS_METRES)));
		Assert.assertEquals(1414, pointManager.get(PointGridSpace.NB_POINTS_POUR_TROIS_METRES).distanceOctile(pointManager.get(1)));
		Assert.assertEquals(2000, pointManager.get(1).distanceOctile(pointManager.get(1 + 2 * PointGridSpace.NB_POINTS_POUR_TROIS_METRES)));
		Assert.assertEquals(2000, pointManager.get(1 + 2 * PointGridSpace.NB_POINTS_POUR_TROIS_METRES).distanceOctile(pointManager.get(1)));
		Assert.assertEquals(1000 * (PointGridSpace.NB_POINTS_POUR_TROIS_METRES - 2) + 1414, pointManager.get(PointGridSpace.NB_POINTS_POUR_TROIS_METRES).distanceOctile(pointManager.get(PointGridSpace.NB_POINTS_POUR_TROIS_METRES - 1)));
		Assert.assertEquals(1000 * (PointGridSpace.NB_POINTS_POUR_TROIS_METRES - 2) + 1414, pointManager.get(PointGridSpace.NB_POINTS_POUR_TROIS_METRES - 1).distanceOctile(pointManager.get(PointGridSpace.NB_POINTS_POUR_TROIS_METRES)));
	}

	@Test
	public void test_getGridPointVoisin() throws Exception
	{
		for(int i = 0; i < 8; i++)
		{
			log.debug(i);
			Assert.assertEquals((i < 4 ? 1414 : 1000), pointManager.getGridPointVoisin(pointManager.get(150), Direction.values()[i]).distanceOctile(pointManager.get(150)));
		}

		Assert.assertEquals(null, pointManager.getGridPointVoisin(pointManager.get(21), Direction.S));
		Assert.assertEquals(null, pointManager.getGridPointVoisin(pointManager.get(63), Direction.SE));
		Assert.assertEquals(null, pointManager.getGridPointVoisin(pointManager.get(127), Direction.E));
		Assert.assertEquals(null, pointManager.getGridPointVoisin(pointManager.get(128), Direction.O));
		Assert.assertEquals(null, pointManager.getGridPointVoisin(pointManager.get(21), Direction.SE));
	}

	@Test
	public void test_computeGridPoint() throws Exception
	{
		for(int i = 0; i < PointGridSpace.NB_POINTS; i++)
			Assert.assertTrue(pointManager.get(pointManager.get(i).computeVec2()).hashCode() == i);
	}
	/*
	 * @Test
	 * public void test_ajout_obstacle() throws Exception
	 * {
	 */
	/**
	 * Ce test ne peut plus être mené car getOldAndNewObstacles est appelé par
	 * un thread, ce qui fausse tout
	 */
	/*
	 * int peremption = config.getInt(ConfigInfo.DUREE_PEREMPTION_OBSTACLES);
	 * Assert.assertEquals(gridspace.getCurrentObstacles().cardinality(), 0);
	 * BitSet[] b = gridspace.getOldAndNewObstacles();
	 * log.debug(b[0].cardinality()+" "+b[1].cardinality());
	 * Assert.assertEquals(b[0].cardinality(), 0);
	 * Assert.assertEquals(b[1].cardinality(), 0);
	 * gridspace.addObstacleAndRemoveNearbyObstacles(new Vec2RO(200, 1100));
	 * b = gridspace.getOldAndNewObstacles();
	 * log.debug(b[0].cardinality()+" "+b[1].cardinality());
	 * Assert.assertEquals(b[0].cardinality(), 0);
	 * Assert.assertTrue(b[1].cardinality() != 0);
	 * gridspace.addObstacleAndRemoveNearbyObstacles(new Vec2RO(200, 1100));
	 * b = gridspace.getOldAndNewObstacles();
	 * log.debug(b[0].cardinality()+" "+b[1].cardinality());
	 * Assert.assertEquals(b[0].cardinality(), 0); // finalement, rien n'a
	 * changé
	 * Assert.assertEquals(b[1].cardinality(), 0);
	 * gridspace.addObstacleAndRemoveNearbyObstacles(new Vec2RO(230, 1100));
	 * b = gridspace.getOldAndNewObstacles();
	 * log.debug(b[0].cardinality()+" "+b[1].cardinality());
	 * Assert.assertEquals(b[0].cardinality(), 0); // on a supprimé l'autre qui
	 * était trop proche
	 * Assert.assertEquals(b[1].cardinality(), 0);
	 * gridspace.addObstacleAndRemoveNearbyObstacles(new Vec2RO(400, 600));
	 * b = gridspace.getOldAndNewObstacles();
	 * log.debug(b[0].cardinality()+" "+b[1].cardinality());
	 * Assert.assertEquals(b[0].cardinality(), 0);
	 * Assert.assertTrue(b[1].cardinality() != 0);
	 * b = gridspace.getOldAndNewObstacles();
	 * log.debug(b[0].cardinality()+" "+b[1].cardinality());
	 * Assert.assertEquals(b[0].cardinality(), 0);
	 * Assert.assertEquals(b[1].cardinality(), 0);
	 * Thread.sleep(peremption+10);
	 * b = gridspace.getOldAndNewObstacles();
	 * log.debug(b[0].cardinality()+" "+b[1].cardinality());
	 * Assert.assertTrue(b[0].cardinality() != 0);
	 * Assert.assertEquals(b[1].cardinality(), 0);
	 * gridspace.addObstacleAndRemoveNearbyObstacles(new Vec2RO(400, 800));
	 * Assert.assertTrue(gridspace.getCurrentObstacles().cardinality() != 0);
	 * Assert.assertTrue(gridspace.getCurrentObstacles().cardinality() != 0);
	 * b = gridspace.getOldAndNewObstacles();
	 * log.debug(b[0].cardinality()+" "+b[1].cardinality());
	 * Assert.assertEquals(b[0].cardinality(), 0);
	 * Assert.assertEquals(b[1].cardinality(), 0);
	 * }
	 */

	/*
	 * @Test
	 * public void test_masque() throws Exception
	 * {
	 * MasqueManager mm = injector.getService(MasqueManager.class);
	 * PrintBuffer buffer = injector.getService(PrintBuffer.class);
	 * Masque m = mm.getMasqueEnnemi(new Vec2RO(0, 1000));
	 * if(config.getBoolean(ConfigInfo.GRAPHIC_D_STAR_LITE))
	 * buffer.add(m);
	 * }
	 */

}
