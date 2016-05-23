package ru.nsu.rayTracer.g13201.boyarintsev.models;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;

import java.awt.*;

public class PointLight {
    public Double3DPoint position = new Double3DPoint();
    public Color color = Color.white;

    public PointLight()
    {

    }

    public PointLight(PointLight pointLight)
    {
        this.position = new Double3DPoint(pointLight.position);
        this.color = new Color(pointLight.color.getRGB());
    }
}
