/*
Name: Hunter Daily.
Date: February, 22, 2024.
Description: Program responsible for control of mrs pacman and editing tile if you decide to edit my map >:(
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
	private boolean keepGoing; //checks if q or escape was pressed, quit the game if so.
	private View view; //View Object
	private Model model; //Model object
	private Tile tile; //Tile object
	private MsPacman msPacman; //msPacman object
	private boolean keyUp; //Is up key being pressed?
	private boolean keyDown; //Is down key being pressed?
	private boolean keyLeft; //Is left key beign pressed?
	private boolean keyRight; //Is right key being pressed?
	private boolean editMode = false; //Edit mode boolean
	private boolean removeMode = false; //Remove mode boolean
	private boolean addTiles = false; //Add Mode boolean
	private int lastSnappedX = -1; //x coordinate to snap tiles in place
	private int lastSnappedY = -1; //y coordinate to snap tiles in place

	public Controller(Model m, Tile t, MsPacman mp)
	{
		model = m;
		tile = t;
		msPacman = mp;
		keepGoing = true;
	}

	public void setView(View v)
	{
		view = v;
	}

	public void actionPerformed(ActionEvent e)
	{
		
	}

	public boolean update() //If one of the keys are pressed, call the corresponding method in the model
	{
		if(keyUp)
			msPacman.goUp();
		if(keyDown)
			msPacman.goDown();
		if(keyLeft)
			msPacman.goLeft();
		if(keyRight)
			msPacman.goRight();
		return keepGoing;
	}

	//Getters for our editing values (I did not include setters for these modes)
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

//------------------------------------------
//START OF ALL MOUSE METHODS
	public void mouseDragged(MouseEvent e) //If mouse is dragged, send coordinates to model to add a tile
	{
		
		int snapped_X = Math.floorDiv(e.getX(), tile.getX()) * tile.getX();
    	int snapped_Y = Math.floorDiv(e.getY() + tile.getScrollPos(), tile.getY()) * tile.getY();
		if (this.addTiles == true) //Checks if we have edit mode on or not
		{
			//Add a mouse limiter in case the user wobbles so it doesn't add a 100 tiles to the array
			if (snapped_X != lastSnappedX || snapped_Y != lastSnappedY) {
				model.addTile(snapped_X, snapped_Y);

				lastSnappedX = snapped_X; //I found this online the last snapped solution online
				lastSnappedY = snapped_Y; //Its not perfect because theres still some extra tiles being added but it works well enough.
				//Added an if statement in the model method addTile as a side precaution and this fixed the problem
			}
		}
		else if (this.removeMode == true)
		{
			model.removeTile(snapped_X, snapped_Y); //Call remove method from model to remove a tile from the array
		}
		
	}

	public void mouseReleased(MouseEvent e) 
	{
		//Responsible for not adding an extra tile to the last x and coord if we drag
		if (this.addTiles == true)
		{
			lastSnappedX = -1;
    		lastSnappedY = -1;
		}
		
	}

	public void mousePressed(MouseEvent e) //If mouse is pressed, send coordinates to model to add a tile
	{
		int snapped_X = Math.floorDiv(e.getX(), tile.getX()) * tile.getX();
		int snapped_Y = Math.floorDiv(e.getY() + tile.getScrollPos(), tile.getY()) * tile.getY();
		if (this.addTiles == true)
		{
			model.addTile(snapped_X, snapped_Y);
		}
		else if(this.removeMode == true)
		{
			model.removeTile(snapped_X, snapped_Y);
		}
	}
	
	// Empty mouse event methods (Wasn't sure what to do with these).
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
//END OF ALL MOUSE METHODS
//---------------------------------------------------

//---------------------------------------------------
//Start All Keybind Methods	
	public void keyPressed(KeyEvent e) //Code responsible for checking if a key is pressed.
	{
		char c = Character.toLowerCase(e.getKeyChar());
		switch(e.getKeyCode()) //Checks if a key is being pressed
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
		switch(e.getKeyCode()) //Stop moving mrs pacman if key was released
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
			case KeyEvent.VK_ESCAPE: //My escape key stopped working to quit the program, not sure why it stopped but I had to add this
				System.exit(0); // closes the window + ends program
				break;
		}
		if(c == 'q')
			keepGoing = false;
		if(c == 'e')
		{
			this.editMode = !this.editMode;
			if (editMode == true)
					System.out.println("EDIT MODE ON");
				else
					System.out.println("EDIT MODE OFF");
					removeMode = false;
					addTiles = false;
		}
		if(c == 'r')
		{
			if (editMode == true)
			{
				this.removeMode = true;
				this.addTiles = false;
				System.out.println("REMOVE MODE ON");
			}
		}
		if(c == 'a')
		{
			if (editMode == true)
			{
				this.addTiles = true;
				this.removeMode = false;
				System.out.println("ADD TILE MODE ON");
			}
		}
		if(c == 'c')
		{
			if (editMode == true)
			{
				System.out.println("Clear all tiles");
				model.getRidOfAllTiles();
			}
		}
	}
	//Also wasn't entirely sure what this was for.
	public void keyTyped(KeyEvent e)
	{    }

	
	

}
