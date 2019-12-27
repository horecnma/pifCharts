package temp.pifcharts.gui;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

/**
 * @author Mikhail
 */
public class PifsChartPanel extends ChartPanel {

    public PifsChartPanel() {
        super(new JFreeChart(new LegendWithoutSeriesXYPlot().init()));
        addChartMouseListener(new HideSeriesChartMouseListener());
        addChartMouseListener(new ShowLineMouseListener(this));
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

    private class ShowLineMouseListener implements ChartMouseListener {
        private final PifsChartPanel chartPanel;

        public ShowLineMouseListener(PifsChartPanel chartPanel) {
            this.chartPanel = chartPanel;
        }

        @Override
        public void chartMouseMoved(ChartMouseEvent event) {
        }

        @Override
        public void chartMouseClicked(ChartMouseEvent event) {
            ChartEntity entity = event.getEntity();
            if (entity instanceof PlotEntity) {
                PlotEntity plotEntity = (PlotEntity) entity;
                plotEntity.getShapeCoords();
                LegendWithoutSeriesXYPlot plot = (LegendWithoutSeriesXYPlot) plotEntity.getPlot();
                System.out.println(event.getTrigger().getPoint());

                Point2D p = chartPanel.translateScreenToJava2D(event.getTrigger().getPoint());
                Rectangle2D plotArea = chartPanel.getScreenDataArea();
                double chartX = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
                double chartY = plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge());
                System.out.println(new Date((long) chartX) + "  " + chartY);

                plot.drawMouseLine(plotArea, chartPanel.getGraphics(), new Date((long) chartX));
            }
        }
    }
}
