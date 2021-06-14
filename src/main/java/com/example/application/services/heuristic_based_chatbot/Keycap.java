import java.util.LinkedList;
import java.util.List;

public class Keycap {
    private String symbol;
    private List<Keycap> neighbouringKeycaps;

    public Keycap(String symbol){
        this.symbol = symbol;
        this.neighbouringKeycaps =  new LinkedList<Keycap>();
    }

    public void addneighbouringKeycap(Keycap neighbouringKeycap) {
        neighbouringKeycaps.add(neighbouringKeycap);
    }

    public List<Keycap> getNeighbouringKeycaps(){
        return this.neighbouringKeycaps;
    }

    public String getSymbol(){
        return this.symbol;
    }
}
