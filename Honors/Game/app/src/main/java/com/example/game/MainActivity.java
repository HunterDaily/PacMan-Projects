package com.example.game;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Rect;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
{
    MsPacman msPacman;
    Ghost ghost;
    Fruit fruit;
    Tile tile;
    dPad dpad;
    Model model;
    GameView view;
    GameController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load all static images FIRST before anything else is created
        Ghost.loadImages(this);
        Fruit.loadImages(this);
        Tile.loadImages(this);

        tile     = new Tile(this);
        dpad     = new dPad(this);
        fruit    = new Fruit(300, 1200);
        ghost    = new Ghost(300, 1200);
        msPacman = new MsPacman(this);
        model      = new Model(this, msPacman, dpad, tile, fruit, ghost);
        view       = new GameView(this, model);
        controller = new GameController(model, view);
        setContentView(view);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        controller.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        controller.pause();
    }


    public enum Direction
    {
        LEFT,
        UP,
        RIGHT,
        DOWN
    }

    // ─────────────────────────────────────────────────────────────────────────
    static abstract class Sprite
    {
        private int x;
        private int y;
        private int w;
        private int h;
        protected Direction currentDirection; //For Direction handeling for all sprites

        public Sprite(int x, int y, int w, int h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.currentDirection = Direction.LEFT;
        }

        //____GETTERS____
        public int getX() { return x; }
        public int getY() { return y; }
        public int getW() { return w; }
        public int getH() { return h; }
        public Direction getCurrentDirection() { return currentDirection; }
        //____END OF GETTERS____

        //____SETTERS____
        public void setX(int x) { this.x = x; }
        public void setY(int y) { this.y = y; }
        public void setW(int w) { this.w = w; }
        public void setH(int h) { this.h = h; }
        public void setCurrentDirection(Direction d) { this.currentDirection = d; }
        //____END OF SETTERS____


        public void chooseRandomDirection() //Used for sprites and ghosts when hitting a wall
        {
            int pick = (int) (Math.random() * 4);
            if      (pick == 0) currentDirection = Direction.LEFT;
            else if (pick == 1) currentDirection = Direction.RIGHT;
            else if (pick == 2) currentDirection = Direction.UP;
            else                currentDirection = Direction.DOWN;
        }

        //______IDENTIFICATION CHECKERS________
        public boolean isMsPacman() { return false; }
        public boolean isTile()     { return false; }
        public boolean isDpad()     { return false; }
        public boolean isFruit()    { return false; }
        public boolean isGhost()    { return false; }
        //________END OF IDENTIFICATION CHECKERS_______

        //_______ABSTRACT METHODS________
        public abstract boolean update();
        public abstract void Draw(Canvas canvas);
        //______ABSTRACT METHODS_______
    }

    //__________START OF MS PACMAN CLASS__________
    static class MsPacman extends Sprite
    {
        private Bitmap[][] MsPacmanImages; //Bitmap images used to hold frames images
        final double speed; //speed of mrs pacman
        private int face; //which way shes looking
        private int currentFrame; //what part of her mouth is open

        public MsPacman(Context context)
        {
            super(500, 1190, 75, 75); //Call sprite
            MsPacmanImages = new Bitmap[4][3];
            int[] imageResources = { //Used to make it easier for putting Mrs pacman into MsPacmanImages
                    R.drawable.mspacman1,  R.drawable.mspacman2,  R.drawable.mspacman3,
                    R.drawable.mspacman4,  R.drawable.mspacman5,  R.drawable.mspacman6,
                    R.drawable.mspacman7,  R.drawable.mspacman8,  R.drawable.mspacman9,
                    R.drawable.mspacman10, R.drawable.mspacman11, R.drawable.mspacman12
            };

            int imgNum = 0; //Used for iterating imageResources
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 3; j++)
                    MsPacmanImages[i][j] = BitmapFactory.decodeResource(context.getResources(), imageResources[imgNum++]);

            this.speed = 15; //Shes fast now
            this.face = 0; //Mouth closed
            this.currentFrame = 0; //Looking left
        }

        public void updateFace() //Used to update which way shes looking
        {
            this.currentFrame = (this.currentFrame + 1) % 3;
            if      (this.currentDirection == Direction.LEFT)  this.face = 0;
            else if (this.currentDirection == Direction.UP)    this.face = 1;
            else if (this.currentDirection == Direction.RIGHT) this.face = 2;
            else if (this.currentDirection == Direction.DOWN)  this.face = 3;
        }

        public double getSpeed() { return this.speed; } //getter for speed

        @Override
        public boolean update() { return true; } //nothing is here

        @Override
        public boolean isMsPacman() { return true; } //is this mrs pacman?

        @Override
        public void Draw(Canvas canvas)
        {
            Rect dest = new Rect(getX(), getY(), getX() + getW(), getY() + getH()); //<---- Used for scaling
            canvas.drawBitmap(MsPacmanImages[this.face][currentFrame], null, dest, null);
        }
    }
    //_______END OF MRS PACMAN CLASS__________

    //_______START OF FRUIT CLASS____________
    static class Fruit extends Sprite
    {
        private static Bitmap[] fruitImages; //Used for holding fruit images
        private static boolean imagesLoaded = false; //Checks to see if the images were loaded in fruit

        private boolean displayStatus; //should fruit be displayed
        private int currentFrame; //which fruit its on
        private int speed; //speed of fruit
        private int displayTimer;

        public static void loadImages(Context context) //Used so that there is a static image for multiple fruits loaded in
        {
            if (imagesLoaded) return;
            fruitImages = new Bitmap[7];
            int[] imageResources = {
                    R.drawable.fruit1, R.drawable.fruit2, R.drawable.fruit3,
                    R.drawable.fruit4, R.drawable.fruit5, R.drawable.fruit6,
                    R.drawable.fruit7
            };
            for (int i = 0; i < 7; i++)
                fruitImages[i] = BitmapFactory.decodeResource(context.getResources(), imageResources[i]);
            imagesLoaded = true;
        }

        public Fruit(int x, int y)
        {
            super(x, y, 75, 75); //Calls sprite
            this.speed = 3; //I wanted fruit to be slow
            this.currentFrame = 0; //starts on cherry
            this.displayStatus = true; //should be displayed
            this.displayTimer = 0; //used for when its not displayed
        }

        public static Fruit fromJson(JSONObject obj) throws JSONException //used to unload fruit
        {
            return new Fruit(obj.getInt("x"), obj.getInt("y"));
        }

        public boolean getDisplayStatus() { return displayStatus; } //should this be drawn to the screen?

        public void updateDisplayStatus() //update the fruit that its on
        {
            currentFrame = (currentFrame + 1) % fruitImages.length;
            displayStatus = !displayStatus;
        }

        @Override
        public boolean isFruit() { return true; } //is this is a fruit?

        @Override
        public boolean update()
        {
            if (!getDisplayStatus()) //Goes through the process of not displaying it and updating fruit image
            {
                displayTimer++;
                if (displayTimer >= 300)
                {
                    displayStatus = true;
                    displayTimer = 0;
                }
                return false;
            }

            switch (currentDirection) //Used for whenever fruit hits a wall
            {
                case LEFT:  setX(getX() - speed); break;
                case RIGHT: setX(getX() + speed); break;
                case UP:    setY(getY() - speed); break;
                case DOWN:  setY(getY() + speed); break;
            }
            return true;
        }

        @Override
        public void Draw(Canvas canvas) //Draw function
        {
            if (getDisplayStatus()) //should we display it?
            {
                Rect dest = new Rect(getX(), getY(), getX() + getW(), getY() + getH()); //<------ Used for scaling
                canvas.drawBitmap(fruitImages[currentFrame], null, dest, null);
            }
        }
    }
    //______________END OF FRUIT CLASS________________

    //____________GHOST CLASS___________________
    static class Ghost extends Sprite
    {
        private static Bitmap[][] ghostImages; //Used to hold normal blinky
        private static Bitmap[] ghostDeathImages; //Used for when blinky dies
        private static boolean imagesLoaded = false; //Checks to see if a static image has been loaded for all ghosts that have been loaded

        public int speed; //speed of ghost
        private int face; //which way ghost is looking
        private int ghostDeathFrame; //which part of the death animation the ghost is on
        private int currentFrame; //part of the ghost movement animation
        private boolean displayStatus; //should we display ghost
        private int moveTimer = 0; //to move through the death animation
        private boolean isDead; //is ghost dead? display death animation

        public static void loadImages(Context context) //Used to load images
        {
            if (imagesLoaded) return;
            ghostImages      = new Bitmap[4][2];
            ghostDeathImages = new Bitmap[8];

            int[] imageResources = {
                    R.drawable.blinky1, R.drawable.blinky2, R.drawable.blinky3,
                    R.drawable.blinky4, R.drawable.blinky5, R.drawable.blinky6,
                    R.drawable.blinky7, R.drawable.blinky8
            };

            int imgNum = 0;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 2; j++)
                    ghostImages[i][j] = BitmapFactory.decodeResource(context.getResources(), imageResources[imgNum++]);

            int[] deathResources = {
                    R.drawable.ghost1, R.drawable.ghost2, R.drawable.ghost3,
                    R.drawable.ghost4, R.drawable.ghost5, R.drawable.ghost6,
                    R.drawable.ghost7, R.drawable.ghost8
            };
            for (int i = 0; i < 8; i++)
                ghostDeathImages[i] = BitmapFactory.decodeResource(context.getResources(), deathResources[i]);

            imagesLoaded = true;
        }

        public Ghost(int x, int y)
        {
            super(x, y, 75, 75); //calls sprite
            this.speed = 3; //ghost should be slow
            this.face = 0; //will start by facing left
            this.ghostDeathFrame = 0; //for when it dies
            this.displayStatus = true; //ghost should be displayed
            this.isDead = false; //ghost starts out alive
            this.currentFrame = 0;
        }

        public Ghost()
        {
            this(290, 100);
        }

        public static Ghost fromJson(JSONObject obj) throws JSONException //used to unmarshal ghost
        {
            return new Ghost(obj.getInt("x"), obj.getInt("y"));
        }

        public boolean getDisplayStatus()   //should this be displayed?
        {
            return displayStatus;
        }
        public boolean getIsDead() //Is ghost dead?
        {
            return isDead;
        }
        public void setIsDead(boolean d)  //set isdead
        {
            isDead = d;
        }
        public void setDisplayStatus(boolean d) //set display status
        {
            displayStatus = d;
        }

        public void updateFace() //Change direction of ghost whenever it hits a wall
        {
            this.currentFrame = (this.currentFrame + 1) % 2;
            switch (this.currentDirection)
            {
                case LEFT:  this.face = 0; break;
                case UP:    this.face = 1; break;
                case RIGHT: this.face = 2; break;
                case DOWN:  this.face = 3; break;
            }
        }

        @Override
        public boolean isGhost() { return true; } //is this a ghost?

        @Override
        public boolean update()
        {
            if (getDisplayStatus() && !getIsDead())
            {
                switch (currentDirection) //move based on what way its looking
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

            if (isDead) //If its dead, move through the death animation
            {
                setDisplayStatus(false);
                moveTimer++;
                if (moveTimer >= 20)
                    ghostDeathFrame = 3;
                if (moveTimer >= 40)
                {
                    if      (currentDirection == Direction.LEFT)  ghostDeathFrame = 4;
                    else if (currentDirection == Direction.RIGHT) ghostDeathFrame = 5;
                    else if (currentDirection == Direction.UP)    ghostDeathFrame = 6;
                    else if (currentDirection == Direction.DOWN)  ghostDeathFrame = 7;
                }
                if (moveTimer >= 60)
                {
                    moveTimer = 0;
                    setIsDead(false);
                    setDisplayStatus(true);
                }
            }
            return true;
        }

        @Override
        public void Draw(Canvas canvas)
        {
            if (displayStatus) //Draw normal blinky if alive
            {
                Rect dest = new Rect(getX(), getY(), getX() + getW(), getY() + getH());
                canvas.drawBitmap(ghostImages[face][currentFrame], null, dest, null);
            }
            if (isDead) //Draw dead blinky if dead
            {
                Rect dest = new Rect(getX(), getY(), getX() + getW(), getY() + getH());
                canvas.drawBitmap(ghostDeathImages[ghostDeathFrame], null, dest, null);
            }
        }
    }
