package ru.nsu.rayTracer.g13201.boyarintsev.MathUtils;

/**
 * Created by Артем on 08.05.2016.
 */
public class SpaceLine
{
    private Double3DPoint start;
    private Double3DPoint finish;
    public SpaceLine( Double3DPoint start, Double3DPoint finish)
    {
        this.start = start;
        this.finish = finish;
    }

    public Double3DPoint getFinish() {
        return finish;
    }

    public Double3DPoint getStart() {
        return start;
    }

    public void setFinish(Double3DPoint finish) {
        this.finish = finish;
    }

    public void setStart(Double3DPoint start) {
        this.start = start;
    }
}
