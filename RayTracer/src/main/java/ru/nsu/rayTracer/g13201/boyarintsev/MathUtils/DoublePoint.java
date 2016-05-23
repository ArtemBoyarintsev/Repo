package ru.nsu.rayTracer.g13201.boyarintsev.MathUtils;


public class DoublePoint
{
    private double x = 0;
    private double y =0;

    public DoublePoint(double x,double y)
    {
        this.x = x;
        this.y = y;
    }


    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
