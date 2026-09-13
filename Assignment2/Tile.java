/*
Name: Hunter Daily.
Date: February, 9, 2024.
Description: Class for keeping width/height and x/y coordinates
*/

public class Tile
{
    private int x;
    private int y;
    private int w;
    private int h;


    public Tile() //Default constructor (Constructor Chaining in play)
    {
        this(0, 0);
        this.w = 32;
        this.h = 32;
    }


    public Tile(int x, int y) 
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
    
    public Json marshal() //When we parse through the array of tiles, it'll call this
    {
        Json objectTile = Json.newObject(); 
        objectTile.add("x", x);
        objectTile.add("y", y);
        objectTile.add("w", w);
        objectTile.add("h", h);
        return objectTile;
    }

}