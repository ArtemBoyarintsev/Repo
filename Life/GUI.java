package ru.nsu.graphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;


import java.awt.event.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

public class GUI extends JFrame implements Observer{
    static class HINTS {
        static public String xorHINT = "XOR Mode";
        static public String replaceHINT = "Replace Mode";
        static public String newHINT = "Create new field";
        static public String openHINT = "Open field";
        static public String saveHINT = "Save field";
        static public String nextStepHINT = "next state of the field";
        static public String runHINT = "game is starting...";
        static public String impactsHINT = "impacts will be shown";
        static public String clearHINT = "clear the field";
        static public String paramHINT = "setting params";
        static public String aboutHINT = "about";
    }

    private void addMenuItem(JMenu menu,JMenuItem item,ActionListener actionListener)
    {
        Font font = new Font("Verdana", Font.PLAIN, 11);
        menu.setFont(font);
        item.addActionListener(actionListener);
        menu.add(item);
    }

    public  void createFileMenu(JMenuBar menuBar) {

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem newMenu = new JMenuItem("New");
        addMenuItem(fileMenu,newMenu,new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.createNew();
            }
        });

        JMenuItem openItem = new JMenuItem("Open...");
        addMenuItem(fileMenu, openItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.open();
            }
        });

        JMenuItem saveAsItem = new JMenuItem("Save As...");
        addMenuItem(fileMenu, saveAsItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.save();
            }
        });

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        addMenuItem(fileMenu,exitItem,new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                controller.save();
                System.exit(0);
            }
        });

    }

    public  void createEditMenu(JMenuBar menuBar) {
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        JMenuItem xorItem = new JMenuItem("XOR");
        addMenuItem(editMenu, xorItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setMode(Controller.Mode.xor);
            }
        });

        JMenuItem replaceItem = new JMenuItem("Replace");
        addMenuItem(editMenu, replaceItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setMode(Controller.Mode.replace);
            }
        });

        JMenuItem clearItem = new JMenuItem("Clear");
        addMenuItem(editMenu, clearItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                field.makeClear();
            }
        });

        JMenuItem paramItem = new JMenuItem("Params");
        addMenuItem(editMenu, paramItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.param();
            }
        });
    }

    public  void createViewMenu(JMenuBar menuBar) {
        JMenu viewMenu = new JMenu("View");
        menuBar.add(viewMenu);

        JMenuItem displayImpacts = new JMenuItem("Display Impacts");
        addMenuItem(viewMenu, displayImpacts, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.impactsShowing();
            }
        });
    }

    public  void createSimulationMenu(JMenuBar menuBar) {

        JMenu simulationMenu = new JMenu("Simulation");
        menuBar.add(simulationMenu);

        JMenuItem runItem = new JMenuItem("Run");
        addMenuItem(simulationMenu, runItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.runGame();
            }
        });

        JMenuItem stepItem = new JMenuItem("Step");
        addMenuItem(simulationMenu, stepItem, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.nextState();
            }
        });
    }

    public void setHintInStateMessage(final FieldPanel pane,JButton button, final String hint) {
        button.addChangeListener(new ChangeListener() {
            boolean flag = false;
            public void stateChanged(ChangeEvent e) {
                if (!flag) {
                    pane.setState(hint);
                }
                if (flag)
                {
                    pane.setState("ready");
                }
                flag = !flag;
            }
        });
    }


    protected static  ImageIcon createIcon(String path) {

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
        button.setIcon(icon);
        button.setToolTipText(hint);
        button.addActionListener(actionListener);
        setHintInStateMessage(fieldPanel,button,hint);
        toolBar.add(button);
    }

    public  void createToolbar() {

        JToolBar toolBar = new JToolBar();

        add(toolBar);
        toolBar.setFloatable(false);
        toolBar.setVisible(true);

        final JButton newButton = new JButton();
        addButton(toolBar, newButton, HINTS.newHINT,"/new.png", new ActionListener() {
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

        JButton displayImpactButton = new JButton();
        addButton(toolBar, displayImpactButton, HINTS.impactsHINT,"/impactsShow.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.impactsShowing();
            }
        });

        JButton xorModeButton = new JButton();
        addButton(toolBar, xorModeButton, HINTS.xorHINT,"/xor.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setMode(Controller.Mode.xor);
            }
        });

        JButton replaceModeButton = new JButton();
        addButton(toolBar, replaceModeButton, HINTS.replaceHINT,"/replace.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setMode(Controller.Mode.replace);
            }
        });

        final JButton clearButton = new JButton();
        addButton(toolBar, clearButton, HINTS.clearHINT, "/clearField.png",new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.clear();
            }
        });

        final JButton paramButton = new JButton();
        addButton(toolBar, paramButton, HINTS.paramHINT, "/params.png",new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.param();
            }
        });

        final JButton stepButton = new JButton();
        addButton(toolBar, stepButton, HINTS.nextStepHINT, "/nextStep.png",new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.nextState();
            }
        });

        final JButton runButton = new JButton();
        addButton(toolBar, runButton, HINTS.runHINT, "/play.png",new ActionListener() {
            boolean flag = false;
            public void actionPerformed(ActionEvent e) {
                controller.setMouseForbidden(!flag);
                stepButton.setEnabled(flag);
                paramButton.setEnabled(flag);
                clearButton.setEnabled(flag);
                flag = !flag;
                controller.runGame();
            }
        });

        JButton aboutButton = new JButton();
        addButton(toolBar, aboutButton, HINTS.aboutHINT, "/about.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.about();
            }
        });
        add(toolBar, BorderLayout.PAGE_START);
    }

    private void createGUI() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JMenuBar menuBar = new JMenuBar();

        createFileMenu(menuBar);
        createEditMenu(menuBar);
        createViewMenu(menuBar);
        createSimulationMenu(menuBar);
        createToolbar();

        setPreferredSize(new Dimension(800,600));
        pack();
        setLocationRelativeTo(null);
        setJMenuBar(menuBar);
        setVisible(true);
    }

    Field field = null;
    FieldPanel fieldPanel = null;
    Controller controller = null;

    public void setCellSizeAndWeight(int cellSize,int weight) {
        fieldPanel.setLineLengthAndWidth(cellSize,weight);
    }


    public void setField(Field field)  {
        this.field.deleteObserver(this);
        this.field = field;
        fieldPanel.setField(field);
        this.field.addObserver(this);
        controller.setField(field);
        fieldPanel.repaint();
    }

    public int getWeight()
    {
        return fieldPanel.getLineWidth();
    }

    public int getCellSize()
    {
        return fieldPanel.getLineLength();
    }

    public FieldPanel getFieldPanel()
    {
        return fieldPanel;
    }

    public GUI(Field field,Controller c)
    {
        super("Life");
        this.controller = c;
        this.field = field;
        this.fieldPanel = new FieldPanel(field,30,10);
        this.fieldPanel.addMouseListener(new MouseListener() {
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
        this.fieldPanel.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                controller.mouseDragged(e);
            }

            public void mouseMoved(MouseEvent e) {

            }
        });
        JScrollPane jScrollPane = new JScrollPane(fieldPanel);
        createGUI();
        getContentPane().add(jScrollPane, BorderLayout.CENTER);
        field.addObserver(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                controller.save();
            }
        });
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void update(Observable o, Object arg)
    {
        if (field == o)
        {
            fieldPanel.repaint();
        }
    }
}