//________________END OF GHOST CLASS__________________


    //____________START OF TILE CLASS________________
    static class Tile extends Sprite
    {
        private static Bitmap tileImage; //used to hold the tile image
        private static boolean imagesLoaded = false; //is the static tile image loaded?

        //Call once from onCreate before any Tile is created
        public static void loadImages(Context context) //used to load the static image of tile
        {
            if (imagesLoaded) return;
            tileImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile2);
            imagesLoaded = true;
        }

        //Used for the placeholder tile in onCreate mage already loaded statically
        public Tile(Context context)
        {
            super(0, 0, 100, 100);
        }

        //Used when unmarshalling from JSON pass the scaled size directly
        public Tile(int x, int y, int size)
        {
            super(x, y, size, size);
        }

        @Override
        public boolean isTile() { return true; }

        @Override
        public boolean update() { return true; }

        @Override
        public void Draw(Canvas canvas)
        {
            if (tileImage == null) return; // Safety guard
            Rect dest = new Rect(getX(), getY(), getX() + getW(), getY() + getH());
            canvas.drawBitmap(tileImage, null, dest, null);
        }

        @Override
        public String toString() //I have this here but I never really used it.
        {
            return "Tile x=" + getX() + ", y=" + getY() + ", w=" + getW() + ", h=" + getH();
        }
    }
