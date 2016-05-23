package ru.nsu.rayTracer.g13201.boyarintsev.models.meshes;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.MathUtils;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.SpaceLine;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Vector3;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene.Ray;

import java.util.ArrayList;

/**
 * Created by Артем on 08.05.2016.
 */
public abstract class Mesh {

    public void setQuality(int quality)
    {
        this.quality = quality;
    }

    public ArrayList<SpaceLine> getFigureSpaceLines() {
        return figureSpaceLines;
    }
    public ArrayList<SpaceLine> getOrts()
    {
        return orts;
    }

    abstract public Vector3 getNormal(Double3DPoint point);

    abstract public boolean crossWith(Ray ray);

    abstract public Double3DPoint getPointCrossWith(Ray ray);


    protected ArrayList<SpaceLine> figureSpaceLines = new ArrayList<SpaceLine>();
    protected ArrayList<SpaceLine> orts = new ArrayList<SpaceLine>();

    protected int quality;

    public static Double3DPoint crossLineWithPlane(Ray ray, Plane plane)
    {
        int x = 0;
        int y = 1;
        int z = 2;
        Vector3 Rd = ray.vector;
        Vector3 R0 = new Vector3(ray.startPoint);
        Vector3 Pn = plane.normal;
        double D = plane.D;
        double vd = MathUtils.scalarMultiplication(Pn,Rd);
        if (vd >=0)
        {
            return null;
        }
        double v0 = -(MathUtils.scalarMultiplication(Pn,R0) + D);
        double t = v0/vd;
        if (t < 0)
        {
            return null;
        }
        return new Double3DPoint(
                ray.startPoint.getX()+Rd.get(x)*t,
                ray.startPoint.getY()+Rd.get(y)*t,
                ray.startPoint.getZ()+Rd.get(z)*t
        );
    }
}
