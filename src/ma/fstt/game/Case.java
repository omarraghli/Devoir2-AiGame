package ma.fstt.game;


import java.awt.Dimension;

public class Case extends Dimension implements Comparable{

    /**
	 * 
	 */
	
	private double g;
    private double f;

    public Case(int width, int height) {
        super(width, height);
        f=0;
        g=0;
    }

    public Case(Dimension d, double g,double f) {
        super(d);
        this.g = g;
        this.f =f;
    }
    
    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    @Override
    public int compareTo(Object o) {
       if(f<((Case)o).f)
           return -1;
       else if(f>((Case)o).f)
           return 1;
       else 
           return 0;
    }
    
    public String toString() {
        String s;
        s = "("+this.width+", "+this.height+"), f= "+f+" et g= "+g;
        return s;
    }
    
}
