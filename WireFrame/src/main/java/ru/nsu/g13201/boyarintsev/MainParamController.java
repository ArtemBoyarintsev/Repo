package ru.nsu.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Артем on 24.04.2016.
 */
public class MainParamController {
    static private double PI = 3.1415;
    private final JLabel nParamLabel = new JLabel("n: ");
    private final JTextField nParamText = new JTextField("10",5);

    private final JLabel mParamLabel = new JLabel("m:");
    private final JTextField mParamText = new JTextField("10",5);

    private final JLabel aParamLabel = new JLabel("a:");
    private final  JTextField aParamText = new JTextField("0.0",5);

    private final JLabel bParamLabel = new JLabel("b: ");
    private final  JTextField bParamText = new JTextField("1.0",5);

    private final JLabel cParamLabel = new JLabel("c: ");
    private final  JTextField cParamText = new JTextField("0.0",5);

    private final JLabel dParamLabel = new JLabel("d: ");
    private final  JTextField dParamText = new JTextField("6.28",5);

    private final JLabel znParamLabel = new JLabel("zn: ");
    private final  JTextField znParamText = new JTextField("1.0",5);

    private final JLabel zfParamLabel = new JLabel("zf: ");
    private final  JTextField zfParamText = new JTextField("3.0",5);

    private final JLabel swParamLabel = new JLabel("sw: ");
    private final  JTextField swParamText = new JTextField("2.0",5);

    private final JLabel shParamLabel = new JLabel("sh: ");
    private final JTextField shParamText = new JTextField("3.0",5);

    private Dimension constParamDimension = new Dimension(240,180);
    private Point configsTextsPoint = new Point(0,0);


    private Scene scene;

    public MainParamController(Scene scene)
    {
        this.scene = scene;
        JDialog jFrame = new JDialog();
        jFrame.setLocation(200, 200); //TODO: FIX IT
        jFrame.setPreferredSize(constParamDimension);
        jFrame.setMinimumSize(constParamDimension);
        mainParams(jFrame);
        Point availableSpace = ControllerUtils.addButtons(znParamText,new OkCancelPressed(new Runnable() {
            public void run() {
                checkMainParams();
            }
        }),jFrame,jFrame.getContentPane());
        jFrame.setVisible(true);
    }

