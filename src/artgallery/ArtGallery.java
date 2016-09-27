/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artgallery;

import alg.AvisToussaint;
import alg.GhoshVS;
import alg.TriSetCover;
import struct.DPolygon;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author Ivan Bestvina; ivan.bestvina@fer.hr; jmbag 0036475086
 */
public class ArtGallery {

    public static final double FACTOR = 20;
    public static final int ADD = 0;

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        if(args.length != 1) {
            System.out.println("args error");
            return;
        }
        
        int instToSolve = Integer.parseInt(args[0]);
        
        if (instToSolve == -1) {
            System.out.println("algoritmi: AvisT - Avis-Toussaint, TriSet - triangulacija + set-cover, Ghosh - algoritam S. K. Ghosha [doi:10.1016/j.dam.2009.12.004]");
            System.out.println("n - broj čuvara, t - vrijeme, s - memorija\n");
            //System.out.println("#\tTip poligona\tVelicina\tInstanca\tAvisT n\t\tAvisT t\t\tTriSet n\tTriSet t\tGhosh n\t\tGhosh t\t\tGhosh s");
            System.out.println("#\tTip poligona\tVelicina\tInstanca\tGhosh n\t\tGhosh t\t\tGhosh s");
            return;
        }
        
        analyzeAlg(instToSolve);

        //analyzeAlg(true);
        
        /*System.out.println("Total: " + i + ".\nFailed: " + errorList.size() +"." + "\nNumFormatExcs: " + numFormatExcs + ".");
        System.out.println("Failed at: " + errorList.toString());
        System.out.println("\n\ndone");*/
        //if (1 == 1) return;
        
        //String fileName = "StSerninH.pol";
        /*String fileName = "simple-50-2.pol";
        String filePath = new File("").getAbsolutePath().concat("/src/test/");
        
        //System.setIn(new FileInputStream(new File("D:\\Dropbox\\PROJEKT galerija\\ArtGallery\\src\\test\\" + fileName)));
        System.setIn(new FileInputStream(new File(filePath + fileName)));
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();

        JFrame frame = new JFrame();
        frame.setTitle("Art Gallery");
        frame.setSize(1200, 700);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        DPolygon poly = parsePoly(line);

        PolyPanel ppanel = new PolyPanel();
        ppanel.setMainPoly(poly.toPolygon());

        frame.add(ppanel);
        frame.setVisible(true);*/

        //ppanel.addGuardAvisToussaint(poly.p[0]);
        //ppanel.addGuardAvisToussaint(new Point2D.Double(137.99376869395618, 276.86372431147555));
        //ppanel.addGuardAvisToussaint(new Point2D.Double(127.94977635145187, 294.0469148941338));
        //poly.writePoly();
        //TriSetCover triSetCover = new TriSetCover(poly, ppanel);
        //triSetCover.demo();

        //AvisToussaint avisToussaint = new AvisToussaint(poly, ppanel);
        //avisToussaint.demo();

        /*for(int i = 0; i< poly.n; i++) {
         ppanel.addGuardSetCover(poly.p[i]);
         }*/
        //GhoshVS ghoshVS = new GhoshVS(poly, ppanel);
        //ghoshVS.demo();

        
        //System.out.println("done");
    }

    public static DPolygon parsePoly(String line) {
        String[] inPart = line.split(" ");
        int n = Integer.parseInt(inPart[0]);
        DPolygon poly = new DPolygon(n);
        boolean success = true;
        Double x, y;

        for (int i = 1; i <= n * 2; i += 2) {
            //for (int i = 123; i <= 152; i += 2) {
            try {
                x = resize(Long.parseLong(inPart[i].split("/")[0]) / (double) Long.parseLong(inPart[i].split("/")[1]));
                y = resize(Long.parseLong(inPart[i + 1].split("/")[0]) / (double) Long.parseLong(inPart[i + 1].split("/")[1]));
            } catch (Exception e) {
                numFormatExcs++;
                //System.out.println(e);
                return null;
            }
            
            success = success & poly.addPoint(new Point2D.Double(x, y));
        }

        if (!success) {
            System.out.println("Greska pri parsiranju poligona!");
        }

        return poly;
    }

    public static Double resize(Double d) {
        return FACTOR * (ADD + d);
    }
    static List<Integer> errorList = new LinkedList<>();
    static int numFormatExcs = 0;
    static int i = 1;
    public static void analyzeAlg(int instToSolve) throws FileNotFoundException, IOException {

        String fileName;
        BufferedReader br;
        String line;
        DPolygon poly;

        AvisToussaint avisToussaint;
        TriSetCover triSetCover;
        GhoshVS ghoshVS;

        long avgtAT;
        long avgtTSC;
        long avgtGVS;
        
        long avggAT;
        long avggTSC;
        long avggGVS;
        
        String file = new File("").getAbsolutePath().concat("/testAll.mpoly");
        br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
        
        for(int i = 0; i < instToSolve*2; i++) {
            if(br.readLine() == null) System.exit(1);
        }
            
        String polyType;
        String polySize;
        String polyId;
        long avisTime;
        long ghoshTime;
        long trisetTime;
        int avisGuards;
        int ghoshGuards;
        int trisetGuards;
        int avisSpace;
        int ghoshSpace;
        int trisetSpace;
        
        line = br.readLine();
        if(line == null) return;

        //System.out.println(file.getName());
        if(line.split("\\.")[0].split("-").length != 3) return;
        polyType = line.split("\\.")[0].split("-")[0];



        polySize = line.split("\\.")[0].split("-")[1];
        polyId = line.split("\\.")[0].split("-")[2];

        line = br.readLine();
        if(line == null) return;
        poly = parsePoly(line);
        if(poly == null) return;

        ghoshVS = new GhoshVS(poly);
        try {
            ghoshTime = ghoshVS.demo();
            ghoshGuards = ghoshVS.numOfGuards;
            ghoshSpace = ghoshVS.algSize;
        } catch (OutOfMemoryError | Exception e) {
            ghoshVS = null;
            System.gc();
            ghoshTime = -42;
            ghoshGuards = -42;
            ghoshSpace = -42;
            errorList.add(i);
            System.out.println(e.toString());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        ghoshVS = null;
        System.gc();
        /*
        avisToussaint = new AvisToussaint(poly);
        try {
            avisTime = avisToussaint.demo();
            avisGuards = avisToussaint.numOfGuards;
        } catch (OutOfMemoryError | Exception e) {
            avisToussaint = null;
            System.gc();
            avisTime = -42;
            avisGuards = -42;
            avisSpace = -42;
            errorList.add(i);
        }
        avisToussaint = null;
        System.gc();

        triSetCover = new TriSetCover(poly);
        try {
            trisetTime = triSetCover.demo();
            trisetGuards = triSetCover.numOfGuards;
        } catch (OutOfMemoryError | Exception e) {
            triSetCover = null;
            System.gc();
            trisetTime = -42;
            trisetGuards = -42;
            errorList.add(i);
        }
        triSetCover = null;
        System.gc();
        

        System.out.println(instToSolve + "\t" + polyType + "\t\t" + polySize + "\t\t" + polyId + "\t\t" + 
                avisGuards + "\t\t" + avisTime + "\t\t" + trisetGuards + "\t\t" + trisetTime + "\t\t" + 
                ghoshGuards + "\t\t" + ghoshTime + "\t\t" + ghoshSpace);
        */ 
        System.out.println(instToSolve + "\t" + polyType + "\t\t" + polySize + "\t\t" + polyId + "\t\t" +  
                ghoshGuards + "\t\t" + ghoshTime + "\t\t" + ghoshSpace);
    }
}
