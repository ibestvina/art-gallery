package struct;


public class DualTree {
    public int a, b, c;
    public int x, y;
    public boolean done3c = false;
    public boolean doneSetCov = false;

    public DualTree() {
        a = -1;
        b = -1;
        c = -1;
        x = -1;
        y = -1;
    }
    
    public void add(int i) {
        if(a == -1) {
            a = i;
            return;
        }
        if(b == -1) {
            b = i;
            return;
        }
        if(c == -1) {
            c = i;
            return;
        }
        System.out.println("Pokušaj dodjeljivanja više od 3 grane!");
    }
    
    public void set(int i, int j) {
        x = i;
        y = j;
    }
    
    public boolean isConnected (int i) {
        return (a == i | b == i | c == i);
    }
    
    public void writeDual() {
        System.out.println(a + " " + b + " " + c + " " + x + " " + y + " " + done3c);
    }
}