    private Point mainParams(JDialog frame)  {
        Container panel = frame.getContentPane();
        panel.setLayout(null);

        ControllerUtils.settingSize(nParamLabel,configsTextsPoint);
        ControllerUtils.settingSize(nParamText,new Point(nParamLabel.getX() + nParamLabel.getWidth() + 3 , nParamLabel.getY()));

        ControllerUtils.settingSize(mParamLabel,new Point(nParamLabel.getX(),nParamLabel.getY() + nParamLabel.getHeight() + 5));
        ControllerUtils.settingSize(mParamText, new Point(mParamLabel.getX() + mParamLabel.getWidth() + 3,mParamLabel.getY()));

        ControllerUtils.settingSize(aParamLabel,new Point(nParamLabel.getX(),mParamLabel.getY()+mParamLabel.getHeight()+5));
        ControllerUtils.settingSize(aParamText,new Point(aParamLabel.getX() + aParamLabel.getWidth() + 3 , aParamLabel.getY()));

        ControllerUtils.settingSize(bParamLabel,new Point(aParamLabel.getX(),aParamLabel.getY() + aParamLabel.getHeight() + 5));
        ControllerUtils.settingSize(bParamText, new Point(bParamLabel.getX() + bParamLabel.getWidth() + 3,bParamLabel.getY()));

        ControllerUtils.settingSize(cParamLabel,new Point(nParamLabel.getX(),bParamLabel.getY()+bParamLabel.getHeight()+5));
        ControllerUtils.settingSize(cParamText,new Point(cParamLabel.getX() + cParamLabel.getWidth() + 3 , cParamLabel.getY()));

        ControllerUtils.settingSize(dParamLabel,new Point(nParamLabel.getX(),cParamLabel.getY()+cParamLabel.getHeight()+5));
        ControllerUtils.settingSize(dParamText,new Point(dParamLabel.getX() + dParamLabel.getWidth() + 3 , dParamLabel.getY()));

        ControllerUtils.settingSize(znParamLabel, new Point(aParamText.getX() + aParamText.getWidth() +5,aParamText.getY()));
        ControllerUtils.settingSize(znParamText,new Point(znParamLabel.getX() + znParamLabel.getWidth() + 3,znParamLabel.getY()));

        ControllerUtils.settingSize(zfParamLabel, new Point(znParamLabel.getX(),znParamLabel.getY() + znParamLabel.getHeight()+4));
        ControllerUtils.settingSize(zfParamText,new Point(zfParamLabel.getX() + zfParamLabel.getWidth() + 3,zfParamLabel.getY()));

        ControllerUtils.settingSize(swParamLabel, new Point(zfParamLabel.getX(),zfParamLabel.getY() + zfParamLabel.getHeight()+4));
        ControllerUtils.settingSize(swParamText,new Point(swParamLabel.getX() + swParamLabel.getWidth() + 3,swParamLabel.getY()));

        ControllerUtils.settingSize(shParamLabel, new Point(swParamLabel.getX(),swParamLabel.getY() + swParamLabel.getHeight()+4));
        ControllerUtils.settingSize(shParamText,new Point(shParamLabel.getX() + shParamLabel.getWidth() + 3,shParamLabel.getY()));

        panel.add(nParamLabel);
        panel.add(nParamText);
        panel.add(mParamLabel);
        panel.add(mParamText);
        panel.add(aParamLabel);
        panel.add(aParamText);
        panel.add(bParamLabel);
        panel.add(bParamText);
        panel.add(cParamLabel);
        panel.add(cParamText);
        panel.add(dParamLabel);
        panel.add(dParamText);
//
        panel.add(znParamLabel);
        panel.add(zfParamLabel);
        panel.add(znParamText);
        panel.add(zfParamText);
        panel.add(swParamLabel);
        panel.add(shParamLabel);
        panel.add(swParamText);
        panel.add(shParamText);
        panel.setVisible(true);
        return null;
    }

    private void checkMainParams()
    {
        String nParamStr = nParamText.getText();
        String mParamStr = mParamText.getText();
        String aParamStr = aParamText.getText();
        String bParamStr = bParamText.getText();
        String cParamStr = cParamText.getText();
        String dParamStr = dParamText.getText();

        String znParamStr = znParamText.getText();
        String zfParamStr = zfParamText.getText();
        String swParamStr = swParamText.getText();
        String shParamStr = shParamText.getText();

        try
        {
            int nParam = Integer.parseInt(nParamStr);
            int mParam = Integer.parseInt(mParamStr);

            double aParam = Double.parseDouble(aParamStr);
            double bParam = Double.parseDouble(bParamStr);
            double cParam = Double.parseDouble(cParamStr);
            double dParam = Double.parseDouble(dParamStr);

            double zn = Double.parseDouble(znParamStr);
            double zf = Double.parseDouble(zfParamStr);
            double sw = Double.parseDouble(swParamStr);
            double sh = Double.parseDouble(shParamStr);


            if (aParam < 0 || aParam > bParam || 1 < bParam )
            {
                throw new NumberFormatException();
            }
            if (nParam > 100 || nParam < 0||mParam > 100 || mParam < 0)
            {
                throw new NumberFormatException();
            }
            if (cParam < 0 || cParam > dParam || 2*PI < bParam )
            {
                throw new NumberFormatException();
            }
            if (zn < 0 || zf < 0 || sw < 0 || sh < 0)
            {
                throw  new NumberFormatException();
            }
            MainSceneParams mainSceneParams = new MainSceneParams();
            mainSceneParams.a = aParam;
            mainSceneParams.b = bParam;
            mainSceneParams.c = cParam;
            mainSceneParams.d = dParam;
            mainSceneParams.n = nParam;
            mainSceneParams.m = mParam;
            scene.changeMainParams(mainSceneParams);
        }

        catch(NumberFormatException exc)
        {
            String v = "Должно быть 0 <= a <= b <= 1\n";
            v+="0 <= c <= d <= 2 * pi\n";
            v+="n и m должны быть в диапазоне 0...10\n";
            JOptionPane.showMessageDialog(null,v);
        }
    }

}
