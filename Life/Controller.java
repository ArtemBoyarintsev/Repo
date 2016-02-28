package ru.nsu.graphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class Controller {
    enum Mode
    {
        xor,
        replace
    }
    private final static String DATA_PATH = "E:\\NSU\\Graphic\\Life\\Resources\\FIT_13201_Boyarintsev_Life_Data";
    private final static Integer WEIGHT_MIN = 1;
    private final static Integer WEIGHT_MAX = 10;
    private final static Integer CELL_SIZE_MIN = 15;
    private final static Integer CELL_SIZE_MAX = 100;
    private final static String WEIGHT_DEFAULT = "1";
    private final static String CELL_SIZE_DEFAULT = "50";

    final JLabel firstImpact = new JLabel("Fst Impact");
    final JLabel secondImpact = new JLabel("Snd Impact");
    final JLabel liveBegin = new JLabel("Live Begin");
    final JLabel liveEnd = new JLabel("Live End ");
    final JLabel birthBegin = new JLabel("Birth Begin");
    final JLabel birthEnd = new JLabel("Birth End");

    final JTextField firstImpactText = new JTextField("2.5",5);
    final JTextField secondImpactText = new JTextField("0.3",5);
    final JTextField liveBeginText = new JTextField("2",5);
    final JTextField liveEndText = new JTextField("3.3",3);
    final JTextField birthBeginText = new JTextField("2.3",5);
    final JTextField birthEndText = new JTextField("2.9",5);

    final JLabel fieldSizeLabel = new JLabel("Field Size");
    final JLabel width = new JLabel("Width");
    final JLabel height = new JLabel("Height");

    final JTextField widthText = new JTextField("30");
    final JTextField heightText = new JTextField("30");

    final JLabel cellPropertiesLabel = new JLabel("Cell Properties");
    final JLabel weight = new JLabel("Line Weight");
    final JLabel cellSize = new JLabel("Line Length");

    final JTextField weightText = new JTextField(WEIGHT_DEFAULT,4);
    final JTextField cellSizeText = new JTextField(CELL_SIZE_DEFAULT,4);

    final JSlider weightSlider = new JSlider(JSlider.HORIZONTAL, WEIGHT_MIN, WEIGHT_MAX,Integer.parseInt(WEIGHT_DEFAULT));
    final JSlider cellSizeSlider = new JSlider(JSlider.HORIZONTAL, CELL_SIZE_MIN, CELL_SIZE_MAX,Integer.parseInt(CELL_SIZE_DEFAULT));

    final JLabel modeLabel = new JLabel("Mode");
    final JRadioButton xor = new JRadioButton("xor");
    final JRadioButton replace = new JRadioButton("replace");

    Timer timer = null;
    Field field = null;
    GUI gui = null;
    boolean runFlag = false;
    boolean showImpactsFlag = false;
    boolean mouseForbidden = false;
    FieldPanel fieldPanel = null;
    Point lastPoint = null;
    private Mode mode = Mode.replace;

    static public  void main(String args[])
    {
        new Controller();
    }
    
    public Controller() {
        field = new Field(20,20);
        gui = new GUI(field,this);
        fieldPanel = gui.getFieldPanel();
    }

    public void setField(Field field) {this.field = field;}
    
    public void clear()
    {
        field.makeClear();
    }

    public void save() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(DATA_PATH));
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File file = fileChooser.getSelectedFile();
                if (null == file)
                {
                    return;
                }
                FileWriter writer = new FileWriter(file);
                BufferedWriter fileWriter = new BufferedWriter(writer);
                int width = field.getWidth();
                int height = field.getHeight();
                int weight = gui.getWeight();
                int cellSize = gui.getCellSize();
                int aliveCount = field.getAliveCount();
                Field.Cage[][] cages = field.getCages();
                fileWriter.write(((Integer)width).toString()+' '+ ((Integer)height).toString());
                fileWriter.newLine();
                fileWriter.write(((Integer)weight).toString());
                fileWriter.newLine();
                fileWriter.write(((Integer)cellSize).toString());
                fileWriter.newLine();
                fileWriter.write(((Integer)aliveCount).toString());
                fileWriter.newLine();
                for (int i = 0; i < cages.length; ++i)
                {
                    for ( int j = 0; j < cages[i].length; ++j)
                    {
                        if (cages[i][j].alive)
                        {
                            fileWriter.write(((Integer)i).toString()+ ' ');
                            fileWriter.write(((Integer)j).toString());
                            fileWriter.newLine();
                        }
                    }
                }
                fileWriter.close();
            }
            catch(IOException exception)
            {
                exception.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void param() {
        JDialog jFrame = new JDialog();
        jFrame.setLocation(200, 200); //TODO: FIX IT
        jFrame.setSize(new Dimension(530, 150));
        jFrame.setLayout(new FlowLayout());

        Point p = FieldSize(jFrame);
        p = mode(jFrame,new Point(p.x+5,0));
         cellProperties(jFrame,new Point(p.x +5 ,0));
        java.awt.Component[] components = jFrame.getContentPane().getComponents();
        int max = 0;
        for (java.awt.Component c : components)
        {
            if (max < c.getBounds().y + c.getHeight() )
            {
                max = c.getBounds().y + c.getHeight();
            }
        }
        p = new Point(0,max);
        p = rules(jFrame,p);
        addButtons(jFrame,new Point(p.x+3,p.y+10));
        jFrame.setVisible(true);
    }
    
    public void createNew() {
        final JDialog jDialog = new JDialog();
        Container container = jDialog.getContentPane();
        container.setLayout(null);
        JLabel label = new JLabel("Сохранить текущее поле?");
        label.setSize(label.getPreferredSize());
        JButton yesButton = new JButton("да");
        JButton noButton = new JButton("нет");
        JButton cancelButton = new JButton("отмена");
        jDialog.setSize(250,100);
        jDialog.setLocation(400,400);
        label.setVisible(true);

        label.setBounds(10, 10,label.getPreferredSize().width,label.getPreferredSize().height);
        yesButton.setBounds(label.getX(),label.getY() + label.getHeight() + 10,yesButton.getPreferredSize().width,yesButton.getPreferredSize().height);
        noButton.setBounds(yesButton.getX()+yesButton.getWidth() ,yesButton.getY(),noButton.getPreferredSize().width,noButton.getPreferredSize().height);
        cancelButton.setBounds(noButton.getX()+noButton.getWidth() ,noButton.getY(),cancelButton.getPreferredSize().width,cancelButton.getPreferredSize().height);
        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
                clear();
                param();
                jDialog.setVisible(false);
            }
        });
        noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
                param();
                jDialog.setVisible(false);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jDialog.setVisible(false);
            }
        });
        container.add(label);
        container.add(yesButton);
        container.add(noButton);
        container.add(cancelButton);
        jDialog.setVisible(true);
    }

    public void impactsShowing() {
        showImpactsFlag = !showImpactsFlag;
        fieldPanel.impactShow(showImpactsFlag);
        fieldPanel.repaint();
    }

    public void nextState() {
        field.nextState();
    }

    public void runGame() {
        runFlag = !runFlag;
        if (runFlag)
        {
            timer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    field.nextState();
                }
            });
            timer.start();
            return;
        }
        if (!runFlag )
        {
            if (timer != null)
            {
                timer.stop();
                timer = null;
            }
        }
    }

    public void open() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(DATA_PATH));
        fileChooser.showDialog(null,"Открыть");
        File file = fileChooser.getSelectedFile();
        if (file != null)
            read(file);
    }

    private Point mode(JDialog frame, Point start) {
        ButtonGroup buttonGroup = new ButtonGroup();
        Container panel = frame.getContentPane();

        settingSize(modeLabel,start);
        settingSize(xor,new Point(modeLabel.getX(),modeLabel.getY() + modeLabel.getHeight()));
        settingSize(replace,new Point(xor.getX(),xor.getY() + xor.getHeight()));
        replace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = Mode.replace;
            }
        });
        xor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = Mode.xor;
            }
        });
        buttonGroup.add(xor);
        buttonGroup.add(replace);
        panel.add(modeLabel);
        panel.add(xor);
        panel.add(replace);

        return new Point(start.x + replace.getWidth(),start.y);
    }

    private Point getMax5(JComponent a,JComponent b,JComponent c, JComponent d, JComponent e)  {
        int x = Math.max(a.getSize().width,b.getSize().width);
        x = Math.max(x,c.getSize().width);
        x = Math.max(x,d.getSize().width);
        x = Math.max(x,e.getSize().width);
        int y = Math.max(a.getSize().height,b.getHeight());
        y = Math.max(y,c.getHeight());
        y = Math.max(y,d.getHeight());
        y = Math.max(y, e.getHeight());
        return new Point(x,y);
    }

    private Point FieldSize(JDialog frame)  {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        settingSize(fieldSizeLabel,new Point(0,0));
        settingSize(width,new Point(fieldSizeLabel.getX(),fieldSizeLabel.getY() + fieldSizeLabel.getHeight()));
        settingSize(widthText,new Point(width.getX() + width.getWidth() + 3 , width.getY()));
        settingSize(height,new Point(fieldSizeLabel.getX(),width.getY() + width.getHeight() + 5));
        settingSize(heightText, new Point(height.getX() + height.getWidth() + 3,height.getY()));

        panel.add(fieldSizeLabel);
        panel.add(width);
        panel.add(height);
        panel.add(widthText);
        panel.add(heightText);
        panel.setVisible(true);
        frame.setContentPane(panel);
        return getMax5(fieldSizeLabel,width,height,heightText,widthText);
    }

    private Point cellProperties(JDialog frame,Point start) {
        Container panel = frame.getContentPane();

        settingSize(cellPropertiesLabel,start);
        settingSize(weight,new Point(cellPropertiesLabel.getX(),cellPropertiesLabel.getY() + cellPropertiesLabel.getHeight()));
        settingSize(weightText,new Point(weight.getX() + weight.getWidth() + 3, weight.getY()));
        settingSize(cellSize,new Point(cellPropertiesLabel.getX(),weight.getY() + weight.getHeight() + 5));
        settingSize(cellSizeText,new Point(cellSize.getX() + cellSize.getWidth()+3,cellSize.getY()));
        settingSize(cellSizeSlider,new Point(cellSizeText.getX() + cellSizeText.getWidth() + 3, cellSize.getY()));
        settingSize(weightSlider,new Point(cellSizeText.getX() + cellSizeText.getWidth() + 3, weight.getY()));
        cellSizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                Integer value = slider.getValue();
                cellSizeText.setText(value.toString());
                if (value >= CELL_SIZE_MIN)
                    fieldPanel.setLineLength(value);
            }
        });
        weightSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                Integer value = slider.getValue();
                weightText.setText(value.toString());
                if (value >= WEIGHT_MIN)
                    fieldPanel.setLineWidth(value);
            }
        });

        panel.add(cellPropertiesLabel);
        panel.add(weight);
        panel.add(cellSize);
        panel.add(weightText);
        panel.add(cellSizeText);
        panel.add(weightSlider);
        panel.add(cellSizeSlider);
        panel.setVisible(true);
        return new Point(start.x + cellPropertiesLabel.getWidth(),start.y);
    }
    public void about()
    {
        JDialog dialog = new JDialog();
        dialog.add(new Label("This is the life from FIT!"));
        dialog.setVisible(true);
        dialog.setSize(dialog.getPreferredSize());
        dialog.setLocation(500,300);
    }
    private void settingSize(java.awt.Component component,Point start)  {
        Dimension sizeFstImpact = component.getPreferredSize();
        Point fstImpactPosition = new Point(start.x,start.y);
        component.setBounds(fstImpactPosition.x,fstImpactPosition.y,sizeFstImpact.width,sizeFstImpact.height);
    }

    private Point rules(JDialog frame, Point start) {
        Container panel = frame.getContentPane();
        settingSize(firstImpact,start);
        settingSize(secondImpact,new Point(firstImpact.getX()+firstImpact.getWidth() + 5,firstImpact.getY()));
        settingSize(liveBegin,new Point(secondImpact.getX()+ secondImpact.getWidth() +5 , secondImpact.getY()));
        settingSize(liveEnd,new Point(liveBegin.getX() + liveBegin.getWidth() +5, liveBegin.getY()));
        settingSize(birthBegin,new Point(liveEnd.getX() + liveEnd.getWidth() +5 ,liveEnd.getY()));
        settingSize(birthEnd,new Point(birthBegin.getX() + birthBegin.getWidth() +5 ,birthBegin.getY()));

        settingSize(firstImpactText,new Point(firstImpact.getX(),firstImpact.getY()+firstImpact.getHeight()));
        settingSize(secondImpactText,new Point(secondImpact.getX(),secondImpact.getY() + secondImpact.getHeight()));
        settingSize(birthBeginText,new Point(birthBegin.getX(), birthBegin.getY() + birthBegin.getHeight()));
        settingSize(birthEndText,new Point(birthEnd.getX(),birthEnd.getY() + birthEnd.getHeight()));
        settingSize(liveBeginText,new Point(liveBegin.getX(),liveBegin.getY()+liveBegin.getHeight()));
        settingSize(liveEndText,new Point(liveEnd.getX(),liveEnd.getY() + liveEnd.getHeight()));

        panel.add(birthBegin);
        panel.add(birthEnd);
        panel.add(liveBegin);
        panel.add(liveEnd);
        panel.add(firstImpact);
        panel.add(secondImpact);
        panel.add(firstImpactText);
        panel.add(secondImpactText);
        panel.add(liveBeginText);
        panel.add(liveEndText);
        panel.add(birthBeginText);
        panel.add(birthEndText);
        return new Point(birthEndText.getX() + birthEndText.getWidth(),start.y);
    }

    private void addButtons(final JDialog frame, Point start) {
        Container pane = frame.getContentPane();
        JButton okButton = new JButton("ok");
        JButton cancelButton = new JButton("cancel");
        settingSize(okButton,start);
        settingSize(cancelButton,new Point(okButton.getX()+okButton.getWidth() + 2,okButton.getY()));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String width = widthText.getText();
                String height = heightText.getText();
                String lineWeight = weightText.getText();
                String cellSize = cellSizeText.getText();
                try
                {
                    int w = Integer.parseInt(width);
                    int h = Integer.parseInt(height);
                    int lw = Integer.parseInt(lineWeight);
                    int cz = Integer.parseInt(cellSize);
                    float fstImpact = Float.parseFloat(firstImpactText.getText());
                    float secImpact = Float.parseFloat(secondImpactText.getText());
                    float birthBegin = Float.parseFloat(birthBeginText.getText());
                    float birthEnd = Float.parseFloat(birthEndText.getText());
                    float liveBegin = Float.parseFloat(liveBeginText.getText());
                    float liveEnd = Float.parseFloat(liveEndText.getText());

                    if (w<1 || w > 100||h<1||h>100||cz<11 || cz>100||lw < 1||lw>10|| cz < lw)
                    {
                        throw new NumberFormatException();
                    }
                    if (fstImpact<0.0||fstImpact>=5.0||secImpact<0.0||secImpact>=5.0)
                    {
                        throw new NumberFormatException();
                    }
                    if (liveBegin<0.0||liveBegin>7.0||liveEnd<0.0||liveEnd>9.0||liveEnd < liveBegin)
                    {
                        throw new NumberFormatException();
                    }
                    if (birthBegin<0.0||birthBegin>7.0||birthEnd<0.0||birthEnd>9.0||birthEnd < birthBegin||birthBegin<liveBegin||birthEnd>liveEnd)
                    {
                        throw new NumberFormatException();
                    }
                    field.setFstImpact(fstImpact);
                    field.setSecImpact(secImpact);
                    field.setBirthBegin(birthBegin);
                    field.setBirthEnd(birthEnd);
                    field.setLiveBegin(liveBegin);
                    field.setLiveEnd(liveEnd);


                    gui.setCellSizeAndWeight(cz,lw);
                    field.setSizes(h,w);
                    gui.fieldPanel.repaint();

                    frame.setVisible(false);
                }
                catch(NumberFormatException exc)
                {
                    String v = "Ширина должна быть от 1 до 100\n";
                    v = v + "Высота должна быть от 1 до 100\n";
                    v = v + "Размер ячейки должен быть от 15 до 100\n";
                    v = v + "Ширина линии должна быть от 1 до 10\n";
                    v = v + "0.0<Live Begin<=7.0,0.0<liveEnd <= 9.0\n";
                    v = v+ "0.0 <= Birth Begin <= 7.0, 0.0<= Birth End <= 9.0\n";
                    JOptionPane.showMessageDialog(null,v);
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        });
        pane.add(okButton);
        pane.add(cancelButton);
    }

    private void read(File name) {
        try {
            Field field;
            Scanner sc = new Scanner(name);

            int m = sc.nextInt();
            int n = sc.nextInt();
            int w = sc.nextInt();
            int k = sc.nextInt();
            int all = sc.nextInt();

            field = new Field(n,m);

            gui.setCellSizeAndWeight(k,w);

            for ( int index = 0; index < all; ++index)
            {
                int i = sc.nextInt(); // по X
                int j = sc.nextInt(); // по Y
                field.setCageAlive(j,i);
            }
            gui.setField(field);
            sc.close();
        }
        catch(IOException ex)
        {
            System.err.println("io system broke...");
            System.exit(1);
        }
    }

    public void setMode(Mode mode)
    {
        this.mode = mode;
    }

    public void mousePressed(MouseEvent e) {
        if (mouseForbidden)
        {
            return;
        }
        setAlive(e);
    }

    public void setMouseForbidden(boolean v)
    {
        mouseForbidden = v;
    }

    public void mouseReleased(MouseEvent e) {
        lastPoint = null;
    }

    public void mouseDragged(MouseEvent e) {
        if (mouseForbidden)
        {
            return;
        }
        setAlive(e);
    }

    private void setAlive(MouseEvent e) {
        Point pp = fieldPanel.getCage(e.getPoint());
        if (null != pp &&null != lastPoint && pp.x == lastPoint.x && pp.y == lastPoint.y)
        {
            return;
        }
        if (pp != null) {
            if (mode == Mode.replace) {
                field.setCageAlive(pp.x, pp.y);
            }
            else
            {
                field.xorCageAlive(pp.x,pp.y);
            }
        }
        lastPoint = pp;
    }
}
