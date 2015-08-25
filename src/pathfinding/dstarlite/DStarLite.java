package pathfinding.dstarlite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

import permissions.ReadOnly;
import utils.Config;
import utils.Log;
import utils.Vec2;
import container.Service;

/**
 * Recherche de chemin avec replanification rapide.
 * @author pf
 *
 */

public class DStarLite implements Service
{
	protected Log log;
	private GridSpace gridspace;

	public DStarLite(Log log, GridSpace gridspace)
	{
		this.log = log;
		this.gridspace = gridspace;
		for(int i = 0; i < GridSpace.NB_POINTS; i++)
		{
			memory[i] = new DStarLiteNode(i);
		}
	}
	
	private DStarLiteNode[] memory = new DStarLiteNode[GridSpace.NB_POINTS];

	private PriorityQueue<DStarLiteNode> openset = new PriorityQueue<DStarLiteNode>(GridSpace.NB_POINTS, new DStarLiteNodeComparator());
	private int km;
	private DStarLiteNode arrivee;
	private DStarLiteNode depart;
	private int last;
	private long nbPF = 0;
	
	private Cle knew = new Cle();
	private Cle inutile = new Cle();

	private Cle calcKey(DStarLiteNode s, Cle copy)
	{
		copy.set(add(Math.min(s.g,s.rhs), distanceHeuristique(s.gridpoint), km),
				Math.min(s.g, s.rhs));
		return copy;
	}

	private DStarLiteNode getFromMemory(int gridpoint)
	{
		DStarLiteNode out = memory[gridpoint];
		
		/**
		 * Si ce point n'a pas encore été utilisé pour ce pathfinding, on l'initialise
		 */
		if(out.nbPF != nbPF)
		{
			out.g = Integer.MAX_VALUE;
			out.rhs = Integer.MAX_VALUE;
			out.nbPF = nbPF;
		}
		return out;
	}
	
	/**
	 * Ajout dans une liste triée
	 * @param u
	 */
/*	private void addToOpenset(DStarLiteNode u)
	{
//		if(openset.contains(u))
//			log.critical("Déjà dans openset !");
		Iterator<DStarLiteNode> iterator = openset.listIterator();
		int i = 0;
		while(iterator.hasNext())
		{
			if(u.cle.isLesserThan(iterator.next().cle))
			{
				openset.add(i, u);
				return;
			}
			i++;
		}
		openset.add(u);
	}*/
	
	private void updateVertex(DStarLiteNode u)
	{
		boolean contains = openset.contains(u);
		if(u.g != u.rhs)
		{
			calcKey(u, u.cle);
			if(contains)
				openset.poll();
			openset.add(u);
		}
		else if(contains)
			openset.poll();
	}
	
	private void computeShortestPath()
	{
		DStarLiteNode u;
		// TODO : continuer à étendre des noeuds même après la fin de l'algo
		while(!openset.isEmpty() && ((u = openset.peek()).cle.isLesserThan(calcKey(depart, inutile)) || depart.rhs > depart.g))
		{
			Cle kold = u.cle.clone();
			calcKey(u, knew);
			if(kold.isLesserThan(knew))
			{
//				log.debug("Cas 1");
				knew.copy(u.cle);
				openset.poll();
				openset.add(u);
			}
			else if(u.g > u.rhs)
			{
//				log.debug("Cas 2");
				u.g = u.rhs;
				openset.poll();
				for(int i = 0; i < 8; i++)
				{
					int voisin = gridspace.getGridPointVoisin(u.gridpoint, i);
					if(voisin < 0)
						continue;
					DStarLiteNode s = getFromMemory(voisin);
					s.rhs = Math.min(s.rhs, add(gridspace.distanceDStarLite(u.gridpoint, i), u.g));
					updateVertex(s);
				}
			}
			else
			{
//				log.debug("Cas 3");
				int gold = u.g;
				u.g = Integer.MAX_VALUE;
				for(int i = 0; i < 8; i++)
				{
					int voisin = gridspace.getGridPointVoisin(u.gridpoint, i);
					if(voisin < 0)
						continue;
					DStarLiteNode s = getFromMemory(voisin);
					if(s == null)
						continue;
					if(s.rhs == add(gridspace.distanceDStarLite(u.gridpoint, i), gold) && s.gridpoint != arrivee.gridpoint)
					{
						s.rhs = Integer.MAX_VALUE;
						for(int j = 0; j < 8; j++)
						{
							voisin = gridspace.getGridPointVoisin(s.gridpoint, j);
							if(voisin < 0)
								continue;
							DStarLiteNode s2 = getFromMemory(voisin);
							s.rhs = Math.min(s.rhs, add(gridspace.distanceDStarLite(s.gridpoint, j), s2.g));
						}
					}
					updateVertex(s);
				}
				if(u.rhs == gold && u.gridpoint != depart.gridpoint)
				{
					u.rhs = Integer.MAX_VALUE;
					for(int i = 0; i < 8; i++)
					{
						int voisin = gridspace.getGridPointVoisin(u.gridpoint, i);
						if(voisin < 0)
							continue;
						DStarLiteNode s = getFromMemory(voisin);
						u.rhs = Math.min(u.rhs, add(gridspace.distanceDStarLite(u.gridpoint, i), s.g));
					}
				}
				updateVertex(u);
			}

		}
	}

