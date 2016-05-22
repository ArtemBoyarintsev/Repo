package ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.*;
import ru.nsu.rayTracer.g13201.boyarintsev.models.OpticalCharacteristics;
import ru.nsu.rayTracer.g13201.boyarintsev.models.PointLight;
import ru.nsu.rayTracer.g13201.boyarintsev.models.SceneObject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


public class ColorExpressionist {


    public ColorExpressionist(RayTracerScene rayTracerScene, Double3DPoint pointOnPlane, int depth)
    {
        this.sceneObjects = rayTracerScene.getSceneObjects();
        this.pointLights = rayTracerScene.getSceneLights().pointLights;
        this.diffuseColor = rayTracerScene.getSceneLights().diffuseColor;
        this.backGroundColor = rayTracerScene.getRenderSettings().backGroundColor;
        this.depth = depth; //rayTracerScene.getTracerDepth();

        Matrix4x4 cameraMatrixRevers = rayTracerScene.getReverseCameraMatrix();
        double[] startInCameraCoors = {0, 0, 0, 1};
        double[] finishInCameraCoors = { pointOnPlane.getX(), pointOnPlane.getY(), pointOnPlane.getZ(),1 };
        Double3DPoint start = viewPosition =
                    MathUtils.mulMatrixOnVector(cameraMatrixRevers, new Vector4(startInCameraCoors)).get3DPoint();
        Double3DPoint finish = MathUtils.mulMatrixOnVector(cameraMatrixRevers, new Vector4(finishInCameraCoors)).get3DPoint();

        traceVector = new TraceVector(MathUtils.subtract(finish,start), start);
    }

    public void rayTracerPassStart() // функция запуска луча.
    {
        ArrayList<Double> red = new ArrayList<Double>();
        ArrayList<Double> green = new ArrayList<Double>();
        ArrayList<Double> blue = new ArrayList<Double>();
        ArrayList<SceneObject> correspondObjects = new ArrayList<SceneObject>();
        for (int i = 0; i < depth; ++i) {
            List<SceneObject> allTracedObjects = getAllAreTracedObjects();
            SceneObject sceneObject = findClosest(allTracedObjects);
            if (sceneObject == null) {
                break;
            }
            Double3DPoint crossPoint = getCrossPoint(sceneObject);
            Vector3 sceneObjectNormal = sceneObject.getNormal(crossPoint);
            crossPoint = getCrossPoint(sceneObject);
            if (sceneObjectNormal == null) {
                break;
            }
            if (!visibleSide(sceneObjectNormal))
            {
                break;
            }
            double[] cc = expressAmbientAndFromLightsSourceColor(sceneObject,crossPoint,sceneObjectNormal);
            red.add(cc[0]);
            green.add(cc[1]);
            blue.add(cc[2]);
            correspondObjects.add(sceneObject);
            setupTraceVectorAfterCrossing(crossPoint, sceneObjectNormal);
        }
        expressColor(red,green,blue,correspondObjects);
    }

    public double[] getColorComponents() {
        return new double[]{redSum,greenSum,blueSum};
    }

    private void expressColor(ArrayList<Double> red,ArrayList<Double> green,ArrayList<Double> blue,ArrayList<SceneObject> objects)
    {
        if (red.size() == 0) {
            redSum = backGroundColor.getRed();
            greenSum = backGroundColor.getGreen();
            blueSum = backGroundColor.getBlue();
            return;
        }
        OpticalCharacteristics ch = new OpticalCharacteristics();
        for (int i = red.size()-1; i >= 0; --i) {
            redSum = redSum*ch.redMirror + red.get(i);
            greenSum = greenSum*ch.greenMirror + green.get(i);
            blueSum = blueSum*ch.blueMirror + blue.get(i);
            ch = objects.get(i).getCharacteristics();
        }
    }

    private boolean visibleSide(Vector3 normal)
    {
        double angle = MathUtils.getAngleBetween(traceVector.getDirection(), normal);
        return angle <= 0;
    }

