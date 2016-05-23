package ru.nsu.rayTracer.g13201.boyarintsev.MathUtils;

public class Vector
{
    public Vector(double[] v)
    {
        size = v.length;
        this.v = new double[size];
        System.arraycopy(v,0,this.v,0,v.length);
    }

    public Vector(Vector vec)
    {
        size = vec.size;
        this.v =  new double[size];
        System.arraycopy(vec.getDoubleArray(),0,v,0,size);
    }

    public Vector(Vector vec,double k)
    {
        double[] coors = vec.getDoubleArray();
        size = vec.size;
        v = new double[size];
        for (int i = 0 ; i <  size;++i)
        {
            v[i] = coors[i]*k;
        }
    }

    public Vector(int size)
    {
        v = new double[size];
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public double get(int i)
    {
        if (i > v.length)
        {
            throw  new IllegalArgumentException();
        }
        return v[i];
    }

    public double getLength()
    {
        double sum = 0 ;
        for (double aV : v) {
            sum += aV * aV;
        }
        return Math.sqrt(sum);
    }


    public double[] getDoubleArray()
    {
        return v;
    }


    protected double[] v;
    protected int size;

}
