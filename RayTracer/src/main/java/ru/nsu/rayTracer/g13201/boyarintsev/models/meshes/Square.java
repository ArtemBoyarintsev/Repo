package ru.nsu.rayTracer.g13201.boyarintsev.models.meshes;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.*;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene.Ray;
import java.util.ArrayList;

public class Square extends Mesh {

    @Override
    public Vector3 getNormal(Double3DPoint point) {
        Vector3 ret = first.getNormal(point);
        if (ret != null)
        {
            return ret;
        }
        return second.getNormal(point);
    }

    @Override
    public boolean crossWith(Ray ray) {
        return first.crossWith(ray) || second.crossWith(ray);
    }

    @Override
    public Double3DPoint getPointCrossWith(Ray ray) {
        Double3DPoint ret = first.getPointCrossWith(ray);
        if (ret != null)
        {
            return ret;
        }
        return second.getPointCrossWith(ray);
    }

    public Square(Double3DPoint a, Double3DPoint b, Double3DPoint c,Double3DPoint d)
    {
        this.a = new Double3DPoint(a);
        this.b = new Double3DPoint(b);
        this.c = new Double3DPoint(c);
        this.d = new Double3DPoint(d);
        init();
    }

    @Override
    public ArrayList<SpaceLine> getFigureSpaceLines()
    {
        ArrayList<SpaceLine> ret = first.getFigureSpaceLines();
        ret.addAll(second.getFigureSpaceLines());
        return ret;
    }

    private void init()
    {
        first = new Triangle(a,b,c);
        second = new Triangle(b,d,c);
    }

    private Double3DPoint a;
    private Double3DPoint b;
    private Double3DPoint c;
    private Double3DPoint d;

    private Triangle first,second;
}
