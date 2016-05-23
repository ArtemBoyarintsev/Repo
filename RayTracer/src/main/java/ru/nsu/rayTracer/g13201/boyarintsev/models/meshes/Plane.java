package ru.nsu.rayTracer.g13201.boyarintsev.models.meshes;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.MathUtils;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Vector3;

public class Plane {
    final public Vector3 normal;
    final public double D;

    public Plane(Vector3 a,Vector3 b, Double3DPoint point)
    {
        int x = 0 ;
        int y = 1 ;
        int z = 2 ;
        this.normal = MathUtils.getNormalToPlane(a,b);
        this.D = -(normal.get(x)*point.getX() +
                normal.get(y)*point.getY() +
                normal.get(z)*point.getZ());
    }
}
