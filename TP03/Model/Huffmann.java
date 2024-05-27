package Model;

import java.util.ArrayList;

public class Huffmann {

    public static void printTree(HuffmannNode node) {
        printTree(node, "");
    }

    private static void printTree(HuffmannNode node, String path) {
        if (node.left == null && node.right == null) {
            System.out.println(node.symbol + " " + path);
        } else {
            printTree(node.left, path + "0");
            printTree(node.right, path + "1");
        }
    }

    public static ArrayList<HuffmannNode> TreatString(String word) {

        ArrayList<HuffmannNode> nodeArr = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            boolean exists = false;
            for (HuffmannNode str : nodeArr) {
                if (word.charAt(i) == str.symbol.charAt(0)) {
                    str.frequency++;
                    exists = true;
                }
            }
            if (!exists) {
                HuffmannNode node = new HuffmannNode();
                node.symbol += word.charAt(i);
                node.frequency = 1;
                nodeArr.add(node);
            }
        }

        for (HuffmannNode huff : nodeArr) {
            huff.frequency = huff.frequency / word.length();
        }

        return nodeArr;
    }

    private static HuffmannNode joinNodes(HuffmannNode left, HuffmannNode right) {
        HuffmannNode node = new HuffmannNode();
        node.symbol = left.symbol + right.symbol;
        node.frequency = left.frequency + right.frequency;
        node.left = left;
        node.right = right;
        return node;
    }

    public static HuffmannNode buildTree(ArrayList<HuffmannNode> nodeArr) {
        nodeArr.sort((a, b) -> {
            return Float.compare(a.frequency, b.frequency);
        });
        while (nodeArr.size() > 1) {
            nodeArr.sort((a, b) -> {
                return Float.compare(a.frequency, b.frequency);
            });
            HuffmannNode newNode = joinNodes(nodeArr.get(0), nodeArr.get(1));
            nodeArr.remove(0);
            nodeArr.remove(0);
            nodeArr.add(newNode);
        }
        return nodeArr.get(0);
    }

    public static void main(String[] args) {
        ArrayList<HuffmannNode> nodeArr = TreatString("abcdefgh");
        HuffmannNode node = buildTree(nodeArr);
        printTree(node);
    }

}