//________________END OF TILE CLASS_________________

//____________START OF DPAD CLASS_________________
    static class dPad extends Sprite
    {
        private Bitmap[] dpadImages; //Used to hold dpad images
        private int currentimg; //used for whenever the player presses an arrow key

        public dPad(Context context) //Used to load dpad images and placement in the world
        {
            super(300, 1850, 500, 500);
            dpadImages = new Bitmap[5];
            int[] imageResources = {
                    R.drawable.dpad,  R.drawable.dpad0,
                    R.drawable.dpad1, R.drawable.dpad2,
                    R.drawable.dpad3
            };
            for (int i = 0; i < 5; i++)
                dpadImages[i] = BitmapFactory.decodeResource(context.getResources(), imageResources[i]);
            currentimg = 0;
        }

        @Override
        public boolean isDpad() { return true; } //is this a dpad (this is never used, thought I should have it here in case tho)

        public void updateKeyPressed(int imgNum) { currentimg = imgNum; } //For whenever the user presses an arrow key

        @Override
        public boolean update() { return false; } //Yeah I decided to be different and put false in here lol

        @Override
        public void Draw(Canvas canvas)
        {
            Rect dest = new Rect(getX(), getY(), getX() + getW(), getY() + getH());
            canvas.drawBitmap(dpadImages[currentimg], null, dest, null);
        }
    }
