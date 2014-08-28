package com.mauriciotogneri.andwars.ui.renders;

import org.eclipse.jdt.annotation.NonNull;
import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.mauriciotogneri.andwars.activities.GameActivity;
import com.mauriciotogneri.andwars.objects.Game.GameResult;

public class GameRenderer extends MapRenderer
{
	@NonNull
	private final GameActivity gameActivity;

	public GameRenderer(@NonNull Context context)
	{
		super(context, null);
		
		this.gameActivity = (GameActivity)context;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		super.surfaceCreated(holder);
		this.gameActivity.setGameRenderer(this);
	}
	
	public void showEndMessage(GameResult result, int color)
	{
		this.gameActivity.showEndMessage(result, color);
	}

	public void lockButtons()
	{
		this.gameActivity.enableButtons(false);
	}

	public void unlockButtons()
	{
		this.gameActivity.enableButtons(true);
	}
	
	public void updateTurnNumber(int turn)
	{
		this.gameActivity.updateTurnNumber(turn);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_UP:
				int x = (int)(event.getX() / MapRenderer.BLOCK_WIDTH);
				int y = (int)(event.getY() / MapRenderer.BLOCK_HEIGHT);
				this.gameActivity.onClick(x, y);
				break;
		}
		
		return true;
	}
}