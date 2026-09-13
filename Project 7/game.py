# CSCE 31903 Programming Paradigms
# Spring 2026
# Name: Hunter Daily
# Date: 4/30/2026
# Assignment 7 starter code

import pygame
import time
import json
import math
import random #For Random movement
from enum import Enum #To handle Direction of all sprites
from pygame.locals import*
from time import sleep


class Direction(Enum): #I looked online and this looked to be the equivalent of my ENUM class
    LEFT = 0
    UP = 1
    RIGHT = 2
    DOWN = 3

class Sprite():
    def __init__(self, x, y, w, h): #Images were originally a parameter that was passed, but I wanted to use an array of images so that it would be easier to control what was being draw so I removed it.
        self.x = x
        self.y = y
        self.w = w
        self.h = h
        self.speed = 1
        self.valid = True
        
        # creates a bounding box around the image
        self.rect = pygame.Rect(x,y,w,h)
        self.facing = Direction.LEFT #Starts all sprites facing left

    def update(self):
        return self.valid

#________ALL CLASS CHECKS__________
    def is_MsPacman(self):
        return False
    
    def is_tile(self):
        return False
    
    def is_ghost(self):
        return False
    
    def is_fruit(self):
        return False
    
    def is_pellet(self):
        return False
    
    #Method for choosing random direction of sprites
    def choose_random_direction(self):
        pick = int(random.randint(0, 3))
        if (pick == 0):
            self.facing = Direction.LEFT
        if (pick == 1):
            self.facing = Direction.RIGHT
        if (pick == 2):
            self.facing = Direction.UP
        if (pick == 3):
            self.facing = Direction.DOWN
    
    #Draw method
    def draw(self, screen):
        location = (self.x, self.y)
        size = (self.w, self.h)
        screen.blit(pygame.transform.scale(self.image, size), location)

    #Marshal method
    def marshal(self):
        return {
            "x": self.x,
            "y": self.y
        }

#____________START OF TILE CLASS____________
class Tile(Sprite):
    TILE_WIDTH = 30 #Width is 30
    TILE_HEIGHT = 30 #Height is 30
    TILE_IMAGE = pygame.image.load("images/tile2.png") #Pellet and tile are loaded here so that we only have to load one tile and pellet image for the whole thing

    def __init__(self, x, y):
        super().__init__(x, y, self.TILE_WIDTH, self.TILE_HEIGHT)

    def is_tile(self): #Is this is a tile
        return True
    
    def draw(self, screen): #Draw method for the screen
        location = (self.x, self.y) #Location tuple
        size = (self.w, self.h) #Size tuple
        screen.blit(pygame.transform.scale(self.TILE_IMAGE, size), location) #Draws tile to the screen
#____________END OF TILE CLASS_____________

#_____________START OF MS PACMAN CLASS____________
class MsPacman(Sprite):
    PACMAN_WIDTH = 24 #width is 24
    PACMAN_HEIGHT = 24 #heigh is 24
    PACMAN_DIRECTION = 0 #Direction is left
    PACMAN_CURRENT_FRAME = 0 #What part of her mouth is open
    PACMAN_IMAGES = [] #array for holding images
    PACMAN_SPEED = 5 #her speed is 5

    def __init__(self, x, y):
        self.PACMAN_IMAGES = [[] for i in range(4)] #Fills the array with more arrays for imagse to be appended to it.
        img_counter = 1

        for i in range(4):
            for j in range(3):
                self.PACMAN_IMAGES[i].append(f"images/mspacman{img_counter}.png")
                img_counter += 1
            
        super().__init__(x, y, self.PACMAN_WIDTH, self.PACMAN_HEIGHT)

    def is_MsPacman(self): #Is this Mrs Pacman
        return True
    
    def updateFace(self): #Called whenever the player is moving, self.facing.value is the enum value of something like left, so if left then PACMAN_DIRECTION will be equal to 0
        self.PACMAN_CURRENT_FRAME = (self.PACMAN_CURRENT_FRAME + 1) % 3
        if (self.facing == Direction.LEFT):
            self.PACMAN_DIRECTION = self.facing.value
        elif (self.facing == Direction.UP):
            self.PACMAN_DIRECTION = self.facing.value
        elif (self.facing == Direction.RIGHT):
            self.PACMAN_DIRECTION = self.facing.value
        elif (self.facing == Direction.DOWN):
            self.PACMAN_DIRECTION = self.facing.value
    
    def update(self): #Update
        return True
    
    def draw(self, screen): #Draw function
        location = (self.x, self.y) #location tuple
        size = (self.w, self.h) #size tuple
        image = pygame.image.load(self.PACMAN_IMAGES[self.PACMAN_DIRECTION][self.PACMAN_CURRENT_FRAME]) #image load
        screen.blit(pygame.transform.scale(image, size), location) #Draw to the screen
