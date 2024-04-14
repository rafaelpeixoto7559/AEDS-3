package Model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BTree {

    public Node root; // Pointer to root node
    public int degree; // Minimum degree

    public BTree(int degree) {
        this.root = null;
        this.degree = degree;
    }

    // Function to traverse the tree
    public void traverse() {
        if (this.root != null) {
            this.root.traverse();
        } else {
            System.out.println("Arvore vazia!");
        }
        System.out.println();
    }

    // Function to search given key in tree
    public Node search(int key) {
        if (this.root == null) {
            System.out.println("Arvore vazia!");
            return null;
        } else {
            return this.root.search(key);
        }
    }

    // Function to insert a new key in the tree
    public void insert(int key, long address) {
        if (root == null) {
            root = new Node(degree, true);
            root.keys[0] = key;
            root.addresses[0] = address;
            root.keycount = 1;
        } else {
            if (root.keycount == 2 * degree - 1) {// If the root is full, then tree grows in height
                Node newNode = new Node(degree, false);
                newNode.child[0] = root;
                newNode.splitChild(0, root);
                int i = 0;
                if (newNode.keys[0] < key) {
                    i++;
                }
                newNode.child[i].insertNotFull(key, address);
                root = newNode;
            } else {
                root.insertNotFull(key, address);// If root is not full, call insertNotFull for root
            }
        }
    }

    // Function to delete a key in the tree
    public void delete(Node node, int value) {
        int pos = node.find(value);
        if (pos != -1) {
            if (node.isLeaf) {
                int i = 0;
                for (i = 0; i < node.keycount && node.keys[i] != value; i++)
                    ; // Find the index of the key to be deleted
                for (; i < node.keycount - 1; i++) {
                    if (i != 2 * degree - 2) {
                        node.keys[i] = node.keys[i + 1];
                        node.addresses[i] = node.addresses[i + 1];
                    }
                }
                node.keycount--;
                return;
            }

            if (!node.isLeaf) {// If the node is not a leaf, then find the predecessor of the key to be deleted
                Node predecesor = node.child[pos];
                int predecesorValue = 0;
                if (predecesor.keycount >= degree) {
                    while (!predecesor.isLeaf) {
                        predecesor = predecesor.child[predecesor.keycount];
                    }
                    predecesorValue = predecesor.keys[predecesor.keycount - 1];
                    delete(predecesor, predecesorValue);
                    node.keys[pos] = predecesorValue;
                    return;
                }
                
                Node nextNode = node.child[pos + 1];
                if (nextNode.keycount >= degree) {
                    int nextKey = nextNode.keys[0];
                    if (!nextNode.isLeaf) {
                        nextNode = nextNode.child[0];
                        for (; !nextNode.isLeaf; nextNode = nextNode.child[nextNode.keycount])
                            ;
                    }
                    nextKey = nextNode.keys[nextNode.keycount - 1];
                    delete(nextNode, nextKey);
                    node.keys[pos] = nextKey;
                    return;
                }

                int temp = predecesor.keycount + 1;
                predecesor.keys[predecesor.keycount++] = node.keys[pos];
                predecesor.addresses[predecesor.keycount++] = node.addresses[pos];
                for (int i = 0, j = predecesor.keycount; i < nextNode.keycount; i++, j++) {
                    predecesor.keys[j] = node.keys[i];
                    predecesor.addresses[j] = node.addresses[i];
                    predecesor.keycount++;
                }
                for (int i = 0; i < nextNode.keycount + 1; i++) {
                    predecesor.child[temp++] = nextNode.child[i];
                }

                node.child[pos] = predecesor;
                for (int i = pos; i < node.keycount; i++) {
                    if (i != 2 * degree - 2) {
                        node.keys[i] = node.keys[i + 1];
                        node.addresses[i] = node.addresses[i + 1];
                    }
                }
                for (int i = pos + 1; i < node.keycount + 1; i++) {
                    if (i != 2 * degree - 1) {
                        node.child[i] = node.child[i + 1];
                    }
                }
                node.keycount--;
                if (node.keycount == 0) {
                    if (node == root) {
                        root = node.child[0];
                    }
                    node = node.child[0];
                }
                delete(predecesor, value);
                return;
            }
        } else {
            for (pos = 0; pos < node.keycount; pos++) {
                if (node.keys[pos] > value) {
                    break;
                }
            }
            Node temp = node.child[pos];
            if (temp.keycount >= degree) {
                delete(temp, value);
                return;
            }
            Node nb = null;
            int separator = 0;
            long separatoraddress = 0;

            if(pos != node.keycount && node.child[pos+1].keycount >= degree){
                separator = node.keys[pos];
                separatoraddress = node.addresses[pos];
                nb = node.child[pos+1];
                node.keys[pos] = nb.keys[0];
                node.addresses[pos] = nb.addresses[0];
                temp.keys[temp.keycount] = separator;
                temp.addresses[temp.keycount] = separatoraddress;
                temp.keycount++;
                temp.child[temp.keycount] = nb.child[0];
                for(int i = 0; i < nb.keycount - 1; i++){
                    nb.keys[i] = nb.keys[i+1];
                    nb.addresses[i] = nb.addresses[i+1];
                }
                for(int i = 0; i < nb.keycount; i++){
                    nb.child[i] = nb.child[i+1];
                }
                nb.keycount--;
                delete(temp, value);
                return;
            } else if (pos != 0 && node.child[pos - 1].keycount >= degree){

                separator = node.keys[pos - 1];
                separatoraddress = node.addresses[pos - 1];
                nb = node.child[pos - 1];
                node.keys[pos - 1] = nb.keys[nb.keycount - 1];
                node.addresses[pos - 1] = nb.addresses[nb.keycount - 1];
                Node child = nb.child[nb.keycount];
                nb.keycount--;

                for (int i = temp.keycount; i > 0; i--) {
                    temp.keys[i] = temp.keys[i - 1];
                    temp.addresses[i] = temp.addresses[i - 1];
                }
                temp.keys[0] = separator;
                temp.addresses[0] = separatoraddress;
                for (int i = temp.keycount + 1; i > 0; i--) {
                    temp.child[i] = temp.child[i - 1];
                }
                temp.child[0] = child;
                temp.keycount++;
                delete(temp, value);
                return;
            } else {
                Node lt = null;
                Node rt = null;
                boolean last = false;
                if (pos != node.keycount) {
                    separator = node.keys[pos];
                    separatoraddress = node.addresses[pos];
                    lt = node.child[pos];
                    rt = node.child[pos + 1];
                } else {
                    separator = node.keys[pos - 1];
                    separatoraddress = node.addresses[pos - 1];
                    rt = node.child[pos];
                    lt = node.child[pos - 1];
                    last = true;
                    pos--;
                }
                for (int i = pos; i < node.keycount - 1; i++) {
                    node.keys[i] = node.keys[i + 1];
                    node.addresses[i] = node.addresses[i + 1];
                }
                for (int i = pos + 1; i < node.keycount; i++) {
                    node.child[i] = node.child[i + 1];
                }
                node.keycount--;
                lt.keys[lt.keycount] = separator;
                lt.addresses[lt.keycount] = separatoraddress;
                lt.keycount++;

                for (int i = 0, j = lt.keycount; i < rt.keycount; i++, j++) {
                    if(i != rt.keycount){
                        lt.keys[j] = rt.keys[i];
                        lt.addresses[j] = rt.addresses[i];
                    }
                    lt.child[j] = rt.child[i];
                }
                lt.keycount += rt.keycount;
                if (node.keycount == 0) {
                    if (node == root) {
                        root = node.child[0];
                    }
                    node = node.child[0];
                }
                delete(lt, value);
                return;
            }
        }
    }
    public void delete(int value) {
        Node node = search(root, value);
        if (node == null) {
            System.out.println("Arvore vazia!");
            return;
        }
        delete(root, value);
    }

    private Node search(Node node, int value) {
        int i = 0;

        if (node == null) {
            return null;
        }

        for (i = 0; i < node.keycount; i++) {
            if (value < node.keys[i]) {
                break;
            }
            if (value == node.keys[i]) {
                return node;
            }
        }
        if (node.isLeaf) {
            return null;
        } else {
            return search(node.child[i], value);
        }

    }

    // Function to store the tree in a file
    
    public void store(RandomAccessFile braf) throws IOException {
        braf.setLength(0);
        String filename = "./Database/BTree.db";
        braf.seek(0);
        braf.writeLong(228);
        braf.writeLong(0);

        if (this.root != null) {
            this.root.store(braf);
        } else {
            System.out.println("Arvore vazia!");
        }
        System.out.println();

    }

    // Function to load the tree from a file
    public void load(RandomAccessFile braf) throws IOException {
        braf.seek(0);
        long rootAddress = braf.readLong();
        this.root = new Node(degree, false);
        this.root.load(braf, rootAddress);
    }
}
