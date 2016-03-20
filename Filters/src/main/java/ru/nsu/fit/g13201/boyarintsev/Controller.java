package ru.nsu.fit.g13201.boyarintsev;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

class OkCancelPressed {
    private final Field storeField;
    private Controller controller;
    private Runnable runnable;

    OkCancelPressed(Field f, Controller controller, Runnable runnable) {
        storeField = f;
        this.runnable = runnable;
        this.controller = controller;
    }
    public Runnable getIfOkPressed()
    {
        return runnable;
    }

    public void cancelPressed() {
        controller.getGui().setFieldOfConvertedImage(storeField);
    }
}

interface Filter
{
    void apply();
}

class TurnFilter implements Filter
{
    final private static int ANGLE_MIN = -180;
    final private static int ANGLE_MAX = 180;

    final private JLabel turnLabel = new JLabel("Поворот");
    final private JSlider turnSlider = new JSlider(JSlider.HORIZONTAL, ANGLE_MIN, ANGLE_MAX, 0);
    final private JTextField turnText = new JTextField("0", 3);

    private Controller controller;
    TurnFilter(Controller controller)
    {
        this.controller = controller;
    }

    private void createTurnDialog(final OkCancelPressed okCancel)
    {
        turnSlider.setMinorTickSpacing(5);
        turnSlider.setPaintLabels(true);
        turnSlider.setMajorTickSpacing(50);
        turnSlider.setPaintTicks(true);
        turnSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                Integer v = slider.getValue();
                turnText.setText(v.toString());
                okCancel.getIfOkPressed().run();
            }
        });
        JComponent[] components = {turnLabel,turnText,turnSlider};
        ControllerUtils.createCancelDialog(okCancel,"Параметры поворота",components);
    }

    private void turnAlgorithm(int angle)
    {
        final  double PI = 3.1415;
        Field field = controller.getGui().getFieldOfPartImage();
        if (field == null)
        {
            return;
        }
        int width = field.getWidth();
        int height = field.getHeight();
        int[][] bitmap = field.getBitMap();
        int[][] bitmapOfConvertedImage = new int[width][height];
        double[][] rotMat = {{Math.cos(PI*angle/180),Math.sin(PI*angle/180)},{-Math.sin(PI*angle/180),Math.cos(PI*angle/180)}};
        Point centre = new Point(width/2,height/2);
        for ( int i = 0; i < width;++i)
        {
            for (int j = 0; j < height; ++j)
            {
                Point vector = new Point(i-centre.x,j-centre.y);
                vector = new Point((int)(rotMat[0][0]*vector.x+rotMat[0][1]*vector.y),(int)(rotMat[1][0]*vector.x+rotMat[1][1]*vector.y));
                vector = new Point(vector.x+centre.x,vector.y+centre.y);
                if (vector.x < field.getWidth() && vector.y < field.getHeight() && vector.x >=0 && vector.y>=0)
                    bitmapOfConvertedImage[i][j]=bitmap[vector.x][vector.y];
                else
                {
                    bitmapOfConvertedImage[i][j]= Color.WHITE.getRGB();
                }
            }
        }
        controller.getGui().setFieldOfConvertedImage(new Field(bitmapOfConvertedImage,width,height));
    }

    public void apply() {
        createTurnDialog(new OkCancelPressed(controller.getGui().getFieldOfConvertedImage(),controller,new Runnable(){
            public void run() {
                String value = turnText.getText();
                int v = ControllerUtils.getIntValue(value,ANGLE_MIN,ANGLE_MAX);
                if (v < ANGLE_MIN)
                {
                    return;
                }
                turnAlgorithm(v);
            }
        }));
    }
}

class BlackAndWhiteFilter implements Filter
{
    private GUI gui;
    BlackAndWhiteFilter(Controller controller)
    {
        this.gui = controller.getGui();
    }
    public void apply() {
        Field field = gui.getFieldOfPartImage();
        if (field == null) {
            return;
        }
        int width = field.getWidth();
        int height = field.getHeight();
        int[][] bitmap = field.getBitMap();
        int[][] bitmapOfConvertedImage = new int[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int color = bitmap[i][j];
                int cw = getBlackAndWhite(color);
                bitmapOfConvertedImage[i][j] = cw;
            }
        }
        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage, width, height));
    }
    private int getBlackAndWhite(int rgb)
    {
        Color c = new Color(rgb);
        int blue = c.getBlue();
        int green = c.getGreen();
        int red = c.getRed();
        int nGray = (int)(0.56 * green + 0.33 * red +
                0.11 * blue);

        return (0xff000000 | nGray << 16 |
                nGray << 8 | nGray);
    }
}

