package com.mauriciotogneri.andwars.ui.animation;

import android.os.AsyncTask;
import com.mauriciotogneri.andwars.objects.Game;
import com.mauriciotogneri.andwars.objects.Move;

public class MoveAnimation extends AsyncTask<Void, Void, Void>
{
	private final Game game;
	private final Move move;
	private final AnimationCallback animationCallback;
	
	public MoveAnimation(AnimationCallback animationCallback, Game game, Move move)
	{
		this.game = game;
		this.move = move;
		this.animationCallback = animationCallback;
	}

	@Override
	protected Void doInBackground(Void... params)
	{
		for (int i = 0; i < 20; i++)
		{
			this.move.addProgress(5);
			publishProgress();
			
			try
			{
				Thread.sleep(40);
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

		this.game.updateMap(this.move);
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		super.onPostExecute(result);

		this.animationCallback.onAnimationFinish(this.move);
	}
}