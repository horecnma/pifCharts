package temp.pifcharts.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * @author Mikhail
 */

public class FrameMonitor {

    public void registerFrame(JFrame frame, String frameUniqueId, int defaultW, int defaultH) {
        Preferences prefs = Preferences.userRoot()
                                       .node(FrameMonitor.class.getSimpleName() + "-" + frameUniqueId);

        Optional<Point> frameLocation = getFrameLocation(prefs);
        if (isFullScreen(prefs)) {
            frame.setSize(defaultW, defaultH);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            frame.setLocationRelativeTo(null);
        } else {
            if (frameLocation.isPresent()) {
                frame.setLocation(frameLocation.get());
            } else {
                frame.setLocationRelativeTo(null);
            }
            frame.setSize(getFrameSize(prefs, defaultW, defaultH));
        }

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

    private void updatePref(JFrame frame, Preferences prefs) {
        // todo use EDT
        prefs.putInt("x", frame.getLocation().x);
        prefs.putInt("y", frame.getLocation().y);
        prefs.putInt("w", frame.getSize().width);
        prefs.putInt("h", frame.getSize().height);
        prefs.putInt("extendedState", frame.getExtendedState());
    }

    private Dimension getFrameSize(Preferences pref, int defaultW, int defaultH) {
        int w = pref.getInt("w", defaultW);
        int h = pref.getInt("h", defaultH);
        return new Dimension(w, h);
    }

    private boolean isFullScreen(Preferences pref) {
        return pref.getInt("extendedState", 0) == Frame.MAXIMIZED_BOTH;
    }

    private Optional<Point> getFrameLocation(Preferences pref) {
        int x = pref.getInt("x", 0);
        int y = pref.getInt("y", 0);
        if (x == 0 && y == 0) {
            return Optional.empty();
        }
        return Optional.of(new Point(x, y));
    }
}
