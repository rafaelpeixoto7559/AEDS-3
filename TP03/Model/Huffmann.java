package Model;

import java.util.ArrayList;

public class Huffmann {

    public static ArrayList<finalNode> printTree(HuffmannNode node) {
        ArrayList<finalNode> freq = new ArrayList<>();
        printTree(node, "", freq);
        return freq;
    }

    public static HuffmannNode buildTreeFromPath(ArrayList<finalNode> nodes) {
        HuffmannNode root = new HuffmannNode();
        for (finalNode node : nodes) {
            HuffmannNode current = root;
            for (int i = 0; i < node.path.length(); i++) {
                if (node.path.charAt(i) == '0') {
                    if (current.left == null) {
                        current.left = new HuffmannNode();
                    }
                    current = current.left;
                } else {
                    if (current.right == null) {
                        current.right = new HuffmannNode();
                    }
                    current = current.right;
                }
            }
            current.symbol = node.symbol;
        }
        return root;
    }

    private static ArrayList<finalNode> printTree(HuffmannNode node, String path, ArrayList<finalNode> freq) {
        if (node.left == null && node.right == null) {
            freq.add(new finalNode(node.symbol, path));
            System.out.println(node.symbol + " " + path);
        } else {
            printTree(node.left, path + "0", freq);
            printTree(node.right, path + "1", freq);
        }
        return freq;
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
        ArrayList<finalNode> nodes = printTree(node);
        for (finalNode f : nodes) {
            System.out.println(f.symbol + " " + f.path);
        }
    }

}
