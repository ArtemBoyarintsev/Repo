package ru.nsu.g13201.boyarintsev;

import java.util.*;

/**
 * Created by Артем on 15.04.2016.
 */

class SplineFieldDrawParams
{
    Collection<DoublePoint> collectionOfValues;

    ArrayList<DoublePoint> mainDots;
    double xMin;
    double xMax;
    double yMin;
    double yMax;
    SplineFieldDrawParams(Collection<DoublePoint> set, ArrayList<DoublePoint> mainDots,double xMin,double xMax,double yMin, double yMax)
    {
        this.collectionOfValues = set;
        this.mainDots = mainDots;
        this.xMax = xMax;
        this.xMin= xMin;
        this.yMax = yMax;
        this.yMin = yMin;
    }
}

public class SplineField extends Observable
{
    private Map<Double, DoublePoint > mapOfValues;
    private double xMin = 0.0; //Эти значения
    private double xMax = 0.0; // являются минмальными
    private double yMin = 0.0; //  и максимальные
    private double yMax = 0.0; //   для поля сллайна включая опорные точки
    private double xSplineMin = 0.0; // а это минимум и максимум для сплайна
    private double xSplineMax = 0.0;
    private double splineLength = 0.0;

    private ArrayList<DoublePoint> mainDots;
    private ArrayList<DoublePoint> allDots;
    private int dotsCount;
    private int extraDotsCount;
    private int mainDotsCount;

    public SplineField()
    {
        mapOfValues = new HashMap<Double, DoublePoint>();
        mainDots = new ArrayList<DoublePoint>();
        allDots = new ArrayList<DoublePoint>();
        buildSpline();
    }

    public double getXSplineMin()
    {
        return xSplineMin;
    }
    public double getXSplineMax()
    {
        return xSplineMax;
    }

    public DoublePoint getValue(double i)
    {
        //в форме акта дарения должна быть указана цена автомоб
        double length = splineLength;
        double p = i * length ;
        double min = 10;
        double v_min = -1;
        for (Double v : mapOfValues.keySet())
        {
            if (Math.abs(p-v) < min)
            {
                min = Math.abs(p-v);
                v_min = v;
            }
        }
        return mapOfValues.get(v_min);
    }


    public void deleteMainDotByUser(DoublePoint point, double d)
    {
        if (mainDotsCount > 0) {
            if (removeClosest(point,d)!=-1) {
                mainDotsCount--;
                buildSpline();
            }
        }
    }
    public int removeFrom(DoublePoint p, double d)
    {
        if (mainDotsCount > 0 )
        {
            int m = removeClosest(p,d);
            if (m !=-1)
            {
                --mainDotsCount;
                return m;
            }
        }
        return -1;
    }
    public void moveMainDotByUser(DoublePoint point,DoublePoint newPoint, double d)
    {
        if (mainDotsCount > 0) {
            if (removeClosest(point,d)!=-1) {
                addMainDot(newPoint);
                buildSpline();
            }
        }
    }

