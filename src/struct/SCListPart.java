package struct;

import java.util.LinkedList;
import java.util.List;

public class SCListPart {
    public List<Integer> list = new LinkedList<>();
    public int index;

    public SCListPart(int index, LinkedList<Integer> l) {
        this.index = index;
        list.addAll(l);
    }
    
}
