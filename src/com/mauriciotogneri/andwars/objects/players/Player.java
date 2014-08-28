package com.mauriciotogneri.andwars.objects.players;

import android.graphics.Color;
import com.mauriciotogneri.andwars.objects.Cell;
import com.mauriciotogneri.andwars.objects.Game;
import com.mauriciotogneri.andwars.objects.Game.OnCellSelected;
import com.mauriciotogneri.andwars.states.Initialization;
import com.mauriciotogneri.andwars.states.TurnManager;
import com.mauriciotogneri.andwars.util.ColorUtils;

public abstract class Player implements OnCellSelected
{
	public final int color;
	public final int borderColor;
	
	private final boolean local;

	public static final int PLAYER_COLOR_BLUE = Color.argb(255, 50, 50, 255);
	public static final int PLAYER_COLOR_RED = Color.argb(255, 255, 0, 0);
	
	public Player(int color, boolean local)
	{
		this.color = color;
		this.borderColor = ColorUtils.getDarkColor(color);
		
		this.local = local;
	}

	public void initialize(Game game)
	{
		game.addOnCellSelectedListener(this);
	}

	public boolean isBlue()
	{
		return (this.color == Player.PLAYER_COLOR_BLUE);
	}

	@Override
	public void onCellSelected(Cell cell)
	{
	}

	public boolean isLocal()
	{
		return this.local;
	}

	public abstract boolean isHuman();
	
	public abstract void restart();

	public abstract void passTurn();

	public abstract void selectInitialCell(Initialization initialization);

	public abstract void makeMove(TurnManager turnManager);
}