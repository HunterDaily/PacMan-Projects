/*
Name: Hunter Daily.
Date: February 9, 2024.
Description: Main class for adding tiles
*/

import javax.swing.JFrame;
import java.awt.Toolkit;

public class Game extends JFrame
{
	private boolean keepGoing;
	private Model model;
	private Controller controller;
	private View view;
	private Tile tile;

	public Game()
	{
		//Start of initialization
		model = new Model();
		controller = new Controller(model);
		tile = new Tile();
		view = new View(controller, model, tile);
		keepGoing = true;
		view.addMouseMotionListener(controller);
		view.addMouseListener(controller);
		this.addKeyListener(controller);
		this.setTitle("A2 - Map Editor"); //Title of the window
		this.setSize(624, 580);
		this.setFocusable(true);
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		//End of initialization
	}

	public void run()
	{
		do
		{
			keepGoing = controller.update();
			model.update();
			view.repaint(); // This will indirectly call View.paintComponent
			Toolkit.getDefaultToolkit().sync(); // Updates screen

			// Go to sleep for 50 milliseconds
			try
			{
				Thread.sleep(50);
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			
		}
		while(keepGoing);
		
		//Final message before program ends
		System.out.println("User has decided to close the window!"); 
		System.exit(0);
	}

	public static void main(String[] args)
	{
		Game g = new Game();
		g.run();
	}
}
