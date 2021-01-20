package temp.pifcharts.gui;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import temp.pifcharts.Application;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author Mikhail
 */
public class LegendWithoutSeriesXYPlot extends XYPlot {
    public LegendWithoutSeriesXYPlot() {
        super(new TimeSeriesCollection(),
              new DateAxis(),
              new NumberAxis(), new SamplingXYLineRenderer() {
                    @Override
                    public LegendItem getLegendItem(int datasetIndex, int series) {
                        LegendItem legendItem = super.getLegendItem(datasetIndex, series);
                        legendItem.setShapeVisible(true);
                        boolean seriesVisible = isSeriesVisible(series);
                        legendItem.setLabelPaint(!seriesVisible ? Color.LIGHT_GRAY : Color.BLACK);

                        return legendItem;
                    }
                }
        );
        //noinspection OverridableMethodCallDuringObjectConstruction
        addRangeMarker(new ValueMarker(0, Color.BLACK, new BasicStroke(0.5f)));
        //noinspection OverridableMethodCallDuringObjectConstruction
        setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);
    }

    @Override
    public TimeSeriesCollection getDataset() {
        return (TimeSeriesCollection) super.getDataset();
    }

    @Override
    public DateAxis getDomainAxis() {
        return (DateAxis) super.getDomainAxis();
    }

    public void updateDomainMarkers(LocalDate startDate) {
        clearDomainMarkers();
        getDomainAxis().setAutoRange(true);
        getDomainAxis().setAutoTickUnitSelection(true);
        getDomainAxis().setTickMarkPosition(DateTickMarkPosition.END);

        if (Math.abs(DAYS.between(startDate, LocalDate.now()) - 30) < 10) {
            drawLineMonth(1, startDate);
        } else if (Math.abs(DAYS.between(startDate, LocalDate.now()) - 90) < 10) {
            drawLineMonth(1, startDate);
        } else if (Math.abs(DAYS.between(startDate, LocalDate.now()) - 180) < 10) {
            drawLineMonth(1, startDate);
        } else if (Math.abs(DAYS.between(startDate, LocalDate.now()) - 365) < 10) {
            getDomainAxis().setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1, new SimpleDateFormat("MMM-yy")));
        } else if (Math.abs(DAYS.between(startDate, LocalDate.now()) - 365 * 3) < 10) {
            getDomainAxis().setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 3, new SimpleDateFormat("MMM-yy")));
            drawLineYear(1, startDate);
        } else if (Math.abs(DAYS.between(startDate, LocalDate.now()) - 365 * 6) < 10) {
            getDomainAxis().setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 6, new SimpleDateFormat("MMM-yy")));
            drawLineYear(1, startDate);
        } else {
            getDomainAxis().setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 1, new SimpleDateFormat("yyyy")));
        }
    }

    private void drawLineYear(int amountToAdd, LocalDate startDate) {
        Year my = Year.from(startDate);
        while (my.isBefore(Year.now().plusYears(amountToAdd))) {
            addDomainMarker(new ValueMarker(Application.toDate(my.atDay(1)).getTime(), Color.BLACK, new BasicStroke(0.2f)));
            my = my.plusYears(amountToAdd);
        }
    }

    private void drawLineMonth(int amountToAdd, LocalDate startDate) {
        YearMonth my = YearMonth.from(startDate);
        while (my.isBefore(YearMonth.now().plusMonths(amountToAdd))) {
            boolean newYear = my.getMonth() == Month.JANUARY;
            addDomainMarker(new ValueMarker(Application.toDate(my.atDay(1)).getTime(), Color.BLACK, new BasicStroke(newYear ? 0.6f : 0.2f)));
            my = my.plusMonths(amountToAdd);
        }
    }

    @Override
    public LegendItemCollection getLegendItems() {
        if (getFixedLegendItems() != null) {
            return getFixedLegendItems();
        }
        LegendItemCollection result = new LegendItemCollection();
        for (int dataSetIndex = 0; dataSetIndex < getDatasetCount(); dataSetIndex++) {
            XYDataset dataset = getDataset(dataSetIndex);
            if (dataset == null) {
                continue;
            }
            int datasetIndex = indexOf(dataset);
            XYItemRenderer renderer = getRenderer(datasetIndex);
            if (renderer == null) {
                renderer = getRenderer(0);
            }
            if (renderer != null) {
                int seriesCount = dataset.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    boolean seriesVisible = renderer.isSeriesVisible(i);
                    boolean seriesLegendVisible = renderer.isSeriesVisibleInLegend(i);
                    if (seriesLegendVisible) {// original code: v1 && v2
                        LegendItem item = renderer.getLegendItem(datasetIndex, i);
                        if (item != null) {
                            result.add(item);
                        }
                    }
                }
            }
        }
        return result;
    }
}
