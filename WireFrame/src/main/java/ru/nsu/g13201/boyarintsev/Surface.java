package ru.nsu.g13201.boyarintsev;

import javafx.util.Pair;
import sun.applet.Main;

import java.awt.*;
import java.util.*;

/**
 * Created by Артем on 14.04.2016.
 */



class SurfaceParams
{
    String name;
    int R,G,B;
    SplineField splineField;
    double CX,CY,CZ; // положение в мировом пространстве
    double RX,RY,RZ; //повороты

}



public class Surface extends Observable {
    private Map<Pair<Double,Double>,Double> x = null;
    private Map<Pair<Double,Double>,Double> y = null ;
    private Map<Pair<Double,Double>,Double> z = null;

    private Map<Pair<Double,Double>,Double> xInWorldCoordinates = null ; // с
    private Map<Pair<Double,Double>,Double> yInWorldCoordinates = null; // учетом
    private Map<Pair<Double,Double>,Double> zInWorldCoordinates = null; // поворотов

    private Set<SpaceLine> spaceLines = null;
    private Matrix rotations = null;

    private MainSceneParams mainParams = null ;
    private SurfaceParams surfaceParams = null;


    public Set<SpaceLine> getSpaceLines()
    {
        return spaceLines;
    }
    public Surface(MainSceneParams mainParams,SurfaceParams surfaceParams)
    {
        this.mainParams = mainParams;
        this.surfaceParams = surfaceParams;
        x = new HashMap<Pair<Double, Double>, Double>();
        y = new HashMap<Pair<Double, Double>, Double>();
        z = new HashMap<Pair<Double, Double>, Double>();
        xInWorldCoordinates = new HashMap<Pair<Double, Double>, Double>();
        yInWorldCoordinates = new HashMap<Pair<Double, Double>, Double>();
        zInWorldCoordinates = new HashMap<Pair<Double, Double>, Double>();
        spaceLines = new HashSet<SpaceLine>();
        buildSurface();
    }

    public Surface(MainSceneParams mainSceneParams)
    {
        this.mainParams = mainSceneParams;
    }

    public SplineField getSplineField()
    {
        if (surfaceParams == null )
        {
            return null;
        }
        return surfaceParams.splineField;
    }

    public boolean changeMainParams(MainSceneParams params)
    {
        this.mainParams = params;
        return applyParams();
    }

    public boolean changeSurfaceParams(SurfaceParams params)
    {
        this.surfaceParams = params;
        return applyParams();
    }

    public String getName()
    {
        if (surfaceParams == null)
        {
            return null;
        }
        return surfaceParams.name;
    }

    public boolean changeAllParams(MainSceneParams sceneParams,SurfaceParams surfaceParams)
    {
        this.mainParams = sceneParams;
        this.surfaceParams = surfaceParams;
        return applyParams();
    }

    public SurfaceParams getSurfaceParams() {
        return surfaceParams;
    }

    private boolean applyParams()
    {
        x = new HashMap<Pair<Double, Double>, Double>();
        y = new HashMap<Pair<Double, Double>, Double>();
        z = new HashMap<Pair<Double, Double>, Double>();
        xInWorldCoordinates = new HashMap<Pair<Double, Double>, Double>();
        yInWorldCoordinates = new HashMap<Pair<Double, Double>, Double>();
        zInWorldCoordinates = new HashMap<Pair<Double, Double>, Double>();
        spaceLines = new HashSet<SpaceLine>();
        return buildSurface();
    }