class NegativeFilter implements Filter
{
    private GUI gui;
    public NegativeFilter(Controller c)
    {
        gui = c.getGui();
    }
    public void apply() {
        Field field = gui.getFieldOfPartImage();
        if (field == null) {
            return;
        }
        int width = field.getWidth();
        int height = field.getHeight();
        int[][] bitmap = field.getBitMap();
        int[][] bitmapOfConvertedImage = new int[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int color = bitmap[i][j];
                int cw = getNegative(color);
                bitmapOfConvertedImage[i][j] = cw;
            }
        }
        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage, width, height));
    }
    private int getNegative(int rgb)
    {
        Color c = new Color(rgb);
        int blue =  255 - c.getBlue();
        int green = 255 - c.getGreen();
        int red = 255 - c.getRed();

        return new Color(red,green,blue).getRGB();
    }
}

class DitherFSFilter implements Filter
{
    final private static int DITHER_MIN = 2;
    final private static int DITHER_MAX = 200;

    final private JLabel redLevelsLabel = new JLabel("Уровни красного");
    final private JTextField redLevelsText = new JTextField("5", 2);
    final private JLabel greenLevelLabels = new JLabel("Уровни зеленого");
    final private JTextField greenLevelText = new JTextField("5", 2);
    final private JLabel blueLevelsLabel = new JLabel("Уровни синего  ");
    final private JTextField blueLevelsText = new JTextField("5", 2);
    private GUI gui;
    private Controller controller;

    public DitherFSFilter(Controller controller)
    {
        this.controller = controller;
        gui = controller.getGui();
    }

    public void apply()
    {
        createDitherMenu(new OkCancelPressed(gui.getFieldOfConvertedImage(), controller, new Runnable() {
            public void run() {
                try {
                    String red = redLevelsText.getText();
                    String green = greenLevelText.getText();
                    String blue = blueLevelsText.getText();
                    int r = ControllerUtils.getIntValue(red,DITHER_MIN,DITHER_MAX);
                    int g = ControllerUtils.getIntValue(green,DITHER_MIN,DITHER_MAX);
                    int b = ControllerUtils.getIntValue(blue,DITHER_MIN,DITHER_MAX);
                    if (r < DITHER_MIN|| g<DITHER_MIN||b<DITHER_MIN)
                    {
                        return;
                    }
                    ditherFSAlgorithm(r,g,b);
                }
                catch(NumberFormatException ex)
                {
                    String v = "Цвета должны быть в диапазоне от 1 до 255\n";
                    JOptionPane.showMessageDialog(null,v);
                }
            }
        }));
    }

    private void createDitherMenu(final OkCancelPressed runIfOkPress)
    {
        final JDialog jDialog = new JDialog();
        JPanel panel = new JPanel();
        panel.setLayout(null);

        jDialog.setTitle("Параметры Дизеринга");
        jDialog.setLocation(200,200);
        jDialog.setSize(new Dimension(200,180));

        ControllerUtils.settingSize(redLevelsLabel,new Point(20,20));
        ControllerUtils.settingSize(redLevelsText,new Point(redLevelsLabel.getX()+ redLevelsLabel.getWidth()+10, redLevelsLabel.getY()));

        int width = redLevelsLabel.getWidth();
        ControllerUtils.settingSize(greenLevelLabels,new Point(redLevelsLabel.getX(), redLevelsLabel.getY()+ redLevelsLabel.getHeight()+7));
        ControllerUtils.settingSize(greenLevelText,new Point(greenLevelLabels.getX()+ width+10, greenLevelLabels.getY()));

        ControllerUtils.settingSize(blueLevelsLabel,new Point(greenLevelLabels.getX(), greenLevelLabels.getY()+ greenLevelLabels.getHeight()+7));
        ControllerUtils.settingSize(blueLevelsText,new Point(blueLevelsLabel.getX()+ width+10, blueLevelsLabel.getY()));


        panel.add(redLevelsLabel);
        panel.add(redLevelsText);
        panel.add(greenLevelLabels);
        panel.add(greenLevelText);
        panel.add(blueLevelsLabel);
        panel.add(blueLevelsText);


        ControllerUtils.addButtons(blueLevelsLabel,runIfOkPress,jDialog,panel);
        jDialog.setContentPane(panel);
        jDialog.setVisible(true);
    }

