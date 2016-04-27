package ru.nsu.g13201.boyarintsev;


import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Артем on 16.04.2016.
 */

public class ParamController {
    enum Mode {
        modify,
        delete,
        add
    }
    private static double PI = 3.14;

    private final JLabel redParamLabel = new JLabel("R: ");
    private final  JTextField redParamText = new JTextField("200",5);

    private final JLabel greenParamLabel = new JLabel("G: ");
    private final  JTextField greenParamText = new JTextField("20",5);

    private final JLabel blueParamLabel = new JLabel("B: ");
    private final  JTextField blueParamText = new JTextField("230",5);

    private final JLabel noExtraDotsParamLabel = new JLabel("K: ");
    private final  JTextField noExtraDotsParamText = new JTextField("0",5);

    private final JLabel cxParamLabel = new JLabel("CX:");
    private final  JTextField cxParamText = new JTextField("0",5);

    private final JLabel cyParamLabel = new JLabel("CY:");
    private final  JTextField cyParamText = new JTextField("0",5);

    private final JLabel czParamLabel = new JLabel("CZ:");
    private final  JTextField czParamText = new JTextField("0",5);

    private final JLabel rxParamLabel = new JLabel("RX:");
    private final  JTextField rxParamText = new JTextField("0",5);

    private final JLabel ryParamLabel = new JLabel("RY:");
    private final  JTextField ryParamText = new JTextField("0",5);

    private final JLabel rzParamLabel = new JLabel("RZ:");
    private final JTextField rzParamText = new JTextField("0",5);

    private final JLabel nameParamLabel = new JLabel("name: ");
    private final JTextField nameParamText = new JTextField("Surface",15);

    private Dimension constParamDimension = new Dimension(640,480);
    private Point configsTextsPoint = new Point(0,290);

    private final JButton deleteButton = new JButton("delete");
    private final JButton addMainDots = new JButton("+");
    private final JButton deleteMainDots = new JButton("-");

    private final JButton upExtraDotButton = new JButton("+");
    private final JButton downExtraDotButton = new JButton("-");
    private final JButton markMainDotButton = new JButton("=");

    private final JButton scaleIncreaseButton = new JButton("Scale +");
    private final JButton scaleDecreaseButton = new JButton("Scale -");

    private SplineField splineField;
    private Dimension splineDrawZone;
    private SplinePanel splinePanel;

    private Mode mode;
    private Surface surface;
    private Scene scene;
    private boolean needToAddToScene  =false;

