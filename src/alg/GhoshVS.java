package alg;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import struct.DPolygon;
import struct.Diagonal;
import struct.SCListPart;

public class GhoshVS {

    static final double EPSILON = -0.000000001;
    static final double EPSILON2 = 0.00001;

    DPolygon poly;
    int n;

    int[] oldNewIndex;

    public List<Diagonal> diagonals = new LinkedList<>();
    public LinkedList<Point2D.Double> Lpoly = new LinkedList<>();

    Intersection intersection = new Intersection();

    public GhoshVS(DPolygon poly) {
        this.poly = new DPolygon(poly);
        n = poly.n;
        oldNewIndex = new int[n];
        for (int i = 0; i < n; i++) {
            oldNewIndex[i] = i;
        }
        for (int i = 0; i < n; i++) {
            Lpoly.add(poly.p[i]);
        }
    }

    public int next(int i, int n) {
        return ((i + 1) % n);
    }

    public int prev(int i, int n) {
        int prev = i - 1;
        if (prev < 0) prev += n;
        return prev;
    }

    public int next(int i) {
        return next(i, n);
    }

    public int prev(int i) {
        return prev(i, n);
    }

    public double signedTriangleArea(Point2D.Double a, Point2D.Double b, Point2D.Double c) {
        return ((a.x * b.y - a.y * b.x + a.y * c.x - a.x * c.y + b.x * c.y - c.x * b.y) / 2.0);
    }

    /*
     * 
     * 1. dio: na listu diagonals staviti sve validne dijagonale
     * 
     */
    public boolean isDiagonalInside(int i, int j) {

        if (i == j | i == prev(j) | i == next(j)) return false; //RAZMISLITI

        Point2D.Double v1 = new Point2D.Double(poly.p[i].x - poly.p[next(i)].x, -(poly.p[i].y - poly.p[next(i)].y));
        Point2D.Double v2 = new Point2D.Double(poly.p[i].x - poly.p[prev(i)].x, -(poly.p[i].y - poly.p[prev(i)].y));
        Point2D.Double v3 = new Point2D.Double(poly.p[i].x - poly.p[j].x, -(poly.p[i].y - poly.p[j].y));

        double r1 = Math.atan2(v1.y, v1.x);
        double r2 = Math.atan2(v2.y, v2.x);
        double r3 = Math.atan2(v3.y, v3.x);

        r1 -= r3;
        r2 -= r3;

        if (r1 < 0) r1 += 2 * Math.PI;
        if (r2 <= 0) r2 += 2 * Math.PI;
        return (r1 < r2);
    }

    public boolean diagonalIntersects(int i, int j) {
        Line2D diagonal = new Line2D.Double(poly.p[i].x, poly.p[i].y, poly.p[j].x, poly.p[j].y);
        Line2D edge = new Line2D.Double();

        for (int k = 0; k < n; k++) {
            if (k == prev(i) | k == i | k == prev(j) | k == j) continue;
            edge.setLine(poly.p[k].x, poly.p[k].y, poly.p[next(k)].x, poly.p[next(k)].y);
            if (diagonal.intersectsLine(edge)) return true;
        }
        return false;
    }

    public boolean isRealDiagonal(int i, int j) {
        if (!isDiagonalInside(i, j)) return false;
        if (diagonalIntersects(i, j)) return false;
        return true;
    }

    public void fillRealDiagonals() {
        
        for (int i = 0; i < n; i++) {
            for (int j = i + 2; j < n; j++) {
                if (isRealDiagonal(i, j)) {
                    diagonals.add(new Diagonal(i, j, poly.p[i], poly.p[j]));
                }
            }
        }
    }

    /*
     * 
     * 2. dio: na listu diagonals dodati produzetke dijagonala
     * 
     */
    public class DiagonalEndPoints {

        int ai, bi;
        boolean aIsEnd, bIsEnd;
        double k, l;

