/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.graphic;

import pfg.config.ConfigInfo;

/**
 * Informations accessibles par la config
 * Les informations de config.ini surchargent celles-ci
 * Certaines valeurs sont constantes, ce qui signifie qu'elles ne peuvent être
 * modifiées dynamiquement au cours d'un match.
 * Chaque variable a une valeur par défaut, afin de pouvoir lancer le programme
 * sans config.ini.
 * 
 * @author pf
 *
 */

public enum ConfigInfoGraphic implements ConfigInfo
{
	CONSOLE_NB_ROWS(10), // nombre de lignes dans la console affichée
	CONSOLE_NB_COLUMNS(30), // nombre de colonnes dans la console affichée
	
	DEFAULT_ZOOM(0), // zoom par défaut
	GRAPHIC_ENABLE(false), // autorise l'affichage 
	BACKGROUND_PATH(""), // background path ; empty if none	
	DISPLAY_GRID(true), // display the grid (over the background if there is one)
	REFRESH_FREQUENCY(5), // in Hertz, 0 to disable
	GRAPHIC_SERVER_PORT_NUMBER(13370), // port number of the graphic server
	SIZE_X_WINDOW(900), // taille par défaut (sans image) de la fenêtre
	SIZE_Y_WINDOW(600), // taille par défaut (sans image) de la fenêtre
	SIZE_X_WITH_UNITARY_ZOOM(50), // taille en mm de la zone à afficher (sur l'axe X)
	SIZE_Y_WITH_UNITARY_ZOOM(50); // taille en mm de la zone à afficher (sur l'axe Y)

	private Object defaultValue;
	public volatile boolean uptodate;

	private ConfigInfoGraphic(Object defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue()
	{
		return defaultValue;
	}
}