    private void ditherFSAlgorithm(int redPalette,int greenPalette,int bluePalette)
    {
        Field field = gui.getFieldOfPartImage();
        if (field == null)
        {
            return;
        }
        int width = field.getWidth();
        int height = field.getHeight();
        int[][] bitmap = field.getBitMap();
        int[][] bitmapOfConvertedImage = new int[width][height];

        for (int i = 0 ; i < bitmapOfConvertedImage.length;++i)
        {
            System.arraycopy(bitmap[i],0,bitmapOfConvertedImage[i],0,bitmapOfConvertedImage[i].length);
            /*for(int j = 0 ; j < bitmapOfConvertedImage[i].length;++j)
            {

                bitmapOfConvertedImage[i][j] = bitmap[i][j];
            }*/
        }
        for(int i=1;i<width-1;i++)
        {
            for(int j=0;j<height-1;j++)
            {
                fsAlgorithmStep(bitmapOfConvertedImage,i,j,redPalette,greenPalette,bluePalette);
            }
        }
        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage,width,height));
    }

    private void addErrors(int[][] bitmapOfConvertedImage,int i,int j,int errorRed,int errorGreen,int errorBlue)
    {
        {
            Color c = new Color(bitmapOfConvertedImage[i + 1][j]);
            int finalRed = c.getRed() + 7 * errorRed / 16;
            int finalGreen = c.getGreen() +7 * errorGreen / 16;
            int finalBlue = c.getBlue() + 7 * errorBlue / 16;

            if (finalBlue > 255)
                finalBlue = 255;
            if (finalBlue < 0)
                finalBlue = 0;
            if (finalGreen > 255)
                finalGreen = 255;
            if (finalGreen < 0)
                finalGreen = 0;
            if (finalRed > 255)
                finalRed = 255;
            if (finalRed < 0)
                finalRed = 0;
            bitmapOfConvertedImage[i + 1][j] = new Color(finalRed, finalGreen, finalBlue).getRGB();
        }
        {
            Color c = new Color(bitmapOfConvertedImage[i - 1][j+1]);
            int finalRed = c.getRed() + 3 * errorRed / 16;
            int finalGreen = c.getGreen() + 3 * errorGreen / 16;
            int finalBlue = c.getBlue() +  3* errorBlue / 16;

            if (finalBlue > 255)
                finalBlue = 255;
            if (finalBlue < 0)
                finalBlue = 0;
            if (finalGreen > 255)
                finalGreen = 255;
            if (finalGreen < 0)
                finalGreen = 0;
            if (finalRed > 255)
                finalRed = 255;
            if (finalRed < 0)
                finalRed = 0;
            bitmapOfConvertedImage[i  - 1][j + 1 ] = new Color(finalRed, finalGreen, finalBlue).getRGB();
        }
        {
            Color c = new Color(bitmapOfConvertedImage[i ][j+1]);
            int finalRed = c.getRed() + 5 * errorRed / 16;
            int finalGreen = c.getGreen() + 5 * errorGreen / 16;
            int finalBlue = c.getBlue() + 5 * errorBlue / 16;

            if (finalBlue > 255)
                finalBlue = 255;

            if (finalBlue < 0)
                finalBlue = 0;

            if (finalGreen > 255)
                finalGreen = 255;

            if (finalGreen < 0)
                finalGreen = 0;

            if (finalRed > 255)
                finalRed = 255;

            if (finalRed < 0)
                finalRed = 0;
            bitmapOfConvertedImage[i ][j + 1 ] = new Color(finalRed, finalGreen, finalBlue).getRGB();
        }
        {
            Color c = new Color(bitmapOfConvertedImage[i + 1][j+1]);
            int finalRed = c.getRed() + 1* errorRed / 16;
            int finalGreen = c.getGreen() + 1* errorGreen / 16;
            int finalBlue = c.getBlue() +1*errorBlue / 16;

            if (finalBlue > 255)
                finalBlue = 255;
            if (finalBlue < 0)
                finalBlue = 0;
            if (finalGreen > 255)
                finalGreen = 255;
            if (finalGreen < 0)
                finalGreen = 0;
            if (finalRed > 255)
                finalRed = 255;
            if (finalRed < 0)
                finalRed = 0;
            bitmapOfConvertedImage[i  +  1][j  +  1] = new Color(finalRed, finalGreen, finalBlue).getRGB();
        }
    }

    private void fsAlgorithmStep(int[][] bitmapOfConvertedImage,int i,int j,int redPalette,int greenPalette,int bluePalette)
    {
        Color c = new Color(bitmapOfConvertedImage[i][j]);
        int red = c.getRed();
        int newRed = getClosestColor(red,redPalette);
        int errorRed = red - newRed ;
        int green = c.getGreen();
        int newGreen = getClosestColor(green,greenPalette);
        int errorGreen = green - newGreen ;
        int blue = c.getBlue();
        int newBlue = getClosestColor(blue,bluePalette);
        int errorBlue = blue - newBlue ;
        addErrors(bitmapOfConvertedImage,i,j,errorRed,errorGreen,errorBlue);
        bitmapOfConvertedImage[i][j] = new Color(newRed,newGreen,newBlue).getRGB();
    }

    private int getClosestColor(int color,int paletteLevel)
    {
        int step = 255/(paletteLevel-1);
        int closest = color/step;
        if (color - closest * step < (closest+1)*(step) - color)
        {
            return closest*step;
        }
        return (closest+1)*(step);
    }
}

