package ru.nsu.g13201.boyarintsev;

/**
 * Created by Артем on 14.04.2016.
 */

class DoublePoint
{
    double x = 0;
    double y =0;
    DoublePoint(double x,double y)
    {
        this.x = x;
        this.y = y;
    }
    DoublePoint(DoublePoint d)
    {
        this.x = d.x;
        this.y = d.y;
    }
}
class Point2D
{
    DoublePoint point;
    int color;

    public Point2D (DoublePoint point,int color)
    {
        this.color = color;
        this.point = point;
    }
}
class Double3DPoint
{
    double x = 0;
    double y = 0;
    double z = 0;
    Double3DPoint(double x,double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    Double3DPoint divOn(double k)
    {
        return new Double3DPoint(x/k,y/k,z/k);
    }
}

class SpaceLine
{
    Double3DPoint start;
    Double3DPoint finish;
    SpaceLine( Double3DPoint start, Double3DPoint finish)
    {
        this.start = start;
        this.finish = finish;
    }
}

class Vector
{
    double[] v;
    int size;

    public Vector(int size,Vector vec)
    {
        double[] c = vec.getDoubleArray();
        this.size = Math.max(size,c.length);
        v = new double[this.size];
        for ( int i = 0 ; i <  c.length;++i )
        {
            v[i] = c[i];
        }
    }
    public Vector (int size, Double3DPoint vec)
    {
        this.size = Math.max(size,3);
        v = new double[this.size];
        v[0] = vec.x;
        v[1] = vec.y;
        v[2] = vec.z;
    }

    public Vector(Double3DPoint k)
    {
        v = new double[3];
        this.size = 3;
        v[0]=k.x;
        v[1]=k.y;
        v[2]=k.z;
    }

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
    public Vector(Vector vect,double k)
    {
        double[] coords = vect.getDoubleArray();
        size = vect.size;
        v = new double[size];
        for (int i = 0 ; i <  size;++i)
        {
            v[i] = coords[i]*k;
        }
    }
    public double[] getDoubleArray()
    {
        return v;
    }
}
class Matrix
{
    /* *
    *   Матрица хранится по строкам
    * */
    double[][] a;
    int size;
    public Matrix(int size)
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
    public Matrix(double[][] m)
    {
        size = m.length;
        this.a = new double[size][];
        for (int i = 0 ; i < size;++i)
        {
            a[i] = new double[size];
            System.arraycopy(m[i],0,a[i],0,size);
        }
    }
    public Matrix(Vector[] vectors)
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

    public Matrix(double rotation_angleX, double rotation_angleY,double rotation_angleZ)
    {
        double[][] xRot = {
                {1, 0 ,                       0 ,                       0 },
                {0, Math.cos(rotation_angleX),-Math.sin(rotation_angleX),0 },
                {0, Math.sin(rotation_angleX),Math.cos(rotation_angleX),0 },
                {0, 0,                         0,                        1 }
        };
        double[][] yRot = {
                {Math.cos(rotation_angleY), 0, Math.sin(rotation_angleY),0 },
                {0,                         1, 0,                        0 },
                {-Math.sin(rotation_angleY),0, Math.cos(rotation_angleY),0 },
                {0,                         0, 0,                        1 }
        };
        double[][] zRot = {
                { Math.cos(rotation_angleZ),-Math.sin(rotation_angleZ),0,0 },
                { Math.sin(rotation_angleZ),Math.cos(rotation_angleZ), 0,0 },
                { 0,                        0,                         1,0 },
                { 0,                        0,                         0,1 }
        };
        Matrix matrixXRot = new Matrix(xRot);
        Matrix matrixYRot = new Matrix(yRot);
        Matrix matrixZRot = new Matrix(zRot);
        a = MathUtils.mulMatrixOnMatrix(MathUtils.mulMatrixOnMatrix(matrixXRot,matrixYRot),matrixZRot).getDoubleArray();
    }
    public double[][] getDoubleArray()
    {
        return a;
    }
}

public class MathUtils
{
    static public Vector mulMatrixOnVectorTrans(Matrix matrix,Vector vector)
    {
        double[] ret = new double[vector.size];
        double[] vec = vector.getDoubleArray();
        double[][] a = matrix.getDoubleArray();
        for (int i = 0 ; i < vector.size;++i)
        {
            for (int j = 0; j < vector.size; ++j)
            {
                ret[i] += a[i][j] * vec[j];
            }
        }
        return new Vector(ret);
    }

    static public Matrix mulMatrixOnMatrix(Matrix a,Matrix b)
    {
        //По идее не помешают проверки на размеры, но, так как мы будем оперировать только с матрицами 4*4, особо не страшно.
        int size = a.size;
        Matrix ret = new Matrix(size);
        double[][] aa = a.getDoubleArray();
        double[][] ba = b.getDoubleArray();
        for(int i = 0; i < size; ++i)
        {
            for(int j = 0; j < size;++j)
            {
                double sum = 0.0;
                for(int k = 0; k < size; ++k)
                {
                    sum += aa[i][k] * ba[k][j];
                }
                ret.setValue(i,j,sum);
            }
        }
        return ret;
    }
    static public Vector mulVecOnVec(Vector v1,Vector v2)
    {
        double[] a = v1.getDoubleArray();
        double[] b = v2.getDoubleArray();
        int x = 0, y =1 , z =2;
        double[] ret = {a[y]*b[z]-a[z]*b[y],a[z]*b[x]-a[x]*b[z],a[x]*b[y]-a[y]*b[x]};
        return new Vector(ret);
    }
    static public double getLength(Vector vector)
    {
        double sum  = 0;
        double[] ar = vector.getDoubleArray();
        for (double d :ar )
        {
            sum+=d*d;
        }
        return Math.sqrt(sum);
    }
    static public Vector increaseLen(Vector v, double k)
    {
        Vector newVector = new Vector(v);
        double[] ar = newVector.getDoubleArray();
        for (int i = 0 ; i  <  ar.length;++i)
        {
            ar[i]*=k;
        }
        return newVector;
    }
}
