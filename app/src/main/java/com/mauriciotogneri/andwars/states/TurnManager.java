package com.mauriciotogneri.andwars.states;

import android.os.AsyncTask;

import com.mauriciotogneri.andwars.objects.Cell;
import com.mauriciotogneri.andwars.objects.Game;
import com.mauriciotogneri.andwars.objects.Move;
import com.mauriciotogneri.andwars.objects.players.Player;
import com.mauriciotogneri.andwars.ui.animation.AnimationCallback;
import com.mauriciotogneri.andwars.ui.animation.MoveAnimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TurnManager implements AnimationCallback
{
    private final Game game;
    private final List<Player> players;

    private int turn = 0;
    private int subTurn = 0;
    private int playerIndex = -1;

    public TurnManager(Game game, List<Player> players)
    {
        this.game = game;
        this.players = new ArrayList<>();
        this.players.addAll(players);
        Collections.reverse(this.players);
    }

    public void start()
    {
        runNextTurn();
    }

    public void updateMap()
    {
        this.game.updateMap();
    }

    private void runNextTurn()
    {
        this.turn++;
        this.playerIndex = -1;

        this.game.updateTurnNumber(this.turn);
        runNextPlayer();
    }

    private void runNextPlayer()
    {
        this.game.lockScreen();
        this.game.updateMap();

        this.playerIndex++;

        if (this.playerIndex < this.players.size())
        {
            this.subTurn = 0;
            runCurrentPlayer();
        }
        else
        {
            this.game.updateUnits();
            runNextTurn();
        }
    }

    private void runCurrentPlayer()
    {
        if (this.subTurn < Game.NUMBER_MOVES_PER_PLAYER)
        {
            Player player = getCurrentPlayer();

            if (player.isHuman() && player.isLocal())
            {
                this.game.unlockScreen(true);
            }

            player.makeMove(this);
        }
        else
        {
            runNextPlayer();
        }
    }

    public void moveExecuted(Move move)
    {
        this.game.lockScreen();

        if (move != null)
        {
            move.executeDeparture();

            MoveAnimation moveAnimation = new MoveAnimation(this, this.game, move);
            moveAnimation.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
        else
        {
            subTurnFinished();
        }
    }

    private void subTurnFinished()
    {
        if (isTie())
        {
            gameTie();
        }
        else
        {
            Player winner = getWinner();

            if (winner != null)
            {
                gameFinished();
            }
            else
            {
                this.subTurn++;
                runCurrentPlayer();
            }
        }
    }

    @Override
    public void onAnimationFinish(Move move)
    {
        move.executeArrival();
        subTurnFinished();
    }

    private boolean isTie()
    {
        boolean result = true;

        List<Cell> cells = this.game.getMap().getCells();

        for (Cell cell : cells)
        {
            if (!cell.isEmpty())
            {
                result = false;
                break;
            }
        }

        return result;
    }

    private Player getWinner()
    {
        Player result = null;

        List<Cell> cells = this.game.getMap().getCells();

        for (Cell cell : cells)
        {
            Player owner = cell.getOwner();

            if (owner != null)
            {
                if (result == null)
                {
                    result = owner;
                }
                else if (!cell.isOwner(result))
                {
                    result = null;
                    break;
                }
            }
        }

        return result;
    }

    private void gameFinished()
    {
        Player winner = getWinner();

        this.game.lockScreen();
        this.game.updateMap();
        this.game.gameFinished(winner);
    }

    private void gameTie()
    {
        this.game.lockScreen();
        this.game.updateMap();
        this.game.gameTie();
    }

    public void skipTurn()
    {
        runNextPlayer();
    }

    public void passTurn()
    {
        Player player = getCurrentPlayer();
        player.passTurn();
    }

    private Player getCurrentPlayer()
    {
        return this.players.get(this.playerIndex);
    }

    public boolean isTurnOf(Player player)
    {
        return ((this.playerIndex < this.players.size()) && (getCurrentPlayer() == player));
    }
}