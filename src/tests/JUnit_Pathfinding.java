package tests;

import hook.Hook;
import hook.HookFactory;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import permissions.ReadOnly;
import permissions.ReadWrite;
import planification.MemoryManager;
import planification.astar.AStar;
import planification.astar.arc.PathfindingNodes;
import planification.astar.arc.SegmentTrajectoireCourbe;
import planification.astar.arcmanager.PathfindingArcManager;
import container.ServiceNames;
import robot.RobotChrono;
import robot.RobotReal;
import strategie.GameState;
import table.GameElementNames;
import utils.ConfigInfo;
import utils.Vec2;
import enums.Tribool;
import exceptions.PathfindingException;
import exceptions.PathfindingRobotInObstacleException;

/**
 * Tests unitaires de la recherche de chemin.
 * @author pf
 *
 */

public class JUnit_Pathfinding extends JUnit_Test {

	private AStar<PathfindingArcManager, SegmentTrajectoireCourbe> pathfinding;
	private GameState<RobotChrono,ReadWrite> state_chrono;
	private GameState<RobotReal,ReadWrite> state;
	private MemoryManager memorymanager;
	private HookFactory hookfactory;
	
	@SuppressWarnings("unchecked")
	@Before
    public void setUp() throws Exception {
        super.setUp();
    	config.set(ConfigInfo.DUREE_PEREMPTION_OBSTACLES, 100);
        pathfinding = (AStar<PathfindingArcManager, SegmentTrajectoireCourbe>) container.getService(ServiceNames.A_STAR_PATHFINDING);
		state = (GameState<RobotReal,ReadWrite>)container.getService(ServiceNames.REAL_GAME_STATE);
		state_chrono = GameState.cloneGameState(state.getReadOnly());
		memorymanager = (MemoryManager) container.getService(ServiceNames.MEMORY_MANAGER);
		hookfactory = (HookFactory) container.getService(ServiceNames.HOOK_FACTORY);
	}

	@Test(expected=PathfindingRobotInObstacleException.class)
    public void test_robot_dans_obstacle() throws Exception
    {
		GameState.setPosition(state_chrono, new Vec2<ReadOnly>(80, 80));
		// TODO: utiliser obstacle ennemi
//		GameState.creer_obstacle(state_chrono.getTestOnly(),new Vec2<ReadOnly>(80, 80));
    	pathfinding.computePath(state_chrono, PathfindingNodes.values()[0], false);
    }

	@Test(expected=PathfindingException.class)
    public void test_obstacle() throws Exception
    {
		GameState.setPosition(state_chrono, new Vec2<ReadOnly>(80, 80));
		// TODO: utiliser obstacle ennemi
//		GameState.creer_obstacle(state_chrono, PathfindingNodes.values()[0].getCoordonnees());
    	pathfinding.computePath(state_chrono, PathfindingNodes.values()[0], false);
    }

	@Test
    public void test_brute_force() throws Exception
    {
    	for(PathfindingNodes i: PathfindingNodes.values())
        	for(PathfindingNodes j: PathfindingNodes.values())
        	{
        		GameState.reinitDate(state_chrono.getTestOnly());
        		GameState.setPosition(state_chrono, i.getCoordonnees());
    			pathfinding.computePath(state_chrono, j, true);
        	}
    }
	
	@Test
    public void test_element_jeu_disparu() throws Exception
    {
    	// une fois ces éléments pris, le chemin est libre
		GameState.setDone(state_chrono, GameElementNames.PLOT_1, Tribool.TRUE);
		GameState.setDone(state_chrono, GameElementNames.PLOT_2, Tribool.TRUE);
		GameState.setDone(state_chrono, GameElementNames.VERRE_2, Tribool.TRUE);
		GameState.setPositionPathfinding(state_chrono, PathfindingNodes.CLAP_DROIT_SECOND);
    	pathfinding.computePath(state_chrono, PathfindingNodes.CLAP_DROIT, false);
    }

	@Test(expected=PathfindingException.class)
    public void test_element_jeu_disparu_2() throws Exception
    {
		// Exception car il y a un verre sur le passage
		GameState.setPositionPathfinding(state_chrono, PathfindingNodes.CLAP_DROIT_SECOND);
    	pathfinding.computePath(state_chrono, PathfindingNodes.CLAP_DROIT, false);
    }
	
	@Test
    public void test_element_jeu_disparu_3() throws Exception
    {
		// Pas d'exception car on demande au pathfinding de passer sur les éléments de jeux.
		GameState.setPositionPathfinding(state_chrono, PathfindingNodes.CLAP_DROIT_SECOND);
    	pathfinding.computePath(state_chrono, PathfindingNodes.CLAP_DROIT, true);
    }

	@Test
    public void test_peremption_pendant_trajet() throws Exception
    {
		GameState.setPosition(state_chrono, new Vec2<ReadOnly>(80, 80));
		// TODO: utiliser obstacle ennemi
//		GameState.creer_obstacle(state_chrono, PathfindingNodes.values()[0].getCoordonnees());
    	pathfinding.computePath(state_chrono, PathfindingNodes.values()[0], true);
    }

