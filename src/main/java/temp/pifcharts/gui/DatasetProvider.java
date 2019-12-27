package temp.pifcharts.gui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import temp.pifcharts.Application;
import temp.pifcharts.dto.ChartSeries;

/**
 * @author Mikhail
 */
public class DatasetProvider {

    public TimeSeriesCollection createJavaDataset(List<ChartSeries<Long>> data, LocalDate from) {
        TimeSeriesCollection collection = new TimeSeriesCollection();
        for (ChartSeries<Long> datum : data) {
            TimeSeries result = new TimeSeries(datum.getName());
            for (Object[] entry : toPercentage(datum, from).getData()) {
                result.add(new Day(new Date((Long) entry[0])), (Double) entry[1]);
            }
            collection.addSeries(result);
        }
        return collection;
    }

    public List<ChartSeries<Double>> createJsDataset(List<ChartSeries<Long>> data, LocalDate from) {
        List<ChartSeries<Double>> collection = new ArrayList<>();
        for (ChartSeries<Long> datum : data) {
            collection.add(toPercentage(datum, from));
        }
        return collection;
    }

    public String createCsv(List<ChartSeries<Long>> data, LocalDate from) {
        Table<Long, String, Double> table = TreeBasedTable.create();
        TreeMap<Long, List<Double>> dateToSeriesValues = new TreeMap<>();
        int i=0;
        for (ChartSeries<Long> datum : data) {
            ChartSeries<Double> percentData = toPercentage(datum, from);
            for (Comparable[] percentDataDatum : percentData.getData()) {
                Long time = (Long) percentDataDatum[0];
                List<Double> doubles = dateToSeriesValues.computeIfAbsent(time, k -> new ArrayList<>());
                doubles.add((Double) percentDataDatum[1]);
                table.put(time, "\n" + datum.getName(), (Double) percentDataDatum[1]);
            }
            i++;
        }
        StringBuilder s = new StringBuilder();
        s.append("\"Время,").append(data.stream().map(it->it.getName()).collect(Collectors.joining(","))).append("\" + \n");
        for (Long time : table.rowKeySet()) {
            s.append("\""+time).append(",");
            s.append(data.stream().map(datum -> table.get(time, "\n" + datum.getName()).toString()).collect(Collectors.joining(","))).append("\"+\n");
        }
        s.append("\"");
        return s.toString();
    }

    private ChartSeries<Double> toPercentage(ChartSeries<Long> datum, LocalDate from) {
        long fromLong = Application.toDate(from).getTime();
        List<Comparable[]> collect = datum.getData().stream()
                                          .filter(it -> (Long) it[0] >= fromLong)
                                          .collect(Collectors.toList()
                                          );

        Long initValue = collect.stream()
                                .min(Comparator.comparingLong(value -> (Long)value[0]))
                                .map(it -> (Long) it[1])
                                .orElseThrow(() -> new IllegalArgumentException(datum.getName()));
        List<Comparable[]> result = new ArrayList<>();
        for (Comparable[] entry : collect) {
            Comparable[] comparables = {entry[0], getPercentValue(initValue, (Long) entry[1])};
            result.add(comparables);
        }
        return ChartSeries.createChartSeries(datum.getColor(), datum.getName(), result);
    }

    private double getPercentValue(Long initValue, long absoluteValue) {
        return (((double) (absoluteValue - initValue)) / (double) initValue) * 100;
    }
}
