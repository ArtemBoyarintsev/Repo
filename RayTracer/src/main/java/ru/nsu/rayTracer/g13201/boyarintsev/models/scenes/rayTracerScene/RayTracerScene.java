package ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene;

import javafx.util.Pair;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.DoublePoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.MathUtils;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Matrix4x4;
import ru.nsu.rayTracer.g13201.boyarintsev.models.Camera;
import ru.nsu.rayTracer.g13201.boyarintsev.models.RenderSettings;
import ru.nsu.rayTracer.g13201.boyarintsev.models.SceneLights;
import ru.nsu.rayTracer.g13201.boyarintsev.models.SceneObject;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.iScene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Артем on 13.05.2016.
 */
public class RayTracerScene extends iScene {

    public RayTracerScene(Camera camera, Dimension size, SceneLights sceneLights,RenderSettings renderSettings)
    {
        this.sceneLights = new SceneLights(sceneLights);
        this.renderSettings = new RenderSettings(renderSettings);
        this.camera = camera;
        paneSize = new Dimension(size);
    }

    public Matrix4x4 getReverseCameraMatrix()
    {
        return camera.getMatrixCameraReverse();
    }

    public double getSw()
    {
        return camera.getSw();
    }

    public double getSh()
    {
        return camera.getSh();
    }

    public double getZn()
    {
        return camera.getZn();
    }

    public void addSceneObject(SceneObject sceneObject) {
        sceneObjects.add(sceneObject);
    }

    public SceneLights getSceneLights()
    {
        return sceneLights;
    }

    public RenderSettings getRenderSettings() {
        return renderSettings;
    }

    public List<SceneObject> getSceneObjects() {
        return sceneObjects;
    }

    public void sceneBuilding() {
        buildAScene();
        setChanged();
        notifyObservers();
    }

    private int getGammaColor(double c,double gamma)
    {
        double floatRet = Math.pow(c,gamma);
        return (int)(floatRet*255);
    }

    private boolean checkOnBackGround(Double[] components) {
        Color bgColor = renderSettings.backGroundColor;
        return components.length == 3 && (components[0] == bgColor.getRed() &&
                components[1] == bgColor.getGreen() && components[2] == bgColor.getBlue());

    }

    private Color getColor(Double[] colorComponents,double max)
    {
        if (checkOnBackGround(colorComponents))
        {
            return renderSettings.backGroundColor;
        }
        int red = getGammaColor(colorComponents[0]/max,1);
        int green = getGammaColor(colorComponents[1]/max,1);
        int blue =  getGammaColor(colorComponents[2]/max,1);
        return new Color(red,green,blue);
    }

    private ColorExpressionist expressionistCreatingAndStart(DoublePoint point)
    {
        Double3DPoint p = new Double3DPoint(point.getX(),point.getY(),-camera.getZn());
        ColorExpressionist expressionist = new ColorExpressionist(this,p,renderSettings.depth);
        expressionist.rayTracerPassStart();
        return expressionist;
    }
    private ArrayList<ColorExpressionist> getExpressionistsQualityCorresponded(double i, double j)
    {
        ArrayList<ColorExpressionist> ret = new ArrayList<ColorExpressionist>();
        DoublePoint point;
        switch (renderSettings.quality)
        {
            case rough:
                if (i%2 ==0 && j ==0)
                {
                    point = MathUtils.getMathCoordinates(i,j,paneSize);
                }
                else if (i%2 == 0 && j %2 != 0)
                {
                    point = MathUtils.getMathCoordinates(i,j-1,paneSize);
                }
                else if (i%2 != 0 && j %2 == 0)
                {
                    point = MathUtils.getMathCoordinates(i,j-1,paneSize);
                }
                else
                {
                    point = MathUtils.getMathCoordinates(i-1,j-1,paneSize);
                }
                ret.add(expressionistCreatingAndStart(point));
                return ret;
            case normal:
                point = MathUtils.getMathCoordinates(i,j,paneSize);
                ret.add(expressionistCreatingAndStart(point));
                return ret;
            case fine:
                DoublePoint[] points = new DoublePoint[]
                        {
                        MathUtils.getMathCoordinates(i,j,paneSize),
                        MathUtils.getMathCoordinates((i)+1/2.0,j,paneSize),
                        MathUtils.getMathCoordinates(i,(j)+1/2.0,paneSize),
                        MathUtils.getMathCoordinates((i)+1/2.0,(j)+1/2.0,paneSize),
                };
                for (DoublePoint point1 :points)
                {
                    ret.add(expressionistCreatingAndStart(point1));
                }
                return ret;
        }
        return ret;
    }

    private void buildAScene() {
        int red = 0;
        int green = 1;
        int blue = 2;
        int width = paneSize.width;
        int height = paneSize.height;
        sceneImage = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
        HashMap<Pair<Integer,Integer>,Double[]> colors =
                                new HashMap<Pair<Integer, Integer>,Double[]>();
        double sw = camera.getSw();
        double sh = camera.getSh();
        double kw = sw > sh ? sw/sh:1;
        double kh = sh > sw ? sh/sw :1;
        double max = 0;
        for(int i = 0; i < sceneImage.getWidth();++i)
        {
            for (int j = 0 ; j < sceneImage.getHeight(); ++j)
            {
                ArrayList<ColorExpressionist> pointExpressionists = getExpressionistsQualityCorresponded(i,j);
                Double[] dar = new Double[]{new Double(0.0),new Double(0.0),new Double(0.0)};
                for( ColorExpressionist expressionist : pointExpressionists)
                {
                    double[] cc = expressionist.getColorComponents();
                    dar[red]  += cc[red];
                    dar[green]+= cc[green];
                    dar[blue] += cc[blue];
                }
                dar[red]   /=  pointExpressionists.size();
                dar[green] /=  pointExpressionists.size();
                dar[blue]  /=  pointExpressionists.size();
                colors.put(new Pair<Integer, Integer>(i,j),dar);
                if (checkOnBackGround(dar)) {
                    continue;
                }
                for (double c : dar) {
                    if (c > max) {
                        max = c;
                    }
                }
            }
        }
        for (int i = 0; i < sceneImage.getWidth();++i)
        {
            for ( int j = 0 ; j < sceneImage.getHeight(); ++j)
            {
                Double[] colorComponents = colors.get(new Pair<Integer, Integer>(i,j));
                sceneImage.setRGB(i,j,getColor(colorComponents,max).getRGB());
            }
        }
    }


    final private Dimension paneSize;

    private Camera camera;
    private List<SceneObject> sceneObjects = new ArrayList<SceneObject>();

    private SceneLights sceneLights;
    private RenderSettings renderSettings;

}