package temp.pifcharts.dto;


import java.util.ArrayList;
import java.util.List;


/**
 * Data with absolute values from external source
 *
 * @author Mikhail
 */
public class PifInvestData {
    private String name;
    private List<long[]> data = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<long[]> getData() {
        return data;
    }

    public void setData(List<long[]> data) {
        this.data = data;
    }
}
