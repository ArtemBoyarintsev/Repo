package ru.nsu.rayTracer.g13201.boyarintsev.MathUtils;


public class Matrix4x4
{
    /* *
    *   Матрица хранится по строкам
    * */
    private double[][] a;
    private int size;

    public double[][] getDoubleArray()
    {
        return a;
    }

    public Matrix4x4(int size)
    {
        this.size = size;
        a = new double[size][];
        for ( int i = 0 ; i < size; ++i)
        {
            a[i] = new double[size];
        }
    }

    public void setValue(int i,int j, double value)
    {
        if (i < size && j < size)
        {
            a[i][j] = value;
            return;
        }
        System.err.println("Какие то проблемы...Matrix.setValue");
    }

    public int getSize() {
        return size;
    }

    public Matrix4x4(double[][] m)
    {
        size = m.length;
        this.a = new double[size][];
        for (int i = 0 ; i < size;++i)
        {
            a[i] = new double[size];
            System.arraycopy(m[i],0,a[i],0,size);
        }
    }


    public Matrix4x4(Vector4[] vectors)
    {
        double[][] m = new double[vectors.length][];
        for (int i = 0 ;  i < vectors.length;++i)
        {
            m[i]=vectors[i].getDoubleArray();
        }
        size = m.length;
        this.a = new double[size][];
        for (int i = 0 ; i < size;++i)
        {
            a[i] = new double[size];
            System.arraycopy(m[i],0,a[i],0,size);
        }
    }
}