    private double[] expressAmbientAndFromLightsSourceColor(SceneObject sceneObject, Double3DPoint crossPoint, Vector3 objectNormal) {
        OpticalCharacteristics ch = sceneObject.getCharacteristics();
        double localRed = diffuseColor.getRed()*ch.redDiffuse;
        double localGreen = diffuseColor.getGreen()*ch.greenDiffuse;
        double localBlue = diffuseColor.getBlue()*ch.blueDiffuse;
        ArrayList<PointLight> influencePointLights = getInfluencePointLights(sceneObject,crossPoint);
        for (PointLight pointLight: influencePointLights)
        {
            Double3DPoint pointLightPosition = pointLight.position;
            Vector3 Li = MathUtils.getNormalizeVector(MathUtils.subtract(pointLightPosition,crossPoint)); // направление на источник.
            Vector3 E = MathUtils.getNormalizeVector(MathUtils.subtract(viewPosition,crossPoint)); // вектор на наблюдателя
            Vector3 H = MathUtils.getNormalizeVector(MathUtils.getBisector(E,Li));
            double fatt = 1/(1+Li.getLength());
            double lnScalar = MathUtils.scalarMultiplication(Li,objectNormal);
            double nhScalar = MathUtils.scalarMultiplication(H,objectNormal);
            double power = ch.power;
            localRed +=  pointLight.color.getRed()*fatt * (ch.redDiffuse*lnScalar+ch.redMirror*Math.pow(nhScalar,power));
            localGreen += pointLight.color.getGreen() *fatt * (ch.greenDiffuse*lnScalar+ch.greenMirror*Math.pow(nhScalar,power));
            localBlue += pointLight.color.getBlue()*fatt * (ch.blueDiffuse*lnScalar+ch.blueMirror*Math.pow(nhScalar,power));
        }
        return returnWithChecks(new double[]{localRed,localGreen,localBlue});
    }

    private double[] returnWithChecks(double[] check)
    {
        if (check.length < 3)
        {
            throw new IllegalArgumentException();
        }
        for(int i = 0 ; i <  check.length;++i)
        {
            if (check[i] < 0 )
            {
                check[i] = 0;
            }
        }
        return check;
    }

    private ArrayList<PointLight> getInfluencePointLights(SceneObject sceneObject,Double3DPoint point)
    {
        ArrayList<PointLight> ret = new ArrayList<PointLight>();
        for (PointLight pointLight: pointLights)
        {
            Vector3 sourceDirection = MathUtils.subtract(point,pointLight.position);
            TraceVector traceVector = new TraceVector(sourceDirection,pointLight.position);
            List<SceneObject> areCrossedByTV = getAllAreTracedObjects(traceVector);
            SceneObject closest = findClosest(areCrossedByTV,traceVector);
            if (closest == sceneObject)
            {
                ret.add(pointLight);
            }
        }
        return ret;
    }

    private void setupTraceVectorAfterCrossing(Double3DPoint crossPoint, Vector3 sceneObjectNormal)
    {
        int x = 0, y = 1, z = 2;
        Vector3 vector = MathUtils.getReflectedVector(traceVector.getDirection(),sceneObjectNormal);
        traceVector.setDirection(vector);
        Vector3 v = traceVector.getDirection();
        Double3DPoint position =
                new Double3DPoint
                (
                        crossPoint.getX() + v.get(x)*0.01,
                        crossPoint.getY()+v.get(y)*0.01,
                        crossPoint.getZ()+v.get(z)*0.01
                );
        traceVector.setStartPosition(position);
    }

    private Double3DPoint getCrossPoint(SceneObject sceneObject)
    {
        return getCrossPoint(sceneObject,traceVector);
    }

    private Double3DPoint getCrossPoint(SceneObject sceneObject,TraceVector traceVector)
    {
        Ray ray = new Ray(traceVector);
        return sceneObject.getPointCrossWith(ray);
    }

    //выдают список всех объектов, которые пересекает TraceVector.
    private List<SceneObject> getAllAreTracedObjects(TraceVector traceVector)
    {
        List<SceneObject> ret = new ArrayList<SceneObject>();
        Ray ray = new Ray(traceVector);
        for (SceneObject sceneObject : sceneObjects)
        {
            if (sceneObject.crossWith(ray))
            {
                ret.add(sceneObject);
            }
        }
        return ret;
    }

    private List<SceneObject> getAllAreTracedObjects()
    {
        return getAllAreTracedObjects(traceVector);
    }

    private SceneObject findClosest(List<SceneObject> sceneObjectList,TraceVector traceVector)
    {
        Double3DPoint traceVectorStart = traceVector.getStartPosition();
        double min = 0;

        SceneObject ret = null;
        for (SceneObject sceneObject: sceneObjectList)
        {
            Double3DPoint crossPoint = getCrossPoint(sceneObject,traceVector);
            Vector v = MathUtils.subtract(crossPoint,traceVectorStart);
            if (ret == null || v.getLength() < min)
            {
                min = v.getLength();
                ret = sceneObject;
            }
        }
        return ret;
    }

    private SceneObject findClosest(List<SceneObject> sceneObjectList)
    {
        return findClosest(sceneObjectList,traceVector);
    }

    private List<SceneObject> sceneObjects;
    private List<PointLight> pointLights;

    final private Color diffuseColor;
    final private Color backGroundColor;

    private double redSum = 0f;
    private double greenSum = 0f;
    private double blueSum = 0f;

    private Double3DPoint viewPosition;

    private TraceVector traceVector;

    private int depth;
}