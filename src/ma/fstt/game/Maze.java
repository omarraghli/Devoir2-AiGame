package ma.fstt.game;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
/**
 * Class Maze - private class for representing search space as a two-dimensional
 * maze
 */
public class Maze implements Serializable {


    /**
	 * 
	 */
	
	public static short OBSTICLE = -1;
    public static short START_LOC_VALUE = -2;
    public static short GOAL_LOC_VALUE = -3;
    public static short Bomb = -4;
    public static short Bonus = -5;
    int width = 0;
    int height = 0;
    int bonusNumber=5;
    public Dimension startLoc = new Dimension();
    public Dimension goalLoc = new Dimension();
    
    //agent mouvement key codes 
    public final static short UP=38;
    public final static short DOWN=40;
    public final static short LEFT=37;
    public final static short RIGHT=39;
    public final static short HelpButton=72;
    private List <Dimension> EmptyBoxesForBombes = new ArrayList<Dimension>();
    private List <Dimension> EmptyBoxesForBonuses = new ArrayList<Dimension>();
    
    public List <Dimension> BombDimensions= new ArrayList<Dimension>();

    //Emplacement de l'agent
    public static int line=1,col=1;

    private final short[][] maze;


    public Maze(int width, int height,String FileName) {
        System.out.println("New maze of size " + width + " by " + height);
        this.width = width;
        this.height = height;
        maze = new short[width + 2][height + 2];

        int j = 0;
        int i = 0;

        //cases vide du labyrinthe
        for (i = 0; i < width + 2; i++) {
            for (j = 0; j < height + 2; j++) {
                maze[i][j] = 0;
            }
        }

        //ajouter -1 pour les case qui vont contenir des obstacles
        for (i = 0; i < height + 2; i++) {
            maze[0][i] = maze[width + 1][i] = OBSTICLE;
        }
        for (i = 0; i < width + 2; i++) {
            maze[i][0] = maze[i][height + 1] = OBSTICLE;
        }

        //chemin absolu du fichier du lanyrinthe choisis par le joueur
        File fileDirs = new File("C:\\Users\\Omar Ra\\Desktop\\Work\\MiniGame-WithAi\\src\\mazeFile\\"+FileName);

        BufferedReader in = null;
        
            try {
				in = new BufferedReader(
				        new InputStreamReader(new FileInputStream(fileDirs), "utf-8"));
			} catch (UnsupportedEncodingException | FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        

        String str;


            i = 1;
            try {
				while ((str = in.readLine()) != null) {

				    for (j = 0; j < str.length(); j++) {
				        if (str.charAt(j) == ' ') {
				            maze[j+1][i] = 0;
				        } else {
				            maze[j+1][i] = -1;
				        }
				    }
				    i++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
    

            // ---------- lecture de maze
            for (i = 0; i < maze.length; i++) {

                for (j = 0; j < maze[i].length; j++) {
                    System.out.print(maze[i][j]);
                }
                System.out.println();
            }
            i=0;j=0;


        //generation des bombes arbitrairement
        setBombes();
        
        
        //generation des bonus arbitrairement
        for(int ii=0;ii<bonusNumber;ii++){
            setBonuses();          
        }
        
        //positionner le point de depart du joueur
        startLoc.width = 0;
        startLoc.height = 0;
        this.setValue(0, 0, Maze.START_LOC_VALUE);  

        //positioner le point d'arrivee du joueur (objectif)
        goalLoc.width = width - 1;
        goalLoc.height = height - 1;
        maze[width][height] = GOAL_LOC_VALUE;
    }
    
    

    //retourner la value d'une position
    synchronized public short getValue(int x, int y) {
        return maze[x + 1][y + 1];
    }
    //set la valeur d'une position
    synchronized public void setValue(int x, int y, short value) {
        maze[x+1][y+1] = value;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    
    
    
    
    public void setBombes() {
    	//-------------------get les cases vides pour les traiter apres------------------
    	this.EmptyBoxesForBombes.clear();
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
            	short val = getValue(x,y);
            	if ( val ==0) {
            		this.EmptyBoxesForBombes.add(new Dimension(x,y));
            	}
            }
        }
        
        ArrayList<Integer> UniqueRandomInts=RandomUniqueNumbers(this.EmptyBoxesForBombes); 
        
        //--------------------------AddRadomsBombes---------------------------
        int max_obsticles = 5;
        for (int i=0; i<max_obsticles; i++) {
          Random randomizer = new Random();
      	  Dimension RandomeBombe = this.EmptyBoxesForBombes.get(UniqueRandomInts.get(randomizer.nextInt(UniqueRandomInts.size())));
      	  
      	  System.out.println("The width is "+RandomeBombe.width+" and the height is "+RandomeBombe.height);
      	  
      	  setValue(RandomeBombe.width, RandomeBombe.height,Maze.Bomb); 	 
          
          //add the bombs for re-generation
          BombDimensions.add(new Dimension(RandomeBombe.width, RandomeBombe.height));
        }
    
    }
    
    public void setBonuses() {
        
    	//-------------------get les cases vides pour les traiter apr�s------------------
    	this.EmptyBoxesForBonuses.clear();
    	for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
            	short val = getValue(x,y);
            	if ( val ==0) {
            		this.EmptyBoxesForBonuses.add(new Dimension(x,y));
            	}
            }
        }
    	
    	//--------------------RandomPositionsOfBonues---------------------
    	ArrayList<Integer> UniqueRandomInts=RandomUniqueNumbers(this.EmptyBoxesForBonuses);
        
    	//--------------------------AddRadomBonuse---------------------------
          Random randomizer = new Random();
          
      	  Dimension RandomeBonuses = this.EmptyBoxesForBombes.get(UniqueRandomInts.get(randomizer.nextInt(UniqueRandomInts.size())));
      	  
      	  System.out.println("The width of the current bonus is "+RandomeBonuses.width+" and the height of the current bonus is "+RandomeBonuses.height);
      	           
      	  setValue(RandomeBonuses.width, RandomeBonuses.height,Maze.Bonus);
          
          
    }
    
    
    public ArrayList<Integer> RandomUniqueNumbers(List<Dimension> emptyBoxesForBombes2){
        
    	//----------------------avoir des ints random unique :-----------------------------
        ArrayList<Integer> UniqueRandomInts = new ArrayList<Integer>();
        for (int i=0; i<emptyBoxesForBombes2.size()+5; i++) {
        	UniqueRandomInts.add(i);
        }
         Collections.shuffle(UniqueRandomInts);
         
         return UniqueRandomInts;
         
    }



	public int getLine() {return line;}



	public int getCol() {return col;}



    
    
    
    
}
