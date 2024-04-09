package Model;

public class BTree {

    public Node root; // Pointer to root node
    public int degree; // Minimum degree

    public BTree(int degree) {
        this.root = null;
        this.degree = degree;
    }

    // Function to traverse the tree
    public void traverse(){

        if(this.root != null){
            this.root.traverse();
        }else{
            System.out.println("Arvore vazia!");
        }
        System.out.println();
    }

    // Function to search given key in tree
    public Node search(int key){
        if(this.root == null){
            System.out.println("Arvore vazia!");
            return null;
        }else{
            return this.root.search(key);
        }
    }

    // Function to insert a new key in the tree
    public void insert(int key, int address){
        if (root == null){
            root = new Node(degree, true);
            root.keys[0] = key;
            root.addresses[0] = address;
            root.keycount = 1; 
        }else{
            if (root.keycount == 2 * degree - 1){// If the root is full, then tree grows in height
                Node newNode = new Node(degree, false);
                newNode.child[0] = root;
                newNode.splitChild(0, root);
                int i = 0;
                if (newNode.keys[0] < key){
                    i++;
                }
                newNode.child[i].insertNotFull(key, address);
                root = newNode;
            }else{
                root.insertNotFull(key, address);// If root is not full, call insertNotFull for root
            }
        }
    }

}