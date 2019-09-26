package temp.pifcharts;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import temp.pifcharts.dto.Data;

/**
 * @author Mikhail
 */
public class DataDownloader {
    private final ObjectMapper objectMapper = createMapper();
    private List<SeriesConfig> defaultConfig = Arrays.asList(
            new SeriesConfig("2225", "gold"),
            new SeriesConfig("641", "prirResource"),
            new SeriesConfig("803", "potrebSector"),
            new SeriesConfig("2233", "globInternet"),
            new SeriesConfig("764", "america"),
            new SeriesConfig("5247", "mosBirgaBrutto"),
            new SeriesConfig("3125", "euroobligation"),
            new SeriesConfig("640", "telecomAndTech"),
            new SeriesConfig("366", "prespect"),
            new SeriesConfig("642", "electroenerg")
    );

    public List<Data> getData(LocalDate fromDate)
            throws Exception {
        File configFile = new File(FilenameUtils.concat(FileUtils.getUserDirectory().getPath(), ".pifsChart/series.json"));
        List<SeriesConfig> configs;
        if (!configFile.exists()) {
            new File(configFile.getParent()).mkdirs();
            configFile.createNewFile();
            objectMapper.writer().withDefaultPrettyPrinter().writeValue(configFile, defaultConfig);
            configs = defaultConfig;
        } else {
            configs = objectMapper.readValue(configFile, new TypeReference<List<SeriesConfig>>() {});
        }
        return dowload(configs, fromDate);
    }

    private List<Data> dowload(List<SeriesConfig> configs, LocalDate fromDate)
            throws MalformedURLException {
        String from = fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String template = "https://investfunds.ru/funds/#########/?action=chartData&data_key=pay&date_from=" + from;

        List<Data> result = new ArrayList<>();
        for (SeriesConfig config : configs) {
            if (config.isEnabled()) {
                URL url = new URL(template.replace("#########", config.getId()));
                result.add(downloadUrl(url));
            }
        }
        return result;
    }

    private Data downloadUrl(URL url) {
        try {
            return objectMapper.readValue(url, Data.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private ObjectMapper createMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static class SeriesConfig {
        private String name;
        private String id;
        private boolean enabled = false;

        public SeriesConfig() {
        }

        public SeriesConfig(String id, String name) {
            this.id = id;
            this.name = name;
            this.enabled = true;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
