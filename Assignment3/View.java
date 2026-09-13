/*
Name: Hunter Daily.
Date: February, 9, 2024.
Description: Program responsible for displaying everything to the screen.
*/

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class View extends JPanel
{
	//Private variables
	private Model model;
	private Tile tile;
	private MsPacman msPacman;
	private Controller control; //Didn't have this before, but I needed to get the values for editMode, Addtiles, and RemoveMode
	private int scrollPos;
	private int scrollSpeed;

	public View(Controller c, Model m, Tile t, MsPacman mp)
	{
		model = m;
		tile = t;
		control = c; //to call methods from controller
		msPacman = mp;
		scrollPos = 0;	
		scrollSpeed = 1;
		c.setView(this); //<-- This one line of code gave me the BIGGEST headache ever (I originally had it commented so I wasn't able to call view methods from controller)
	}



	public void paintComponent(Graphics g)
	{
		g.setColor(new Color(254, 189, 149)); //Makes the background peach
		g.fillRect(0, 0, this.getWidth(), this.getHeight()); //Fills the background
		tile.DrawYourself(g); //Draws all tiles in the model
		msPacman.DrawYourself(g); //Draws MsPacman
		if (control.getEditModeValue() == true) //Change Edit mode to green
		{
			g.setColor(new Color(34, 139, 34));
			g.fillRect(50, 50, 30, 30);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(new Color(34, 139, 34));
			g.drawString("EDIT MODE ON!", 100, 72);
		}
		
		if (control.getAddTilesValue() == true) //Right side for when addTiles is on
		{
			g.setColor(Color.BLUE);
			g.fillRect(310, 50, 30, 30);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.BLUE);
			g.drawString("ACTIVE MODE ON!", 360, 72);
		}
		else if (control.getRemoveModeValue() == true) //Right side for when Removemode is on
		{
			g.setColor(Color.RED);
			g.fillRect(310, 50, 30, 30);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.RED);
			g.drawString("REMOVE MODE ON!", 360, 72);
		}
		
	}
}
