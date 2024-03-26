package Model;

public class Node {
    int[] key; // An array of keys
    int degree; //degree (the number of children a node can have)
    Node[] child; // An array of child pointers
    int keycount; // Current number of keys
    boolean leaf; // Is true when node is leaf. Otherwise false
 
    // Constructor
    Node(int degree, boolean leaf)
    {
        this.leaf = leaf;
        this.key = new int[degree * 2 - 2];
        this.child = new Node[degree];
        this.keycount = 0;
    }

    // Function to traverse all nodes in a subtree rooted with this node
    public void traverse(){

        int i = 0;
        for (i = 0; i < this.keycount; i++) {
            if (this.leaf == false) {
                this.child[i].traverse();
            }
            System.out.print(this.key[i*2] + this.key[i*2+1] + " ");
        }

        if (leaf == false) {
            child[i].traverse();
        }
    }

    // Function to search for key in subtree rooted with this node
    public Node search(int key){
        int i = 0;
        while (i < this.keycount && key > this.key[i*2]) {
            i++;
        }

        if (this.key[i*2] == key) {
            return this;
        }

        if (this.leaf == true) {
            return null;
        }

        return this.child[i].search(key);
    }

}
