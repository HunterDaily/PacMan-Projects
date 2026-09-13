/*
Name: Hunter Daily.
Date: March 10th, 2026.
Description: Program responsible for Mrs Pacman
*/

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics;



public class MsPacman extends Sprite
{
	private BufferedImage[][] MsPacmanImages; //Images
    public double speed; //Speed (not sure why this needed to be a double) (RUSHING OR DRAGGING)
    private int face; //Which way is she facing?
    private int currentFrame; //Mouth open or closed???
	

	public MsPacman() //Constructor
	{
		// other initializiation code
        super(290, 260, 23, 23); // Call the Sprite constructor to initialize x, y, w, h
		MsPacmanImages = new BufferedImage[4][3];
        try 
        { 
            int imgNum = 1;
            for(int i = 0; i < 4; i++)
            {
                for(int j = 0; j < 3; j++)
                {
                    MsPacmanImages[i][j] = ImageIO.read(new File("images/mspacman" + imgNum + ".png")); //Image folders
                    imgNum++;
                }
            }
            
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        this.speed = 5;
        this.face = 0;
        this.currentFrame = 0;
	}

    public void updateFace() //Method that changes Pacmans face
    {
        this.currentFrame = (this.currentFrame + 1) % 3;
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

    public double getSpeed() //Get Speed
    {
        return this.speed;
    }

//_________START OF OVERRIDE METHODS_________
    @Override
    public void Draw(Graphics g)
    {
        g.drawImage(MsPacmanImages[this.face][currentFrame], getX(), getY(), getW(), getH(), null); //Draws Mrs Pacman
    }


    @Override
    public boolean update()
    {
        return true;
    }

    @Override 
    public String toString()
    {
        return "MsPacman: x=" + getX() + ", y=" + getY() + ", w=" + getW() + ", h=" + getH();
    }

    @Override
    public Json marshal() //This is to satisfy the abstract method in Sprite, but we don't need it for MsPacman so it just returns null
    {
        return null;
    }
//_________END OF OVERRIDE METHODS_________
    


}