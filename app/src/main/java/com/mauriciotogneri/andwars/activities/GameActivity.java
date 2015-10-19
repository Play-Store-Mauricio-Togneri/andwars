package com.mauriciotogneri.andwars.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;
import com.mauriciotogneri.andwars.R;
import com.mauriciotogneri.andwars.objects.Game;
import com.mauriciotogneri.andwars.objects.Game.GameMode;
import com.mauriciotogneri.andwars.objects.Game.GameResult;
import com.mauriciotogneri.andwars.objects.Map;
import com.mauriciotogneri.andwars.objects.players.Player;
import com.mauriciotogneri.andwars.objects.players.PlayerComputer;
import com.mauriciotogneri.andwars.objects.players.PlayerHuman;
import com.mauriciotogneri.andwars.ui.renders.GameRenderer;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity
{
    private Game game;

    private MenuItem buttonRestartGame;
    private MenuItem buttonPassTurn;

    public static final String PARAMETER_GAME_MODE = "game_mode";
    public static final String PARAMETER_MAP_NAME = "map_name";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle("  " + getString(R.string.title_bar_initializing));

        GameRenderer gameRenderer = new GameRenderer(this);
        addContentView(gameRenderer, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        GameMode mode = (GameMode) getIntent().getSerializableExtra(GameActivity.PARAMETER_GAME_MODE);
        String mapName = getIntent().getStringExtra(GameActivity.PARAMETER_MAP_NAME);

        this.game = createGame(mode, mapName);
    }

    public void enableButtons(boolean enabled)
    {
        if (this.buttonPassTurn != null)
        {
            this.buttonPassTurn.setEnabled(enabled);
        }

        if (this.buttonRestartGame != null)
        {
            this.buttonRestartGame.setEnabled(enabled);
        }
    }

    private void enableRestart()
    {
        if (this.buttonRestartGame != null)
        {
            this.buttonRestartGame.setEnabled(true);
        }
    }

    public void showEndMessage(GameResult result, int color)
    {
        enableRestart();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.dialog_game_finished, null);

        TextView message = (TextView) layout.findViewById(R.id.message);
        message.setText(result.getTextId());
        message.setTextColor(color);

        builder.setView(layout);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void updateTurnNumber(int turn)
    {
        setTitle("  " + getString(R.string.title_bar_turn) + "  " + turn);
    }

    public void setGameRenderer(GameRenderer gameRenderer)
    {
        this.game.setGameRenderer(gameRenderer);

        if (!this.game.isStarted())
        {
            this.game.start();
        }
    }

    public void onClick(int x, int y)
    {
        this.game.onClick(x, y);
    }

    private void passTurn()
    {
        this.game.passTurn();
    }

    private void confirmCloseGame()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmation_close_title);
        builder.setCancelable(false);
        builder.setIcon(android.R.drawable.ic_menu_info_details);
        builder.setMessage(R.string.confirmation_close_message);

        builder.setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });

        builder.setNegativeButton(R.string.button_cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void confirmRestartGame()
    {
        if ((this.game != null) && (this.game.isFinished()))
        {
            restartGame();
        }
        else
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.confirmation_restart_title);
            builder.setCancelable(false);
            builder.setIcon(android.R.drawable.ic_menu_info_details);
            builder.setMessage(R.string.confirmation_restart_message);

            builder.setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    restartGame();
                }
            });

            builder.setNegativeButton(R.string.button_cancel, null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void restartGame()
    {
        setTitle("  " + getString(R.string.title_bar_initializing));
        this.game.restart();
    }

    @Override
    public void onBackPressed()
    {
        if ((this.game != null) && (this.game.isFinished()))
        {
            this.game.close();
            finish();
        }
        else
        {
            confirmCloseGame();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        this.buttonRestartGame = menu.getItem(1);
        this.buttonPassTurn = menu.getItem(2);

        enableButtons(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.gamemenu_pass_turn:
                passTurn();
                return true;
            case R.id.gamemenu_restart_game:
                confirmRestartGame();
                return true;
            case R.id.gamemenu_help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showHelp()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_help_game);
        builder.setIcon(android.R.drawable.ic_menu_help);
        builder.setCancelable(true);

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.dialog_help_game, null);
        builder.setView(layout);

        builder.setPositiveButton(R.string.button_accept, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Game createGame(GameMode mode, String mapName)
    {
        AndWars application = (AndWars) getApplication();
        Tracker tracker = application.getTracker();

        Map map = new Map(mapName, this);

        List<Player> players = new ArrayList<>();

        switch (mode)
        {
            case VS_COMPUTER:
                players.add(new PlayerHuman(Player.PLAYER_COLOR_BLUE, true, this));
                players.add(new PlayerComputer(Player.PLAYER_COLOR_RED, true, map));
                break;
            case VS_HUMAN:
                players.add(new PlayerHuman(Player.PLAYER_COLOR_BLUE, true, this));
                players.add(new PlayerHuman(Player.PLAYER_COLOR_RED, true, this));
                break;
        }

        return new Game(mode, map, players, tracker);
    }
}