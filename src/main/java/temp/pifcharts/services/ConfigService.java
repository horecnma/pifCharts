package temp.pifcharts.services;

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

import static temp.pifcharts.services.DataDownloader.createMapper;

/**
 * @author Mikhail
 */
public class ConfigService {
    private static List<SeriesConfig> DEFAULT_CONFIG = Arrays.asList(
            new SeriesConfig(new Color(0xff5555), "43", "Dobrynya_Nikitich", false),
            new SeriesConfig(new Color(0x0000c0), "641", "prirResource", true),
            new SeriesConfig(new Color(0x5555ff), "5247", "mosBirgaBrutto", true),

            new SeriesConfig(new Color(0x55ff55), "2050", "glob_dolg", true),
            new SeriesConfig(new Color(0xffff55), "3125", "euroobligation", false),

            new SeriesConfig(new Color(0x00c0c0), "1070", "act_upravlenie", false),
            new SeriesConfig(new Color(0xffafaf), "47", "Ilia_Muromec", false),
            new SeriesConfig(new Color(0x808080), "764", "america", false),
            new SeriesConfig(new Color(0x00c000), "45", "sbalansir", true),
            new SeriesConfig(new Color(0xff55ff), "957", "fin_sector", true),
            new SeriesConfig(new Color(0xc0c000), "2225", "gold", true),
            new SeriesConfig(new Color(0xc00000), "803", "potrebSector", true),
            new SeriesConfig(new Color(0xff4040), "2233", "globInternet", true),
            new SeriesConfig(new Color(0xc000c0), "640", "telecomAndTech", false),
            new SeriesConfig(new Color(0x404040), "366", "perspect", true),
            new SeriesConfig(new Color(0x55ffff), "642", "electroenerg", true)
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