	/**
	 * Calcule un nouvel itinéraire.
	 * @param arrivee
	 * @param depart
	 */
	public void computeNewPath(Vec2<ReadOnly> depart, Vec2<ReadOnly> arrivee)
	{
		nbPF++;
		km = 0;
		this.depart = getFromMemory(gridspace.computeGridPoint(depart));
		last = this.depart.gridpoint;

		this.arrivee = getFromMemory(gridspace.computeGridPoint(arrivee));
		this.arrivee.rhs = 0;
		this.arrivee.cle.set(distanceHeuristique(this.arrivee.gridpoint), 0);
		
		openset.clear();
		openset.add(this.arrivee);

		computeShortestPath();
	}
	
	private int distanceHeuristique(int gridpoint)
	{
		return gridspace.distanceHeuristiqueDStarLite(depart.gridpoint, gridpoint);
	}

	@Override
	public void updateConfig(Config config)
	{}

	@Override
	public void useConfig(Config config)
	{}
	
	/**
	 * Met à jour le pathfinding
	 */
	public void updatePath(Vec2<ReadOnly> positionRobot)
	{
		depart = getFromMemory(gridspace.computeGridPoint(positionRobot));
		km += distanceHeuristique(last);
		last = depart.gridpoint;
		
		// TODO
		
		computeShortestPath();
	}
	
	/**
	 * Utilisé pour l'affichage et le debug
	 * @return
	 */
	public ArrayList<Vec2<ReadOnly>> itineraireBrut()
	{
		ArrayList<Vec2<ReadOnly>> trajet = new ArrayList<Vec2<ReadOnly>>();

		DStarLiteNode node = depart;
		DStarLiteNode min = null;
		int coutMin;
		
		while(!node.equals(arrivee))
		{
			trajet.add(gridspace.computeVec2(node.gridpoint));
			log.debug(node);
			coutMin = Integer.MAX_VALUE;
			
			for(int i = 0; i < 8; i++)
			{
				int voisin = gridspace.getGridPointVoisin(node.gridpoint, i);
				if(voisin < 0)
					continue;
				DStarLiteNode s = getFromMemory(voisin);
				int coutTmp = add(gridspace.distanceDStarLite(node.gridpoint, i), s.g);
				if(coutTmp < coutMin)
				{
					coutMin = coutTmp;
					min = s;
				}
			}
			node = min;
		}
		trajet.add(gridspace.computeVec2(arrivee.gridpoint));
		return trajet;
		
	}
	
	public int heuristicCostThetaStar(int gridpoint)
	{
		return getFromMemory(gridpoint).g;
	}
	
	public int getHashDebut()
	{
		return depart.gridpoint;
	}

	public int getHashArrivee()
	{
		return arrivee.gridpoint;
	}

	/**
	 * Somme en faisant attention aux valeurs infinies
	 * @param a
	 * @param b
	 * @return
	 */
	private int add(int a, int b)
	{
		if(a == Integer.MAX_VALUE || b  == Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return a + b;
	}

	/**
	 * Somme en faisant attention aux valeurs infinies
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	private int add(int a, int b, int c)
	{
		if(a == Integer.MAX_VALUE || b  == Integer.MAX_VALUE || c  == Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return a + b + c;
	}

	private int gridpointIterator;
	private int iterator;
	private ArrayList<Integer> voisinsTries;
	
	/**
	 * Renvoie l'itérateur des nodes du plus court au plus long
	 * @param gridpoint
	 * @return
	 */
	public Iterator<Integer> getIterator(int gridpoint)
	{
		gridpointIterator = gridpoint;
		iterator = 0;
		// TODO
		return voisinsTries.iterator();
	}
		
}