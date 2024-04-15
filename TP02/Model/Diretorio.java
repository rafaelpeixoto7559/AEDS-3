package Model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Diretorio {
    static RandomAccessFile dir;
    static RandomAccessFile aux;
    static RandomAccessFile buck;
    long pos;
    static int pGlobal;
    static Bucket pointers[];

    public Diretorio() {
        try {
            Indexes index = new Indexes();
            dir = new RandomAccessFile("./Database/Diretorio.db", "rw");
            aux = new RandomAccessFile("./Database/Indexes.db", "r");
            buck = new RandomAccessFile("./Database/Bucket.db", "rw");
            pointers = new Bucket[50];
            if (dir.length() >= 4) {
                pGlobal = dir.readInt();
            } else {
                System.out.println("Nenhum bucket encontrado!");
                dir.seek(0);
                dir.writeInt(1);
                long pointer = writeBucket(new Bucket());
                dir.writeLong(pointer);
                long pointer2 = writeBucket(new Bucket());
                dir.writeLong(pointer2);
                pGlobal = 1;
            }
            aux.seek(0);
            for (int i = 0; i < 50; i++) {
                pointers[i] = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(int id) throws Exception {
        System.out.println(pGlobal);
        int idIdx = 0;
        long posIdx = 0;
        try {
            aux.seek(0);
            while (aux.getFilePointer() < aux.length()) {
                idIdx = aux.readInt();
                posIdx = aux.readLong();
                // System.out.println("Registro encontrado!");
                if (idIdx == id) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Index não encontrado");
        }
        int k = (int) getKey(id);
        System.out.println(k);
        try {
            long address = readPointer(k);
            System.out.println(address);
            Bucket bct = readBucket(address);
            if (bct == null) { // se o bucket estiver vazio
                throw new Exception(" :( ");
            } else if (bct.currentsize < bct.maxsize) {
                bct.id[bct.currentsize] = idIdx;
                bct.pos[bct.currentsize] = posIdx;
                writeBucket(bct, address);
            } else {
                Split(bct, address, idIdx, posIdx);
            }
        } catch (Exception e) {
            System.out.println("ESPAÇO VAZIO!");
        }
    }

    public static long Split(Bucket bct, long address, int idReg, long posReg) throws Exception {
        RegistryHash reg[] = new RegistryHash[bct.maxsize + 1];
        buck.seek(address);
        int p = buck.readInt();
        if (pGlobal == p) {
            duplicate();
            updateP();
        }
        for (int i = 0; i < bct.maxsize; i++) {
            reg[i] = new RegistryHash(bct.id[i], bct.pos[i]);
        }
        reg[bct.maxsize - 1].id = idReg;
        reg[bct.maxsize - 1].pointer = posReg;
        resetBucket(bct);
        writeBucket(bct, address);

        for (int i = 0; i < bct.maxsize - 1; i++) {
            int k = (int) getKey(reg[i].id);
            long bctPointer = readPointer(k);
            Bucket nBucket = readBucket(bctPointer);

            if (pGlobal != nBucket.pLocal) {
                Bucket auxBucket = new Bucket();
                long auxPos = writeBucket(auxBucket);
                auxBucket.pLocal = pGlobal;
                bct.pLocal++;
                writeBucket(bct, address);
                writePointer(k, auxPos);
            }
            if (nBucket.currentsize < nBucket.maxsize) {
                nBucket.id[nBucket.currentsize] = reg[i].id;
                nBucket.pos[nBucket.currentsize] = reg[i].pointer;
                nBucket.currentsize++;
                writeBucket(bct, bctPointer);
            } else {
                Split(nBucket, bctPointer, reg[i].id, reg[i].pointer);
            }
        }
        return 0;
    }

    public static void writePointer(int k, long pointer) throws IOException {
        dir.seek(4 + (k) * 8 * pGlobal);
        dir.writeLong(pointer);
    }

    public static void resetBucket(Bucket bct) throws IOException {
        for (int i = 0; i < bct.maxsize; i++) {
            bct.id[i] = -1;
            bct.pos[i] = -1;
        }
        bct.currentsize = 0;
    }

    public static void updateP() throws IOException {
        pGlobal++;
        dir.seek(0);
        dir.writeInt(pGlobal);
    }

    public static void duplicate() throws IOException {

        long[] newPointers = new long[(int) Math.pow(2, pGlobal)];
        for (int i = 0; i < (int) Math.pow(2, pGlobal); i++) {
            long buckpointer = readPointer(i);
            newPointers[i] = buckpointer;
            int k = i + (int) Math.pow(2, pGlobal);
            writePointer(k, buckpointer);
        }
    }

    public static long writeBucket(Bucket bkt) throws IOException {
        buck.seek(buck.length());
        long pointer = buck.getFilePointer();
        buck.writeInt(bkt.pLocal);
        buck.writeInt(bkt.currentsize);
        for (int i = 0; i < bkt.maxsize; i++) {
            buck.writeInt(bkt.id[i]);
            buck.writeLong(bkt.pos[i]);
        }
        return pointer;
    }

    public static void writeBucket(Bucket bkt, long address) throws IOException {
        bkt.currentsize++;
        buck.seek(address);
        buck.writeInt(bkt.pLocal);
        buck.writeInt(bkt.currentsize);
        for (int i = 0; i < bkt.maxsize; i++) {
            buck.writeInt(bkt.id[i]);
            buck.writeLong(bkt.pos[i]);
        }
    }

    public static int getByteSize() throws FileNotFoundException {
        Bucket bkt = new Bucket();
        return 4 + 8 + (bkt.maxsize * (4 + 8));
    }

    public static long readPointer(int k) throws IOException {
        dir.seek(4 + k * (8));
        return dir.readLong();
    }

    public static Bucket readBucket(long pointer) throws IOException {
        Bucket bucket = new Bucket();
        try {
            buck.seek(pointer);

            bucket.pLocal = buck.readInt();
            bucket.currentsize = buck.readInt();

            for (int i = 0; i < bucket.maxsize; i++) {
                bucket.id[i] = buck.readInt();
                bucket.pos[i] = buck.readLong();
            }
            return bucket;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static double getKey(int k) {
        System.out.println(k);
        return k % Math.pow(2, pGlobal);
    }
}