    private boolean buildSurface()
    {
        if (surfaceParams==null || surfaceParams.splineField == null||surfaceParams.name ==null)
        {
            return false;
        }

        final SplineField splineField = surfaceParams.splineField;
        double uStart = mainParams.a;
        double uEnd = mainParams.b;
        double vStart = mainParams.c;
        double vEnd = mainParams.d;
        int n = mainParams.n;
        int m = mainParams.m;
        double stepU = (uEnd - uStart) / (mainParams.n - 1);
        double stepV = (vEnd-vStart) / (mainParams.m -1);

        double xMin = surfaceParams.splineField.getXSplineMin();
        double xMax = surfaceParams.splineField.getXSplineMax();

        Double3DPoint surfaceCentre = getSurfaceCentre();
        Double3DPoint surfaceCentreInWorld = new Double3DPoint(surfaceParams.CX,surfaceParams.CY,surfaceParams.CZ);

        for (int i = 0 ; i < n; ++i)
        {
            DoublePoint gpu = splineField.getValue(uStart+stepU*i);

            for ( int j = 0 ; j < m; ++j)
            {
                Pair<Double, Double> uvPair = new Pair<Double, Double>(uStart + stepU * i, vStart + stepV * j);
                double pointX = gpu.y * Math.cos(vStart + stepV * j);
                double pointY = gpu.y * Math.sin(vStart + stepV * j);
                double pointZ = gpu.x;
                Double3DPoint point = new Double3DPoint(pointX, pointY, pointZ);
                x.put(uvPair, pointX);
                y.put(uvPair, pointY);
                z.put(uvPair, pointZ);
                Double3DPoint pointWithTurns = turns(surfaceParams, point);
                Double3DPoint pointWithTurnAndOffset = offset(surfaceCentreInWorld, surfaceCentre, pointWithTurns);
                xInWorldCoordinates.put(uvPair, pointWithTurnAndOffset.x);
                yInWorldCoordinates.put(uvPair, pointWithTurnAndOffset.y);
                zInWorldCoordinates.put(uvPair, pointWithTurnAndOffset.z);
                if (i != 0)
                {
                    Pair<Double,Double> p = new Pair<Double, Double>(uStart + stepU * (i-1), vStart + stepV * j);
                    double xx = xInWorldCoordinates.get(p);
                    double yy = yInWorldCoordinates.get(p);
                    double zz = zInWorldCoordinates.get(p);
                    spaceLines.add(new SpaceLine(new Double3DPoint(xx,yy,zz),pointWithTurnAndOffset));
                }
                if (j != 0)
                {
                    Pair<Double,Double> p = new Pair<Double, Double>(uStart + stepU * (i), vStart + stepV * (j - 1));
                    double xx = xInWorldCoordinates.get(p);
                    double yy = yInWorldCoordinates.get(p);
                    double zz = zInWorldCoordinates.get(p);
                    spaceLines.add(new SpaceLine(new Double3DPoint(xx,yy,zz),pointWithTurnAndOffset));
                }
            }
        }
        setChanged();
        notifyObservers();
        //После инициализаци Поверхность задана: как в мировых координатах, так и в собственных.
        //и содержит все свои отрезки.
        return true;
    }

    private Double3DPoint getSurfaceCentre()
    {
        SplineField field = surfaceParams.splineField;
        DoublePoint p = field.getValue(0);
        return new Double3DPoint(p.y,0,p.x);
    }

    private Double3DPoint offset(Double3DPoint surfaceCentreInWorld,Double3DPoint surfaceCentre,Double3DPoint point)
    {
        double[] off = {
                surfaceCentreInWorld.x-surfaceCentre.x,
                surfaceCentreInWorld.y-surfaceCentre.y,
                surfaceCentreInWorld.z-surfaceCentre.z,
        };
        return new Double3DPoint(point.x+off[0],point.y+off[1],point.z+off[2]);
    }

    private Double3DPoint turns(SurfaceParams params,Double3DPoint point)
    {
        double[] vec = {point.x,point.y,point.z,1.0};
        rotations = new Matrix(params.RX,params.RY,params.RZ);
        Vector vector = new Vector(vec);
        Vector result = MathUtils.mulMatrixOnVectorTrans(rotations,vector);
        double[] ret = result.getDoubleArray();
        return new Double3DPoint(ret[0],ret[1],ret[2]);
    }

    public Color getColor()
    {
        return new Color(surfaceParams.R,surfaceParams.G,surfaceParams.B);
    }

    private double getMin(DoublePoint[] points )
    {
        double min = points[0].x;
        for ( int i = 1 ; i < points.length; ++i)
        {
            if (min > points[i].x)
            {
                min = points[i].x;
            }
        }
        return min;
    }

    private double getMax(DoublePoint[] points)
    {
        double max = points[0].x;
        for ( int i = 1 ; i < points.length; ++i)
        {
            if (max < points[i].x)
            {
                max = points[i].x;
            }
        }
        return max;
    }
}