#______________END OF MRS PACMAN CLASS_______________

#______________START OF FRUIT CLASS_______________
class Fruit(Sprite):
    FRUIT_WIDTH = 24 #Fruit width
    FRUIT_HEIGHT = 24 #Fruit height
    DISPLAY_STATUS = True #Should we draw this
    FRUIT_SPEED = 1 #Fruit speed is 1 (I like them slow and easy to catch)
    DISPLAY_TIMER = 0 #For whenever the fruit respawns
    CURRENT_FRAME = 0 #Cycle through the fruit array
    FRUIT_IMAGES = [] #Array for holding fruit images

    def __init__(self, x, y): #initalize
        img_counter = 1
        for i in range(7):
            self.FRUIT_IMAGES.append(f"images/fruit{img_counter}.png")
            img_counter += 1
        super().__init__(x, y, self.FRUIT_WIDTH, self.FRUIT_HEIGHT)

    def is_fruit(self): #is this a fruit?
        return True
    
    def update_display_status(self): #For whenever this gets eaten
        self.CURRENT_FRAME = (self.CURRENT_FRAME + 1) % len(self.FRUIT_IMAGES)
        self.DISPLAY_STATUS = not(self.DISPLAY_STATUS)
    
    def update(self):
        if not(self.DISPLAY_STATUS): #If display_status is false, run through a timer to respawn it.
            self.DISPLAY_TIMER += 1
            if (self.DISPLAY_TIMER >= 300):
                self.DISPLAY_STATUS = True
                self.DISPLAY_TIMER = 0
            return False
        if (self.facing == Direction.LEFT): #If display_status = true, move fruiit
            self.x = self.x - self.FRUIT_SPEED
        elif (self.facing == Direction.RIGHT):
            self.x = self.x + self.FRUIT_SPEED
        elif (self.facing == Direction.UP):
            self.y = self.y - self.FRUIT_SPEED
        elif (self.facing == Direction.DOWN):
            self.y = self.y + self.FRUIT_SPEED
    
    def draw(self, screen): #Draw functoin
        location = (self.x, self.y) #location tuple
        size = (self.w, self.h) #size tuple
        if (self.DISPLAY_STATUS): #draw this if display_status is true
            image = pygame.image.load(self.FRUIT_IMAGES[self.CURRENT_FRAME]) #image loaded for fruit
            screen.blit(pygame.transform.scale(image, size), location) #Draw fruit
#____________END OF FRUIT CLASS_____________