class OrderDitherFilter implements Filter
{
    GUI gui;
    OrderDitherFilter(Controller controller)
    {
        gui  = controller.getGui();
    }
    public void apply() {
        int[][] thresholdMap = {
                {3,1},
                {0,2}
        };
        Field field = gui.getFieldOfPartImage();
        if (field == null)
        {
            return;
        }
        int width = field.getWidth();
        int height = field.getHeight();
        int[][] bitmap = field.getBitMap();
        int[][] bitmapOfConvertedImage = new int[width][height];
        for (int i = 0 ; i < bitmapOfConvertedImage.length;++i)
        {
            System.arraycopy(bitmap[i],0,bitmapOfConvertedImage[i],0,bitmapOfConvertedImage[i].length);
        }
        for(int i=0;i<width;i++)
        {
            for(int j=0;j<height;j++)
            {
                int red = new Color(bitmap[i][j]).getRed();
                int blue = new Color(bitmap[i][j]).getBlue();
                int green = new Color(bitmap[i][j]).getGreen();
                int nRed = red  + (red*thresholdMap[ i % 2 ][ j % 2 ]);
                int nGreen = green + green* thresholdMap[ i % 2 ][ j % 2 ];
                int nBlue = blue + blue*thresholdMap[i%2][j%2];
                if ( nRed > 255)
                {
                    nRed = 255;
                }
                if (nGreen > 255)
                {
                    nGreen = 255;
                }
                if (nBlue > 255)
                {
                    nBlue = 255;
                }
                bitmapOfConvertedImage[i][j] = new Color(nRed,nGreen,nBlue).getRGB();
            }
        }
        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage,width,height));
    }
}

class PowerTwoFilter implements Filter
{
    GUI gui;
    PowerTwoFilter(Controller c)
    {
        gui = c.getGui();
    }
    public void apply() {
        Field field = gui.getFieldOfPartImage();
        if (field == null)
        {
            return;
        }
        int width = field.getWidth();
        int height = field.getHeight();
        int[][] bitmap = field.getBitMap();
        int[][] bitmapOfConvertedImage = new int[width][height];
        for (int i = width/4; i < 3 * width / 4;++i)
        {
            for(int j =  height/4; j < 3*(height-height%2)/4; ++j)
            {
                setInterpolInBitmapOfConverted(bitmap,bitmapOfConvertedImage,i,j,width/4,height/4);
            }
        }
        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage,width,height));
    }
    private void setInterpolInBitmapOfConverted(int[][] bitmap,int[][] bitmapOfConverted,int i, int j,int iStart,int jStart)
    {
        int c1 = bitmap[i][j];
        int c2 = bitmap[i][j+1];
        int c3 = bitmap[i+1][j];
        int c4 = bitmap[i+1][j+1];
        i = i - iStart;
        j = j - jStart;

        bitmapOfConverted[2*i][2*j] = c1;
        bitmapOfConverted[2*i][2*j+1] = getMedium(c1, c2);
        bitmapOfConverted[2*i+1][2*j] = getMedium(c1,c3);
        bitmapOfConverted[2*i+1][2*j+1] = getMedium(c1,c4);
    }
    private int getMedium(int c1,int c2)
    {
        Color color1 = new Color(c1);
        Color color2 = new Color(c2);
        Color ret = new Color((color1.getRed() + color2.getRed())/2,(color1.getGreen() + color2.getGreen())/2,(color1.getBlue() + color2.getBlue())/2);
        return ret.getRGB();
    }
}

class DiffRoberts implements Filter
{
    private GUI gui;
    private Controller controller;

    final private JLabel edgeSelectionRoberts = new JLabel("Edge Selection");
    final private JTextField edgeSelectionTextRoberts = new JTextField("16", 4);
    final private JSlider edgeSelectionSliderRoberts = new JSlider(JSlider.HORIZONTAL, SELECTION_MIN, SELECTION_MAX, 16);

    final private static int SELECTION_MIN = 0;
    final private static int SELECTION_MAX = 255;

    public DiffRoberts(Controller controller)
    {
        gui = controller.getGui();
        this.controller = controller;
    }

    public void apply() {
        DifferenceUtils.createEdgeSelectionDialog(edgeSelectionRoberts, edgeSelectionTextRoberts, edgeSelectionSliderRoberts,
                new OkCancelPressed(gui.getFieldOfConvertedImage(),controller,new Runnable() {
                    public void run() {
                        Field field = gui.getFieldOfPartImage();
                        if (field == null)
                        {
                            return;
                        }
                        int width = field.getWidth();
                        int height = field.getHeight();
                        int[][] bitmap = field.getBitMap();
                        int[][] bitmapOfConvertedImage = new int[width][height];
                        String value = edgeSelectionTextRoberts.getText();
                        int v = ControllerUtils.getIntValue(value,SELECTION_MIN,SELECTION_MAX);
                        if ( v== -1 )
                        {
                            return;
                        }
                        for (int i = 0; i < width-1;++i)
                        {
                            for(int j = 0; j < height-1; ++j)
                            {
                                int tmp1 = Math.abs(ControllerUtils.getAbsoluteValue(bitmap[i][j]) - ControllerUtils.getAbsoluteValue(bitmap[i+1][j+1]));
                                int tmp2 = Math.abs(ControllerUtils.getAbsoluteValue(bitmap[i+1][j]) - ControllerUtils.getAbsoluteValue(bitmap[i][j+1]));
                                double s = Math.sqrt(tmp1*tmp1 + tmp2*tmp2);
                                if (v <= s)
                                {
                                    bitmapOfConvertedImage[i][j] = Color.WHITE.getRGB();
                                }
                                else
                                {
                                    bitmapOfConvertedImage[i][j] = Color.BLACK.getRGB();
                                }
                            }
                        }
                        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage,width,height));
                    }
                }));
    }
}

