package ru.nsu.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Created by Артем on 15.04.2016.
 */
public class SplinePanel extends JPanel implements Observer
{
    SplineField splineField;
    BufferedImage image;
    Dimension lastCoordinates;
    Dimension paneSize;
    double xMin = 0.0;
    double xMax = 0.0;
    double yMin = 0.0;
    double yMax = 0.0;
    int scale = 1;
    public double getCharacterMainDotsSize()
    {
        return 10.0/paneSize.width;
    }

    public SplinePanel(Dimension ps,SplineField field)
    {
        this.paneSize = new Dimension(ps.width-16,ps.height);
        this.lastCoordinates = new Dimension(paneSize.width - 1,paneSize.height - 1);
        this.splineField = field;

        SplineFieldDrawParams fieldDrawParams = splineField.getDrawParams();
        xMin = fieldDrawParams.xMin;
        xMax = fieldDrawParams.xMax;
        yMin = fieldDrawParams.yMin;
        yMax = fieldDrawParams.yMax;
        xMin = -Math.max(Math.abs(xMin),Math.abs(xMax))-1;
        xMax = -xMin;
        yMin = -Math.max(Math.abs(yMin),Math.abs(yMax))-1;
        yMax = -yMin;
        field.addObserver(this);
        image = buildImage();
    }
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        final int d = 8;
        g.setColor(Color.BLACK);
        g.fillRect(0,0,paneSize.width,paneSize.height);

        g.drawImage(image,0,0,null);
        g.setColor(Color.WHITE);
        for(int i = 0; i < d; ++i)
        {
            Double value = xMin + (i+1) * (xMax-xMin) / d;
            String str = value.toString();
            if (str.length()>4) {
                str = str.substring(0, 4);
            }
            g.drawString(str,(i+1)*lastCoordinates.width/d,lastCoordinates.height/2);
        }

        for (int j = 0; j < d; ++j)
        {
            Double value = yMin + (j+1)*(yMax -yMin)/d;
            String str = value.toString();
            if (str.length() > 4)
            {
                str = str.substring(0,4);
            }
            g.drawString(str,lastCoordinates.width/2,(d-(j+1))*lastCoordinates.height/d);
        }

        SplineFieldDrawParams fieldDrawParams = splineField.getDrawParams();
        for (DoublePoint doublePoint : fieldDrawParams.mainDots)
        {
            double x = doublePoint.x;
            double y = doublePoint.y;
            int xInWindowCoors = (int)((x -  xMin ) / ( xMax - xMin ) * lastCoordinates.width);
            int yInWindowCoors = (int)((y -  yMin) / ( yMax - yMin ) * lastCoordinates.height);
            g.drawOval(xInWindowCoors,lastCoordinates.height - yInWindowCoors,10,10);
        }

        g.setColor(Color.CYAN);
        g.fillRect(0,paneSize.height,paneSize.width,getHeight() - paneSize.height);
    }

    public void scaleDecrease()
    {
        //if (scale <= 16) {

        {  scale *= 2;
            xMin = 1.5 * xMin;
            xMax = -xMin;
            yMin = 1.5 * yMin;
            yMax = -yMin;
            image = buildImage();
            repaint();
        }
    }

    public void scaleIncrease()
    {
        {
            scale /= 2;
            xMin = 0.5 * xMin;
            xMax = -xMin;
            yMin = 0.5 * yMin;
            yMax = -yMin;
            image = buildImage();
            repaint();
        }
    }

    public void update(Observable o, Object arg)
    {
        SplineFieldDrawParams fieldDrawParams = splineField.getDrawParams();
        xMin = fieldDrawParams.xMin;
        xMax = fieldDrawParams.xMax;
        yMin = fieldDrawParams.yMin;
        yMax = fieldDrawParams.yMax;

        //делаем нуль симметричным OX, OY
        xMin = -Math.max(Math.abs(xMin),Math.abs(xMax)) - 1;
        xMax = -xMin;
        yMin = -Math.max(Math.abs(yMin),Math.abs(yMax)) - 1;
        yMax = -yMin;

        image = buildImage();
        repaint();
    }

    public DoublePoint getSplineCoorsByWindowsCoors(Point windowCoors)
    {
        int x = windowCoors.x;
        int y = windowCoors.y;
        int width = lastCoordinates.width ;
        int height = lastCoordinates.height;
        // int xInWindowCoors = (int)((x -  xMin ) / ( xMax - xMin ) * width);
        double xx = xMin + (double)x*((xMax - xMin)) / width;
        double yy = yMax - (double)y*((yMax - yMin)) / height;
        //int xInWindowCoors = lastCoordinates.width / 2 + (int)((x - (xMax-xMin)/2) / ( xMax - xMin ) * width);
        return new DoublePoint(xx,yy);
    }

    private void drawBlackFrame(BufferedImage image)
    {
        for (int i = 0 ; i <= lastCoordinates.width;++i)
        {
            image.setRGB(i,0,Color.WHITE.getRGB());
            image.setRGB(i,lastCoordinates.height,Color.BLACK.getRGB());
        }
        for (int j = 0; j <= lastCoordinates.height; ++j)
        {
            image.setRGB(0,j,Color.WHITE.getRGB());
            image.setRGB(lastCoordinates.width,j,Color.BLACK.getRGB());
        }
    }

    private void drawAxisOnImage(BufferedImage image)
    {
        Color constAxisColor = new Color(255,255,255);
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i = 0 ;i < width; ++i)
        {
            image.setRGB(i,height/2,constAxisColor.getRGB());
        }
        for (int i = 0 ;i < height; ++i)
        {
            image.setRGB(width/2,i,constAxisColor.getRGB());
        }
    }

    private BufferedImage buildImage()
    {
        BufferedImage ret = new BufferedImage(paneSize.width, paneSize.height,BufferedImage.TYPE_4BYTE_ABGR);
        drawAxisOnImage(ret);
        drawBlackFrame(ret);
        SplineFieldDrawParams fieldDrawParams = splineField.getDrawParams();
        Collection<DoublePoint> collection = fieldDrawParams.collectionOfValues;

        for (DoublePoint doublePoint : collection)
        {
            double x = doublePoint.x;
            double y = doublePoint.y;
            if (x > xMax || y > yMax || y < yMin || x < xMin )
                continue;
            int xInWindowCoors = (int)((x -  xMin ) / ( xMax - xMin ) * lastCoordinates.width);
            int yInWindowCoors = (int)((y -  yMin) / ( yMax - yMin ) * lastCoordinates.height);
            ret.setRGB(xInWindowCoors,lastCoordinates.height - yInWindowCoors,new Color(255, 255, 255).getRGB());
        }
        return ret;
    }
}
