package ru.nsu.rayTracer.g13201.boyarintsev.models.scenes;

import ru.nsu.rayTracer.g13201.boyarintsev.models.SceneLights;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;

/**
 * Created by Артем on 09.05.2016.
 */

public abstract class iScene extends Observable {

    public BufferedImage render() {
        return sceneImage;
    }


    public void save(FileOutputStream file)
    {
        if (file==null || sceneImage == null)
        {
            return;
        }
        try {
            writeFileHeader(file);
            writeInfoHeader(file);
            writeBitmap(file);
        }
        catch(IOException er)
        {
            System.err.println("IO Error");
            er.printStackTrace();
            System.exit(1);
        }
    }

    private void writeFileHeader(FileOutputStream file) throws IOException
    {
        int width = sceneImage.getWidth();
        int height = sceneImage.getHeight();
        file.write("BM".getBytes());
        file.write(intToBytes(54+width*height*3));
        file.write(intToBytes(0));
        file.write(intToBytes(54));
    }

    private void writeInfoHeader(FileOutputStream file) throws IOException
    {
        int width = sceneImage.getWidth();
        int height = sceneImage.getHeight();
        file.write(intToBytes(40));
        file.write(intToBytes(width));
        file.write(intToBytes(height));
        file.write(shortToBytes((short)1));
        file.write(shortToBytes((short)24));
        file.write(intToBytes(0)); //Compression
        file.write(intToBytes(width*height*3));
        file.write(intToBytes(0));
        file.write(intToBytes(0));
        file.write(intToBytes(0));
        file.write(intToBytes(0));
    }

    private void writeBitmap(FileOutputStream file) throws IOException
    {
        int width = sceneImage.getWidth();
        int height = sceneImage.getHeight();
        for(int j = height - 1; j >= 0; --j)
        {
            for (int i = 0 ; i < width; ++i)
            {
                int c = sceneImage.getRGB(i,j);
                byte r = (byte)((c & 0x00ff0000)>>16);
                byte g = (byte)((c & 0x0000ff00)>>8);
                byte b = (byte)((c & 0x000000ff));
                file.write(b);
                file.write(g);
                file.write(r);
            }
            if (width%4 != 0)
            {
                byte[] extra = new byte[width%4];
                file.write(extra);
            }
        }
    }

    private byte[] intToBytes(int value)
    {
        byte[] bytes = new byte[4];
        bytes[3] = (byte)((value& 0xff000000)>>24);
        bytes[2] = (byte)((value& 0x00ff0000)>>16);
        bytes[1] = (byte)((value& 0x0000ff00)>>8);
        bytes[0] = (byte)((value& 0x000000ff));
        return bytes;
    }

    private byte[] shortToBytes(short value)
    {
        byte[] bytes = new byte[2];
        bytes[1] = (byte)((value& 0x0000ff00)>>8);
        bytes[0] = (byte)((value& 0x000000ff));
        return bytes;
    }

    protected  BufferedImage sceneImage = null;
}
