package ru.nsu.fit.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerUtils {
    public static void settingSize(Component component,Point start)
    {
        Dimension sizeFstImpact = component.getPreferredSize();
        Point fstImpactPosition = new Point(start.x,start.y);
        component.setBounds(fstImpactPosition.x,fstImpactPosition.y,sizeFstImpact.width,sizeFstImpact.height);
    }
    public  static void addButtons(Component start, final OkCancelPressed okCancel, final JDialog jDialog, JPanel panel)
    {
        JButton okButton = new JButton("ok");
        JButton cancelButton = new JButton("cancel");
        okButton.setFocusable(false);
        cancelButton.setFocusable(false);
        settingSize(okButton,
                new Point(start.getX(),start.getY()+start.getHeight()+7));
        settingSize(cancelButton,new Point(okButton.getX() + okButton.getWidth()+3, okButton.getY()));

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okCancel.getIfOkPressed().run();jDialog.setVisible(false);
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
    }
    public static void createCancelDialog(OkCancelPressed okCancel, String name, JComponent[] components)
    {
        final JDialog jDialog = new JDialog();
        JPanel panel = new JPanel();
        panel.setLayout(null);

        jDialog.setTitle(name);
        jDialog.setLocation(200,200);
        jDialog.setSize(new Dimension(250,200));

        settingSize(components[0],new Point(10,10));
        panel.add(components[0]);

        for (int i = 1 ;i < components.length;++i)
        {
            settingSize(components[i], new Point(components[i-1].getX(), components[i-1].getY() + components[i-1].getHeight() + 5));
            panel.add(components[i]);
        }

        addButtons(components[components.length-1],okCancel,jDialog,panel);

        jDialog.setContentPane(panel);
        jDialog.setVisible(true);
    }
    public static int getIntValue(String value, int min, int max)
    {
        try
        {
            int ret = Integer.parseInt(value);
            if (ret<min||ret>max)
            {
                throw new NumberFormatException();
            }
            return ret;
        }
        catch(NumberFormatException e)
        {
            String v = "Значение должно находится от "+min+" ... "+max+"\n";
            JOptionPane.showMessageDialog(null, v);
            return min-1;
        }
    }
    public static int getAbsoluteValue(int color)
    {
        Color c = new Color(color);
        return Integer.max(Integer.max(c.getBlue(),c.getGreen()),c.getRed());
    }

    public  static void kernelHandle(Field field,int[][] bitmapOfConverted,double[][] kernel,int i,int j)
    {
        int kernelWidth = kernel.length;
        int kernelHeight = kernel[0].length;
        int width = field.getWidth();
        int height = field.getHeight();
        int[][] bitmap = field.getBitMap();
        double rSum = 0, gSum = 0, bSum = 0, kSum = 0;
        for (int x = 0 ; x < kernelWidth;++x) {
            for (int y = 0; y < kernelHeight; ++y) {
                int pixelPosX = i + (x - (kernelWidth / 2));
                int pixelPosY = j + (y - (kernelHeight / 2));
                if ((pixelPosX < 0) ||
                        (pixelPosX >= width) ||
                        (pixelPosY < 0) ||
                        (pixelPosY >= height)) continue;

                int red = new Color(bitmap[pixelPosX][pixelPosY]).getRed();
                int green = new Color(bitmap[pixelPosX][pixelPosY]).getGreen();
                int blue = new Color(bitmap[pixelPosX][pixelPosY]).getBlue();
                double kernelVal = kernel[x][y];
                rSum += red * kernelVal;
                gSum += green * kernelVal;
                bSum += blue * kernelVal;

                kSum += kernelVal;
            }
        }

        if (kSum <= 0) kSum = 1;

        //Контролируем переполнения переменных
        rSum /= kSum;
        if (rSum < 0) rSum = 0;
        if (rSum > 255) rSum = 255;

        gSum /= kSum;
        if (gSum < 0) gSum = 0;
        if (gSum > 255) gSum = 255;

        bSum /= kSum;
        if (bSum < 0) bSum = 0;
        if (bSum > 255) bSum = 255;

        bitmapOfConverted[i][j]=new Color((int)rSum,(int)gSum,(int)bSum).getRGB();

    }
    public static double getDoubleValue(String value,int min,int max)
    {
        try
        {
            double ret = Double.parseDouble(value);
            if (ret < min || ret > max)
            {
                throw new NumberFormatException();
            }
            return ret;
        }
        catch(NumberFormatException e)
        {
            String v = "Значение должно находится от "+min+" "+max+"\n";
            JOptionPane.showMessageDialog(null, v);
            return min-1;
        }
    }
}