        //solve aIsEnd and bIsEnd for diagonal
        public void solveEndsDiagonal() {
            aIsEnd = true;
            bIsEnd = true;
            //dijagonala vertikalna
            if (poly.p[ai].x == poly.p[bi].x) {

                if ((poly.p[prev(ai)].x < poly.p[ai].x) & (poly.p[next(ai)].x < poly.p[ai].x)
                        | (poly.p[prev(ai)].x > poly.p[ai].x) & (poly.p[next(ai)].x > poly.p[ai].x)) {
                    aIsEnd = false;
                }
                if ((poly.p[prev(bi)].x < poly.p[bi].x) & (poly.p[next(bi)].x < poly.p[bi].x)
                        | (poly.p[prev(bi)].x > poly.p[bi].x) & (poly.p[next(bi)].x > poly.p[bi].x)) {
                    bIsEnd = false;
                }
                return;
            }

            k = (poly.p[ai].y - poly.p[bi].y) / (poly.p[ai].x - poly.p[bi].x);
            l = poly.p[ai].y - k * poly.p[ai].x;

            if ((poly.p[prev(ai)].y < (k * poly.p[prev(ai)].x + l))
                    & (poly.p[next(ai)].y < (k * poly.p[next(ai)].x + l))
                    | (poly.p[prev(ai)].y > (k * poly.p[prev(ai)].x + l))
                    & (poly.p[next(ai)].y > (k * poly.p[next(ai)].x + l))) {
                aIsEnd = false;
            }

            if ((poly.p[prev(bi)].y < (k * poly.p[prev(bi)].x + l))
                    & (poly.p[next(bi)].y < (k * poly.p[next(bi)].x + l))
                    | (poly.p[prev(bi)].y > (k * poly.p[prev(bi)].x + l))
                    & (poly.p[next(bi)].y > (k * poly.p[next(bi)].x + l))) {
                bIsEnd = false;
            }
        }

        private boolean clockwise(Point2D.Double a, Point2D.Double b, Point2D.Double c) {
            return (((a.x * b.y - a.y * b.x + a.y * c.x - a.x * c.y + b.x * c.y - c.x * b.y) / 2.0) > EPSILON);
        }

        //solve aIsEnd and bIsEnd for edge
        public void solveEndsEdge() {
            aIsEnd = false;
            if (clockwise(poly.p[prev(ai)], poly.p[ai], poly.p[next(ai)])) {
                aIsEnd = true;
            }

            bIsEnd = false;
            if (clockwise(poly.p[prev(bi)], poly.p[bi], poly.p[next(bi)])) {
                bIsEnd = true;
            }
        }
    }

    public boolean correctOrder(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        if (p1.x == p2.x) {
            if ((p1.y <= p2.y & p2.y <= p3.y) | (p1.y >= p2.y & p2.y >= p3.y)) {
                return true;
            }
            return false;
        }
        if ((p1.x <= p2.x & p2.x <= p3.x) | (p1.x >= p2.x & p2.x >= p3.x)) {
            return true;
        }
        return false;
    }

    //Varijable koje se koriste samo u produljivanju dijagonala i rubova.
    List<Diagonal> newDiagonals = new LinkedList<>();
    DiagonalEndPoints endPoints = new DiagonalEndPoints();
    Line2D.Double edge = new Line2D.Double();
    Point2D.Double aNear = new Point2D.Double();
    Point2D.Double bNear = new Point2D.Double();

    //index prvog kraja ruba s kojim se sijece produljenje dijagonale na stranu a ili b
    int aIndex;
    int bIndex;

