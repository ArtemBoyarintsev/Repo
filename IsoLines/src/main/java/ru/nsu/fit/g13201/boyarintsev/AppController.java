package ru.nsu.fit.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;

/**
 * Created by Артем on 24.03.2016.
 */
public class AppController
{
    private static Dimension windowSize = new Dimension(600,600);
    private static Dimension functionMapZoneSize = new Dimension(windowSize.width/2, windowSize.height*3/4);
    private final static String DATA_PATH = ".\\Resources\\FIT_13201_Boyarintsev_IsoLines_Data";
    private final Double X_MIN = -100.0;
    private final Double X_MAX = 100.0;
    private final Double Y_MIN = -100.0;
    private final Double Y_MAX = 100.0;
    private final Integer GRID_MIN = 2;
    private final Integer GRID_MAX = 100;

    private Dimension paramDimension = new Dimension(200,180);
    private final JLabel xMinLabel = new JLabel("X min: ");
    private final JTextField xMinText = new JTextField(X_MIN.toString(),5);

    private final JLabel xMaxLabel = new JLabel("X max:");
    private final JTextField xMaxText = new JTextField(X_MAX.toString(),5);

    private final  JLabel yMinLabel = new JLabel("Y min:  ");
    private final  JTextField yMinText = new JTextField(Y_MIN.toString(),5);

    private final JLabel yMaxLabel = new JLabel("Y max:");
    private final  JTextField yMaxText = new JTextField(Y_MAX.toString(),5);

    private final JLabel gridXLabel = new JLabel("grid X: ");
    private final  JTextField gridXText = new JTextField(GRID_MAX.toString(),5);

    private final JLabel gridYLabel = new JLabel("grid Y: ");
    private final  JTextField gridYText = new JTextField(GRID_MAX.toString(),5);

    private boolean gridShow = false;
    private boolean differenceLevel = false;
    private boolean extraIsoLinesDraw = false;
    private boolean defaultIsoLinesShow = false;

    private Params createDefaultParams()
    {
        int n = 5;
        Params params = new Params();

        params.isoLinesColor = new Color(0,0,0).getRGB();
        params.gridX = 5;
        params.gridY = 5;
        params.windowHeight = functionMapZoneSize.height;
        params.windowWidth = functionMapZoneSize.width;

        int[] colors = new int[n];
        Color c[] = { new Color(255,255,255), new Color(255,0,0), new Color(0,255,0), new Color(0,0,255), new Color(0,255,255)};
        for (int i = 0 ; i < n ;++i)
        {
            colors[i] = c[i].getRGB();
        }
        params.colors = colors;
        return params;
    }

    public AppController()
    {
        Params params = createDefaultParams();
        Field field = new Field(params);
        this.field = field;
        GUI gui = new GUI(this,field,windowSize);
        this.gui = gui;
    }
    public void deferenceLevel()
    {
        differenceLevel = !differenceLevel;
    }

    public static void main(String[] args)
    {
        AppController appController = new AppController();
    }

    public void open()
    {
        openFile();
    }

    public void about()
    {
        String v = "Данное приложение \"IsoLines\" разработанно на\n";
        v += "Факультете Информационных технологий НГУ\n";
        v = v + "Версия 1.0\n";
        v = v + "(c) Бояринцев Артем\n";
        JOptionPane.showMessageDialog(null, v);
    }

    public void exit()
    {
        System.exit(0);
    }

    public void interpol()
    {
        field.setInterpol(true);
        gui.resetField();
    }
    public void isoLinesShow()
    {
        defaultIsoLinesShow = !defaultIsoLinesShow;
        field.drawDefaultIsoLines(defaultIsoLinesShow);
        gui.resetField();
    }

    public void colorMap()
    {
        field.setInterpol(false);
        gui.resetField();
    }

    public void showGrid()
    {
        gridShow = !gridShow;
        field.setDrawGrid(gridShow);
        gui.resetField();
    }

    public void extraIsoLines()
    {
        extraIsoLinesDraw = !extraIsoLinesDraw;
    }

    public void mousePressed(MouseEvent event)
    {
        Point pos = event.getPoint();
        double value = field.getValueByMouse(pos);
        if (extraIsoLinesDraw)
        {
            field.restoreBitmap();
            field.buildExtraIsoLines(value);
            if (!differenceLevel)
            {
                gui.resetField();
            }
        }
        if (differenceLevel)
        {
            if (!extraIsoLinesDraw)
            {
                field.restoreBitmap();
            }
            field.drawInRangeOf(value);
            gui.resetField();
        }
    }

    public void changeParams()
    {
        JDialog jFrame = new JDialog();
        jFrame.setLocation(200, 200); //TODO: FIX IT
        jFrame.setPreferredSize(paramDimension);
        jFrame.setMinimumSize(paramDimension);
        jFrame.setLayout(new FlowLayout());
        isoLinesParams(jFrame);
        ControllerUtils.addButtons(yMinText,new OkCancelPressed(field, this, new Runnable() {
            public void run() {
                checkParams();
            }
        }),jFrame,jFrame.getContentPane());
        jFrame.setVisible(true);
    }

    public void mouseMoved(MouseEvent e)
    {
        Point pos = e.getPoint();
        gui.setStatus(field.getValuesByMousePos(pos));
    }
    public void mouseReleased(MouseEvent e)
    {
        field.restoreBitmap();
        gui.resetField();
    }

    public void resize(Dimension d)
    {
        field.setWindowWidthHeight(d.width/2,3*d.height/4);
        gui.resizeWindow(d);
    }

