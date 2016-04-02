package ru.nsu.fit.g13201.boyarintsev;


import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Артем on 24.03.2016.
 */

class Params
{
    int gridX;
    int gridY;

    int windowWidth;
    int windowHeight;

    int[] colors;
    int isoLinesColor;
}
class Function
{
    double xMin = 0;
    double yMin = 0;
    double xMax = 50;
    double yMax = 50;
    Function(double xMin, double xMax,double yMin,double yMax)
    {
        setMinMax(xMin,xMax,yMin,yMax);
    }
    public void setMinMax(double xMin,double xMax,double yMin,double yMax)
    {
        this.xMax = xMax;
        this.xMin = xMin;
        this.yMax = yMax;
        this.yMin = yMin;
    }

    public double getMax()
    {
        double max1 = xMax*yMax;
        double max2 = xMin*yMin;
        return Math.max(max1,max2);
    }
    public double getMin()
    {
        double min1 = xMax*yMin;
        double min2 = xMin*yMax;
        return Math.min(min1,min2);
    }

    public double function(double x, double y)
    {
        return x*y;
    }
}
public class Field {

    public Field(Params params) {
        function = new Function(xMin, xMax, yMin, yMax);

        this.noDots = params.colors.length - 1; // -1, as we have n+1 color
        this.colors = params.colors;
        this.isoLinesColor = params.isoLinesColor;
        this.funcMin = function.getMin();
        this.funcMax = function.getMax();
        this.xGrid = params.gridX;
        this.yGrid = params.gridY;
        this.interpol = false;
        setWindowWidthHeight(params.windowWidth,params.windowHeight);
    }

    public void setWindowWidthHeight(int windowWidth,int windowHeight)
    {
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
        this.stepX = (xMax - xMin) / windowWidth;
        this.stepY = (yMax - yMin) / windowHeight;
        bitMap = new int[windowWidth][windowHeight];
        expressColors();
        saveOriginal();
    }
    public void setInterpol(boolean f) {
        interpol = f;
        expressColors();
        saveOriginal();
    }

    public double getMin()
    {
        return function.getMin();
    }

    public double getMax()
    {
        return function.getMax();
    }

    public int getNoDots()
    {
        return noDots;
    }

    public int[] getColors()
    {
        return colors;
    }

    public int [][] getBitMap()
    {
        return bitMap;
    }

    public int getWindowWidth()
    {
        return windowWidth;
    }

    public int getWindowHeight()
    {
        return windowHeight;
    }

    public String getValuesByMousePos(Point p)
    {
        Double x = xMin+p.x*stepX;
        Double y=  yMin+p.y*stepY;
        Double value = function.function(x,y);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while(x.toString().charAt(i++) != '.');
        builder.append(x.toString().substring(0,i+1));
        builder.append(" | ");
        i = 0;
        while(y.toString().charAt(i++) != '.');
        builder.append(y.toString().substring(0,i+1));
        builder.append(" | ");
        i = 0;
        while(value.toString().charAt(i++) != '.');
        builder.append(value.toString().substring(0,i+1));
        return builder.toString();
    }

    public void setMinMax(double xMin,double xMax,double yMin,double yMax,int xGrid,int yGrid)
    {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.xGrid = xGrid;
        this.yGrid = yGrid;
        this.stepX = (xMax - xMin)/ windowWidth;
        this.stepY = (yMax-yMin)/ windowHeight;
        function.setMinMax(xMin,xMax,yMin,yMax);
        this.funcMin = function.getMin();
        this.funcMax = function.getMax();
        expressColors();
        saveOriginal();
    }
    public void setDrawGrid(boolean v) {
        gridDraw = v;
        expressColors();
        saveOriginal();
    }

    public void drawDefaultIsoLines(boolean v) {
        defaultIsoLinesDraw = v;
        expressColors();
        saveOriginal();
    }

    public void drawInRangeOf(double value)
    {
        int colorIndex = (int)((value - funcMin)/((funcMax - funcMin)/(noDots+1)));
        if (interpol)
        {
            return;
        }
        if (colorIndex < 0)
        {
            colorIndex = 0;
        }
        if (colorIndex >= noDots)
        {
           colorIndex = noDots - 1;
        }
        for(int i = 0; i < windowWidth; ++i)
        {
            for (int j = 0; j < windowHeight; ++j)
            {
                if (bitMap[i][j] != colors[colorIndex])
                {
                    bitMap[i][j] = Color.BLACK.getRGB();
                }
            }
        }
    }

    public boolean getInterpol()
    {
        return interpol;
    }

    public double getValueByMouse(Point p)
    {
        Double x = xMin+p.x*stepX;
        Double y=  yMin+p.y*stepY;
        Double value = function.function(x,y);
        return value;
    }
    public void restoreBitmap()
    {
        for (int i = 0; i < windowWidth; ++i)
        {
            System.arraycopy(savedBitmap[i],0,bitMap[i],0, windowHeight);
        }
    }
    public void buildExtraIsoLines(double value)
    {
        matchingCubesAlg(value);
    }

