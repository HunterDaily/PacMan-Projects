/*
Name: Hunter Daily.
Date: February 22, 2024.
Description: Main class for running the msPacman game
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
	private MsPacman msPacman;

	public Game()
	{
		//Start of initialization
		model = new Model();
		tile = new Tile(model);
		msPacman = new MsPacman(model);
		controller = new Controller(model, tile, msPacman);
		view = new View(controller, model, tile, msPacman);
		keepGoing = true;
		view.addMouseMotionListener(controller);
		view.addMouseListener(controller);
		this.addKeyListener(controller);
		this.setTitle("A3 - Mrs. Pacman"); //Title of the window
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
