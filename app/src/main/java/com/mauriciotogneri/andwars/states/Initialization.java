package com.mauriciotogneri.andwars.states;

import com.mauriciotogneri.andwars.objects.Game;
import com.mauriciotogneri.andwars.objects.players.Player;

import java.util.List;

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
        initializeNextPlayer();
    }

    private void initializeNextPlayer()
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
        initializeNextPlayer();
    }

    public boolean isTurnOf(Player player)
    {
        return ((this.playerIndex < this.players.size()) && (this.players.get(this.playerIndex) == player));
    }
}