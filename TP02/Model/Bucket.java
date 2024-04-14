package Model;

import java.io.FileNotFoundException;

public class Bucket {

    int id[];
    long pos[];
    int maxsize = 154;
    int currentsize = 0;
    int pLocal = 1;

    public Bucket() throws FileNotFoundException {
        id = new int[maxsize];
        pos = new long[maxsize];
        for (int i = 0; i < maxsize; i++) {
            id[i] = -1;
            pos[i] = -1;
        }
    }

}
