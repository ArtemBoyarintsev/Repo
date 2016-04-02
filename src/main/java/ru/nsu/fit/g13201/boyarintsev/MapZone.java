package ru.nsu.fit.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Артем on 24.03.2016.
 */
public class MapZone extends JPanel{
    private Dimension paneSize;
    private Field field;
    private Image image = null;

    MapZone(Field field, Dimension paneSize)
    {
        this.field = field;
        setPaneSize(paneSize);
    }

    public void setField(Field field)
    {
        this.field = field;
        image = makeImage(field);
        repaint();
    }
    public void setPaneSize(Dimension d)
    {
        paneSize = d;
        setSize(paneSize);
        this.image = makeImage(field);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(image,g);
    }

    private Image makeImage(Field field)
    {
        if (field == null)
        {
            return null;
        }
        int[][] ar = field.getBitMap();
        BufferedImage image = new BufferedImage(field.getWindowWidth(),field.getWindowHeight(),BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i <  ar.length && i < field.getWindowWidth(); i ++)
        {
            for(int j = 0; j <  ar[i].length && j < field.getWindowHeight(); j ++)
            {
                image.setRGB(i,j,ar[i][j]);
            }
        }
        return image;
    }

    private void draw(Image image,Graphics g)
    {
        if (null != image) {
            g.drawImage(image, 1, 1, null);
        }
    }
}
