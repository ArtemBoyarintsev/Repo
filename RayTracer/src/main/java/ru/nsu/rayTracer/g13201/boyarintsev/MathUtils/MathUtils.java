package ru.nsu.rayTracer.g13201.boyarintsev.MathUtils;

import java.awt.*;

public class MathUtils
{
    static public Vector4 mulMatrixOnVector(Matrix4x4 matrix, Vector4 vector)
    {
        double[] ret = new double[vector.getSize()];
        double[] vec = vector.getDoubleArray();
        double[][] a = matrix.getDoubleArray();
        for (int i = 0 ; i < vector.getSize();++i)
        {
            for (int j = 0; j < vector.getSize(); ++j)
            {
                ret[i] += a[i][j] * vec[j];
            }
        }
        return new Vector4(ret);
    }

    static public Matrix4x4 mulMatrixOnMatrix(Matrix4x4 a, Matrix4x4 b)
    {
        //По идее не помешают проверки на размеры, но, так как мы будем оперировать только с матрицами 4*4, особо не страшно.
        int size = a.getSize();
        Matrix4x4 ret = new Matrix4x4(size);
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

    static public Vector3 subtract(Double3DPoint a,Double3DPoint b)
    {
        try {
            double[] ar = {a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ()};
            return new Vector3(ar);
        }
        catch(NullPointerException ex)
        {
            System.err.println("ааа");
        }
        return new Vector3(b);
    }

    static public Double3DPoint add(Double3DPoint a,Double3DPoint b)
    {
        return new Double3DPoint(a.getX() + b.getX(), a.getY()+b.getY(), a.getZ()+b.getZ());
    }

    static public Vector3 mulVecOnVec(Vector3 v1,Vector3 v2)
    {
        double[] a = v1.getDoubleArray();
        double[] b = v2.getDoubleArray();
        int x = 0, y =1 , z =2;
        double[] ret = { a[y]*b[z]-a[z]*b[y],a[z]*b[x]-a[x]*b[z],a[x]*b[y]-a[y]*b[x] };
        return new Vector3(ret);
    }

    static public Double3DPoint normalize(Double3DPoint point)
    {
        double len = Math.sqrt(point.getX()*point.getX()+point.getY()*point.getY()+point.getZ()*point.getZ());
        return new Double3DPoint(point.getX()/len,point.getY()/len,point.getZ()/len);
    }

    static public Matrix4x4 trans(Matrix4x4 a)
    {
        double[][] aArray = a.getDoubleArray();
        double[][] retArray = new double[aArray[0].length][aArray.length];
        for (int i = 0 ; i  < aArray.length; ++i)
        {
            for ( int j = 0 ; j  < aArray[i].length;++j)
            {
                retArray[j][i] = aArray[i][j];
            }
        }
        return new Matrix4x4(retArray);
    }

    static public double scalarMultiplication(Vector a,Vector b) {
        double mul = 0;

        if (a.getSize() != b.getSize())
        {
            throw new IllegalArgumentException("Размеры векторов должны быть одинаковыми!");
        }

        for (int i = 0; i < a.getSize(); ++i)
        {
            mul += a.get(i)*b.get(i);
        }
        return mul;
    }
    static public double getAngleBetween(Vector a, Vector b)
    {
        if (a.getSize() != b.getSize())
        {
            throw new IllegalArgumentException("Размеры векторов должны быть одинаковыми!");
        }
        double mul = scalarMultiplication(a,b);
        double aLength = a.getLength();
        double bLength = b.getLength();
        return mul/(aLength*bLength);
    }
    /*
    * Здесь плоскость задана векторами a и b
    * */
    static public Vector3 getNormalToPlane(Vector3 a,Vector3 b)
    {
        return MathUtils.getNormalizeVector(mulVecOnVec(a,b));
    }
    static public Vector3 vectorAdd(Vector3 a, Vector3 b)
    {
        double[] retArray = new double[3];
        for (int i = 0; i < 3 ; ++i)
        {
            retArray[i] = a.get(i) + b.get(i);
        }
        return new Vector3(retArray);
    }
    static public Vector3 getReflectedVector(Vector3 b,Vector3 normal)
    {
        //b - вектор
        //проецируется на normal, c = b-normal
        Vector3 bBack = new Vector3(b,-1);
        double cof = MathUtils.scalarMultiplication(bBack,normal)/normal.getLength();
        Vector3 c = MathUtils.subtract(bBack.get3DPoint(),new Vector3(normal,cof).get3DPoint());
        return MathUtils.subtract(bBack.get3DPoint(),new Vector3(c,2).get3DPoint());
    }

    static public Vector3 getNormalizeVector(Vector3 vector)
    {
        double length = vector.getLength();
        if (length ==0)
        {
            return vector;
        }
        return new Vector3(vector,1/length);
    }
    static public Vector3 getBisector(Vector3 a,Vector3 b)
    {
        a = getNormalizeVector(a);
        b = getNormalizeVector(b);
        return getNormalizeVector(vectorAdd(a,b));
    }

    static public Point screenCoordinates(double xP, double yP,Dimension paneSize) {
        int x = (int) ((paneSize.getWidth() - 1) * (xP+1)/2);
        int y = (int) ((paneSize.getHeight() - 1)* ((yP+1)/2));
        return new Point(x, y);
    }

    static public DoublePoint getMathCoordinates(double x,double y,Dimension paneSize)
    {
        double xP = 1 - 2 * x / (paneSize.getWidth() - 1) ;
        double yP = 1 - 2 * y / (paneSize.getHeight() - 1) ;
        return new DoublePoint(xP,yP);
    }

    static public Double3DPoint moveDouble3DPoint(Double3DPoint move,Vector3 direction)
    {
        int x = 0, y = 1, z = 2;
        return
                new Double3DPoint(move.getX()+direction.get(x),move.getY()+direction.get(y),move.getZ()+direction.get(z));
    }

    static public Matrix4x4 getRotationMatrix(double rotation_angleX,double rotation_angleY,double rotation_angleZ)
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
        Matrix4x4 matrixXRot = new Matrix4x4(xRot);
        Matrix4x4 matrixYRot = new Matrix4x4(yRot);
        Matrix4x4 matrixZRot = new Matrix4x4(zRot);
        return mulMatrixOnMatrix(mulMatrixOnMatrix(matrixXRot,matrixYRot),matrixZRot);
    }
}
