package temp.pifcharts.services;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import temp.pifcharts.dto.Data;
import temp.pifcharts.dto.PifSeries;
import temp.pifcharts.dto.SeriesConfig;

/**
 * @author Mikhail
 */
public class DataDownloader {
    private final ObjectMapper objectMapper = createMapper();
    private final ConfigService configService = new ConfigService();

    public List<PifSeries> getData(LocalDate fromDate)
            throws Exception {
        String from = fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String template = "https://investfunds.ru/funds/#########/?action=chartData&currencyId=1&data_key=pay&date_from=" + from + "&ids%5B%5D=#########";

        List<PifSeries> result = new ArrayList<>();
        for (SeriesConfig config : configService.getConfigs()) {
            if (config.isEnabled()) {
                URL url = new URL(template.replaceAll("#########", config.getPifInvestId()));
                Data data = downloadUrl(url);
                result.add(new PifSeries(config.getColor(), data));
            }
        }
        return result;
    }

    private Data downloadUrl(URL url) {
        try {
            List<Data> data = objectMapper.readValue(url, new TypeReference<List<Data>>() {});
            data.forEach(it -> it.setName(it.getName().replaceAll("<br>", " ").replaceAll("</br>", " ")));
            validate(url, data.get(0));
            return data.get(0);
        } catch (Exception e) {
            System.out.println("error in " + url);
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private void validate(URL url, Data data) {
        if (data.getName() == null || data.getName().equals("")) {
            throw new IllegalArgumentException("empty name in " + url);
        }
    }

    public static ObjectMapper createMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Color.class, new ColorSerializer());
        module.addDeserializer(Color.class, new ColorDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }

    private static class ColorSerializer extends StdSerializer<Color> {
        ColorSerializer() {super(Color.class);}

        @Override
        public void serialize(Color value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(colorToString(value));
        }

        private String colorToString(Color c) {
            return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        }
    }

    private static class ColorDeserializer extends StdDeserializer<Color> {
        ColorDeserializer() {super(Color.class);}

        @Override
        public Color deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            String valueAsString = p.getValueAsString();
            return fromString(valueAsString);
        }

        private Color fromString(String colorStr) {
            return new Color(
                    Integer.valueOf(colorStr.substring(1, 3), 16),
                    Integer.valueOf(colorStr.substring(3, 5), 16),
                    Integer.valueOf(colorStr.substring(5, 7), 16)
            );
        }
    }
}
