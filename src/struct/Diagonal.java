/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struct;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Ivan Bestvina; ivan.bestvina@fer.hr; jmbag 0036475086
 */
public class Diagonal extends Line2D.Double{
    //index in poly
    public int ai, bi;

    public Diagonal(int ai, int bi, Line2D line) {
        super(line.getP1(), line.getP2());
        this.ai = ai;
        this.bi = bi;
    }
    
    public Diagonal(int ai, int bi, Point2D pa, Point2D pb) {
        super(pa, pb);
        this.ai = ai;
        this.bi = bi;
    }

    public Diagonal(int ai, int bi, double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);
        this.ai = ai;
        this.bi = bi;
    }
    
    public boolean isMyEnd(Point2D.Double pt) {
        return ((pt.x == x1 & pt.y == y1) | (pt.x == x2 & pt.y == y2));
    }
    
    @Override
    public Point2D.Double getP1() {
        return new Point2D.Double(x1, y1);
    }
    public Point2D.Double getP2() {
        return new Point2D.Double(x2, y2);
    }
}
