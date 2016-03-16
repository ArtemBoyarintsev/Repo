package ru.nsu.fit.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class ImagePanel extends JPanel {
    private boolean shouldBeCompressed;
    private int pixelSize = 1;
    private Dimension paneSize;
    private Dimension imageZoneSize;
    private Image image = null;
    private Field field = null;
    private int[] scaledCof;
    private Point start;
    private Point finish;
    private boolean needToDrawSquare = false;
    private boolean wasChanged = false;

    ImagePanel(Dimension paneSize,boolean shouldBeCompressed)
    {
        this.paneSize = paneSize;
        this.imageZoneSize = new Dimension(paneSize.width-2,paneSize.height-2);
        this.shouldBeCompressed = shouldBeCompressed;
        this.scaledCof = new int[]{1,1};
        setPreferredSize(paneSize);
        setMaximumSize(paneSize);
        setMinimumSize(paneSize);
        setSize(paneSize);
    }
    public Dimension getImageZoneSize()
    {
        return imageZoneSize;
    }

    public void setField(Field field)
    {
        setField(field,1);
    }

    public final int[] getScaledCof()
    {
        return scaledCof;
    }

    public void setField(Field field,int pixelSize)
    {
        this.field = field;
        this.pixelSize = pixelSize;
        image = makeImage(field,shouldBeCompressed);
        needToDrawSquare = false;
        if (field!=null)
            wasChanged = true;
        else
            wasChanged = false;
    }
    public boolean wasChanged()
    {
        return wasChanged;
    }
    public void flushChanged() {wasChanged = false; }
    public Field getField()
    {
        return field;
    }

    public void clearSquare()
    {
        needToDrawSquare = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(image,paneSize,g);
    }

    private Image makeImage(Field field,boolean shouldBeCompressed)
    {
        if (field == null)
        {
            return null;
        }
        int[][] ar = field.getBitMap();
        BufferedImage image = new BufferedImage(pixelSize*field.getWidth(),pixelSize*field.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
        for (int i =0 ; i <  ar.length; i += 1)
        {
            for(int j = 0; j <  ar[i].length; j += 1)
            {
                image.setRGB(i,j,ar[i][j]);
            }
        }
        if (shouldBeCompressed) {
            return compressImage(image);
        }
        return image;
    }

    private Image compressImage(BufferedImage image)
    {
        if (image.getHeight() > imageZoneSize.height  || image.getWidth() > imageZoneSize.width ) {
            float k1 = ((float) imageZoneSize.width) / image.getWidth();
            float k2 = ((float) imageZoneSize.height) / (image.getHeight());
            if (k1 < k2) {
                scaledCof[0] = imageZoneSize.width;
                scaledCof[1] = image.getWidth();
            } else {
                scaledCof[0]= imageZoneSize.height;
                scaledCof[1] = image.getHeight();
            }
            return (image.getScaledInstance(image.getWidth()*scaledCof[0]/scaledCof[1], image.getHeight() * (scaledCof[0]) /scaledCof[1], Image.SCALE_SMOOTH));
        }
        scaledCof[0] = 1;
        scaledCof[1] = 1;
        return image;
    }
    public void addSquare(Point start,Point finish)
    {
        needToDrawSquare = true;
        this.start = start;
        this.finish = finish;
        repaint();
    }
    private void drawSquare(Graphics2D g,Point start,Point end,Stroke pen)
    {
        Stroke old = g.getStroke();
        g.setStroke(pen);
        g.drawLine(start.x,start.y,end.x,start.y);
        g.drawLine(end.x,start.y,end.x,end.y);
        g.drawLine(end.x,end.y,start.x,end.y);
        g.drawLine(start.x,end.y,start.x,start.y);
        g.setStroke(old);
    }
    private void draw(Image image,Dimension paneSize,Graphics g)
    {
        float[] dashl = {4,4};
        BasicStroke pen = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL,10,dashl,0);
        drawSquare((Graphics2D)g,new Point(0,0),new Point(paneSize.width - 1, paneSize.height - 1),pen);
        if (null != image) {
            g.drawImage(image, 1, 1, null);
        }
        if (needToDrawSquare)
        {
            drawSquare((Graphics2D)g,start,finish,pen);
        }
    }
}
