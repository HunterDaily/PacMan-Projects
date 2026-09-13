/*
Name: Hunter Daily.
Date: March 10th, 2026.
Description: Parent program resopnsible for tiles, ghosts, fruits, and Mrs Pacman
*/
import java.awt.Graphics;


public abstract class Sprite 
{
    private int x;
    private int y;
    private int w;
    private int h;
    protected Direction currentDirection;


    public Sprite(int x, int y, int w, int h) 
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.currentDirection = Direction.LEFT; //All sprites will start facing left by default.
    }

//_________START OF GETTER METHODS_________
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

    public Direction getCurrentDirection()
    {
        return currentDirection;
    }
//_________END OF GETTER METHODS_________

//_________START OF SETTER METHODS_________
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

    public void setCurrentDirection(Direction d)
    {
        this.currentDirection = d;
    }
//_________END OF SETTER METHODS_________

    public void chooseRandomDirection() //For ghosts and fruits to choose their next direction.
    {
        int pick = (int) (Math.random() * 4); // 0,1,2,3
        switch (pick) {
            case 0 -> currentDirection = Direction.LEFT;
            case 1 -> currentDirection = Direction.RIGHT;
            case 2 -> currentDirection = Direction.UP;
            case 3 -> currentDirection = Direction.DOWN;
        }
    }
    
    //Methods needed for each of the sprites
    public abstract boolean update(); //Methods for each sprite class
    public abstract void Draw(Graphics g); //Methods for each sprite class
    public abstract Json marshal(); //Methods for each sprite class
}
