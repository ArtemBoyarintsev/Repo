package ru.nsu.rayTracer.g13201.boyarintsev.models.meshes;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.MathUtils;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.SpaceLine;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Vector3;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene.Ray;

public class Triangle extends Mesh {

    public Triangle(Double3DPoint a,Double3DPoint b,Double3DPoint c)
    {
        this.a = a;
        this.b = b;
        this.c = c;
        init();
    }

    @Override
    public Vector3 getNormal(Double3DPoint point) {
        if (!pointLaysInTriangle(point))
        {
            return null;
        }
        Vector3 first = MathUtils.subtract(a,b);
        Vector3 second = MathUtils.subtract(c,b);
        return MathUtils.getNormalToPlane(first,second);
    }

    @Override
    public boolean crossWith(Ray ray) {
        return getPointCrossWith(ray)!=null;
    }

    @Override
    public Double3DPoint getPointCrossWith(Ray ray) {
        Double3DPoint crossPoint = crossWithTrianglePlane(ray);
        if (crossPoint == null)
        {
            return null;
        }
        if (pointLaysInTriangle(crossPoint))
        {
            return crossPoint;
        }
        return null;
    }

    private boolean pointLaysInTriangle(Double3DPoint crossPoint)
    {
        double abcSquare = triangleSquare(a,b,c);
        double abcPSquare = triangleSquare(a,b,crossPoint);
        double accPSquare = triangleSquare(a,c,crossPoint);
        double bccPSquare = triangleSquare(b,c,crossPoint);
        double sum = abcPSquare+accPSquare+bccPSquare;
        double eps = 0.05;
        return Math.abs(abcSquare - sum) < eps;
    }
    private static double triangleSquare(Double3DPoint a,Double3DPoint b,Double3DPoint c)
    {
        double first = MathUtils.subtract(a,b).getLength();
        double second = MathUtils.subtract(c,b).getLength();
        double third = MathUtils.subtract(a,c).getLength();
        double p = (first+second+third)/2;
        return Math.sqrt(p*(p-first)*(p-second)*(p-third));
    }

    private Double3DPoint crossWithTrianglePlane(Ray ray)
    {
        Vector3 first = MathUtils.subtract(a,b);
        Vector3 second = MathUtils.subtract(c,b);
        Plane trianglePlane =  new Plane(first,second,a);
        return  Mesh.crossLineWithPlane(ray,trianglePlane);
    }
    private void init()
    {
        initOrts();
        SpaceLine spaceLine = new SpaceLine(a,b);
        SpaceLine spaceLine2 = new SpaceLine(b,c);
        SpaceLine spaceLine3 = new SpaceLine(a,c);
        figureSpaceLines.add(spaceLine);
        figureSpaceLines.add(spaceLine2);
        figureSpaceLines.add(spaceLine3);
    }
    private void initOrts()
    {

    }

    private Double3DPoint b;
    private Double3DPoint a;
    private Double3DPoint c;
}
