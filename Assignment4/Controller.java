/*
Name: Hunter Daily.
Date: March 10th, 2026.
Description: Controller responsible for Mrs Pacman key methods, map editor, amd mouse methods.
*/
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Controller implements ActionListener, MouseListener, KeyListener, MouseMotionListener
{
	private boolean keepGoing;
	private Model model;
	private Tile tile;
	private Fruit fruit;
	private boolean keyUp;
	private boolean keyDown;
	private boolean keyLeft;
	private boolean keyRight;
	private boolean editMode = false; //When you load in you will immediately be able to edit
	//private boolean removeMode = false;//Move these to model.
	//private boolean addTiles = false;
	private int lastSnappedX = -1;
	private int lastSnappedY = -1;

	public Controller(Model m, Tile t, Fruit f) //Constructor
	{
		model = m;
		tile = t;
		fruit = f;
		keepGoing = true; //Keep game going
	}


	

	public void actionPerformed(ActionEvent e)
	{
		
	}

	public boolean update() //If one of the keys are pressed, call the corresponding method in the model
	{
		if(keyUp)
			model.moveMsPacmanUp(); //Had to change this to model so it'd be easier to get movement
			fruit.countPacManMovement(); //Once Mrs pacman has gone 100 pixels, the fruit will show up
		if(keyDown)
			model.moveMsPacmanDown();
			fruit.countPacManMovement();
		if(keyLeft)
			model.moveMsPacmanLeft();
			fruit.countPacManMovement();
		if(keyRight)
			model.moveMsPacmanRight();
			fruit.countPacManMovement();
		return keepGoing;
	}

//______________MAP EDITOR METHODS________________
	private void handleSpriteEdit(MouseEvent e) //Method for putting stuff on map (tiles, ghosts, fruits)
	{
		int x = e.getX();
		int y = e.getY();
		if (x >= 50 && x <= 80 && y >= 50 && y <= 80) //Change map editor if you click on the box in top left
		{
			model.updateCurrentMapItemEditor(); //Cycle through map editor
			return;
		}
		if (model.getItemsICanAdd().get(model.getCurrentMapItemEditor()) instanceof Tile) //Is edit mode set to tile?
		{
			int gridW = tile.getW();

			int mapX = (e.getX() / gridW) * gridW; //Snapped X coord
			int mapY = model.snapToGrid(e.getX(), e.getY()); // Method in model to calculate where to snap the tile to.

			if (mapX == lastSnappedX && mapY == lastSnappedY) return; //Prevent adding multiple tiles to the same spot if you drag the mouse

			if (model.getAddTilesValue()) //Are we adding?
			{ 
				model.addTile(mapX, mapY);
			} 
			else if (model.getRemoveModeValue()) //Are we removing?
			{ 
				model.removeTile(mapX, mapY);
			}

			lastSnappedX = mapX;
			lastSnappedY = mapY;
		}
		if (model.getItemsICanAdd().get(model.getCurrentMapItemEditor()) instanceof Ghost) //Is edit mode set to ghost?
		{
			if (model.getAddTilesValue()) //Are we adding?
			{
				model.addGhost(x, y);
			}
			else if (model.getRemoveModeValue()) //Are we removing?
			{
				model.removeGhost(x, y);
			}
		}
		if (model.getItemsICanAdd().get(model.getCurrentMapItemEditor()) instanceof Fruit) //Is edit mode set to fruit?
		{
			if (model.getAddTilesValue()) //Are we adding?
			{
				model.addFruit(x, y);
			}
			else if (model.getRemoveModeValue()) //Are we removing?
			{
				model.removeFruit(x, y);
			}
		}
	}
//______________END OF MAP EDITOR METHODS________________

//______________START OF ALL MOUSE METHODS________________
	public void mouseDragged(MouseEvent e) //If mouse is dragged, send coordinates to model to add a tile
	{
		handleSpriteEdit(e); //Mouse dragged and mouse pressed do the same thing, so I put the code in a separate method to avoid repetition.
	}

	public void mouseReleased(MouseEvent e) 
	{
		//Responsible for not adding an extra tile to the last x and coord if we drag
		if (model.getAddTilesValue() == true)
		{
			lastSnappedX = -1;
    		lastSnappedY = -1;
		}
		
	}

	public void mousePressed(MouseEvent e) //If mouse is pressed, send coordinates to model to add a tile
	{
		handleSpriteEdit(e); //Mouse dragged and mouse pressed do the same thing, so I put the code in a separate method to avoid repetition.
	}
	
	// Empty mouse event methods (Wasn't sure what to do with these).
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
//______________END OF ALL MOUSE METHODS________________

//______________START OF ALL KEY METHODS________________
	public void keyPressed(KeyEvent e) //Code responsible for checking if a key is pressed.
	{
		char c = Character.toLowerCase(e.getKeyChar()); //Get key pressed
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_UP:
				keyUp = true;
				break;
			case KeyEvent.VK_DOWN:
				keyDown = true;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = true;
				break;
			case KeyEvent.VK_RIGHT:
				keyRight = true;
				break;
		}
	}

	public void keyReleased(KeyEvent e) //Code responsible for checking if a key is released.
	{
		char c = Character.toLowerCase(e.getKeyChar());
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_UP:
				keyUp = false;
				break;
			case KeyEvent.VK_DOWN:
				keyDown = false;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = false;
				break;
			case KeyEvent.VK_RIGHT:
				keyRight = false;
				break;
			case KeyEvent.VK_ESCAPE:
				System.exit(0); // closes the window + ends program
				break;
		}
		if(c == 'q')
			keepGoing = false;
		if(c == 'e')
		{
			this.editMode = !this.editMode;
			model.setEditModeValue(this.editMode); //Update models edit method
			if (editMode == true)
					System.out.println("EDIT MODE ON");
			else
			{
				System.out.println("EDIT MODE OFF");
				model.setRemoveModeValue(false);
				model.setAddTilesValue(false);
			}
		}
		if(c == 'r') //Turn Remove on if in edit mode, otherwise does nothing
		{
			if (model.getEditModeValue() == true)
			{
				model.setRemoveModeValue(true);
				model.setAddTilesValue(false);
				System.out.println("REMOVE MODE ON");
			}
		}
		if(c == 'a') //Turn Add on if in edit mode, otherwise does nothing
		{
			if (model.getEditModeValue() == true)
			{
				model.setAddTilesValue(true);
				model.setRemoveModeValue(false);
				System.out.println("ADD TILE MODE ON");
			}
		}
		if(c == 'c') //Remove all tiles if in edit mode, otherwise does nothing
		{
			if (model.getEditModeValue() == true)
			{
				System.out.println("Clear all tiles");
				model.getRidOfAllSprites();
			}
		}
		if(c == 's') //Save to json
		{
			Json json_file = model.marshal();
			json_file.save("map.json");
		}
		if(c == 'l') //Load from json
		{
			model.loadFromJson();
		}
	}
	//Also wasn't entirely sure what this was for.
	public void keyTyped(KeyEvent e)
	{    }

//______________END OF ALL KEY METHODS________________
	

}
