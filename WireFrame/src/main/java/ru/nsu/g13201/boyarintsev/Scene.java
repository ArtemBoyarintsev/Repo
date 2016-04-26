package ru.nsu.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by Артем on 20.04.2016.
 */

class MainSceneParams
{
    int n;
    int m;
    double a;
    double b;
    double c;
    double d;
    double zn;
    double zf;
    double sw;
    double sh;
    double ex;
    double ey;
    double ez;
    int br;
    int bb;
    int bg;
    public MainSceneParams()
    {
        n = 10;
        m = 10;
        a = 0.0;
        b = 1.0;
        c = 0.0;
        d = 6.28;
        zn = 0.0;
        zf = 13.0f;
        sw = 480;
        sh = 360;
        br = 0;
        bg = 0;
        bb = 0;
        ex = ey = ez = 0;

    }
    public MainSceneParams(int n, int m,double a,double b,double c, double d,double zn,double zf,double sw,
                           double sh,double ex,double ey,double ez,int br,int bg,int bb)
    {
        this.n = n;
        this.m = m;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.zn = zn;
        this.zf = zf;
        this.sw = sw;
        this.sh = sh;
        this.ex = ex;
        this.ey = ey;
        this.ez = ez;
        this.br = br;
        this.bg = bg;
        this.bb = bb;
    }
}
public class Scene extends JPanel implements Observer {

    BufferedImage bufferedImage;
    ArrayList<Surface> surfaces = new ArrayList<Surface>();
    Camera camera;
    Set<SpaceLine> spaceLines = new HashSet<SpaceLine>();
    private MainSceneParams sceneParams;
    private Matrix rotations = null;
    private Dimension paneSize;
    private Matrix offsetAndTurns = null;
    private double[] turnsAngle;
    private Matrix cameraMatrix;

    public Scene( Camera camera,Dimension paneSize,MainSceneParams mainSceneParams)
    {
        this.sceneParams = mainSceneParams;
        this.camera = camera;
        this.turnsAngle = new double[]{mainSceneParams.ex,mainSceneParams.ey,mainSceneParams.ez};
        this.paneSize = paneSize;

        BufferedImage bufferedImage = new BufferedImage(paneSize.width,paneSize.height,BufferedImage.TYPE_4BYTE_ABGR);
        initImage(bufferedImage);
        setSize(paneSize);
        setMinimumSize(paneSize);
    }

    public MainSceneParams getSceneParams()
    {
        return sceneParams;
    }


    public void changeMainParams(MainSceneParams params)
    {
        camera.setSh(params.sh);
        camera.setSw(params.sw);
        camera.setZf(params.zf);
        camera.setZn(params.zn);
        this.sceneParams = params;
        for (Surface surface: surfaces)
        {
            surface.changeMainParams(params);
        }
        buildAScene();
    }
    public void addSurfaceWithOutDrawing(Surface surface)
    {
        for(Surface s : surfaces)
        {
            if (surface.getName().equals(s.getName()))
            {
                System.err.println("Such name surface already exist");
                return;
            }
        }
        surfaces.add(surface);
        surface.addObserver(this);
    }
    public void addSurface(Surface surface)
    {
        addSurfaceWithOutDrawing(surface);
        buildAScene();
    }
    public void build()
    {
        buildAScene();
    }
    public void clearScene()
    {
        surfaces.clear();
        spaceLines.clear();
        buildAScene();
//        BufferedImage image = new BufferedImage(paneSize.width,paneSize.height,BufferedImage.TYPE_4BYTE_ABGR);
//        initImage(image);
//        repaint();
    }
    public void deleteSurface(Surface surface)
    {
        for (int i = 0 ; i < surfaces.size();++i)
        {
            if (surface == surfaces.get(i))
            {
                surfaces.remove(i);
                buildAScene();
                return;
            }
        }
    }

    public Surface getSurfaceByName(String name)
    {
        for (Surface surface : surfaces)
        {
            if (name.equals(surface.getName()))
            {
                return surface;
            }
        }
        return null;
    }

    public double[] getTurnsAngle() {
        double[] ret = new double[3];
        System.arraycopy(turnsAngle,0,ret,0,3);
        return ret;
    }

    public void setTurnsAngle(double[] turnsAngle) {
        this.turnsAngle = turnsAngle;
        buildAScene();
    }

    public ArrayList<Surface> getSurfaces() {
        return surfaces;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(bufferedImage,0,0,null);
    }
    private void checks(Double3DPoint double3DPoint,Double3DPoint min,Double3DPoint max)
    {
        if (double3DPoint.x < min.x)
        {
            min.x = double3DPoint.x;
        }
        if (double3DPoint.y < min.y)
        {
            min.y = double3DPoint.y;
        }
        if (double3DPoint.z < min.z)
        {
            min.z = double3DPoint.z;
        }
        if (double3DPoint.x > max.x)
        {
            max.x = double3DPoint.x;
        }
        if (double3DPoint.y > max.y)
        {
            max.y = double3DPoint.y;
        }
        if (double3DPoint.z > max.z)
        {
            max.z = double3DPoint.z;
        }
    }

