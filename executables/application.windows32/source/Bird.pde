class Bird {
  float x, y;
  float vy;
  float g, lift;
  boolean dead;
  Bird(int bx, int by) {
    g = 0.3; // gravitational pull downwards
    lift = -7; // lift the bird gets from flying
    x = bx;
    y = by;
    vy = 0; // bird velocity for falling
    dead = false;
  }

  void show(float dh) {
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
  
  void checkCollision(Pipe pipe) {
    //// if the bird touches anything, it dies
    //// collision with pipe
    if (!pipe.up) {
      if ((x+birdSize/2. >= pipe.x) && (x-birdSize/2. <= pipe.x + pipeWidth)) {
        if (y+birdSize/2. >= pipe.y) {
          died();
        }
      }
    }
    else if (pipe.up) {
      if ((x+birdSize/2. >= pipe.x) && (x-birdSize/2. <= pipe.x + pipeWidth)) {
        if (y-birdSize/2. <= pipe.y+pipeHeight) {
          died();
        }
      }
    }
    //// collision with the ground
    if (y + birdSize/2. >= height-10) {
      died();
    }
  }
  
  int getScore() {
    int score = 0;
    for (Pipe p : pipes) {
      if (bird.x > p.x + pipeWidth) {
        score ++;
      }
    }
    return score/2;
  }
  
  void fall() {
    //// only move while the game is not yet won
    if (!win) {
      if (y > height - birdSize/2.-10) {
        vy = 0;
        y = height - birdSize/2.-10;
      }
      if (y <= height - birdSize/2.) {
        vy += g;
        y += vy;
      }
    }
  }

  void fly() {
    //// cannot leave the screen at the top
    if (y-birdSize >= 0) {
      vy = lift;
    }
  }

  void died() {
    dead = true;
    vy = 20; // bird falls down like a rock
  }
}
