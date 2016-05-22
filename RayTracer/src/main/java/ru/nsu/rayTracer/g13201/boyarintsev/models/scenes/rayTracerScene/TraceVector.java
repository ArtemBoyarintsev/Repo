package ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.MathUtils;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Vector3;

/**
 * Created by Артем on 15.05.2016.
 */
class TraceVector {
    private  Vector3 vector;
    private  Double3DPoint startPosition;

    public TraceVector(Vector3 vector, Double3DPoint startPosition)
    {
        this.vector = MathUtils.getNormalizeVector(vector);
        this.startPosition = startPosition;
    }
    public TraceVector()
    {

    }

    public TraceVector(TraceVector traceVector)
    {
        this.vector = new Vector3(traceVector.vector);
        this.startPosition = new Double3DPoint(traceVector.startPosition);
    }

    public void setDirection(Vector3 vector) {
        this.vector = MathUtils.getNormalizeVector(vector);
    }

    public void setStartPosition(Double3DPoint startPosition) {
        this.startPosition = startPosition;
    }

    public Double3DPoint getStartPosition() {
        return startPosition;
    }

    public Vector3 getDirection() {
        return vector;
    }
}