    private int interPolCase(double value,int colorIndex)
    {
        Color c1 = new Color(colors[colorIndex]);
        double value1= funcMin + colorIndex*(funcMax - funcMin)/(noDots+1);
        int red1 = c1.getRed();
        int green1 = c1.getGreen();
        int blue1 = c1.getBlue();
        Color c2 = new Color(colors[colorIndex+1]);
        double value2= funcMin+(colorIndex+1)*(funcMax - funcMin)/(noDots+1);
        int red2 = c2.getRed();
        int green2=c2.getGreen();
        int blue2 = c2.getBlue();
        int red = (int)((value-value1)*red2/(value2-value1) + (value2-value)*red1/(value2-value1));
        int green = (int)((value-value1)*green2/(value2-value1) + (value2-value)*green1/(value2-value1));
        int blue = (int)((value-value1)*blue2/(value2-value1) + (value2-value)*blue1/(value2-value1));
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
        return new Color(red,green,blue).getRGB();
    }
    private void expressColors()
    {
        for (int i = 0; i < windowWidth; ++i)
        {
            for(int j = 0; j < windowHeight; ++j)
            {
                double x = xMin+i*stepX;
                double y = yMin+j*stepY;
                double value = function.function(x,y);
                int colorIndex = (int)((value - funcMin)/((funcMax - funcMin)/(noDots+1)));
                //noDots+1, because count of segments is noDots+1.
                if (colorIndex < 0)
                {
                    bitMap[i][j] = colors[0];
                    continue;
                }
                if (colorIndex >= noDots)
                {
                    bitMap[i][j] = colors[colors.length-1];
                    continue;
                }
                if (!interpol)
                {
                    bitMap[i][j] = colors[colorIndex];
                    continue;
                }
                bitMap[i][j] = interPolCase(value,colorIndex);
            }
        }
        if (gridDraw)
        {
            gridDraw();
        }
        if (defaultIsoLinesDraw)
        {
            for ( int i = 1; i < noDots+1;++i)
            {
                matchingCubesAlg(funcMin + i *(funcMax-funcMin)/(noDots+1));
            }
        }
    }
    private void gridDraw()
    {
        int girdStepX = windowWidth /(xGrid-1); //шаг сетки
        int gridStepY = windowHeight /(yGrid-1);
        for (int i = girdStepX; i < windowWidth; i+=girdStepX)
        {
            for(int j = 0; j < windowHeight; ++j)
            {
                bitMap[i][j]=Color.BLACK.getRGB();
            }
        }
        for (int j = gridStepY; j < windowHeight; j+=gridStepY) {
            for(int i = 0; i< windowWidth; ++i)
            {
                bitMap[i][j]=Color.BLACK.getRGB();
            }
        }
    }
    private void saveOriginal()
    {
        savedBitmap = new int[windowWidth][windowHeight];
        for (int i = 0; i < windowWidth; ++i)
        {
            System.arraycopy(bitMap[i],0,savedBitmap[i],0, windowHeight);
        }
    }
    private int crossLine(double v1,double v2,double isoLinesLevel,int dl,int i ,int max)
    {
        int ret = -1;
        if (isoLinesLevel >= v1 && isoLinesLevel <= v2)
        {
            ret = (int)(i + dl * (isoLinesLevel - v1)/ (v2 - v1 ));
            if (ret >= max)
            {
                ret = max -1;
            }
            if (ret <=0)
            {
                ret = 0;
            }
            return ret;
        }
        if (isoLinesLevel <= v1 && isoLinesLevel >= v2)
        {
            ret = (int)(i + dl * (v1 - isoLinesLevel)/(v1-v2));
            if (ret >= max)
            {
                ret = max -1;
            }
            if (ret <=0)
            {
                ret = 0;
            }
            return ret;
        }
        return ret;
    }
    private void matchingCubesAlg(double isoLinesLevel)
    {
        int gridStepX = windowWidth /(xGrid-1); //шаг сетки
        int gridStepY = windowHeight /(yGrid-1);
        for (int i = 0; i < windowWidth; i+=gridStepX)
        {
            for(int j = 0; j < windowHeight; j+=gridStepY)
            {
                join(isoLinesLevel,i,j,gridStepX,gridStepY);
            }
        }
    }
    private boolean step(double isoLinesLevel,int i,int j,int dx,int dy,boolean drawAnywhere)
    {
        double f1 = function.function(xMin + i * stepX, yMin + j * stepY);
        double f2 = function.function(xMin + (i + dx) * stepX, yMin + j * stepY);
        double f3 = function.function(xMin + i * stepX, yMin + (j + dy) * stepY);
        double f4 = function.function(xMin + (i + dx) * stepX, yMin + (j + dy) * stepY);

        int d = j+dy -1 >= windowHeight ? windowHeight - 1: j + dy - 1;
        int r = i+dx-1 >= windowWidth ? windowWidth - 1: i+dx-1;
        Point up = new Point(crossLine(f1, f2, isoLinesLevel, dx,i, windowWidth), j);
        Point down = new Point(crossLine(f3, f4, isoLinesLevel, dx,i, windowWidth), d);
        Point left = new Point(i, crossLine(f1, f3, isoLinesLevel, dy,j, windowHeight));
        Point right = new Point(r, crossLine(f2, f4, isoLinesLevel, dy,j, windowHeight));

        ArrayList<Point> validPoints = getValidPoints(up, down, left, right);
        if (validPoints.size() == 0)
        {
            return  true;
        }
        if (validPoints.size() == 2)
        {
            Point first = validPoints.get(0);
            Point second = validPoints.get(1);
            drawLine(first.x, first.y, second.x, second.y, isoLinesColor);
            return true;
        }
        if (drawAnywhere && validPoints.size() == 4)
        {
            drawLine(up.x,up.y,left.x,left.y,isoLinesColor);
            drawLine(up.x,up.y,right.x,right.y,isoLinesColor);
            drawLine(right.x,right.y,down.x,down.y,isoLinesColor);
            drawLine(down.x,down.y,left.x,left.y,isoLinesColor);
            return true;
        }
        if (validPoints.size() == 3)
        {
            if (up.x != -1 && down.x!=-1)
            {
                Point p = left.y != -1 ? left : right;
                drawLine(up.x,up.y,p.x,p.y,isoLinesColor);
                drawLine(down.x,down.y,p.x,p.y,isoLinesColor);
                return true;
            }
            Point  p = up.x != -1 ? up : down;
            drawLine(right.x,right.y,p.x,p.y,isoLinesColor);
            drawLine(left.x,left.y,p.x,p.y,isoLinesColor);
            return true;
        }
        return false;
    }
    private void join(double isoLinesLevel,int i,int j,int gridStepX,int gridStepY)
    {
        double eps = 3.0;
        boolean value = step(isoLinesLevel,i,j,gridStepX,gridStepY,false);
        if (value)
        {
            return;
        }
        value = step(isoLinesLevel+eps,i,j,gridStepX,gridStepY,false);
        if (!value)
        {
            step(isoLinesLevel,i,j,gridStepX,gridStepY,true);
        }
    }
    private ArrayList<Point> getValidPoints(Point up,Point down,Point left,Point right)
    {
        ArrayList<Point> ret = new ArrayList<Point>();
        if (up.x!=-1)
        {
            ret.add(up);
        }
        if (left.y!=-1)
        {
            ret.add(left);
        }
        if (right.y != -1)
        {
            ret.add(right);
        }
        if (down.x!=-1)
        {
            ret.add(down);
        }
        return ret;
    }

