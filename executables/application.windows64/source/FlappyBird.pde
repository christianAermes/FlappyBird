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


void setup() {
  size(1000, 600);
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

void draw() {
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
    pipeVelocity *= 1.005;
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


void restartGame() {
  clear();
  win = false;
  bird = new Bird(width/4, height/2);
  prevY = bird.y;
  score = 0;
  
  pipeVelocity = pipeVelocity0;
  pipes = new ArrayList();
  for (int i=0; i<N_pipes; i++) {
    int pipePosition = height - floor(random(height/2) + verticalPipeDistance/2.);
    
    Pipe pipeDown = new Pipe(width + i*pipeDistance, pipePosition, pipeHeight, pipeWidth, false);
    pipes.add(pipeDown);
    
    Pipe pipeUp = new Pipe(width + i*pipeDistance, pipePosition - pipeHeight - verticalPipeDistance, pipeHeight, pipeWidth, true);
    pipes.add(pipeUp);
  }
}

void keyPressed() {
  if (bird.dead || win) {
    restartGame();
  } else {
    bird.fly();
  }
}
