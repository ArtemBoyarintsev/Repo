package ru.nsu.rayTracer.g13201.boyarintsev.models.meshes;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.SpaceLine;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Vector3;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene.Ray;

import java.util.ArrayList;


public class Box extends Mesh {
    private Double3DPoint min;
    private Double3DPoint max;
    private ArrayList<Square> consistingSquares = new ArrayList<Square>();

    public Box(Double3DPoint min,Double3DPoint max)
    {
        this.max = max;
        this.min = min;
        init();
    }

    private void init()
    {
        ArrayList<Double3DPoint> points = new ArrayList<Double3DPoint>();
        double[] xS = new double[]{min.getX(),max.getX()};
        double[] yS = new double[]{min.getY(),max.getY()};
        double[] zS = new double[]{min.getZ(),max.getZ()};
        // 0 -- (min,min,min)
        //1 -- (min,min,max)
        //2 -- (min,max,min)
        //3 -- (min,max,max)
        //4 -- (max,min,min)
        //5 -- (max,min,max)
        //6 -- (max ,max ,min)
        // 7 -- (max,max,max)

        for (int i = 0 ; i < 2; ++i )
        {
            for (int j = 0  ;j < 2; ++j)
            {
                for (int k = 0; k < 2; ++k)
                {
                    points.add(new Double3DPoint(xS[i],yS[j],zS[k]));
                }
            }
        }
        consistingSquares.add(new Square(points.get(2),points.get(3),points.get(0),points.get(1)));
        consistingSquares.add(new Square(points.get(0),points.get(1),points.get(4),points.get(5)));
        consistingSquares.add(new Square(points.get(4),points.get(5),points.get(6),points.get(7)));
        consistingSquares.add(new Square(points.get(6),points.get(7),points.get(2),points.get(3)));
        consistingSquares.add(new Square(points.get(1),points.get(3),points.get(5),points.get(7)));
        consistingSquares.add(new Square(points.get(4),points.get(6),points.get(0),points.get(2)));
    }


    @Override
    public Vector3 getNormal(Double3DPoint point) {
        for (Square s: consistingSquares)
        {
            Vector3 normal = s.getNormal(point);
            if (normal != null)
            {
                return normal;
            }
        }
        return null;
    }

    @Override
    public boolean crossWith(Ray ray) {
        return getPointCrossWith(ray) != null;
    }

    @Override
    public Double3DPoint getPointCrossWith(Ray ray) {
        for (Square s: consistingSquares)
        {
            Double3DPoint point = s.getPointCrossWith(ray);
            if (point != null)
            {
                return point;
            }
        }
        return null;
    }

    @Override
    public ArrayList<SpaceLine> getFigureSpaceLines()
    {
        ArrayList<SpaceLine> ret = new ArrayList<SpaceLine>();
        for (Square s: consistingSquares)
        {
            ret.addAll(s.getFigureSpaceLines());
        }
        return ret;
    }
}
