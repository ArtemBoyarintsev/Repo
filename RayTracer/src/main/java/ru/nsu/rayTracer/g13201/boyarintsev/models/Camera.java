package ru.nsu.rayTracer.g13201.boyarintsev.models;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.*;


public class Camera
{
    private  Double3DPoint position;
    private  Double3DPoint viewPoint = new Double3DPoint(0,0,0);
    private  Vector3 up;

    private double zn,zf;
    private double sw,sh;

    {
        double[] d = {0,1,0};
        up = new Vector3(d);
    }

    public Camera(RenderSettings renderSettings)
    {
        Vector3 actualUp = correctUp(renderSettings);
        this.position = new Double3DPoint(renderSettings.cameraPosition);
        this.viewPoint = new Double3DPoint(renderSettings.viewPosition);
        this.up = new Vector3(actualUp);
        this.zn = renderSettings.zn;
        this.zf = renderSettings.zf;
        this.sw = renderSettings.sw;
        this.sh = renderSettings.sh;
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

    public Matrix4x4 getMatrixCameraReverse()
    {
        double[][] firstMatrixComponent = {
                {1,0,0,position.getX()},
                {0,1,0,position.getY()},
                {0,0,1,position.getZ()},
                {0,0,0,1          },
        };
        Matrix4x4 firstMatrix = new Matrix4x4(firstMatrixComponent);
        Matrix4x4 secondMatrix = MathUtils.trans(getLeftPartOfCameraMatrix());
        return MathUtils.mulMatrixOnMatrix(firstMatrix,secondMatrix);
    }

    private Vector3 correctUp(RenderSettings renderSettings)
    {
        int x = 0, y = 1, z = 2;
        Vector3 Z = MathUtils.subtract(renderSettings.viewPosition,renderSettings.cameraPosition);
        if (MathUtils.scalarMultiplication(Z,renderSettings.up) == 0)
        {
            return renderSettings.up;
        }
        Vector3 right = MathUtils.mulVecOnVec(Z,renderSettings.up);
        if (right.get(x)==0 && right.get(y)==0 && right.get(z) == 0)
        {
            throw new IllegalArgumentException();
        }
        return MathUtils.mulVecOnVec(right,Z);
    }
    private Matrix4x4 getLeftPartOfCameraMatrix()
    {
        Vector3 K = MathUtils.subtract(position,viewPoint);
        Vector3 k = new Vector3(K,1/K.getLength());
        Vector3 I = MathUtils.mulVecOnVec(up,k);
        Vector3 i = new Vector3(I,1/I.getLength());
        Vector3 j = MathUtils.mulVecOnVec(k,i);
        double[] a = { 0,0,0,1 };
        Vector4[] vs = {new Vector4(i,0),new Vector4(j,0),new Vector4(k,0),new Vector4(a)};
        return new Matrix4x4(vs);
    }

    private Matrix4x4 getRightPartOfCameraMatrix()
    {
        double[][] d = {
                {1,0,0,-position.getX()},
                {0,1,0,-position.getY()},
                {0,0,1,-position.getZ()},
                {0,0,0,1          },
        };
        return new Matrix4x4(d);
    }
    public Matrix4x4 getMatrixCamera()
    {
        Matrix4x4 rightPart = getRightPartOfCameraMatrix();
        Matrix4x4 leftPart = getLeftPartOfCameraMatrix();
        return MathUtils.mulMatrixOnMatrix(leftPart,rightPart);
    }

    private DoublePoint getPoint(Double3DPoint point)
    {
        if (point.getZ() < -2 || point.getZ() > 2)
        {
            return null;
        }
        if (point.getX() < -1 ||point.getX() > 1||point.getY() < -1 ||point.getY() > 1)
        {
            return null;
        }
        return new DoublePoint(point.getX(),point.getY());
    }

    public Double3DPoint[] cameraMatrixTransform(Double3DPoint start,Double3DPoint finish)
    {
        Double3DPoint[] ret = new Double3DPoint[2];
        Matrix4x4 cameraMatrix = getMatrixCamera();

        double[] vecStart = {start.getX(),start.getY(),start.getZ(),1.0};
        double[] vecFinish = {finish.getX(),finish.getY(),finish.getZ(),1.0};

        Vector4 vectorStart = new Vector4(vecStart);
        Vector4 vectorFinish = new Vector4(vecFinish);
        Vector4 resultStart = MathUtils.mulMatrixOnVector(cameraMatrix,vectorStart);
        Vector4 resultFinish = MathUtils.mulMatrixOnVector(cameraMatrix,vectorFinish);

        Double3DPoint retS = resultStart.get3DPoint();
        Double3DPoint retF = resultFinish.get3DPoint();

        ret[0] = retS;
        ret[1] = retF;

        return ret;
    }

    public DoublePoint[] projection(Double3DPoint start,Double3DPoint finish)
    {
        double[][] projectionArray = {
                {2.0*zf/sw,0,0,0},
                {0,2.0*zf/sh,0,0},
                {0,0,  (zn) / (zf-zn), -zf*zn / (zf-zn)},
                {0,0,   1, 0}
        };

//        double[][] projectionArray = {
//                {2.0*zf/sw,0,0,0},
//                {0,2.0*zf/sh,0,0},
//                {0,0,  -(zf+zn) / (zf-zn), -2*zf*zn / (zf-zn)},
//                {0,0,   -1, 0}
//        };

        Matrix4x4 projection = new Matrix4x4(projectionArray);

        Vector4 ss = new Vector4(start,1);
        Vector4 ff = new Vector4(finish,1);

        Vector4 vStart  = MathUtils.mulMatrixOnVector(projection,ss);
        Vector4 vFinish = MathUtils.mulMatrixOnVector(projection,ff);

        Double3DPoint pointStart = vStart.get3DPoint();
        Double3DPoint pointFinish = vFinish.get3DPoint();

        DoublePoint retStart = getPoint(pointStart);
        DoublePoint retFinish = getPoint(pointFinish);

        if (null == retStart || null == retFinish)
        {
            return null;
        }
        return new DoublePoint[]  { retStart, retFinish };
    }
}
