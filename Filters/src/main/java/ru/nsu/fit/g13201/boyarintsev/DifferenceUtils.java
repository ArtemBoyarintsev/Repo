package ru.nsu.fit.g13201.boyarintsev;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;


public class DifferenceUtils {
    final private static Dimension edgeSelectionDimension = new Dimension(250, 130);

    public static void createEdgeSelectionDialog(JLabel label, final JTextField textField, final JSlider slider, final OkCancelPressed selectionAlgorithm)
    {
        final JDialog jFrame = new JDialog();
        jFrame.setLocation(200, 200); //TODO: FIX IT

        jFrame.setPreferredSize(edgeSelectionDimension);
        jFrame.setMinimumSize(edgeSelectionDimension);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        ControllerUtils.settingSize(label,new Point(10,0));
        ControllerUtils.settingSize(textField,new Point(10,label.getY() + label.getHeight() + 3));
        ControllerUtils.settingSize(slider,new Point(10,textField.getY() + textField.getHeight() +3));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider sl = (JSlider)e.getSource();
                Integer value = sl.getValue();
                textField.setText(value.toString());
            }
        });
        textField.setFocusable(false);
        panel.add(label);
        panel.add(textField);
        panel.add(slider);
        ControllerUtils.addButtons(slider,selectionAlgorithm,jFrame,panel);
        jFrame.setContentPane(panel);
        jFrame.setVisible(true);
    }
}
