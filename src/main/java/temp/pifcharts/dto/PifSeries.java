package temp.pifcharts.dto;

import java.awt.*;

/**
 * @author Mikhail
 */
public class PifSeries {
    private Color color;
    private Data data;

    public PifSeries(Color color, Data data) {
        this.color = color;
        this.data = data;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
