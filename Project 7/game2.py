# CSCE 31903 Programming Paradigms
# Spring 2026
# Assignment 7 starter code
# Name: Peter Pham
# Date: 4/30/26
# Description: This final project represents a Ms. Pacman-like video game where the player controls
# Ms. Pacman, travels around a maze, and eats various things for points. This project focuses on
# using Python, putting together most previous functionality, and adding edible pellets.

import pygame
import time
import json
import math
import random # imported so I can randomize directions for Ghost and Fruit

from pygame.locals import*
from time import sleep

class Sprite():
    # generic initialization for Sprites
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

    # generic update function, used to verify if something should still be in the game
    def update(self):
        return self.valid
    
    # generic draw function, passes in view to draw since we are calling this in view
    def draw(self, scrollpos, view):
        LOCATION = (self.x, self.y - scrollpos) # graphics will shift with scroll position, but real time y-values do not
        SIZE = (self.w, self.h)
        view.screen.blit(pygame.transform.scale(self.image, SIZE), LOCATION)
        self.rect = pygame.Rect(self.x, self.y - scrollpos, self.w, self.h) # update bounding box
    
    # generic type checkers
    def is_mspacman(self):
        return False
    
    def is_tile(self):
        return False
    
    def is_fruit(self):
        return False
    
    def is_ghost(self):
        return False
    
    def is_pellet(self):
        return False
    
    # shortcuts for measurements
    def get_right(self):
        return (self.x + self.w)
    
    def get_left(self):
        return (self.x)

    def get_top(self):
        return (self.y)
    
    def get_bottom(self):
        return (self.y + self.h)

    # from starter code
    # def is_turtle(self):
    #     return False
    
    # def is_rock(self):
    #     return False
    
    # def is_lilypad(self):
    #     return False

    # for the starter code, we assume that all Sprites of a certain
    # type are the same size, and thus don't need w and h saved
    # However, it would be very easy to add more attributes to be 
    # saved here!
    # Notice that we are returning a Dictionary object here, 
    # thus the curly braces {"key1":value1, "key2":value2}
    def marshal(self):
        return {
            "x": self.x, # I kept the marshalling as it is since my original JSON only included x's and y's
            "y": self.y
        }
    
class MsPacman(Sprite):
    score = 0 # MsPacman class will store the player score, which is output to the screen
    
    # add to the player score via a static method to a static value
    @staticmethod
    def add_score(val):
        MsPacman.score += val
    
    # reset the player score, assumed to be done when loading in a map for my project design
    @staticmethod
    def reset_score():
        MsPacman.score = 0

    # initialization of MsPacman adds a list of images to load in, animation frames, and movement direction
    def __init__(self, x, y):
        super().__init__(x, y, 30, 30, "images/mspacman1.png")
        self.px = 0
        self.py = 0
        self.speed = 5
        self.image_list = [[""]*3,[""]*3,[""]*3,[""]*3] # initialize to a bunch of empty strings as placeholders
        self.frame = 0
        self.direction = 0
        self.load_images()
    
    # attempt to load in an array of images to reference if not already done
    def load_images(self):
        for i in range(0, 4):
            for j in range(0, 3):
                if (self.image_list[i][j] == ""): # if there is a placeholder, fill in the proper image
                    self.image_list[i][j] = pygame.image.load(f"images/mspacman{3*i+j+1}.png")

    # move when prompted by user, cycle animation frames and change based on direction
    def move(self, direction):
        self.px = self.x # record previous coordinates to help with collision fixing
        self.py = self.y

        self.frame = (self.frame + 1) % 3 # cycle frame
        self.image = self.image_list[self.direction][self.frame]

        # move based on input direction from Controller
        if direction == "up":
            self.y -= self.speed
            self.direction = 1
        if direction == "down":
            self.y += self.speed
            self.direction = 3
        if direction == "left":
            self.x -= self.speed
            self.direction = 0
            if (self.x <= 0):
                self.x = 600 - self.w # warp around
        if direction == "right":
            self.x += self.speed
            self.direction = 2
            if (self.x >= 600 - self.w):
                self.x = 0 # warp around

    # unique MsPacman draw function, does not use passed in scrollpos since MsPacman is always vertically locked
    def draw(self, scrollpos, view):
        LOCATION = (self.x, 325)
        SIZE = (self.w, self.h)
        view.screen.blit(pygame.transform.scale(self.image, SIZE), LOCATION)
        self.rect = pygame.Rect(self.x, 325, self.w, self.h) # update bounding box

    # get MsPacman out of a tile, very basic collision fix
    def get_out_of_tile(self, tile):
        if ((self.get_right() >= tile.get_left()) and (self.px + self.w <= tile.get_left())):
            self.x = tile.get_left() - self.w - 1
        if ((self.get_left() <= tile.get_right()) and (self.px >= tile.get_right())):
            self.x = tile.get_right() + 1
        if ((self.get_top() <= tile.get_bottom()) and (self.py >= tile.get_bottom())):
            self.y = tile.get_bottom() + 1
        if ((self.get_bottom() >= tile.get_top()) and (self.py + self.h <= tile.get_top())):
            self.y = tile.get_top() - self.h - 1

    # this is a MsPacman
    def is_mspacman(self):
        return True