    private Point isoLinesParams(JDialog frame)  {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        ControllerUtils.settingSize(xMinLabel,new Point(0,0));
        ControllerUtils.settingSize(xMinText,new Point(xMinLabel.getX() + xMinLabel.getWidth() + 3 , xMinLabel.getY()));
        ControllerUtils.settingSize(xMaxLabel,new Point(xMinLabel.getX(),xMinLabel.getY() + xMinLabel.getHeight() + 5));
        ControllerUtils.settingSize(xMaxText, new Point(xMaxLabel.getX() + xMaxLabel.getWidth() + 3,xMaxLabel.getY()));


        ControllerUtils.settingSize(yMinLabel,new Point(xMinLabel.getX(),xMaxLabel.getY()+xMaxLabel.getHeight()+5));
        ControllerUtils.settingSize(yMinText,new Point(yMinLabel.getX() + yMinLabel.getWidth() + 3 , yMinLabel.getY()));
        ControllerUtils.settingSize(yMaxLabel,new Point(yMinLabel.getX(),yMinLabel.getY() + yMinLabel.getHeight() + 5));
        ControllerUtils.settingSize(yMaxText, new Point(yMaxLabel.getX() + yMaxLabel.getWidth() + 3,yMaxLabel.getY()));

        ControllerUtils.settingSize(gridXLabel,new Point(xMinLabel.getX(),yMaxLabel.getY()+yMaxLabel.getHeight()+5));
        ControllerUtils.settingSize(gridXText,new Point(gridXLabel.getX() + gridXLabel.getWidth() + 3 , gridXLabel.getY()));

        ControllerUtils.settingSize(gridYLabel,new Point(xMinLabel.getX(),gridXLabel.getY()+gridXLabel.getHeight()+5));
        ControllerUtils.settingSize(gridYText,new Point(gridYLabel.getX() + gridYLabel.getWidth() + 3 , gridYLabel.getY()));

        panel.add(xMinLabel);
        panel.add(xMinText);
        panel.add(xMaxLabel);
        panel.add(xMaxText);
        panel.add(yMinLabel);
        panel.add(yMinText);
        panel.add(yMaxLabel);
        panel.add(yMaxText);
        panel.add(gridXLabel);
        panel.add(gridXText);
        panel.add(gridYLabel);
        panel.add(gridYText);
        panel.setVisible(true);
        frame.setContentPane(panel);
        return null;
    }

    private void checkParams()
    {
        String xMinStr = this.xMinText.getText();
        String xMaxStr = xMaxText.getText();
        String yMinStr = yMinText.getText();
        String yMaxStr = yMaxText.getText();
        String yGridStr = gridYText.getText();
        String xGridStr = gridXText.getText();
        try
        {
            double xMin = Double.parseDouble(xMinStr);
            double xMax = Double.parseDouble(xMaxStr);
            double yMin = Double.parseDouble(yMinStr);
            double yMax = Double.parseDouble(yMaxStr);
            int xGrid = Integer.parseInt(xGridStr);
            int yGrid = Integer.parseInt(yGridStr);

            if (xMin<X_MIN || xMax > X_MAX||yMin<Y_MIN || yMax > Y_MAX)
            {
                throw new NumberFormatException();
            }
            if (xGrid < GRID_MIN || xGrid > GRID_MAX ||yGrid < GRID_MIN || yGrid > GRID_MAX)
            {
                throw new NumberFormatException();
            }
            field.setMinMax(xMin,xMax,yMin,yMax,xGrid,yGrid);
            gui.resetField();
        }
        catch(NumberFormatException exc)
        {
            String v = "Диапазон xMin...xMax может быть от -100 до 100\n";
            v+="Диапазон yMin...yMax аналогично\n";
            JOptionPane.showMessageDialog(null,v);
        }
    }

    private Params getParams(BufferedReader reader)
    {
        Params ret = new Params();
        ret.windowHeight = functionMapZoneSize.height;
        ret.windowWidth = functionMapZoneSize.width;
        try
        {
            String str = reader.readLine();
            String[] params = str.split(" ");
            ret.gridX = Integer.parseInt(params[0]);
            ret.gridY = Integer.parseInt(params[1]);
            str = reader.readLine();
            int n = Integer.parseInt(str);
            ret.colors = new int[n+1];
            for ( int i = 0 ; i  < n + 1;  ++i)
            {
                str = reader.readLine();
                params = str.split(" ");
                int red =  Integer.parseInt(params[0]);
                int green = Integer.parseInt(params[1]);
                int blue = Integer.parseInt(params[2]);
                Color c = new Color(red,green,blue);
                ret.colors[i] = c.getRGB();
            }
            {
                str = reader.readLine();
                params = str.split(" ");
                int red=  Integer.parseInt(params[0]);
                int green = Integer.parseInt(params[1]);
                int blue = Integer.parseInt(params[2]);
                Color c = new Color(red,green,blue);
                ret.isoLinesColor = c.getRGB();
            }
            return ret;
        }
        catch(IOException er)
        {
            System.err.println("IO error");
            er.printStackTrace();
            System.exit(1);
        }
        catch(NumberFormatException er)
        {
            System.err.println("Bad Number! Check the file!");
            System.exit(1);
        }
        return ret;
    }
    private void openFile()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(DATA_PATH));
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File file = fileChooser.getSelectedFile();
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                Params params = getParams(bufferedReader);
                params.windowHeight = field.getWindowHeight();
                params.windowWidth = field.getWindowWidth();
                Field field = new Field(params);
                this.field = field;
                gui.setField(field);
            }
            catch (IOException ex)
            {
                System.err.println("IO error");
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
    private GUI gui;
    private Field field;
}
