package ru.nsu.fit.g13201.boyarintsev;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;


/**
 * Created by Артем on 24.03.2016.
 */
public class GUI extends JFrame {
    static class HINTS
    {
        static public String openHINT = "Открыть файл с параметрами";
        static public String gridHINT = "Сетка";
        static public String paramHINT = "Изменить параметры";
        static public String isoLinesHINT = "Нарисовать isoLines";
        static public String interpolHINT = "Интерполяция цвета";
        static public String colorMapHINT = "Цветовая карта";
        static public String aboutHINT = "Информация о разработчике";
        static public String diffLevelHINT = "Выделение уровней";
        static public String addMouseIsoLines = "Добавить изолинии мышкой";
        static public String exitHINT = "Выход";
    }
    private AppController controller = null;
    private MapZone mapZone = null;
    private LegendZone legendZone = null;
    private JLabel statusLabel;
    private Field field;

    public GUI(AppController c,Field field,Dimension windowSize)
    {
        super("IsoLines");
        this.field = field;
        this.controller = c;
       /*addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                int width = getWidth();
                int height = getHeight();
                int MIN_WIDTH = 420;
                int MAX_WIDTH = 820;
                int MIN_HEIGHT = 420;
                int MAX_HEIGHT = 820;
                if (width <= MIN_WIDTH || width >= MAX_WIDTH || height <=MIN_HEIGHT || height >= MAX_HEIGHT)
                {
                    return;
                }
                controller.resize(new Dimension(width-20,height-20));
            }
        });*/
        createMenus();
        createZones(field,windowSize);
        setMinimumSize(new Dimension(400,400));
        setMaximumSize(new Dimension(800,800));
        setSize(new Dimension(windowSize.width+20,windowSize.height+20));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void resizeWindow(Dimension windowSize)
    {
        Dimension functionMapZoneSize = new Dimension(windowSize.width/2, windowSize.height*3/4);
        mapZone.setPaneSize(functionMapZoneSize);
        Point p = new Point(0,0);
        int offX = p.x  + 10;
        int offY = p.y + 10;
        Dimension dim = new Dimension(windowSize.width - functionMapZoneSize.width-offX,windowSize.height-offY);
        legendZone.setLocation(mapZone.getX()+mapZone.getWidth(),mapZone.getY());
        legendZone.setPaneSize(dim);
        setSize(new Dimension((int)(windowSize.width*1.1),(int)(windowSize.height*1.1)));
        addStatusLabel();
    }

    public void setStatus(String value)
    {
        statusLabel.setText(value);
    }

    public void setField(Field field)
    {
        this.field = field;
        mapZone.setField(field);
        legendZone.setLegendConfig(field.getColors(),field.getMin(),field.getMax(),field.getNoDots(),field.getInterpol());
    }
    public void resetField()
    {
        setField(field);
    }

    private Point getAvailableStart()
    {
        Point ret = new Point(0,0);
        for (Component c: getComponents())
        {
            ret.y+=c.getY()+c.getHeight()+20;
        }
        return ret;
    }
    private void createZones(Field field,Dimension windowSize)
    {
        Dimension functionMapZoneSize = new Dimension(windowSize.width/2, windowSize.height*3/4);
        Point p = getAvailableStart();
        int offX = p.x  + 10;
        int offY = p.y + 10;
        MapZone mapZone = createMapZone(new Point(offX,offY),functionMapZoneSize);
        Dimension dim = new Dimension(windowSize.width - functionMapZoneSize.width-offX,windowSize.height-offY);
        createLegendZone(field,new Point(mapZone.getX()+mapZone.getWidth(),mapZone.getY()),dim);
        addStatusLabel();
    }

    private void addStatusLabel()
    {
        if (statusLabel == null)
            statusLabel = new JLabel();
        statusLabel.setLocation(mapZone.getX(),mapZone.getY()+mapZone.getHeight());
        statusLabel.setSize(new Dimension(mapZone.getWidth(),20));
        statusLabel.setVisible(true);
        add(statusLabel);
    }


    private LegendZone createLegendZone(Field field,Point p,Dimension dim)
    {
        int width = dim.width;
        int height = dim.height;
        legendZone = new LegendZone(new Dimension(width,height),field.getColors(),field.getMin(),field.getMax(),field.getNoDots());
        legendZone.setLocation(p.x,p.y);
        legendZone.setFocusable(false);
        add(legendZone);
        return legendZone;
    }

    private MapZone createMapZone(Point p,Dimension functionMapZoneSize)
    {
        mapZone = new MapZone(field,functionMapZoneSize);
        mapZone.setLocation(p.x,p.y);
        mapZone.setFocusable(false);
        add(mapZone);
        mapZone.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                controller.mousePressed(e);
            }

            public void mouseMoved(MouseEvent e) {
                controller.mouseMoved(e);
            }
        });
        mapZone.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mouseClicked(e);
                controller.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                controller.mouseReleased(e);
            }
        });
        return mapZone;
    }

    private void createMenus()
    {
        JMenuBar menuBar = new JMenuBar();
        JToolBar toolbar = createToolbar();
        toolbar.setLocation(0,0);
        createFileMenu(menuBar);
        createEffectMenu(menuBar);
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

    private  void createEffectMenu(JMenuBar menuBar) {
        JMenu effectMenu = new JMenu("Эффекты");
        menuBar.add(effectMenu);

        JMenuItem drawDefaultIsoLines = new JMenuItem(HINTS.isoLinesHINT);
        addMenuItem(effectMenu, drawDefaultIsoLines, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.isoLinesShow();
            }
        });

        JMenuItem drawExtraIsoLines = new JMenuItem(HINTS.addMouseIsoLines);
        addMenuItem(effectMenu, drawExtraIsoLines, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.extraIsoLines();
            }
        });

        JMenuItem diffLevel = new JMenuItem(HINTS.diffLevelHINT);
        addMenuItem(effectMenu, diffLevel, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.deferenceLevel();
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


        JButton openButton = new JButton();
        addButton(toolBar, openButton, HINTS.openHINT,"/open.png",new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.open();
            }
        });
        Dimension d = new Dimension();

        JButton interpolButton = new JButton();
        addButton(toolBar, interpolButton, HINTS.interpolHINT, "/interpol.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.interpol();
            }
        });

        final JButton colorMapButton = new JButton();
        addButton(toolBar, colorMapButton, HINTS.colorMapHINT, "/colorMap.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.colorMap();
            }
        });

        JButton gridShowButton = new JButton();
        addButton(toolBar, gridShowButton, HINTS.gridHINT, "/grid.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.showGrid();
            }
        });

        final JButton changeParamsButton = new JButton();
        addButton(toolBar, changeParamsButton, HINTS.paramHINT, "/param.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.changeParams();
            }
        });

        JButton isoLinesShowButton = new JButton();
        addButton(toolBar, isoLinesShowButton, HINTS.isoLinesHINT, "/isoLinesShow.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.isoLinesShow();
            }
        });
        JButton differenceLevelButton = new JButton();
        addButton(toolBar, differenceLevelButton, HINTS.diffLevelHINT, "/differenceLevel.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.deferenceLevel();
            }
        });

        JButton extraIsoLines = new JButton();
        addButton(toolBar, extraIsoLines, HINTS.addMouseIsoLines, "/extraIsoLines.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.extraIsoLines();
            }
        });

        JButton aboutButton = new JButton();
        addButton(toolBar, aboutButton, HINTS.aboutHINT, "/about.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.about();
            }
        });
        for (Component c : toolBar.getComponents())
        {
            d.setSize(d.width+c.getWidth(),c.getHeight());
        }
        toolBar.setSize(d);
        toolBar.setVisible(true);
        return toolBar;
    }

    protected static  ImageIcon createIcon(String path)
    {
        URL imgURL = GUI.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("File not found " + path);
            return null;
        }
    }

    private void addButton(JToolBar toolBar, JButton button, String hint, String name, ActionListener actionListener) {
        ImageIcon icon = createIcon(name);
        if (icon!=null)
        {
            button.setIcon(icon);
            button.setSize(icon.getIconWidth()+12,icon.getIconHeight()+12);
        }
        button.setToolTipText(hint);
        button.addActionListener(actionListener);
        toolBar.add(button);
    }
}