	@Test
	public void test_pathfinding2() throws Exception
	{
		PathfindingNodes i = PathfindingNodes.SORTIE_ZONE_DEPART;
		PathfindingNodes j = PathfindingNodes.CLAP_GAUCHE;
		GameState.setPositionPathfinding(state_chrono, i);
		ArrayList<SegmentTrajectoireCourbe> chemin = pathfinding.computePath(state_chrono, j, true);
		for(SegmentTrajectoireCourbe n: chemin)
			log.debug(n);
	}
	
	@Test
    public void test_pathfinding() throws Exception
    {
		Random randomgenerator = new Random();
		PathfindingNodes i = PathfindingNodes.values()[randomgenerator.nextInt(PathfindingNodes.values().length)];
		PathfindingNodes j = PathfindingNodes.values()[randomgenerator.nextInt(PathfindingNodes.values().length)];
		GameState.setPositionPathfinding(state_chrono, i);
		ArrayList<SegmentTrajectoireCourbe> chemin = pathfinding.computePath(state_chrono, j, true);
		for(SegmentTrajectoireCourbe n: chemin)
			log.debug(n);
    }

	@Test
    public void test_parcours() throws Exception
    {
		PathfindingNodes i = PathfindingNodes.COTE_MARCHE_DROITE;
		PathfindingNodes j = PathfindingNodes.COTE_MARCHE_GAUCHE;
		GameState.setPositionPathfinding(state_chrono, i);
		GameState.setPosition(state_chrono, i.getCoordonnees());
		ArrayList<SegmentTrajectoireCourbe> chemin = pathfinding.computePath(state_chrono, j, true);
		for(SegmentTrajectoireCourbe n: chemin)
			log.debug(n.objectifFinal+" courbe? "+(n.differenceDistance!=0));
		GameState.suit_chemin(state_chrono, chemin, new ArrayList<Hook>());
    }

	
	@Test
    public void test_memorymanager_vide() throws Exception
    {
		Random randomgenerator = new Random();
		for(int k = 0; k < 100; k++)
		{
//			state_chrono.robot.reinitDate();
			PathfindingNodes i = PathfindingNodes.values()[randomgenerator.nextInt(PathfindingNodes.values().length)];
			PathfindingNodes j = PathfindingNodes.values()[randomgenerator.nextInt(PathfindingNodes.values().length)];
			GameState.setPositionPathfinding(state_chrono, i);
			long old_hash = GameState.getHash(state_chrono.getReadOnly());
			pathfinding.computePath(state_chrono, j, true);
			Assert.assertEquals(old_hash, GameState.getHash(state_chrono.getReadOnly()));
			Assert.assertTrue(memorymanager.isMemoryManagerEmpty(0));
		}
    }

	@Test
	public void test_hook_chrono_suit_chemin() throws Exception
	{
		state_chrono = GameState.cloneGameState(state.getReadOnly());
		ArrayList<Hook> hooks_table = hookfactory.getHooksEntreScriptsChrono(state_chrono, 90000);
		GameState.setPosition(state_chrono, PathfindingNodes.BAS.getCoordonnees().plusNewVector(new Vec2<ReadWrite>(10, 10)).getReadOnly());
    	ArrayList<SegmentTrajectoireCourbe> chemin = pathfinding.computePath(state_chrono, PathfindingNodes.COTE_MARCHE_DROITE, true);

		ArrayList<Vec2<ReadOnly>> cheminVec2 = new ArrayList<Vec2<ReadOnly>>();
		cheminVec2.add(PathfindingNodes.BAS.getCoordonnees().plusNewVector(new Vec2<ReadWrite>(10, 10)).getReadOnly());
		for(SegmentTrajectoireCourbe n: chemin)
		{
			log.debug(n);
			cheminVec2.utiliseActionneurs(n.objectifFinal.getCoordonnees());
		}
    	
		Assert.assertEquals(PathfindingNodes.BAS.getCoordonnees().plusNewVector(new Vec2<ReadWrite>(10, 10)), GameState.getPosition(state_chrono.getReadOnly()));
		Assert.assertTrue(GameState.isDone(state_chrono.getReadOnly(), GameElementNames.PLOT_6) == Tribool.FALSE);
		GameState.suit_chemin(state_chrono, chemin, hooks_table);
		Assert.assertTrue(GameState.isDone(state_chrono.getReadOnly(), GameElementNames.PLOT_6) == Tribool.TRUE);
		Assert.assertEquals(PathfindingNodes.COTE_MARCHE_DROITE.getCoordonnees(), GameState.getPosition(state_chrono.getReadOnly()));
		
    	// on vérifie qu'à présent qu'on a emprunté ce chemin, il n'y a plus d'élément de jeu dessus et donc qu'on peut demander un pathfinding sans exception
		GameState.setPosition(state_chrono, PathfindingNodes.BAS.getCoordonnees());
    	pathfinding.computePath(state_chrono, PathfindingNodes.COTE_MARCHE_DROITE, false);
	}


}
