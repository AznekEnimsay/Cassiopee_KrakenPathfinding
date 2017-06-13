/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package kraken.exceptions;

/**
 * Exception thrown when the pathfinding isn't initialized
 * 
 * @author pf
 *
 */

public class NotInitializedPathfindingException extends PathfindingException
{

	private static final long serialVersionUID = -960091158805232282L;

	public NotInitializedPathfindingException()
	{
		super();
	}

	public NotInitializedPathfindingException(String m)
	{
		super(m);
	}

}
