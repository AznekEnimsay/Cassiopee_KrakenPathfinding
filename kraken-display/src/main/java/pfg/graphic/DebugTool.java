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
		config = new Config(ConfigInfoGraphic.values(), false, configFilename, configprofile);
		injector = new Injector();
		injector.addService(injector);
		injector.addService(config);
		Log log;
		try {
			log = new Log(cat, configFilename, configprofile);
			injector.addService(log);
			WindowFrame fenetre;
			GraphicDisplay gd = new GraphicDisplay(defaultCenter2, center);
			injector.addService(gd);
			if(config.getBoolean(ConfigInfoGraphic.GRAPHIC_ENABLE))
			{
				GraphicPanel g = new GraphicPanel(defaultCenter2, center, config, gd);
				injector.addService(g);
				fenetre = injector.getService(WindowFrame.class);
				injector.addService(fenetre);
				double frequency = config.getDouble(ConfigInfoGraphic.REFRESH_FREQUENCY);
				if(frequency != 0)
				{
					assert injector.getExistingService(ThreadRefresh.class) == null;
					ThreadRefresh t = injector.getService(ThreadRefresh.class);
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
		WindowFrame f = injector.getExistingService(WindowFrame.class);
		if(f != null)
			f.close();
		
		if(injector.getExistingService(ThreadRefresh.class) != null)
			injector.getExistingService(ThreadRefresh.class).interrupt();
		if(injector.getExistingService(ThreadPrintClient.class) != null)
			injector.getExistingService(ThreadPrintClient.class).interrupt();
		if(injector.getExistingService(ThreadPrintServer.class) != null)
			injector.getExistingService(ThreadPrintServer.class).interrupt();
		if(injector.getExistingService(ThreadSaveVideo.class) != null)
			injector.getExistingService(ThreadSaveVideo.class).interrupt();
	}

	public void startPrintClient(String hostname)
	{
		try {
			if(injector.getExistingService(ThreadPrintClient.class) == null)
			{
				ThreadPrintClient th = injector.getService(ThreadPrintClient.class);
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
			if(injector.getExistingService(ThreadPrintServer.class) == null)
				injector.getService(ThreadPrintServer.class).start();
		} catch (InjectorException e) {
			e.printStackTrace();
			assert false : e;
		}
	}
	
	public void startSaveVideo()
	{
		try {
			if(injector.getExistingService(ThreadSaveVideo.class) == null)
				injector.getService(ThreadSaveVideo.class).start();
		} catch (InjectorException e) {
			e.printStackTrace();
			assert false : e;
		}
	}
	
	public Display getDisplay()
	{
		return injector.getExistingService(GraphicPanel.class);
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
