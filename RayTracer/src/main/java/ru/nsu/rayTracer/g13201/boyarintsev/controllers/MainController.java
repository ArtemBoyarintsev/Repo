package ru.nsu.rayTracer.g13201.boyarintsev.controllers;

import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.*;
import ru.nsu.rayTracer.g13201.boyarintsev.models.*;
import ru.nsu.rayTracer.g13201.boyarintsev.models.meshes.*;
import ru.nsu.rayTracer.g13201.boyarintsev.models.meshes.Box;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.PrimaryScene;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.iScene;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.rayTracerScene.RayTracerScene;
import ru.nsu.rayTracer.g13201.boyarintsev.views.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;


public class MainController {
    private static final String DATA_PATH = ".\\Resources\\FIT_13201_Boyarintsev_RayTracer_Data";

    private JLabel backGroundRedLabel = new JLabel("Фон Красный");
    private JLabel backGroundGreenLabel = new JLabel("Фон Зеленый");
    private JLabel backGroundBlueLabel = new JLabel("Фон Голубой");
    private JLabel gammaLabel = new JLabel("Гамма");
    private JLabel traceDepthLabel = new JLabel("Глубина трассировки");
    private JLabel qualityLabel = new JLabel("Качество изображения");
    private JLabel cameraPositionLabel = new JLabel("Положение камеры");
    private JLabel viewPositionLabel = new JLabel("Позиция view");
    private JLabel vectorUPLabel = new JLabel("Вектор up");
    private JLabel znLabel = new JLabel("Ближняя панель");
    private JLabel zfLabel = new JLabel("дальная панель");
    private JLabel swLabel = new JLabel("ширина б.п.");
    private JLabel shLabel = new JLabel("высота б.п.");

    private JTextField backGroundRedText = new JTextField("0");
    private JTextField backGroundGreenText = new JTextField("0");
    private JTextField backGroundBlueText = new JTextField("0");
    private JTextField gammaText = new JTextField("2",3);
    private JTextField traceDepthText = new JTextField("3",3);
    private JTextField qualityText = new JTextField("normal");
    private JTextField cameraPositionXText = new JTextField("13",3);
    private JTextField cameraPositionYText = new JTextField("0",3);
    private JTextField cameraPositionZText = new JTextField("0",3);
    private JTextField viewPositionXText = new JTextField("0",3);
    private JTextField viewPositionYText = new JTextField("0",3);
    private JTextField viewPositionZText = new JTextField("0",3);

    private JTextField vectorUPXText = new JTextField("0",3);
    private JTextField vectorUPYText = new JTextField("1",3);
    private JTextField vectorUPZText = new JTextField("0",3);

    private JTextField znText = new JTextField("1.0",4);
    private JTextField zfText = new JTextField("13.0");
    private JTextField swText = new JTextField("75",3);
    private JTextField shText = new JTextField("50",3);


    public MainController()
    {

    }

