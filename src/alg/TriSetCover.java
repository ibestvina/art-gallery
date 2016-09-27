package alg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import struct.DPolygon;

public class TriSetCover extends AvisToussaint {

    class VertexTriangles {

        public List<Integer> triangles = new ArrayList<>();
        
        public int numUndone;

        public void add(int i) {
            triangles.add(i);
        }

        public void update() {
            numUndone = 0;
            for (Integer triangle : triangles) {
                if (!dual[triangle].doneSetCov) numUndone++;
            }
        }
        
        public void allDone() {
            for (Integer triangle : triangles) {
                dual[triangle].doneSetCov = true;
                numUndone = 0;
            }
        }
    }
    
    List<Integer> solution = new LinkedList<>();
    
    VertexTriangles[] triSet;

    
    public int numDone = 0;

    public TriSetCover(DPolygon poly) {
        super(poly);
        triSet  = new VertexTriangles[n];
        for (int i = 0; i < n; i++) {
            triSet[i] = new VertexTriangles();
        }
    }
    
    
    public void fillTriSet() {
        for (int i = 0; i < n; i++) {
            if(dual[i].x != -1) triSet[i].add(i);
            for (int j = 0; j < n; j++) {
                if(dual[j].x == i | dual[j].y == i) triSet[i].add(j);
            }
        }
    }
    
    public void updateSet() {
        for (VertexTriangles vert : triSet) {
            vert.update();
        }
    }
    
    public int maxSet() {
        int max = -1;
        int index = -1;
        
        for (int i = 0; i < n; i++) {
            if(triSet[i].numUndone > max) {
                max = triSet[i].numUndone;
                index = i;
            }
        }
        return index;
    }
    
    public void addMaxCover() {
        int maxIndex = maxSet();
        numDone += triSet[maxIndex].numUndone;
        solution.add(maxIndex);
        triSet[maxIndex].allDone();
        updateSet();
    }
    
    public void setCover(){
        while(numDone < n-2) {
            addMaxCover();
        }
    }
    

    @Override
    public long demo() throws OutOfMemoryError{
        long starTime = System.currentTimeMillis();
        
        triangulate();
        fillTriSet();
        updateSet();
        setCover();
        //solutionToPanel();
        numOfGuards = solution.size();
        //System.out.println("SetCover number of guards: " + solution.size());
        long endTime = System.currentTimeMillis();
        //System.out.println("SetCover time: " + (endTime - starTime) + "ms");
        return endTime - starTime;
    }

}
