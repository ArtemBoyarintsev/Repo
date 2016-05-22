package ru.nsu.rayTracer.g13201.boyarintsev.MathUtils;

public class Vector3 extends Vector
{
    public Vector3(Double3DPoint vec)
    {
        super(3);
        v[0] = vec.getX();
        v[1] = vec.getY();
        v[2] = vec.getZ();
    }

    public Vector3(double[] v)
    {
        super(v);
    }

    public Vector3(Vector3 vec)
    {
        super(vec);
    }

    public Vector3(Vector3 vec, double k)
    {
        super(vec,k);
    }

    public Double3DPoint get3DPoint()
    {
        Double3DPoint ret;
        ret = new Double3DPoint(v[0],v[1],v[2]);
        return ret;
    }
}
