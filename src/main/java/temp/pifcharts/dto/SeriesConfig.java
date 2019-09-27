package temp.pifcharts.dto;

import java.awt.*;

/**
 * @author Mikhail
 */
public class SeriesConfig {
    private String name;
    private String pifInvestId;
    private boolean enabled = false;
    private Color color;

    public SeriesConfig() {
    }

    public SeriesConfig(Color color, String pifInvestId, String name, boolean enabled) {
        this.pifInvestId = pifInvestId;
        this.color = color;
        this.name = name;
        this.enabled = enabled;
    }

    public SeriesConfig(Color color, String pifInvestId, String name) {
        this.pifInvestId = pifInvestId;
        this.color = color;
        this.name = name;
        this.enabled = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPifInvestId() {
        return pifInvestId;
    }

    public void setPifInvestId(String pifInvestId) {
        this.pifInvestId = pifInvestId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
