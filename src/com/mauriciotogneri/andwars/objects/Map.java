package com.mauriciotogneri.andwars.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;
import com.mauriciotogneri.andwars.objects.players.Player;
import com.mauriciotogneri.andwars.ui.animation.HighlightAnimation;

public class Map
{
	private final String name;
	private final String path;
	public final int width;
	public final int height;
	private final List<Cell> cells = new ArrayList<Cell>();
	
	public Map(String path, Context context)
	{
		this.path = path;

		JSONObject json = getJson(path, context);

		String mapName = "";
		int mapWidth = 1;
		int mapHeight = 1;
		
		try
		{
			mapName = json.getString("name");
			mapWidth = json.getInt("width");
			mapHeight = json.getInt("height");

			SparseArray<Cell> mapCell = new SparseArray<Cell>();

			JSONArray cells = json.getJSONArray("cells");

			for (int i = 0; i < cells.length(); i++)
			{
				JSONObject jsonCell = cells.getJSONObject(i);

				int id = jsonCell.getInt("id");
				int x = jsonCell.getInt("x");
				int y = jsonCell.getInt("y");

				Cell cell = new Cell(id, x, y);
				this.cells.add(cell);
				mapCell.put(id, cell);
			}

			for (int i = 0; i < cells.length(); i++)
			{
				JSONObject jsonCell = cells.getJSONObject(i);

				int id = jsonCell.getInt("id");
				JSONArray adjacents = jsonCell.getJSONArray("adjacents");

				Cell cell = mapCell.get(id);

				if (cell != null)
				{
					for (int j = 0; j < adjacents.length(); j++)
					{
						int adjacentId = adjacents.getInt(j);

						Cell adjacent = mapCell.get(adjacentId);

						if ((adjacent != null) && (adjacent != cell))
						{
							cell.addAdjatenct(adjacent);
						}
					}
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		this.name = mapName;
		this.width = mapWidth;
		this.height = mapHeight;
	}
	
	private JSONObject getJson(String path, Context context)
	{
		JSONObject result = new JSONObject();
		
		BufferedReader reader = null;
		
		try
		{
			StringBuilder builder = new StringBuilder();
			InputStream inputStream = context.getAssets().open(path);
			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;

			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}

			result = new JSONObject(builder.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	public void restart()
	{
		for (Cell cell : this.cells)
		{
			cell.restart();
		}
	}
	
	public List<Cell> getCells()
	{
		return this.cells;
	}

	public List<Cell> getCellsOf(Player player)
	{
		List<Cell> result = new ArrayList<Cell>();

		for (Cell cell : this.cells)
		{
			if (cell.isOwner(player))
			{
				result.add(cell);
			}
		}

		return result;
	}
	
	public void updateUnits(final Game game)
	{
		List<Cell> cellsToHighLight = new ArrayList<Cell>();
		
		for (Cell cell : this.cells)
		{
			if (cell.update())
			{
				cellsToHighLight.add(cell);
			}
		}
		
		HighlightAnimation highlightAnimation = new HighlightAnimation(game, cellsToHighLight);
		highlightAnimation.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	public String getPath()
	{
		return this.path;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
}