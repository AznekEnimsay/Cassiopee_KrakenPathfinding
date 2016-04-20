package robot;

import permissions.ReadWrite;
import utils.Vec2;

/**
 * Une structure qui regroupe des infos de cinématique
 * @author pf
 *
 */

public class Cinematique
{
	public final Vec2<ReadWrite> position = new Vec2<ReadWrite>();
	public volatile double orientation;
	public volatile boolean enMarcheAvant;
	public volatile double courbure;
	public volatile double vitesseTranslation;
	public volatile double vitesseRotation;
	
	/**
	 * Renvoie vrai si this est proche de autre
	 * @param autre
	 * @return
	 */
	public boolean estProche(Cinematique autre)
	{
		return true; // TODO écrire
	}

	/**
	 * Renvoie vrai si this est proche de autre. Plus laxiste que "estProche"
	 * @param autre
	 * @return
	 */
	public boolean estProcheUrgence(Cinematique autre)
	{
		return true; // TODO écrire
	}

	/**
	 * Copie this dans autre
	 * @param autre
	 */
	public void copy(Cinematique autre)
	{
    	Vec2.copy(position.getReadOnly(), autre.position);
    	autre.orientation = orientation;
    	autre.enMarcheAvant = enMarcheAvant;
    	autre.courbure = courbure;
    	autre.vitesseRotation = vitesseRotation;
    	autre.vitesseTranslation = vitesseTranslation;
	}

	public void setVitesse(Speed speed)
	{
		vitesseRotation = speed.rotationalSpeed;
		vitesseTranslation = speed.translationalSpeed;
	}
}