    public void extendLine(boolean isEdge, Diagonal diagonal) {
        endPoints.ai = diagonal.ai;
        endPoints.bi = diagonal.bi;

        //rijesi koje krajeve treba produljiti
        //ako je dan RUB
        if (isEdge) endPoints.solveEndsEdge();
        //ako je dana DIJAGONALA
        if (!isEdge) endPoints.solveEndsDiagonal();

        //ako ne treba ni jedan
        if (endPoints.aIsEnd & endPoints.bIsEnd) return;

        aIndex = -1;
        bIndex = -1;

        //za svaki rub OSIM PRILEZECIH DIJAGONALI!
        for (int i = 0; i < n; i++) {

            if (diagonal.ai == i | diagonal.ai == next(i, n) | diagonal.bi == i | diagonal.bi == next(i, n)) {
                continue;
            }

            //pronadji sjeciste s rubom
            edge.setLine(poly.p[i], poly.p[next(i)]);
            intersection.setLines(diagonal, edge);

            //ako ne sijece, nastavi
            if (!intersection.solve()) continue;

            //ako treba produljiti a, i jos za njega nemamo definirano sjeciste
            if (aIndex == -1 & !endPoints.aIsEnd) {
                //ako je sjeciste sa strane a
                if (correctOrder(poly.p[diagonal.bi], poly.p[diagonal.ai], intersection.intersection)) {
                    aIndex = i;
                    aNear.setLocation(intersection.intersection);
                    continue;
                }
            }

            //ako treba produljiti b, i jos za njega nemamo definirano sjeciste
            if (bIndex == -1 & !endPoints.bIsEnd) {
                //ako je sjeciste sa strane b
                if (correctOrder(poly.p[diagonal.ai], poly.p[diagonal.bi], intersection.intersection)) {
                    bIndex = i;
                    bNear.setLocation(intersection.intersection);
                    continue;
                }
            }

            //AKO JE VEC UNESEN aNear ILI bNear!
            //ako treba produljiti a, ali vec postoji neko sjeciste
            if (aIndex != -1 & !endPoints.aIsEnd) {
                //ako je sjeciste sa strane a
                if (correctOrder(poly.p[diagonal.bi], poly.p[diagonal.ai], intersection.intersection)) {
                    //ako je sjeciste blize od dosadasnjeg
                    if (poly.p[diagonal.ai].distance(intersection.intersection)
                            < poly.p[diagonal.ai].distance(aNear)) {
                        aIndex = i;
                        aNear.setLocation(intersection.intersection);
                        continue;
                    }
                }
            }

            //ako treba produljiti b, ali vec postoji neko sjeciste
            if (bIndex != -1 & !endPoints.bIsEnd) {
                //ako je sjeciste sa strane b
                if (correctOrder(poly.p[diagonal.ai], poly.p[diagonal.bi], intersection.intersection)) {
                    //ako je sjeciste blize od dosadasnjeg
                    if (poly.p[diagonal.bi].distance(intersection.intersection)
                            < poly.p[diagonal.bi].distance(bNear)) {
                        bIndex = i;
                        bNear.setLocation(intersection.intersection);
                        continue;
                    }
                }
            }
        }

        //pronadjeno je (nadam se) najblize sjeciste i od a i od b (ako za oba treba produljit) pa dodajem na listu dijagonala i vrhova
        if (!endPoints.aIsEnd) {
            newDiagonals.add(new Diagonal(diagonal.ai, aIndex + 1, poly.p[diagonal.ai], aNear));
            addToLPoly(aIndex, new Point2D.Double(aNear.x, aNear.y));
        }
        if (!endPoints.bIsEnd) {
            newDiagonals.add(new Diagonal(diagonal.bi, bIndex + 1, poly.p[diagonal.bi], bNear));
            addToLPoly(bIndex, new Point2D.Double(bNear.x, bNear.y));
        }
    }

    public void addToLPoly(int index, Point2D.Double pt) {
        if (Lpoly.contains(pt)) return;
        List<Point2D.Double> list = new LinkedList<>();
        int pointsBetween;
        if (index == n - 1) pointsBetween = Lpoly.size() - oldNewIndex[index];
        else pointsBetween = oldNewIndex[index + 1] - oldNewIndex[index];
        for (int i = 0; i < pointsBetween; i++) {
            list.add(Lpoly.get(oldNewIndex[index] + i));
        }
        Point2D.Double firstPoint = poly.p[index];

        list.sort((Point2D.Double o1, Point2D.Double o2) -> {
            if (firstPoint.distance(o1) < firstPoint.distance(o2)) return -1;
            if (firstPoint.distance(o1) > firstPoint.distance(o2)) return 1;
            return 0;
        });
        list.add(poly.p[next(index)]);
        int newIndex;
        for (newIndex = 0; newIndex < list.size() - 1; newIndex++) {
            if (correctOrder(list.get(newIndex), pt, list.get(newIndex + 1)))
                break;
        }
        //ppanel.testPoint = pt;
        //ppanel.repaint();
        Lpoly.add(oldNewIndex[index] + newIndex + 1, pt);

        for (int i = index + 1; i < n; i++) {
            oldNewIndex[i]++;
        }
    }