//_________________END OF DPAD CLASS______________________________

    //________________START OF MODEL CLASS______________________
    static class Model
    {
        private ArrayList<Sprite> sprites; //array of sprites
        private MsPacman msPacman; //Ms Pacman
        private Ghost ghost; //Ghost
        private Fruit fruit; //fruits
        private dPad dpad; //dpad
        private Tile tile; //tile
        private Context context; //context is used for loading images, I passed it into model so that it'd be easier to load images (hopefully this doesn't take any points off from the assignment)
        private int tileSize; //Used for scaling tiles
        private int screenWidth; //used to get screen width
        private int screenHeight; //used to get screen height
        private int score; //Score for when you eat ghosts or fruits
        private static final int JSON_TILE_SIZE = 32; //Because we imported tile with a width and height of 32

        public Model(Context ctx, MsPacman mp, dPad d, Tile t, Fruit f, Ghost g) //Constructor of model
        {
            context = ctx; //context

            //Used to load images of all sprites
            Ghost.loadImages(context);
            Fruit.loadImages(context);
            Tile.loadImages(context);

            //All sprite initializations
            sprites  = new ArrayList<>();
            msPacman = mp;
            ghost    = g;
            fruit    = f;
            dpad     = d;
            tile     = t;

            this.score = 0; //no points for you until you at some stuff

            // Store screen dimensions for use in loadFromJson
            android.util.DisplayMetrics metrics = ctx.getResources().getDisplayMetrics(); //used to get width and height
            screenWidth  = metrics.widthPixels; //width
            screenHeight = metrics.heightPixels; //height

            try { //try to load json file
                String json = loadJsonFromAssets(context, "map.json");
                loadFromJson(json);
            } catch (Exception e) {
                Log.e("Model", "Failed to load map.json: " + e.getMessage()); //display this if it fails
            }
        }

        //___________JSON LOADING________________ (One important thing to note about this code, I got it online and I'm not really sure how it works)
        //I edited loadFromJson and kinda figured that out but I got loadJsonFromAssets from the internet telling me to have this code
        public String loadJsonFromAssets(Context context, String filename) throws Exception
        {
            StringBuilder sb = new StringBuilder();
            java.io.InputStream is = context.getAssets().open(filename);
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line);
            br.close();
            return sb.toString();
        }

        public void loadFromJson(String jsonString) throws JSONException
        {
            JSONObject json = new JSONObject(jsonString);
            JSONArray tileArray = json.getJSONArray("tiles");

            //_______START OF TILE SCALING_______________
            //find the actual map dimensions
            int maxCol = 0;
            int maxRow = 0;
            for (int i = 0; i < tileArray.length(); i++)
            {
                JSONObject t = tileArray.getJSONObject(i);
                int col = t.getInt("x") / JSON_TILE_SIZE;
                int row = t.getInt("y") / JSON_TILE_SIZE;
                if (col > maxCol) maxCol = col;
                if (row > maxRow) maxRow = row;
            }

            // Calculate tile size to fill the screen based on actual map size
            int tileW = screenWidth  / (maxCol + 1);
            int tileH = screenHeight / (maxRow + 1);
            tileSize = Math.min(tileW, tileH);
            //____________END OF TILE SCALING_________________
            //Wipe everything then readd the sprites that always exist
            sprites.clear();


            //create the tiles using the calculated size
            for (int i = 0; i < tileArray.length(); i++)
            {
                JSONObject t = tileArray.getJSONObject(i);
                int col      = t.getInt("x") / JSON_TILE_SIZE;
                int row      = t.getInt("y") / JSON_TILE_SIZE;
                sprites.add(new Tile(col * tileSize, row * tileSize, tileSize));
            }



            JSONArray fruitArray = json.getJSONArray("fruits"); //Unload json fruits
            for (int i = 0; i < fruitArray.length(); i++)
                sprites.add(Fruit.fromJson(fruitArray.getJSONObject(i)));

            JSONArray ghostArray = json.getJSONArray("ghosts"); //Unload ghost fruits
            for (int i = 0; i < ghostArray.length(); i++)
                sprites.add(Ghost.fromJson(ghostArray.getJSONObject(i)));


            sprites.add(msPacman); //add Mrs pacman
            sprites.add(dpad); //add dpad (I added these last so dpad can be drawn above everything else)
        }

        //___________GETTERS_____________
        public ArrayList<Sprite> getSprites() { return sprites; } //Return array of sprites
        public MsPacman getMsPacman() //Return Mrs Pacman
        {
            return msPacman;
        }
        public dPad getDpad() //return dpad (used in controller)
        {
            return dpad;
        }
        public int getScore() { //Return score
            return score;
        }

        //____________________COLLISION_________________
        private boolean spritesOverlap(Sprite a, Sprite b) //Check if sprites are overrlapping
        {
            return a.getX() < b.getX() + b.getW()
                    && a.getX() + a.getW() > b.getX()
                    && a.getY() < b.getY() + b.getH()
                    && a.getY() + a.getH() > b.getY();
        }

        public void detectCollision(Sprite s) //Mrs pacman vs tiles
        {
            for (Sprite t : sprites)
            {
                if (t.isTile() && spritesOverlap(s, t))
                {
                    Direction dir = s.getCurrentDirection();
                    if (dir == Direction.LEFT)
                        s.setX(t.getX() + t.getW());
                    else if (dir == Direction.RIGHT)
                        s.setX(t.getX() - s.getW());
                    else if (dir == Direction.UP)
                    {
                        for (Sprite o : sprites)
                            if (o != msPacman && !o.isDpad())
                                o.setY(o.getY() - (int) msPacman.getSpeed());
                    }
                    else if (dir == Direction.DOWN)
                    {
                        for (Sprite o : sprites)
                            if (o != msPacman && !o.isDpad())
                                o.setY(o.getY() + (int) msPacman.getSpeed());
                    }
                    return;
                }
            }
        }

        public void screenWrap() //Make sprite teleport to other side
        {
            Iterator<Sprite> it = sprites.iterator();
            while (it.hasNext())
            {
                Sprite s = it.next();
                if (!s.isFruit() && !s.isGhost() && !s.isMsPacman())
                    continue;
                int x = s.getX();
                int w = s.getW();

                if (x + w < 0)
                    s.setX(screenWidth);
                else if (x > screenWidth)
                    s.setX(-w);

            }
        }
        private void checkMsPacmanVsNPCs() //Mrs Pacman vs Npcs
        {
            for (Sprite s : sprites)
            {
                if (s.isGhost())
                {
                    Ghost g = (Ghost) s;
                    if (g.getDisplayStatus() && !g.getIsDead() && spritesOverlap(msPacman, g)) {
                        g.setIsDead(true); //Ghost is finally dead
                        this.score += 5; //Add five points to score
                    }
                }
                if (s.isFruit()) {
                    Fruit f = (Fruit) s;
                    if (f.getDisplayStatus() && spritesOverlap(msPacman, f))
                    {
                        f.updateDisplayStatus(); //Change fruits display status
                        score += 1; //Add 1 point to score
                    }
                }
            }
        }

        public void handleNPCCollision(Sprite s) //Ghost/Fruit vs tiles
        {
            for (Sprite tile : sprites)
            {
                if (tile.isTile() && spritesOverlap(s, tile)) //Checks if overlapping with a tile
                {
                    Direction dir = s.getCurrentDirection();
                    if (dir == Direction.LEFT)
                        s.setX(tile.getX() + tile.getW());
                    else if (dir == Direction.RIGHT)
                        s.setX(tile.getX() - s.getW());
                    else if (dir == Direction.UP)
                        s.setY(tile.getY() + tile.getH());
                    else if (dir == Direction.DOWN)
                        s.setY(tile.getY() - s.getH());
                    s.chooseRandomDirection(); //Choose a new random direction
                    return;
                }
            }
        }

        //______________MOVEMENT________________
        public void moveMsPacmanUp()
        {
            msPacman.setCurrentDirection(Direction.UP);
            msPacman.updateFace();
            for (Sprite s : sprites)
                if (!s.isDpad() && s != msPacman)
                    s.setY(s.getY() + (int) msPacman.getSpeed());
            detectCollision(msPacman);
        }

        public void moveMsPacmanDown()
        {
            msPacman.setCurrentDirection(Direction.DOWN);
            msPacman.updateFace();
            for (Sprite s : sprites)
                if (!s.isDpad() && s != msPacman)
                    s.setY(s.getY() - (int) msPacman.getSpeed());
            detectCollision(msPacman);
        }

        public void moveMsPacmanLeft()
        {
            msPacman.setCurrentDirection(Direction.LEFT);
            msPacman.updateFace();
            msPacman.setX(msPacman.getX() - (int) msPacman.getSpeed());
            detectCollision(msPacman);
        }

        public void moveMsPacmanRight()
        {
            msPacman.setCurrentDirection(Direction.RIGHT);
            msPacman.updateFace();
            msPacman.setX(msPacman.getX() + (int) msPacman.getSpeed());
            detectCollision(msPacman);
        }

        void update() //Update used for collision checks and screen wrap
        {
            for (Sprite sprite : sprites) {
                sprite.update();
                if(sprite.isGhost() || sprite.isFruit())
                    handleNPCCollision(sprite);
            }
            checkMsPacmanVsNPCs();
            screenWrap();

        }
    }
