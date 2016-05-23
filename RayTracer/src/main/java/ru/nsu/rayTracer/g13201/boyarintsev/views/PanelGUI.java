package ru.nsu.rayTracer.g13201.boyarintsev.views;

import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.iScene;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;


public class PanelGUI extends JPanel implements Observer
{
    private BufferedImage image = null;
    private iScene scene;

    public PanelGUI(iScene scene)
    {
        this.scene = scene;
        scene.addObserver(this);
        image = scene.render();
        repaint();
    }

    public iScene getScene() {
        return scene;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (null != image)
        {
            g.drawImage(image,0,0,null);
        }
    }

    public void update(Observable o, Object arg) {
        image = scene.render();
        repaint();
    }
}