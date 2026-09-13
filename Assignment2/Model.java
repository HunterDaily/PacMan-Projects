/*
Name: Hunter Daily.
Date: February 9, 2024.
Description: Program responsible for adding tiles to an array, checking, adding, and removing tiles. (Also clearing and saving json)
*/

import java.util.ArrayList;

public class Model
{
	private ArrayList<Tile> tiles;

	public Model()
	{
		tiles = new ArrayList<Tile>();
	}

	public void update() {} //I didn't have use for this but I just kept it around just in case

	public Tile getTiles(int i)
	{
		return tiles.get(i);
	}

	public boolean isTile(int x, int y)
	{
		for (Tile t : tiles) 
		{
			if (t.getX() == x && t.getY() == y) 
			{
				return true; // Tile already exists at this position, do not add
			}
		}
		return false;
	}

	public void addTile(int x, int y) //Add a tile to the array
	{
		if (isTile(x, y) == false)
		{
			Tile tile = new Tile(x, y); //Should already be snapped coordinates
			tiles.add(tile);
		}
	}

	public int getLengthOfTiles()
	{
		int length = tiles.size();
		return length;
	}

	public void removeTile(int x, int y) 
	{
		for (Tile tile : tiles)
		{
			if (tile.getX() == x && tile.getY() == y)
			{
				tiles.remove(tile); //X and Y should already be snapped coords
				return; //Originally I was somehow illegally going over the array index, so i had to include this return statementf
			}
		}
	}

	public void getRidOfAllTiles() // Get rid of all Tiles
	{
		tiles.clear();
	}

	public Json marshal()  //Masrhal Object for saving the tile objects to json
	{
		Json root = Json.newObject();
		Json list = Json.newList();
		root.add("tiles", list);
		for (Tile t : tiles) 
		{
			list.add(t.marshal());
		}

		return root;
	}

	public void loadFromJson()
	{
		Json root = Json.load("map.json");
		Json list = root.get("tiles");

		// Find the minimum Y value in the saved map
		int minY = Integer.MAX_VALUE;
		for (int i = 0; i < list.size(); i++) {
			int y = (int) list.get(i).getLong("y");
			if (y < minY) minY = y;
		}

		// Shift all tiles so the top of the map starts at y=0
		for (int i = 0; i < list.size(); i++) {
			Json t = list.get(i);
			int x = (int) t.getLong("x");
			int y = (int) t.getLong("y") - minY; // normalize
			tiles.add(new Tile(x, y));
		}
	}

	

}