package ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.MathUtils;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Vector3;

public class Ray { //данный класс является всего лишь представлением TraceVector для всех остальных классов модели.
    final public Vector3 vector;
    final public Double3DPoint startPoint;

    public Ray(TraceVector traceVector)
    {
        vector = MathUtils.getNormalizeVector(traceVector.getDirection());
        startPoint = traceVector.getStartPosition();
    }
}
