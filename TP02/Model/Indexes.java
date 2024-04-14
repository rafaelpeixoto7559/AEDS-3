package Model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Indexes {
    int id;
    long pos;
    boolean deleted;

    static RandomAccessFile raf;
    static RandomAccessFile aux;

    public Indexes() throws FileNotFoundException {
        this.id = 0;
        this.pos = 0;
        this.deleted = false;
        raf = new RandomAccessFile("./Database/Indexes.db", "rw");
        aux = new RandomAccessFile("./Database/Screenplay.db", "r");
    }

    public static void Indexify(int id, long pos) throws IOException {
        raf.writeInt(id);
        raf.writeLong(pos);
    }

    public static Screenplay getFromIndex(int id) throws IOException {
        raf.seek(0);
        Screenplay screen = new Screenplay();
        while (raf.getFilePointer() < raf.length()) {
            int idIndex = raf.readInt();
            long pos = raf.readLong();
            if (idIndex == id) {
                aux.seek(pos);
                int size = aux.readInt();
                System.out.println(size);
                screen.rip = aux.readBoolean();
                screen.id = aux.readInt();
                screen.type = aux.readUTF();
                screen.title = aux.readUTF();
                screen.director = aux.readUTF();
                screen.cast = aux.readUTF();
                screen.dateadded = aux.readLong();
                screen.releasedate = aux.readInt();
                for (int i = 0; i < 5; i++) {
                    screen.rating[i] = aux.readChar();
                }
                System.out.println(screen.toString());
            }
        }
        return screen;
    }

}
