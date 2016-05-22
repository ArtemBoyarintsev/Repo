package ru.nsu.rayTracer.g13201.boyarintsev.MathUtils;

public class Double3DPoint
{
    private double x = 0;
    private double y = 0;
    private double z = 0;

    public Double3DPoint(double x,double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Double3DPoint(Double3DPoint point)
    {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
    }

    public Double3DPoint()
    {

    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getZ() {
        return z;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }
}
