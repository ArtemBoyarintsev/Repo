package ru.nsu.graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by Артем on 11.02.2016.
 */
public class FieldPanel extends JPanel {

    private static double twoCosAngle = 1.5;
    Field field;
    JLabel stateLabel;

    BufferedImage deadCage = null;
    BufferedImage liveCage = null;

    int lineWidth;
    int lineLength;
    boolean impactShow;
    private void drawHex(BufferedImage hex,int color)
    {
        drawFirstLine(hex,color);
        drawSecondLine(hex,color);
        drawThirdLine(hex,color);
        drawForthLine(hex,color);
        drawFifthLine(hex,color);
        drawSixthLine(hex,color);
    }

    private void cagesInit()
    {
        deadCage = new BufferedImage(1 + (int)(twoCosAngle *lineLength),1+2*lineLength,BufferedImage.TYPE_4BYTE_ABGR);
        liveCage = new BufferedImage(1 + (int)(twoCosAngle *lineLength),1+2*lineLength,BufferedImage.TYPE_4BYTE_ABGR);
        int borderColor = Color.BLACK.getRGB();
        int fillColor = Color.GREEN.getRGB();
        drawHex(deadCage,borderColor);
        drawHex(liveCage,borderColor);
        fillCage(liveCage,fillColor,borderColor);
        fillCage(deadCage,Color.PINK.getRGB(),borderColor);
        evenXStart = 0;
        notEvenXStart = (int)(twoCosAngle *lineLength)/2  - (lineWidth)/2;
        xOffset = deadCage.getWidth() - lineWidth;
        yOffset = ((3*deadCage.getHeight()/4)) - lineWidth/2 -(lineWidth%2);

    }

    public FieldPanel(Field field,int lineLength,int lineWidth) {
        super();
        this.field = field;
        this.lineLength = lineLength;
        this.lineWidth = lineWidth;
        this.impactShow = false;
        this.stateLabel = new JLabel();
        cagesInit();
        setLayout(new BorderLayout());
        add(stateLabel,BorderLayout.SOUTH);
    }

    public void impactShow(boolean b)
    {
        impactShow = b;
    }

    public void setField(Field field)
    {
        this.field = field;
    }

    int evenXStart = 0;
    int notEvenXStart = 0;
    int xOffset = 0;
    int yOffset = 0;
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Field.Cage[][] cages = field.getCages();
        setPreferredSize(new Dimension((int)((double)cages[0].length * twoCosAngle *(double) lineLength),
                                        3*cages.length * lineLength/2+lineLength/2));

        if (null == cages)
        {
            return;
        }

        int startOffsetOnX ;
        int startOffsetOnY = 0;

