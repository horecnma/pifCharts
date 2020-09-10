package temp.pifcharts.gui;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import temp.pifcharts.Application;
import temp.pifcharts.dto.Data;
import temp.pifcharts.dto.PifSeries;

/**
 * @author Mikhail
 */
public class DatasetProvider {
    public TimeSeriesCollection createDataset(List<PifSeries> data, LocalDate from) {
        TimeSeriesCollection collection = new TimeSeriesCollection();
        for (PifSeries datum : data) {
            collection.addSeries(getSeries(datum.getData(), from));
        }
        return collection;
    }

    private TimeSeries getSeries(Data data, LocalDate from) {
        TimeSeries result = new TimeSeries(data.getName());
        long fromLong = Application.toDate(from).getTime();
        List<long[]> collect = data.getData().stream().filter(it -> it[0] > fromLong).collect(Collectors.toList());
        long initValue = collect.get(0)[1];
        for (long[] datum : collect) {
            Date date = new Date(datum[0]);
            result.add(new Day(date), (((double) (datum[1] - initValue)) / (double) initValue) * 100);
        }
        return result;
    }
}
