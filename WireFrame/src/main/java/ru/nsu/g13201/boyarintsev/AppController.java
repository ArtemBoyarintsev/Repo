package ru.nsu.g13201.boyarintsev;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Артем on 14.04.2016.
 */
interface SurfaceAction
{
    void run(String name);
}
public class AppController {
    private static double PI=3.14;

    private static String DATA_PATH = ".\\Resources\\FIT_13201_Boyarintsev_WireFrame_Data";
    private ArrayList<Surface> surfaces;
    private GUI gui;

    private Scene scene;
    private FieldParams currentConfig;

    public AppController()
    {
        currentConfig = new FieldParams();
        surfaces = new ArrayList<Surface>();
    }
    Dimension sceneDimension = new Dimension(800,600);

    public void start()
    {
        MainSceneParams sceneParams = new MainSceneParams();
        Camera camera = new Camera(sceneParams.zn,sceneParams.zf,sceneParams.sw,sceneParams.sh);
        double[] turns = { 0, 0, 0 };
        scene = new Scene(camera,sceneDimension,sceneParams);
        gui = new GUI(this,scene);
    }

    public void newScene()
    {
//        Field field = new Field();
//        gui.setField(field);
//        this.field = field;
    }

    public void initState()
    {

    }

    public void save()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(DATA_PATH));
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            if (null == file)
            {
                return;
            }
            saveScene(file);
        }
    }

    public void open()
    {
        openFile();
    }

    public void changeParamsOfExistedSurface()
    {
        surfaces = scene.surfaces;
    }

    public void createNewSurface()
    {
        Surface surface = new Surface(scene.getSceneParams());
        new ParamController(scene,surface,true);
    }

    public void changeMainParams()
    {
        new MainParamController(scene);
    }
    public void changeExistedSurface()
    {
       selectAndModifySurface(new SurfaceAction() {
           public void run(String name) {
               Surface surface = scene.getSurfaceByName(name);
               if (surface == null)
               {
                   System.err.println("no existed surface with such name");
                   return;
               }
               new ParamController(scene,surface,false);
           }
       });
    }
    public void rotateSurface()
    {
        selectAndModifySurface(new SurfaceAction() {
            public void run(String name) {
                Surface surface = scene.getSurfaceByName(name);
                if (surface == null)
                {
                    System.err.println("no existed surface with such name");
                    return;
                }
                rotate(surface);
            }
        });
    }
    public void moveSurface()
    {
        selectAndModifySurface(new SurfaceAction() {
            public void run(String name) {
                Surface surface = scene.getSurfaceByName(name);
                if (surface == null)
                {
                    System.err.println("no existed surface with such name");
                    return;
                }
                move(surface);
            }
        });
    }
    public void rotateScene()
    {
        final JDialog jFrame = new JDialog();
        double[] eulerAngles = scene.getTurnsAngle();
        int RX = (int)(180*eulerAngles[0]/PI);
        int RY = (int)(180*eulerAngles[1]/PI);
        int RZ = (int)(180*eulerAngles[2]/PI);
        jFrame.setLocation(200, 200);
        jFrame.setSize(new Dimension(200,200));
        jFrame.setLayout(null);
        final JSlider sliderX = new JSlider(JSlider.HORIZONTAL, 0, 360,RX);
        final JSlider sliderY = new JSlider(JSlider.HORIZONTAL, 0, 360,RY);
        final JSlider sliderZ = new JSlider(JSlider.HORIZONTAL, 0, 360,RZ);
        ControllerUtils.settingSize(sliderX,new Point(0,0));
        ControllerUtils.settingSize(sliderY,new Point(sliderX.getX(),sliderX.getY()+sliderX.getHeight()));
        ControllerUtils.settingSize(sliderZ,new Point(sliderY.getX(),sliderY.getY()+sliderY.getHeight()));
        ChangeListener listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double[] eulerAngles = scene.getTurnsAngle();
                JSlider slider = (JSlider)e.getSource();
                Integer value = slider.getValue();
                if (slider == sliderX)
                {
                    eulerAngles[0] = PI * value / 180;
                    scene.setTurnsAngle(eulerAngles);
                }
                if (slider == sliderY) {
                    eulerAngles[1] = PI * value / 180;
                    scene.setTurnsAngle(eulerAngles);
                }
                if (slider == sliderZ) {
                    eulerAngles[2] = PI * value / 180;
                    scene.setTurnsAngle(eulerAngles);
                }
            }
        };
        sliderX.addChangeListener(listener);
        sliderY.addChangeListener(listener);
        sliderZ.addChangeListener(listener);
        jFrame.add(sliderX);
        jFrame.add(sliderY);
        jFrame.add(sliderZ);
        jFrame.setVisible(true);
    }
    public void exit()
    {

    }

    public void about()
    {
        String v = "Данное приложение \"WireFrame\" разработанно на\n";
        v += "Факультете Информационных технологий НГУ\n";
        v = v + "Версия 1.0\n";
        v = v + "(c) Бояринцев Артем\n";
        JOptionPane.showMessageDialog(null, v);
    }
    private void move (final Surface surface)
    {
        final int maxDiv = 300;
        final double maxValue = 15.0;
        final double minValue = -15.0;
        final JDialog jFrame = new JDialog();
        SurfaceParams surfaceParams = surface.getSurfaceParams();
        double CX = surfaceParams.CX;
        double CY = surfaceParams.CY;
        double CZ = surfaceParams.CZ;
        jFrame.setLocation(200, 200);
        jFrame.setSize(new Dimension(200,200));
        jFrame.setLayout(null);
        int cx = (int)(maxDiv*(CX-minValue)/(maxValue-minValue));
        int cy = (int)(maxDiv*(CY-minValue)/(maxValue-minValue));
        int cz = (int)(maxDiv*(CZ-minValue)/(maxValue-minValue));

        final JSlider sliderX = new JSlider(JSlider.HORIZONTAL, 0, maxDiv,cx);
        final JSlider sliderY = new JSlider(JSlider.HORIZONTAL, 0, maxDiv,cy);
        final JSlider sliderZ = new JSlider(JSlider.HORIZONTAL, 0, maxDiv,cz);
        ControllerUtils.settingSize(sliderX,new Point(0,0));
        ControllerUtils.settingSize(sliderY,new Point(sliderX.getX(),sliderX.getY()+sliderX.getHeight()));
        ControllerUtils.settingSize(sliderZ,new Point(sliderY.getX(),sliderY.getY()+sliderY.getHeight()));
        ChangeListener listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                SurfaceParams surfaceParams = surface.getSurfaceParams();
                JSlider slider = (JSlider)e.getSource();
                Integer value = slider.getValue();
                if (slider == sliderX) {
                    surfaceParams.CX = minValue + value *(maxValue-minValue) /maxDiv;
                    surface.changeSurfaceParams(surfaceParams);
                }
                if (slider == sliderY) {
                    surfaceParams.CY = minValue + value *(maxValue-minValue) /maxDiv;
                    surface.changeSurfaceParams(surfaceParams);
                }
                if (slider == sliderZ) {
                    surfaceParams.CZ = minValue + value *(maxValue-minValue) /maxDiv;
                    surface.changeSurfaceParams(surfaceParams);
                }
            }
        };
        sliderX.addChangeListener(listener);
        sliderY.addChangeListener(listener);
        sliderZ.addChangeListener(listener);
        jFrame.add(sliderX);
        jFrame.add(sliderY);
        jFrame.add(sliderZ);
        jFrame.setVisible(true);
    }
    private void rotate(final Surface surface)
    {
        final JDialog jFrame = new JDialog();
        SurfaceParams surfaceParams = surface.getSurfaceParams();
        int RX = (int)(180*surfaceParams.RX/PI);
        int RY = (int)(180*surfaceParams.RY/PI);
        int RZ = (int)(180*surfaceParams.RZ/PI);
        jFrame.setLocation(200, 200);
        jFrame.setSize(new Dimension(200,200));
        jFrame.setLayout(null);
        final JSlider sliderX = new JSlider(JSlider.HORIZONTAL, 0, 360,RX);
        final JSlider sliderY = new JSlider(JSlider.HORIZONTAL, 0, 360,RY);
        final JSlider sliderZ = new JSlider(JSlider.HORIZONTAL, 0, 360,RZ);
        ControllerUtils.settingSize(sliderX,new Point(0,0));
        ControllerUtils.settingSize(sliderY,new Point(sliderX.getX(),sliderX.getY()+sliderX.getHeight()));
        ControllerUtils.settingSize(sliderZ,new Point(sliderY.getX(),sliderY.getY()+sliderY.getHeight()));
        ChangeListener listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                SurfaceParams surfaceParams = surface.getSurfaceParams();
                JSlider slider = (JSlider)e.getSource();
                Integer value = slider.getValue();
                if (slider == sliderX)
                {
                    surfaceParams.RX = PI * value / 180;
                    surface.changeSurfaceParams(surfaceParams);
                }
                if (slider == sliderY) {
                    surfaceParams.RY = PI * value / 180;
                    surface.changeSurfaceParams(surfaceParams);
                }
                if (slider == sliderZ) {
                    surfaceParams.RZ = PI * value / 180;
                    surface.changeSurfaceParams(surfaceParams);
                }
            }
        };
        sliderX.addChangeListener(listener);
        sliderY.addChangeListener(listener);
        sliderZ.addChangeListener(listener);
        jFrame.add(sliderX);
        jFrame.add(sliderY);
        jFrame.add(sliderZ);
        jFrame.setVisible(true);
    }
    private void selectAndModifySurface(final SurfaceAction action)
    {
        final JDialog jFrame = new JDialog();
        jFrame.setLocation(200, 200);
        jFrame.setSize(new Dimension(200,200));
        ArrayList<Surface> surfaces = scene.getSurfaces();
        String[] names = new String[surfaces.size()];
        for (int i = 0 ; i < surfaces.size();++i)
        {
            names[i] = surfaces.get(i).getName();
        }
        JComboBox edit = new JComboBox(names);
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                String item = (String)box.getSelectedItem();
                jFrame.setVisible(false);
                action.run(item);
            }
        });
        jFrame.add(edit);
        jFrame.setVisible(true);
    }

    private void openFile()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(DATA_PATH));
        if ( fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION )
        {
            try
            {
                File file = fileChooser.getSelectedFile();
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                buildScene(bufferedReader);
//                this.field = field;
//                gui.setField(field);
                //договор дарения, по цене что там как цена от трех тысяц + 0.003 * стоимости машины
                //что надо для договора дарения вин-код где делать оценку авто.
            }
            catch (IOException ex)
            {
                System.err.println("IO error");
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void fillGeneralSceneParams(MainSceneParams sceneParams,BufferedReader reader) throws IOException
    {
        String str = reader.readLine();
        String[] generalParams = str.split(" ");
        sceneParams.n = Integer.parseInt(generalParams[0]);
        sceneParams.m = Integer.parseInt(generalParams[1]);
        sceneParams.a = Double.parseDouble(generalParams[2]);
        sceneParams.b = Double.parseDouble(generalParams[3]);
        sceneParams.c = Double.parseDouble(generalParams[4]);
        sceneParams.d = Double.parseDouble(generalParams[5]);
        str = reader.readLine();
        String[] viewParams = str.split(" ");
        sceneParams.zn = Double.parseDouble(viewParams[0]);
        sceneParams.zf = Double.parseDouble(viewParams[1]);
        sceneParams.sw = Double.parseDouble(viewParams[2]);
        sceneParams.sh = Double.parseDouble(viewParams[3]);
        str = reader.readLine();
        String[] eulerAngels = str.split(" ");
        sceneParams.ex = Double.parseDouble(eulerAngels[0]);
        sceneParams.ey = Double.parseDouble(eulerAngels[1]);
        sceneParams.ez = Double.parseDouble(eulerAngels[2]);
        str = reader.readLine();
        String[] backGroundColors = str.split(" ");
        sceneParams.br = Integer.parseInt(backGroundColors[0]);
        sceneParams.bg = Integer.parseInt(backGroundColors[1]);
        sceneParams.bb = Integer.parseInt(backGroundColors[2]);
    }

    private void saveSceneParams(MainSceneParams sceneParams,BufferedWriter fileWriter) throws IOException
    {
        Integer n = sceneParams.n;
        Integer m = sceneParams.m;
        Double a = sceneParams.a;
        Double b = sceneParams.b;
        Double c = sceneParams.c;
        Double d = sceneParams.d;
        Double zn = sceneParams.zn;
        Double zf = sceneParams.zf;
        Double sw = sceneParams.sw;
        Double sh = sceneParams.sh;

        Double ex = sceneParams.ex;
        Double ey = sceneParams.ey;
        Double ez = sceneParams.ez;

        Integer br = sceneParams.br;
        Integer bg = sceneParams.bg;
        Integer bb = sceneParams.bb;

        fileWriter.write(n.toString() +' '+m.toString()+' ' + a.toString()+' '+ b.toString()+ ' ' + c.toString() + ' '+d.toString());
        fileWriter.newLine();
        fileWriter.write(zn.toString() +  ' ' + zf.toString() + ' '+ sw.toString() +  ' ' + sh.toString());
        fileWriter.newLine();
        fileWriter.write(ex.toString() + ' ' + ey.toString() + ' ' + ez.toString());
        fileWriter.newLine();
        fileWriter.write(br.toString() + ' ' + bg.toString() + ' ' + bb.toString());
        fileWriter.newLine();
    }

    private void saveSpline(SplineField field, BufferedWriter writer) throws IOException
    {
        ArrayList<DoublePoint> mainDots = field.getMainDots();
        Integer N = mainDots.size();
        writer.write(N.toString());
        writer.newLine();
        for ( int i =  0; i < mainDots.size(); ++i)
        {
            DoublePoint point = mainDots.get(i);
            Double x = point.x;
            Double y = point.y;
            writer.write(x.toString() +  ' ' + y.toString());
            writer.newLine();
        }
    }

    private void saveSurface(Surface surface, BufferedWriter writer) throws IOException
    {
        SurfaceParams surfaceParams = surface.getSurfaceParams();
        SplineField splineField = surfaceParams.splineField;

        String name = surfaceParams.name;

        Integer r = surfaceParams.R;
        Integer g = surfaceParams.G;
        Integer b = surfaceParams.B;

        Double cx = surfaceParams.CX;
        Double cy = surfaceParams.CY;
        Double cz = surfaceParams.CZ;

        Double rx = surfaceParams.RX;
        Double ry = surfaceParams.RY;
        Double rz = surfaceParams.RZ;

        writer.write(name);
        writer.newLine();

        writer.write(r.toString() + ' ' +  g.toString() + ' ' + b.toString());
        writer.newLine();

        writer.write(cx.toString() + ' ' +  cy.toString() + ' ' + cz.toString());
        writer.newLine();

        writer.write(rx.toString() + ' ' +  ry.toString() + ' ' + rz.toString());
        writer.newLine();

        saveSpline(splineField,writer);
    }

    private void saveScene(File file )
    {
        try
        {
            FileWriter writer = new FileWriter(file);
            BufferedWriter fileWriter = new BufferedWriter(writer);
            MainSceneParams sceneParams = scene.getSceneParams();
            saveSceneParams(sceneParams,fileWriter);
            ArrayList<Surface> surfaces = scene.getSurfaces();
            Integer k = surfaces.size();
            fileWriter.write(k.toString());
            fileWriter.newLine();

            for(Surface surface: surfaces)
            {
                saveSurface(surface,fileWriter);
            }
            fileWriter.close();
        }
        catch(IOException er)
        {
            er.printStackTrace();
        }
    }
    private void buildScene(BufferedReader reader)
    {
        try {
            MainSceneParams sceneParams = new MainSceneParams();
            fillGeneralSceneParams(sceneParams,reader);

            scene.clearScene();
            scene.changeMainParams(sceneParams);
            //gui.setScene(scene);
            String str = reader.readLine();
            int k = Integer.parseInt(str);
            for (int i = 0; i < k; ++i)
            {
                SurfaceParams surfaceParams = new SurfaceParams();
                str=reader.readLine();
                surfaceParams.name = str;
                str = reader.readLine();
                String[] colors = str.split(" ");
                surfaceParams.R = Integer.parseInt(colors[0]);
                surfaceParams.G = Integer.parseInt(colors[1]);
                surfaceParams.B = Integer.parseInt(colors[2]);
                str = reader.readLine();
                String[] position = str.split(" ");
                surfaceParams.CX = Double.parseDouble(position[0]);
                surfaceParams.CY = Double.parseDouble(position[1]);
                surfaceParams.CZ = Double.parseDouble(position[2]);
                str = reader.readLine();
                String[] eulerAngels = str.split(" ");
                surfaceParams.RX = Double.parseDouble(eulerAngels[0]);
                surfaceParams.RY = Double.parseDouble(eulerAngels[1]);
                surfaceParams.RZ = Double.parseDouble(eulerAngels[2]);
                str = reader.readLine();
                SplineField splineField = new SplineField();
                int N = Integer.parseInt(str);
                //surfaceParams.coordinates = new DoublePoint[N];
                for (int j = 0; j < N; ++j)
                {
                    str = reader.readLine();
                    String[] bSplineDot = str.split(" ");
                    double d1 = Double.parseDouble(bSplineDot[0]);
                    double d2 = Double.parseDouble(bSplineDot[1]);
                    splineField.addMainDotByUser(new DoublePoint(d1,d2));
                }
                surfaceParams.splineField = splineField;
                Surface surface = new Surface(sceneParams,surfaceParams);
                scene.addSurfaceWithOutDrawing(surface);
            }
            scene.build();
        }
        catch (NumberFormatException er)
        {
            System.exit(21);
        }
        catch(IOException er)
        {
            System.exit(21);
        }
    }
}