#______________START OF GHOST CLASS_____________
class Ghost(Sprite):
    GHOST_WIDTH = 24 #Ghost width is 24
    GHOST_HEIGHT = 24 #Ghost height is 24
    DISPLAY_STATUS = True #Should we display this?
    IS_GHOST_DEAD = False #Is this ghost dead?
    GHOST_SPEED = 1 #Ghost is slow like fruit
    MOVE_TIMER = 0 #Timer for death animation
    GHOST_FACE = 0 #which way its looking
    CURRENT_FRAME = 0 #animation
    GHOST_DEATH_FRAME = 0 #Death frame animation
    GHOST_IMAGES = [] #Array used to hold ghost images
    DEATH_IMAGES = [] #Array used to hold death images

    def __init__(self, x, y):
        self.GHOST_IMAGES = [[] for i in range(4)] #Similar to pacman, fill array with empty arrays to that will images appended to eventually
        img_counter = 1
        for i in range(4):
            for j in range(2):
                self.GHOST_IMAGES[i].append(f"images/blinky{img_counter}.png")
                img_counter += 1

        img_counter = 1
        for i in range(5):
            self.DEATH_IMAGES.append(f"images/ghost{img_counter}.png")
            img_counter += 1

        super().__init__(x, y, self.GHOST_WIDTH, self.GHOST_HEIGHT)

    def is_ghost(self): #Is this a ghost?
        return True
    
    def update_face(self): #Similar to fruits update
        self.CURRENT_FRAME = (self.CURRENT_FRAME + 1) % 2 #Animate ghost
        if (self.facing == Direction.LEFT): #Used to move ghost where its looking
            self.GHOST_FACE = 0
        elif (self.facing == Direction.RIGHT):
            self.GHOST_FACE = 2
        elif (self.facing == Direction.UP):
            self.GHOST_FACE = 1
        elif (self.facing == Direction.DOWN):
            self.GHOST_FACE = 3
    
    def update(self): 
        if (self.DISPLAY_STATUS and not(self.IS_GHOST_DEAD)): #Ghost movement for whenever display status is true and is dead is false
            if (self.facing == Direction.LEFT):
                self.x = self.x - self.GHOST_SPEED
                self.update_face()
            elif (self.facing == Direction.RIGHT):
                self.x = self.x + self.GHOST_SPEED
                self.update_face()
            elif (self.facing == Direction.UP):
                self.y = self.y - self.GHOST_SPEED
                self.update_face()
            elif (self.facing == Direction.DOWN):
                self.y = self.y + self.GHOST_SPEED
                self.update_face()

        if (self.IS_GHOST_DEAD): #If is dead is true, then we run this animation where ghost dies then eventually respawns
            self.DISPLAY_STATUS = False
            self.MOVE_TIMER += 1
            if (self.MOVE_TIMER >= 150):
                self.MOVE_TIMER = 0
                self.IS_GHOST_DEAD = False
                self.DISPLAY_STATUS = True
                self.GHOST_DEATH_FRAME = 0
            elif (self.MOVE_TIMER >= 100):
                self.GHOST_DEATH_FRAME = 4
            elif (self.MOVE_TIMER >= 50):
                self.GHOST_DEATH_FRAME = 3
        return True
    
    def draw(self, screen): #Draw Method for ghost
        location = (self.x, self.y) #location tuple
        size = (self.w, self.h) #Location size
        if (self.DISPLAY_STATUS): #Draw the living ghost if DISPLAY_STATUS is true
            image = pygame.image.load(self.GHOST_IMAGES[self.GHOST_FACE][self.CURRENT_FRAME])
            screen.blit(pygame.transform.scale(image, size), location)
        if (self.IS_GHOST_DEAD): #Otherwise start iterating through the dead ghost animation
            image = pygame.image.load(self.DEATH_IMAGES[self.GHOST_DEATH_FRAME])
            screen.blit(pygame.transform.scale(image, size), location)
#_________________END OF GHOST CLASS________________    

#________________START OF PELLET CLASS_______________
class Pellet(Sprite): #Pellets should be small in order to to not take up as much as space on the screen
    PELLET_WIDTH = 10 #Pellet width
    PELLET_HEIGHT = 10 #Pellet height
    PELLET_IMAGE = pygame.image.load("images/pellet.png") #Load image here to reduce lag on the game

    def __init__(self, x, y):
        super().__init__(x, y, self.PELLET_WIDTH, self.PELLET_HEIGHT)
        self.eaten = False #This hides the pellet if its been in contact with Mrs Pacman
    
    def is_pellet(self): #Is this is a pellet
        return True
    
    def update(self): #Update
        return True
    
    def draw(self, screen): #Draw to the screen
        if not self.eaten: #Draw to the screen if the pellet has not been eaten
            location = (self.x, self.y) #location tuple
            size = (self.w, self.h) #size tuple
            screen.blit(pygame.transform.scale(self.PELLET_IMAGE, size), location) #Draw to the screen
#_____________END OF PELLET CLASS____________