    public ParamController(Scene scene,Surface surface,boolean needToAddToScene)
    {
        this.needToAddToScene = needToAddToScene;
        this.scene = scene;
        this.surface =  surface;
        this.splineField = surface.getSplineField();
        if (!needToAddToScene)
        {
            nameParamText.setText(surface.getName());
            nameParamText.setFocusable(false);
        }
        if (this.splineField == null)
        {
            this.splineField = new SplineField();
        }
        mode = Mode.modify;
        JDialog jFrame = new JDialog();
        jFrame.setLocation(200, 200); //TODO: FIX IT
        jFrame.setPreferredSize(constParamDimension);
        jFrame.setMinimumSize(constParamDimension);
        splineDrawZone = new Dimension(constParamDimension.width,configsTextsPoint.y);
        splinePanel = new SplinePanel(splineDrawZone,splineField);
        addMouseListeners(splinePanel);
        jFrame.setContentPane(splinePanel);
        surfaceParams(jFrame);
        Point availableSpace = ControllerUtils.addButtons(rxParamText,new OkCancelPressed(new Runnable() {
            public void run() {
                checkSurfaceParams();
            }
        }),jFrame,jFrame.getContentPane());
        GUIUtils.addAdditionButton(jFrame, new JButton(), "/scale+.png", availableSpace, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        GUIUtils.addAdditionButton(jFrame, new JButton(), "/scale-.png", availableSpace, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        jFrame.setVisible(true);
    }
    private Point surfaceParams(JDialog frame)  {
        Container panel = frame.getContentPane();
        panel.setLayout(null);

        ControllerUtils.settingSize(redParamLabel,configsTextsPoint);
        ControllerUtils.settingSize(redParamText,new Point(redParamLabel.getX() + redParamLabel.getWidth() + 3 , redParamLabel.getY()));

        ControllerUtils.settingSize(blueParamLabel,new Point(redParamLabel.getX(),redParamLabel.getY() + redParamLabel.getHeight() + 5));
        ControllerUtils.settingSize(blueParamText, new Point(blueParamLabel.getX() + blueParamLabel.getWidth() + 3,blueParamLabel.getY()));

        ControllerUtils.settingSize(greenParamLabel,new Point(blueParamLabel.getX(),blueParamLabel.getY()+blueParamLabel.getHeight()+5));
        ControllerUtils.settingSize(greenParamText,new Point(greenParamLabel.getX() + greenParamLabel.getWidth() + 3 , greenParamLabel.getY()));

        ControllerUtils.settingSize(nameParamLabel,new Point(greenParamLabel.getX(),greenParamLabel.getY()+greenParamLabel.getHeight()+5));
        ControllerUtils.settingSize(nameParamText,new Point(nameParamLabel.getX() + nameParamLabel.getWidth() + 3 , nameParamLabel.getY()));

        ControllerUtils.settingSize(deleteButton,new Point(nameParamLabel.getX(),nameParamLabel.getY()+nameParamLabel.getHeight()+5));


        ControllerUtils.settingSize(cxParamLabel, new Point(redParamText.getX()+redParamText.getWidth() +3, redParamText.getY()));
        ControllerUtils.settingSize(cxParamText,new Point(cxParamLabel.getX()+cxParamLabel.getWidth(),cxParamLabel.getY()));

        ControllerUtils.settingSize(cyParamLabel, new Point(cxParamLabel.getX(), cxParamLabel.getY()+cxParamLabel.getHeight()));
        ControllerUtils.settingSize(cyParamText,new Point(cyParamLabel.getX()+cyParamLabel.getWidth(),cyParamLabel.getY()));


        ControllerUtils.settingSize(czParamLabel, new Point(cyParamLabel.getX(), cyParamLabel.getY()+cyParamLabel.getHeight()));
        ControllerUtils.settingSize(czParamText,new Point(czParamLabel.getX()+czParamLabel.getWidth(),czParamLabel.getY()));

        ControllerUtils.settingSize(rxParamLabel, new Point(cxParamText.getX()+cxParamText.getWidth() +3, cxParamText.getY()));
        ControllerUtils.settingSize(rxParamText,new Point(rxParamLabel.getX()+rxParamLabel.getWidth(),rxParamLabel.getY()));

        ControllerUtils.settingSize(ryParamLabel, new Point(rxParamLabel.getX(), rxParamLabel.getY()+rxParamLabel.getHeight()));
        ControllerUtils.settingSize(ryParamText,new Point(ryParamLabel.getX()+ryParamLabel.getWidth(),ryParamLabel.getY()));

        ControllerUtils.settingSize(rzParamLabel, new Point(ryParamLabel.getX(), ryParamLabel.getY()+ryParamLabel.getHeight()));
        ControllerUtils.settingSize(rzParamText,new Point(rzParamLabel.getX()+rzParamLabel.getWidth(),rzParamLabel.getY()));


        panel.add(redParamLabel);
        panel.add(redParamText);
        panel.add(blueParamLabel);
        panel.add(blueParamText);
        panel.add(greenParamLabel);
        panel.add(greenParamText);
        panel.add(cxParamLabel);
        panel.add(cxParamText);
        panel.add(cyParamLabel);
        panel.add(cyParamText);
        panel.add(czParamLabel);
        panel.add(czParamText);

        panel.add(nameParamLabel);
        panel.add(nameParamText);
        panel.add(deleteButton);
        panel.add(rxParamLabel);
        panel.add(rxParamText);
        panel.add(ryParamLabel);
        panel.add(ryParamText);
        panel.add(rzParamLabel);
        panel.add(rzParamText);
        addDotsParams(panel,cxParamLabel);
        panel.setVisible(true);
        return null;
    }
    private void addMouseListeners(final SplinePanel panel)
    {
        panel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e)
            {
                Point pos = e.getPoint();
                if (pos.x > splineDrawZone.width || pos.y > splineDrawZone.height)
                {
                    return;
                }
                if (mode == Mode.add)
                {
                    DoublePoint point = splinePanel.getSplineCoorsByWindowsCoors(pos);
                    splineField.addMainDotByUser(point);
                }
                if (mode == Mode.delete)
                {
                    DoublePoint point = splinePanel.getSplineCoorsByWindowsCoors(pos);
                    double d = splinePanel.getCharacterMainDotsSize();
                    splineField.deleteMainDotByUser(point,d);
                }
            }

            public void mouseReleased(MouseEvent e)
            {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mode == Mode.modify)
                {
                    super.mouseMoved(e);
                    Point p = e.getPoint();
                    DoublePoint pp = splinePanel.getSplineCoorsByWindowsCoors(p);
                    double d = splinePanel.getCharacterMainDotsSize();
                    int m = splineField.removeFrom(pp,d);
                    if (m != -1)
                    {
                        splineField.addMainDotByUser(pp,m);
                    }
                }
            }
        });
    }



    private void addDotsParams(Container panel,Component start)
    {
        /*ControllerUtils.settingSize(noDotsParamLabel, new Point(start.getX() + 250,start.getY()));
        ControllerUtils.settingSize(noDotsParamText,new Point(noDotsParamLabel.getX() + noDotsParamLabel.getWidth() + 3,
                                                                                                    noDotsParamLabel.getY()));
        */
        ControllerUtils.settingSize(markMainDotButton, new Point(start.getX() + 250,start.getY()));
        ControllerUtils.settingSize(addMainDots,new Point(markMainDotButton.getX()+markMainDotButton.getWidth(),markMainDotButton.getY()));
        ControllerUtils.settingSize(deleteMainDots,new Point(addMainDots.getX()+ addMainDots.getWidth(), addMainDots.getY()));

        ControllerUtils.settingSize(noExtraDotsParamLabel,
                new Point(markMainDotButton.getX(), markMainDotButton.getY()+markMainDotButton.getHeight()+10));
        ControllerUtils.settingSize(noExtraDotsParamText,
                new Point(noExtraDotsParamLabel.getX() + noExtraDotsParamLabel.getWidth() + 3, noExtraDotsParamLabel.getY()));
        ControllerUtils.settingSize(upExtraDotButton,new Point(noExtraDotsParamText.getX()+noExtraDotsParamText.getWidth(),noExtraDotsParamLabel.getY()));
        ControllerUtils.settingSize(downExtraDotButton,new Point(upExtraDotButton.getX()+upExtraDotButton.getWidth(),upExtraDotButton.getY()));
        ControllerUtils.settingSize(scaleIncreaseButton, new Point(deleteMainDots.getX()+deleteMainDots.getWidth(),deleteMainDots.getY()));
        ControllerUtils.settingSize(scaleDecreaseButton,
                new Point(scaleIncreaseButton.getX()+scaleIncreaseButton.getWidth(),scaleIncreaseButton.getY()));

        noExtraDotsParamText.setFocusable(false);
        magic_func();
        panel.add(addMainDots);
        panel.add(deleteMainDots);
        panel.add(markMainDotButton);
        panel.add(upExtraDotButton);
        panel.add(downExtraDotButton);

        panel.add(noExtraDotsParamLabel);
        panel.add(noExtraDotsParamText);

        panel.add(scaleIncreaseButton);
        panel.add(scaleDecreaseButton);
    }

    private void magic_func()
    {
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scene.deleteSurface(surface);
            }
        });
        addMainDots.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                mode = Mode.add;
            }
        });

        deleteMainDots.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = Mode.delete;
            }
        });
        markMainDotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = Mode.modify;
            }
        });

        upExtraDotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String value = noExtraDotsParamText.getText();
                Integer k = Integer.parseInt(value) + 1;
                if (k <=10)
                {
                    noExtraDotsParamText.setText(k.toString());
                    splineField.addExtraDot();
                }
            }
        });

        downExtraDotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String value = noExtraDotsParamText.getText();
                Integer k = Integer.parseInt(value) - 1;
                if (k >=0)
                {
                    noExtraDotsParamText.setText(k.toString());
                    splineField.deleteExtraDot();
                }
            }
        });
        scaleIncreaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                splinePanel.scaleIncrease();
            }
        });
        scaleDecreaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                splinePanel.scaleDecrease();
            }
        });
    }
    private void checkSurfaceParams()
    {
        try {
            String redParamStr = redParamText.getText();
            String greenParamStr = greenParamText.getText();
            String blueParamStr = blueParamText.getText();

            String cxParamStr = cxParamText.getText();
            String cyParamStr = cyParamText.getText();
            String czParamStr = czParamText.getText();

            String rxParamStr = rxParamText.getText();
            String ryParamStr = ryParamText.getText();
            String rzParamStr = rzParamText.getText();
            String nameParamStr = nameParamText.getText();

            int r = Integer.parseInt(redParamStr);
            int g = Integer.parseInt(greenParamStr);
            int b = Integer.parseInt(blueParamStr);

            int rx = Integer.parseInt(rxParamStr);
            int ry = Integer.parseInt(ryParamStr);
            int rz = Integer.parseInt(rzParamStr);

            double cx = Double.parseDouble(cxParamStr);
            double cy = Double.parseDouble(cyParamStr);
            double cz = Double.parseDouble(czParamStr);

            if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                throw new NumberFormatException();
            }
            if (rx < 0 || rx > 360|| ry < 0 || ry > 360|| rz < 0 || rz > 360)
            {
                throw new NumberFormatException();
            }
            if (cx < -8 || cx > 8.0 || cy < -8 || cy > 8.0 || cz < -8 || cz > 8.0)
            {
                throw new NumberFormatException();
            }

            SurfaceParams surfaceParams = new SurfaceParams();
            surfaceParams.CX = cx;
            surfaceParams.CY = cy;
            surfaceParams.CZ = cz;

            surfaceParams.RX = PI*rx/180;
            surfaceParams.RY = PI*ry/180;
            surfaceParams.RZ = PI*rz/180;

            surfaceParams.R = r;
            surfaceParams.B = b;
            surfaceParams.G = g;

            surfaceParams.name = nameParamStr;
            surfaceParams.splineField = splineField;

            boolean status = surface.changeSurfaceParams(surfaceParams);
            if (status && needToAddToScene)
            {
                scene.addSurface(surface);
            }
        }
        catch(NumberFormatException exc)
        {
            String v = "Цвета должны быть в диапазоне от 0 до 255\n";
            v+="Максимальное расстояние 8 в любом направление по всем осям";
            v+="Углы не могут быть меньше нуля и больше 360!\n";
            JOptionPane.showMessageDialog(null,v);
        }
    }
}
