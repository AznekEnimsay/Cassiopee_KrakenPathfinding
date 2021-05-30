/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.graphic;


import pfg.config.Config;
import pfg.injector.Injector;
import pfg.injector.InjectorException;
import pfg.kraken.display.Display;
import pfg.kraken.struct.XY;
import pfg.log.Log;
import pfg.log.Severity;
import pfg.graphic.*;

/**
 * The debug tool
 * @author pf
 *
 */

public class DebugTool
{
	private Injector injector;
	private Config config;
	private static DebugTool instance = null;

	public static DebugTool getExistingDebugTool()
	{
		return instance;
	}

	public static DebugTool getDebugTool(XY defaultCenter, XY center, Severity cat, String configFilename, String... configprofile)
	{
		if(instance == null)
			instance = new DebugTool(defaultCenter, center, cat, configFilename, configprofile);
		return instance;
	}

	public static DebugTool getDebugTool(XY defaultCenter, Severity cat, String configFilename, String... configprofile)
	{
		if(instance == null)
			instance = new DebugTool(defaultCenter, defaultCenter, cat, configFilename, configprofile);
		return instance;
	}

	private DebugTool(XY defaultCenter, XY center, Severity cat, String configFilename, String... configprofile)
	{
		XY defaultCenter2 = new XY(defaultCenter.getX(), defaultCenter.getY());
		config = new Config(pfg.graphic.ConfigInfoGraphic.values(), false, configFilename, configprofile);
		injector = new Injector();
		injector.addService(injector);
		injector.addService(config);
		Log log;
		try {
			log = new Log(cat, configFilename, configprofile);
			injector.addService(log);
			pfg.graphic.WindowFrame fenetre;
			pfg.graphic.GraphicDisplay gd = new pfg.graphic.GraphicDisplay(defaultCenter2, center);
			injector.addService(gd);
			if(config.getBoolean(pfg.graphic.ConfigInfoGraphic.GRAPHIC_ENABLE))
			{
				pfg.graphic.GraphicPanel g = new pfg.graphic.GraphicPanel(defaultCenter2, center, config, gd);
				injector.addService(g);
				fenetre = injector.getService(pfg.graphic.WindowFrame.class);
				injector.addService(fenetre);
				double frequency = config.getDouble(pfg.graphic.ConfigInfoGraphic.REFRESH_FREQUENCY);
				if(frequency != 0)
				{
					assert injector.getExistingService(pfg.graphic.ThreadRefresh.class) == null;
					pfg.graphic.ThreadRefresh t = injector.getService(pfg.graphic.ThreadRefresh.class);
					t.setFrequency(frequency);
					t.start();
				}
			}
		} catch (InjectorException e) {
			e.printStackTrace();
		}
	}
	
	public void destructor()
	{
		pfg.graphic.WindowFrame f = injector.getExistingService(pfg.graphic.WindowFrame.class);
		if(f != null)
			f.close();
		
		if(injector.getExistingService(pfg.graphic.ThreadRefresh.class) != null)
			injector.getExistingService(pfg.graphic.ThreadRefresh.class).interrupt();
		if(injector.getExistingService(pfg.graphic.ThreadPrintClient.class) != null)
			injector.getExistingService(pfg.graphic.ThreadPrintClient.class).interrupt();
		if(injector.getExistingService(pfg.graphic.ThreadPrintServer.class) != null)
			injector.getExistingService(pfg.graphic.ThreadPrintServer.class).interrupt();
		if(injector.getExistingService(pfg.graphic.ThreadSaveVideo.class) != null)
			injector.getExistingService(pfg.graphic.ThreadSaveVideo.class).interrupt();
	}

	public void startPrintClient(String hostname)
	{
		try {
			if(injector.getExistingService(pfg.graphic.ThreadPrintClient.class) == null)
			{
				pfg.graphic.ThreadPrintClient th = injector.getService(pfg.graphic.ThreadPrintClient.class);
				th.setHostname(hostname);
				th.start();
			}
		} catch (InjectorException e) {
			e.printStackTrace();
			assert false : e;
		}
	}
	
	public void startPrintServer()
	{
		try {
			if(injector.getExistingService(pfg.graphic.ThreadPrintServer.class) == null)
				injector.getService(pfg.graphic.ThreadPrintServer.class).start();
		} catch (InjectorException e) {
			e.printStackTrace();
			assert false : e;
		}
	}
	
	public void startSaveVideo()
	{
		try {
			if(injector.getExistingService(pfg.graphic.ThreadSaveVideo.class) == null)
				injector.getService(pfg.graphic.ThreadSaveVideo.class).start();
		} catch (InjectorException e) {
			e.printStackTrace();
			assert false : e;
		}
	}
	
	public Display getDisplay()
	{
		return injector.getExistingService(pfg.graphic.GraphicPanel.class);
	}

	/*
	public WindowFrame getWindowFrame()
	{
		return injector.getExistingService(WindowFrame.class);
	}*/
	
	/**
	 * Print the values overridden by the configuration file
	 */
	public void displayOverriddenConfigValues()
	{
		config.printChangedValues();
	}
}
