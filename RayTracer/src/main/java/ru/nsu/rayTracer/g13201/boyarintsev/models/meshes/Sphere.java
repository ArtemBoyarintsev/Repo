package ru.nsu.rayTracer.g13201.boyarintsev.models.meshes;

import javafx.util.Pair;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.MathUtils;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.SpaceLine;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Vector3;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene.Ray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Артем on 08.05.2016.
 */
public class Sphere extends Mesh {

    private ArrayList<Double3DPoint> consistingPoints = new ArrayList<Double3DPoint>();
    private Double3DPoint centre;
    private double radius;

    public Sphere(Double3DPoint centre,double radius)
    {
        this.centre = centre;
        this.radius= radius;
        init();
    }

    @Override
    public void setQuality(int quality)
    {
        super.setQuality(quality);
        init();
    }

    @Override
    public Vector3 getNormal(Double3DPoint point) {
        Vector3 v = MathUtils.subtract(point,centre);
        double eps = 0.001;
        double len = v.getLength();
        if (radius - eps <= len && len <= radius+eps)
        {
            return new Vector3(v,1/radius);
        }
        return null;
    }

    @Override
    public boolean crossWith(Ray ray) {
        return getPointCrossWith(ray) != null;
    }

    @Override
    public Double3DPoint getPointCrossWith(Ray ray) {
        Vector3 oc = MathUtils.subtract(ray.startPoint,centre);
        double B = MathUtils.scalarMultiplication(ray.vector, oc);
        double C = (oc.getLength() * oc.getLength()) - radius*radius;
        double D = B*B - C;
        if (D < 0)
        {
            return null;
        }
        double t = -B - Math.sqrt(D);
        if (t    < 0)
        {
            t = -B + Math.sqrt(D);
        }
        if (t <  0)
        {
            return null;
        }
        int x=0, y=1,z=2;
        return new Double3DPoint(
                ray.startPoint.getX() + t*ray.vector.get(x),
                ray.startPoint.getY() + t*ray.vector.get(y),
                ray.startPoint.getZ() + t*ray.vector.get(z));
    }

    private void init()
    {
        Map<Pair<Integer,Integer>,Double3DPoint> map = new HashMap<Pair<Integer, Integer>, Double3DPoint>();
        initOrts();
        for (int i = 0; i < quality; ++i)
        {
            for (int j = 0; j < quality; ++j)
            {
                Double3DPoint point = getPoint(i,j);
                Double3DPoint norm = MathUtils.normalize(point);
                Double3DPoint inWorld = MathUtils.add(point,centre);
                consistingPoints.add(point);
                if (i != 0 )
                {
                    Double3DPoint prePoint = map.get(new Pair<Integer, Integer>(i-1,j));
                    SpaceLine spaceLine = new SpaceLine(prePoint,inWorld);
                    figureSpaceLines.add(spaceLine);
                }
                if (j != 0)
                {
                    Double3DPoint prePoint = map.get(new Pair<Integer, Integer>(i,j - 1));
                    SpaceLine spaceLine = new SpaceLine(prePoint,inWorld);
                    figureSpaceLines.add(spaceLine);
                }
                if (i == quality - 1)
                {
                    Double3DPoint prePoint = map.get(new Pair<Integer, Integer>(0,j));
                    SpaceLine spaceLine = new SpaceLine(prePoint,inWorld);
                    figureSpaceLines.add(spaceLine);
                }
                map.put(new Pair<Integer, Integer>(i,j),inWorld);
            }
        }
    }

    private void initOrts()
    {
        Double3DPoint zero = MathUtils.add(new Double3DPoint(0,0,0),centre);
        Double3DPoint xOrt = MathUtils.add(new Double3DPoint(2*radius,0,0),centre);
        Double3DPoint yOrt = MathUtils.add(new Double3DPoint(0,2*radius,0),centre);
        Double3DPoint zOrt = MathUtils.add(new Double3DPoint(0,0,2*radius),centre);
        orts.add(new SpaceLine(zero,xOrt));
        orts.add(new SpaceLine(zero,yOrt));
        orts.add(new SpaceLine(zero,zOrt));
    }

    private Double3DPoint getPoint(int i,int j)
    {
        final double PI = 3.1415;
        double phi = 2* PI * ((double)i) / quality;
        double theta = PI * ((double)j) / quality;
        Double3DPoint point = new Double3DPoint();
        point.setX(radius * Math.sin(theta)*Math.cos(phi));
        point.setY(radius * Math.sin(theta)*Math.sin(phi));
        point.setZ(radius * Math.cos(theta));
        return point;
    }
}
