package ru.nsu.rayTracer.g13201.boyarintsev.views;

import ru.nsu.rayTracer.g13201.boyarintsev.controllers.MainController;
import ru.nsu.rayTracer.g13201.boyarintsev.models.scenes.iScene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class GUI extends JFrame
{
    private MainController mainController;
    private PanelGUI panelGUI;
    public GUI(MainController c)
    {
        super("RayTracer");
        getContentPane().setSize(getContentPane().getMinimumSize());
        this.mainController = c;
        setSize(new Dimension(700,700));
        createMenus();
        addListeners();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setScene(iScene scene)
    {
        panelGUI = new PanelGUI(scene);
        setContentPane(panelGUI);
        panelGUI.repaint();
        repaint();
    }


    private void addListeners()
    {
        addMouseWheelListener(new MouseWheelListener() {
            private final double k = 1.1;
            public void mouseWheelMoved(MouseWheelEvent e) {
                double grad = e.getWheelRotation();
                if (e.isControlDown())
                {
                    grad = -0.5 * grad;
                    mainController.cameraMovementAmongEyeViewVector(grad);
                    return;
                }
                if (grad < 0)
                {
                    grad = k;
                }
                else
                {
                    grad = 1/k;
                }
                mainController.cameraZoom(grad);
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                mainController.cameraMovement(e.getKeyCode());
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                mainController.mouseReleased();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                mainController.cameraRotation(e);
            }
        });
    }

    private void createMenus()
    {
        JMenuBar menuBar = new JMenuBar();
        createFileMenu(menuBar);
        createSettingsMenu(menuBar);
        createAboutMenu(menuBar);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setJMenuBar(menuBar);
    }

    private  void createFileMenu(JMenuBar menuBar) {
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        
        JMenuItem selectItem = new JMenuItem(HINTS.openHINT);
        addMenuItem(fileMenu, selectItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainController.openSceneFile();
            }
        });

        JMenuItem exitItem = new JMenuItem(HINTS.exitHINT);
        addMenuItem(fileMenu, exitItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private  void createSettingsMenu(JMenuBar menuBar) {
        JMenu viewMenu = new JMenu("Параметры");
        menuBar.add(viewMenu);

        JMenuItem loadRenderSettings = new JMenuItem(HINTS.loadRenderSettingsHINT);
        addMenuItem(viewMenu, loadRenderSettings, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainController.openRenderSettingsFile();
            }
        });

        JMenuItem saveRenderSettings = new JMenuItem(HINTS.saveRenderSettingsHINT);
        addMenuItem(viewMenu, saveRenderSettings, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainController.saveRenderSettings();
            }
        });
        JMenuItem settings = new JMenuItem(HINTS.settingsHINT);
        addMenuItem(viewMenu, settings, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainController.renderSettings();
            }
        });
        JMenuItem selectItem = new JMenuItem(HINTS.selectHINT);
        addMenuItem(viewMenu, selectItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainController.modeChangeCameraPosition();
            }
        });

        JMenuItem saveImage = new JMenuItem(HINTS.saveImageHINT);
        addMenuItem(viewMenu, saveImage, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainController.savePicture(panelGUI.getScene());
            }
        });

        JMenuItem render = new JMenuItem(HINTS.renderHINT);
        addMenuItem(viewMenu, render, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainController.rayTracerRendering();
            }
        });


    }

    private  void createAboutMenu(JMenuBar menuBar) {

        JMenu simulationMenu = new JMenu("О Программе");
        menuBar.add(simulationMenu);

        JMenuItem stepItem = new JMenuItem("About...");
        addMenuItem(simulationMenu, stepItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainController.about();
            }
        });
    }

    private void addMenuItem(JMenu menu,JMenuItem item,ActionListener actionListener) {
        Font font = new Font("Verdana", Font.PLAIN, 11);
        menu.setFont(font);
        item.addActionListener(actionListener);
        menu.add(item);
    }

    private static class HINTS {
        static public String openHINT = "Открыть файл с параметрами";
        static public String exitHINT = "Выход";
        static public String loadRenderSettingsHINT = "Загрузка настроек рендеринга из файла";
        static public String saveRenderSettingsHINT = "Сохранение настроек рендеринга в файл";
        static public String settingsHINT = "Показать диалог настроек рендеринга";
        static public String selectHINT = "Переход в режим выбора ракурса";
        static public String renderHINT = "Запуск процесса рендеринга сцены";
        static public String saveImageHINT = "Сохранение изображения в файл";
    }

}
