package temp.pifcharts.dto;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail
 */
public class Data {
    private String name;
    private List<long[]> data = new ArrayList<long[]>();

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