    private double getMax(Double3DPoint p1,Double3DPoint p2)
    {
        double m1 = Math.max(Math.abs(p1.x),Math.abs(p1.y));
        m1 = Math.max(Math.abs(p1.z),m1);
        double m2 = Math.max(Math.abs(p2.x),Math.abs(p2.y));
        m2 = Math.max(Math.abs(p2.z),m2);
        return Math.max(Math.abs(m2),m1);
    }


    private void initImage(BufferedImage image)
    {
        int c = new Color(sceneParams.br,sceneParams.bg,sceneParams.bb).getRGB();
        for (int i = 0; i < image.getWidth();++i)
        {
            for ( int j = 0 ; j <  image.getHeight();++j)
            {
                image.setRGB(i,j,c);
            }
        }
    }

    private void buildAScene()
    {
        int width = paneSize.width;
        int height = paneSize.height;
        bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
        initImage(bufferedImage);

        Double3DPoint min = new Double3DPoint(10000, 10000,10000);
        Double3DPoint max = new Double3DPoint(0,0,0);
        for(Surface surface : surfaces)
        {
            if (surface.getSplineField()  == null)
            {
                continue;
            }
            Set<SpaceLine> spaceLines = surface.getSpaceLines();
            for ( SpaceLine line : spaceLines)
            {
                Double3DPoint start = line.start;
                Double3DPoint finish = line.start;
                checks(start,min,max);
                checks(finish,min,max);
            }
        }
        double k = getMax(min,max);
        //Строим габаритный бокс.
        ArrayList<Point2D[] > lines = new ArrayList<Point2D[]>();
        for(Surface surface : surfaces)
        {
            if (surface.getSplineField()  == null)
            {
                continue;
            }
            Set<SpaceLine> spaceLines = surface.getSpaceLines();
            for ( SpaceLine line : spaceLines)
            {
                Double3DPoint start = line.start;
                Double3DPoint finish = line.finish;
                Double3DPoint newStart = start.divOn(k);
                Double3DPoint newFinish = finish.divOn(k);
                Double3DPoint[] finalSF = turnEuler(newStart,newFinish);
                Double3DPoint[] inCameraCoors = finalSF;// cameraMatrixTransform(finalSF[0],finalSF[1]);
                DoublePoint[] draw = projection(inCameraCoors[0],inCameraCoors[1]);
                Point2D[] p = new Point2D[2];
                p[0] = new Point2D(draw[0],surface.getColor().getRGB());
                p[1] = new Point2D(draw[1],surface.getColor().getRGB());
                if (draw != null)
                    lines.add(p);
                   // finalStage(draw[0],draw[1],surface);
                //this.spaceLines.add(new SpaceLine(inCameraCoors[0], inCameraCoors[1]));
            }
        }
        if (lines.size() <=0)
        {
            repaint();
            return;
        }
        double pMinX = lines.get(0)[0].point.x;
        double pMaxX = pMinX;
        double pMinY = lines.get(0)[0].point.y;
        double pMaxY = pMinY;
        for (Point2D[] d : lines)
        {
            for ( Point2D p : d)
            {
                if (p.point.x < pMinX)
                {
                    pMinX =p.point.x;
                }
                if (p.point.x > pMaxX)
                {
                    pMaxX = p.point.x;
                }
                if (p.point.y < pMinY)
                {
                    pMinY = p.point.y;
                }
                if (p.point.y> pMaxY)
                {
                    pMaxY = p.point.y;
                }
            }
        }
        for (Point2D[] d: lines)
        {
            finalStage(d[0],d[1],pMinX,pMaxX,pMinY,pMaxY);
        }
        repaint();
    }

