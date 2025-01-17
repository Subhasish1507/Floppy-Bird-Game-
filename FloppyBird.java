import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
//import java.util.random.*;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener,KeyListener
{
    int boardWidth = 360;
    int boardHeight = 640;

    //Image Declare 
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;


    //Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;
    Image img;

    class Bird
    {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;
        Bird(Image img)
        {
            this.img = img;
        }
    }


    //Pipes logic
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    class Pipe
    {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;
        Pipe(Image img)
        {
            this.img = img;
        }

    }



    //Game Logic
    Bird bird;
    int velocityX = -4; //moves pipes to left speed(simulate to bird right)
    int velocityY = 0;  //moves bird up and down
    int gravity = 1;

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    FlappyBird()
    {
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //Load Image
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //Bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        //placePipe  Timer
        placePipeTimer = new Timer(1500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                placePipes();
            }
        });

        placePipeTimer.start();

        //game timer
        gameLoop = new Timer(1000/30, this);
        gameLoop.start();
    }
    

    public void placePipes()
    {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;
        Pipe topPipe = new Pipe(topPipeImg);
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
        bottomPipe.y = topPipe.y + openingSpace+pipeHeight;
        pipes.add(bottomPipe);

    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g)
    {
        //For Background
        g.drawImage(backgroundImg,0,0,boardWidth,boardHeight,null);

        //For Bird
        g.drawImage(bird.img,bird.x,bird.y,bird.width,bird.height,null);

        //For pipes
        for(int i = 0 ;i<pipes.size();i++)
        {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img,pipe.x,pipe.y,pipe.width,pipe.height,null);
        }

        //For Score
        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if(gameOver)
        {
            g.drawString("GAME OVER : "+String.valueOf((int)score), 50, 350);
        }
        else
        {
            g.drawString(String.valueOf((int)score), 10, 35);
        }
    }

    public void move()
    {
        //bird
        velocityY += gravity; 
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for(int i = 0 ;i<pipes.size();i++)
        {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x+pipe.width)
            {
                pipe.passed = true;
                score+=0.5;   // 0.5 because there are 2 pipes , so 0.5*2 = 1 , 1 for a pipe set  
            }

            if(collision(bird, pipe))
            {
                gameOver = true;
            }
        }
        if(bird.y > boardHeight)
        {
            gameOver = true;
        }
    }

    public boolean collision(Bird a , Pipe b)
    {
        return a.x < b.x+b.width &&
                a.x+a.width > b.x &&
                a.y < b.y+b.height &&
                a.y+a.height > b.y;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver)
        {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }


    
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE )
        {
            velocityY = -9;
        }
        if(gameOver)
        {
            // restart the game again
            velocityY = 0;
            bird.y = birdY;
            pipes.clear();
            score = 0;
            gameOver = false;
            gameLoop.start();
            placePipeTimer.start();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}
