package Model;

import java.util.ArrayList;

public class HuffmannNode {
    String symbol;
    float frequency;
    HuffmannNode left;
    HuffmannNode right;

    public HuffmannNode() {
        this.symbol = "";
        this.frequency = 0;
        this.left = null;
        this.right = null;
    }
}
