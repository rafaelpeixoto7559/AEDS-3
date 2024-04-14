package Model;

import java.io.RandomAccessFile;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Node {
    int[] keys; // An array of keys
    long[] addresses; // An array of addresses
    int degree; // minimum degree (defines the range for number of keys)
    Node[] child; // An array of child pointers
    int keycount; // Current number of keys
    boolean isLeaf; // Is true when node is isLeaf. Otherwise false

    // Constructor
    Node(int degree, boolean isLeaf) {
        this.degree = degree;
        this.keys = new int[degree * 2 - 1];
        this.addresses = new long[degree * 2 - 1];
        this.keycount = 0;
        this.child = new Node[2 * degree];
        this.isLeaf = isLeaf;
    }

    void insertNotFull(int key, long address) {
        int i = keycount - 1;
        if (isLeaf) {
            while (i >= 0 && keys[i] > key) {
                keys[i + 1] = keys[i];
                addresses[i + 1] = addresses[i];
                i--;
            }
            keys[i + 1] = key;
            addresses[i + 1] = address;
            keycount = keycount + 1;
        } else {
            while (i >= 0 && keys[i] > key) {
                i--;
            }
            if (child[i + 1].keycount == 2 * degree - 1) {
                splitChild(i + 1, child[i + 1]);
                if (keys[i + 1] < key) {
                    i++;
                }
            }
            child[i + 1].insertNotFull(key, address);
        }
    }

    // A utility function to split the child y of this node
    // Note that y must be full when this function is called
    void splitChild(int i, Node fullNode) {
        Node newNode = new Node(fullNode.degree, fullNode.isLeaf);
        newNode.keycount = degree - 1;

        for (int j = 0; j < degree - 1; j++) { // Copy the last (degree - 1) keys of fullNode to newNode
            newNode.keys[j] = fullNode.keys[j + degree];
            newNode.addresses[j] = fullNode.addresses[j + degree];
        }

        if (!fullNode.isLeaf) { // If fullNode is not a leaf, then move all its child pointers and addresses
            for (int j = 0; j < degree; j++) {
                newNode.child[j] = fullNode.child[j + degree];
            }
        }

        fullNode.keycount = degree - 1;

        for (int j = keycount; j >= i + 1; j--) {// Move all child pointers one space ahead
            child[j + 1] = child[j];
        }

        child[i + 1] = newNode;// Link the new child to this node

        for (int j = keycount; j > i; j--) {// Move all keys one space ahead
            keys[j + 1] = keys[j];
            addresses[j + 1] = addresses[j];
        }

        keys[i] = fullNode.keys[degree - 1]; // Copy the middle key of fullNode to this node
        addresses[i] = fullNode.addresses[degree - 1];
        keycount++;

    }

    // Function to traverse all nodes in a subtree rooted with this node
    public void traverse() {

        int i = 0;
        for (i = 0; i < this.keycount; i++) {
            if (!this.isLeaf) {
                this.child[i].traverse();
            }
            System.out.print(" " + this.keys[i] + " " + this.addresses[i] + " |");
        }

        if (!isLeaf) {
            child[i].traverse();
        }
    }

    // Function to search for key in subtree rooted with this node
    public Node search(int key) {
        int i = 0;
        while (i < this.keycount && key > this.keys[i]) {
            i++;
        }

        if (this.keys[i] == key && i < this.keycount) {
            return this;
        }

        if (this.isLeaf) {
            return null;
        }

        return this.child[i].search(key);
    }

    public int find(int k) {
        for (int i = 0; i < this.keycount; i++) {
            if (this.keys[i] == k) {
                return i;
            }
        }
        return -1;
    }

    public long store(RandomAccessFile braf) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        int i = 0;
        braf.seek(8);
        long pointer = braf.readLong() + 228;
        braf.seek(8);
        braf.writeLong(pointer);
        braf.seek(pointer);
        long address;

        for (i = 0; i < this.keycount; i++) {
            braf.seek(pointer + 4 + i * 20);
            if (!this.isLeaf) {
                address = this.child[i].store(braf);
                if(i == 0){
                    braf.seek(pointer + i * 20);
                    braf.writeInt(keycount);
                }else{
                    braf.seek(pointer + 4 + i * 20);
                }
                braf.writeLong(address);
                braf.writeInt(this.keys[i]);
                braf.writeLong(this.addresses[i]);
            }else{
                if (i == 0) {
                braf.seek(pointer + i * 20);
                braf.writeInt(keycount);
                braf.writeLong(-1);
                braf.writeInt(this.keys[i]);
                braf.writeLong(this.addresses[i]);
            }else{
                braf.seek(pointer + 4 + i * 20);
                braf.writeLong(-1);
                braf.writeInt(this.keys[i]);
                braf.writeLong(this.addresses[i]);
            }
            }
            
        }

        if (!isLeaf) {
            address = child[i].store(braf);
            braf.seek(pointer + 4 + keycount * 20);
            braf.writeLong(address);
        }else{
            braf.seek(pointer + 4 + keycount * 20);
            braf.writeLong(-1);
        }

        return pointer;
    }

    // Function to load the tree from a file
    
    public void load(RandomAccessFile braf, long pointer) throws IOException {
    
        braf.seek(pointer);
        int keycount = braf.readInt();
        this.keycount = keycount;
        int j = 0, i = 0;
        long address;
        for (i = 0; i < keycount; i++) {
            braf.seek(pointer + 4 + i * 20);
            address = braf.readLong();
            if (address != -1) {
                this.child[i] = new Node(4, false);
                child[i].load(braf, address);
            }
            j++;
            braf.seek(pointer + 4 + i * 20);
            braf.readLong();
            this.keys[i] = braf.readInt();
            this.addresses[i] = braf.readLong();
        }
        
        address = braf.readLong();

        if(address != -1){
            this.child[i] = new Node(4, false);
            child[i].load(braf, address);
        }else{
            j++;
        }

        if (j > keycount) {
            this.isLeaf = true;
        }

    }
        
}
