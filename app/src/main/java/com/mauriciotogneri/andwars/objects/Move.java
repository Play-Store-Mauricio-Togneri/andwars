package com.mauriciotogneri.andwars.objects;

import android.graphics.PointF;

public class Move
{
    public final Cell source;
    private final Cell target;
    public final int numberOfUnits;
    private int progress = 0;

    public Move(Cell source, Cell target, int numberOfUnits)
    {
        this.source = source;
        this.target = target;
        this.numberOfUnits = numberOfUnits;
    }

    public Move(Cell source, Cell target)
    {
        this.source = source;
        this.target = target;
        this.numberOfUnits = source.getHalfUnits();
    }

    public void addProgress(int value)
    {
        this.progress += value;
    }

    public PointF getProgressPosition(int mapWidth, int mapHeight)
    {
        PointF centerSource = this.source.getCenter(mapWidth, mapHeight);
        PointF centerTarget = this.target.getCenter(mapWidth, mapHeight);

        float currentProgress = this.progress / 100f;

        float diffX = (centerTarget.x - centerSource.x) * currentProgress;
        float diffY = (centerTarget.y - centerSource.y) * currentProgress;

        float x = centerSource.x + diffX;
        float y = centerSource.y + diffY;

        return new PointF(x, y);
    }

    public void executeDeparture()
    {
        this.source.removeUnitsKeepingOwner(this.numberOfUnits);
    }

    public void executeArrival()
    {
        this.source.moveUnitsTo(this.target, this.numberOfUnits);
    }
}