class Tile(Sprite):
    # initialize a very basic, stagnant tile
    def __init__(self, x, y, w, h):
        super().__init__(x, y, w, h, "images/tile2.png")
    
    # this is a tile
    def is_tile(self):
        return True

class Fruit(Sprite):
    # initialize Fruit, should start moving in a random direction
    def __init__(self, x, y, w, h):
        super().__init__(x, y, w, h, "images/fruit1.png")
        self.px = 0
        self.py = 0
        self.speed = 5
        self.direction = random.randint(0, 3)

    # Fruit constantly moves, returns if it should still exist (is Fruit consumed yet?)
    def update(self):
        word_directions = ["left", "up", "right", "down"] # translate my number system for direction into how the starter code takes direction for move()
        self.move(word_directions[self.direction])

        return self.valid
    
    # Fruit's move function, works very similar to MsPacman and Ghosts
    def move(self, direction):
        self.px = self.x # previous coordinates for collision fixing
        self.py = self.y

        if direction == "up":
            self.y -= self.speed
        if direction == "down":
            self.y += self.speed
        if direction == "left":
            self.x -= self.speed
            if (self.x <= 0):
                self.x = 600 - self.w # warp
        if direction == "right":
            self.x += self.speed
            if (self.x >= 600 - self.w):
                self.x = 0 # warp

    # fix collision to get out of a tile, bounce to the opposite direction
    def get_out_of_tile(self, tile):
        if (self.get_right() >= tile.get_left() and self.px + self.w <= tile.get_left()):
            self.x = tile.get_left() - self.w - 1
        if (self.get_left() <= tile.get_right() and self.px >= tile.get_right()):
            self.x = tile.get_right() + 1
        if (self.get_top() <= tile.get_bottom() and self.py >= tile.get_bottom()):
            self.y = tile.get_bottom() + 1
        if (self.get_bottom() >= tile.get_top() and self.py + self.h <= tile.get_top()):
            self.y = tile.get_top() - self.h - 1
        
        # flip direction
        if (self.direction == 0):
            self.direction = 2
        elif (self.direction == 2):
            self.direction = 0
        elif (self.direction == 1):
            self.direction = 3
        else:
            self.direction = 1
    
    # should be called when MsPacman touches Fruit, flags that Fruit should be deleted when checked in update()
    def consumed(self):
        self.valid = False

    # this is a fruit
    def is_fruit(self):
        return True

