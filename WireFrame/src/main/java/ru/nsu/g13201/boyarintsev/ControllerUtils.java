package ru.nsu.g13201.boyarintsev;

/**
 * Created by Артем on 14.04.2016.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class OkCancelPressed
{
    //private AppController controller;
    private Runnable runnable;

    OkCancelPressed(Runnable runnable) {
        this.runnable = runnable;
     //   this.controller = controller;
    }
    public Runnable getIfOkPressed()
    {
        return runnable;
    }

    public void cancelPressed()
    {
        //TODO здесь что нужно сделать
    }
}

public class ControllerUtils {
    public static void settingSize(Component component, Point start)
    {
        Dimension sizeFstImpact = component.getPreferredSize();
        Point fstImpactPosition = new Point(start.x,start.y);
        component.setBounds(fstImpactPosition.x,fstImpactPosition.y,sizeFstImpact.width,sizeFstImpact.height);
    }
    public  static Point addButtons(Component start, final OkCancelPressed okCancel, final JDialog jDialog, Container panel)
    {
        JButton okButton = new JButton("ok");
        JButton cancelButton = new JButton("cancel");
        okButton.setFocusable(false);
        cancelButton.setFocusable(false);
        settingSize(okButton,
                new Point(start.getX()+start.getWidth() + 10,start.getY()));
        settingSize(cancelButton,new Point(okButton.getX(), okButton.getY()+okButton.getHeight()));

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okCancel.getIfOkPressed().run();
                jDialog.setVisible(false);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okCancel.cancelPressed();
                jDialog.setVisible(false);
            }
        });

        panel.add(okButton);
        panel.add(cancelButton);
        return new Point(cancelButton.getX(),cancelButton.getY() + cancelButton.getHeight());
    }
}
