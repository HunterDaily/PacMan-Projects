/*
Name: Hunter Daily.
Date: February, 22, 2024.
Description: Class for keeping track of where the tiles are/movement for when mrs pacman goes up or down
*/


import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics;

public class Tile
{
    private int x;
    private int y;
    private int w;
    private int h;
    private int scrollPos = 0;
    private int scrollSpeed = 5;
    private Model model;
    private static BufferedImage tileImage; //This is static because all tiles will be using the same image, so we only need to load it once
    static {
        try {
            tileImage = ImageIO.read(new File("images/tile2.png")); // example
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Tile(Model m) //Default constructor (Constructor Chaining in play)
    {
        this.model = m;
        this.x = 32;
        this.y = 32;
        this.w = 32;
        this.h = 32;
        
    }

    public Tile(int x, int y) //Default constructor (Constructor Chaining in play)
    {
        this.x = x;
        this.y = y;
        this.w = 32;
        this.h = 32;
        
    }

    public Tile(Json ob) { //This is to rebuild those tile objects when we load map.json
        this.x = (int) ob.getLong("x");
        this.y = (int) ob.getLong("y");
        this.w = (int) ob.getLong("w");
        this.h = (int) ob.getLong("h");
    }

    //Start of Getter methods
    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getW()
    {
        return w;
    }

    public int getH()
    {
        return h;
    }
    //End of Getter Methods

    //Start of Setter methods
    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setW(int w)
    {
        this.w = w;
    }

    public void setH(int h)
    {
        this.h = h;
    }

    public int getScrollPos()
	{
		return scrollPos;
	}

	public void setScrollPos(int pos)
	{
		scrollPos = pos;
	}

	public void goUp()
	{
		scrollPos += scrollSpeed; //make the tiles go down
	}

	public void goDown()
	{
		scrollPos -= scrollSpeed; //make the tiles go up
	}

    public void DrawYourself(Graphics g) //This is a getter for the tile image, which is static so we can call it without creating a tile object
    {
        for(int i = 0; i < model.getLengthOfTiles(); i++)
		{
   			Tile tile = model.getTiles(i);
   			g.drawImage(tileImage, tile.getX(), tile.getY(), tile.getW(), tile.getH(), null);
		} //Draws the tile image

    }
    

    public Json marshal() //When we parse through the array of tiles, it'll call this
    {
        Json objectTile = Json.newObject(); 
        objectTile.add("x", x);
        objectTile.add("y", y);
        objectTile.add("w", w);
        objectTile.add("h", h);
        return objectTile;
    }

    public String toString()
    {
        return "Tile (x,y) = (" + x + ", " + y + "), w = " + w + ", h = " + h;
    }

}