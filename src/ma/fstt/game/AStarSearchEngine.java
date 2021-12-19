package ma.fstt.game;


import java.awt.Dimension;
import java.util.*;

public class AStarSearchEngine extends AbstractSearchEngine {
    
    ArrayList<Dimension> L2= new ArrayList<Dimension>(), T=new ArrayList<Dimension>();
    PriorityQueue<Dimension> L1 = new PriorityQueue<Dimension>();
    Dimension predecessor[][];  
        
    public AStarSearchEngine(int width, int height,String FileName) {
        super(width, height,FileName);
        
        predecessor = new Dimension[width][height];  
        for (int i=0; i<width; i++) 
            for (int j=0; j<height; j++) 
                predecessor[i][j] = null;
        
        L1.add(new Case(startLoc,0,-1.0));
        T.add(goalLoc);
        
        doAStar();
        
        maxDepth = 0;
        if (!isSearching) {
            searchPath[maxDepth++] = goalLoc;
            for (int i=0; i<400; i++) {
                searchPath[maxDepth] = predecessor[searchPath[maxDepth - 1].width][searchPath[maxDepth - 1].height];
                maxDepth++;
                if (equals(searchPath[maxDepth - 1], startLoc))  break;  
            }
        }
    }
    
    public double h(Dimension d) {
          return Math.sqrt((goalLoc.height-d.height)*(goalLoc.height-d.height)+(goalLoc.width-d.width)*(goalLoc.width-d.width));
    }
   
    private void doAStar() {
              
        while (!L1.isEmpty()) {
            
            currentLoc = L1.remove(); 
            
            L2.add(currentLoc);           
            if(T.contains(currentLoc)){
                 System.out.println("But trouvé : (" + currentLoc.width +", " + currentLoc.height+")");
                 isSearching = false;
                 break;
             }
             else{
                
                Dimension [] connected = getPossibleMoves(currentLoc);
                for (int i=0; i<4; i++) {

                    if ( connected[i]!=null && !L1.contains(connected[i]) && !L2.contains(connected[i])) {
                        Case casei= new Case(connected[i],((Case)currentLoc).getG()+1,((Case)currentLoc).getG()+1+h(connected[i]));
                        predecessor[connected[i].width][connected[i].height] = currentLoc;
                        L1.add(casei);
                    }
                }
            }
        }
        
    }
   
}
