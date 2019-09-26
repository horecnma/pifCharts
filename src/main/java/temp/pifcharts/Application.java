package temp.pifcharts;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import temp.pifcharts.dto.Data;

/**
 * @author Mikhail
 */
public class Application {
    private static DataDownloader dataDownloader = new DataDownloader();
    private static DatasetProvider datasetProvider = new DatasetProvider();

    public static void main(String[] args)
            throws Exception {
        JWindow splash = createSplash();
        try {
            List<Data> data = dataDownloader.getData(LocalDate.now().minusYears(5));

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
            boolean selected, List<Data> data, LegendWithoutSeriesXYPlot plot, String title, LocalDate localDate, ButtonGroup bg
    ) {
        JRadioButton button = new JRadioButton(new AbstractAction(title) {
            @Override
            public void actionPerformed(ActionEvent e) {
                plot.setDataset(datasetProvider.createDataset(data, localDate));
                plot.updateDomainMarkers(localDate);
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
