package ru.nsu.rayTracer.g13201.boyarintsev.models;

import java.awt.*;
import java.util.ArrayList;

public class SceneLights
{
    public Color diffuseColor = Color.WHITE;
    public ArrayList <PointLight> pointLights = new ArrayList<PointLight>();

    public SceneLights()
    {

    }
    public SceneLights(SceneLights sceneLights)
    {
        this.diffuseColor = new Color(sceneLights.diffuseColor.getRGB());
        for (PointLight pointLight : sceneLights.pointLights)
        {
            PointLight copyPL = new PointLight(pointLight);
            this.pointLights.add(copyPL);
        }
    }
}
