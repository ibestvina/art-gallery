/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alg;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Objects;

/**
 *
 * Za rub i pravac (liniju/dijagonalu), nađi sjeciste i vrati true. Ako ne
 * postoji, vrati false. Sjeciste zapisano u intersection.
 */
public class Intersection {

    Line2D.Double line = new Line2D.Double();
    Line2D.Double edge = new Line2D.Double();

    double lineK, lineL;
    double edgeK, edgeL;

    boolean lIsVertical, eIsVertical;
    boolean isPartOf;

    Point2D.Double intersection = new Point2D.Double();

    public void setLines(Line2D line, Line2D edge) {
        this.line.setLine(line);
        this.edge.setLine(edge);
        isPartOf = false;
    }

    public void solveEqn() {
        lIsVertical = false;
        if (line.x1 == line.x2) {
            lIsVertical = true;
        }

        eIsVertical = false;
        if (edge.x1 == edge.x2) {
            eIsVertical = true;
        }

        if (!lIsVertical) {
            lineK = (line.y1 - line.y2) / (line.x1 - line.x2);
            lineL = line.y1 - lineK * line.x1;
        }

        if (!eIsVertical) {
            edgeK = (edge.y1 - edge.y2) / (edge.x1 - edge.x2);
            edgeL = edge.y1 - edgeK * edge.x1;
        }
    }
    
    
    public boolean solve() {
        solveEqn();

        double x, y;

        if (lIsVertical & eIsVertical) {
            if (line.x1 != edge.x1) {
                return false;
            }
            if (line.y1 < edge.y1) {
                if (edge.y1 < edge.y2) {
                    intersection.setLocation(edge.x1, edge.y1);
                    return true;
                }
                intersection.setLocation(edge.x2, edge.y2);
                return true;
            }
            if (line.y1 > edge.y1) {
                if (edge.y1 > edge.y2) {
                    intersection.setLocation(edge.x1, edge.y1);
                    return true;
                }
                intersection.setLocation(edge.x2, edge.y2);
                return true;
            }
        }

        if (lIsVertical) {
            if (((edge.x1 < line.x1) & (edge.x2 < line.x1))
                    | ((edge.x1 > line.x1) & (edge.x2 > line.x1))) {
                return false;
            }

            intersection.setLocation(line.x1, (edgeK * line.x1 + edgeL));
            return true;
        }

        if (eIsVertical) {
            x = edge.x1;
            y = lineK * x + lineL;
            if ((edge.y1 < y & edge.y2 < y) | (edge.y1 > y & edge.y2 > y)) {
                return false;
            }
            intersection.setLocation(x, y);
            return true;
        }

        if (lineK == edgeK) {
            if (lineL != edgeL) {
                return false;
            }
            if (line.x1 < edge.x1) {
                if (edge.x1 < edge.x2) {
                    intersection.setLocation(edge.x1, edge.y1);
                    return true;
                }
                intersection.setLocation(edge.x2, edge.y2);
                return true;
            }
            if (line.x1 > edge.x1) {
                if (edge.x1 > edge.x2) {
                    intersection.setLocation(edge.x1, edge.y1);
                    return true;
                }
                intersection.setLocation(edge.x2, edge.y2);
                return true;
            }
        }

        x = (lineL - edgeL) / (edgeK - lineK);
        if ((edge.x1 < x & edge.x2 < x) | (edge.x1 > x & edge.x2 > x)) {
            return false;
        }

        y = lineK * x + lineL;

        intersection.setLocation(x, y);
        return true;

    }

    
        //TO DO, KOLINEARNOST!!!!!!!!!

    public boolean solveSegments() {
        if (!doLinesIntersect(line, edge)) return false;
        solveEqn();

        //zajednicki krajevi?
        if (line.getP1().equals(edge.getP1()) | line.getP1().equals(edge.getP2())) {
            intersection.setLocation(line.getP1());
            return true;
        }
        if (line.getP2().equals(edge.getP1()) | line.getP2().equals(edge.getP2())) {
            intersection.setLocation(line.getP2());
            return true;
        }
        
        //jedna dio druge?
        if ((edge.contains(line.getP1()) & edge.contains(line.getP2())) |
                (line.contains(edge.getP1()) & line.contains(edge.getP2()))) {
            isPartOf = true;
            return true;
        }
        

        double x, y;

        if (lIsVertical & eIsVertical) {
            if (line.x1 != edge.x1) {
                return false;
            } else {
                intersection.setLocation(line.getP1());
                return true;
            }

            //OVAJ KOD TRENUTNO NIŠTA NE RADI
            //kada se i donji TODO napuni: provjerava preklapaju li se vertikalne linije
            /*
            Double ly1, ly2, ey1, ey2;

            if (line.y1 < line.y2) {
                ly1 = line.y1;
                ly2 = line.y2;
            } else {
                ly1 = line.y2;
                ly2 = line.y1;
            }
            if (edge.y1 < edge.y2) {
                ey1 = edge.y1;
                ey2 = edge.y2;
            } else {
                ey1 = edge.y2;
                ey2 = edge.y1;
            }

            if (ly1 > ey2 | ey1 > ly2) {
                return false;
            }
            //SLUCAJEVI KADA SE LINIJE DJELOMICNO PREKLAPAJU (ILI JE JEDNA CIJELA UNUTAR DRUGE)
            //NISU POSEBNO OBRAĐENI JER TRENUTNO TA FUNKCIONALNOST NIJE POTREBNA!!!
                    */
        }

        if (lIsVertical) {
            intersection.setLocation(line.x1, (edgeK * line.x1 + edgeL));
            return true;
        }

        if (eIsVertical) {
            intersection.setLocation(edge.x1, (lineK * edge.x1 + lineL));
            return true;
        }

        if (lineK == edgeK) {
            intersection.setLocation(line.getP1());
            return true;
        }

        x = (lineL - edgeL) / (edgeK - lineK);
        y = lineK * x + lineL;

        intersection.setLocation(x, y);
        return true;

    }
    
    public static boolean doLinesIntersect(Line2D.Double l1, Line2D.Double l2) {
        if (l1.x1 > l2.x1 & l1.x1 > l2.x2 & l1.x2 > l2.x1 & l1.x2 > l2.x2) {
            return false;
        }
        if (l1.x1 < l2.x1 & l1.x1 < l2.x2 & l1.x2 < l2.x1 & l1.x2 < l2.x2) {
            return false;
        }
        if (l1.y1 > l2.y1 & l1.y1 > l2.y2 & l1.y2 > l2.y1 & l1.y2 > l2.y2) {
            return false;
        }
        if (l1.y1 < l2.y1 & l1.y1 < l2.y2 & l1.y2 < l2.y1 & l1.y2 < l2.y2) {
            return false;
        }
        return l1.intersectsLine(l2);
    }

}
