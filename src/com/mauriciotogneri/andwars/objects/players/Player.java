package com.mauriciotogneri.andwars.objects.players;

import com.mauriciotogneri.andwars.objects.Cell;
import com.mauriciotogneri.andwars.objects.Game;
import com.mauriciotogneri.andwars.objects.Game.OnCellSelected;
import com.mauriciotogneri.andwars.states.Initialization;
import com.mauriciotogneri.andwars.states.TurnManager;
import com.mauriciotogneri.andwars.util.ColorUtils;

public abstract class Player implements OnCellSelected
{
	public final int id;
	public final String name;
	public final int color;
	public final int borderColor;
	
	private final boolean local;
	
	public Player(int id, String name, int color, boolean local)
	{
		this.id = id;
		this.name = name;
		this.color = color;
		this.borderColor = ColorUtils.getDarkColor(color);
		
		this.local = local;
	}

	public void initialize(Game game)
	{
		game.addOnCellSelectedListener(this);
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
	
	public abstract void passTurn();

	public abstract void selectInitialCell(Initialization initialization);

	public abstract void makeMove(TurnManager turnManager);
}