class Ghost(Sprite):
    # initialize Ghost, starts in a random direction and has a set of arrays and variables involved for withering animation
    def __init__(self, x, y, w, h):
        super().__init__(x, y, w, h, "images/inky1.png")
        self.px = 0
        self.py = 0
        self.speed = 5
        self.image_list = [[""]*2,[""]*2,[""]*2,[""]*2,[""]*2,[""]*2] # 6 arrays of length 2 with placeholders
        self.eye_list = [""]*4 # initalize to an array with 4 placeholder strings 
        self.frame = 0
        self.direction = 0
        self.withering = False

        self.load_images()
        self.randomize_direction() # start in a random direction
    
    # attempt to load in array images to be referenced in arrays if not already done
    def load_images(self):
        for i in range(0, 6):
            for j in range(0, 2):
                if (self.image_list[i][j] == "" and i > 3): # if there is a placeholder, fill in the proper image
                    self.image_list[i][j] = pygame.image.load(f"images/ghost{2*(i-4)+j+1}.png")
                elif (self.image_list[i][j] == ""): # different file names at different parts of the array
                    self.image_list[i][j] = pygame.image.load(f"images/inky{2*i+j+1}.png") 

        for i in range(0, 4):
            if (self.eye_list[i] == ""): # if there is a placeholder, fill in the proper image
                self.eye_list[i] = pygame.image.load(f"images/ghost{i + 5}.png")

    # unique Ghost draw function, tracks frames to know when to change phases in the withering animation, or just doing general animation
    def draw(self, scrollpos, view):
        LOCATION = (self.x, self.y - scrollpos)
        SIZE = (self.w, self.h)

        if (self.frame >= 40):
            self.image = self.eye_list[self.direction]
        elif (self.frame >= 20):
            self.image = self.image_list[5][self.frame % 2]
        elif (self.withering):
            self.image = self.image_list[4][self.frame % 2]
        else:
            self.image = self.image_list[self.direction][self.frame]

        view.screen.blit(pygame.transform.scale(self.image, SIZE), LOCATION)
        self.rect = pygame.Rect(self.x, self.y - scrollpos, self.w, self.h) # update bounding box

    # Ghost constantly moves, will go through withering process when flagged to be removed
    def update(self):
        if (self.withering):
            self.frame += 1 # start ticking up frames to carry out animation phases

            if (self.frame == 60): # should be removed from the game now
                self.valid = False
        else:
            word_directions = ["left", "up", "right", "down"]
            self.move(word_directions[self.direction])

            self.frame = (self.frame + 1) % 2 # flicker animation or "ruffles"

        return self.valid
    
    # Ghost's move function, works very similar to MsPacman and Fruit
    def move(self, direction):
        self.px = self.x
        self.py = self.y

        if direction == "up":
            self.y -= self.speed
        if direction == "down":
            self.y += self.speed
        if direction == "left":
            self.x -= self.speed
            if (self.x <= 0):
                self.x = 600 - self.w
        if direction == "right":
            self.x += self.speed
            if (self.x >= 600 - self.w):
                self.x = 0

    # fix collision to get out of a tile, bounce to a random direction
    def get_out_of_tile(self, tile):
        if (self.get_right() >= tile.get_left() and self.px + self.w <= tile.get_left()):
            self.x = tile.get_left() - self.w - 1
        if (self.get_left() <= tile.get_right() and self.px >= tile.get_right()):
            self.x = tile.get_right() + 1
        if (self.get_top() <= tile.get_bottom() and self.py >= tile.get_bottom()):
            self.y = tile.get_bottom() + 1
        if (self.get_bottom() >= tile.get_top() and self.py + self.h <= tile.get_top()):
            self.y = tile.get_top() - self.h - 1
        
        # go to a new direction
        old_direction = self.direction
        while (old_direction == self.direction):
            self.randomize_direction()

    # get a new random direction
    def randomize_direction(self):
        self.direction = random.randint(0, 3)
    
    # start the withering animation
    def trigger_withering(self):
        self.withering = True

    # this is a ghost
    def is_ghost(self):
        return True
    
class Pellet(Sprite):
    # initialize a non-moving pellet
    def __init__(self, x, y, w, h):
        super().__init__(x, y, w, h, "images/pellet.png")

    # should be called when MsPacman touches Pellets, flags that Pellet should be deleted when checked in update()
    def consumed(self):
        self.valid = False

    # this is a pellet
    def is_pellet(self):
        return True

# class Rock(Sprite):
#     # variables that belong to the class, not to a specific
#     # instance of the class - this is similar to Java's static variables
#     ROCK_WIDTH = 50
#     ROCK_HEIGHT = 50
#     num_rocks = 0
    
#     # this method belongs to the class itself and does not need
#     # the 'self' object
#     @staticmethod
#     def reset_rocks():
#         Rock.num_rocks = 0
    
