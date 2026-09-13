# CSCE 31903 Programming Paradigms
# Spring 2026
# Assignment 7 starter code

import pygame
import time
import json
import math

from pygame.locals import*
from time import sleep
from enum import Enum


class Sprite():
    def __init__(self, x, y, w, h, image):
        self.x = x
        self.y = y
        self.w = w
        self.h = h
        self.speed = 1
        self.valid = True
        self.image = pygame.image.load(image)
        # creates a bounding box around the image
        self.rect = pygame.Rect(x,y,w,h)

    def update(self):
        return self.valid

    def is_turtle(self):
        return False
    
    def is_rock(self):
        return False
    
    def is_lilypad(self):
        return False
    
    def is_msPacman(self):
        return False
    
    def is_tile(self):
        return False

    # for the starter code, we assume that all Sprites of a certain
    # type are the same size, and thus don't need w and h saved
    # However, it would be very easy to add more attributes to be 
    # saved here!
    # Notice that we are returning a Dictionary object here, 
    # thus the curly braces {"key1":value1, "key2":value2}
    def draw(self, screen):
        buh = 1
        

    def marshal(self):
        return {
            "x": self.x,
            "y": self.y
        }
    

class Direction(Enum):
    LEFT = 0
    UP = 1
    RIGHT = 2
    DOWN = 3


class Rock(Sprite):
    # variables that belong to the class, not to a specific
    # instance of the class - this is similar to Java's static variables
    ROCK_WIDTH = 50
    ROCK_HEIGHT = 50
    num_rocks = 0
    
    # this method belongs to the class itself and does not need
    # the 'self' object
    @staticmethod
    def reset_rocks():
        Rock.num_rocks = 0
    
    # constructor with default values - one of the ways you can
    # mimic Java's overloaded constructors
    # In this example, if w and h are provided, create the rock
    # as defined. If they are not provided, create a rock at 
    # half the regular size
    # ee is an easter egg for the drag-and-add Rock image
    # it has solely been added to tell the difference between
    # mouse motion and mouse click for the starter code
    def __init__(self, x, y, w=None, h=None, ee=None):
        if ee is not None:
            super().__init__(x, y, w, h, "images/rock2.png")
        elif w is None or h is None:
            super().__init__(x, y, Rock.ROCK_WIDTH/2, Rock.ROCK_HEIGHT/2, "images/rock.png")
        else:
            super().__init__(x, y, w, h, "images/rock.png")
        Rock.num_rocks += 1

    def is_rock(self):
        return True

class Turtle(Sprite):
    TURTLE_WIDTH = 80
    TURTLE_HEIGHT = 59
    
    def __init__(self, x, y):
        super().__init__(x, y, Turtle.TURTLE_WIDTH, Turtle.TURTLE_HEIGHT, "images/turtle.png")

    def is_turtle(self):
        return True

    def move(self, direction):
        if direction == "up":
            self.y -= self.speed
        if direction == "down":
            self.y += self.speed
        if direction == "left":
            self.x -= self.speed
        if direction == "right":
            self.x += self.speed

class MsPacman(Sprite):
    PACMAN_WIDTH = 24
    PACMAN_HEIGHT = 24
    #PACMAN_IMAGES = [[], [], [], []]

    def __init__(self, x, y):
        #img_counter = 1
        #for i in range(0, 4):
            #for j in range(0, 2):
                #self.PACMAN_IMAGES[i][j] = ("images/mspacman" + str(img_counter) + ".png")
        super().__init__(x, y, MsPacman.PACMAN_WIDTH, MsPacman.PACMAN_HEIGHT, "images/mspacman1.png")