    public void deleteExtraDot()
    {
        if (extraDotsCount > 0 )
        {
            extraDotsCount--;
            buildSpline();
        }
    }
    public void addExtraDot()
    {
        extraDotsCount++;
        buildSpline();
    }
    private int removeClosest(DoublePoint point,double d)
    {
        double min = 10000;
        int i_min = -1;
        int size = mainDots.size();

        for (int i = 0 ; i < size ;++i)
        {
            DoublePoint p = mainDots.get(i);
            double v = (p.x - point.x) * (p.x-point.x) + (p.y - point.y) * (p.y - point.y);
            if (v < min)
            {
                min = v;
                i_min = i;
            }
        }
        if (i_min != -1 && min < d)
        {
            mainDots.remove(i_min);
            return i_min;
        }
        return -1;
    }
    private void checkMinMax(double x, double y)
    {
        if (x < xMin)
        {
            xMin = x;
        }
        if (y < yMin)
        {
            yMin = y;
        }
        if (y > yMax)
        {
            yMax = y;
        }
        if (x > xMax)
        {
            xMax = x;
        }
        if (x < xSplineMin)
        {
            xSplineMin  =x;
        }
        if (x > xSplineMax)
        {
            xSplineMax = x;
        }
    }
    private void buildSpline()
    {
        buildDotSet();
        mapOfValues.clear();
        splineLength = 0;
        for (int i = 1  ; i < dotsCount -2; ++i)
        {
            double[][] coffs = buildPartOfSpline(i);
            double tStep = 0.001;
            double xPrev = 0.0;
            double yPrev = 0.0f;
            int v = (int)(1.0/tStep);
            for (int j = 0; j < v; ++j)
            {
                double t = tStep * j;
                double x = coffs[0][3] + coffs[0][2] * t + coffs[0][1]*t*t+coffs[0][0] * t* t* t;
                double y = coffs[1][3] + coffs[1][2] * t + coffs[1][1]*t*t+coffs[1][0] * t* t* t;
                if (i ==0 && j ==0)
                {
                    xSplineMin = xSplineMax = xMin = xMax = x;
                    yMax = yMin = y;
                }
                else
                {
                    checkMinMax(x, y);
                }

                if (j!=0)
                {
                    double len = (x-xPrev) * (x-xPrev) + (y-yPrev)*(y - yPrev);
                    splineLength += Math.sqrt(len);
                }
                xPrev = x;
                yPrev = y;
                mapOfValues.put(splineLength,new DoublePoint(x,y));
            }
        }
        setChanged();
        notifyObservers();
    }
    private void checkMin(DoublePoint dot)
    {
        double x =dot.x;
        double y = dot.y;
        if (x < xMin)
        {
            xMin = x;
        }
        if (y < yMin)
        {
            yMin =y;
        }
        if (y > yMax)
        {
            yMax =y;
        }
        if (x > xMax)
        {
            xMax = x;
        }
    }
    private void addMainDot(DoublePoint extraMainDot)
    {
        DoublePoint newMainDot = new DoublePoint(extraMainDot);
        mainDots.add(newMainDot);
    }
    private void addMainDot(DoublePoint extraMainDot,int i)
    {
        DoublePoint newMainDot = new DoublePoint(extraMainDot);
        double x =newMainDot.x;
        double y = newMainDot.y;
        mainDots.add(i,newMainDot);
        checkMin(extraMainDot);
    }
    public void addMainDotByUser(DoublePoint extraMainDot)
    {
        addMainDot(extraMainDot);
        mainDotsCount++;
        buildSpline();
    }

    public void addMainDotByUser(DoublePoint extraMainDot,int i)
    {
        addMainDot(extraMainDot,i);
        mainDotsCount++;
        buildSpline();
    }

    public void changeExtraDotsCount(int count)
    {
        extraDotsCount = count;
        buildSpline();
    }

    public ArrayList<DoublePoint> getMainDots() {
        return mainDots;
    }

    public SplineFieldDrawParams getDrawParams()
    {
        double xMin = this.xMin > 0 ? 0 : this.xMin;
        double yMin = this.yMin > 0 ? 0 : this.yMin;
        return new SplineFieldDrawParams(mapOfValues.values(),mainDots,xMin,xMax,yMin,yMax);
    }

    private void buildDotSet()
    {
        //TODO этот метод должен учитывать еще и дополнительные точки!
        allDots = mainDots;
        dotsCount = mainDotsCount;
    }

    private double[][] buildPartOfSpline(int i)
    {
        double[][] Ms = {
                {-1.0 / 6, 1.0 / 2, -1.0 / 2, 1.0 / 6},
                {1.0 / 2, -1.0, 1.0 / 2, 0.0},
                {-1.0 / 2, 0, 1.0 / 2, 0.0},
                {1.0 / 6, 2.0 / 3, 1.0 / 6, 0}
        };
        double[] Gsx =
                {
                        allDots.get(i-1).x,
                        allDots.get(i).x,
                        allDots.get(i + 1).x,
                        allDots.get(i + 2).x,
                };
        double[] Gsy =
                {
                        allDots.get(i - 1).y,
                        allDots.get(i).y,
                        allDots.get(i + 1).y,
                        allDots.get(i + 2).y,
                };
        Matrix matrix = new Matrix(Ms);
        Vector vectorX = new Vector(Gsx);
        Vector retX = MathUtils.mulMatrixOnVectorTrans(matrix,vectorX);
        double[] rx = retX.getDoubleArray();
        Vector vectorY = new Vector(Gsy);
        Vector retY = MathUtils.mulMatrixOnVectorTrans(matrix,vectorY);
        double[] ry = retY.getDoubleArray();
        double[][] ret = new double[2][];
        ret[0] = rx;
        ret[1] = ry;
        return ret;
    }
}
