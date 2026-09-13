/*
Name: Hunter Daily.
Date: February 22, 2024.
Description: Program responsible for Communicating between all the other methods. responsible for moving the tiles up and down.
*/

import java.util.ArrayList;

public class Model
{
	private ArrayList<Tile> tiles;
	private MsPacman msPacman;

	public Model()
	{
		tiles = new ArrayList<Tile>();
		this.loadFromJson();
	}

	public void update() 
	{
		msPacman.update();
	} 

	public void setMsPacman(MsPacman mp)
	{
		msPacman = mp;
	}


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

	public ArrayList<Tile> getTilesArrayList()
	{
		return tiles;
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

	public void scrollTilesUp(int speed) 
	{
		for (Tile t : tiles)
			t.setY(t.getY() - speed); // world moves down
	}

	public void scrollTilesDown(int speed) 
	{
		for (Tile t : tiles)
			t.setY(t.getY() + speed); // world moves up
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

	public void loadFromJson() //Load method to load map.json from memory
	{
		Json root = Json.load("map.json");       // load JSON file
		Json list = root.get("tiles");           // get tile array

		for (int i = 0; i < list.size(); i++) {
			tiles.add(new Tile(list.get(i)));    // unmarshal each tile
		}
	}



	

}