class Model():
    filename = "map.json"
    
    def __init__(self):
        self.load_map()

    def load_map(self):
        # reset the rock count if we're loading (or reloading)
        # the map
        Rock.reset_rocks()
        
        self.sprites = []
        # example of adding a hardcoded rock
        self.sprites.append(Rock(200,100,Rock.ROCK_WIDTH, Rock.ROCK_HEIGHT))
        self.sprites.append(MsPacman(100, 100))
        # example of reading through the map.json file
        # and loading rocks and the turtle's location
        # open the json map and pull out the individual lists of sprite objects
        with open(Model.filename) as file:
            data = json.load(file)
            #get the lists . as "rocks" and "lilypads" from the map.json file
            rocks = data["rocks"]
            lilypads = data["lilypads"]
            #get turtle data out - these are individual
            #attributes, not a list
            turtle_x = data["turtlex"]
            turtle_y = data["turtley"]
        file.close()
        
        #create turtle using saved attributes
        self.turtle = Turtle(turtle_x, turtle_y)
        self.sprites.append(self.turtle)
        
        #for each entry inside the rocks list, pull the key:value pair out and create 
        #a new Rock object with (x,y,w,h)
        for entry in rocks:
            self.sprites.append(Rock(entry["x"], entry["y"], Rock.ROCK_WIDTH, Rock.ROCK_HEIGHT))

    def save_map(self):
        # create lists for each type of sprite you want to save
        rocks = []
        lilypads = []

        # go through all of the sprites, saving them into the 
        # appropriate lists
        for s in self.sprites:
            if s.is_rock():
                rocks.append(s.marshal())
            elif s.is_lilypad():
                lilypads.append(s.marshal())

        # create the dictionary of sprites, split by what types
        # they are - rocks and lilypads are lists, while 
        # turtlex and turtley are singular attributes
        map_to_save = {
            "rocks": rocks,
            "lilypads": lilypads,
            "turtlex": self.turtle.x,
            "turtley": self.turtle.y
        }

        # Save to file
        with open(Model.filename, "w") as f:
            json.dump(map_to_save, f)

    def update(self):
        for sprite in self.sprites:
            sprite.update()

    def clear_map(self):
        self.sprites.clear()
        self.sprites.append(self.turtle)
        # calling a static method - notice the lack of 'self'
        Rock.reset_rocks()

    # pos was passed as the mouse position tuple - 
    # pos[0] is x, pos[1] is y
    # ee is an easter egg to determine whether or not the mouse was clicked
    # or dragged - different images will appear for each action
    def add_rock(self, pos, ee=None):
        temp_sprite = pygame.Rect(pos[0], pos[1], Rock.ROCK_WIDTH, Rock.ROCK_HEIGHT)
        if self.is_this_space_empty(temp_sprite):
            self.sprites.append(Rock(pos[0], pos[1], Rock.ROCK_WIDTH, Rock.ROCK_HEIGHT, ee))

    def is_this_space_empty(self, other_sprite):
        for sprite in self.sprites:
            if other_sprite.colliderect(sprite.rect):
                return False
        return True
    
    


class View():
    def __init__(self, model):
        SCREEN_SIZE = (800,600)
        self.screen = pygame.display.set_mode(SCREEN_SIZE, 32)
        self.model = model

    def update(self):
        # change background color if the user is in edit_mode
        if Controller.edit_mode:
            self.screen.fill([146, 203, 146]) #light green
        else:
            self.screen.fill([72, 152, 72]) #dark forest green

        # draw sprites to the screen
        for sprite in self.model.sprites:
            LOCATION = (sprite.x, sprite.y)
            SIZE = (sprite.w, sprite.h)
            self.screen.blit(pygame.transform.scale(sprite.image, SIZE), LOCATION)
            # draw a bounding box around each image - good for debugging!
            # pygame.draw.rect(self.screen, (255, 0, 0), sprite.rect, 2)

        # add text to the screen
        # Default font, size 32
        font = pygame.font.SysFont(None, 32)   
        text_string = "There are " + str(Rock.num_rocks) + " rocks on the screen!"
        PURPLE_COLOR = (160, 32, 240)
        text_surface = font.render(text_string, True, PURPLE_COLOR)
        TEXT_LOCATION = (250, 10)
        self.screen.blit(text_surface, TEXT_LOCATION)
        
        # update display screen
        pygame.display.flip()

class Controller():
    edit_mode = False
    
    def __init__(self, model, view):
        self.model = model
        self.view = view
        self.keep_going = True

    def update(self):
        for event in pygame.event.get():
            if event.type == QUIT:
                self.keep_going = False
            elif event.type == KEYDOWN:
                if event.key == K_ESCAPE or event.key == K_q:
                    self.keep_going = False
            elif event.type == pygame.MOUSEBUTTONUP:
                if Controller.edit_mode:
                    # add a rock at the mouse's position using the "overloaded" constructor
                    self.model.add_rock(pygame.mouse.get_pos())
            elif event.type == pygame.MOUSEMOTION and event.buttons[0]:
                if Controller.edit_mode:
                    # add a continuous line of rocks - there's an extra parameter to tell 
                    # the difference between clicking and adding, and dragging and adding
                    # this will not be necessary for your homework code, but was for the example
                    # I wanted to share here
                    self.model.add_rock(pygame.mouse.get_pos(), "The Rock")
            elif event.type == pygame.KEYUP: #this is keyReleased!
                if event.key == K_c:
                    self.model.clear_map()
                    print("Map cleared and game reset")
                if event.key == K_e:
                    Controller.edit_mode = not Controller.edit_mode
                if event.key == K_l:
                    self.model.load_map()
                    print("Map loaded")
                if event.key == K_s:
                    self.model.save_map()
                    print("Map saved")
            elif event.type == pygame.MOUSEBUTTONDOWN:
                self.mouse_motion = True

        keys = pygame.key.get_pressed()
        # turtle's movement function changed to be closer related
        # to Link
        if keys[K_LEFT]:
            self.model.turtle.move("left")
        if keys[K_RIGHT]:
            self.model.turtle.move("right")
        if keys[K_UP]:
            self.model.turtle.move("up")
        if keys[K_DOWN]:
            self.model.turtle.move("down")

print("Use the arrow keys to move. Press Esc to quit.")
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