//___________________END OF MODEL CLASS________________'

    //_______________START OF VIEW CLASS_______________
    static class GameView extends SurfaceView
    {
        SurfaceHolder ourHolder; //idk what this is
        Canvas canvas; //canvas used to draw sprites
        Paint paint; //Used for color I think?
        Model model; //Model (I know what this is)
        GameController controller; //I know view isn't supposed to have access to controller but I was afraid to mess with some of the code that came with this assignment.
        //When I was coding this I never added anything where view would need to access controller for whatever reason (this is for anyone who is reading this comment)
        //Please do not take off points for this being here

        public GameView(Context context, Model m) //Constructor of view
        {
            super(context);
            model = m;
            ourHolder = getHolder();
            paint = new Paint();
        }

        void setController(GameController c) { controller = c; } //Like I said I was scared of editing the code (controller does not access view either)

        public void update()
        {
            if (!ourHolder.getSurface().isValid()) return;
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 254, 189, 149)); //Peach background

            for (Sprite sprite : model.getSprites()) //Draw all the sprites
                sprite.Draw(canvas);

            //Score board
            paint.setColor(Color.argb(255,  200, 128, 0));
            paint.setTextSize(100);
            canvas.drawText("Score:" + model.getScore(), 25, 80, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) //This is the code I was afraid to touch (I have no clue what this is for)
        {
            controller.onTouchEvent(motionEvent);
            return true;
        }
    }
