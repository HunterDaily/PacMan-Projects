/*
Name: Hunter Daily.
Date: March 10th, 2026.
Description: Program responsible for displaying sprites to window as well as displaying edit mode and add/remove mode
*/

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class View extends JPanel
{
	//Private variables
	private Model model;


	public View(Model m)
	{
		model = m;
	}



	public void paintComponent(Graphics g)
	{
		//Basic Screen components
		g.setColor(new Color(254, 189, 149)); //Makes the background peach
		g.fillRect(0, 0, this.getWidth(), this.getHeight()); //Fills the background
		//Put sprites to screen
		for (Sprite sprite : model.getSpritesArrayList()) {
			sprite.Draw(g);
		}
//____________________________EDIT MODE AND ADD/REMOVE MODE INDICATORS___________________________
		if (model.getEditModeValue() == true && model.getAddTilesValue() == true) //Edit mode and add items on
		{
			g.setColor(new Color(34, 139, 34));
			g.fillRect(50, 50, 30, 30);
			g.drawImage(model.getItemIAmAdding(), 52, 52, 27, 27, null);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(new Color(34, 139, 34));
			g.drawString("EDIT MODE ON!", 100, 72);
		}
		else if (model.getEditModeValue() == true && model.getAddTilesValue() == false) //Edit mode on but add items off (so remove mode on)
		{
			g.setColor(Color.RED);
			g.fillRect(50, 50, 30, 30);
			g.drawImage(model.getItemIAmAdding(), 52, 52, 27, 27, null);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.RED);
			g.drawString("EDIT MODE ON!", 100, 72);
		}
		if (model.getAddTilesValue() == true) //Right side for when addTiles is on
		{
			g.setColor(Color.BLUE);
			g.fillRect(310, 50, 30, 30);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.BLUE);
			g.drawString("ADD MAP ITEMS ON!", 360, 72);
		}
		else if (model.getRemoveModeValue() == true) //Right side for when Removemode is on
		{
			g.setColor(Color.RED);
			g.fillRect(310, 50, 30, 30);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.RED);
			g.drawString("REMOVE MODE ON!", 360, 72);
		}
		
	}
}
