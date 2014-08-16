package com.mauriciotogneri.andwars.objects.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.mauriciotogneri.andwars.objects.Cell;
import com.mauriciotogneri.andwars.objects.Game;
import com.mauriciotogneri.andwars.objects.Map;
import com.mauriciotogneri.andwars.objects.Move;
import com.mauriciotogneri.andwars.states.Initialization;
import com.mauriciotogneri.andwars.states.TurnManager;

public class PlayerComputer extends Player
{
	private final Map map;

	public PlayerComputer(int id, String name, int color, boolean local, Map map)
	{
		super(id, name, color, local);

		this.map = map;
	}

	@Override
	public void selectInitialCell(Initialization initialization)
	{
		Cell cell = getInitialCell(this.map);
		cell.setUnits(Game.NUMBER_INITIAL_CELLS, this);

		initialization.initialCellSelected();
	}

	private Cell getInitialCell(Map map)
	{
		List<Cell> cells = getCellsWithMaxAdjacency(map.getCells());

		int index = new Random().nextInt(cells.size());

		return cells.get(index);
	}

	private List<Cell> getCellsWithMaxAdjacency(List<Cell> cells)
	{
		List<Cell> result = new ArrayList<Cell>();
		int maximum = 0;

		for (Cell cell : cells)
		{
			if (cell.isEmpty())
			{
				int adjacencyValue = cell.getAdjacents().size();

				if (adjacencyValue == maximum)
				{
					result.add(cell);
				}
				else if (adjacencyValue > maximum)
				{
					result.clear();
					result.add(cell);
					maximum = adjacencyValue;
				}
			}
		}

		return result;
	}

	@Override
	public void makeMove(TurnManager turnManager)
	{
		List<Cell> cells = this.map.getCellsOf(this);

		Move move = conquerEmptyCell(cells);

		if (move == null)
		{

			move = preventOverPopulation(cells);

			if (move == null)
			{

				move = attackEnemy(cells);

				if (move == null)
				{
					move = randomMove(cells);
				}
			}
		}
		
		turnManager.moveExecuted(move);
	}

	/**
	 * Tries to conquer an empty cell. The candidate cell is the cell with more units in the list (and also
	 * with more than 1 unit) that contains at least one empty adjacent. The target cell is the first empty
	 * adjacent in the list.
	 */
	private Move conquerEmptyCell(List<Cell> cells)
	{
		Move result = null;

		Cell candidate = null;

		// the candidate cell is the cell with more units in
		// the list that contains at least one empty adjacent
		for (Cell cell : cells)
		{
			if ((cell.getNumberOfUnits() > 1) && cell.hasEmptyAdjacents())
			{
				if ((candidate == null) || (cell.getNumberOfUnits() > candidate.getNumberOfUnits()))
				{
					candidate = cell;
					
					if (candidate.getNumberOfUnits() == Cell.MAXIMUM_NUMBER_OF_UNITS)
					{
						break;
					}
				}
			}
		}

		if (candidate != null)
		{
			// the target is the first empty adjacent that we find
			// TODO: choose the target cell that can create a cell group
			for (Cell cell : candidate.getEmptyAdjacents())
			{
				result = new Move(candidate, cell);
				break;
			}
		}

		return result;
	}

	private Move preventOverPopulation(List<Cell> cells)
	{
		Move result = null;

		for (Cell cell : cells)
		{
			if (cell.getNumberOfUnits() == Cell.MAXIMUM_NUMBER_OF_UNITS)
			{
				Cell weakestEnemy = cell.getWeakestEnemyAdjacent();

				if (weakestEnemy != null)
				{
					result = new Move(cell, weakestEnemy);
					break;
				}

				// ---------------------

				Cell weakestOwn = cell.getWeakestOwnAdjacent();

				if ((weakestOwn != null) && (weakestOwn.getNumberOfUnits() <= (Cell.MAXIMUM_NUMBER_OF_UNITS / 2)))
				{
					result = new Move(cell, weakestOwn);
					break;
				}
			}
		}

		return result;
	}

	private Move attackEnemy(List<Cell> cells)
	{
		Move result = null;

		Cell candidate = null;

		// the candidate cell is the one with more units that has enemy adjacents
		for (Cell cell : cells)
		{
			if (cell.hasEnemyAdjacents())
			{
				if ((candidate == null) || (cell.getNumberOfUnits() > candidate.getNumberOfUnits()))
				{
					candidate = cell;
					
					if (candidate.getNumberOfUnits() == Cell.MAXIMUM_NUMBER_OF_UNITS)
					{
						break;
					}
				}
			}
		}

		if (candidate != null)
		{
			Cell weakestEnemy = candidate.getWeakestEnemyAdjacent();

			if (weakestEnemy != null)
			{
				result = new Move(candidate, weakestEnemy);
			}
		}

		return result;
	}

	private Move randomMove(List<Cell> cells)
	{
		Move result = null;
		
		Cell cell = getCellWithHighestUnits(cells);

		if (cell != null)
		{
			int index = new Random().nextInt(cell.getAdjacents().size());
			
			Cell randomTarget = cell.getAdjacents().get(index);
			
			result = new Move(cell, randomTarget);
		}
		
		return result;
	}

	private Cell getCellWithHighestUnits(List<Cell> cells)
	{
		Cell result = null;

		for (Cell cell : cells)
		{
			if ((result == null) || (cell.getNumberOfUnits() > result.getNumberOfUnits()))
			{
				result = cell;

				if (cell.getNumberOfUnits() == Cell.MAXIMUM_NUMBER_OF_UNITS)
				{
					break;
				}
			}
		}

		return result;
	}

	@Override
	public boolean isHuman()
	{
		return false;
	}

	@Override
	public void passTurn()
	{
	}
	
	// private static class CellEvaluation
	// {
	// private final Move move;
	//
	// public CellEvaluation(Move move)
	// {
	// this.move = move;
	// }
	//
	// public int evaluate()
	// {
	// return 0;
	// }
	// }
}