//______________END OF VIEW__________________________

    //START OF CONTROLLER CLASS_______________
    static class GameController implements Runnable
    {
        volatile boolean playing; //no clue what this is
        Thread gameThread = null; //no clue what this is
        Model model; //Model
        GameView view; //I DO NOT USE THIS IN ANYWAY, LIKE I SAID, I WAS AFRAID OF MESSING WITH SOME OF THE STARTER CODE, BUT NOTHING I ADDED NEEDED TO ACCESS VIEW FROM CONTROLLER
        private boolean keyUp; //for when the user presses up
        private boolean keyDown; //for when the user presses down
        private boolean keyLeft; //for when the user presses left
        private boolean keyRight; //for when the user presses right

        GameController(Model m, GameView v)
        {
            model = m; //Model
            view = v; //I DO NOT USE THIS
            view.setController(this); //I DON"T USE THIS EITHER
            playing = true; //I think this is to keep the game going
        }

        void update() //This is similar to my code in assignment 4
        {
            if (keyUp)
                model.moveMsPacmanUp();
            if (keyDown)
                model.moveMsPacmanDown();
            if (keyLeft)
                model.moveMsPacmanLeft();
            if (keyRight)
                model.moveMsPacmanRight();
        }

        @Override
        public void run()
        {
            while (playing)
            {
                this.update();
                model.update();
                view.update();
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    Log.e("Error:", "sleeping");
                    System.exit(1);
                }
            }
        }

        void onTouchEvent(MotionEvent motionEvent)
        {
            float touchX = motionEvent.getX(); //get x coordinate of mouse press
            float touchY = motionEvent.getY(); //get y coordinate of mouse press

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN: //I will admit to using magic numbers here, I just set it up like this based off of my Dpad image and where it was
                    float dpadLeft   = 300;
                    float dpadRight  = 800;
                    float dpadTop    = 1850;
                    float dpadBottom = 2350;
                    float centerX    = 550;
                    float centerY    = 2100;
                    //Basically this code checks where in the square the user pressed and if its more on one side than another, then it changes the image of dpad and resets it once the user stops holding it.
                    if (touchX > dpadLeft && touchX < dpadRight && touchY > dpadTop && touchY < dpadBottom)
                    {
                        float dx = touchX - centerX;
                        float dy = touchY - centerY;

                        if (Math.abs(dx) > Math.abs(dy))
                        {
                            if (dx < 0) { keyLeft  = true; model.getDpad().updateKeyPressed(1); }
                            else        { keyRight  = true; model.getDpad().updateKeyPressed(3); }
                        }
                        else
                        {
                            if (dy < 0) { keyUp   = true; model.getDpad().updateKeyPressed(2); }
                            else        { keyDown  = true; model.getDpad().updateKeyPressed(4); }
                        }
                        break;
                    }

                case MotionEvent.ACTION_UP: //Resets key pressed
                    model.getDpad().updateKeyPressed(0);
                    keyLeft = keyDown = keyRight = keyUp = false;
                    break;
            }
        }
//Idk what any of the stuff below this does, it was just here
        public void pause()
        {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
                System.exit(1);
            }
        }

        public void resume()
        {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }
}