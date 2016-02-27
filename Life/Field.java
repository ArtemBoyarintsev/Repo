package ru.nsu.graphic;

import java.util.Observable;

/**
 * Created by Артем on 09.02.2016.
 */

public class Field extends Observable {
    static class Cage
    {
        boolean alive;
        float impact;
        Cage()
        {
            alive = false;
            impact = 0.0f;
        }
        public float getImpact()
        {
            return impact;
        }
    }
    float liveBegin,birthBegin,birthEnd,liveEnd;
    int width,height;
    private Cage[][] cages;
    float fstImpact,secImpact;

    public Field(int hn,int wm) /*n - количество строк, то есть высота,m - количество элементов в строке, то есть ширина*/
    {
        height = hn;
        width = wm;
        cages = new Cage[height][width];
        for (int i =0 ;i < hn; ++i)
        {
            for (int j = 0; j < wm; ++j)
            {
                cages[i][j] = new Cage();
            }
        }
        liveBegin = 2.0f;
        liveEnd = 3.3f;
        birthBegin = 2.3f;
        birthEnd = 2.9f;
        fstImpact = 2.5f;
        secImpact = 0.3f;
    }

    public void setSizes(int hn,int wm) {
        width = wm;
        height = hn;
        Cage[][] cs = new Cage[hn][wm];
        for (int i = 0; i < hn; ++i)
        {
            for (int j = 0; j < wm; ++j)
            {
                if (i < cages.length && j < cages[i].length )
                {
                    cs[i][j] = cages[i][j];
                    continue;
                }
                cs[i][j]= new Cage();
            }
        }
        cages = cs;
        setChanged();
        notifyObservers(cages);
    }

    public void setLiveBegin(float v)
    {
        liveBegin = v;
    }

    public void setLiveEnd(float v)
    {
        liveEnd = v;
    }

    public void setBirthBegin(float v)
    {
        birthBegin = v;
    }

    public void setBirthEnd(float v)
    {
        birthEnd = v;
    }

    public void setCageAlive(int i, int j) {
        if (null != cages)
        {
            cages[i][j].alive = true;
        }
        updateImpacts();
        setChanged();
        notifyObservers(cages);
    }

    public void xorCageAlive(int i, int j) {
        if (null != cages)
        {
            cages[i][j].alive = !cages[i][j].alive;
        }
        updateImpacts();
        setChanged();
        notifyObservers(cages);
    }

    public void setFstImpact(float value)
    {
        fstImpact = value;
    }

    public void setSecImpact(float value)
    {
        secImpact = value;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public Cage[][] getCages()
    {
        return cages;
    }

    private void updateImpacts() {
        for (int i = 0; i < cages.length; ++i)
        {
            int k = i%2;
            for(int j = 0; j < cages[i].length - k ; ++j)
            {
                int firstLevelCount = gatherAllFirstLevelNeighboors(i,j);
                int secondLevelCount = gatherAllSecondLevelNeighboors(i,j);
                float impact;

                impact = fstImpact * firstLevelCount + secImpact * secondLevelCount;
                cages[i][j].impact = impact;
            }
        }
    }

    public int getAliveCount() {
        int ret = 0 ;
        for (Cage[] cage : cages) {
            for (Cage aCage : cage) {
                if (aCage.alive) {
                    ret++;
                }
            }
        }
        return ret;
    }

    public void nextState() {
        updateImpacts();
        switchAlive();
        updateImpacts();
        setChanged();
        notifyObservers(cages);
    }

    private int gatherAllFirstLevelNeighboors(int i,int j) {
        int ret = 0;

        if (j + 1 < width)
        {
            if (cages[i][j+1].alive)
            {
                ret++;
            }
        }
        if (j - 1 >= 0)
        {
            if (cages[i][j-1].alive)
            {
                ret++;
            }
        }
        if (i % 2 == 1)
        {
            //посмотреть соседей его же и на один больше уровней снизу и сверху.
            if (i - 1 >= 0)
            {
                if (cages[i-1][j].alive)
                {
                    ret++;
                }
                if (j + 1 < width)
                {
                    if (cages[i-1][j+1].alive)
                    {
                        ret++;
                    }
                }
            }
            if (i+1 < height)
            {
                if (cages[i+1][j].alive)
                {
                    ret++;
                }
                if (j + 1 < width)
                {
                    if (cages[i+1][j+1].alive)
                    {
                        ret++;
                    }
                }
            }
        }
        else //0,2,4... ряд
        {
            //посмотреть соседей его же и на один меньше уровней снизу и сверху.
            if (i - 1 >= 0)
            {
                if (cages[i-1][j].alive)
                {
                    ret++;
                }
                if (j - 1 >= 0)
                {
                    if (cages[i-1][j-1].alive)
                    {
                        ret++;
                    }
                }
            }
            if (i+1 < height)
            {
                if (cages[i+1][j].alive)
                {
                    ret++;
                }
                if (j - 1 >= 0)
                {
                    if (cages[i+1][j-1].alive)
                    {
                        ret++;
                    }
                }
            }
        }
        return ret;
    }

    private int gatherAllSecondLevelNeighboors(int i,int j) {
        int ret = 0 ;
        if (i-2 >=0)
        {
            if (cages[i-2][j].alive)
                ret++;
        }
        if (i+2 < height)
        {
            if (cages[i+2][j].alive)
            {
                ret++;
            }
        }
        if (i%2 ==0)
        {
            if (i-1 >=0&&j-2>=0)
            {
                if (cages[i-1][j-2].alive)
                {
                    ret++;
                }
            }
            if (i-1 >= 0 && j+1 <width)
            {
                if (cages[i-1][j+1].alive)
                {
                    ret++;
                }
            }
            if (i+1 < height && j-2>=0 )
            {
                if (cages[i+1][j-2].alive)
                {
                    ret++;
                }
            }
            if (i+1 < height && j+1<width )
            {
                if (cages[i+1][j + 1].alive)
                {
                    ret++;
                }
            }
        }
        else
        {
            if (i-1 >=0&&j-1>=0)
            {
                if (cages[i-1][j-1].alive)
                {
                    ret++;
                }
            }
            if (i-1 >= 0 && j+2 <width)
            {
                if (cages[i-1][j+2].alive)
                {
                    ret++;
                }
            }
            if (i+1 < height && j-1>=0 )
            {
                if (cages[i+1][j-1].alive)
                {
                    ret++;
                }
            }
            if (i+1 < height && j+2<width )
            {
                if (cages[i+1][j + 2].alive)
                {
                    ret++;
                }
            }
        }
        return ret;
    }

    private void switchAlive() {
        for (Cage[] cage : cages) {
            for (Cage aCage : cage) {
                float impact = aCage.impact;
                if (!aCage.alive && (birthBegin <= impact && impact <= birthEnd)) {
                    aCage.alive = true;
                    continue;
                }
                if (aCage.alive && !(liveBegin <= impact && impact <= liveEnd)) {
                    aCage.alive = false;
                }
            }
        }
    }

    public void makeClear() {
        for (Cage[] cage : cages) {
            for (Cage aCage : cage) {
                aCage.alive = false;
            }
        }
        updateImpacts();
        setChanged();
        notifyObservers(cages);
    }
}
