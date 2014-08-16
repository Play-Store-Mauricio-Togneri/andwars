package com.mauriciotogneri.andwars.objects;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import android.graphics.PointF;
import com.mauriciotogneri.andwars.objects.players.Player;
import com.mauriciotogneri.andwars.util.ColorUtils;

public class Cell
{
	public final int id;
	public final int x;
	public final int y;
	private int units = 0;
	private Player owner = null;
	private boolean selected = false;
	private int highlight = 0;
	private final List<Cell> adjacents = new ArrayList<Cell>();
	
	public static final int MAXIMUM_NUMBER_OF_UNITS = 9;

	public static final int COLOR_EMPTY_CELL_FILL = Color.argb(255, 190, 190, 190);
	public static final int COLOR_EMPTY_CELL_BACKGROUND = ColorUtils.getDarkColor(Cell.COLOR_EMPTY_CELL_FILL);
	
	public Cell(int id, int x, int y)
	{
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	public void restart()
	{
		this.units = 0;
		this.owner = null;
		this.selected = false;
		this.highlight = 0;
	}

	public PointF getCenter(int blockWidth, int blockHeight)
	{
		float resultX = (this.x * blockWidth) + (blockWidth / 2);
		float resultY = (this.y * blockHeight) + (blockHeight / 2);
		
		return new PointF(resultX, resultY);
	}
	
	public void setHighlight(int value)
	{
		this.highlight = value;
	}

	public void addHighlight(int value)
	{
		this.highlight += value;
	}
	
	public int getHighlight()
	{
		return this.highlight;
	}
	
	public void addAdjatenct(Cell cell)
	{
		this.adjacents.add(cell);
	}
	
	public List<Cell> getAdjacents()
	{
		return this.adjacents;
	}
	
	public List<Cell> getEmptyAdjacents()
	{
		List<Cell> result = new ArrayList<Cell>();
		
		for (Cell cell : this.adjacents)
		{
			if (cell.isEmpty())
			{
				result.add(cell);
			}
		}
		
		return result;
	}
	
	public List<Cell> getOwnAdjacents()
	{
		List<Cell> result = new ArrayList<Cell>();
		
		for (Cell cell : this.adjacents)
		{
			if (cell.isOwner(this.owner))
			{
				result.add(cell);
			}
		}
		
		return result;
	}

	public List<Cell> getEnemyAdjacents()
	{
		List<Cell> result = new ArrayList<Cell>();
		
		for (Cell cell : this.adjacents)
		{
			if ((!cell.isEmpty()) && (!cell.isOwner(this.owner)))
			{
				result.add(cell);
			}
		}
		
		return result;
	}

	public Cell getWeakestEnemyAdjacent()
	{
		List<Cell> enemies = getEnemyAdjacents();
		
		return getWeakestFrom(enemies);
	}
	
	public Cell getWeakestOwnAdjacent()
	{
		List<Cell> own = getOwnAdjacents();
		
		return getWeakestFrom(own);
	}

	private Cell getWeakestFrom(List<Cell> list)
	{
		Cell result = null;
		
		for (Cell cell : list)
		{
			if ((result == null) || (cell.getNumberOfUnits() < result.getNumberOfUnits()))
			{
				result = cell;
			}
		}
		
		return result;
	}

	public boolean isAdjacent(Cell cell)
	{
		boolean result = false;

		for (Cell adjacent : this.adjacents)
		{
			if (adjacent == cell)
			{
				result = true;
				break;
			}
		}

		return result;
	}
	
	public boolean hasEmptyAdjacents()
	{
		return getEmptyAdjacents().size() > 0;
	}

	public boolean hasEnemyAdjacents()
	{
		return getEnemyAdjacents().size() > 0;
	}

	public boolean hasOwnAdjacents()
	{
		return getOwnAdjacents().size() > 0;
	}

	public boolean isSelected()
	{
		return this.selected;
	}

	public void setSelected(boolean value)
	{
		this.selected = value;
	}
	
	public void removeUnits(int numberOfUnits)
	{
		this.units -= numberOfUnits;
		
		if (this.units <= 0)
		{
			this.units = 0;
			this.owner = null;
		}
	}

	public void removeUnitsKeepingOwner(int numberOfUnits)
	{
		this.units -= numberOfUnits;
		
		if (this.units <= 0)
		{
			this.units = 0;
		}
	}

	private void addUnits(int numberOfUnits)
	{
		this.units += numberOfUnits;

		if (this.units > Cell.MAXIMUM_NUMBER_OF_UNITS)
		{
			this.units = Cell.MAXIMUM_NUMBER_OF_UNITS;
		}
	}
	
	public void moveUnitsTo(Cell target, int numberOfUnits)
	{
		if (isAdjacent(target))
		{
			if (target.isEmpty())
			{
				target.setUnits(numberOfUnits, this.owner);
			}
			else if (target.isOwner(this.owner))
			{
				target.addUnits(numberOfUnits);
			}
			else
			{
				target.fight(numberOfUnits, this.owner);
			}
		}
		
		if (this.units <= 0)
		{
			this.owner = null;
		}
	}

	public int getHalfUnits()
	{
		int result = 1;

		if (this.units > 1)
		{
			result = this.units / 2;
			
			if ((this.units % 2) != 0)
			{
				result++;
			}
		}
		
		return result;
	}

	private void fight(int numberOfUnits, Player player)
	{
		if (this.units > numberOfUnits)
		{
			removeUnits(numberOfUnits);
		}
		else if (this.units < numberOfUnits)
		{
			this.units = numberOfUnits - this.units;
			this.owner = player;
		}
		else
		{
			this.units = 0;
			this.owner = null;
		}
	}
	
	public void setUnits(int units, Player player)
	{
		this.units = units;
		this.owner = player;
	}

	public int getNumberOfUnits()
	{
		return this.units;
	}
	
	public boolean isEmpty()
	{
		return (this.units == 0);
	}

	public boolean isOwner(Player player)
	{
		return this.owner == player;
	}
	
	public boolean sameOwner(Cell cell)
	{
		return this.owner == cell.getOwner();
	}
	
	public Player getOwner()
	{
		return this.owner;
	}
	
	public int getMainColor()
	{
		return this.owner.color;
	}
	
	public int getBorderColor()
	{
		return isSelected() ? Color.WHITE : this.owner.borderColor;
	}
	
	public boolean update()
	{
		boolean result = false;
		
		if ((this.owner != null) && (this.units < Cell.MAXIMUM_NUMBER_OF_UNITS) && (getNumberOfOwnAdjacents() > 1))
		{
			this.units++;
			result = true;
		}
		
		return result;
	}
	
	private int getNumberOfOwnAdjacents()
	{
		int result = 0;
		
		for (Cell cell : this.adjacents)
		{
			if (cell.isOwner(this.owner))
			{
				result++;
			}
		}
		
		return result;
	}
}