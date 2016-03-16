package ru.nsu.fit.g13201.boyarintsev;



import java.awt.*;
import java.io.*;
import java.util.Observable;

class NotBMPException extends Exception
{

}
public class Field extends Observable {
    final static private short BMP_TYPE = 19778;

    class FileHeader {
        int  bfType;
        long bfSize;
        long reserve;
        long bfOff;
    }
    class InfoHeader
    {
        long Size;         //число байт, занимаемых структурой InfoHeader
        int Width;        //ширина битового образа в пикселах
        int Height;        //высота битового образа в пикселах
        int  Planes;        //число битовых плоскостей устройства
        int BitCount;    //число битов на пиксел
        long Compression;    //тип сжатия
        long SizeImage;    //размер картинки в байтах
        long XPelsPerMeter;//горизонтальное разрешение устройства, пиксел/м
        long YPelPerMeter; //вертикальное разрешение устройства, пиксел/м
        long ClrUsed;    //число используемых цветов
        long ClrImportant; //число "важных" цветов
    }
    FileHeader fileHeader;
    InfoHeader infoHeader;
    public Field(FileInputStream image) throws NotBMPException
    {
        try {
            fileHeader = new FileHeader();
            infoHeader = new InfoHeader();
            readFileHeader(fileHeader,image);
            readInfoHeader(infoHeader,image);
            this.width = infoHeader.Width;
            this.height = infoHeader.Height;
            bitMap = new int[width][height];
            for(int j = height-1; j >= 0; --j)
            {
                for ( int i = 0; i < width; ++i)
                {
                    int b = image.read();
                    int g = image.read();
                    int r = image.read();
                    r = r < 0 ? r + 128 :r ;
                    g = g < 0 ? g + 128:g;
                    b = b < 0 ? b + 128:b;
                    Color color = new Color(r,g,b);
                    bitMap[i][j] = color.getRGB();
                }
                for (int i = 0; i < width%4;++i)
                {
                    int skip = image.read();
                    if (skip != 0)
                    {
                        throw new NotBMPException();
                    }
                }
            }
            //fileBfSize = fileHeader.bfSize;
        }
        catch(IOException e)
        {
            System.err.println("IO error");
            e.printStackTrace();
            System.exit(1);
        }
    }



    public int [][] getBitMap()
    {
        return bitMap;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public Field(int[][] bitMap,int width,int height)
    {
        this.bitMap = bitMap;
        this.height = height;
        this.width = width;
    }
    public void writeToFile(FileOutputStream file)
    {
        if (file==null)
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
    private void writeBitmap(FileOutputStream file) throws IOException
    {
        for(int j = height-1; j>=0;--j)
        {
            for (int i = 0 ; i < width; ++i)
            {
                int c = bitMap[i][j];
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
    private void writeInfoHeader(FileOutputStream file) throws IOException
    {
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
    private byte[] shortToBytes(short value)
    {
        byte[] bytes = new byte[2];
        bytes[1] = (byte)((value& 0x0000ff00)>>8);
        bytes[0] = (byte)((value& 0x000000ff));
        return bytes;
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
    private void writeFileHeader(FileOutputStream file) throws IOException
    {
        file.write("BM".getBytes());
        file.write(intToBytes(54+width*height*3));
        file.write(intToBytes(0));
        file.write(intToBytes(54));
    }
    private short getNumberShort(byte[] bytes)
    {
        return (short)(((bytes[1] & 0xFF) << 8) + (bytes[0] & 0xFF));
    }

    private int getNumberInt(byte[] bytes)
    {
       return ((bytes[3] & 0xFF) << 24) + ((bytes[2] & 0xFF) << 16) + ((bytes[1] & 0xFF) << 8) + (bytes[0] & 0xFF);
    }

    private short getShort(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[2];
        int wasRead = stream.read(bytes,0,2);
        if (wasRead != 2)
        {
            throw  new IOException();
        }
        return getNumberShort(bytes);
    }

    private int getInt(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[4];
        int wasRead = stream.read(bytes,0,4);
        if (wasRead != 4)
        {
            throw  new IOException();
        }
        return getNumberInt(bytes);
    }

    private void readFileHeader(FileHeader fileHeader,FileInputStream stream) throws IOException, NotBMPException
    {
        fileHeader.bfType = getShort(stream);
        fileHeader.bfSize = getInt(stream);
        fileHeader.reserve = getInt(stream);
        fileHeader.bfOff = getInt(stream);
        if (fileHeader.bfType != BMP_TYPE)
        {
            throw new NotBMPException();
        }
    }

    private void readInfoHeader(InfoHeader infoHeader,FileInputStream stream) throws IOException,NotBMPException
    {
        infoHeader.Size = getInt(stream);
        infoHeader.Width =  getInt(stream);
        infoHeader.Height =  getInt(stream);
        infoHeader.Planes =  getShort(stream);
        infoHeader.BitCount =  getShort(stream);
        infoHeader.Compression =  getInt(stream);
        infoHeader.SizeImage = getInt(stream);
        infoHeader.XPelsPerMeter = getInt(stream);
        infoHeader.YPelPerMeter = getInt(stream);
        infoHeader.ClrUsed =  getInt(stream);
        infoHeader.ClrImportant = getInt(stream);
        if (infoHeader.BitCount != 24)
        {
            throw  new NotBMPException();
        }
    }

    private int width;
    private int height;
    private int[][] bitMap;
}
