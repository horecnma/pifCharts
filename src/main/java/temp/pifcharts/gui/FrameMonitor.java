package temp.pifcharts.gui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Optional;
import java.util.prefs.Preferences;

import javax.swing.*;

/**
 * @author Mikhail
 */

public class FrameMonitor {

    public static void registerFrame(JFrame frame, String frameUniqueId, int defaultW, int defaultH) {
        Preferences prefs = Preferences.userRoot()
                                       .node(FrameMonitor.class.getSimpleName() + "-" + frameUniqueId);

        Optional<Point> frameLocation = getFrameLocation(prefs);
        if (frameLocation.isPresent()) {
            frame.setLocation(frameLocation.get());
        } else {
            frame.setLocationRelativeTo(null);
        }
        frame.setSize(getFrameSize(prefs, defaultW, defaultH));

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePref(frame, prefs);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                updatePref(frame, prefs);
            }
        });
    }

    private static void updatePref(JFrame frame, Preferences prefs) {
        // todo use EDT
        System.out.print("Updating preferences");
        Point location = frame.getLocation();
        prefs.putInt("x", location.x);
        prefs.putInt("y", location.y);
        System.out.printf("x=%d, y=%d ", location.x, location.y);
        Dimension size = frame.getSize();
        prefs.putInt("w", size.width);
        prefs.putInt("h", size.height);
        System.out.printf("w=%d, h=%d ", size.width, size.height);
    }

    private static Dimension getFrameSize(Preferences pref, int defaultW, int defaultH) {
        int w = pref.getInt("w", defaultW);
        int h = pref.getInt("h", defaultH);
        return new Dimension(w, h);
    }

    private static Optional<Point> getFrameLocation(Preferences pref) {
        int x = pref.getInt("x", 0);
        int y = pref.getInt("y", 0);
        if (x == 0 && y == 0) {
            return Optional.empty();
        }
        return Optional.of(new Point(x, y));
    }
}