class DiffSobel implements Filter
{
    private GUI gui;
    private Controller controller;

    final private static int SELECTION_MIN = 0;
    final private static int SELECTION_MAX = 255;
    final private JLabel edgeSelectionSo = new JLabel("Edge Selection");
    final private JTextField edgeSelectionTextSo = new JTextField("85", 4);
    final private JSlider edgeSelectionSliderSo = new JSlider(JSlider.HORIZONTAL, SELECTION_MIN, SELECTION_MAX, 85);

    DiffSobel(Controller controller)
    {
        this.gui =controller.getGui();
        this.controller = controller;
    }
    public void apply() {
        DifferenceUtils.createEdgeSelectionDialog(edgeSelectionSo, edgeSelectionTextSo, edgeSelectionSliderSo,
                new OkCancelPressed(gui.getFieldOfConvertedImage(),controller,new Runnable() {
                    public void run() {

                        Field field = gui.getFieldOfPartImage();
                        if (field == null)
                        {
                            return;
                        }
                        int width = field.getWidth();
                        int height = field.getHeight();
                        int[][] bitmap = field.getBitMap();
                        int[][] bitmapOfConvertedImage = new int[width][height];
                        int v = edgeSelectionSliderSo.getValue();
                        for (int i = 0; i < width-2;++i)
                        {
                            for(int j = 0; j < height-2; ++j)
                            {
                                int sx = ControllerUtils.getAbsoluteValue(bitmap[i+2][j])+2*ControllerUtils.getAbsoluteValue(bitmap[i+2][j+1]) + ControllerUtils.getAbsoluteValue(bitmap[i+2][j+2])
                                        - ControllerUtils.getAbsoluteValue(bitmap[i][j]) - 2 * ControllerUtils.getAbsoluteValue(bitmap[i][j+1]) - ControllerUtils.getAbsoluteValue(bitmap[i][j+2]);
                                int sy = ControllerUtils.getAbsoluteValue(bitmap[i][j+2]) + 2*ControllerUtils.getAbsoluteValue(bitmap[i+1][j+2])+ControllerUtils.getAbsoluteValue(bitmap[i+2][j+2])
                                        - ControllerUtils.getAbsoluteValue(bitmap[i][j]) - 2* ControllerUtils.getAbsoluteValue(bitmap[i+1][j])- ControllerUtils.getAbsoluteValue(bitmap[i+2][j]);
                                double sum = Math.sqrt(sx*sx+sy*sy);
                                if (v <= sum)
                                {
                                    bitmapOfConvertedImage[i][j] = Color.WHITE.getRGB();
                                }
                                else
                                {
                                    bitmapOfConvertedImage[i][j] = Color.BLACK.getRGB();
                                }
                            }
                        }
                        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage,width,height));
                    }
                }));
    }
}

class SmoothingFilter implements Filter
{
    private GUI gui;
    public SmoothingFilter(Controller c)
    {
        gui = c.getGui();
    }
    public void apply() {
        Field field = gui.getFieldOfPartImage();
        if (field == null) {
            return;
        }
        int width = field.getWidth();
        int height = field.getHeight();
        int[][] bitmap = field.getBitMap();
        int[][] bitmapOfConvertedImage = new int[width][height];
        for (int i = 0; i < width ; ++i) {
            for (int j = 0; j < height ; ++j) {
                int color = getColorForSmoothing(bitmap, i, j);
                bitmapOfConvertedImage[i][j] = color;
            }
        }
        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage, width, height));
    }
    private int getColorForSmoothing(int[][] bitmap,int i,int j)
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int k = i-2;k<i+2;++k)
        {
            for(int r = j-2;r<j+2;++r)
            {
                if (k>=0 && k<bitmap.length&&r>=0&& r<bitmap[k].length)
                    list.add(bitmap[k][r]);
            }
        }
        list.sort(new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                int ab1 = ControllerUtils.getAbsoluteValue(o1);
                int ab2 = ControllerUtils.getAbsoluteValue(o2);
                if (ab1<ab2)
                {
                    return -1;
                }
                if (ab1 == ab2)
                {
                    return 0;
                }
                else
                    return 1;
            }
        });
        return list.get(list.size()/2);
    }

}

