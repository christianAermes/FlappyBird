import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class FlappyBird extends PApplet {

boolean win;

Bird bird;
int birdSize = 60;
float prevY;

int pipeWidth = 100;
int pipeHeight = 1000;
int verticalPipeDistance = round(3*birdSize);     // vertical distance between a pair of pipes
int pipeDistance = 500;                           // horizontal distance between consecutive pipes
int N_pipes = 1000;                               // number of pipes
ArrayList<Pipe> pipes = new ArrayList();

float pipeVelocity0 = 2;
float pipeVelocity = pipeVelocity0;

PImage backGround, pipeUp, pipeDown;              // background image and sprites for the pipes 
PImage sprite, spriteDead, spriteUp, spriteDown;  // sprites for the bird

PFont Font;
int textsize;
String fontName = "AR Destine";                   // may not work on all systems

int score = 0;
int highScore = 0;
PrintWriter output;                               // For writing the hghscore to a save file


public void setup() {
  
  //// read in the latest highscore
  String[] lines = loadStrings("HighScores.txt");
  highScore = lines.length > 0 ? parseInt(lines[lines.length-1]) : 0;

  restartGame();
  
  //// load the sprites
  backGround = loadImage("data/Background.png");
  backGround.resize(width, height);
  
  pipeUp = loadImage("data/PipeUp.png");
  pipeUp.resize(pipeWidth, pipeHeight);
  pipeDown = loadImage("data/PipeDown.png");
  pipeDown.resize(pipeWidth, pipeHeight);
  
  sprite = loadImage("data/BirdSprite.png");
  spriteDead = loadImage("data/BirdSpriteDead.png");
  spriteUp = loadImage("data/BirdSpriteUp.png");
  spriteDown = loadImage("data/BirdSpriteDown.png");
  
  sprite.resize(birdSize, birdSize);
  spriteDead.resize(birdSize, birdSize);
  spriteUp.resize(birdSize, birdSize);
  spriteDown.resize(birdSize, birdSize);
}

public void draw() {
  image(backGround, 0, 0);
  float dh = bird.y - prevY;
  prevY = bird.y;
  
  bird.fall();
  for (Pipe p : pipes) {
    p.move();
    bird.checkCollision(p);
    p.show();
  }
  bird.show(dh);
  
  //// Managing the high scores
  score = bird.getScore();
  if (score > highScore) {
    highScore = score;
  }
  if (score%5 == 0 && score > 0 && !win) {
    //// increases the speed of the pipes each time the score increments by 5
    pipeVelocity *= 1.005f;
  }
  if (score == N_pipes) {
    win = true;
  }
  
  //// Write highscore to save file
  output = createWriter("data/HighScores.txt");
  output.println(str(highScore));
  output.flush(); // Writes the remaining data to the file
  output.close();
  
  //// Printing information to the screen
  //// Highscore, score, and gameover/ win
  if (!win) {
    if (!bird.dead) {
      textsize = 75;
      Font = createFont(fontName, textsize);
      textFont(Font);
      text(str(score), width/2, textsize);
    } else {
      textsize = 50;
      Font = createFont(fontName, textsize);
      textFont(Font);
      text("GAME OVER" , width/2, textsize);
      textsize = 50;
      text("- "+str(score)+" -", width/2, 125);
    }
  }
  else {
    textsize = 100;
    Font = createFont(fontName, textsize);
    textFont(Font);
    text("YOU WIN!", width/2, height/2-textsize/2);
  }
  textsize = 75;
  Font = createFont(fontName, textsize);
  textFont(Font);
  textAlign(CENTER);
  text(str(highScore), textsize, textsize);
}


public void restartGame() {
  clear();
  win = false;
  bird = new Bird(width/4, height/2);
  prevY = bird.y;
  score = 0;
  
  pipeVelocity = pipeVelocity0;
  pipes = new ArrayList();
  for (int i=0; i<N_pipes; i++) {
    int pipePosition = height - floor(random(height/2) + verticalPipeDistance/2.f);
    
    Pipe pipeDown = new Pipe(width + i*pipeDistance, pipePosition, pipeHeight, pipeWidth, false);
    pipes.add(pipeDown);
    
    Pipe pipeUp = new Pipe(width + i*pipeDistance, pipePosition - pipeHeight - verticalPipeDistance, pipeHeight, pipeWidth, true);
    pipes.add(pipeUp);
  }
}

public void keyPressed() {
  if (bird.dead || win) {
    restartGame();
  } else {
    bird.fly();
  }
}
class Bird {
  float x, y;
  float vy;
  float g, lift;
  boolean dead;
  Bird(int bx, int by) {
    g = 0.3f; // gravitational pull downwards
    lift = -7; // lift the bird gets from flying
    x = bx;
    y = by;
    vy = 0; // bird velocity for falling
    dead = false;
  }

  public void show(float dh) {
    //// shows a sprite depending on the direction of movement
    if (dead) {
      image(spriteDead, x-birdSize/2, y-birdSize/2);
    } else {
      if (dh > 0) {
        image(spriteDown, x-birdSize/2, y-birdSize/2);
      } else if (dh < 0) {
        image(spriteUp, x-birdSize/2, y-birdSize/2);
      } else {
        image(sprite, x-birdSize/2, y-birdSize/2);
      }
    }
  }
  
  public void checkCollision(Pipe pipe) {
    //// if the bird touches anything, it dies
    //// collision with pipe
    if (!pipe.up) {
      if ((x+birdSize/2.f >= pipe.x) && (x-birdSize/2.f <= pipe.x + pipeWidth)) {
        if (y+birdSize/2.f >= pipe.y) {
          died();
        }
      }
    }
    else if (pipe.up) {
      if ((x+birdSize/2.f >= pipe.x) && (x-birdSize/2.f <= pipe.x + pipeWidth)) {
        if (y-birdSize/2.f <= pipe.y+pipeHeight) {
          died();
        }
      }
    }
    //// collision with the ground
    if (y + birdSize/2.f >= height-10) {
      died();
    }
  }
  
  public int getScore() {
    int score = 0;
    for (Pipe p : pipes) {
      if (bird.x > p.x + pipeWidth) {
        score ++;
      }
    }
    return score/2;
  }
  
  public void fall() {
    //// only move while the game is not yet won
    if (!win) {
      if (y > height - birdSize/2.f-10) {
        vy = 0;
        y = height - birdSize/2.f-10;
      }
      if (y <= height - birdSize/2.f) {
        vy += g;
        y += vy;
      }
    }
  }

  public void fly() {
    //// cannot leave the screen at the top
    if (y-birdSize >= 0) {
      vy = lift;
    }
  }

  public void died() {
    dead = true;
    vy = 20; // bird falls down like a rock
  }
}
class Pipe {
  int h, w; // width and height of the pipe
  int x, y; // position of the pipe
  boolean up; // does the opening point upwards?
  Pipe(int pipeX, int pipeY, int Height, int Width, boolean upperRow) {
    h = Height;
    w = Width;
    x = pipeX;
    y = pipeY;
    up = upperRow;
    
  }
  
  public void show() {
    if (up) {
      image(pipeDown, x, y);
    } else if (!up) {
      image(pipeUp, x, y);
    }
  }
  
  public void move() {
    //// only move while the game is not yet won
    if (!bird.dead && !win) {
      x -= pipeVelocity;
    }
  }
}
  public void settings() {  size(1000, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FlappyBird" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
