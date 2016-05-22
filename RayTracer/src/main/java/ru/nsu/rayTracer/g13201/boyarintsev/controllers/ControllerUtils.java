package ru.nsu.rayTracer.g13201.boyarintsev.controllers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class OkCancelPressed {
    private Runnable runnable;

    OkCancelPressed( Runnable runnable) {
        this.runnable = runnable;
    }
    public Runnable getIfOkPressed()
    {
        return runnable;
    }
}
public class ControllerUtils {
    public static void settingSize(Component component, Point start) {
        Dimension sizeFstImpact = component.getPreferredSize();
        Point fstImpactPosition = new Point(start.x, start.y);
        component.setBounds(fstImpactPosition.x, fstImpactPosition.y, sizeFstImpact.width, sizeFstImpact.height);
    }

    public static void addButtons(Component start, final OkCancelPressed okCancel, final JDialog jDialog, JPanel panel) {
        JButton okButton = new JButton("ok");
        JButton cancelButton = new JButton("cancel");
        okButton.setFocusable(false);
        cancelButton.setFocusable(false);
        settingSize(okButton,
                new Point(start.getX(), start.getY() + start.getHeight() + 7));
        settingSize(cancelButton, new Point(okButton.getX() + okButton.getWidth() + 3, okButton.getY()));

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okCancel.getIfOkPressed().run();
                jDialog.setVisible(false);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jDialog.setVisible(false);
            }
        });

        panel.add(okButton);
        panel.add(cancelButton);
    }

    public static String getDoubleString(Double value, int ap)
    {
        ap = ap > value.toString().length() ? value.toString().length() : ap;
        return value.toString().substring(0,ap);
    }
}
