import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class App {
    
    public static void main(String[] args) {
        
        // Create a new Screenplay object 
        // Test

        Screenplay screenplay = new Screenplay("Movie", "The Godfather", "Francis Ford Coppola", "1972-03-24", 1972, new char[]{'R', ' ', ' ', ' ', ' '});

        // Declare file input and output streams

        FileOutputStream arqout;
        DataOutputStream dos;

        FileInputStream arqin;
        DataInputStream dis;

        byte[] ba;

        try{
            // Save to file
            arqout = new FileOutputStream("Database/Screenplay.db");
            dos = new DataOutputStream(arqout);
            ba = screenplay.toByteArray();
            dos.writeInt(ba.length);
            dos.write(ba);

            arqout.close();
            dos.close();
            // Read from file

            
            Screenplay screenplay2 = new Screenplay();
            int tam;
            
            arqin = new FileInputStream("Database/Screenplay.db");
            dis = new DataInputStream(arqin);

            tam = dis.readInt();
            ba = new byte[tam];
            dis.read(ba);
            screenplay2.fromByteArray(ba);

            System.out.println(screenplay2);


        }
        catch(Exception e){
            System.out.println("Error: " + e);
        }

    }

}
