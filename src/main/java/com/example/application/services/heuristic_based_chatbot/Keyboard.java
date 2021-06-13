import java.util.LinkedList;
import java.util.List;

public class Keyboard {
    private List<Keycap> keycaps;

    public Keyboard() {
        this.keycaps = new LinkedList<Keycap>();
    }

    public void addKeycap(Keycap keycap) {
        this.keycaps.add(keycap);
    }

    public boolean isNeighbour(String keycap1, String keycap2){
        for (int i = 0; i < keycaps.size(); i++) {
            if(keycaps.get(i).getSymbol().equals(keycap1)){
                List<Keycap> neighbours = keycaps.get(i).getNeighbouringKeycaps();
                for (int j = 0; j < neighbours.size(); j++) {
                    if(neighbours.get(j).getSymbol().equals(keycap2)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void printKeyboard() {
        for (int i = 0; i < keycaps.size(); i++) {
            for (int j = 0; j < keycaps.get(i).getNeighbouringKeycaps().size(); j++) {
                System.out.print(keycaps.get(i).getSymbol() + ": \n");
            }
        }
    }
}
