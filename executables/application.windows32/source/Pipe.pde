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
  
  void show() {
    if (up) {
      image(pipeDown, x, y);
    } else if (!up) {
      image(pipeUp, x, y);
    }
  }
  
  void move() {
    //// only move while the game is not yet won
    if (!bird.dead && !win) {
      x -= pipeVelocity;
    }
  }
}
