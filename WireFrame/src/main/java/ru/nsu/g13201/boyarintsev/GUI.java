package ru.nsu.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * Created by Артем on 14.04.2016.
 */

public class GUI extends JFrame {
    static class HINTS
    {
        static public String newHINT = "очистить рабочую область";
        static public String openHINT = "Открыть файл с параметрами";
        static public String saveHINT = "Сохранить текущее состояние сцены";
        static public String paramHINT = "Изменить параметры";
        static public String initStateHINT = "Начальное положение сцены";
        static public String mainParamsHINT = "Изменение основных параметров сцены";
        static public String changeExistedHINT = "Вы можете изменить существующую поверхность";
        static public String rotateHINT = "Вы можете вращать фигуру";
        static public String moveHINT = "Вы можете двигать фигуру";
        static public String aboutHINT = "Информация о разработчике";
        static public String rotateSceneHINT = "Вы можете вращать сцену";
        static public String exitHINT = "Выход";
    }

    private AppController controller = null;
    private JLabel statusLabel;
    private Scene scene;

    public GUI(AppController c,Scene scene)
    {
        super("WireFrame");
        setScene(scene);
        this.controller = c;
        setSize(new Dimension(800,600));
        createMenus();
        createToolbar();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setScene(Scene scene)
    {
        this.scene = scene;
        setContentPane(this.scene);
    }

    private void createMenus()
    {
        JMenuBar menuBar = new JMenuBar();
        JToolBar toolbar = createToolbar();
        toolbar.setLocation(0,0);
        createFileMenu(menuBar);
        createViewMenu(menuBar);
        createAboutMenu(menuBar);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(toolbar);
        setLocationRelativeTo(null);
        setJMenuBar(menuBar);
    }

    private  void createFileMenu(JMenuBar menuBar)
    {
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        JMenuItem newItem = new JMenuItem(HINTS.newHINT);
        addMenuItem(fileMenu, newItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.newScene();
            }
        });

        JMenuItem selectItem = new JMenuItem(HINTS.openHINT);
        addMenuItem(fileMenu, selectItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.open();
            }
        });

        JMenuItem exitItem = new JMenuItem(HINTS.exitHINT);
        addMenuItem(fileMenu, exitItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.exit();
            }
        });
    }

    private  void createViewMenu(JMenuBar menuBar) {
        JMenu viewMenu = new JMenu("Вид");
        menuBar.add(viewMenu);

        JMenuItem paramsItem = new JMenuItem(HINTS.paramHINT);
        addMenuItem(viewMenu, paramsItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.createNewSurface();
            }
        });

        JMenuItem initState = new JMenuItem(HINTS.initStateHINT);
        addMenuItem(viewMenu, initState, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.initState();
            }
        });
    }

    private  void createAboutMenu(JMenuBar menuBar) {

        JMenu simulationMenu = new JMenu("О Программе");
        menuBar.add(simulationMenu);

        JMenuItem stepItem = new JMenuItem("About...");
        addMenuItem(simulationMenu, stepItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.about();
            }
        });
    }

    private void addMenuItem(JMenu menu,JMenuItem item,ActionListener actionListener)
    {
        Font font = new Font("Verdana", Font.PLAIN, 11);
        menu.setFont(font);
        item.addActionListener(actionListener);
        menu.add(item);
    }

    private JToolBar createToolbar()
    {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setVisible(true);

        final JButton newButton = new JButton();
        GUIUtils.addButton(toolBar, newButton, HINTS.newHINT, "/new.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.newScene();
            }
        });
        JButton openButton = new JButton();
        GUIUtils.addButton(toolBar, openButton, HINTS.openHINT,"/open.png",new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.open();
            }
        });

        JButton saveButton = new JButton();
        GUIUtils.addButton(toolBar, saveButton, HINTS.saveHINT, "/save.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.save();
            }
        });

        JButton newSurfaceButton = new JButton();
        GUIUtils.addButton(toolBar, newSurfaceButton, HINTS.paramHINT, "/params.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.createNewSurface();
            }
        });
        JButton changeExistedSurfaceButton = new JButton();
        GUIUtils.addButton(toolBar, changeExistedSurfaceButton, HINTS.changeExistedHINT, "/params.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.changeExistedSurface();
            }
        });
        JButton mainSceneParamsButton = new JButton();
        GUIUtils.addButton(toolBar, mainSceneParamsButton, HINTS.mainParamsHINT, "/params.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.changeMainParams();
            }
        });
        JButton rotateButton = new JButton();
        GUIUtils.addButton(toolBar, rotateButton, HINTS.rotateHINT, "/new.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.rotateSurface();
            }
        });
        JButton moveButton = new JButton();
        GUIUtils.addButton(toolBar, moveButton, HINTS.moveHINT, "/open.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.moveSurface();
            }
        });
        JButton rotateSceneButton = new JButton();
        GUIUtils.addButton(toolBar, rotateSceneButton, HINTS.rotateSceneHINT, "/asdf", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.rotateScene();
            }
        });
        final JButton initStateButton = new JButton();
        GUIUtils.addButton(toolBar, initStateButton, HINTS.initStateHINT, "/initState.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.initState();
            }
        });


        JButton aboutButton = new JButton();
        GUIUtils.addButton(toolBar, aboutButton, HINTS.aboutHINT, "/about.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.about();
            }
        });
        Dimension d = new Dimension();
        for (Component c : toolBar.getComponents())
        {
            d.setSize(d.width+c.getWidth(),c.getHeight());
        }
        toolBar.setSize(d);
        toolBar.setVisible(true);
        return toolBar;
    }
}