#__________START OF MODEL CLASS___________
class Model():
    filename = "map.json"
    scrollPos = 0 #Used for whenever mrs pacman moves up or down
    items_I_can_add = [] #Map editor list of objects
    currentMapEditor = 0 #Used to cycle between objects in items_I_Can_add
    score = 0 #Score for points, pellets = 1, fruits = 3, ghosts = 5
    
    def __init__(self):
        self.load_map() #load map

#LOAD AND SAVE METHODS
    def load_map(self):
        self.sprites = [] #sprites array to hold sprites
        self.items_I_can_add = [] #I had this here so it resets everytime we reload
        self.currentMapEditor = 0 #Always start on tile
        self.score = 0 #Edit this if you want a billion points to start I guess
        
        with open(Model.filename) as file: #Opens up the json file to be read, .get is used to avoid an error if that object happens to not be in the json file
            data = json.load(file) #loads json file as an array into data
            tiles = data.get("tiles", []) #Gets tiles as an array
            fruits = data.get("fruits", []) #Gets fruits as an array
            ghosts = data.get("ghosts", []) #Gets ghosts as an arry
            pellets = data.get("pellets", []) #Gets pellets as an array
            mspacmanx = data.get("mspacmanx", 375.0) #Mrs Pacman x coordinate, the second parameter is just the default variabe if nothing is there
            mspacmany = data.get("mspacmany", 300.0) #Mrs Pacman y coordinate, the second parameter is just the default variabe if nothing is there
            self.scrollPos = data.get("scrollPos", 0) #Save scroll position to make it easier how to edit tiles, the second parameter is just the default variabe if nothing is there
        file.close() #close file, we can no longer acces json
        
        self.mspacman = MsPacman(mspacmanx, mspacmany) #Make a Mrs Pacman object
        self.sprites.append(self.mspacman) #Add mrspacman to sprites
        
        #For each entry in tiles, fruits, ghosts, and pellets. Add that as a sprite in the sprites array
        for entry in tiles:
            self.sprites.append(Tile(entry["x"], entry["y"]))
        for entry in fruits:
            self.sprites.append(Fruit(entry["x"], entry["y"]))
        for entry in ghosts:
            self.sprites.append(Ghost(entry["x"], entry["y"]))
        for entry in pellets:
            self.sprites.append(Pellet(entry["x"], entry["y"]))

        #Initalize all editable objects (doesn't matter what the x or y - coords are)
        #Then add it to items_I_can_add array.
        self.tile = Tile(200, 200)
        self.fruit = Fruit(200, 200)
        self.ghost = Ghost(200, 200)
        self.pellet = Pellet(200, 200)
        self.items_I_can_add.append(self.tile)
        self.items_I_can_add.append(self.fruit)
        self.items_I_can_add.append(self.ghost)
        self.items_I_can_add.append(self.pellet)

    def save_map(self): #Save map
        # create lists for each type of sprite you want to save
        tiles = []
        fruits = []
        ghosts = []
        pellets = []

        # go through all of the sprites, saving them into the 
        # appropriate lists
        for s in self.sprites:
            if s.is_tile():
                tiles.append(s.marshal())
            elif s.is_fruit():
                fruits.append(s.marshal())
            elif s.is_ghost():
                ghosts.append(s.marshal())
            elif s.is_pellet():
                pellets.append(s.marshal())

        # create the dictionary of sprites, split by what types
        # they are - tiles, fruits, ghosts, and pellets are lists, while 
        # mspacmanx, mspacmany, and scrollpos are singular attributes
        map_to_save = {
            "tiles": tiles,
            "fruits": fruits,
            "ghosts": ghosts,
            "pellets": pellets,
            "mspacmanx": self.mspacman.x,
            "mspacmany": self.mspacman.y,
            "scrollPos": self.scrollPos
        }

        with open(Model.filename, "w") as f: #Load data into json file
            json.dump(map_to_save, f)

