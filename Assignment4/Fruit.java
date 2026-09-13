/*
Name: Hunter Daily.
Date: March 10th, 2026.
Description: Program responsible for Fruits. Make sure you eat fruit to stay healthy
*/

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics;
public class Fruit extends Sprite
{
    private BufferedImage[] fruitImage;
    private boolean displayStatus; //Keep track of whether fruit has been eaten or not, don't display if it has
    private int currentFrame;
    private int distanceCovered; // This will keep track of how far Mrs Pacman has moved to determine when to show the fruit
    private int speed; //I shoulda just made this a parent class variable but I'm lazy, its whatever.
    private int moveTimer = 0; // Timer to control direction changes

    public Fruit() //Constructor
    {
        super(290, 485, 23, 23); // Call the Sprite constructor to initialize x, y, w, h

        try 
        { 
            fruitImage = new BufferedImage[7];
            for(int i = 0; i < 7; i++)
            {
                fruitImage[i] = ImageIO.read(new File("images/fruit" + (i + 1) + ".png"));
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        this.displayStatus = true; //Fruit has not been eaten
        currentFrame = 0;
        distanceCovered = 0;
        speed = 5;
    }

    public Fruit(int x, int y) //Constructor for map editor
    {
        super(x, y, 23, 23); // Call the Sprite constructor to initialize x, y, w, h
        try 
        { 
            fruitImage = new BufferedImage[7];
            for(int i = 0; i < 7; i++)
            {
                fruitImage[i] = ImageIO.read(new File("images/fruit" + (i + 1) + ".png"));
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        this.displayStatus = true;
        currentFrame = 0;;
        distanceCovered = 0;
        speed = 5;
    }

//____________START OF UPDATE METHODS____________
    public void updateDisplayStatus()
    {
        currentFrame = (currentFrame + 1) % fruitImage.length; // Cycle through the fruit images
        displayStatus = !displayStatus; // Toggle the display status to create a blinking effect
    }

    public boolean getDisplayStatus()
    {
        return displayStatus;
    }
//____________END OF UPDATE METHODS____________

    public void countPacManMovement() //Keeps track of Mrs Pacmans distance covered and if it exceeds a certain amount, it will make the fruit appear
    {
        if (getDisplayStatus()) // If the fruit is currently not displayed, we want to count Mrs Pacman's movement to determine when to show the fruit again
            return;
        distanceCovered += 5;
        if (distanceCovered >= 10000) // If Mrs Pacman has moved 100 pixels, show the fruit
        {
            displayStatus = true;
            distanceCovered = 0; // Reset the distance covered for the next time
        }
        // Placeholder for now, will be implemented in the future        
    }


//_________START OF OVERRIDE METHODS_________
    @Override
    public boolean update()
    {
        if (!getDisplayStatus()) // If the fruit is not currently displayed, we don't need to update its position
            return false;

        // Increment timer to occasionally change direction even if no collision
        moveTimer++;
        if (moveTimer > 120) { // ~2 seconds at 60 FPS
            moveTimer = 0;
        }


        switch (currentDirection) 
        {
            case LEFT -> setX(getX() - speed);
            case RIGHT -> setX(getX() + speed);
            case UP -> setY(getY() - speed);
            case DOWN -> setY(getY() + speed);
        }

        return true;
    }

    @Override
    public void Draw(Graphics g)
    {
        if (getDisplayStatus())
            g.drawImage(fruitImage[currentFrame], getX(), getY(), getW(), getH(), null); // Draws the fruit
    }

    @Override
    public Json marshal()
    {
        return null; // Placeholder for now
    }
//_________END OF OVERRIDE METHODS_________
}
