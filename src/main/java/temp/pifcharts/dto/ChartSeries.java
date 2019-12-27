package temp.pifcharts.dto;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail
 */
public class ChartSeries<T> {
    private Color color;
    private String name;
    private java.util.List<Comparable[]> data = new ArrayList<>();

    private ChartSeries(Color color, String name, List<Comparable[]> data) {
        this.color = color;
        this.name = name;
        this.data = data;
    }

    public static ChartSeries<Long> createChartSeries(Color color, PifInvestData data) {
        List<Comparable[]> m = new ArrayList<>();
        for (long[] datum : data.getData()) {
            Long[] longs = {datum[0], datum[1]};
            m.add(longs);
        }
        return new ChartSeries<>(color, data.getName(), m);
    }

    public static <T extends Comparable> ChartSeries<T > createChartSeries(Color color, String name, List<Comparable[]> data) {
        return new ChartSeries<T>(color, name, data);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Comparable[]> getData() {
        return data;
    }

    public void setData(List<Comparable[]> data) {
        this.data = data;
    }
}
