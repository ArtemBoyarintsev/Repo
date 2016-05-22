package ru.nsu.rayTracer.g13201.boyarintsev.MathUtils;

public class Vector4 extends Vector
{
    public Vector4(Double3DPoint vec, int last)
    {
        super(4);
        v[0] = vec.getX();
        v[1] = vec.getY();
        v[2] = vec.getZ();
        v[3] = last;
    }

    public Vector4(Vector3 vector3,int last)
    {
        super(4);
        v[0] = vector3.v[0];
        v[1] = vector3.v[1];
        v[2] = vector3.v[2];
        v[3] = last;
    }


    public Vector4(double[] v)
    {
        super(v);
    }

    public Vector4(Vector4 vec)
    {
       super(vec);
    }

    public Vector4(Vector4 vec, double k)
    {
        super(vec,k);
    }

    public Double3DPoint get3DPoint() {
        return new Double3DPoint(v[0] / v[3], v[1] / v[3], v[2] / v[3]);
    }

    public Vector3 getVector3()
    {
        double[] v1 = {v[0]/v[3],v[1]/v[3],v[2]/v[3]};
        return new Vector3(v1);
    }
}
