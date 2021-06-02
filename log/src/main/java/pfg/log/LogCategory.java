/*
 * Copyright (C) 2013-2018 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.log;

/**
 * A log category
 * @author pf
 *
 */

public interface LogCategory
{
	public boolean shouldPrint();
	public int getMask();
}
