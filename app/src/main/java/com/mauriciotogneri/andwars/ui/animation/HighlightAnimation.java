package com.mauriciotogneri.andwars.ui.animation;

import android.os.AsyncTask;

import com.mauriciotogneri.andwars.objects.Cell;
import com.mauriciotogneri.andwars.objects.Game;

import java.util.List;

public class HighlightAnimation extends AsyncTask<Void, Void, Void>
{
    private final Game game;
    private final List<Cell> cells;

    public HighlightAnimation(Game game, List<Cell> cells)
    {
        this.game = game;
        this.cells = cells;

        clearHighlight();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        for (int i = 0; i < 20; i++)
        {
            addHighlight(5);
            publishProgress();

            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values)
    {
        super.onProgressUpdate(values);

        this.game.updateMap();
    }

    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);

        clearHighlight();
        this.game.updateMap();
    }

    private void addHighlight(int value)
    {
        for (Cell cell : this.cells)
        {
            cell.addHighlight(value);
        }
    }

    private void clearHighlight()
    {
        for (Cell cell : this.cells)
        {
            cell.setHighlight(0);
        }
    }
}