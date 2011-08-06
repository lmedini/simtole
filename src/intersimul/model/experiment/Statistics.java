package intersimul.model.experiment;



import intersimul.util.xml.StatisticsHandler;

import java.awt.Font;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.xml.sax.SAXException;


public class Statistics extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public Statistics(final String title) {

        super(title);
        final StatisticalCategoryDataset dataset = createDataset();

        final CategoryAxis xAxis = new CategoryAxis("Scenarios");
        xAxis.setLowerMargin(0.01d); // percentage of space before first bar
        xAxis.setUpperMargin(0.01d); // percentage of space after last bar
        xAxis.setCategoryMargin(0.05d); // percentage of space between categories
        final ValueAxis yAxis = new NumberAxis("");

        // define the plot
        final CategoryItemRenderer renderer = new StatisticalBarRenderer();
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart("Statistiques",
                                          new Font("Helvetica", Font.BOLD, 14),
                                          plot,
                                          true);
        //chart.setBackgroundPaint(Color.white);
        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    //dont close the whole programme 
    public void windowClosing(final WindowEvent evt){
    	if(evt.getWindow() == this){
    		dispose();
    	}
    }

    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */
    private StatisticalCategoryDataset createDataset() {

        final DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
        String stats = ParamLoader.basePath+ ParamLoader.slash + "out"+ ParamLoader.slash +"Stats.xml";
        StatisticsHandler px = new StatisticsHandler();
		ArrayList<SimulationResults> sc =new ArrayList<SimulationResults>();
		try {
			sc = px.parseXMLStatistics(stats);
		} catch (SAXException e) {
			e.printStackTrace();
		}
        //parser fichier XML
		//System.out.println(sc.size());
		for (int i=0;i<sc.size();i++){
			result.add(Integer.parseInt(sc.get(i).getNbMessages()), 15,  "Messages exchanged",sc.get(i).getName());
			result.add(Integer.parseInt(sc.get(i).getNbMappingInit()), 15, "Mapping", sc.get(i).getName());
			result.add(Integer.parseInt(sc.get(i).getNbMappingFinal()), 15, "Mapping & Axiom", sc.get(i).getName());
			
			result.add(Integer.parseInt(sc.get(i).nbMappingFinal)- Integer.parseInt(sc.get(i).nbMappingInit), 15, "Mapping difference", sc.get(i).getName());
			//result.add(40, 15, "SimulationResults", " ");
		}
        return result;
    }
}