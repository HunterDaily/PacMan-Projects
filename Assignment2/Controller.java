/*
Name: Hunter Daily.
Date: February, 9, 2024.
Description: Program responsible for control of the turtle (And removing the button).
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
	private View view;
	private Model model;
	private boolean editMode = true; //When you load in you will immediately be able to edit
	private boolean removeMode = false;
	private boolean addTiles = true; //Then add tiles immediately
	private int lastSnappedX = -1;
	private int lastSnappedY = -1;

	public Controller(Model m)
	{
		model = m;
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
		int snapped_X = Math.floorDiv(e.getX(), 32) * 32;
    	int snapped_Y = Math.floorDiv(e.getY() + view.getScrollPos(), 32) * 32;
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
		int snapped_X = Math.floorDiv(e.getX(), 32) * 32;
		int snapped_Y = Math.floorDiv(e.getY() + view.getScrollPos(), 32) * 32;
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

	}

	public void keyReleased(KeyEvent e) //Code responsible for checking if a key is released.
	{
		char c = Character.toLowerCase(e.getKeyChar());
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
		if(c == 'l')
		{
			model.getRidOfAllTiles();
			System.out.println("Loading map.json...");
    		model.loadFromJson();

		}
		if(c == 's')
		{
			System.out.println("Saving to map.json");
			Json json_file = model.marshal();
			json_file.save("map.json");
			System.out.println("SAVE COMPLETE!");
		}
		if(c == '8') //If user stops pressing 8
		{
			view.goUp();
		}
		if(c == '2')//If user stops pressing 2
		{
			view.goDown();
		}
		

	}

	//Also wasn't entirely sure what this was for.
	public void keyTyped(KeyEvent e)
	{    }


	

}
