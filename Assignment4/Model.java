/*
Name: Hunter Daily.
Date: March 10th, 2026.
Description: Program responsible for a lot of things
Map editor, collision detection, movement, JSON stuff.
To whoever reads this, I hope your doing well, I apologize for model being so big.
*/

import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Model
{
    private ArrayList<Sprite> sprites; //Array of sprites, formerly for tiles
    private MsPacman msPacman;
    private Ghost ghost;
    private Fruit fruits;
    private Tile tile;
    private boolean editMode = false; //Start off not in edit mode
    private boolean removeMode = false;
    private boolean addTiles = false;
	private ArrayList<Sprite> itemsICanAdd; //List of items that we can add to map editor
	private int currentMapItemEditor = 0; //Keep track of which item we can add or remove

    public Model(MsPacman mp, Fruit f, Ghost g, Tile t) //Constructor
    {
        sprites = new ArrayList<Sprite>();
        this.loadFromJson(); //Load Map
        msPacman = mp;
        fruits = f;
        ghost = g;
        tile = t;

        //Add an instance to the sprite array for detection.
        sprites.add(msPacman);
        sprites.add(fruits);
        sprites.add(ghost);

        //Add items to list of addable items.
		itemsICanAdd = new ArrayList<Sprite>();
		itemsICanAdd.add(tile); //We usually start with tiles so we put this one first
		itemsICanAdd.add(ghost);
		itemsICanAdd.add(fruits);

    }

    public void update()
    {
        //Start of Collision stuff
        for (Sprite sprite : sprites)
        {
            sprite.update();
            if (sprite instanceof Fruit || sprite instanceof Ghost)
                handleNPCCollision(sprite); //Handles fruits and ghosts collision
        }
        checkMsPacmanVsNPCs(); //Mrs Pacman vs Ghosts and Fruits collision
		//End of collision Stuff
		//Start of screen wrap stuff
        /* 
        screenWrap(msPacman);
		screenWrap(fruits);
		screenWrap(ghost);
        */
        screenWrap(); //Screen wrap all sprites if they go outside
		//End of screen wrap stuff
    }

//______________START OF EDIT MODE GETTERS AND SETTERS________________
    public boolean getEditModeValue()  
    { 
        return editMode; 
    }

    public boolean getAddTilesValue()  
    { 
        return addTiles; 
    }

    public boolean getRemoveModeValue()
    { 
        return removeMode; 
    }

    public void setEditModeValue(boolean v)  
    { 
        editMode = v; 
    }

    public void setAddTilesValue(boolean v)  
    { 
        addTiles = v; 
    }

    public void setRemoveModeValue(boolean v)
    { 
        removeMode = v; 
    }

//____________________BEGINNING OF MRS PACMAN MOVEMENT____________________

    public void moveMsPacmanUp() { //UP method
        msPacman.setCurrentDirection(Direction.UP);
        msPacman.updateFace();
        for (Sprite s : sprites)
            if (s != msPacman) s.setY(s.getY() + (int) msPacman.getSpeed());
        detectCollision(msPacman);
    }

    public void moveMsPacmanDown() { //DOWN method
        msPacman.setCurrentDirection(Direction.DOWN);
        msPacman.updateFace();
        for (Sprite s : sprites)
            if (s != msPacman) s.setY(s.getY() - (int) msPacman.getSpeed());
        detectCollision(msPacman);
    }

    public void moveMsPacmanLeft() { //LEFT method
        msPacman.setCurrentDirection(Direction.LEFT);
        msPacman.updateFace();
        msPacman.setX(msPacman.getX() - (int) msPacman.getSpeed());
        detectCollision(msPacman);
    }

    public void moveMsPacmanRight() { //RIGHT method
        msPacman.setCurrentDirection(Direction.RIGHT);
        msPacman.updateFace();
        msPacman.setX(msPacman.getX() + (int) msPacman.getSpeed());
        detectCollision(msPacman);
    }
//____________________END OF MRS PACMAN MOVEMENT____________________
    
//____________________BEGINNING OF COLLISION METHODS____________________

    private boolean spritesOverlap(Sprite a, Sprite b) //Are the two sprites overlapping? if so return true
    {
        return a.getX() < b.getX() + b.getW() && a.getX() + a.getW() > b.getX() && a.getY() < b.getY() + b.getH() && a.getY() + a.getH() > b.getY();
    }

    
    public void detectCollision(Sprite s) //Ms. Pacman vs tiles
    {
        for (Sprite tile : sprites)
        {
            if (tile instanceof Tile && spritesOverlap(s, tile))
            {
                Direction dir = s.getCurrentDirection();
                if (dir == Direction.LEFT)       
                    s.setX(tile.getX() + tile.getW());
                else if (dir == Direction.RIGHT) 
                    s.setX(tile.getX() - s.getW());
                else if (dir == Direction.UP)
                    for (Sprite o : sprites) //o for object
                    { 
                        if (o != msPacman) o.setY(o.getY() - (int) msPacman.getSpeed());  //To handle map moving down.
                    }
                else if (dir == Direction.DOWN)
                    for (Sprite o : sprites) 
                    {  
                        if (o != msPacman) o.setY(o.getY() + (int) msPacman.getSpeed());  //To handle map moving up.
                    }
                return;
            }
        }
    }

    public void handleNPCCollision(Sprite s) //Ghost/Fruit vs tiles
    {
        for (Sprite tile : sprites)
        {
            if (tile instanceof Tile && spritesOverlap(s, tile)) //Checks if overlapping with a tile
            {
                Direction dir = s.getCurrentDirection();
                if (dir == Direction.LEFT)       
                    s.setX(tile.getX() + tile.getW());
                else if (dir == Direction.RIGHT) 
                    s.setX(tile.getX() - s.getW());
                else if (dir == Direction.UP)    
                    s.setY(tile.getY() + tile.getH());
                else if (dir == Direction.DOWN)  
                    s.setY(tile.getY() - s.getH());
                s.chooseRandomDirection(); //Choose a new random direction
                return;
            }
        }
    }

    // Ms. Pacman vs Ghost/Fruit
    private void checkMsPacmanVsNPCs()
    {
        for (Sprite s : sprites)
        {
            if (s instanceof Ghost)
            {
                Ghost g = (Ghost) s;
                if (g.getDisplayStatus() && !g.getIsDead() && spritesOverlap(msPacman, g))
                    g.setIsDead(true);
            }
            else if (s instanceof Fruit)
            {
                Fruit f = (Fruit) s;
                if (f.getDisplayStatus() && spritesOverlap(msPacman, f))
                    f.updateDisplayStatus();
            }
        }
    }

    public void screenWrap() //Make sprite teleport to other sie (Does not work vertically, may need to add that later)
    {
        Iterator<Sprite> it = sprites.iterator();
        while (it.hasNext())
        {
            Sprite s = it.next();
            int x = s.getX();
            int w = s.getW();
            if (x < -32 + w / 2)      
                s.setX(624 - w / 2);
            else if (x > 624 - w / 2)
                s.setX(0 - w / 2);
            
        }
    }
//____________________END OF COLLISION METHODS_____________________


//____________________BEGINNING OF EDITOR METHODS____________________
    public void addTile(int x, int y) //Add tile
    {
        if (!isTile(x, y))
            sprites.add(new Tile(x, y));
    }

	public void addGhost(int x, int y) //Add ghost
	{
		sprites.add(new Ghost(x, y));
	}

	public void addFruit(int x, int y) //Add fruit
	{
		sprites.add(new Fruit(x, y));
	}

    public void removeTile(int x, int y) //Remove tile
    {
        Iterator<Sprite> it = sprites.iterator();
        while (it.hasNext())
        {
            Sprite s = it.next();
            if (s instanceof Tile && s.getX() == x && s.getY() == y)
            {
                it.remove();
                return;
            }
        }
    }

	public void removeGhost(int x, int y) //Remove ghost
	{
		Iterator<Sprite> it = sprites.iterator();
		while (it.hasNext())
		{
			Sprite s = it.next();
			if (s instanceof Ghost && x > s.getX() - s.getW() / 2 && x < s.getX() + s.getW() / 2 && y > s.getY() - s.getH() / 2 && y < s.getY() + s.getH() / 2)
			{ //sorry this is long, checks if mouse is within sprites bounds
				it.remove();
				return;
			}
		}
	}
	
	public void removeFruit(int x, int y) //Remove fruit
    {
        Iterator<Sprite> it = sprites.iterator();
        while (it.hasNext())
        {
            Sprite s = it.next();
            if (s instanceof Fruit && x > s.getX() - s.getW() / 2 && x < s.getX() + s.getW() / 2 && y > s.getY() - s.getH() / 2 && y < s.getY() + s.getH() / 2)
            { //sorry this is long, checks if mouse is within sprites bounds
                it.remove();
                return;
            }
        }
    }

    public boolean isTile(int x, int y)
    {
        for (Sprite s : sprites)
            if (s instanceof Tile && s.getX() == x && s.getY() == y)
                return true;
        return false;
    }

    public int snapToGrid(int screenX, int screenY) //Takes a pre-existing tile and figures out the best way to snap it
    {
        for (Sprite s : sprites)
        {
            if (s instanceof Tile)
            {
                int tileY = s.getY();
                int gridH  = s.getH();
                return (int)(Math.round((double)(screenY - tileY) / gridH) * gridH) + tileY; 
            } //Sprites y position doesn't line up with the y position on window so screeny is used to find where they really are.
        }
        return (screenY / 32) * 32; // I had an error with my tiles not being able to remove or line up with other tiles.
		//Snap grid is here to do math to perfectly allign or remove tiles, so if there are no tiles, it just snaps to the nearest 32 pixels, which is the tile size.
    }

    public void getRidOfAllSprites()
    {
        Iterator<Sprite> it = sprites.iterator();
        while (it.hasNext())
        {
            Sprite s = it.next();
            if (s instanceof Tile || s instanceof Ghost || s instanceof Fruit)
                it.remove();
        }
    }

    public ArrayList<Sprite> getSpritesArrayList() //All sprites on map
	{ 
		return sprites; 
	}

	public ArrayList<Sprite> getItemsICanAdd() //All addable items for map editor
	{ 
		return itemsICanAdd; 
	}

	public int getCurrentMapItemEditor() //Get current item position in array
	{
		return currentMapItemEditor;
	}

	public void updateCurrentMapItemEditor()
	{
		currentMapItemEditor = (currentMapItemEditor + 1) % itemsICanAdd.size(); //Cycle through addable items.
	}

    public BufferedImage getItemIAmAdding() //Method that view gets query from to get the image of the item we are adding. (Top left corner of screen)
    {
        BufferedImage img = null;
        try {
            if (itemsICanAdd.get(currentMapItemEditor) instanceof Tile)
            {
                img = ImageIO.read(new File("images/tile2.png"));
            }
            else if (itemsICanAdd.get(currentMapItemEditor) instanceof Ghost)
            {
                img = ImageIO.read(new File("images/blinky1.png"));
            }
            else if (itemsICanAdd.get(currentMapItemEditor) instanceof Fruit)
            {
                img = ImageIO.read(new File("images/fruit1.png"));
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            img = null;
        }
        return img;
    }
//____________________END OF EDITOR METHODS____________________

//____________________BEGINNING OF JSON METHODS____________________
    public Json marshal()
    {
        Json root = Json.newObject();
        Json list = Json.newList();
        root.add("tiles", list);
        for (Sprite s : sprites)
            list.add(s.marshal());
        return root;
    }

    public void loadFromJson()
    {
        Json root = Json.load("map.json");
        Json list = root.get("tiles");
        for (int i = 0; i < list.size(); i++)
            sprites.add(new Tile(list.get(i)));
    }
//____________________END OF JSON METHODS____________________

}