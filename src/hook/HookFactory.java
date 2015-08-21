package hook;

import java.util.ArrayList;

import permissions.ReadWrite;
import hook.methods.GameElementDone;
import hook.methods.ThrowScriptRequest;
import hook.types.HookContact;
import hook.types.HookDate;
import hook.types.HookPosition;
import container.Service;
import enums.Tribool;
import exceptions.FinMatchException;
import robot.RobotChrono;
import strategie.GameState;
import table.GameElementNames;
import table.GameElementType;
import utils.Log;
import utils.Config;

/**
 * Service fabriquant des hooks à la demande.
 * Les hooks sont soit simulés dans RobotChrono, soit envoyés à la STM
 * @author pf
 *
 */

public class HookFactory implements Service
{	
	//gestion des log
	private Log log;
	
	private ArrayList<Hook> hooks_table_chrono = null;
	
	// TODO: créer hooks_table_chrono dès la construction, et maintenir un numéro pour chaque hook
	
	/**
	 *  appelé uniquement par Container.
	 *  Initialise la factory
	 * 
	 * @param log système de log
	 */
	public HookFactory(Log log)
	{
		this.log = log;
	}

	@Override
	public void updateConfig(Config config)
	{}
	
	@Override
	public void useConfig(Config config)
	{
		// demande avec quelle tolérance sur la précision on déclenche les hooks
		Hook.useConfig(config);
	}

    /**
     * Donne les hooks des éléments de jeux à un chrono gamestate
     * @param state
     * @return
     * @throws FinMatchException 
     */
    public ArrayList<Hook> getHooksEntreScriptsChrono(GameState<RobotChrono,ReadWrite> state, int date_limite) throws FinMatchException
    {
    	if(hooks_table_chrono == null)
    		hooks_table_chrono = getHooksPermanents(state);

    	// on met à jour dans les hooks les références (gridspace, robot, ...)
		// C'est bien plus rapide que de créer de nouveaux hooks
		for(Hook hook: hooks_table_chrono)
			hook.updateGameState(state);

    	return hooks_table_chrono;
    }
    
    /**
     * Donne les hooks valables pendant tout le match à un gamestate
     * @param state
     * @return
     * @throws FinMatchException 
     */
    public ArrayList<Hook> getHooksPermanents(GameState<?,ReadWrite> state)
    {
    	ArrayList<Hook> hooksPermanents = new ArrayList<Hook>();
		Hook hook;
		GameElementDone action;
		
//		// Il faut s'assurer que le hook de fin de match est toujours en première position
//		hooksPermanents.add(getHooksFinMatch(state.getReadOnly()));
    	
		for(GameElementType t : GameElementType.values())
		{
			// Ce que l'ennemi peut prendre. Un hook par type d'élément de jeux
			if(t.isInCommon())
			{
				hook = new HookDate(log, state.getReadOnly(), t.getDateEnemyTakesIt());
				for(GameElementNames n: GameElementNames.values())
					if(n.getType() == t)
					{
						action = new GameElementDone(state, n, Tribool.MAYBE);
						hook.ajouter_callback(new Callback(new GameElementDone(state, n, Tribool.MAYBE)));
					}
				hooksPermanents.add(hook);
			}

			// Les hooks de contact
			if(t.scriptHookThrown() != null)
			{
				hook = new HookContact(log, state.getReadOnly(), t.scriptHookThrown().getNbCapteur());
				hook.ajouter_callback(new Callback(new ThrowScriptRequest(t.scriptHookThrown())));
				hooksPermanents.add(hook);
			}
		}

		for(GameElementNames n: GameElementNames.values())
		{
			// Ce qu'on peut shooter
			if(n.getType().canBeShot()) // on ne met un hook de collision que sur ceux qui ont susceptible de disparaître quand on passe dessus
			{
//				hook = new HookCollisionElementJeu(log, state.getReadOnly(), n.getObstacle());
				hook = new HookPosition(log, state.getReadOnly(), n.getObstacle().position, n.getObstacleDilate().radius);
				action = new GameElementDone(state, n, Tribool.TRUE);
				hook.ajouter_callback(new Callback(action));
				hooksPermanents.add(hook);
			}
		}
		return hooksPermanents;
    } 

}
