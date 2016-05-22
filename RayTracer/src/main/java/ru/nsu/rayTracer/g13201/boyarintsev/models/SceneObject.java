package ru.nsu.rayTracer.g13201.boyarintsev.models;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.SpaceLine;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Vector3;
import ru.nsu.rayTracer.g13201.boyarintsev.models.meshes.Mesh;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene.Ray;

import java.util.ArrayList;

public class SceneObject {
    private Mesh mesh;
    private OpticalCharacteristics characteristics;

    public SceneObject(Mesh mesh,OpticalCharacteristics characteristics)
    {
        this.characteristics = characteristics;
        this.mesh = mesh;
    }

    public void setQuality(int quality)
    {
        mesh.setQuality(quality);
    }

    public ArrayList<SpaceLine> getSpaceLines()
    {
        return mesh.getFigureSpaceLines();
    }

    public ArrayList<SpaceLine> getOrts() {
        return mesh.getOrts();
    }

    public Vector3 getNormal(Double3DPoint point)
    {
        return mesh.getNormal(point);
    }

    public boolean crossWith(Ray ray)
    {
        return mesh.crossWith(ray);
    }

    public Double3DPoint getPointCrossWith(Ray ray)
    {
        return mesh.getPointCrossWith(ray);
    }

    public OpticalCharacteristics getCharacteristics() {
        return characteristics;
    }
}
