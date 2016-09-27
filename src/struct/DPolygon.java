package struct;

import java.awt.Polygon;
import java.awt.geom.Point2D;



public class DPolygon{


    public Point2D.Double[] p;
    public boolean[] done;
    public int n, index = 0;
    

    public DPolygon(int n) {
        this.n = n;
        p = new Point2D.Double[n];
        done = new boolean[n];
    }
    
    public DPolygon(DPolygon poly) {
        this.n = poly.n;
        p = new Point2D.Double[n];
        for (int i = 0; i < n; i++) {
            p[i] = new Point2D.Double(poly.p[i].x, poly.p[i].y);
        }
        done = new boolean[this.n];
    }

    public boolean addPoint(Point2D.Double p) {

        if (!(index < n)) {
            return false;
        }
        
        this.p[index] = new Point2D.Double(p.x, p.y);
        index++;
        return true;
    }

    public void writePoly() {
        System.out.println("Broj vrhova: " + n + " (" + index + ")");
        for (Point2D.Double pt : p) {
            System.out.print("(" + pt.x + "," + pt.y + ") ");
        }
        System.out.println("");
    }

    public Polygon toPolygon(){
        int[] x = new int[n];
        int[] y = new int[n];
        for (int i = 0; i < n; i++) {
            x[i] = (int) Math.round(p[i].x);
            y[i] = (int) Math.round(p[i].y);
        }
        return new Polygon(x, y, n);
    }
}
