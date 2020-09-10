package temp.pifcharts.gui;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

/**
 * @author Mikhail
 */
public class PifsChartPanel extends ChartPanel {

    public PifsChartPanel() {
        super(new JFreeChart(new LegendWithoutSeriesXYPlot()));
        addChartMouseListener(new HideSeriesChartMouseListener());
    }

    public LegendWithoutSeriesXYPlot getPlot() {
        return (LegendWithoutSeriesXYPlot) getChart().getPlot();
    }

    private static class HideSeriesChartMouseListener implements ChartMouseListener {
        @Override
        public void chartMouseClicked(ChartMouseEvent event) {
            ChartEntity entity = event.getEntity();
            if (event.getEntity() instanceof LegendItemEntity) {
                showOrHide(event, (LegendItemEntity) entity);
            }
        }

        private void showOrHide(ChartMouseEvent event, LegendItemEntity legendItem) {
            XYDataset dataset = (XYDataset) legendItem.getDataset();
            int index = dataset.indexOf(legendItem.getSeriesKey());
            XYPlot plot = (XYPlot) event.getChart().getPlot();

            //set the renderer to hide the series
            XYItemRenderer renderer = plot.getRenderer();
            boolean seriesVisible = renderer.isSeriesVisible(index);
            boolean redrawAllSeries = false;
            renderer.setSeriesVisible(index, !seriesVisible, redrawAllSeries);
            renderer.setSeriesVisibleInLegend(index, true, false);
        }

        @Override
        public void chartMouseMoved(ChartMouseEvent event) {
        }
    }
}