    private int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
        //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
    }
    private void drawLine (int xstart, int ystart, int xend, int yend, int isoLinesColor)
    {
        int x, y, dx, dy, incx, incy, pdx, pdy, es, el, err;

        dx = xend - xstart;//проекция на ось икс
        dy = yend - ystart;//проекция на ось игрек

        incx = sign(dx);
        incy = sign(dy);

        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;

        if (dx > dy)
        {
            pdx = incx;	pdy = 0;
            es = dy;	el = dx;
        }
        else
        {
            pdx = 0;
            pdy = incy;
            es = dx;
            el = dy;
        }

        x = xstart;
        y = ystart;
        err = el/2;
        bitMap[x][y]=isoLinesColor;

        //все последующие точки возможно надо сдвигать, поэтому первую ставим вне цикла

        for (int t = 0; t < el; t++)//идём по всем точкам, начиная со второй и до последней
        {
            err -= es;
            if (err < 0)
            {
                err += el;
                x += incx;
                y += incy;
            }
            else
            {
                x += pdx;
                y += pdy;
            }
            bitMap[x][y]=isoLinesColor;
        }
    }


    private int windowWidth;
    private int windowHeight;
    private int[][] bitMap;
    private int[][] savedBitmap;

    private Function function;
    private  double xMin =0 ;
    private double yMin =0 ;
    private double xMax = 50;
    private double yMax = 50;
    private int xGrid = 10;
    private int yGrid = 10;
    private double stepX; //шаг с которым двигаемся от xMin до xMax.
    private double stepY;
    private double funcMin;
    private double funcMax;

    private int noDots;
    private int[] colors;
    private boolean interpol = false;
    private boolean gridDraw = false;
    private boolean defaultIsoLinesDraw = false;
    private int isoLinesColor;
}
