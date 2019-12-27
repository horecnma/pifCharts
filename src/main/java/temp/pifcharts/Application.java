package temp.pifcharts;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.swing.*;

import org.apache.commons.io.FileUtils;
import org.jfree.data.time.TimeSeries;

import com.fasterxml.jackson.databind.ObjectMapper;

import temp.pifcharts.configs.ConfigService;
import temp.pifcharts.data.DataDownloader;
import temp.pifcharts.dto.ChartSeries;
import temp.pifcharts.gui.DatasetProvider;
import temp.pifcharts.gui.LegendWithoutSeriesXYPlot;
import temp.pifcharts.gui.PifsChartPanel;

/**
 * @author Mikhail
 */
public class Application {
    private static final ObjectMapper mapper = DataDownloader.createMapper();
    private static final DataDownloader dataDownloader = new DataDownloader(mapper, new ConfigService());
    private static final DatasetProvider datasetProvider = new DatasetProvider();


    public static void main(String[] args)
            throws Exception {
        JWindow splash = createSplash();
        try {
            List<ChartSeries<Long>> data = dataDownloader.getData(LocalDate.now().minusYears(5));
            List<ChartSeries<Double>> jsDataset = datasetProvider.createJsDataset(data, LocalDate.now().minusMonths(6));
            System.out.println(jsDataset);
//            String data1 = "var data = " + jsDataset ;
            String data1 = "var data = " + mapper.writeValueAsString(jsDataset) ;
            FileUtils.write(new File("/home/mnikolaev/work/tempProjects/pifCharts/src/main/resources/js/data.js"), data1);

            PifsChartPanel chart = new PifsChartPanel();
            LegendWithoutSeriesXYPlot plot = chart.getPlot();

            JFrame frame = new JFrame("Sberbank pifs");
            JPanel panel = new JPanel(new BorderLayout());

            JPanel buttonPanel = new JPanel();
            ButtonGroup bg = new ButtonGroup();
            buttonPanel.add(newButton(false, data, plot, "1 месяц", LocalDate.now().minusMonths(1), bg));
            buttonPanel.add(newButton(false, data, plot, "3 месяца", LocalDate.now().minusMonths(3), bg));
            buttonPanel.add(newButton(true, data, plot, "6 месяцев", LocalDate.now().minusMonths(6), bg), bg);
            buttonPanel.add(newButton(false, data, plot, "1 год", LocalDate.now().minusYears(1), bg));
            buttonPanel.add(newButton(false, data, plot, "3 года", LocalDate.now().minusYears(3), bg), bg);
            buttonPanel.add(newButton(false, data, plot, "5 лет", LocalDate.now().minusYears(5), bg), bg);
            panel.add(buttonPanel, BorderLayout.NORTH);
            panel.add(chart);
            frame.setContentPane(panel);
            frame.setSize(700, 500);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        } finally {
            splash.dispose();
        }
    }

    private static JWindow createSplash() {
        JWindow window = new JWindow();
        ImageIcon sadfsdf = new ImageIcon(Application.class.getClassLoader().getResource("icons/screen.png"));
        window.getContentPane().add(new JLabel("Ждите 10 сек", sadfsdf, SwingConstants.CENTER));
        window.setSize(800, 350);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        return window;
    }

    private static JRadioButton newButton(
            boolean selected, List<ChartSeries<Long>> data, LegendWithoutSeriesXYPlot plot, String title, LocalDate localDate, ButtonGroup bg
    ) {
        JRadioButton button = new JRadioButton(new AbstractAction(title) {
            @Override
            public void actionPerformed(ActionEvent e) {
                plot.setDataset(datasetProvider.createJavaDataset(data, localDate));
                plot.updateDomainMarkers(localDate);
                colorLines(plot, data);
            }

            private void colorLines(LegendWithoutSeriesXYPlot plot, List<ChartSeries<Long>> data) {
                for (int i = 0; i < plot.getSeriesCount(); i++) {
                    TimeSeries series = plot.getDataset().getSeries(i);
                    Optional<Color> color = data.stream()
                                                .filter(it -> it.getName().equals(series.getKey()))
                                                .findFirst().map(ChartSeries::getColor);
                    if (color.isPresent()) {
                        plot.getRenderer().setSeriesPaint(i, color.get());
                    }
                }
            }
        });
        if (selected) {
            button.doClick();
        }
        bg.add(button);

        return button;
    }

    public static Date toDate(LocalDate from) {
        return Date.from(from.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

}