        for (int i = 0; i < cages.length; ++i)
        {
            startOffsetOnX = (i%2 == 0 ? evenXStart: notEvenXStart);
            int k = i%2;

            for (int j = 0; j < cages[i].length - k; ++j )
            {
                BufferedImage image = (cages[i][j].alive ? liveCage:deadCage);
                g.drawImage(image,startOffsetOnX,startOffsetOnY,null);
                if (impactShow && lineLength>= 30)
                {
                    float impact = cages[i][j].getImpact();
                    writeNumber(g,startOffsetOnX,startOffsetOnY,impact);
                }
                startOffsetOnX += xOffset;
            }
            startOffsetOnY += yOffset;
        }
    }
    private void writeNumber(Graphics g, int startOnX, int startOnY, float impact)  {
        Font oldFont = g.getFont();
        Font newFont = new Font("Verdana",Font.PLAIN, 11);
        g.setFont(newFont);
        startOnY += lineLength + newFont.getSize()/2;
        startOnX += (int)(twoCosAngle *lineLength/2)-  newFont.getSize();
        String str = ((Float)impact).toString();
        int i = 0;
        while(str.charAt(i++) != '.');
        if (str.charAt(i)=='0')
        {
            i-=2;
        }
        g.drawString(str.substring(0,i+1),startOnX,startOnY);
        g.setFont(oldFont);
    }

    private boolean isOnSecondLine(Point p)
    {
        int y = (int)(-1.0/ twoCosAngle * p.x) + lineLength/2 +(lineWidth-1);
        return y >= p.y;
    }
    private void drawSecondLine(BufferedImage g,int color)  {
        int x1 = 0;
        int y1 =  lineLength/2;
        int x2 = (int)(lineLength * twoCosAngle /2);
        int y2 = 0;
        int i = 0;
        while(i < lineWidth) {
            drawLine(g, x1, y1+i, x2, y2+i,color); ++i;
        }
    }

    private boolean isOnThirdLine(Point p)
    {
        int y = (int)(1.0/ twoCosAngle * p.x) - lineLength/2 +(lineWidth-1);
        return (y >= p.y);
    }

    private void drawThirdLine(BufferedImage g,int color) {
        int y1 = 0;
        int x1 =  (int)(lineLength * twoCosAngle /2);
        int y2 = lineLength/2;
        int x2 = (int)(lineLength * twoCosAngle);
        int i = 0;
        while(i < lineWidth) {
            drawLine(g, x1, y1+i, x2, y2+i,color); ++i;
        }
    }

    private boolean isOnForthLine(Point p)
    {
        return (p.x >= (int)(lineLength * twoCosAngle) - (lineWidth-1) && p.y >= lineLength/2 &&p.y<=lineLength/2+lineLength);
    }
    private void drawForthLine(BufferedImage g,int color) {
        int x1 =  (int)(lineLength * twoCosAngle);
        int y1 =  lineLength/2;
        int x2 = x1;
        int y2= y1 + lineLength;
        int i = 0;
        while(i < lineWidth) {
            drawLine(g, x1-i, y1, x2-i, y2,color);
            ++i;
        }
    }
    private boolean isOnFifthLine(Point p)
    {
        int y = (int)(-1.0/ twoCosAngle * p.x) + 5*lineLength/2 -(lineWidth-1);
        return (y <= p.y);
    }
    private void drawFifthLine(BufferedImage g,int color) {
        int x1 = (int)(lineLength * twoCosAngle);
        int y1 = lineLength/2 + lineLength;
        int x2 = (int)(lineLength * twoCosAngle /2);
        int y2 = lineLength/2 + lineLength + lineLength/2;
        int i = 0;
        while(i < lineWidth) {
            drawLine(g, x1, y1-i, x2, y2-i,color);
            ++i;
        }
    }

    private boolean isOnSixthLine(Point p)
    {
        int y = (int)(1.0/ twoCosAngle * p.x) + 3*lineLength/2 -(lineWidth-1);
        return (y <= p.y);
    }
    private void drawSixthLine(BufferedImage g, int color) {
        int x1 = (int)(lineLength * twoCosAngle /2);
        int y1 = lineLength/2 + lineLength + lineLength/2;
        int x2 = 0;
        int y2 =  lineLength/2 + lineLength;
        int i = 0;
        while(i < lineWidth) {
            drawLine(g, x1, y1-i, x2, y2-i,color); ++i;
        }
    }
    private boolean isOnFirstLine(Point p)
    {
        return (p.x <=lineWidth-1 && p.y >= lineLength/2 &&p.y<=lineLength/2+lineLength);
    }
    private void drawFirstLine(BufferedImage g, int color) {
        int x1 = 0;
        int y1 = lineLength/2 + lineLength;
        int x2 = 0;
        int y2 =  lineLength/2;
        int i = 0;
        while(i < lineWidth) {
            drawLine(g, x1+i, y1, x2+i, y2,color); ++i;
        }
    }

    private void drawLine(BufferedImage g,int x1,int y1, int x2, int y2,int color)
    {
        int deltaX = Math.abs(x2 - x1);
        int deltaY = Math.abs(y2 - y1);
        int stepToX = x1 < x2 ? 1 : -1;
        int stepToY = y1 < y2 ? 1 : -1;
        int error = deltaX -deltaY;
        int y = y1;
        int x = x1;
        g.setRGB(x2,y2,color);
        while( x!=x2||y!=y2)
        {
            g.setRGB(x,y,color);
            if (2*error < deltaX)
            {
                y+=stepToY;
                error+=deltaX;
            }
            if (2*error > -deltaY)
            {
                x+=stepToX;
                error-=deltaY;
            }
        }
    }
    class Span
    {
        public int xStart,xEnd,y;
    }
    private void findBorders(BufferedImage image,int borderColor,Span span,int y)
    {
        boolean flag =false;
        int i=0;
        while(i<image.getWidth()&&(image.getRGB(i,y) == borderColor||!flag))
        {
            if (image.getRGB(i,y)==borderColor)
            {
                flag = true;
            }
            ++i;
        }
        span.xStart = i-1;
        for (; i<image.getWidth()&&image.getRGB(i,y) != borderColor;++i)
        {
            span.xEnd = i+1;
        }
        if (i==image.getWidth())
        {
            span.xEnd=span.xStart-1;
        }
    }
    private void fillSpan(BufferedImage image,Span span,int color)
    {
        for (int i=span.xStart+1;i<span.xEnd;++i)
        {
            image.setRGB(i,span.y,color);
        }
    }
    private Span getSpanBySeed(BufferedImage image,Point seed,int borderColor)
    {
        Span ret =new Span();
        findBorders(image,borderColor,ret,seed.y);
        ret.y = seed.y;
        return ret;
    }
    private int getRightBorder(BufferedImage image,int x,int y,int borderColor)
    {
        while(x<image.getWidth()  && image.getRGB(x,y)!=borderColor)
        {
            x++;
        }
        return x;
    }
    private int getLeftBorder(BufferedImage image,int x,int y,int borderColor)
    {
        while(x>= 0 && image.getRGB(x,y)!=borderColor)
        {
            x--;
        }
        return x;
    }
    private void addSpans(BufferedImage image,Stack<Span> spanList,Span current,int borderColor,int color)
    {
        for(int y = current.y-1;y<=current.y+1;y+=2) {

            if (y < 0 || y >= image.getHeight())
            {
                continue;
            }
            for (int x = current.xStart; x < current.xEnd; ++x) {
                if (image.getRGB(x, y) != color&&image.getRGB(x,y)!=borderColor) {
                    Span span = new Span();
                    span.y = y;
                    span.xStart = getLeftBorder(image,x,y,borderColor);
                    if (span.xStart == -1)
                    {
                        continue;
                    }
                    x = span.xEnd = getRightBorder(image, x, y, borderColor);
                    spanList.add(span);
                }
            }
        }
    }
    private void fillCage(BufferedImage image,int color,int borderColor)
    {
        Point seed = new Point(image.getWidth()/2,image.getHeight()/2);
        Stack<Span> spanList = new Stack<Span>();
        Span span = getSpanBySeed(image,seed,borderColor);
        spanList.push(span);

        while(spanList.size()!=0)
        {
            Span current = spanList.pop();
            fillSpan(image,current,color);
            addSpans(image,spanList,current,borderColor,color);
        }
    }

    public int getLineWidth()
    {
        return lineWidth;
    }

    public int getLineLength()
    {
        return lineLength;
    }

    public void setState(String state)
    {
        stateLabel.setText(state);
    }

    private boolean onLine(Point pos,int  i,int j)
    {
        int k = (i%2 == 0 ? evenXStart: notEvenXStart);
        int x = (pos.x - j *xOffset-k) % deadCage.getWidth();

        int y = (pos.y-i*(yOffset))% deadCage.getHeight();
        if (isOnFirstLine(new Point(x,y)))
        {
            return true;
        }
        if (isOnSecondLine(new Point(x,y)))
        {
            return true;
        }
        if (isOnThirdLine(new Point(x,y)))
        {
            return true;
        }
        if (isOnForthLine(new Point(x,y)))
        {
            return true;
        }
        if (isOnFifthLine(new Point(x,y)))
        {
            return true;
        }
        if (isOnSixthLine(new Point(x,y)))
        {
            return true;
        }
        return false;
    }
    public Point findPossiblesButton(ArrayList<Point> possibleButtons,Point pos)
    {
        int buttonStartYFirst;
        int buttonEndYFirst;
        int buttonEndYSecond;
        int buttonStartYSecond;
        int buttonStartXFirst;
        int buttonEndXFirst;
        int buttonEndXSecond;
        int buttonStartXSecond;
        outer:for (  int i = 0; i < field.getHeight();i+=2)
        {
            buttonStartYFirst = (i)*yOffset;//3*lineLength/2- i*lineWidth;
            buttonEndYFirst = buttonStartYFirst + deadCage.getHeight();
            if (pos.y < buttonEndYFirst && buttonStartYFirst < pos.y)
            {
                for (int j = 0; j < field.getWidth();++j)
                {
                    buttonStartXFirst = j*deadCage.getWidth()- j*lineWidth;
                    int w = deadCage.getWidth();
                    buttonEndXFirst = buttonStartXFirst + w;
                    if (pos.x < buttonEndXFirst && buttonStartXFirst <= pos.x)
                    {
                        possibleButtons.add(new Point(i,j));
                        break outer;
                    }
                }
            }
        }
        outer:for (  int i = 1; i < field.getHeight(); i+=2)
        {
            buttonStartYSecond = (i)*(yOffset);
            buttonEndYSecond = buttonStartYSecond + deadCage.getHeight();
            if (pos.y < buttonEndYSecond && buttonStartYSecond < pos.y)
            {
                for (int j = 0; j < field.getWidth() -1 ;++j)
                {
                    buttonStartXSecond = notEvenXStart +j*(deadCage.getWidth() - lineWidth);
                    buttonEndXSecond = buttonStartXSecond + deadCage.getWidth();
                    if (pos.x < buttonEndXSecond && buttonStartXSecond <= pos.x)
                    {
                        possibleButtons.add(new Point(i,j));
                        break outer;
                    }
                }
            }
        }
        if (possibleButtons.size()==0)
        {
            return null;
        }
        if (!onLine(pos,possibleButtons.get(0).x,possibleButtons.get(0).y))
        {
            return possibleButtons.get(0);
        }

        if (possibleButtons.size() == 1)
        {
            return null;
        }
        if (!onLine(pos,possibleButtons.get(1).x,possibleButtons.get(1).y))
        {
            return possibleButtons.get(1);
        }
       return null;
    }
    public Point getCage(Point mousePos) {
        ArrayList<Point> possiblesCages = new ArrayList<Point>();
        return findPossiblesButton(possiblesCages, mousePos);
    }

    public void setLineLengthAndWidth(int lineLength,int lineWidth)
    {
        this.lineLength = lineLength;
        this.lineWidth = lineWidth;
        cagesInit();
    }
    public void setLineLength(int lineLength)
    {
        this.lineLength = lineLength;
        cagesInit();
        repaint();
    }
    public void setLineWidth(int value)
    {
        this.lineWidth = value;
        cagesInit();
        repaint();
    }
}