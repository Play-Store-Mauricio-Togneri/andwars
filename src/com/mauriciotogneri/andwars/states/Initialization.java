package com.mauriciotogneri.andwars.states;

import java.util.List;
import com.mauriciotogneri.andwars.objects.Game;
import com.mauriciotogneri.andwars.objects.players.Player;

public class Initialization
{
	private final Game game;
	private final List<Player> players;
	
	private int playerIndex = -1;

	public Initialization(Game game, List<Player> players)
	{
		this.game = game;
		this.players = players;
	}

	public void start()
	{
		intializeNextPlayer();
	}
	
	private void intializeNextPlayer()
	{
		this.playerIndex++;

		if (this.playerIndex < this.players.size())
		{
			Player player = this.players.get(this.playerIndex);

			if (player.isHuman() && player.isLocal())
			{
				this.game.unlockScreen(false);
			}
			
			player.selectInitialCell(this);
		}
		else
		{
			this.game.gameInitialized();
		}
	}
	
	public void initialCellSelected()
	{
		this.game.lockScreen();
		this.game.updateMap();
		intializeNextPlayer();
	}

	public boolean isTurnOf(Player player)
	{
		return ((this.playerIndex < this.players.size()) && (this.players.get(this.playerIndex) == player));
	}
}