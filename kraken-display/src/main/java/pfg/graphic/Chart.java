/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * Distributed under the MIT License.
 */

package pfg.graphic;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;


/**
 * Affichage des données de debug
 * @author pf
 *
 */

public class Chart extends ApplicationFrame
{
	private static final long serialVersionUID = 1L;
	private HashMap<String, TimeSeries> series = new HashMap<String, TimeSeries>();
    private TimeSeriesCollection dataset = new TimeSeriesCollection();
	private boolean init = false;
	private String title, xAxisLabel, yAxisLabel;
	
    /**
     * Ajoute des données à afficher
     * @param name
     * @param value
     */
	public void addData(String name, Double value)
	{
		if(!init)
			init();
		
		Date temps = new Date();

		TimeSeries ts = series.get(name);
		if(ts == null)
		{
			ts = new TimeSeries(name);
			series.put(name, ts);
            dataset.addSeries(ts);
		}
		ts.addOrUpdate(new Millisecond(temps), value);
	}
	
    /**
     * Ajoute des données à afficher
     * @param values
     */
	public void addData(HashMap<String, Double> values)
	{
		for(String name : values.keySet())
			addData(name, values.get(name));
	}
	
    public Chart(String title, String xAxisLabel, String yAxisLabel)
    {    	
        super("");
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
    }    
    
    /**
     * L'initialisation se fait à part afin de ne pas ouvrir une fenêtre dès qu'on crée un objet
     */
    private void init()
    {
    	init = true;
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
        		title,  		// title
        		xAxisLabel,            // x-axis label
        		yAxisLabel,   		// y-axis label
        		dataset,            // data
        		true,               // create legend?
        		true,               // generate tooltips?
        		false               // generate URLs?
        		);

        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer)
        {
        	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
        	renderer.setBaseShapesVisible(true);
        	renderer.setBaseShapesFilled(true);
        	renderer.setDrawSeriesLineAsPath(true);
      	}
        
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new java.awt.Dimension(1024, 600));
        setContentPane(panel);

		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
    }

}