    public void savePicture(iScene scene)
    {
        try
        {
            File file = openFile();
            if (file == null)
            {
                return;
            }
            FileOutputStream f = new FileOutputStream(file);
            scene.save(f);
            f.close();
        }
        catch(IOException e)
        {
            System.err.println("IO error");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void saveRenderSettings()
    {
        try
        {
            File file = openFile();
            if (file == null)
            {
                return;
            }
            BufferedWriter f = new BufferedWriter(new FileWriter(file));
            sceneParamsHolding.renderSettings.save(f);
            f.close();
        }
        catch(IOException e)
        {
            System.err.println("IO error");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void modeChangeCameraPosition()
    {
        if (primaryScene != null) {
            primaryScene.setCamera(new Camera(sceneParamsHolding.renderSettings));
            gui.setScene(primaryScene);
        }
    }
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void cameraZoom(double k) {
        if (null == primaryScene) {
            return;
        }
        Camera camera = primaryScene.getCamera();
        double zn = camera.getZn();
        double zf = camera.getZf();
        if (zf > k*zn && k*zn > 0.25)
        {
            camera.setZn(k*zn);
        }
        primaryScene.sceneBuilding();
    }

    public void cameraMovementAmongEyeViewVector(double grad)
    {
        if (primaryScene == null){
            return;
        }
        RenderSettings settings = sceneParamsHolding.renderSettings;
        Vector3 direction = MathUtils.getNormalizeVector(MathUtils.subtract(settings.viewPosition,settings.cameraPosition));
        Vector3 moveVector = new Vector3(direction,grad);
        settings.cameraPosition = new Double3DPoint(MathUtils.moveDouble3DPoint(settings.cameraPosition,moveVector));
        primaryScene.setCamera(new Camera(settings));
        primaryScene.sceneBuilding();
    }

    public void cameraMovement(int code) {
        if (null == primaryScene)
        {
            return;
        }
        RenderSettings renderSettings = sceneParamsHolding.renderSettings;
        Camera camera = primaryScene.getCamera();
        Vector3 direction;
        switch (code)
        {
            case KeyEvent.VK_RIGHT:
                direction = new Vector3(new double[]{-1,0,0});
                break;
            case KeyEvent.VK_DOWN:
                direction = new Vector3(new double[]{0,-1,0});
                break;
            case KeyEvent.VK_UP:
                direction = new Vector3(new double[]{0,1,0});
                break;
            case KeyEvent.VK_LEFT:
                direction = new Vector3(new double[]{1,0,0});
                break;
            default:
                return;
        }
        //сейчас direction в координатах камеры, а позиция камера в мировых координатах.
        Double3DPoint end = MathUtils.mulMatrixOnVector(camera.getMatrixCameraReverse(),new Vector4(direction,1)).get3DPoint();
        Double3DPoint start = MathUtils.mulMatrixOnVector(camera.getMatrixCameraReverse(),new Vector4(new double[]{0,0,0,1})).get3DPoint();
        direction = MathUtils.subtract(end,start);
        renderSettings.cameraPosition = MathUtils.moveDouble3DPoint(renderSettings.cameraPosition,direction);
        renderSettings.viewPosition = MathUtils.moveDouble3DPoint(renderSettings.viewPosition,direction);
        primaryScene.setCamera(new Camera(renderSettings));
        primaryScene.sceneBuilding();
    }

    public void mouseReleased()
    {
        if (mouseSpanner !=null)
            mouseSpanner.stopRotate();
    }
    public void cameraRotation(MouseEvent event)
    {
        if (primaryScene == null)
        {
            return;
        }
        if (mouseSpanner == null)
        {
            mouseSpanner = new MouseSpanner(event.getPoint());
            return;
        }
        Point point  = event.getPoint();
        RenderSettings renderSettings = sceneParamsHolding.renderSettings;
        Matrix4x4 rotation = mouseSpanner.getRotationMatrix(point);
        Matrix4x4 cameraMatrixTransform = primaryScene.getCamera().getMatrixCameraReverse();
        Matrix4x4 cameraMatrix = primaryScene.getCamera().getMatrixCamera();
        Matrix4x4 mul = MathUtils.mulMatrixOnMatrix(MathUtils.mulMatrixOnMatrix(cameraMatrixTransform,rotation),cameraMatrix);
        Double3DPoint cameraPosition = renderSettings.cameraPosition;
        cameraPosition = MathUtils.mulMatrixOnVector(mul,new Vector4(cameraPosition,1)).get3DPoint();
        Double3DPoint nullPosition = MathUtils.mulMatrixOnVector(mul,new Vector4(new double[]{0,0,0,1})).get3DPoint();
        renderSettings.cameraPosition = MathUtils.subtract(cameraPosition,nullPosition).get3DPoint();
        primaryScene.setCamera(new Camera(renderSettings));
        primaryScene.sceneBuilding();
    }

    private void labelsInit(RenderSettings renderSettings)
    {
        int x = 0, y = 1, z = 2;
        backGroundRedText.setText(""+renderSettings.backGroundColor.getRed());
        backGroundGreenText.setText(""+renderSettings.backGroundColor.getGreen());
        backGroundBlueText.setText(""+renderSettings.backGroundColor.getBlue());
        gammaText.setText(""+renderSettings.gamma);
        traceDepthText.setText(""+renderSettings.depth);
        qualityText.setText(""+renderSettings.quality.toString());

        cameraPositionXText.setText(""+ControllerUtils.getDoubleString(renderSettings.cameraPosition.getX(),4));
        cameraPositionYText.setText(""+ControllerUtils.getDoubleString(renderSettings.cameraPosition.getY(),4));
        cameraPositionZText.setText(""+ControllerUtils.getDoubleString(renderSettings.cameraPosition.getZ(),4));

        viewPositionXText.setText(""+ControllerUtils.getDoubleString(renderSettings.viewPosition.getX(),4));
        viewPositionYText.setText(""+ControllerUtils.getDoubleString(renderSettings.viewPosition.getY(),4));
        viewPositionZText.setText(""+ControllerUtils.getDoubleString(renderSettings.viewPosition.getZ(),4));

        vectorUPXText.setText(""+ControllerUtils.getDoubleString(renderSettings.up.get(x),4));
        vectorUPYText.setText(""+ControllerUtils.getDoubleString(renderSettings.up.get(y),4));
        vectorUPZText.setText(""+ControllerUtils.getDoubleString(renderSettings.up.get(z),4));

        znText.setText(""+ControllerUtils.getDoubleString(renderSettings.zn,4));
        zfText.setText(""+ControllerUtils.getDoubleString(renderSettings.zf,4));
        swText.setText(""+ControllerUtils.getDoubleString(renderSettings.sw,4));
        shText.setText(""+ControllerUtils.getDoubleString(renderSettings.sh,4));
    }

    private File openFile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(DATA_PATH));
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public void openRenderSettingsFile() {
        try {
            File file = openFile();
            if (null == file)
            {
                return;
            }
            BufferedReader renderSettingsReader = new BufferedReader(new FileReader(file));
            this.sceneParamsHolding.renderSettings = readRenderSettings(renderSettingsReader);
        } catch (IOException ex) {
            System.err.println("IO error");
            ex.printStackTrace();
            System.exit(1);
        }
    }


    public void openSceneFile() {
        try {
            File file = openFile();
            if (null == file)
            {
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            loadNewPrimaryScene(bufferedReader);
            //договор дарения, по цене что там как цена от трех тысяц + 0.003 * стоимости машины
            //что надо для договора дарения вин-код где делать оценку авто.
        } catch (IOException ex) {
            System.err.println("IO error");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void renderSettings()
    {
        createSettingsMenu(new OkCancelPressed(new Runnable() {
            public void run() {
                checkParams();
            }
        }));
    }
    private void createSettingsMenu(final OkCancelPressed runIfOkPress)
    {
        final JDialog jDialog = new JDialog();
        JPanel panel = new JPanel();
        panel.setLayout(null);

        jDialog.setTitle("Настройки Renderingа");
        jDialog.setLocation(200,200);
        jDialog.setSize(new Dimension(380,240));

        labelsInit(sceneParamsHolding.renderSettings);

        ControllerUtils.settingSize(backGroundRedLabel,new Point(20,20));
        ControllerUtils.settingSize(backGroundRedText, new Point(backGroundRedLabel.getX()+backGroundRedLabel.getWidth(),backGroundRedLabel.getY()));

        ControllerUtils.settingSize(backGroundGreenLabel,new Point(backGroundRedLabel.getX(),backGroundRedLabel.getY()+backGroundRedLabel.getHeight()+5));
        ControllerUtils.settingSize(backGroundGreenText, new Point(backGroundGreenLabel.getX()+backGroundGreenLabel.getWidth(),backGroundGreenLabel.getY()));

        ControllerUtils.settingSize(backGroundBlueLabel,new Point(backGroundGreenLabel.getX(),backGroundGreenLabel.getY()+backGroundGreenLabel.getHeight()+5));
        ControllerUtils.settingSize(backGroundBlueText, new Point(backGroundBlueLabel.getX()+backGroundBlueLabel.getWidth(),backGroundBlueLabel.getY()));

        ControllerUtils.settingSize(cameraPositionLabel,new Point(backGroundRedText.getX()+backGroundRedText.getWidth()+5,backGroundRedText.getY()));
        ControllerUtils.settingSize(cameraPositionXText,new Point(cameraPositionLabel.getX()+cameraPositionLabel.getWidth(),cameraPositionLabel.getY()));
        ControllerUtils.settingSize(cameraPositionYText,new Point(cameraPositionXText.getX()+cameraPositionXText.getWidth(),cameraPositionXText.getY()));
        ControllerUtils.settingSize(cameraPositionZText,new Point(cameraPositionYText.getX()+cameraPositionYText.getWidth(),cameraPositionYText.getY()));

        ControllerUtils.settingSize(viewPositionLabel,new Point(cameraPositionLabel.getX(),cameraPositionLabel.getY()+cameraPositionLabel.getHeight()+5));
        ControllerUtils.settingSize(viewPositionXText,new Point(viewPositionLabel.getX()+viewPositionLabel.getWidth(),viewPositionLabel.getY()));
        ControllerUtils.settingSize(viewPositionYText,new Point(viewPositionXText.getX()+viewPositionXText.getWidth(),viewPositionXText.getY()));
        ControllerUtils.settingSize(viewPositionZText,new Point(viewPositionYText.getX()+viewPositionYText.getWidth(),viewPositionYText.getY()));

        ControllerUtils.settingSize(vectorUPLabel,new Point(viewPositionLabel.getX(),viewPositionLabel.getY()+viewPositionLabel.getHeight()+5));
        ControllerUtils.settingSize(vectorUPXText,new Point(vectorUPLabel.getX()+vectorUPLabel.getWidth(),vectorUPLabel.getY()));
        ControllerUtils.settingSize(vectorUPYText,new Point(vectorUPXText.getX()+vectorUPXText.getWidth(),vectorUPXText.getY()));
        ControllerUtils.settingSize(vectorUPZText,new Point(vectorUPYText.getX()+vectorUPYText.getWidth(),vectorUPYText.getY()));

        ControllerUtils.settingSize(gammaLabel, new Point(backGroundBlueLabel.getX(),backGroundBlueLabel.getY()+backGroundBlueLabel.getHeight()+5));
        ControllerUtils.settingSize(gammaText, new Point(gammaLabel.getX()+gammaLabel.getWidth()+3,gammaLabel.getY()));
        ControllerUtils.settingSize(traceDepthLabel,new Point(gammaText.getX()+gammaText.getWidth()+3,gammaLabel.getY()));
        ControllerUtils.settingSize(traceDepthText, new Point(traceDepthLabel.getX()+traceDepthLabel.getWidth(),traceDepthLabel.getY()));

        ControllerUtils.settingSize(qualityLabel,new Point(gammaLabel.getX(),gammaLabel.getY()+gammaLabel.getHeight()+5));
        ControllerUtils.settingSize(qualityText, new Point(qualityLabel.getX()+qualityLabel.getWidth(),qualityLabel.getY()));

        ControllerUtils.settingSize(znLabel, new Point(qualityLabel.getX(),qualityLabel.getY()+qualityLabel.getHeight()+5));
        ControllerUtils.settingSize(znText, new Point(znLabel.getX()+znLabel.getWidth(),znLabel.getY()));
        ControllerUtils.settingSize(zfLabel, new Point(znText.getX()+znText.getWidth(),znText.getY()));
        ControllerUtils.settingSize(zfText,new Point(zfLabel.getX()+zfLabel.getWidth(),zfLabel.getY()));

        ControllerUtils.settingSize(swLabel, new Point(znLabel.getX(),znLabel.getY()+znLabel.getHeight()+5));
        ControllerUtils.settingSize(swText, new Point(swLabel.getX()+swLabel.getWidth(),swLabel.getY()));
        ControllerUtils.settingSize(shLabel, new Point(swText.getX()+swText.getWidth()+3,swLabel.getY()));
        ControllerUtils.settingSize(shText, new Point(shLabel.getX()+shLabel.getWidth(),shLabel.getY()));

        panel.add(backGroundBlueLabel);
        panel.add(backGroundRedLabel);
        panel.add(backGroundGreenLabel);
        panel.add(backGroundBlueText);
        panel.add(backGroundRedText);
        panel.add(backGroundGreenText);
        panel.add(cameraPositionLabel);
        panel.add(cameraPositionXText);
        panel.add(cameraPositionYText);
        panel.add(cameraPositionZText);
        panel.add(viewPositionLabel);
        panel.add(viewPositionXText);
        panel.add(viewPositionYText);
        panel.add(viewPositionZText);
        panel.add(vectorUPLabel);
        panel.add(vectorUPXText);
        panel.add(vectorUPYText);
        panel.add(vectorUPZText);
        panel.add(gammaLabel);
        panel.add(gammaText);
        panel.add(traceDepthLabel);
        panel.add(traceDepthText);
        panel.add(qualityLabel);
        panel.add(qualityText);
        panel.add(znLabel);
        panel.add(znText);
        panel.add(zfLabel);
        panel.add(zfText);
        panel.add(swLabel);
        panel.add(swText);
        panel.add(shLabel);
        panel.add(shText);
        ControllerUtils.addButtons(swText,runIfOkPress,jDialog,panel);
        jDialog.setContentPane(panel);
        jDialog.setVisible(true);
    }

    private void checkParams()
    {
        String redBack = backGroundRedText.getText();
        String greenBack = backGroundGreenText.getText();
        String blueBack = backGroundBlueText.getText();

        String cameraX = cameraPositionXText.getText();
        String cameraY = cameraPositionYText.getText();
        String cameraZ = cameraPositionZText.getText();

        String viewX = viewPositionXText.getText();
        String viewY = viewPositionYText.getText();
        String viewZ = viewPositionZText.getText();

        String upX = vectorUPXText.getText();
        String upY = vectorUPYText.getText();
        String upZ = vectorUPZText.getText();

        String znTextText = znText.getText();
        String zfTextText = zfText.getText();
        String swTextT = swText.getText();
        String shTextT = shText.getText();

        String gammaTextT = gammaText.getText();
        String traceDText = traceDepthText.getText();
        String quality = qualityText.getText();

        RenderSettings renderSettings = new RenderSettings();
        try
        {
            int red = Integer.parseInt(redBack);
            int green = Integer.parseInt(greenBack);
            int blue = Integer.parseInt(blueBack);
            renderSettings.backGroundColor = new Color(red,green,blue);
            double cX = Double.parseDouble(cameraX);
            double cY = Double.parseDouble(cameraY);
            double cZ = Double.parseDouble(cameraZ);
            renderSettings.cameraPosition = new Double3DPoint(cX,cY,cZ);
            double vX = Double.parseDouble(viewX);
            double vY = Double.parseDouble(viewY);
            double vZ = Double.parseDouble(viewZ);
            renderSettings.viewPosition = new Double3DPoint(vX,vY,vZ);
            double uX = Double.parseDouble(upX);
            double uY = Double.parseDouble(upY);
            double uZ = Double.parseDouble(upZ);
            renderSettings.up = new Vector3(new Double3DPoint(uX,uY,uZ));
            renderSettings.zn = Double.parseDouble(znTextText);
            renderSettings.zf = Double.parseDouble(zfTextText);
            renderSettings.sw = Double.parseDouble(swTextT);
            renderSettings.sh = Double.parseDouble(shTextT);
            renderSettings.quality = RenderSettings.Quality.valueOf(quality);
            renderSettings.depth = Integer.parseInt(traceDText);
            renderSettings.gamma = Integer.parseInt(gammaTextT);
            sceneParamsHolding.renderSettings = renderSettings;
        }
        catch(IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }

    }
    public void rayTracerRendering() {
        RayTracerScene rayTracerScene = createNewRayTracerScene();
        rayTracerScene.sceneBuilding();
        gui.setScene(rayTracerScene);
    }

    public void about()
    {
        String v = "Данная задача RayTracer разработана на\n";
        v += "Факультете Информационных технологий НГУ\n";
        v += "В рамках курса \"Инженерная и компьютерная графика\" \n";
        v = v + "Версия 1.0\n";
        v = v + "(c) Бояринцев Артем\n";
        JOptionPane.showMessageDialog(null, v);
    }

    private RenderSettings readRenderSettings(BufferedReader reader) throws IOException
    {
        RenderSettings renderSettings = new RenderSettings();
        renderSettings.backGroundColor = getColor(reader);
        renderSettings.gamma = readInteger(reader);
        renderSettings.depth = readInteger(reader);
        renderSettings.quality = RenderSettings.Quality.valueOf(reader.readLine());
        renderSettings.cameraPosition = getDouble3DPoint(reader);
        renderSettings.viewPosition = getDouble3DPoint(reader);
        renderSettings.up = readVector(reader);
        renderSettings.zn = readDouble(reader);
        renderSettings.zf = readDouble(reader);
        renderSettings.sw = readDouble(reader);
        renderSettings.sh = readDouble(reader);
        return renderSettings;
    }

    private double readDouble(BufferedReader reader) throws IOException
    {
        String str = reader.readLine();
        return Double.parseDouble(str);
    }
    private Vector3 readVector(BufferedReader reader) throws  IOException
    {
        return new Vector3(getDouble3DPoint(reader));
    }
    private Double3DPoint getDouble3DPoint(BufferedReader reader) throws IOException
    {
        String str = reader.readLine();
        String[] par = str.split(" ");
        double x = Double.parseDouble(par[0]);
        double y = Double.parseDouble(par[1]);
        double z = Double.parseDouble(par[2]);
        return new Double3DPoint(x,y,z);
    }

    private Integer readInteger(BufferedReader reader) throws IOException
    {
        String str = reader.readLine();
        return Integer.parseInt(str);
    }

    private Color getColor(BufferedReader reader) throws IOException
    {
        String str = reader.readLine();
        String[] cs = str.split(" ");
        return getColor(cs);
    }
    private RayTracerScene createNewRayTracerScene()
    {
        if (primaryScene == null)
        {
            throw  new IllegalArgumentException("В начале необходимо загрузить первичную сцену.");
        }
        SceneLights sceneLights = sceneParamsHolding.sceneLights;
        RenderSettings renderSettings = sceneParamsHolding.renderSettings;
        Camera rayTracerSceneCamera = new Camera(renderSettings);
        Dimension dimension = gui.getContentPane().getSize();
        RayTracerScene rayTracerScene = new RayTracerScene(rayTracerSceneCamera,dimension,sceneLights,renderSettings);
        copyAllObjectsFromPrimaryScene(rayTracerScene);
        return rayTracerScene;
    }

    private void copyAllObjectsFromPrimaryScene(RayTracerScene rayTracerScene)
    {
        List<SceneObject> sceneObjects = primaryScene.getSceneObjects();
        for (SceneObject sceneObject : sceneObjects)
        {
            rayTracerScene.addSceneObject(sceneObject);
        }
    }

    private Sphere readSphere(BufferedReader reader) throws IOException
    {
        String str = reader.readLine();
        String[] generalParams = str.split(" ");
        double cx = Double.parseDouble(generalParams[0]);
        double cy = Double.parseDouble(generalParams[1]);
        double cz = Double.parseDouble(generalParams[2]);
        str = reader.readLine();
        double radius = Double.parseDouble(str);
        return new Sphere(new Double3DPoint(cx,cy,cz),radius);
    }

    private ru.nsu.rayTracer.g13201.boyarintsev.models.meshes.Box readBox(BufferedReader reader) throws IOException
    {
        String str = reader.readLine();
        String[] generalParams = str.split(" ");
        double minX = Double.parseDouble(generalParams[0]);
        double minY = Double.parseDouble(generalParams[1]);
        double minZ = Double.parseDouble(generalParams[2]);
        str = reader.readLine();
        generalParams = str.split(" ");
        double maxX = Double.parseDouble(generalParams[0]);
        double maxY = Double.parseDouble(generalParams[1]);
        double maxZ = Double.parseDouble(generalParams[2]);
        return new Box(new Double3DPoint(minX,minY,minZ),new Double3DPoint(maxX,maxY,maxZ));
    }

    private Double3DPoint[] readSimple2DFigure(BufferedReader reader,int count) throws IOException
    {
        Double3DPoint[] double3DPoints = new Double3DPoint[count];
        for (int i = 0 ; i  < count ; ++i)
        {
            String str = reader.readLine();
            String[] generalParams = str.split(" ");
            double cx = Double.parseDouble(generalParams[0]);
            double cy = Double.parseDouble(generalParams[1]);
            double cz = Double.parseDouble(generalParams[2]);
            double3DPoints[i] = new Double3DPoint(cx,cy,cz);
        }
        return double3DPoints;
    }

    private OpticalCharacteristics readOpticalCharacteristics(BufferedReader reader ) throws IOException
    {
        OpticalCharacteristics opticalCharacteristics = new OpticalCharacteristics();
        String str = reader.readLine();
        String[] generalParams = str.split(" ");
        opticalCharacteristics.redDiffuse = Double.parseDouble(generalParams[0]);
        opticalCharacteristics.greenDiffuse = Double.parseDouble(generalParams[1]);
        opticalCharacteristics.blueDiffuse = Double.parseDouble(generalParams[2]);
        opticalCharacteristics.redMirror = Double.parseDouble(generalParams[3]);
        opticalCharacteristics.greenMirror = Double.parseDouble(generalParams[4]);
        opticalCharacteristics.blueMirror = Double.parseDouble(generalParams[5]);
        opticalCharacteristics.power = Double.parseDouble(generalParams[6]);
        return opticalCharacteristics;
    }
    private Color getColor(String[] components)
    {
        if (components.length != 3)
        {
            throw new IllegalArgumentException("Ошибка создания цвета: требуется 3 компоненты цвета");
        }
        int[] ints = new int[3];
        for (int i = 0 ; i < components.length; ++i)
        {
            ints[i] = Integer.parseInt(components[i]);
        }
        return new Color(ints[0],ints[1],ints[2]);
    }

    private Double3DPoint getPosition(String[] components)
    {
        if (components.length != 3)
        {
            throw new IllegalArgumentException("Ошибка создания цвета: требуется 3 компоненты цвета");
        }
        double[] doubles = new double[3];
        for (int i = 0 ; i < components.length; ++i)
        {
            doubles[i] = Double.parseDouble(components[i]);
        }
        return new Double3DPoint(doubles[0],doubles[1],doubles[2]);
    }

    private PointLight getPointLight(String[] pointLightParams)
    {
        int len = pointLightParams.length;
        if (len != 6)
        {
            throw  new IllegalArgumentException("Для создания точечного источника требуется 6 параметров, а встретилось "+len);
        }
        String[] position = {pointLightParams[0],pointLightParams[1],pointLightParams[2] };
        String[] color = {pointLightParams[3], pointLightParams[4], pointLightParams[5] };
        PointLight pointLight = new PointLight();
        pointLight.position = getPosition(position);
        pointLight.color = getColor(color);
        return pointLight;
    }

    private SceneLights readSceneHeader(BufferedReader reader) throws IOException
    {
        SceneLights sceneLights = new SceneLights();
        String str = reader.readLine();
        String[] diffuseLight = str.split(" ");
        sceneLights.diffuseColor = getColor(diffuseLight);
        str = reader.readLine();
        int lightCount = Integer.parseInt(str);
        for (int i =0 ; i < lightCount; ++i)
        {
            str = reader.readLine();
            String[] pointLightParams = str.split(" ");
            PointLight pointLight = getPointLight(pointLightParams);
            sceneLights.pointLights.add(pointLight);
        }
        return sceneLights;
    }

    private PrimaryScene readSceneSection(BufferedReader reader) throws IOException
    {
        Dimension dimension = gui.getContentPane().getSize();
        RenderSettings renderSettings = sceneParamsHolding.renderSettings;
        renderSettings.sw = dimension.getWidth() / 10;
        renderSettings.sh = dimension.getHeight() / 10;
        Camera camera =  new Camera(renderSettings);
        primaryScene = new PrimaryScene(camera,dimension);
        String str = reader.readLine();
        int primitiveCount = Integer.parseInt(str);
        for (int i = 0  ; i  < primitiveCount; ++i)
        {
            str = reader.readLine();
            Mesh mesh;
            if (str.toUpperCase().equals("SPHERE")) {
                mesh = readSphere(reader);
            } else if (str.toUpperCase().equals("BOX")) {
                mesh = readBox(reader);
            } else if (str.toUpperCase().equals("TRIANGLE")) {
                Double3DPoint[] double3DPoints = readSimple2DFigure(reader, 3);
                mesh = new Triangle(double3DPoints[0], double3DPoints[1], double3DPoints[2]);
            } else if (str.toUpperCase().equals("SQUARE")){
                Double3DPoint[] double3DPoints = readSimple2DFigure(reader, 4);
                mesh = new Square(double3DPoints[0], double3DPoints[1], double3DPoints[2], double3DPoints[3]);
            }
            else
            {
                throw new IllegalArgumentException();
            }
            OpticalCharacteristics opticalCharacteristics = readOpticalCharacteristics(reader);
            SceneObject sceneObject = new SceneObject(mesh, opticalCharacteristics);
            primaryScene.addSceneObject(sceneObject);
        }
        return primaryScene;
    }

    private void loadNewPrimaryScene(BufferedReader reader) throws IOException
    {
        sceneParamsHolding.sceneLights = readSceneHeader(reader);
        PrimaryScene primaryScene = readSceneSection(reader);
        primaryScene.sceneBuilding();
        gui.setScene(primaryScene);
    }


    private GUI gui;
    private MouseSpanner mouseSpanner = null;
    private PrimaryScene primaryScene = null;
    private SceneParamsHolding sceneParamsHolding = new SceneParamsHolding();
}