class SharpenFilter implements Filter
{
    private GUI gui;
    private Field field;
    public SharpenFilter(Controller c,Field field)
    {
        gui = c.getGui();
        this.field = field;
    }
    public void apply()
    {
        if (field == null)
        {
            return;
        }
        sharpnessIncrease(field);
    }
    private void sharpnessIncrease(Field field)
    {
        int width = field.getWidth();
        int height = field.getHeight();

        int[][] bitmapOfConvertedImage = new int[width][height];
        double[][] kernel ={{-0.1,-0.1,-0.1},{-0.1,1.8,-0.1},{-0.1,-0.1,-0.1}};
        for (int i = 0; i < width ;++i)
        {
            for(int j = 0; j < height; ++j)
            {
                ControllerUtils.kernelHandle(field,bitmapOfConvertedImage,kernel,i,j);
            }
        }
        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage,width,height));
    }
}

class StampingFilter implements Filter
{
    private GUI gui;
    StampingFilter(Controller c)
    {
        gui = c.getGui();
    }
    public void apply() {
        Field field = gui.getFieldOfPartImage();
        if (field == null)
        {
            return;
        }
        int width = field.getWidth();
        int height = field.getHeight();
        int[][] bitmapOfConvertedImage = new int[width][height];
        double[][] kernel ={{0,1,2},{0,0,0},{0,-1,-2}};
        //p.s. Тиснение сильно зависит от матрицы выше.
        for (int i = 0; i < width ;++i)
        {
            for(int j = 0; j < height; ++j)
            {
                ControllerUtils.kernelHandle(field,bitmapOfConvertedImage,kernel,i,j);
            }
        }
        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage,width,height));
    }
}

class xRayFilter implements Filter
{
    final private static int GAMMA_MIN = 0;
    final private static int GAMMA_STEP = 10;
    final private static int GAMMA_MAX = 20;

    final private JLabel gammaLabel = new JLabel("Гамма");
    final private JSlider gammaSlider = new JSlider(JSlider.HORIZONTAL, GAMMA_MIN, GAMMA_MAX, 1);
    final private JTextField gammaText = new JTextField("0", 3);
    private GUI gui;
    private Controller controller;
    public xRayFilter(Controller c)
    {
        gui = c.getGui();
        this.controller = c;
    }
    public void apply() {
        createXCorrectionDialog(new OkCancelPressed(gui.getFieldOfConvertedImage(),controller,new Runnable() {
            public void run() {
                String value = gammaText.getText();
                double v = ControllerUtils.getDoubleValue(value,GAMMA_MIN,GAMMA_MAX/GAMMA_STEP);
                if (v < GAMMA_MIN)
                {
                    return;
                }
                gammaAlgorithm(v);
            }
        }));
    }
    private void createXCorrectionDialog(final OkCancelPressed okCancel)
    {
        gammaSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                Integer v = slider.getValue();
                Double value = (double)v/10;
                gammaText.setText(value.toString());
                okCancel.getIfOkPressed().run();
            }
        });
        JComponent[] components = {gammaLabel,gammaText,gammaSlider};
        ControllerUtils.createCancelDialog(okCancel,"Параметры Гамма Коррекции",components);
    }
    private void gammaAlgorithm(double gamma)
    {
        Field field = gui.getFieldOfPartImage();
        if (field == null)
        {
            return;
        }
        int width = field.getWidth();
        int height = field.getHeight();
        int[][] bitmap = field.getBitMap();
        int[][] bitmapOfConvertedImage = new int[width][height];
        for (int i = 0; i < width ; ++i) {
            for (int j = 0; j < height ; ++j) {
                Color c = new Color(bitmap[i][j]);
                int red = getGammaColor(c.getRed(),gamma);
                int green = getGammaColor(c.getGreen(),gamma);
                int blue = getGammaColor(c.getBlue(),gamma);
                int color = new Color(red,green,blue).getRGB();
                bitmapOfConvertedImage[i][j] = color;
            }
        }
        gui.setFieldOfConvertedImage(new Field(bitmapOfConvertedImage, width, height));
    }

    private int getGammaColor(int c,double gamma)
    {
        double floatC = (double)c/255;
        double floatRet = Math.pow(floatC,gamma);
        return (int)(floatRet*255);
    }
}

class WaterColorFilter implements Filter
{
    private GUI gui;
    private Controller controller;

    public WaterColorFilter(Controller controller)
    {
        this.controller= controller;
        this.gui = controller.getGui();
    }
    public void apply() {
        new SmoothingFilter(controller).apply();
        Field field = gui.getFieldOfConvertedImage();
        if (field == null)
        {
            return;
        }
        new SharpenFilter(controller,field).apply();
    }
}