#ALL COLLISION METHODS
    def screen_wrap(self): #Screen wrap makes it to where Sprites wrap around to the other side if they go outside the screen
        SCREEN_WIDTH = 750
        for s in self.sprites:
            if not (s.is_fruit() or s.is_ghost() or s.is_MsPacman()):
                continue
            if s.x + s.w < 0:
                s.x = SCREEN_WIDTH
            elif s.x > SCREEN_WIDTH:
                s.x = -s.w

    def spritesOverlap(self, a, b): #Method to check if any of the sprites are overlapping
        return a.x < b.x + b.w and a.x + a.w > b.x and a.y < b.y + b.h and a.y + a.h > b.y

    #If Ms Pacman is facing a certain direction and overlapping a tile, push her back by her speed in the opposite direction
    def detectCollision(self, s): #Mrs Pacman vs Tiles
        for t in self.sprites:
            if (t.is_tile() and self.spritesOverlap(s, t)):
                if s.facing == Direction.LEFT:
                    s.x = (t.x + t.w)
                elif s.facing == Direction.RIGHT:
                    s.x = (t.x - s.w)
                elif s.facing == Direction.UP:
                    for o in self.sprites: #Each sprite stops moving in order to show that Mrs pacman is hitting a tile
                        if (o != self.mspacman):
                            o.y = o.y - int(self.mspacman.PACMAN_SPEED)
                    self.scrollPos -= self.mspacman.PACMAN_SPEED  #undos the scrollPos
                elif s.facing == Direction.DOWN:
                    for o in self.sprites: #Each sprites stops moving in order to show that Mrs Pacman is hitting a tile
                        if (o != self.mspacman):
                            o.y = o.y + int(self.mspacman.PACMAN_SPEED)
                    self.scrollPos += self.mspacman.PACMAN_SPEED  #undos the scrollPos
                            

    def checkMsPacmanVsNPC(self): #Mrs Pacman vs NPCS
        for s in self.sprites:
            if (s.is_ghost()): #Ghost
                if (s.DISPLAY_STATUS and not(s.IS_GHOST_DEAD) and self.spritesOverlap(self.mspacman, s)):
                    s.IS_GHOST_DEAD = True #Starts death animation
                    self.score += 5 #Score
            elif (s.is_fruit()): #Fruit
                if (s.DISPLAY_STATUS and self.spritesOverlap(self.mspacman, s)):
                    s.update_display_status() #sets display_status to false
                    self.score += 3 #Score

            elif (s.is_pellet()): #Pellet
                if not s.eaten and self.spritesOverlap(self.mspacman, s):
                    s.eaten = True #Hides pellet to never be shown again
                    self.score += 0.25 #I made it 0.25 because i was getting four points for whenever Mrs pacman would eat (I still don't know why)
                
    def handleNPCCollision(self, s): #NPCs vs Tiles (Only NPCS that should be accessing this array is Ghost and Fruit)
        for tile in self.sprites:
            if (tile.is_tile() and self.spritesOverlap(s, tile)):
                if (s.facing == Direction.LEFT):
                    s.x = tile.x + tile.w
                elif (s.facing == Direction.RIGHT):
                    s.x = tile.x - s.w
                elif (s.facing == Direction.UP):
                    s.y = tile.y + tile.h
                elif (s.facing == Direction.DOWN):
                    s.y = tile.y - s.h
                s.choose_random_direction() #Make the move a random direction if they are indeed overlapping a tile.
                return

    def moveMsPacmanUp(self):
        self.mspacman.facing = Direction.UP #Change Mrs Pacman to face up
        self.mspacman.updateFace() #Update face and Change mouth position
        for s in self.sprites: #Move all other sprites down to give off the illusion that Mrs Pacman is going up
            if not s.is_MsPacman():
                s.y += int(self.mspacman.PACMAN_SPEED) 
        self.scrollPos += self.mspacman.PACMAN_SPEED #Change scroll pos based on how off center Mrs Pacman is originally
        self.detectCollision(self.mspacman) #Detect Collision

    def moveMsPacmanDown(self):
        self.mspacman.facing = Direction.DOWN #Change Mrs Pacman to face own
        self.mspacman.updateFace() #Update face and change mouth position
        for s in self.sprites: #Move all other sprites up to give off the illusion that Mrs Pacman is going down
            if not s.is_MsPacman():
                s.y -= int(self.mspacman.PACMAN_SPEED)
        self.scrollPos -= self.mspacman.PACMAN_SPEED #Change scroll pos based on how off center Mrs Pacman is originally
        self.detectCollision(self.mspacman) #Detect Collision

    def moveMsPacmanRight(self):
        self.mspacman.facing = Direction.RIGHT #Change Mrs Pacman to face Right
        self.mspacman.updateFace() #Update direction and change mouth position
        self.mspacman.x = (self.mspacman.x + int(self.mspacman.PACMAN_SPEED)) #Changes Mrs Pacman's x position
        self.detectCollision(self.mspacman) #Detect Collision

    def moveMsPacmanLeft(self):
        self.mspacman.facing = Direction.LEFT #Change Mrs Pacman to face Left
        self.mspacman.updateFace() #Update direction and change mouth position
        self.mspacman.x = (self.mspacman.x - int(self.mspacman.PACMAN_SPEED)) #Changes Mrs Pacman's x position
        self.detectCollision(self.mspacman) #Detect Collision


    def update(self): #Do all the neccesary checks to set the rules of the game
        for sprite in self.sprites:
            sprite.update()
            if (sprite.is_ghost() or sprite.is_fruit()):
                self.handleNPCCollision(sprite)
        self.checkMsPacmanVsNPC()
        self.screen_wrap()

