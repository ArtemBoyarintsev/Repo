package ru.nsu.rayTracer.g13201.boyarintsev.models;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Double3DPoint;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Vector;
import ru.nsu.rayTracer.g13201.boyarintsev.MathUtils.Vector3;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Артем on 14.05.2016.
 */
public class RenderSettings {
    private static double[] upArray = {0,1,0};
    public enum Quality
    {
        rough,
        normal,
        fine
    }

    public Double3DPoint cameraPosition = new Double3DPoint(25,0,0);
    public Double3DPoint viewPosition = new Double3DPoint(0,0,0);
    public Color backGroundColor = Color.BLACK;
    public Quality quality = Quality.normal;
    public Vector3 up = new Vector3(upArray);

    public double zn = 1.0,zf = 30.0;
    public double sw,sh;
    public int gamma = 1;
    public int depth = 1;

    public RenderSettings()
    {

    }

    public RenderSettings(RenderSettings renderSettings)
    {
        this.cameraPosition = new Double3DPoint(renderSettings.cameraPosition);
        this.backGroundColor = new Color(renderSettings.backGroundColor.getRGB());
        this.up = new Vector3(renderSettings.up);
        this.quality = renderSettings.quality;
        this.zn = renderSettings.zn;
        this.zf = renderSettings.zf;
        this.sw = renderSettings.sw;
        this.sh = renderSettings.sh;
        this.gamma = renderSettings.gamma;
        this.depth = renderSettings.depth;
    }
    //        private RenderSettings readRenderSettings(BufferedReader reader) throws IOException
//        {
//            RenderSettings renderSettings = new RenderSettings();
//            renderSettings.backGroundColor = getColor(reader);
//            renderSettings.gamma = readInteger(reader);
//            renderSettings.depth = readInteger(reader);
//            renderSettings.quality = RenderSettings.Quality.valueOf(reader.readLine());
//            renderSettings.cameraPosition = getDouble3DPoint(reader);
//            renderSettings.viewPosition = getDouble3DPoint(reader);
//            renderSettings.up = readVector(reader);
//            renderSettings.zn = readDouble(reader);
//            renderSettings.zf = readDouble(reader);
//            renderSettings.sw = readDouble(reader);
//            renderSettings.sh = readDouble(reader);
//            return renderSettings;
//        }

    public void save(BufferedWriter writer)
    {
        try {
            writeBackGroundColor(writer);
            writeInteger(writer,gamma);
            writeInteger(writer,depth);
            writeQuality(writer);
            writeDouble3DPoint(writer,cameraPosition);
            writeDouble3DPoint(writer,viewPosition);
            writeVector(writer,up);
            writerDouble(writer,zn);
            writerDouble(writer,zf);
            writerDouble(writer,sw);
            writerDouble(writer,sh);
        }
        catch( IOException ex)
        {
            System.err.println("IO error");
            ex.printStackTrace();
            System.exit(1);
        }
    }
    private void writerDouble(BufferedWriter writer,double value) throws IOException
    {
        writer.write(""+value);
        writer.newLine();
    }

    private void writeVector(BufferedWriter writer,Vector3 vector) throws IOException
    {
        int x = 0 , y = 1, z = 2;
        writer.write(vector.get(x)+" "+vector.get(y)+" " +vector.get(z));
        writer.newLine();
    }
    private void writeBackGroundColor(BufferedWriter writer) throws IOException
    {
        writer.write(backGroundColor.getRed() + " " + backGroundColor.getGreen() + " " +backGroundColor.getBlue());
        writer.newLine();
    }
    private void writeQuality(BufferedWriter writer) throws IOException
    {
        writer.write(quality.toString());
        writer.newLine();
    }
    private void writeDouble3DPoint(BufferedWriter writer,Double3DPoint p) throws IOException
    {
        writer.write(p.getX() + " "+p.getY() + " " +p.getZ());
        writer.newLine();
    }
    private void writeInteger(BufferedWriter writer,int value) throws IOException
    {
        writer.write(""+value);
        writer.newLine();
    }
}
