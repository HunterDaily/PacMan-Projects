/*
Name: Hunter Daily.
Date: March 10th, 2026.
Description: Class for keeping width/height and x/y coordinates
*/
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics;

public class Tile extends Sprite
{
    public int scrollPos = 0;
    private static BufferedImage tileImage; //This is static because all tiles will be using the same image, so we only need to load it once
    static {
        try {
            tileImage = ImageIO.read(new File("images/tile2.png")); // example
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Tile() //Default constructor
    {
        super(32, 32, 32, 32); // Call the Sprite constructor to initialize x, y, w, h  
    }

    public Tile(int x, int y) //Constructor for map editor
    {
        super(x, y, 32, 32); // Call the Sprite constructor to initialize x, y, w, h    
    }

    public Tile(Json ob) { //This is to rebuild those tile objects when we load map.json
        super((int) ob.getLong("x"), (int) ob.getLong("y"), (int) ob.getLong("w"), (int) ob.getLong("h")); // Call the Sprite constructor to initialize x, y, w, h
    }

//________________STATIC METHODS________________
    @Override
    public void Draw(Graphics g) //This is a getter for the tile image, which is static so we can call it without creating a tile object
    {
   		g.drawImage(tileImage, getX(), getY(), getW(), getH(), null);
		//Draws the tile image
    }
    
    @Override
    public Json marshal() //When we parse through the array of tiles, it'll call this
    {
        Json objectTile = Json.newObject();
        objectTile.add("x", getX());
        objectTile.add("y", getY());
        objectTile.add("w", getW());
        objectTile.add("h", getH());
        return objectTile;
    }

    @Override
    public boolean update() //This is to satisfy the abstract method in Sprite, but we don't need it for Tile so it just returns false
    {
        return true;
    }
//________________END OF STATIC METHODS________________
    public String toString() //Tostring
    {
        return "Tile x = " + getX() + ", y = " + getY() + "), w = " + getW() + ", h = " + getH();
    }

}