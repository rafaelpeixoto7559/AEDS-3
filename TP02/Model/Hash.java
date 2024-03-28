package Model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Hash {

    int id;
    Boolean rip;
    int adress;

    static Hash hash [] = new Hash[50];
    static Hash bucket [] = new Hash[155];
    static int bucketSize = 0;

    static RandomAccessFile raf;

    public Hash(){
        for (int i = 0; i < hash.length; i++) {
            hash[i].rip = true;
        }

        for (int i = 0; i < bucket.length; i++) {
            bucket[i].rip = true;
        }
    }

    public static int findKey(int id){
        int key = id;
        key*=5;
        key%=7;
        return key;
    }

    public static void hashify (Screenplay obj, int adress){
        int key = findKey(obj.id);

        if (hash[key].rip == true) {
            hash[key].id = obj.id;
            hash[key].adress = adress;
            hash[key].rip = false;
            System.out.println("Inserido no hash!");
        }
        else{
            if(bucketSize < 155){ //conferindo tamanho do bucket
                bucket[bucketSize].id = obj.id;
                bucket[bucketSize].adress = adress;
                bucket[bucketSize].rip = false;
                bucketSize++;
                System.out.println("Inserido no bucket!");
            }
            else{
                System.out.println("Bucket lotado!");
            }
        }
    }

    public static void indexify () throws IOException{
        try {
            raf = new RandomAccessFile("./Database/indexHash.db", "rw");
            for (int i = 0; i < hash.length; i++) {
                if (hash[i].rip == false) {
                    raf.writeInt(hash[i].id);
                    raf.writeInt(hash[i].adress);
                }
            }
            bucketfy();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado");
        }
    }

    private static void bucketfy () throws IOException{
        try {
            raf = new RandomAccessFile("./Database/bucketHash.db", "rw");
            for (int i = 0; i < bucketSize; i++) {
                raf.writeInt(bucket[i].id);
                raf.writeInt(bucket[i].adress);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado!");
        }
    }
}