#     # constructor with default values - one of the ways you can
#     # mimic Java's overloaded constructors
#     # In this example, if w and h are provided, create the rock
#     # as defined. If they are not provided, create a rock at 
#     # half the regular size
#     # ee is an easter egg for the drag-and-add Rock image
#     # it has solely been added to tell the difference between
#     # mouse motion and mouse click for the starter code
#     def __init__(self, x, y, w=None, h=None, ee=None):
#         if ee is not None:
#             super().__init__(x, y, w, h, "images/rock2.png")
#         elif w is None or h is None:
#             super().__init__(x, y, Rock.ROCK_WIDTH/2, Rock.ROCK_HEIGHT/2, "images/rock.png")
#         else:
#             super().__init__(x, y, w, h, "images/rock.png")
#         Rock.num_rocks += 1

#     def is_rock(self):
#         return True

# class Turtle(Sprite):
#     TURTLE_WIDTH = 80
#     TURTLE_HEIGHT = 59
    
#     def __init__(self, x, y):
#         super().__init__(x, y, Turtle.TURTLE_WIDTH, Turtle.TURTLE_HEIGHT, "images/turtle.png")

#     def is_turtle(self):
#         return True

#     def move(self, direction):
#         if direction == "up":
#             self.y -= self.speed
#         if direction == "down":
#             self.y += self.speed
#         if direction == "left":
#             self.x -= self.speed
#         if direction == "right":
#             self.x += self.speed