#ALL EDITOR METHODS
    def switch_current_editor(self): #Switch what item were editing
        self.currentMapEditor = (self.currentMapEditor + 1) % len(self.items_I_can_add)

    def clear_map(self): #Clears the sprite array
        self.sprites = [sprite for sprite in self.sprites if sprite.is_MsPacman()] #Flexing my python knowledge

    def snapToGrid(self, screenX, screenY): #mouse is in screen space, convert to world space
        for s in self.sprites:
            if s.is_tile():
                # use an existing tile's screen position as a basis for other tiles positions
                offsetX = s.x % Tile.TILE_WIDTH #Figures out offset of x coordinates for loaded tiles
                offsetY = s.y % Tile.TILE_HEIGHT #Figures out offset of y coordinates for loaded tiles
                snappedX = math.floor((screenX - offsetX) / Tile.TILE_WIDTH) * Tile.TILE_WIDTH + offsetX
                snappedY = math.floor((screenY - offsetY) / Tile.TILE_HEIGHT) * Tile.TILE_HEIGHT + offsetY
                return snappedX, snappedY #Returns a tuple of snappedX, and snappedY
        
        # no tiles exist yet, snap to nearest 30px
        snappedX = math.floor(screenX / Tile.TILE_WIDTH) * Tile.TILE_WIDTH
        snappedY = math.floor(screenY / Tile.TILE_HEIGHT) * Tile.TILE_HEIGHT
        return snappedX, snappedY

    #ALL ADD COMMANDS WILL SNAP TO AN X AND Y COORDINATE BASED OFF OF LOADED TILES FROM a JSON
    def addTile(self, pos): #Add tile to the sprites array
        snappedX, snappedY = self.snapToGrid(pos[0], pos[1])
        temp_sprite = pygame.Rect(snappedX, snappedY, Tile.TILE_WIDTH, Tile.TILE_HEIGHT)
        if self.is_this_space_empty(temp_sprite):
            self.sprites.append(Tile(snappedX, snappedY))

    def addGhost(self, pos): #Add ghost to the sprites array
        snappedX, snappedY = self.snapToGrid(pos[0], pos[1])
        temp_sprite = pygame.Rect(snappedX, snappedY, Ghost.GHOST_WIDTH, Ghost.GHOST_HEIGHT)
        if self.is_this_space_empty(temp_sprite):
            self.sprites.append(Ghost(snappedX, snappedY))

    def addFruit(self, pos): #Add fruit to the sprites array
        snappedX, snappedY = self.snapToGrid(pos[0], pos[1])
        temp_sprite = pygame.Rect(snappedX, snappedY, Fruit.FRUIT_WIDTH, Fruit.FRUIT_HEIGHT)
        if self.is_this_space_empty(temp_sprite):
            self.sprites.append(Fruit(snappedX, snappedY))

    def addPellet(self, pos): #Add pellet to the sprites array
        snappedX, snappedY = self.snapToGrid(pos[0], pos[1])
        temp_sprite = pygame.Rect(snappedX, snappedY, Pellet.PELLET_WIDTH, Pellet.PELLET_HEIGHT)
        if self.is_this_space_empty(temp_sprite):
            self.sprites.append(Pellet(snappedX + 10, snappedY + 10)) #Add 10 to x and y coordinate because pellets are a lot smaller than the other sprites
            #This is to make it basically in the middle of a tile space, other wise it would have just been in the top left of the tile space

    def is_this_space_empty(self, other_sprite):
        for sprite in self.sprites:
            if sprite.is_pellet() and sprite.eaten:  #Ignores any hidden pellets that are still hidden in the sprites array
                continue
            current_rect = pygame.Rect(sprite.x, sprite.y, sprite.w, sprite.h)
            if other_sprite.colliderect(current_rect):
                return False
        return True
