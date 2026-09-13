/*
Name: Hunter Daily.
Date: March 10th, 2026.
Description: Program responsible for Ghosts 
*/


import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics;

public class Ghost extends Sprite
{
    private BufferedImage[][] ghostImages;
    private BufferedImage[] ghostDeathImage;
    public int speed;
    private int face;
    private int ghostDeathFrame; //What stage of death are they on
    private int currentFrame;
    private boolean displayStatus; //Don't display if dead
    private int moveTimer = 0; // Timer to control direction changes
    private boolean isDead; // This will keep track of whether the ghost is currently in its death animation or not

    public Ghost() //Constructor
    {
        super(290, 100, 23, 23); // Call the Sprite constructor to initialize x, y, w, h
        ghostImages = new BufferedImage[4][2]; //Alive ghost, blinky is the better ghost
        ghostDeathImage = new BufferedImage[8]; //Death animations

        try 
        { 
            int imgNum = 1;
            for(int i = 0; i < 4; i++)
            {
                for(int j = 0; j < 2; j++)
                {
                    ghostImages[i][j] = ImageIO.read(new File("images/blinky" + imgNum + ".png"));
                    imgNum++;
                }
            }
            imgNum = 1;
            for (int i = 0; i < 8; i++)
            {
                ghostDeathImage[i] = ImageIO.read(new File("images/ghost" + imgNum + ".png"));
                imgNum++;
            }

            
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        this.speed = 5;
        this.face = 0;
        ghostDeathFrame = 0;
        this.displayStatus = true; //Ghost starts off as alive
        this.isDead = false; //Not dead
    }

    public Ghost(int x, int y) //Constructor for map editor
    {
        super(x, y, 23, 23); // Call the Sprite constructor to initialize x, y, w, h
        ghostImages = new BufferedImage[4][2];
        ghostDeathImage = new BufferedImage[8];
        try 
        { 
            int imgNum = 1;
            for(int i = 0; i < 4; i++)
            {
                for(int j = 0; j < 2; j++)
                {
                    ghostImages[i][j] = ImageIO.read(new File("images/blinky" + imgNum + ".png"));
                    imgNum++;
                }
            }
            imgNum = 1;
            for (int i = 0; i < 8; i++)
            {
                ghostDeathImage[i] = ImageIO.read(new File("images/ghost" + imgNum + ".png"));
                imgNum++;
            }

            
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        this.speed = 5;
        this.face = 0;
        ghostDeathFrame = 0;
        this.displayStatus = true;
        this.isDead = false;
    }

//_________Start of Ghost alive or dead methods_________
    public boolean getDisplayStatus()
    {
        return displayStatus;
    }

    public void setDisplayStatus(boolean status)
    {
        this.displayStatus = status;
    }

    public boolean getIsDead()
    {
        return isDead;
    }

    public void setIsDead(boolean status)
    {
        this.isDead = status;
    }
//_________End of Ghost alive or dead methods_________


    public void updateFace() //Copied straight from Mrs Pacmam
    {
        this.currentFrame = (this.currentFrame + 1) % 2;
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

//_________START OF OVERRIDE METHODS_________
    @Override
    public boolean update()
    {
        if (getDisplayStatus() && !getIsDead()) // If the fruit is not currently displayed, we don't need to update its position
        { 
            switch (currentDirection) 
            {
                case LEFT:
                    setX(getX() - speed);
                    updateFace();
                    break;
                case RIGHT:
                    setX(getX() + speed);
                    updateFace();
                    break;
                case UP:
                    setY(getY() - speed);
                    updateFace();
                    break;
                case DOWN:
                    setY(getY() + speed);
                    updateFace();
                    break;
            }
        }
        if (isDead)
        {
            setDisplayStatus(false);
            moveTimer++;
            if (moveTimer >= 20) //Roughly 0.5 secs
            { 
                ghostDeathFrame = 3;
            }
            if (moveTimer >= 40) //Roughly` 1 sec
            { 
                if (currentDirection == Direction.LEFT)
                    ghostDeathFrame = 4;
                else if (currentDirection == Direction.RIGHT)
                    ghostDeathFrame = 5;
                else if (currentDirection == Direction.UP)
                    ghostDeathFrame = 6;
                else if (currentDirection == Direction.DOWN)
                    ghostDeathFrame = 7;
            }
            if (moveTimer >= 60) //Roughly 1.5 secs
            { 
                moveTimer = 0;
                setIsDead(false);
            }
        }
        return true;
    }

    @Override
    public void Draw(Graphics g)
    {
        if (displayStatus)
        {
            g.drawImage(ghostImages[this.face][currentFrame], getX(), getY(), getW(), getH(), null); //Draws the ghost
        }
        if (isDead)
        {
            g.drawImage(ghostDeathImage[ghostDeathFrame], getX(), getY(), getW(), getH(), null); //Draws the ghost death image
        }
    }

    @Override
    public Json marshal()
    {
        return null; // Placeholder for now
    }
    //_________END OF OVERRIDE METHODS_________
}