class Model():
    filename = "map.json"
    
    # load in the map from the JSON, and then set up the items we can draw on a cycle
    def __init__(self):
        self.load_map()
        self.items_i_can_add = []
        self.item_num = 0

        self.items_i_can_add.append(Tile(15, 15, 70, 70))
        self.items_i_can_add.append(Ghost(15, 15, 70, 70))
        self.items_i_can_add.append(Fruit(15, 15, 70, 70))
        self.items_i_can_add.append(Pellet(15, 15, 70, 70))

    # mostly the same as the starter code but adapted for the Ms. Pacman entities, loads map from JSON
    def load_map(self):
        # reset the player score if we're loading (or reloading)
        # the map
        MsPacman.reset_score()
        
        self.sprites = []
        
        # read through the JSON file and retrieve data for objects
        with open(Model.filename) as file:
            data = json.load(file)
            mspacman = data["MsPacman"]
            tiles = data["tiles"]
            ghosts = data["ghosts"]
            fruits = data["fruits"]
            pellets = data["pellets"]

        file.close()
        
        #create MsPacman
        for entry in mspacman:
            self.player = MsPacman(entry["x"], entry["y"])
        self.sprites.append(self.player)

        # pull key value pairs and create the rest of the Sprites
        for entry in tiles:
            self.sprites.append(Tile(entry["x"], entry["y"], 40, 40))

        # small detail but I am appending Pellets after Tiles so they will be drawn underneath moving entities
        for entry in pellets:
            self.sprites.append(Pellet(entry["x"], entry["y"], 10, 10))
        
        for entry in ghosts:
            self.sprites.append(Ghost(entry["x"], entry["y"], 30, 30))

        for entry in fruits:
            self.sprites.append(Fruit(entry["x"], entry["y"], 30, 30))

    # also pretty much the same as starter code but adapted for this project, saves the map to the JSON
    def save_map(self):
        # create lists for each type of sprite
        mspacman = []
        tiles = []
        ghosts = []
        fruits = []
        pellets = []

        # go through all of the sprites, saving them into the 
        # appropriate lists
        for s in self.sprites:
            if s.is_mspacman():
                mspacman.append(s.marshal())
            elif s.is_tile():
                tiles.append(s.marshal())
            elif s.is_ghost():
                ghosts.append(s.marshal())
            elif s.is_fruit():
                fruits.append(s.marshal())
            elif s.is_pellet():
                pellets.append(s.marshal())

        # create the dictionary of sprites for the JSON
        map_to_save = {
            "MsPacman": mspacman,
            "tiles": tiles,
            "ghosts": ghosts,
            "fruits": fruits,
            "pellets" : pellets
        }

        # Save to file
        with open(Model.filename, "w") as f:
            json.dump(map_to_save, f)

    # go through sprites to validate if a sprite should exist and check collisions
    def update(self):
        for sprite in self.sprites:
            if (not (sprite.update())): # remove sprite and continue
                self.sprites.remove(sprite)
                continue
            else:
                for sprite2 in self.sprites:
                    if (sprite != sprite2) and self.check_collision(sprite, sprite2): # collision between unique sprites
                        if (not (sprite.is_tile()) and not (sprite.is_pellet()) and sprite2.is_tile()):
                            sprite.get_out_of_tile(sprite2) # fix collision against a tile
                        if (sprite.is_mspacman() and (sprite2.is_fruit() or sprite2.is_pellet())):
                            if (sprite2.is_fruit()): # add a different score based on if a fruit or pellet is consumed
                                MsPacman.add_score(3)
                            else:
                                MsPacman.add_score(1)
                            sprite2.consumed() # fruit/pellet is flagged for deletion and adds points
                        if (sprite.is_mspacman() and sprite2.is_ghost()):
                            if (not sprite2.withering): # check withering to prevent adding points multiple times
                                MsPacman.add_score(5)
                            sprite2.trigger_withering() # start the withering process

    # returns if two objects are colliding, logic is the same as previous assignments
    def check_collision(self, spriteA, spriteB):
        if (spriteA.get_right() < spriteB.get_left()):
            return False
        if (spriteA.get_left() > spriteB.get_right()):
            return False
        if (spriteA.get_bottom() < spriteB.get_top()):
            return False
        if (spriteA.get_top() > spriteB.get_bottom()):
            return False
        
        return True

    # clear the map of everything but MsPacman, pretty much unchanged
    def clear_map(self):
        self.sprites.clear()
        self.sprites.append(self.player)
        MsPacman.reset_score() # score is reset, too
    
    # add a sprite in edit mode if a sprite can be placed there, based on the icon we are currently on
    def add_sprite(self, pos):
        width = 0 # will store the width and height applied to the drawn item
        height = 0

        # if we are adding a tile or pellet, width and height need to be different
        if (self.items_i_can_add[self.item_num].is_tile()):
            width = 40
            height = 40
        elif (self.items_i_can_add[self.item_num].is_pellet()):
            width = 10
            height = 10
        else:
            width = 30
            height = 30
        
        # based on the width and height, check if the space we are adding into would be empty
        temp_sprite = pygame.Rect(pos[0], pos[1], width, height)
        if self.is_this_space_empty(temp_sprite):
            if (self.items_i_can_add[self.item_num].is_tile()):
                self.sprites.append(Tile(pos[0], pos[1], 40, 40))
            elif (self.items_i_can_add[self.item_num].is_ghost()):
                self.sprites.append(Ghost(pos[0], pos[1], 30, 30))
            elif (self.items_i_can_add[self.item_num].is_fruit()):
                self.sprites.append(Fruit(pos[0], pos[1], 30, 30))
            elif (self.items_i_can_add[self.item_num].is_pellet()):
                self.sprites.append(Pellet(pos[0], pos[1], 10, 10))

    # pos was passed as the mouse position tuple - 
    # pos[0] is x, pos[1] is y
    # ee is an easter egg to determine whether or not the mouse was clicked
    # or dragged - different images will appear for each action
    # def add_rock(self, pos, ee=None):
    #     temp_sprite = pygame.Rect(pos[0], pos[1], Rock.ROCK_WIDTH, Rock.ROCK_HEIGHT)
    #     if self.is_this_space_empty(temp_sprite):
    #         self.sprites.append(Rock(pos[0], pos[1], Rock.ROCK_WIDTH, Rock.ROCK_HEIGHT, ee))

    # check if a space is empty, kept from starter code and reused in my add_sprite()
    def is_this_space_empty(self, other_sprite):
        for sprite in self.sprites:
            if other_sprite.colliderect(sprite.rect):
                return False
        return True


