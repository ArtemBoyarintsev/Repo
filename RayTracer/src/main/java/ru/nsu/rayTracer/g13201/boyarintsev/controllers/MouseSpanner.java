package ru.nsu.rayTracer.g13201.boyarintsev.controllers;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.*;

import java.awt.*;

public class MouseSpanner {
    private final static int y = 1;
    private final static int x = 0;
    double[] turnAngles;
    private Point mousePosition;

    public MouseSpanner(Point mousePosition)
    {
        turnAngles = new double[2];
        this.mousePosition = mousePosition;
    }

    public Matrix4x4 getRotationMatrix(Point mousePoint)
    {
        if (mousePosition ==null)
        {
            mousePosition = new Point(mousePoint);
        }
        double xO = 0.001*(mousePoint.x - mousePosition.x);
        double yO = 0.0001*(mousePoint.y - mousePosition.y);
        turnAngles[x] = (turnAngles[x]+ xO);
        turnAngles[y] =  turnAngles[y] + yO;
        this.mousePosition = new Point(mousePoint);
        return MathUtils.getRotationMatrix(turnAngles[x],turnAngles[y],0);
    }

    public void stopRotate()
    {
        mousePosition = null;
    }
}
