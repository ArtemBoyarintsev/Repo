package ru.nsu.fit.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Артем on 24.03.2016.
 */
public class LegendZone extends JPanel {
    private int[] colors;
    private double min;
    private double max;
    private boolean interpol = false;
    private int noDots;
    private Dimension size;

    LegendZone(Dimension zoneSize,int[] colors,double min,double max,int noDots)
    {
        size = zoneSize;
        this.colors = colors;
        this.min = min;
        this.max = max;
        this.noDots = noDots;
        setPaneSize(zoneSize);
    }
    public void setPaneSize(Dimension d)
    {
        this.size = d;
        setSize(d);
        repaint();
    }
    public void setInterpol(boolean value)
    {
        interpol=value;
        repaint();
    }
    public void setLegendConfig(int[] colors,double  min,double max,int noDots,boolean value)
    {
        this.colors = colors;
        this.noDots = noDots;
        this.interpol = value;
        this.min = min;
        this.max = max;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Color oldColor = g.getColor();
        int width = size.width;
        int height = size.height;
        int bandWidth = 3*width/(4);
        int bandHeight = 3*height/(4*colors.length);
        int colorCount = colors.length;
        Point legendStart = new Point(width/4,height/8); // рисуем после первой четверти зоны
        double step = (max - min)/(noDots+1);
        for(int i = 0 ; i < colorCount; ++i)
        {
            Point point = new Point(legendStart.x,legendStart.y + bandHeight*i);
            String str = "Z"+i+"="+((Integer) ((int) (min + step * i))).toString();
            g.setColor(oldColor);
            g.drawString(str, point.x - 50, point.y);
            if (!interpol||i==colorCount-1)
            {
                Color c = new Color(colors[i]);
                g.setColor(c);
                g.fillRect(point.x, point.y, bandWidth,bandHeight );
                continue;
            }
            for ( int  k = 0 ; k < bandHeight;k+=1)
            {
                Color c1 = new Color(colors[i]);
                Color c2 = new Color(colors[i+1]);
                int red = c1.getRed()*(bandHeight-k) + c2.getRed()*k;
                int green = c1.getGreen()*(bandHeight-k) + c2.getGreen()*k;
                int blue = c1.getBlue()*(bandHeight-k) + c2.getBlue()*k;
                red/=bandHeight;
                green/=bandHeight;
                blue/=bandHeight;
                if (red > 255)
                    red = 255;
                if (red < 0)
                    red = 0;
                if (blue > 255)
                    blue = 255;
                if (blue < 0)
                    blue = 0;
                if (green > 255)
                    green = 255;
                if (green < 0)
                    green = 0;
                g.setColor(new Color(red,green,blue));
                g.fillRect(point.x, point.y+k, bandWidth,1 );g.fillRect(point.x, point.y+k, bandWidth,1 );
            }
        }
        g.setColor(oldColor);
    }
}
