package com.mauriciotogneri.andwars.objects.players;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.mauriciotogneri.andwars.R;
import com.mauriciotogneri.andwars.objects.Cell;
import com.mauriciotogneri.andwars.objects.Game;
import com.mauriciotogneri.andwars.objects.Move;
import com.mauriciotogneri.andwars.states.Initialization;
import com.mauriciotogneri.andwars.states.TurnManager;

public class PlayerHuman extends Player
{
	private Initialization initialization = null;
	private TurnManager turnManager = null;
	private final Context context;

	private Cell selectedSourceCell = null;
	private Cell selectedTargetCell = null;

	private State state = null;
	
	private enum State
	{
		SELECTING_SOURCE_CELL, SELECTING_TARGET_CELL
	}

	public PlayerHuman(int color, boolean local, Context context)
	{
		super(color, local);

		this.context = context;
	}

	@Override
	public void selectInitialCell(Initialization initialization)
	{
		this.initialization = initialization;
	}

	@Override
	public void onCellSelected(Cell cell)
	{
		if ((this.initialization != null) && this.initialization.isTurnOf(this))
		{
			if (cell.isEmpty())
			{
				cell.setUnits(Game.NUMBER_INITIAL_CELLS, this);
				this.initialization.initialCellSelected();
				this.initialization = null;
			}
		}
		else if ((this.turnManager != null) && this.turnManager.isTurnOf(this))
		{
			switch (this.state)
			{
				case SELECTING_SOURCE_CELL:
					selectSourceCell(cell);
					break;
				case SELECTING_TARGET_CELL:
					selectTargetCell(cell);
					break;
			}
		}
	}
	
	@Override
	public boolean isHuman()
	{
		return true;
	}

	@Override
	public void restart()
	{
		this.initialization = null;
		this.turnManager = null;

		this.selectedSourceCell = null;
		this.selectedTargetCell = null;

		this.state = null;
	}
	
	@Override
	public void makeMove(TurnManager turnManager)
	{
		this.turnManager = turnManager;
		clearTurn();
		this.state = State.SELECTING_SOURCE_CELL;
	}

	private void selectSourceCell(Cell cell)
	{
		if (cell.isOwner(this))
		{
			this.selectedSourceCell = cell;
			cell.setSelected(true);
			
			this.state = State.SELECTING_TARGET_CELL;

			this.turnManager.updateMap();
		}
	}

	private void selectTargetCell(Cell cell)
	{
		if (this.selectedSourceCell != null)
		{
			if (this.selectedSourceCell == cell)
			{
				this.selectedSourceCell.setSelected(false);
				this.selectedSourceCell = null;
				
				this.state = State.SELECTING_SOURCE_CELL;
				
				this.turnManager.updateMap();
			}
			else if (this.selectedSourceCell.isAdjacent(cell))
			{
				this.selectedTargetCell = cell;

				int limitNumberOfUnis = this.selectedSourceCell.getNumberOfUnits();

				if (this.selectedTargetCell.sameOwner(this.selectedSourceCell))
				{
					if ((limitNumberOfUnis + this.selectedTargetCell.getNumberOfUnits()) > Cell.MAXIMUM_NUMBER_OF_UNITS)
					{
						limitNumberOfUnis = Cell.MAXIMUM_NUMBER_OF_UNITS - this.selectedTargetCell.getNumberOfUnits();
					}
					
					if (limitNumberOfUnis > 1)
					{
						showDialogMoveUnits(limitNumberOfUnis);
					}
					else
					{
						acceptMove(1);
					}
				}
				else
				{
					showDialogMoveUnits(limitNumberOfUnis);
				}
			}
		}
	}
	
	private void showDialogMoveUnits(int limit)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle(R.string.dialog_units_title);
		builder.setIcon(android.R.drawable.ic_menu_set_as);
		builder.setCancelable(true);

		LayoutInflater inflater = LayoutInflater.from(this.context);
		View layout = inflater.inflate(R.layout.dialog_choose_units, null);
		builder.setView(layout);
		
		builder.setNegativeButton(R.string.button_cancel, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				PlayerHuman.this.selectedTargetCell = null;
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();

		clearDialogButtons(layout, dialog, limit);
	}
	
	private void clearDialogButtons(View layout, final AlertDialog dialog, int limit)
	{
		for (int i = 1; i < 10; i++)
		{
			int resId = this.context.getResources().getIdentifier("units" + i, "id", this.context.getPackageName());
			Button button = (Button)layout.findViewById(resId);
			
			final int value = i;
			
			if (i <= limit)
			{
				button.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						acceptMove(value);
						dialog.dismiss();
					}
				});
			}
			else
			{
				button.setVisibility(View.GONE);
			}
		}
	}

	private void acceptMove(int numberOfUnits)
	{
		Move move = new Move(this.selectedSourceCell, this.selectedTargetCell, numberOfUnits);

		clearTurn();
		
		this.turnManager.moveExecuted(move);
	}
	
	private void clearSelectedCells()
	{
		if (this.selectedSourceCell != null)
		{
			this.selectedSourceCell.setSelected(false);
			this.selectedSourceCell = null;
		}

		if (this.selectedTargetCell != null)
		{
			this.selectedTargetCell.setSelected(false);
			this.selectedTargetCell = null;
		}
		
		this.turnManager.updateMap();
	}

	private void clearTurn()
	{
		clearSelectedCells();
		this.state = null;
	}

	@Override
	public void passTurn()
	{
		clearTurn();
		this.turnManager.skipTurn();
	}
}