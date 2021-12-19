package ma.fstt.game;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
/**
 * Title:        MazeDepthFirstSearch<p>
 * Description:  Demo program for Java AI Programming<p>
 * Copyright:    Copyright (c) Mark Watson, Released under Open Source Artistic License<p>
 * Company:      Mark Watson Associates<p>
 * @author Mark Watson
 * @version 1.0
 */

public class IntelligentGame extends javax.swing.JFrame implements  KeyListener{
	
	/**
	 * 
	 */
	private int xyValue=700;
	private int yImageValue=20;
	
    JPanel jPanel1 = new JPanel();
    Boolean HelpIsActivated=false;
    AStarSearchEngine currentSearchEngine = null;
    Maze maze=null;
    Graphics g=null;
    BufferedImage image=null;
    Graphics g2=null;
    private static int MaxSize=22;
    boolean gameStarted=false;
    public static String CurrentFile="LABY_21x21.txt";
    //--------Combo Box for Files
    String Mazes[] = {"21x21", "41x41", "61x61"};
    JComboBox cb = new JComboBox(Mazes);
    
    JButton StartButton=new JButton("Play");  
    
    JButton StopButton=new JButton("Stop");  
    
    JButton SaveButton=new JButton("Save");  
    
    JButton LoadButton=new JButton("Load");  

    int Score=0;

    
    
    JPanel scorePanel = new JPanel();
    JLabel scoreLabel = new JLabel("Score:"+String.valueOf(Score));
    
    Font scoreFont=new Font("Arial",Font.BOLD,36);
    Font startButtonFont=new Font("Arial",Font.BOLD,26);
    Font stopButtonFont=new Font("Arial",Font.BOLD,26);
    
    private BufferedImage bombImg;
    private BufferedImage bonusImg;
    private BufferedImage playerImg;
    private BufferedImage goalImg;

    boolean GameWon=false;
    int AgentvsCount=0;
    int AgentvsBonuses=0;
    int refreshCount=0;

    int height;
    int width;
    int bonusEaten=0;   
    
    
    //TIMER
    public class TestPane extends JPanel {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Timer timer;
        private long startTime = -1;
        private long duration = 5000000;

        private JLabel label;

        public TestPane() {

            //Un timer pour faire le countdown du partie du joueur
            setLayout(new GridBagLayout());
            timer = new Timer(10, new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (startTime < 0) {
                        startTime = System.currentTimeMillis();
                    }
                    long now = System.currentTimeMillis();
                    long clockTime = now - startTime;
                    if (clockTime >= duration) {
                        clockTime = duration;
                        timer.stop();
                        JFrame frame = new JFrame("time out");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JLabel over=new JLabel();
                over.setText("             Time is over");
                over.setBounds(400, 400, 200, 200);
                frame.add(over);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setBounds(400, 300, 200, 200);
                frame.setVisible(true);
                
                    }
                    SimpleDateFormat df = new SimpleDateFormat("mm:ss:SSS");
                    label.setText(df.format(duration - clockTime));
                }
            });
            
            timer.setInitialDelay(10);

