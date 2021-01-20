package temp.pifcharts;

import org.jfree.data.time.TimeSeries;
import temp.pifcharts.dto.PifSeries;
import temp.pifcharts.gui.DatasetProvider;
import temp.pifcharts.gui.FrameMonitor;
import temp.pifcharts.gui.LegendWithoutSeriesXYPlot;
import temp.pifcharts.gui.PifsChartPanel;
import temp.pifcharts.services.DataDownloader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Mikhail
 */
public class Application {
    private static final DataDownloader dataDownloader = new DataDownloader();
    private static final DatasetProvider datasetProvider = new DatasetProvider();
    private static final FrameMonitor frameMonitor = new FrameMonitor();

    public static void main(String[] args)
            throws Exception {
        JWindow splash = createSplash();
        try {
            LocalDate minDate = LocalDate.now().minusYears(15);
            List<PifSeries> data = dataDownloader.getData(minDate);

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
            buttonPanel.add(newButton(false, data, plot, "6 лет", LocalDate.now().minusYears(6), bg), bg);
            buttonPanel.add(newButton(false, data, plot, "10 лет", LocalDate.now().minusYears(10), bg), bg);
            buttonPanel.add(newButton(false, data, plot, "15 лет", LocalDate.now().minusYears(15), bg), bg);
            panel.add(buttonPanel, BorderLayout.NORTH);
            panel.add(chart);
            frame.setContentPane(panel);
            frameMonitor.registerFrame(frame, "test_pif_chart", 700, 500);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
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
            boolean selected, List<PifSeries> data, LegendWithoutSeriesXYPlot plot, String title, LocalDate localDate, ButtonGroup bg
    ) {
        JRadioButton button = new JRadioButton(new AbstractAction(title) {
            @Override
            public void actionPerformed(ActionEvent e) {
                plot.setDataset(datasetProvider.createDataset(data, localDate));
                plot.updateDomainMarkers(localDate);
                colorLines(plot, data);
            }

            private void colorLines(LegendWithoutSeriesXYPlot plot, List<PifSeries> data) {
                for (int i = 0; i < plot.getSeriesCount(); i++) {
                    TimeSeries series = plot.getDataset().getSeries(i);
                    Optional<Color> color = data.stream()
                                                .filter(it -> it.getData().getName().equals(series.getKey()))
                                                .findFirst().map(PifSeries::getColor);
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
