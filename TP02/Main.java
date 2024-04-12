import Model.BTree;

public class Main {
    public static void main(String[] args) {
        BTree b = new BTree(3);
        b.insert(8,1);
        b.insert(9,2);
        b.insert(10,3);
        b.insert(11,4);
        b.insert(15,5);
        b.insert(20,6);
        b.insert(17,7);
    
        b.traverse();
    
        b.delete(10);
        System.out.println();
        b.traverse();
      }
}

