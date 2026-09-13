/*
Name: Hunter Daily.
Date: February, 22, 2024.
Description: Program responsible for displaying Mrs Pacman and collision
*/

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.ArrayList;

public class MsPacman
{
	private BufferedImage[][] MsPacmanImages;
    private int px;
    private int py;
    private int x;
    private int y;
    private int w;
    private int h;
    private int screenWidth = 624;
    private int screenHeight = 580;
    public double speed;
    private Model model;
    private enum Direction {LEFT, UP, RIGHT, DOWN};
    private Direction currentDirection;
    private int face;
    private int currentFrame;
	

	public MsPacman(Model m)
	{
		// other initializiation code
		MsPacmanImages = new BufferedImage[4][3];
        try 
        { //I couldn't think of a better way to put these in the 2d array, so this is my solution lol.
            MsPacmanImages[0][0] = ImageIO.read(new File("images/mspacman1.png"));
            MsPacmanImages[0][1] = ImageIO.read(new File("images/mspacman2.png")); 
            MsPacmanImages[0][2] = ImageIO.read(new File("images/mspacman3.png")); 
            MsPacmanImages[1][0] = ImageIO.read(new File("images/mspacman4.png")); 
            MsPacmanImages[1][1] = ImageIO.read(new File("images/mspacman5.png")); 
            MsPacmanImages[1][2] = ImageIO.read(new File("images/mspacman6.png")); 
            MsPacmanImages[2][0] = ImageIO.read(new File("images/mspacman7.png")); 
            MsPacmanImages[2][1] = ImageIO.read(new File("images/mspacman8.png")); 
            MsPacmanImages[2][2] = ImageIO.read(new File("images/mspacman9.png")); 
            MsPacmanImages[3][0] = ImageIO.read(new File("images/mspacman10.png")); 
            MsPacmanImages[3][1] = ImageIO.read(new File("images/mspacman11.png")); 
            MsPacmanImages[3][2] = ImageIO.read(new File("images/mspacman12.png")); 
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        this.x = 290;
        this.y = 480;
        this.w = 25;
        this.h = 25;
        this.speed = 5;
        this.face = 0;
        this.currentFrame = 0;
        this.model = m;
        model.setMsPacman(this);
        Direction currentDirection = Direction.LEFT;
	}

    public void update()
    {
        this.detectCollision();
    }

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

    public void updateFace()
    {
        this.currentFrame = (this.currentFrame + 1) % 3; //Iterate through 3 frames
        switch(this.currentDirection)
        {
            case LEFT:
                this.face = 0;
                break;
            case UP:
                this.face = 1;
                break;
            case RIGHT:
                this.face = 2;
                break;
            case DOWN:
                this.face = 3;
                break;
        }
        
    }

    public void goUp()
    {
        this.currentDirection = Direction.UP;
        this.updateFace();
        // scroll zone for top edge
        if (this.y < 150) 
        {
            model.scrollTilesDown((int)this.speed);  // move world up
        } 
        else 
        {
            this.py = this.y;
            this.y -= (int)this.speed;               // move Ms Pacman normally
        }
    }
    public void goDown()
    {
        this.currentDirection = Direction.DOWN;
        this.updateFace();
        // scroll zone for bottom edge
        if (this.y > screenHeight - 150) 
        {
            model.scrollTilesUp((int)this.speed);   // move world down
        } 
        else 
        {
            this.py = this.y;
            this.y += (int)this.speed;              // move Ms Pacman normally
        }
    }

    public void goLeft()
    {
        this.currentDirection = Direction.LEFT;
        this.updateFace();
        this.px = this.x; //Set previous x to current x
        this.x -= (int)this.speed; //Move mrs pacman left
    }

    public void goRight()
    {
        this.currentDirection = Direction.RIGHT;
        this.updateFace();
        this.px = this.x; //Sets previous x to current x
        this.x += (int)this.speed; //Move mrs pacman right
    }

    //Set up collision detection here.
    public boolean detectCollision() //Most important function out of this, check collision between tiles, stops movement if so, and also handles the wrap around of the screen
    {
        Iterator<Tile> it = model.getTilesArrayList().iterator(); // Get an iterator for the tiles in the model

        if (this.x < -32 + this.w / 2) //Left edge of the screen
        {
            this.x = 624 - this.w / 2; //Wrap around to the right edge
            return true;
        }
        else if (this.x > 624 - this.w / 2) //Right edge of the screen
        {
            this.x = 0 - this.w / 2; //Wrap around to the left edge
            return true;
        }
        while (it.hasNext()) //Check if Mrs Pacman is overlapping any of the tiles.
        {
            Tile tile = it.next();
            boolean overLapX = this.x < tile.getX() + tile.getW() && this.x + this.w > tile.getX(); //Check if x is overlapping
            boolean overLapY = this.y < tile.getY() + tile.getH() && this.y + this.h > tile.getY(); //Check if y is overlapping
            if (overLapX && overLapY) //If X and Y are overlapping even a single tile, move x and y coordinates back to previous x and previous y
            {
                this.x = this.px; // Move back to previous x position
                this.y = this.py; // Move back to previous y position
                return true;
            }
        }

    return false;
    }

    public void DrawYourself(Graphics g) //Draw yourself function
    {
        g.drawImage(MsPacmanImages[this.face][currentFrame], x, y, w, h, null); //Draws the tile image
    }

    @Override 
    public String toString() //This was helpful for debugging the detect collision method
    {
        return "MsPacman: x=" + x + ", y=" + y + ", w=" + w + ", h=" + h;
    }

    


}