            //ajouter une bouton pour debuter le timer
            StartButton.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent arg0) {
                if(!timer.isRunning()) {
                  System.out.println("Timer started!");
                  timer.start();
                } 
              }
            });
            
            //une bouton pour arreter le timer
            StopButton.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent arg0) {
                if(timer.isRunning()) {
                  System.out.println("Timer stoped!");
                  timer.stop();
                } 
              }
            });
            
             label = new JLabel("Timer");
             add(label);   
            
            
        }
        

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(70, 50);
        }

    }
    


    

    public IntelligentGame(int height, int width) {
    	
      
        try {
         jbInit();
        } catch (Exception e) {
          System.out.println("GUI initilization error: " + e);
        }
       System.out.println("Constructor called: ");
       this.height=height;
       this.width=width;
        currentSearchEngine = new AStarSearchEngine(height, width,CurrentFile);
        repaint();
    }

    


    //la methode paint qui sert a remplacer les valeurs des cases par des image/couleurs
    public void paint(Graphics g_unused) {

        if(Score<0) Score=0;
        //set the score value
        scoreLabel.setText("Score : \n"+String.valueOf(Score));

        //Lost if score==0
        if(Score==0 && gameStarted && bonusEaten>=1){
            //arreter la partie si c'est gagnee
            int res = JOptionPane.showOptionDialog(null,  "You Lost !! Score is "+Score,"", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, null, null);

            if(res==0) this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }

        //lire les fichiers des images
        try {
            bombImg= ImageIO.read(new File("C:\\Users\\Omar Ra\\Desktop\\Work\\MiniGame-WithAi\\src\\mazeFile\\bomb.png"));
            
            bonusImg= ImageIO.read(new File("C:\\Users\\Omar Ra\\Desktop\\Work\\MiniGame-WithAi\\src\\mazeFile\\watermelon.png"));

            playerImg= ImageIO.read(new File("C:\\Users\\Omar Ra\\Desktop\\Work\\MiniGame-WithAi\\src\\mazeFile\\man.png"));

            goalImg= ImageIO.read(new File("C:\\Users\\Omar Ra\\Desktop\\Work\\MiniGame-WithAi\\src\\mazeFile\\goal.png"));

        } catch (IOException ex) {
            Logger.getLogger(IntelligentGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        if (currentSearchEngine == null) return;

        //recuperer la labyrinthe d'apres le moteur de recherche
        maze = currentSearchEngine.getMaze();
        
        
        int width = maze.getWidth();
        int height = maze.getHeight();
        System.out.println("Size of current maze: " + width + " by " + height);
        
        g = jPanel1.getGraphics();
        image = new BufferedImage(xyValue, xyValue, BufferedImage.TYPE_INT_RGB);
        
        g2 = image.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0, xyValue, xyValue);
        g2.setColor(Color.black);
        
        
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
 
                short val = maze.getValue(x,y);
                
                
                if ( val == Maze.OBSTICLE) {
                    g2.setColor(Color.BLACK);
                    //colorier carre
                    g2.fillRect(6 + x * 29, 3 + y * 29, 29, 29);
                    
                    g2.setColor(Color.black);
                    g2.drawRect(6 + x * 29, 3 + y * 29, 29, 29); 
                } 
                
                else if (val == Maze.START_LOC_VALUE) {
                    //if game is won
                    if(x==width-1 && y==height-1){
                        GameWon=true;                        

                        //Stop the game if won
                        int res = JOptionPane.showOptionDialog(null,  "Congrats You Won !! Score is "+Score,"", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, null, null);
                        
                        if(res==0) this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                        
                        break;
                    }
                    
                    
                    g2.setColor(Color.blue);
                    
                    System.out.println("Repaint : set to ("+x+","+y+")");

                    g2.drawImage(playerImg,6+ x * 29,3 + y * 29, 29 , 29 , null);    
                    g2.setColor(Color.white);
                    g2.drawRect(6 + x * 29, 3 + y * 29, 29, 29);
                } 
                
                else if (val == Maze.GOAL_LOC_VALUE) {
                    g2.setColor(Color.red);
                    g2.drawImage(goalImg,6+ x * 29,3 + y * 29, 29 , 29 , null);    
                    g2.setColor(Color.white);
                    g2.drawRect(6 + x * 29, 3 + y * 29, 29, 29);
                 
                } 
                
                else if (val == Maze.Bomb) {
                    
                    g2.drawImage(bombImg,6+ x * 29,3 + y * 29, 29 , 29 , null);    
                    g2.setColor(Color.white);
                    g2.drawRect(6 + x * 29, 3 + y * 29, 29, 29);

                } 
                
                else if(val == Maze.Bonus){
                    
                    g2.drawImage(bonusImg,6+ x * 29,3 + y * 29, 29 , 29 , null);    
                    g2.setColor(Color.white);
                    g2.drawRect(6 + x * 29, 3 + y * 29, 29, 29);
               
                }
                else {
                	g2.setColor(Color.white);
                	g2.drawRect(6 + x * 29, 3 + y * 29, 29, 29);
                }
                
            }
            
        }

     
        //----------------------A*SearchPATH---------------------------------------------------
        // colorier le path du A* en rouge
        if(HelpIsActivated) {
            g2.setColor(Color.black);
            Dimension [] path = currentSearchEngine.getPath(Maze.col,Maze.line);
            for (int i=1; i< (path.length-1); i++) {
              int x = path[i].width;
              int y = path[i].height;
             g2.setColor(Color.red);
             g2.fillRect(6 + x * 29, 3 + y * 29,29,29);
             
              HelpIsActivated=false;
              
            }
            
            
            
        }
        g.drawImage(image, 10, yImageValue, 1500, 1000, null);   
        
    }
    

    public static void main(String[] args) {
        IntelligentGame mazeSearch1 = new IntelligentGame(MaxSize-1,MaxSize-1);    
        try {
			UIManager.setLookAndFeel (new NimbusLookAndFeel() );
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
    }

    
    private void jbInit() throws Exception {

        this.setContentPane(jPanel1);
        this.setCursor(null);
        this.setDefaultCloseOperation(3);
        this.setTitle("MazeDepthFirstSearch");
        this.getContentPane().setLayout(null);
        
        
        jPanel1.setBackground(Color.white);
        jPanel1.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
        jPanel1.setDoubleBuffered(false);
        jPanel1.setRequestFocusEnabled(false);
        jPanel1.setLayout(null);

        
     
    this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
    this.setUndecorated(false);
    this.addKeyListener(this);
        
        
 
      
    JPanel Sideboard = new JPanel();
    Sideboard.setLayout(new GridLayout(4,1,20,20));
    
      
    scoreLabel.setFont(scoreFont);
    scoreLabel.setForeground(Color.BLACK);
    scoreLabel.setBackground(null);
    scorePanel.add(scoreLabel);
    scorePanel.setVisible(true);
    
    
    
    JPanel timerPanel = new JPanel();
    timerPanel.setVisible(true);
    timerPanel.add(new TestPane());
    timerPanel.setSize(200, 200);
    
    

    JPanel Buttons = new JPanel();
    Buttons.setLayout(new GridLayout(1,2,5,5));
    
    
    //-------Start Button panel
    StartButton.setVisible(true);
    StartButton.setFocusable(false);
    StartButton.setFont(startButtonFont);
    StartButton.setForeground(Color.GREEN);
           

    //---------Stop button panel
    StopButton.setVisible(true);
    StopButton.setFocusable(false);
    StopButton.setFont(stopButtonFont);
    StopButton.setForeground(Color.red);
    
    
    
    
    SaveButton.setVisible(true);
    SaveButton.setFocusable(false);
    SaveButton.setFont(startButtonFont);
    SaveButton.setForeground(Color.black);
    
    LoadButton.setVisible(true);
    LoadButton.setFocusable(false);
    LoadButton.setFont(startButtonFont);
    LoadButton.setForeground(Color.blue);
        

    cb.setBounds(1050, 50, 90, 20);
    cb.setVisible(true);
    cb.setFocusable(false);
    
    
    
    
    Buttons.add(StartButton);
    Buttons.add(StopButton);
    Buttons.add(SaveButton);
    Buttons.add(LoadButton);
    
    Sideboard.add(cb);
    Sideboard.add(scoreLabel);
    Sideboard.add(timerPanel); 
    Sideboard.add(Buttons);
    
    Sideboard.setBounds(1510, 100, 400, 500);
    
    
    
    jPanel1.add(Sideboard);
   
    
    this.setVisible(true);
  //Start The Game
    StartButton.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            gameStarted=true;
            System.out.println("GameStarted : "+gameStarted);
        }    
    });
    
    //stop the game
    StopButton.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            gameStarted=false;
            System.out.println("GameStarted : "+gameStarted);
        }    
    });
    
    SaveButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          try {
        	int i;
        	FileInputStream fis = new FileInputStream ("C:\\Users\\Omar Ra\\Desktop\\Work\\Ai\\Serialised Mazes\\inrementalInteger.txt");
  			ObjectInputStream ois = new ObjectInputStream(fis);
      		i = (int) ois.readObject();
      		System.out.println(i);
			FileOutputStream fos = new FileOutputStream("C:\\Users\\Omar Ra\\Desktop\\Work\\Ai\\Serialised Mazes\\savedmaze"+i+".txt");i++;
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(currentSearchEngine.getMaze());
			os.close();
			new FileOutputStream("C:\\Users\\Omar Ra\\Desktop\\Work\\Ai\\Serialised Mazes\\inrementalInteger.txt").close();
			FileOutputStream fos0 = new FileOutputStream("C:\\Users\\Omar Ra\\Desktop\\Work\\Ai\\Serialised Mazes\\inrementalInteger.txt");
			ObjectOutputStream os0 = new ObjectOutputStream(fos0);
			os0.writeObject(i);
			os0.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        }
      });


    //une load bouton
    LoadButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
        	try {
        		JFileChooser filechooser= new JFileChooser();
        		int response =filechooser.showOpenDialog(null);
        		if (response== JFileChooser.APPROVE_OPTION){
        			String FilePath= filechooser.getSelectedFile().getAbsolutePath();
        			FileInputStream fis = new FileInputStream (FilePath);
        			ObjectInputStream ois = new ObjectInputStream(fis);
            		Maze m = (Maze) ois.readObject();
            		currentSearchEngine.setMaze(m);
            		if(m.getHeight()==21) {
            			MaxSize=21;
                    	height=21;
                    	width=21;
                    	xyValue=700;
                    	yImageValue=20;
            		}else if(m.getHeight()==41) {
            			MaxSize=41;
                    	height=41;
                    	width=41;
                    	xyValue=1200;
                    	yImageValue=20;
            			
            		}else if(m.getHeight()==61) {
            			MaxSize=61;
                    	height=61;
                    	width=61;
                    	xyValue=1800;
                    	yImageValue=23;
            			
            		}
            		repaint();
            		
            		
            		
            		
        		}
        		
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
    });

    //switch case pour afficher le labyrinthe choisit
    cb.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
           

            String s = (String) cb.getSelectedItem();
            switch (s) {//check for a match
                case "21x21":
                	MaxSize=21;
                	height=21;
                	width=21;
                	CurrentFile="LABY_21x21.txt";
                	Score=0;
                	bonusEaten=0;
                	Maze.line=1;
                	Maze.col=1;
                	xyValue=700;
                	yImageValue=20;
                	currentSearchEngine = new AStarSearchEngine(MaxSize, MaxSize,CurrentFile);
                    repaint();
                	System.out.println(MaxSize);
                    break;
                case "41x41":
                	MaxSize=41;
                	height=41;
                	width=41;
                	CurrentFile="LABY_41x41.txt";
                	Score=0;
                	bonusEaten=0;
                	Maze.line=1;
                	Maze.col=1;
                	xyValue=1200;
                	yImageValue=20;
                	currentSearchEngine = new AStarSearchEngine(MaxSize, MaxSize,CurrentFile);
                    repaint();
                	System.out.println(MaxSize);
                    break;
                case "61x61":
                	MaxSize=61;
                	height=61;
                	width=61;
                	CurrentFile="LABY_61x61.txt";
                	Score=0;
                	bonusEaten=0;
                	Maze.line=1;
                	Maze.col=1;
                	xyValue=1800;
                	yImageValue=23;
                	currentSearchEngine = new AStarSearchEngine(MaxSize, MaxSize,CurrentFile);
                    repaint();
                	System.out.println(MaxSize);
                    break;
            }
        }
    });
    
        
    }

    //fonction pour verifier le gain
    public boolean checkWin(int col,int line){
        if(maze.getValue(col,line)==Maze.GOAL_LOC_VALUE) GameWon=true;
        return GameWon;
    }
    
    public int countAgentVsBomb(int col,int line){
        if(maze.getValue(col,line)==Maze.Bomb) {
        	++AgentvsCount;
        	Score -=2;
        }
        return Score;
    }
    
    public int countAgentVsBonus(int col,int line){
        if(maze.getValue(col,line)==Maze.Bonus) {
        	++AgentvsBonuses;
        	Score +=5;
        }
        return Score;
    }
    
    
    
    
    //------------------KEY LISTENER METHODS (agent mouvement)---------------------------//   
    @Override
    public void keyReleased(KeyEvent e) {
        
        if(gameStarted){
              
        switch(e.getKeyCode()){   
            
                case Maze.RIGHT:
                    if(maze.getValue(Maze.col+1,Maze.line)!=Maze.OBSTICLE){  //check if next move is an obstacle
                                            
                        //decrementer score si recontre d'agent et bomb 
                        if(maze.getValue(Maze.col+1,Maze.line)==Maze.Bomb  && Score!=0){
                            Score-=2;                           
                        }
                        
                        
                        if(maze.getValue(Maze.col+1,Maze.line)==Maze.Bonus ) {
                        	Score+=5;
                            bonusEaten++;
                        	maze.setBonuses();
                        }
                      
                        //deplacer le 'S' (agent)
                        maze.setValue(++Maze.col, Maze.line, Maze.START_LOC_VALUE);
                        
                        
                        //suprimmer l'ancien 'S' dans la matrice si c'est pas un bomb
                        maze.setValue(Maze.col-1, Maze.line, (short)0);
                        
                    }
                    else{
                        System.out.println("___OBSTACLE HERE"+maze.getValue(Maze.col, Maze.line));
                    }
                    break;
                     
                    

                //meme commentaire pour le premier case
                case Maze.LEFT:
                   if(maze.getValue(Maze.col-1,Maze.line )!=Maze.OBSTICLE){  //check if next move is an obstacle
                        
                        if(maze.getValue(Maze.col-1,Maze.line)==Maze.Bomb && Score!=0) Score-=2;
                        
                        if(maze.getValue(Maze.col-1,Maze.line)==Maze.Bonus ) {
                        	Score+=5;
                            bonusEaten++;
                        	maze.setBonuses();
                        }

                        maze.setValue(--Maze.col, Maze.line, Maze.START_LOC_VALUE);
                        
                        maze.setValue(Maze.col+1, Maze.line, (short)0);
                                             
                    }
                   else{
                        System.out.println("___OBSTACLE HERE"+maze.getValue(Maze.col, Maze.line));
                    }
                    break;

                    
                    
                    
                    
                case Maze.UP: 
                    if(maze.getValue(Maze.col, Maze.line-1)!=Maze.OBSTICLE){  //check if next move is an obstacle
                        
                        if(maze.getValue(Maze.col,Maze.line-1)==Maze.Bomb && Score!=0) Score-=2;
                        
                        if(maze.getValue(Maze.col,Maze.line-1)==Maze.Bonus ) {
                        	Score+=5;
                            bonusEaten++;
                        	maze.setBonuses();
                        }
                        
                        maze.setValue(Maze.col, --Maze.line, Maze.START_LOC_VALUE);
                        
                        maze.setValue(Maze.col, Maze.line+1, (short)0);
                        
                        System.out.println(Score);
                    }
                      else{
                        System.out.println("___OBSTACLE HERE"+maze.getValue(Maze.col, Maze.line));
                    }
                    break;

                    
                    
                    
                    
                case Maze.DOWN:
                    if(maze.getValue(Maze.col, Maze.line+1)!=Maze.OBSTICLE){  //check if next move is an obstacle
                        
                       if(maze.getValue(Maze.col,Maze.line+1)==Maze.Bomb && Score!=0) Score-=2;
                       
                       if(maze.getValue(Maze.col,Maze.line+1)==Maze.Bonus ) {
                    	   Score+=5;
                           bonusEaten++;
                    	   maze.setBonuses();
                       }

                        maze.setValue(Maze.col, ++Maze.line, Maze.START_LOC_VALUE);
                        
                        maze.setValue(Maze.col, Maze.line-1, (short)0);
                        
                    }
                    else{
                        System.out.println("___OBSTACLE HERE "+maze.getValue(Maze.col, Maze.line));
                    }
                    break;       
                    
                    
                    
                    
                    
                case Maze.HelpButton:
                	HelpIsActivated=true;
                        
                	if(Score>3) Score-=3;
                        
                	System.out.println("The maze Col is "+Maze.col+" And the maze Lin is "+Maze.line);
                	System.out.println("height "+this.height+" width "+width);
                	Maze m = currentSearchEngine.getMaze();
                	currentSearchEngine = new AStarSearchEngine(height, width,CurrentFile);
                	currentSearchEngine.setMaze(m);

                	break;
            }
        
        
        //Regenrating bombs in case of contact
        for(Dimension bombDim : maze.BombDimensions){
            maze.setValue(bombDim.width, bombDim.height, Maze.Bomb);
        }


        //remove player from 1st position
        maze.setValue(0, 0, (short)0);

        //_____UPDATE The FRAME_____//
        repaint();
        
        }
        }
     
    
    
    
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}
    }
    
    
    
    
 
    
    
    

    
    
   