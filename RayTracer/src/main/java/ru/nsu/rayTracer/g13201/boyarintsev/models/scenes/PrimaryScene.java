package ru.nsu.rayTracer.g13201.boyarintsev.models.scenes;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.*;
import ru.nsu.rayTracer.g13201.boyarintsev.models.Camera;
import ru.nsu.rayTracer.g13201.boyarintsev.models.SceneObject;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class PrimaryScene extends iScene
{
    public PrimaryScene(Camera camera,Dimension size)
    {
        this.camera = camera;
        paneSize = new Dimension(size);
    }

    public void addSceneObject(SceneObject sceneObject)
    {
        sceneObjects.add(sceneObject);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void sceneBuilding()
    {
        buildAScene();
        setChanged();
        notifyObservers();
    }

    public List<SceneObject> getSceneObjects() {
        return sceneObjects;
    }

    private void spaceLineMatrixTransformation(SpaceLine spaceLine,Color color)
    {
        Double3DPoint start = spaceLine.getStart();
        Double3DPoint finish = spaceLine.getFinish();
        //Переводим в координаты камеры все.
        Double3DPoint[] inCameraCoors = camera.cameraMatrixTransform(start,finish);
        //Проецируем
        DoublePoint[] draw = camera.projection(inCameraCoors[0],inCameraCoors[1]);
        if (null != draw)
        {
            outLineOnSceneImage(draw[0],draw[1],color.getRGB());
        }
    }
    private void buildAScene()
    {
        int width = paneSize.width;
        int height = paneSize.height;
        int x = 0, y = 1, z = 2;
        sceneImage = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
        initImage(sceneImage);
        for( SceneObject sceneObject : sceneObjects)
        {
            sceneObject.setQuality(sceneObjectQuality);
            ArrayList<SpaceLine> objectSpaceLines = sceneObject.getSpaceLines();
            for (SpaceLine spaceLine : objectSpaceLines)
            {
                spaceLineMatrixTransformation(spaceLine,Color.WHITE);
            }
            ArrayList<SpaceLine> orts = sceneObject.getOrts();
            if (orts.size() < 3)
            {
                continue;
            }
            spaceLineMatrixTransformation(orts.get(x),xOrtColor);
            spaceLineMatrixTransformation(orts.get(y),yOrtColor);
            spaceLineMatrixTransformation(orts.get(z),zOrtColor);
        }
        drawSceneOrts();
    }

    private void drawSceneOrts()
    {
        Double3DPoint zero = new Double3DPoint(0,0,0);
        Double3DPoint xOrt = new Double3DPoint(20,0,0);
        Double3DPoint yOrt = new Double3DPoint(0,20,0);
        Double3DPoint zOrt = new Double3DPoint(0,0,20);
        spaceLineMatrixTransformation(new SpaceLine(zero,xOrt),xOrtColor);
        spaceLineMatrixTransformation(new SpaceLine(zero,yOrt),yOrtColor);
        spaceLineMatrixTransformation(new SpaceLine(zero,zOrt),zOrtColor);
    }

    private Point screenCoordinates(double xP, double yP)
    {
        return MathUtils.screenCoordinates(xP,yP,paneSize);
    }

    private void outLineOnSceneImage(DoublePoint start, DoublePoint finish,int color)
    {

        Point first = screenCoordinates(start.getX(),start.getY());
        Point second = screenCoordinates(finish.getX(),finish.getY());
        {
            drawLine(sceneImage, first.x, first.y, second.x, second.y, color);
        }
    }


    private int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
    }

    private void drawLine (BufferedImage image,int xStart, int yStart, int xEnd, int yEnd, int color)
    {
        int x, y, dx, dy, incX, incY, pdx, pdy, es, el, err;

        dx = xEnd - xStart;
        dy = yEnd - yStart;

        incX = sign(dx);
        incY = sign(dy);

        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;

        if (dx > dy)
        {
            pdx = incX;	pdy = 0;
            es = dy;	el = dx;
        }
        else
        {
            pdx = 0;
            pdy = incY;
            es = dx;
            el = dy;
        }

        x = xStart;
        y = yStart;
        err = el/2;
        image.setRGB(x,y,color);

        for (int t = 0; t < el; t++)
        {
            err -= es;
            if (err < 0)
            {
                err += el;
                x += incX;
                y += incY;
            }
            else
            {
                x += pdx;
                y += pdy;
            }
            image.setRGB(x,y,color);
        }
    }

    private void initImage(BufferedImage image)
    {
        int c = Color.BLACK.getRGB();
        for (int i = 0; i < image.getWidth();++i)
        {
            for ( int j = 0 ; j <  image.getHeight();++j)
            {
                image.setRGB(i,j,c);
            }
        }
    }

    final static private int sceneObjectQuality = 10;

    final private Dimension paneSize;

    private Color xOrtColor = Color.RED;
    private Color yOrtColor = Color.YELLOW;
    private Color zOrtColor = Color.GREEN;

    private Camera camera;
    private List<SceneObject> sceneObjects = new ArrayList<SceneObject>();
}
