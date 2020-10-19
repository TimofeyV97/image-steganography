package controllers;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;

public class Plot extends JFrame {

    public Plot(final String title, final XYSeries series) {
        super(title);

        final XYDataset dataset = createDataset(series);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "PSNR/Message length",
                "Message length",
                "PSNR value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        setContentPane(new ChartPanel(chart));
    }

    private XYDataset createDataset(final XYSeries series) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }


}
