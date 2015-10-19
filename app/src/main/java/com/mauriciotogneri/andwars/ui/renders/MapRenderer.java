package com.mauriciotogneri.andwars.ui.renders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mauriciotogneri.andwars.objects.Cell;
import com.mauriciotogneri.andwars.objects.Map;
import com.mauriciotogneri.andwars.objects.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapRenderer extends SurfaceView implements SurfaceHolder.Callback
{
    @NonNull
    private final Paint clearPaint;

    @NonNull
    private final Paint backgroundPaint;

    @NonNull
    private final Paint adjacencyPaint;

    @NonNull
    private final List<Point> background = new ArrayList<>();

    protected static int BLOCK_WIDTH = 0;
    protected static int BLOCK_HEIGHT = 0;
    private static int FONT_SIZE = 0;

    private static final int NUMBER_OF_STARS = 150;

    private final MapListener mapListener;

    public MapRenderer(@NonNull Context context, MapListener mapListener)
    {
        super(context);

        this.mapListener = mapListener;

        this.clearPaint = new Paint();
        this.clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));

        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(Color.WHITE);

        this.adjacencyPaint = new Paint();
        this.adjacencyPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.adjacencyPaint.setColor(Color.GRAY);
        this.adjacencyPaint.setStrokeWidth(5);

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (this.background.isEmpty())
        {
            setBackgroundStars(getWidth(), getHeight());
        }

        if (this.mapListener != null)
        {
            this.mapListener.onCreateMap();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    private void setBackgroundStars(int width, int height)
    {
        Random random = new Random();

        for (int i = 0; i < MapRenderer.NUMBER_OF_STARS; i++)
        {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            this.background.add(new Point(x, y));
        }
    }

    public void update(Map map, Move move)
    {
        MapRenderer.BLOCK_WIDTH = getWidth() / map.width;
        MapRenderer.BLOCK_HEIGHT = getHeight() / map.height;
        MapRenderer.FONT_SIZE = (MapRenderer.BLOCK_WIDTH + MapRenderer.BLOCK_HEIGHT) / 6;

        SurfaceHolder surfaceHolder = getHolder();
        Canvas canvas = null;

        try
        {
            canvas = surfaceHolder.lockCanvas();

            if (canvas != null)
            {
                synchronized (surfaceHolder)
                {
                    canvas.drawPaint(this.clearPaint);

                    drawBackground(this.background, this.backgroundPaint, canvas);

                    List<Cell> cells = map.getCells();

                    drawAdjacencies(cells, this.adjacencyPaint, canvas);

                    if (move != null)
                    {
                        PointF point = move.getProgressPosition(MapRenderer.BLOCK_WIDTH, MapRenderer.BLOCK_HEIGHT);
                        float pointSize = MapRenderer.BLOCK_WIDTH * 0.15f;

                        drawCircle(point.x, point.y, move.source.getMainColor(), pointSize, canvas);
                        drawText(point.x, point.y, String.valueOf(move.numberOfUnits), (int) (pointSize), Color.WHITE, canvas);
                    }

                    drawCells(cells, canvas);
                }
            }
        }
        finally
        {
            if (canvas != null)
            {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void update(Map map)
    {
        update(map, null);
    }

    private void drawBackground(List<Point> background, Paint backgroundPaint, Canvas canvas)
    {
        for (Point point : background)
        {
            canvas.drawPoint(point.x, point.y, backgroundPaint);
        }
    }

    private void drawAdjacencies(List<Cell> cells, Paint adjacencyPaint, Canvas canvas)
    {
        for (Cell cell : cells)
        {
            drawAdjacency(cell, adjacencyPaint, canvas);
        }
    }

    private void drawAdjacency(Cell cell, Paint adjacencyPaint, Canvas canvas)
    {
        PointF cellCenter = cell.getCenter(MapRenderer.BLOCK_WIDTH, MapRenderer.BLOCK_HEIGHT);

        for (Cell adjacent : cell.getAdjacents())
        {
            PointF adjacentCenter = adjacent.getCenter(MapRenderer.BLOCK_WIDTH, MapRenderer.BLOCK_HEIGHT);

            canvas.drawLine(cellCenter.x, cellCenter.y, adjacentCenter.x, adjacentCenter.y, adjacencyPaint);
        }
    }

    private void drawCells(List<Cell> cells, Canvas canvas)
    {
        float cellSize = MapRenderer.BLOCK_WIDTH * 0.3f;

        for (Cell cell : cells)
        {
            drawCell(cell, cellSize, canvas);
        }
    }

    private void drawCell(Cell cell, float size, Canvas canvas)
    {
        PointF cellCenter = cell.getCenter(MapRenderer.BLOCK_WIDTH, MapRenderer.BLOCK_HEIGHT);

        if (cell.isEmpty())
        {
            drawCircle(cellCenter.x, cellCenter.y, Cell.COLOR_EMPTY_CELL_FILL, size, canvas);
            drawCircleBorder(cellCenter.x, cellCenter.y, Cell.COLOR_EMPTY_CELL_BACKGROUND, size, canvas);
        }
        else
        {
            drawCircle(cellCenter.x, cellCenter.y, cell.getMainColor(), size, canvas);
            drawCircleBorder(cellCenter.x, cellCenter.y, cell.getBorderColor(), size, canvas);
            drawText(cellCenter.x, cellCenter.y, String.valueOf(cell.getNumberOfUnits()), MapRenderer.FONT_SIZE, Color.WHITE, canvas);
        }

        int highlight = cell.getHighlight();

        if (highlight > 0)
        {
            float currentHighlight = ((100f - highlight) / 100f);
            int color = Color.argb((int) (255 * currentHighlight), 255, 255, 255);

            drawCircleBorder(cellCenter.x, cellCenter.y, color, size + (size * (highlight / 100f) * 0.8f), canvas);
        }
    }

    private void drawCircle(float x, float y, int color, float size, Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);

        canvas.drawCircle(x, y, size, paint);
    }

    private void drawCircleBorder(float x, float y, int color, float size, Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(size / 8);
        paint.setColor(color);

        canvas.drawCircle(x, y, size, paint);
    }

    private void drawText(float x, float y, String text, int size, int color, Canvas canvas)
    {
        Paint font = new Paint();
        font.setColor(color);
        font.setTextAlign(Align.CENTER);
        font.setTextSize(size);
        font.setFlags(Paint.ANTI_ALIAS_FLAG);
        font.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));

        canvas.drawText(text, x, y + (size / 3), font);
    }

    public interface MapListener
    {
        void onCreateMap();
    }
}