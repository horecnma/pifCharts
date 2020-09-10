package temp.pifcharts;


import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.time.LocalDate;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import temp.pifcharts.dto.PifSeries;
import temp.pifcharts.services.DataDownloader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mikhail
 */
public class DataDownloaderTest {
    private final DataDownloader test = new DataDownloader();

    @BeforeClass
    public static void setUp() {
        URLStreamHandler stubUrlHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u)
                    throws IOException {
                URLConnection mockUrlCon = mock(URLConnection.class);
                when(mockUrlCon.getInputStream()).thenReturn(DataDownloaderTest.class.getClassLoader().getResourceAsStream("chartData.json"));
                return mockUrlCon;
            }
        };
        URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
        URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
        when(urlStreamHandlerFactory.createURLStreamHandler("https")).thenReturn(stubUrlHandler);
    }

    @Test
    public void shouldParseInputData()
            throws Exception {
        LocalDate dummyDate = LocalDate.now();
        List<PifSeries> data = test.getData(dummyDate);
        assert data.size() > 0;
        assert data.get(0).getData().getName().length() > 0;
    }
}
