package Model;

public class BTree {

    public Node root; // Pointer to root node
    public int degree; // Minimum degree

    BTree(int degree) {
        this.root = null;
        this.degree = degree;
    }

    // Function to traverse the tree
    public void traverse(){

        if(this.root != null){
            this.root.traverse();
        }
        System.out.println();
    }

    // Function to search given key in tree
    public Node search(int key){
        if(this.root == null){
            return null;
        }else{
            return this.root.search(key);
        }
    }


}




