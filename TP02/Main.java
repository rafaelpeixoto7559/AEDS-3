class BTreeNode {
    int[] keys;
    int t;
    BTreeNode[] C;
    int n;
    boolean leaf;

    public BTreeNode(int t, boolean leaf) {
        this.keys = new int[2 * t - 1];
        this.t = t;
        this.C = new BTreeNode[2 * t];
        this.n = 0;
        this.leaf = leaf;
    }

    void insertNonFull(int k) {
        int i = n - 1;
        if (leaf) {
            while (i >= 0 && keys[i] > k) {
                keys[i + 1] = keys[i];
                i--;
            }
            keys[i + 1] = k;
            n++;
        } else {
            while (i >= 0 && keys[i] > k) {
                i--;
            }
            if (C[i + 1].n == 2 * t - 1) {
                splitChild(i + 1, C[i + 1]);
                if (keys[i + 1] < k) {
                    i++;
                }
            }
            C[i + 1].insertNonFull(k);
        }
    }

    void splitChild(int i, BTreeNode y) {
        BTreeNode z = new BTreeNode(y.t, y.leaf);
        z.n = t - 1;
        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
        }
        if (!y.leaf) {
            for (int j = 0; j < t; j++) {
                z.C[j] = y.C[j + t];
            }
        }
        y.n = t - 1;
        for (int j = n; j > i; j--) {
            C[j + 1] = C[j];
        }
        C[i + 1] = z;
        for (int j = n - 1; j >= i; j--) {
            keys[j + 1] = keys[j];
        }
        keys[i] = y.keys[t - 1];
        n++;
    }

    void traverse() {
        for (int i = 0; i < n; i++) {
            if (!leaf) {
                C[i].traverse();
            }
            System.out.print(" " + keys[i]);
        }
        if (!leaf) {
            C[n].traverse();
        }
    }

    BTreeNode search(int k) {
        int i = 0;
        while (i < n && k > keys[i]) {
            i++;
        }
        if (i < n && k == keys[i]) {
            return this;
        }
        if (leaf) {
            return null;
        }
        return C[i].search(k);
    }
}

class BTree {
    BTreeNode root;
    int t;

    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    void traverse() {
        if (root != null) {
            root.traverse();
        }
    }

    BTreeNode search(int k) {
        return root == null ? null : root.search(k);
    }

    void insert(int k) {
        if (root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = k;
            root.n = 1;
        } else {
            if (root.n == 2 * t - 1) {
                BTreeNode s = new BTreeNode(t, false);
                s.C[0] = root;
                s.splitChild(0, root);
                int i = 0;
                if (s.keys[0] < k) {
                    i++;
                }
                s.C[i].insertNonFull(k);
                root = s;
            } else {
                root.insertNonFull(k);
            }
        }
    }
}

class Main {
    public static void main(String[] args) {
        BTree t = new BTree(3);
        t.insert(10);
        t.insert(20);
        t.insert(5);
        t.insert(6);
        t.insert(12);
        t.insert(30);
        t.insert(7);
        t.insert(17);
        t.insert(15);
        t.insert(16);
        t.insert(27);
        t.insert(37);
        t.insert(13);

        System.out.print("Traversal of the constructed tree is ");
        t.traverse();
        System.out.println();

        int key = 6;
        if (t.search(key) != null) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }

        key = 15;
        if (t.search(key) != null) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }
}