public class Controller {
    final static private int hugePixelSize = 10;
     boolean hugePixelMode = false;

    private boolean selectMode = false;
    static public void main(String args[]) {
        new Controller();
    }

    public Controller()
    {
        File file = new File(DATA_PATH);
        if (!file.exists() || !file.isDirectory()) {
            boolean ret = file.mkdir();
            if (!ret) {
                return;
            }
        }
        gui = new GUI(this);
    }

    public GUI getGui()
    {
        return gui;
    }

    public void createNew()
    {
        ImagePanel convertedPanel = gui.getConvertedImagePanel();
        if (convertedPanel.wasChanged()) {
            save(new Runnable() {
                public void run() {
                    clear();
                }
            });
            return;
        }
        clear();
    }

    public void save()
    {
        save(new Runnable() {
            public void run()
            {

            }
        });
    }

    public void clear()
    {
        gui.setFieldOfConvertedImage(null);
        gui.setFieldOfPartImage(null);
        gui.setFieldOfMainImage(null);
    }

    public void open()
    {
        if (gui.getConvertedImagePanel().wasChanged()) {
            save(new Runnable() {
                public void run() {
                    clear();
                    openFile();
                }
            });
            return;
        }
        clear();
        openFile();
    }

    public void exit()
    {
        ImagePanel converted = gui.getConvertedImagePanel();
        if (converted.wasChanged())
        {
            save(new Runnable() {
                public void run() {
                    System.exit(1);
                }
            });
        }
    }

    public void blackAndWhiteFilter()
    {
        new BlackAndWhiteFilter(this).apply();
    }

    public void makeNegativeFilter()
    {
        new NegativeFilter(this).apply();
    }

    public void ditherFSFilter()
    {
        new DitherFSFilter(this).apply();
    }

    public void turnFilter()
    {
        new TurnFilter(this).apply();
    }

    public void ditherOrderFilter()
    {
        new OrderDitherFilter(this).apply();
    }

    public void increaseInTwoFilter()
    {
        new PowerTwoFilter(this).apply();
    }

    public void differenceRobertsFilter()
    {
        new DiffRoberts(this).apply();
    }

    public void differenceSobelFilter()
    {
        new DiffSobel(this).apply();
    }
    //сглаживание. Медианный фильтр.
    public void smoothing() {
        new SmoothingFilter(this).apply();
    }

    public void sharpnessIncreasingFilter() {
        new SharpenFilter(this,gui.getFieldOfPartImage()).apply();
    }

    public void stamping() {
        new StampingFilter(this).apply();
    }

    public void waterColorFilter() {
        new WaterColorFilter(this).apply();
    }

    public void xCorrectionFilter() {
        new xRayFilter(this).apply();
    }

    public void copyFromCtoB()
    {
        Field partField = gui.getFieldOfConvertedImage();
        gui.setFieldOfPartImage(partField);
    }

    public void about()
    {
        String v = "Этот Фильтр разработан на\n";
        v += "Факультете Информационных технологий НГУ\n";
        v = v + "Версия 1.0\n";
        v = v + "(c) Бояринцев Артем\n";
        JOptionPane.showMessageDialog(null, v);
    }

    public void select()
    {
        selectMode = !selectMode;
        if (!selectMode)
        {
            gui.getMainImagePanel().clearSquare();
        }
    }

    public void setHugePixelMode(boolean v)
    {
        hugePixelMode = v;
    }

    private void hugePixel(int[][] bitmap,int width,int height,int pixelSize)
    {
        for(int i=0;i<width;i+=pixelSize)
        {
            for(int j=0;j<height;j+=pixelSize)
            {
                int red = 0;
                int green = 0;
                int blue = 0;
                for (int k = 0; k < pixelSize;++k) {
                    for (int r = 0; r < pixelSize; ++r) {
                        Color c = new Color(bitmap[i + k][j + r]);
                        red += c.getRed();
                        green += c.getGreen();
                        blue += c.getBlue();
                    }
                }
                Color newColor = new Color(red /(pixelSize*pixelSize),green/(pixelSize*pixelSize),blue/(pixelSize*pixelSize));
                for (int k = 0; k < pixelSize;++k)
                {
                    for (int r = 0; r < pixelSize; ++r)
                    {
                        bitmap[i + k][j + r] = newColor.getRGB();
                    }
                }
            }
        }
    }