#______________END OF MODEL CLASS________________

#_______________START OF VIEW CLASS_________________
class View():
    tile_editor_image = "images/tile2.png" #Tile image we want to load for editor
    fruit_editor_image = "images/fruit1.png" #Fruit image we want to load for editor
    blinky1_editor_image = "images/blinky1.png" #Ghost image we want to load for editor
    pellet_editor_image = "images/pellet.png" #pellet image we want to load for editor
    def __init__(self, model):
        SCREEN_SIZE = (750,600) #Had to change screen x coordinate to 750 to adjust to my tile sizes
        self.screen = pygame.display.set_mode(SCREEN_SIZE, 32)
        self.model = model

    def update(self):
        # change background color if the user is in edit_mode
        if Controller.edit_mode: #Change background if edit mode is on
            self.screen.fill([255, 229, 180]) #light peach
        else:
            self.screen.fill([254, 189, 149]) #darker peach

        # draw sprites to the screen
        for sprite in self.model.sprites:
            sprite.draw(self.screen)

        if Controller.edit_mode: #If Edit mode is on do the following
            pygame.draw.rect(self.screen, (34, 139, 34), (50, 50, 50, 50)) #Draws a green square in the top left
            sprite = self.model.items_I_can_add[self.model.currentMapEditor] #Puts the sprite being added in the middle of green square
            location = (63, 63) #Location tuple
            size = (25, 25) #size tuple
            if sprite.is_tile(): #Draw tile top left
                image = pygame.image.load(self.tile_editor_image)
            elif sprite.is_fruit(): #Draw fruit top left
                image = pygame.image.load(self.fruit_editor_image)
            elif sprite.is_ghost(): #Draw ghost top left
                image = pygame.image.load(self.blinky1_editor_image)
            elif sprite.is_pellet(): #Draw pellet top left
                image = pygame.image.load(self.pellet_editor_image)
            self.screen.blit(pygame.transform.scale(image, size), location) #Actually draw ima ge

        #I got this pygame text code online and just edited it
        font = pygame.font.SysFont('Arial', 32) #Font for score
        score_sentence = (f"Score:  {int(self.model.score)}!") #Score sentence
        text_surface = font.render(score_sentence, True, (255, 255, 255)) #text_surace
        self.screen.blit(text_surface, (600, 50)) #Actually draw score

        pygame.display.flip() #Not sure what this does
#________________END OF VIEW CLASS_____________

