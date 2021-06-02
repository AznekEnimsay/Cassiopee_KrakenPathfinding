/*
 * Copyright (C) 2013-2018 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.log;

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

public enum ConfigInfoLog implements ConfigInfo
{
	SAVE_LOG(false), // save the log into a log file
	FAST_LOG(false), // log rapide, sans reflection
	STDOUT_LOG(true); // log into the stdout

	private Object defaultValue;
	public volatile boolean uptodate;

	private ConfigInfoLog(Object defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue()
	{
		return defaultValue;
	}
}