    public void diagonalsExtend() {
        newDiagonals.clear();
        //za svaku dijagonalu
        for (Diagonal diagonal : diagonals) {
            extendLine(false, diagonal);
        }
        diagonals.clear();
        diagonals.addAll(newDiagonals);
    }

    /*
     * 
     * 3. dio: na listu diagonals dodati produzetke rubova
     * 
     */
    public void edgesExtend() {
        newDiagonals.clear();
        //za svaki rub
        for (int i = 0; i < n; i++) {
            extendLine(true, new Diagonal(i, next(i), poly.p[i], poly.p[next(i)]));
        }
        diagonals.addAll(newDiagonals);
    }

    /*
     * 
     * 4. dio: podijeliti poligon na komponente
     * 
     */
    List<LinkedList<Point2D.Double>> polyPartition = new LinkedList<>();

    public List<LinkedList<Point2D.Double>> cutPoly(LinkedList<Point2D.Double> part, Line2D.Double line) {
        int n = part.size();
        Point2D.Double[] poly = part.toArray(new Point2D.Double[0]);
        Line2D.Double edge = new Line2D.Double();
        List<Integer> edgeIndex = new LinkedList<>();

        //ppanel.testDiagonal = line;
        //ppanel.testPolyPart = part;
        List<LinkedList<Point2D.Double>> result = new LinkedList<>();
        for (int i = 0; i < n - 1; i++) {
            edge.setLine(poly[i], poly[i + 1]);
            //ppanel.testPoint = poly[i];
            //ppanel.repaint();
            if (Intersection.doLinesIntersect(line, edge)) {

                edgeIndex.add(i);
            }
        }
        edge.setLine(poly[n - 1], poly[0]);
        if (Intersection.doLinesIntersect(line, edge)) {
            edgeIndex.add(n - 1);
        }

        if (edgeIndex.isEmpty()) {
            result.add(part);
            return result;
        }

        if (edgeIndex.size() == 1) {
            //ppanel.addLine(line);
            //ppanel.addPoly(part);
            //ppanel.testPolyPart = part;
            //ppanel.testDiagonal = line;
            //ppanel.repaint();
            //System.out.println("Notice: Only found one edge!");
            result.add(part);
            return result;
        }
        LinkedList<Point2D.Double> firstP = new LinkedList<>();
        LinkedList<Point2D.Double> secondP = new LinkedList<>();

        if (edgeIndex.size() == 2) {
            int i1 = edgeIndex.get(0);
            int i2 = edgeIndex.get(1);
            if (i1 > i2) {
                int temp = i1;
                i1 = i2;
                i2 = temp;
            }
            edge.setLine(poly[i1], poly[next(i1, n)]);
            intersection.setLines(line, edge);
            intersection.solveSegments();
            Point2D.Double newPoint1 = new Point2D.Double(intersection.intersection.x, intersection.intersection.y);

            edge.setLine(poly[i2], poly[next(i2, n)]);
            intersection.setLines(line, edge);
            intersection.solveSegments();
            Point2D.Double newPoint2 = new Point2D.Double(intersection.intersection.x, intersection.intersection.y);

            //ako dijagonala prolazi bas kroz vrh izmedju dva nadjena ruba
            if (newPoint1.equals(newPoint2)) {
                result.add(part);
                return result;
            }

            //punjenje prvog dijela
            Point2D.Double pointToAdd;
            if (!firstP.contains(newPoint1)) firstP.add(newPoint1);
            for (int i = next(i1, n); i != i2; i = next(i, n)) {
                pointToAdd = new Point2D.Double(poly[i].x, poly[i].y);
                if (!firstP.contains(pointToAdd)) firstP.add(pointToAdd);
            }
            pointToAdd = new Point2D.Double(poly[i2].x, poly[i2].y);
            if (!firstP.contains(pointToAdd)) firstP.add(pointToAdd);
            if (!firstP.contains(newPoint2)) firstP.add(newPoint2);

            //punjenje drugog dijela
            if (!secondP.contains(newPoint2)) secondP.add(newPoint2);
            for (int i = next(i2, n); i != i1; i = next(i, n)) {
                pointToAdd = new Point2D.Double(poly[i].x, poly[i].y);
                if (!secondP.contains(pointToAdd)) secondP.add(pointToAdd);
            }
            pointToAdd = new Point2D.Double(poly[i1].x, poly[i1].y);
            if (!secondP.contains(pointToAdd)) secondP.add(pointToAdd);
            if (!secondP.contains(newPoint1)) secondP.add(newPoint1);

            /*ppanel.testDiagonal = line;
            ppanel.testPolyPart = part;
            ppanel.repaint();
            ppanel.testPolyPart = firstP;
            ppanel.repaint();
            ppanel.testPolyPart = secondP;
            ppanel.repaint();*/

            result.add(firstP);
            result.add(secondP);
            return result;
        }

        if (edgeIndex.size() == 3) {
            int pIndex = -1;
            int eIndex = -1;

            //sjecista s rubovima
            edge.setLine(poly[edgeIndex.get(0)], poly[next(edgeIndex.get(0), n)]);
            intersection.setLines(line, edge);
            intersection.solveSegments();
            if (intersection.isPartOf) {
                result.add(part);
                return result;
            }
            Point2D.Double newPoint1 = new Point2D.Double(intersection.intersection.x, intersection.intersection.y);

            edge.setLine(poly[edgeIndex.get(1)], poly[next(edgeIndex.get(1), n)]);
            intersection.setLines(line, edge);
            intersection.solveSegments();
            if (intersection.isPartOf) {
                result.add(part);
                return result;
            }
            Point2D.Double newPoint2 = new Point2D.Double(intersection.intersection.x, intersection.intersection.y);

            edge.setLine(poly[edgeIndex.get(2)], poly[next(edgeIndex.get(2), n)]);
            intersection.setLines(line, edge);
            intersection.solveSegments();
            if (intersection.isPartOf) {
                result.add(part);
                return result;
            }
            Point2D.Double newPoint3 = new Point2D.Double(intersection.intersection.x, intersection.intersection.y);

            //koja dva ruba su zapravo vrh?
            Point2D.Double newPoint;
            if (newPoint1.equals(newPoint2)) {
                pIndex = edgeIndex.get(1);
                eIndex = edgeIndex.get(2);
                newPoint = newPoint3;
            } else if (newPoint2.equals(newPoint3)) {
                pIndex = edgeIndex.get(2);
                eIndex = edgeIndex.get(0);
                newPoint = newPoint1;
            } else {
                pIndex = edgeIndex.get(0);
                eIndex = edgeIndex.get(1);
                newPoint = newPoint2;
            }

            //popuni prvi dio
            Point2D.Double pointToAdd;
            for (int i = pIndex; i != eIndex; i = next(i, n)) {
                pointToAdd = new Point2D.Double(poly[i].x, poly[i].y);
                if (!firstP.contains(pointToAdd)) firstP.add(pointToAdd);
            }
            pointToAdd = new Point2D.Double(poly[eIndex].x, poly[eIndex].y);
            if (!firstP.contains(pointToAdd)) firstP.add(pointToAdd);
            if (!firstP.contains(newPoint)) firstP.add(newPoint);

            //popuni drugi dio
            pointToAdd = new Point2D.Double(poly[pIndex].x, poly[pIndex].y);
            if (!secondP.contains(pointToAdd)) secondP.add(pointToAdd);
            if (!secondP.contains(newPoint)) secondP.add(newPoint);
            for (int i = next(eIndex, n); i != pIndex; i = next(i, n)) {
                pointToAdd = new Point2D.Double(poly[i].x, poly[i].y);
                if (!secondP.contains(pointToAdd)) secondP.add(pointToAdd);
            }

            /*ppanel.testDiagonal = line;
            ppanel.testPolyPart = part;
            ppanel.repaint();
            ppanel.testPolyPart = firstP;
            ppanel.repaint();
            ppanel.testPolyPart = secondP;
            ppanel.repaint();*/

            result.add(firstP);
            result.add(secondP);
            return result;
        }

        if (edgeIndex.size() == 4) {
            if (next(edgeIndex.get(0), n) != edgeIndex.get(1)
                    & next(edgeIndex.get(1), n) != edgeIndex.get(2)
                    & next(edgeIndex.get(2), n) != edgeIndex.get(3)
                    & next(edgeIndex.get(3), n) != edgeIndex.get(0)) {
                //System.out.println("ERROR! 4 edges, but unexpected order!");
            }
            int i1 = -1;
            int i2 = -1;

            //int found = 0;
            /*for (int i = 0; i < n; i++) {
             if (part.get(i).equals(line.getP1())) {
             i1 = i;
             //found++;
             }
             if (part.get(i).equals(line.getP2())) {
             i2 = i;
             //found++;
             }
             //if (found == 2) break;
             }*/
            for (int i = 0; i < 4; i++) {
                if (line.contains(poly[edgeIndex.get(i)])) {
                    if (i1 == -1) {
                        i1 = edgeIndex.get(i);
                        continue;
                    }
                    i2 = edgeIndex.get(i);
                }
            }

            //pokusaj s tockama blizu
            if (i1 == -1 | i2 == -1) {
                double minDist1 = Double.MAX_VALUE;
                double minDist2 = Double.MAX_VALUE;

                for (int i = 0; i < 4; i++) {
                    if (line.getP1().distance(poly[edgeIndex.get(i)]) < minDist1) {
                        minDist1 = line.getP1().distance(poly[edgeIndex.get(i)]);
                        i1 = edgeIndex.get(i);
                    }
                    if (line.getP2().distance(poly[edgeIndex.get(i)]) < minDist2) {
                        minDist2 = line.getP2().distance(poly[edgeIndex.get(i)]);
                        i2 = edgeIndex.get(i);
                    }
                }
            }

            if (i1 == -1 | i2 == -1) {
                //ppanel.testDiagonal = line;
                //ppanel.testPolyPart = part;
                //System.out.println("Notice: Diagonal end(s) not found!");
                result.add(part);
                return result;
            }
            if (i1 > i2) {
                int temp = i1;
                i1 = i2;
                i2 = temp;
            }
            Point2D.Double pointToAdd;
            //popuni prvi
            for (int i = i1; i != i2; i = next(i, n)) {
                pointToAdd = new Point2D.Double(poly[i].x, poly[i].y);
                if (!firstP.contains(pointToAdd)) firstP.add(pointToAdd);
            }
            pointToAdd = new Point2D.Double(poly[i2].x, poly[i2].y);
            if (!firstP.contains(pointToAdd)) firstP.add(pointToAdd);

            //popuni drugi
            for (int i = i2; i != i1; i = next(i, n)) {
                pointToAdd = new Point2D.Double(poly[i].x, poly[i].y);
                if (!secondP.contains(pointToAdd)) secondP.add(pointToAdd);
            }
            pointToAdd = new Point2D.Double(poly[i1].x, poly[i1].y);
            if (!secondP.contains(pointToAdd)) secondP.add(pointToAdd);

            /*ppanel.testDiagonal = line;
            ppanel.testPolyPart = part;
            ppanel.repaint();
            ppanel.testPolyPart = firstP;
            ppanel.repaint();
            ppanel.testPolyPart = secondP;
            ppanel.repaint();*/

            result.add(firstP);
            result.add(secondP);
            return result;
        }
        //ppanel.testDiagonal = line;
        //ppanel.testPolyPart = part;

        //ppanel.repaint();

        //System.out.println("Notice: edge count = " + edgeIndex.size());
        result.add(part);
        return result;
    }

