package ru.nsu.g13201.boyarintsev;

/**
 * Created by Артем on 14.04.2016.
 */
public class Camera
{
    final Double3DPoint position = new Double3DPoint(-1,0,0);
    final Double3DPoint viewPoint = new Double3DPoint(1,0,0);
    final Vector up;
    private double zn,zf;
    private double sw,sh;
    {
        double[] d = {0,1,0};
        up = new Vector(d);
    }
    Camera(double zn, double zf,double sw,double sh)
    {
        this.zn = zn;
        this.zf = zf;
        this.sw = sw;
        this.sh = sh;
    }

    public void setZn(double value)
    {
        zn = value;
    }
    public void setZf(double value)
    {
        zf = value;
    }
    public void setSw(double value)
    {
        sw = value;
    }
    public void setSh(double value)
    {
        sh = value;
    }

    public double getSh() {
        return sh;
    }

    public double getSw() {
        return sw;
    }

    public double getZf() {
        return zf;
    }

    public double getZn() {
        return zn;
    }

    public Matrix getMatrixCamera()
    {
        double[][] d = {
                {1,0,0,-position.x},
                {1,0,0,-position.y},
                {1,0,0,-position.z},
                {1,0,0,1          },
        };
        Double3DPoint k = new Double3DPoint(position.x-viewPoint.x,position.y-viewPoint.y,position.z-viewPoint.z);
        Vector KK = new Vector(k);
        double lenK = MathUtils.getLength(KK);
        Vector kk = MathUtils.increaseLen(KK,1/lenK);
        Vector I = MathUtils.mulVecOnVec(up,kk);
        double len = MathUtils.getLength(I);
        Vector i = MathUtils.increaseLen(I,1/len);
        Vector j = MathUtils.mulVecOnVec(kk,i);
        double[] a = { 0,0,0,1 };
        Vector[] vs = {new Vector(4,i),new Vector(4,j),new Vector(4,kk),new Vector(a)};
        Matrix m  = new Matrix(vs);
        return MathUtils.mulMatrixOnMatrix(m,new Matrix(d));
    }
}
