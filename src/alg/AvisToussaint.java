package alg;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import struct.DPolygon;
import struct.DualTree;


public class AvisToussaint {
    
    static final double EPSILON = -0.0000000001;
    
    public DualTree[] dual;
    public DPolygon poly;
    public int n;
    
    
    int[] midTriangle = new int[3];
    
    int[] threeColouring;
    public int[] colourCount = new int[3];
    
    public AvisToussaint(DPolygon poly) {
        this.poly = new DPolygon(poly);
        n = poly.n;
        dual = new DualTree[n];
                
        threeColouring = new int[n];
        Arrays.fill(threeColouring, -1);
        
        for (int i = 0; i < n; i++) {
            dual[i] = new DualTree();
        }
        
    }

    public int next(int i) {
        i = (i + 1) % n;
        while (poly.done[i % n]) {
            i = (i + 1) % n;
        }
        return i;
    }

    public int prev(int i) {
        i--;
        if (i < 0) {
            i += n;
        }
        while (poly.done[i]) {
            i--;
            if (i < 0) {
                i += n;
            }
        }
        return i;
    }

    static double signedTriangleArea(Point2D.Double a, Point2D.Double b, Point2D.Double c) {
        //System.out.println(("Area: "));
        return ((a.x * b.y - a.y * b.x + a.y * c.x - a.x * c.y + b.x * c.y - c.x * b.y) / 2.0);
    }

    static boolean pointCompare(Point2D.Double a, Point2D.Double b) {
        if(a.x != b.x) return a.x > b.x;
        return a.y > b.y;
    }

    static boolean clockwise(Point2D.Double a, Point2D.Double b, Point2D.Double c) {
        double d = signedTriangleArea(a, b, c);
        if(d > EPSILON & -d > EPSILON) {
            int climbCount = 0;
            if (pointCompare(b, a)) climbCount++;
            if (pointCompare(c, b)) climbCount++;
            if (pointCompare(a, c)) climbCount++;
            return climbCount == 2;
        }
        return d > EPSILON;
    }

    static boolean pointInTriangle(Point2D.Double p, Point2D.Double a, Point2D.Double b, Point2D.Double c) {
        if (!clockwise(a, b, p)) {
            return false;
        }
        if (!clockwise(b, c, p)) {
            return false;
        }
        if (!clockwise(c, a, p)) {
            return false;
        }
        return true;
    }

    public boolean isEar(int a, int b, int c) {

        if (!clockwise(poly.p[a], poly.p[b], poly.p[c])) {
            return false;
        }


        for (int i = 0; i < n; i++) {
            if (i != a && i != b && i != c) {
                if (pointInTriangle(poly.p[i], poly.p[a], poly.p[b], poly.p[c])) {
                    return false;
                }
            }
        }
        return true;
    }

    public int findDualConnection(int i, int j) {
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }

        for (int k = 0; k < n; k++) {
            if (dual[k].x == i && dual[k].y == j) {
                return k;
            }
        }
        return -1;
    }

    public void fillDual(int p, int i, int n) {

        int x = findDualConnection(p, i);
        int y = findDualConnection(i, n);

        int z = findDualConnection(p, n);

        dual[i].add(x);
        dual[i].add(y);
        dual[i].add(z);
        if (x != -1) dual[x].add(i);
        if (y != -1) dual[y].add(i);
        if (z != -1) dual[z].add(i);

        if (p < n) {
            dual[i].set(p, n);
        } else {
            dual[i].set(n, p);
        }

    }

    public void triangulate() {

        int i = 0;
        int diagonals = 0;
        int prev, next;
        
        while (diagonals < n - 3) {
            prev = prev(i);
            next = next(i);

            if (isEar(prev, i, next)) {
                fillDual(prev, i, next);
                poly.done[i] = true;
                diagonals++;
                //draw(poly.p[prev].x, poly.p[prev].y, poly.p[next].x, poly.p[next].y);
            }
            i = next(i);
        }
        prev = prev(i);
        next = next(i);
        fillDual(prev, i, next);
        poly.done[i] = true;
        poly.done[prev] = true;
        poly.done[next] = true;
        midTriangle[0] = prev;
        midTriangle[1] = i;
        midTriangle[2] = next;
        

    }

    
    public void writeDual() {
        for(int i = 0; i < n; i++) {
            System.out.println(dual[i].a + " " + dual[i].b + " " + dual[i].c + 
                    " " + dual[i].x + " " + dual[i].y);
        }
    }
    
    
    
    
    public int[] threeColouring() {
        
        int i = 0;
        while(dual[i].a == -1 | dual[i].b != -1) i++;
        
        
        
        threeColouring[i] = 0;
        threeColouring[dual[i].x] = 1;
        threeColouring[dual[i].y] = 2;
        
        colourCount[0] = 1;
        colourCount[1] = 1;
        colourCount[2] = 1;
        
        dual[i].done3c = true;
        
        recColour(dual[i].a);
        
        return threeColouring;
    }
    
    private void recColour(int i) {
        dual[i].done3c = true;
        //System.out.println("bojam: " + i);
        
        if(threeColouring[i] == -1) threeColouring[i] = thirdColour(threeColouring[dual[i].x], threeColouring[dual[i].y]);
        else if(threeColouring[dual[i].x] == -1) threeColouring[dual[i].x] = thirdColour(threeColouring[i], threeColouring[dual[i].y]);
        else if(threeColouring[dual[i].y] == -1) threeColouring[dual[i].y] = thirdColour(threeColouring[i], threeColouring[dual[i].x]);
        
        //System.out.println("obojao: " + threeColouring[i] + threeColouring[dual[i].x] + threeColouring[dual[i].y]);
        
        if (dual[i].a == -1) {
            //System.out.println("Bad miss.");
            return;
        }
        if (!dual[dual[i].a].done3c) recColour(dual[i].a);
        
        if (dual[i].b == -1) return;
        if (!dual[dual[i].b].done3c) recColour(dual[i].b);
        
        if (dual[i].c == -1) return;
        if (!dual[dual[i].c].done3c) recColour(dual[i].c);
        
        
    }
    
    private int thirdColour(int i, int j) {
        int colour = 0;
        if ((i == 0 & j == 1) | (i == 1 & j == 0)) colour = 2;
        else if ((i == 1 & j == 2) | (i == 2 & j == 1)) colour = 0;
        else if ((i == 0 & j == 2) | (i == 2 & j == 0)) colour = 1;
        colourCount[colour]++;
        //System.out.println("boja: " + colour + " " + colourCount[colour]);
        return colour;
    }

    
    public int minColour() {
        int min = 0;
        if(colourCount[1] < colourCount[min]) min = 1;
        if(colourCount[2] < colourCount[min]) min = 2;
        return min;
    }
    
    
    public int numOfGuards;
    public int algSize;
    public long demo() throws OutOfMemoryError{
        long starTime = System.currentTimeMillis();

        triangulate();
        threeColouring();
        
        int minColour = minColour();

        numOfGuards = colourCount[minColour];
        algSize = dual.length;
        long endTime = System.currentTimeMillis();
        
        return endTime - starTime;
    }
    
}