    public void partitionPoly() {

        polyPartition.add(Lpoly);
        List<LinkedList<Point2D.Double>> newPolyPartition = new LinkedList<>();
        int i = 0;
        for (Diagonal diagonal : diagonals) {
            //if(i++%10 == 0) System.out.println("i: "+i);
            for (LinkedList<Point2D.Double> part : polyPartition) {
                newPolyPartition.addAll(cutPoly(part, diagonal));
            }
            polyPartition.clear();
            polyPartition.addAll(newPolyPartition);
            newPolyPartition.clear();
        }
    }


    /*
     * 
     * 5. dio: pripremiti set cover za komponente
     * 
     */
    
    public void cleanPolyPartition() {
        //System.out.println("Before clean size: " + polyPartition.size());
        for (int i = 0; i < polyPartition.size(); i++) {
            //if(i%100 == 0) System.out.println("clean: " + i);
            if(polyPartition.get(i).size() < 3) {
                polyPartition.remove(i);
                i--;
            }
        }
        //System.out.println("After clean size: " + polyPartition.size());
    }
    
    List<SCListPart> scList = new ArrayList<>();
    int numOfParts;

    public void fillScList() {
        numOfParts = polyPartition.size();
        LinkedList<Integer> vList = new LinkedList<>();

        Line2D.Double line = new Line2D.Double();
        Line2D.Double edge = new Line2D.Double();

        Point2D.Double pt1;
        Point2D.Double pt2 = new Point2D.Double();

        boolean addPart;
        int partIndex;
        
        
       
        //nadji min set cover
        for (int i = 0; i < n; i++) {
            //System.out.println("sc: "+i);
            pt1 = poly.p[i];
            partIndex = 0;
            for (LinkedList<Point2D.Double> pol : polyPartition) {
                if (pol.size() < 3) {
                    //System.out.println("Poly part size less than 3!");
                    continue;
                }
                pt2.setLocation((pol.getFirst().x + pol.get(1).x + pol.getLast().x) / 3,
                        (pol.getFirst().y + pol.get(1).y + pol.getLast().y) / 3);

                line.setLine(pt1, pt2);
                addPart = true;
                for (int j = 1; j < n; j++) {
                    if (i != j - 1 && i != j) {
                        edge.setLine(poly.p[j - 1], poly.p[j]);
                        if (Intersection.doLinesIntersect(line, edge)) {
                            //ppanel.testDiagonal = line;
                            //ppanel.testPoint = poly.p[j];
                            addPart = false;
                            break;
                        }
                    }
                }
                if (addPart) {
                    vList.add(partIndex);
                }
                partIndex++;
            }
            scList.add(new SCListPart(i, vList));
            vList = new LinkedList<>();

        }
    }