    private DoublePoint[] projection(Double3DPoint start,Double3DPoint finish)
    {
        double zn = camera.getZn();
        double zf = camera.getZf();
        double sw = camera.getSw();
        double sh = camera.getSh();
        double[][] projection = {
                {sw,0,0,0},
                {0,sh,0,0},
                {0,0,  zf / (zf-zn),1},
                {0,0,   -(zn*zf) / (zf-zn), 0}
        };
        Vector ss = new Vector(4,start);
        ss.getDoubleArray()[3]=1;
        Vector ff = new Vector(4,finish);
        ff.getDoubleArray()[3]=1;

        Vector vStart  = MathUtils.mulMatrixOnVectorTrans(new Matrix(projection),ss);
        Vector vFinish = MathUtils.mulMatrixOnVectorTrans(new Matrix(projection),ff);
        vStart = new Vector(vStart,1/vStart.getDoubleArray()[2]);
        vFinish = new Vector(vFinish,1/vFinish.getDoubleArray()[2]);
        if (vStart.getDoubleArray()[2]  < 0 || vStart.getDoubleArray()[2]  > 1||
                vFinish.getDoubleArray()[2]  < 0 || vFinish.getDoubleArray()[2]  > 1)
        {
            return null;
        }
        DoublePoint retStart = new DoublePoint(vStart.getDoubleArray()[0],vStart.getDoubleArray()[1]);
        DoublePoint retFinish = new DoublePoint(vFinish.getDoubleArray()[0],vFinish.getDoubleArray()[1]);
        DoublePoint[] ret = { retStart, retFinish};
        return ret;
    }
    private int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
    }
    private void drawLine (BufferedImage image,int xstart, int ystart, int xend, int yend, int color)
    {
        int x, y, dx, dy, incx, incy, pdx, pdy, es, el, err;

        dx = xend - xstart;
        dy = yend - ystart;

        incx = sign(dx);
        incy = sign(dy);

        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;

        if (dx > dy)
        {
            pdx = incx;	pdy = 0;
            es = dy;	el = dx;
        }
        else
        {
            pdx = 0;
            pdy = incy;
            es = dx;
            el = dy;
        }

        x = xstart;
        y = ystart;
        err = el/2;
        image.setRGB(x,y,color);



        for (int t = 0; t < el; t++)
        {
            err -= es;
            if (err < 0)
            {
                err += el;
                x += incx;
                y += incy;
            }
            else
            {
                x += pdx;
                y += pdy;
            }
            image.setRGB(x,y,color);
        }
    }
    private void finalStage(Point2D point2DStart,Point2D point2DFinish,double xMin,double xMax,double yMin,double yMax)
    {
        DoublePoint start = point2DStart.point;
        DoublePoint finish = point2DFinish.point;
        int lastCoorsX = (paneSize.width - 1);
        int lastCoorsY = (paneSize.height -1);

        int coorsXStart = (int)(lastCoorsX * (start.x - xMin) / (xMax - xMin));
        int coorsYStart = (int)(lastCoorsY * (start.y - yMin) / (yMax - yMin));

        int coorsXFinish = (int)(lastCoorsX * (finish.x - xMin) / (xMax - xMin));
        int coorsYFinish = (int)(lastCoorsY * (finish.y - yMin) / (yMax - yMin));
        drawLine(bufferedImage,coorsXStart,coorsYStart,coorsXFinish,coorsYFinish,point2DStart.color);
        //bufferedImage.getGraphics().setColor(Color.blue);
        //bufferedImage.getGraphics().setColor(surface.getColor());
       // bufferedImage.getGraphics().drawLine(0,0,500,500);
    }
    private Double3DPoint[] cameraMatrixTransform(Double3DPoint start,Double3DPoint finish)
    {
        Double3DPoint[] ret = new Double3DPoint[2];
        Matrix cameraMatrix = camera.getMatrixCamera();
        double[] vecStart = {start.x,start.y,start.z,1.0};
        double[] vecFinish = {finish.x,finish.y,finish.z,1.0};
        Vector vectorStart = new Vector(vecStart);
        Vector vectorFinish = new Vector(vecFinish);
        Vector resultStart = MathUtils.mulMatrixOnVectorTrans(cameraMatrix,vectorStart);
        Vector resultFinish = MathUtils.mulMatrixOnVectorTrans(cameraMatrix,vectorFinish);
        double[] retSA = resultStart.getDoubleArray();
        double[] retFA = resultFinish.getDoubleArray();
        Double3DPoint retS = new Double3DPoint(retSA[0],retSA[1],retSA[2]);
        Double3DPoint retF = new Double3DPoint(retFA[0],retFA[1],retFA[2]);
        ret[0] = retS;
        ret[1] = retF;
        return ret;
    }


    private Double3DPoint[] turnEuler(Double3DPoint start,Double3DPoint finish)
    {
        Double3DPoint[] ret = new Double3DPoint[2];
        if (turnsAngle == null)
        {
            ret[0] = start;
            ret[1] = finish;
            return ret;
        }
        double[] vecStart = {start.x,start.y,start.z,1.0};
        double[] vecFinish = { finish.x,finish.y,finish.z,1.0};
        Vector vectorStart = new Vector(vecStart);
        Vector vectorFinish = new Vector(vecFinish);
        rotations = new Matrix(turnsAngle[0],turnsAngle[1],turnsAngle[2]);
        Vector resultStart = MathUtils.mulMatrixOnVectorTrans(rotations,vectorStart);
        Vector resultFinish = MathUtils.mulMatrixOnVectorTrans(rotations,vectorFinish);
        double[] retSA = resultStart.getDoubleArray();
        double[] retFA = resultFinish.getDoubleArray();
        Double3DPoint retS = new Double3DPoint(retSA[0],retSA[1],retSA[2]);
        Double3DPoint retF = new Double3DPoint(retFA[0],retFA[1],retFA[2]);
        ret[0] = retS;
        ret[1] = retF;
        return ret;
    }

    public void update(Observable o, Object arg) {
        buildAScene();
    }
}
