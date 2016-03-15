package ru.nsu.fit.g13201.boyarintsev;


import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import java.net.URL;

public class GUI extends JFrame {
    public static int offsetOnX = 10;
    public static int offsetOnY = 10;

    static class HINTS
    {
        static public String newHINT = "Отчистить поле";
        static public String openHINT = "Открыть изображение";
        static public String saveHINT = "Сохранить изображение из области 3";
        static public String blackAndWhiteHINT = "Черно-белый";
        static public String negativeHINT = "Негатив";
        static public String x2HINT = "Увеличить в два раза";
        static public String aboutHINT = "Информация о разработчике";
        static public String selectHINT = "Выбрать область картинки";
        static public String robertHINT = "Выделение границ(Роберт)";
        static public String smoothingHINT = "Сглаживание";
        static public String sobolHINT = "Выделение Границ(Соболь)";
        static public String ditherFSHINT = "Дизеринг (Флойда Стинберга)";
        static public String orderDitherHINT = "Упорядоченный дизеринг";
        static public String fromCToBHINT = "Копировать из области С в область B";
        static public String turnHINT = "Поворот";
        static public String sharpenHINT = "Повышение резкости";
        static public String stampingHINT = "Тиснение";
        static public String waterColorHINT = "Акварель";
        static public String gammaHINT = "Гамма коррекция";
        static public String exitHINT = "Выход";
        static public String hugePixelHINT = "Режим крупных пикселей";
        static public String littlePixelHINT = "Режим маленьких пикселей";
    }

    //private int zoneBCPixelSize = 1;
    private Controller controller = null;
    private ImagePanel imagePanel = null;
    private ImagePanel partImagePanel = null;
    private ImagePanel convertedImagePanel = null;