#_________________START OF CONTROLLER CLASS________________
class Controller():
    edit_mode = False #Edit mode
    key_left = False #Is player pressing left key
    key_up = False #Is player pressing up key
    key_right = False #Is player pressing right key
    key_down = False #Is player pressing down key
    mouse_motion = False #mouse motion
    
    
    def __init__(self, model, view):
        self.model = model
        self.view = view
        self.keep_going = True

    def update(self):
        for event in pygame.event.get():
            if event.type == QUIT: #Quit the game if this happens
                self.keep_going = False

            elif event.type == KEYDOWN: 
                if event.key == K_UP: #GO UP
                    self.key_up = True
                elif event.key == K_DOWN: #GO DOWN
                    self.key_down = True
                elif event.key == K_LEFT: #GO LEFT
                    self.key_left = True
                elif event.key == K_RIGHT: #GO RIGHT
                    self.key_right = True

            elif event.type == KEYUP:
                if event.key == K_UP: #STOP GONIG UP
                    self.key_up = False
                elif event.key == K_DOWN: #STOP GOING DOWN
                    self.key_down = False
                elif event.key == K_LEFT: #STOP GOING FET
                    self.key_left = False
                elif event.key == K_RIGHT: #STOP GOING RIGHT
                    self.key_right = False
                elif event.key == K_ESCAPE or event.key == K_q: #Quit the game
                    self.keep_going = False
                elif event.key == K_c: #Clear the sprites array
                    if Controller.edit_mode:
                        self.model.clear_map()
                        print("Map Cleared!")
                elif event.key == K_e: #Enter edit mode
                    Controller.edit_mode = not Controller.edit_mode
                    print("Edit mode enabled")
                elif event.key == K_l: #load map
                    self.model.load_map()
                    print("Map Loaded!")
                elif event.key == K_s: #Save map
                    self.model.save_map()
                    print("Map Saved!")

            elif event.type == pygame.MOUSEMOTION: #Mouse dragged method
                mouse_pos = pygame.mouse.get_pos()
                # Access individual coordinates
                x = mouse_pos[0] #Get x and y coordinates
                y = mouse_pos[1] 
                
                if (Controller.edit_mode and event.buttons[0]) and not(x > 50 and x < 100 and y > 50 and y < 100): #basically check to make sure that the mouse motion isn't in the edit boxed, we don't want to add sprites in that case
                    if self.model.items_I_can_add[self.model.currentMapEditor].is_tile():
                        self.model.addTile(pygame.mouse.get_pos()) #Add tile
                    elif self.model.items_I_can_add[self.model.currentMapEditor].is_fruit():
                        self.model.addFruit(pygame.mouse.get_pos()) #Add fruit
                    elif self.model.items_I_can_add[self.model.currentMapEditor].is_ghost():
                        self.model.addGhost(pygame.mouse.get_pos()) #Add ghost
                    elif self.model.items_I_can_add[self.model.currentMapEditor].is_pellet():
                        self.model.addPellet(pygame.mouse.get_pos()) #Add pellet

            elif event.type == pygame.MOUSEBUTTONDOWN:
                mouse_pos = pygame.mouse.get_pos()
                # Access individual coordinates
                x = mouse_pos[0]
                y = mouse_pos[1]
                if (x > 50 and x < 100 and y > 50 and y < 100): #This is the edit box in the top left basically
                    self.model.switch_current_editor()
                elif Controller.edit_mode:
                    if self.model.items_I_can_add[self.model.currentMapEditor].is_tile():
                        self.model.addTile(pygame.mouse.get_pos()) #Add tile
                    elif self.model.items_I_can_add[self.model.currentMapEditor].is_fruit():
                        self.model.addFruit(pygame.mouse.get_pos()) #Add fruit
                    elif self.model.items_I_can_add[self.model.currentMapEditor].is_ghost():
                        self.model.addGhost(pygame.mouse.get_pos()) #Add ghost
                    elif self.model.items_I_can_add[self.model.currentMapEditor].is_pellet():
                        self.model.addPellet(pygame.mouse.get_pos()) #Add pellet

        if self.key_left:
            self.model.moveMsPacmanLeft() #Move mrs pacman left
        if self.key_right:
            self.model.moveMsPacmanRight() #Move mrs pacman right
        if self.key_up:
            self.model.moveMsPacmanUp() #Move mrs pacman up
        if self.key_down:
            self.model.moveMsPacmanDown() #Move Mrs pacman down
#________________END OF CONTROLLER CLASS_________________

#Print statements detailing how to use the editor and the game
print("Use the arrow keys to move. Press Esc or q to quit.")
print("Press e to enable edit mode, afterwards use c to clear")
print("Press l to load map, and s to save map")
pygame.init()
pygame.font.init()
m = Model()
v = View(m)
c = Controller(m, v)
while c.keep_going:
    c.update()
    m.update()
    v.update()
    sleep(0.04)
print("Goodbye!")