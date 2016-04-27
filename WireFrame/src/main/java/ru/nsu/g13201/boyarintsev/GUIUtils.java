package ru.nsu.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Created by Артем on 15.04.2016.
 */
public class GUIUtils
{
    public static void addButton(JComponent toolBar, JButton button, String hint, String name, ActionListener actionListener) {
        ImageIcon icon = createIcon(name);
        if (icon!=null)
        {
            button.setIcon(icon);
            button.setSize(icon.getIconWidth()+12,icon.getIconHeight()+12);
        }
        if (null != hint)
        {
            button.setToolTipText(hint);
        }
        button.addActionListener(actionListener);
        toolBar.add(button);
    }
    public static void addAdditionButton(JDialog dialog, JButton button, String name, Point position, ActionListener actionListener) {
        ImageIcon icon = createIcon(name);
        if (icon!=null)
        {
            button.setIcon(icon);
            button.setSize(icon.getIconWidth()+12,icon.getIconHeight()+12);
        }
        button.setLocation(position.x,position.y);
        button.addActionListener(actionListener);
        dialog.add(button);
    }
    protected static  ImageIcon createIcon(String path)
    {
        URL imgURL = GUI.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("File not found " + path);
            return null;
        }
    }

}
