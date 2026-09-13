/*
Name: Hunter Daily.
Date: February, 9, 2024.
Description: Program responsible for displaying tiles, the edit status/remove or add status
*/

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

public class View extends JPanel
{
	//Private variables
	private JButton b1;
	private BufferedImage tileImage;
	private Tile tile;
	private Model model;
	private Controller control; //Didn't have this before, but I needed to get the values for editMode, Addtiles, and RemoveMode
	private int scrollPos;
	private int scrollSpeed;

	public View(Controller c, Model m, Tile t)
	{
		model = m;
		tile = t;
		//Start of loading tile image
		try
		{
			this.tileImage = ImageIO.read(new File("images/tile2.png")); //had turtle here before, now were loading tile images
		}
		catch(Exception e) 
		{
    		e.printStackTrace(System.err);
    		System.exit(1);
		}
		//End of loading tile image
		control = c; //to call methods from controller
		scrollPos = 0;	
		scrollSpeed = 20;
		c.setView(this); //<-- This one line of code gave me the BIGGEST headache ever (I originally had it commented so I wasn't able to call view methods from controller)
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

	public void paintComponent(Graphics g)
	{
		g.setColor(new Color(254, 189, 149)); //Makes the background peach
		g.fillRect(0, 0, this.getWidth(), this.getHeight()); //Fills the background
		for(int i = 0; i < model.getLengthOfTiles(); i++)
		{
   			Tile tile = model.getTiles(i);
   			g.drawImage(this.tileImage, tile.getX(), tile.getY() - scrollPos, tile.getW(), tile.getH(), null);
		} //Draws the tile image

		if (control.getEditModeValue() == true) //Change Edit mode to green
		{
			g.setColor(new Color(34, 139, 34));
			g.fillRect(50, 50, 30, 30);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(new Color(34, 139, 34));
			g.drawString("EDIT MODE ON!", 100, 72);
		}
		else //If edit mode is off, change editmode to red
		{
			g.setColor(Color.RED);
			g.fillRect(50, 50, 30, 30);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.RED);
			g.drawString("EDIT MODE OFF!", 100, 72);
		}
		
		if (control.getAddTilesValue() == true) //Right side for when addTiles is on
		{
			g.setColor(Color.BLUE);
			g.fillRect(400, 50, 30, 30);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.BLUE);
			g.drawString("ACTIVE MODE ON!", 450, 72);
		}
		else if (control.getRemoveModeValue() == true) //Right side for when Removemode is on
		{
			g.setColor(Color.RED);
			g.fillRect(400, 50, 30, 30);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.RED);
			g.drawString("REMOVE MODE ON!", 450, 72);
		}
		
	}
}