    /*
     * 
     * 6. dio: odraditi set cover za komponente
     * 
     */
    boolean[] done;
    List<Integer> result = new LinkedList<>();

    public void setCoverPoly() {
        
        //sortiraj listu ((o2, o1) -> padajuce)
        scList.sort((SCListPart sc1, SCListPart sc2) -> Integer.compare(sc2.list.size(), sc1.list.size()));
        
        /*System.out.println("SC LISTS: ");
        for (SCListPart sclp : scList) {
            System.out.println(sclp.index + ". " + Arrays.toString(sclp.list.toArray()));
        }
        System.out.println("");*/
        
        done = new boolean[polyPartition.size()];
        Arrays.fill(done, false);

        int maxNew;
        int maxInd = -1;
        int newHere;
        int scSize = scList.size();
        int partsDone = 0;
        
        //System.out.println("numOfParts: "+numOfParts);
        while (partsDone < numOfParts) {
            //System.out.println("partsdone: " + partsDone + " (" + (numOfParts-partsDone) + ")");
            maxNew = 0;
            for (int i = 0; i < scSize; i++) {
                newHere = 0;
                for (Integer p : scList.get(i).list) {
                    if (!done[p]) newHere++;
                }
                if (newHere > maxNew) {
                    maxNew = newHere;
                    maxInd = i;
                }
                if (i < scSize - 1) {
                    if (maxNew >= scList.get(i + 1).list.size()) break;
                }
            }
            result.add(scList.get(maxInd).index);
            partsDone += maxNew;
            for (Integer p : scList.get(maxInd).list) {
                done[p] = true;
            }
            scList.remove(maxInd);
            scSize--;

        }
    }


    public int numOfGuards;
    public int algSize;
    public long demo() {
        long starTime = System.currentTimeMillis();

        fillRealDiagonals();
        //System.out.println("1");
        diagonalsExtend();
        //System.out.println("2");
        edgesExtend();
        //System.out.println("3");
        algSize = diagonals.size();
        //System.out.println("dijagonale:" + algSize);
        //System.out.println("===================================================");
        partitionPoly();
        //System.out.println("4");
        algSize += polyPartition.size();
        
        cleanPolyPartition();
        //System.out.println("5 (clean)");
        
        fillScList();
        //System.out.println("6");
        setCoverPoly();

        numOfGuards = result.size();
        long endTime = System.currentTimeMillis();
        return endTime - starTime;
    }

}