class View():
    def __init__(self, model):
        SCREEN_SIZE = (600,720) # changed from the original to fit my original map, orignally (800, 600)
        self.screen = pygame.display.set_mode(SCREEN_SIZE, 32)
        self.model = model
        self.scrollpos = 0

    # set up background, set up sprite graphics with the scrolling position, display score, and display edit icons
    def update(self):
        # change background color if the user is in edit_mode
        if Controller.edit_mode:
            self.screen.fill([146, 203, 146]) #light green, kept the same
        else:
            self.screen.fill([0, 0, 40]) #originally dark forest green, changed to fit previous assignment requirements (differing background)

        self.scrollpos = self.model.player.y - 325 # calculate the scroll position based on MsPacman

        # make sprites draw themselves to the screen
        for sprite in self.model.sprites:
            sprite.draw(self.scrollpos, self)
            # draw a bounding box around each image - good for debugging!
            pygame.draw.rect(self.screen, (255, 0, 0), sprite.rect, 2)

        # add text to the screen
        # Default font, size 32
        font = pygame.font.SysFont(None, 32)   
        text_string = "Score: " + str(MsPacman.score) # Output the player score using a static value from MsPacman
        SCORE_COLOR = (255, 255, 255)
        text_surface = font.render(text_string, True, SCORE_COLOR)
        TEXT_LOCATION = (500, 10)
        self.screen.blit(text_surface, TEXT_LOCATION)

        # add the edit mode icon, needs to be done down here to be drawn over everything else
        if Controller.edit_mode:
            # create the icon
            edit_rect = pygame.Rect(0, 0, 100, 100)
            pygame.draw.rect(self.screen, (0, 195, 0), edit_rect)
            icon = self.model.items_i_can_add[self.model.item_num]
            icon.draw(0, self)
        
        # update display screen
        pygame.display.flip()

class Controller():
    edit_mode = False
    
    # set up is unchanged from starter code
    def __init__(self, model, view):
        self.model = model
        self.view = view
        self.keep_going = True

    # mostly the same as starter code but now clicks add Sprites based on the icon cycle in edit mode
    def update(self):
        for event in pygame.event.get():
            if event.type == QUIT:
                self.keep_going = False
            elif event.type == KEYDOWN:
                if event.key == K_ESCAPE or event.key == K_q:
                    self.keep_going = False
            elif event.type == pygame.MOUSEBUTTONUP:
                if Controller.edit_mode:
                    # change the edit mode icon if clicking in the area of the icon box
                    if (self.edit_mode and pygame.mouse.get_pos()[0] <= 100 and pygame.mouse.get_pos()[1] <= 100):
                        self.model.item_num = (self.model.item_num + 1) % 4
                    else:
                        # otherwise, add a sprite appropriately if possible
                        x_pos = pygame.mouse.get_pos()[0]
                        y_pos = pygame.mouse.get_pos()[1] + self.view.scrollpos # account for scrolling position when clicking somewhere

                        # if we are adding tiles or pellets, our mouse input should be snapped
                        if (self.model.items_i_can_add[self.model.item_num].is_tile()):
                            x_pos = math.floor(x_pos/40) * 40
                            y_pos = math.floor(y_pos/40) * 40

                        elif (self.model.items_i_can_add[self.model.item_num].is_pellet()):
                            x_pos = math.floor(x_pos/40) * 40 + 15
                            y_pos = math.floor(y_pos/40) * 40 + 15

                        self.model.add_sprite((x_pos, y_pos)) # pass in a new tuple similar to how get_pos() returns a tuple
            elif event.type == pygame.MOUSEMOTION and event.buttons[0]:
                if Controller.edit_mode:
                    # for PELLETS only, dragging can be used to add pellets rapidly, uses the same snapping math as above
                    if (self.model.items_i_can_add[self.model.item_num].is_pellet()):
                        x_pos = pygame.mouse.get_pos()[0]
                        y_pos = pygame.mouse.get_pos()[1] + self.view.scrollpos # account for scrolling position when clicking somewhere
                        x_pos = math.floor(x_pos/40) * 40 + 15
                        y_pos = math.floor(y_pos/40) * 40 + 15
                        self.model.add_sprite((x_pos, y_pos))
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
        # arrow key movement for the player's MsPacman
        if keys[K_LEFT]:
            self.model.player.move("left")
        elif keys[K_RIGHT]:
            self.model.player.move("right")
        elif keys[K_UP]:
            self.model.player.move("up")
        elif keys[K_DOWN]:
            self.model.player.move("down")

# section unchanged from starter code
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
