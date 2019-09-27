package temp.pifcharts;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import temp.pifcharts.dto.SeriesConfig;

import static temp.pifcharts.DataDownloader.createMapper;

/**
 * @author Mikhail
 */
public class ConfigService {
    private static List<SeriesConfig> DEFAULT_CONFIG = Arrays.asList(
            new SeriesConfig(new Color(0xFF, 0x55, 0x55), "43", "Dobrynya_Nikitich", false),
            new SeriesConfig(new Color(0x55, 0x55, 0xFF), "641", "prirResource"),
            new SeriesConfig(new Color(0x55, 0xFF, 0x55), "5247", "mosBirgaBrutto"),

            new SeriesConfig(new Color(0xFF, 0x40, 0x40), "2050", "glob_dolg"),
            new SeriesConfig(new Color(0xFF, 0xFF, 0x55), "3125", "euroobligation", false),

//            new SeriesConfig(new Color(0x55, 0xFF, 0xFF), 4265", "arendn_bisnes", false),
            new SeriesConfig(new Color(0x00, 0xC0, 0xC0), "1070", "act_upravlenie", false),
            new SeriesConfig(new Color(0xff, 0xaf, 0xaf), "47", "Ilia_Muromec", false),
            new SeriesConfig(new Color(0x80, 0x80, 0x80), "764", "america", false),
            new SeriesConfig(new Color(0xc0, 0x00, 0x00), "45", "sbalansir"),
            new SeriesConfig(new Color(0x00, 0x00, 0xC0), "957", "fin_sector"),
            new SeriesConfig(new Color(0xC0, 0xC0, 0x00), "2225", "gold"),
            new SeriesConfig(new Color(0x00, 0xC0, 0x00), "803", "potrebSector"),
            new SeriesConfig(new Color(0xC0, 0x00, 0xC0), "2233", "globInternet"),
            new SeriesConfig(new Color(0x55, 0xFF, 0xFF), "640", "telecomAndTech"),
            new SeriesConfig(new Color(0x40, 0x40, 0x40), "366", "perspect"),
            new SeriesConfig(new Color(0xFF, 0x55, 0xFF), "642", "electroenerg")
    );
    private final ObjectMapper objectMapper = createMapper();

    public List<SeriesConfig> getConfigs()
            throws IOException {
        File configFile = new File(FilenameUtils.concat(FileUtils.getUserDirectory().getPath(), ".pifsChart/series.json"));
        List<SeriesConfig> configs;
        if (!configFile.exists()) {
            new File(configFile.getParent()).mkdirs();
            configFile.createNewFile();
            objectMapper.writer().withDefaultPrettyPrinter().writeValue(configFile, DEFAULT_CONFIG);
            configs = DEFAULT_CONFIG;
        } else {
            configs = objectMapper.readValue(configFile, new TypeReference<List<SeriesConfig>>() {});
        }
        return configs;
    }
}