    private  void save(final Runnable toRun)
    {
        final JDialog jDialog = new JDialog();
        Container container = jDialog.getContentPane();
        container.setLayout(null);
        JLabel label = new JLabel("Сохранить текущее поле?");
        label.setSize(label.getPreferredSize());
        JButton yesButton = new JButton("да");
        JButton noButton = new JButton("нет");
        JButton cancelButton = new JButton("отмена");
        jDialog.setSize(250, 100);
        jDialog.setLocation(400, 400);
        label.setVisible(true);

        label.setBounds(10, 10, label.getPreferredSize().width, label.getPreferredSize().height);
        yesButton.setBounds(label.getX(), label.getY() + label.getHeight() + 10, yesButton.getPreferredSize().width, yesButton.getPreferredSize().height);
        noButton.setBounds(yesButton.getX() + yesButton.getWidth(), yesButton.getY(), noButton.getPreferredSize().width, noButton.getPreferredSize().height);
        cancelButton.setBounds(noButton.getX() + noButton.getWidth(), noButton.getY(), cancelButton.getPreferredSize().width, cancelButton.getPreferredSize().height);
        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jDialog.setVisible(false);
                gui.getConvertedImagePanel().flushChanged();
                saveImage();
                toRun.run();
            }
        });
        noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jDialog.setVisible(false);
                toRun.run();
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

    private void select(Point squareStart)
    {
        Field field = gui.getFieldOfMainImage();
        if (field == null)
        {
            return;
        }

        int width = gui.getImageZoneWidth();
        int height = gui.getImageZoneHeight();
        int[][] bitmap = field.getBitMap();
        int[][] bitmapOfConvertedImage = new int[width][height];
        width = width < gui.getFieldOfMainImage().getWidth() ? width :  gui.getFieldOfMainImage().getWidth();
        height = height < gui.getFieldOfMainImage().getHeight() ? height : gui.getFieldOfMainImage().getHeight();
        Point startCoordinateOnImage = new Point(squareStart.x,squareStart.y);

        ImagePanel mainImagePanel = gui.getMainImagePanel();
        final int[] cof = mainImagePanel.getScaledCof();
        startCoordinateOnImage.x = startCoordinateOnImage.x * cof[1]/cof[0];
        startCoordinateOnImage.y = startCoordinateOnImage.y * cof[1]/cof[0];

        if (startCoordinateOnImage.x + width >= gui.getFieldOfMainImage().getWidth())
        {
            startCoordinateOnImage.x = gui.getFieldOfMainImage().getWidth() - width;
        }
        if (startCoordinateOnImage.y + height >= gui.getFieldOfMainImage().getHeight())
        {
            startCoordinateOnImage.y = gui.getFieldOfMainImage().getHeight() - height;
        }
        if (startCoordinateOnImage.x < 0)
        {
            startCoordinateOnImage.x = 0;
        }
        if (startCoordinateOnImage.y < 0)
        {
            startCoordinateOnImage.y = 0;
        }
        {
            Point start = new Point(startCoordinateOnImage.x *cof[0]/cof[1],startCoordinateOnImage.y * cof[0]/cof[1]);
            Point finish =  new Point((startCoordinateOnImage.x+width) *cof[0]/cof[1]+1,(height+startCoordinateOnImage.y) * cof[0]/cof[1]+1);
            ImagePanel mainPanel = gui.getMainImagePanel();
            mainPanel.addSquare(start,finish);
        }
        for (int i = startCoordinateOnImage.x; i < startCoordinateOnImage.x+width; i++)
        {
            System.arraycopy(bitmap[i],startCoordinateOnImage.y,bitmapOfConvertedImage[i-startCoordinateOnImage.x],0,height);
        }
        if (hugePixelMode)
        {
            hugePixel(bitmapOfConvertedImage,width,height,hugePixelSize);
        }
        gui.setFieldOfPartImage(new Field(bitmapOfConvertedImage,width,height));
    }

    private void saveImage()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(DATA_PATH));
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            Field convertedField = gui.getFieldOfConvertedImage();
            try
            {
                FileOutputStream f = new FileOutputStream(file);
                convertedField.writeToFile(f);
                f.close();
            }
            catch(IOException e)
            {
                System.err.println("IO error");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


    private void openFile()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(DATA_PATH));
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                Field field = new Field(new FileInputStream(file));
                gui.setFieldOfMainImage(field);
            } catch (NotBMPException nb) {
                JOptionPane.showConfirmDialog(null, ("File is not in BMP format!"));
            } catch (IOException ex) {
                System.err.println("IO error");
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }


    private void handleSelect(MouseEvent e)
    {
        if (!selectMode)
        {
            return;
        }
        Point p = e.getPoint();
        int[] cof = gui.getMainImagePanel().getScaledCof();
        int width = gui.getImageZoneWidth()*cof[0]/cof[1];
        int height = gui.getImageZoneHeight()*cof[0]/cof[1];
        select(new Point(p.x-width/2,p.y-height/2));
    }

    public void mousePressed(MouseEvent e) {
        handleSelect(e);
    }

    public void mouseReleased(MouseEvent e) {
        handleSelect(e);
    }

    public void mouseDragged(MouseEvent e) {
        handleSelect(e);
    }

    private final static String DATA_PATH = ".\\Resources\\FIT_13201_Boyarintsev_Filter_Data";

    private GUI gui = null;
}