    public GUI(Controller c)
    {
        super("Filters");
        this.controller = c;
        createGUI();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public  ImagePanel getMainImagePanel() {return imagePanel;}
    public Field getFieldOfPartImage()
    {
        return partImagePanel.getField();
    }
    public Field getFieldOfMainImage()
    {
        return imagePanel.getField();
    }
    public Field getFieldOfConvertedImage()
    {
        return convertedImagePanel.getField();
    }

    public void setFieldOfMainImage(Field field)
    {
        imagePanel.setField(field);
        imagePanel.repaint();
    }

    public void setFieldOfPartImage(Field field)
    {
        partImagePanel.setField(field);
        partImagePanel.repaint();
    }
    public ImagePanel getConvertedImagePanel()
    {
        return convertedImagePanel;
    }
    public void setFieldOfConvertedImage(Field field)
    {
        convertedImagePanel.setField(field);
        convertedImagePanel.repaint();
    }

    public int getImageZoneWidth()
    {
        return imagePanel.getImageZoneSize().width;
    }
    public int getImageZoneHeight()
    {
        return imagePanel.getImageZoneSize().height;
    }

   /* public int getPaneWidth()
    {
        return imagePanel.getWidth();
    }
    public int getPaneHeight()
    {
        return imagePanel.getHeight();
    }*/

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


    static private void addListeners(ImagePanel panel,final Controller controller)
    {
        panel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {
                controller.mousePressed(e);
            }

            public void mouseReleased(MouseEvent e) {
                controller.mouseReleased(e);
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        panel.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                controller.mouseDragged(e);
            }

            public void mouseMoved(MouseEvent e) {

            }
        });
    }

    private  void createFileMenu(JMenuBar menuBar) {

        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        JMenuItem newMenu = new JMenuItem(HINTS.newHINT);
        addMenuItem(fileMenu,newMenu,new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.createNew();
            }
        });

        JMenuItem openItem = new JMenuItem(HINTS.openHINT);
        addMenuItem(fileMenu, openItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.open();
            }
        });

        JMenuItem saveAsItem = new JMenuItem(HINTS.saveHINT);
        addMenuItem(fileMenu, saveAsItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.save();
            }
        });

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem(HINTS.exitHINT);
        addMenuItem(fileMenu,exitItem,new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                controller.exit();
            }
        });

    }

    private  void createEffectMenu(JMenuBar menuBar) {
        JMenu effectMenu = new JMenu("Эффекты");
        menuBar.add(effectMenu);

        JMenuItem blackAndWhiteItem = new JMenuItem(HINTS.blackAndWhiteHINT);
        addMenuItem(effectMenu, blackAndWhiteItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.blackAndWhiteFilter();
            }
        });

        JMenuItem negativeItem = new JMenuItem(HINTS.negativeHINT);
        addMenuItem(effectMenu, negativeItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.makeNegativeFilter();
            }
        });

        JMenuItem ditherFSItem = new JMenuItem(HINTS.ditherFSHINT);
        addMenuItem(effectMenu, ditherFSItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.ditherFSFilter();
            }
        });

        JMenuItem orderDither = new JMenuItem(HINTS.orderDitherHINT);
        addMenuItem(effectMenu, orderDither, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.ditherOrderFilter();
            }
        });

        JMenuItem gammaItem = new JMenuItem(HINTS.gammaHINT);
        addMenuItem(effectMenu, gammaItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.xCorrectionFilter();
            }
        });

        JMenuItem robertDiffItem = new JMenuItem(HINTS.robertHINT);
        addMenuItem(effectMenu, robertDiffItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.differenceRobertsFilter();
            }
        });

        JMenuItem sharpenItem = new JMenuItem(HINTS.sharpenHINT);
        addMenuItem(effectMenu, sharpenItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.sharpnessIncreasingFilter();
            }
        });

        JMenuItem smoothingItem = new JMenuItem(HINTS.smoothingHINT);
        addMenuItem(effectMenu, smoothingItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.smoothing();
            }
        });

        JMenuItem turnItem = new JMenuItem(HINTS.turnHINT);
        addMenuItem(effectMenu, turnItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.turnFilter();
            }
        });

        JMenuItem stampingItem = new JMenuItem(HINTS.stampingHINT);
        addMenuItem(effectMenu, stampingItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.stamping();
            }
        });

        JMenuItem sobelDiffItem = new JMenuItem(HINTS.sobolHINT);
        addMenuItem(effectMenu, sobelDiffItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.differenceSobelFilter();
            }
        });

        JMenuItem waterColorItem = new JMenuItem(HINTS.waterColorHINT);

        addMenuItem(effectMenu, waterColorItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.waterColorFilter();
            }
        });
        JMenuItem x2Item = new JMenuItem(HINTS.x2HINT);

        addMenuItem(effectMenu, x2Item, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.increaseInTwoFilter();
            }
        });
    }

    private  void createViewMenu(JMenuBar menuBar) {
        JMenu viewMenu = new JMenu("Настройки");

        JMenuItem selectItem = new JMenuItem(HINTS.selectHINT);
        addMenuItem(viewMenu, selectItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.select();
            }
        });

        JMenuItem fromCtoBItem = new JMenuItem(HINTS.fromCToBHINT);
        addMenuItem(viewMenu, fromCtoBItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.copyFromCtoB();
            }
        });

        JMenuItem hugePixelItem = new JMenuItem(HINTS.hugePixelHINT);
        addMenuItem(viewMenu, hugePixelItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setHugePixelMode(true);
            }
        });
        JMenuItem littlePixelItem = new JMenuItem(HINTS.littlePixelHINT);
        addMenuItem(viewMenu, littlePixelItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setHugePixelMode(false);
            }
        });

        menuBar.add(viewMenu);
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
    private JToolBar createToolbar()
    {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setVisible(true);

        JButton newButton = new JButton();
        addButton(toolBar, newButton, HINTS.newHINT, "/new.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.createNew();
            }
        });

        JButton openButton = new JButton();
        addButton(toolBar, openButton, HINTS.openHINT,"/open.png",new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.open();
            }
        });

        JButton saveButton = new JButton();
        addButton(toolBar, saveButton, HINTS.saveHINT,"/save.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.save();
            }
        });

        JButton selectButton = new JButton();
        addButton(toolBar, selectButton, HINTS.selectHINT, "/select.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.select();
            }
        });

        JButton blackAndWhiteButton = new JButton();
        addButton(toolBar, blackAndWhiteButton, HINTS.blackAndWhiteHINT, "/blackAndWhite.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.blackAndWhiteFilter();
            }
        });

        JButton negativeButton = new JButton();
        addButton(toolBar, negativeButton, HINTS.negativeHINT, "/negative.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.makeNegativeFilter();
                }
        });

        JButton hugePixelButton = new JButton();
        addButton(toolBar, hugePixelButton, HINTS.hugePixelHINT, "/hugePixel.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setHugePixelMode(true);
            }
        });

        JButton littlePixelButton = new JButton();
        addButton(toolBar, littlePixelButton, HINTS.littlePixelHINT, "/littlePixel.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setHugePixelMode(false);
            }
        });
        JButton x2Button = new JButton();
        addButton(toolBar, x2Button, HINTS.x2HINT, "/x2.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.increaseInTwoFilter();
            }
        });

        JButton robertButton = new JButton();
        addButton(toolBar, robertButton, HINTS.robertHINT, "/robert.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.differenceRobertsFilter();
            }
        });
        JButton sobolButton = new JButton();
        addButton(toolBar, sobolButton, HINTS.sobolHINT, "/sobel.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.differenceSobelFilter();
            }
        });

        JButton ditherFSButton = new JButton();
        addButton(toolBar, ditherFSButton, HINTS.ditherFSHINT, "/dither.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.ditherFSFilter();
            }
        });
        JButton orderDitherButton = new JButton();
        addButton(toolBar, orderDitherButton, HINTS.orderDitherHINT, "/orderDither.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.ditherOrderFilter();
            }
        });

        JButton smoothingButton = new JButton();
        addButton(toolBar, smoothingButton, HINTS.smoothingHINT, "/smoothing.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.smoothing();
            }
        });

        JButton fromCtoBButton = new JButton();
        addButton(toolBar, fromCtoBButton, HINTS.fromCToBHINT, "/fromCtoB.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.copyFromCtoB();
            }
        });

        JButton turnButton = new JButton();
        addButton(toolBar, turnButton, HINTS.turnHINT, "/turn.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.turnFilter();
            }
        });
        JButton sharpenButton = new JButton();
        addButton(toolBar, sharpenButton, HINTS.sharpenHINT, "/sharpen.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.sharpnessIncreasingFilter();
            }
        });
        JButton stampingButton = new JButton();
        addButton(toolBar, stampingButton, HINTS.stampingHINT, "/stamping.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.stamping();
            }
        });
        JButton waterColorButton = new JButton();
        addButton(toolBar, waterColorButton, HINTS.waterColorHINT, "/watercolor.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.waterColorFilter();
            }
        });
        JButton gammaButton = new JButton();
        addButton(toolBar, gammaButton, HINTS.gammaHINT, "/gamma.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.xCorrectionFilter();
            }
        });
        JButton aboutButton = new JButton();
        addButton(toolBar, aboutButton, HINTS.aboutHINT, "/about.png", new ActionListener() {
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

    private void createGUI()
    {
        JMenuBar menuBar = new JMenuBar();

        createFileMenu(menuBar);
        createEffectMenu(menuBar);
        createViewMenu(menuBar);
        createAboutMenu(menuBar);
        JToolBar toolbar = createToolbar();
        toolbar.setLocation(0,0);
        int offY = toolbar.getHeight() + offsetOnY;
        this.imagePanel = new ImagePanel(new Dimension(352,352),true);
        this.imagePanel.setLocation(offsetOnX,offY);
        imagePanel.setFocusable(false);

        this.partImagePanel = new ImagePanel(new Dimension(352,352),false);
        this.partImagePanel.setLocation(imagePanel.getLocation().x + imagePanel.getWidth() + offsetOnX,offY);
        partImagePanel.setFocusable(false);

        this.convertedImagePanel = new ImagePanel(new Dimension(352,352),false);
        this.convertedImagePanel.setLocation(partImagePanel.getLocation().x + partImagePanel.getWidth() + offsetOnX,offY);
        convertedImagePanel.setFocusable(false);
        addListeners(imagePanel,controller);
        setLayout(null);
        add(imagePanel);
        add(partImagePanel);
        add(convertedImagePanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        add(toolbar);
        Dimension d = new Dimension(362*3+50,352+125);
        setSize(d);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
        pack();
        setLocationRelativeTo(null);
        setJMenuBar(menuBar);
        setVisible(true);
    }

    private void addMenuItem(JMenu menu,JMenuItem item,ActionListener actionListener)
    {
        Font font = new Font("Verdana", Font.PLAIN, 11);
        menu.setFont(font);
        item.addActionListener(actionListener);
        menu.add(item);
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