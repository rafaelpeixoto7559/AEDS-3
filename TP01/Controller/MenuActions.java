package Controller;

import Model.Screenplay;
import java.io.RandomAccessFile;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MenuActions {
    RandomAccessFile raf;

    public void startApp() throws Exception {
        raf = new RandomAccessFile("./Database/Screenplay.db", "rw");
    }

    public void finishApp() {
        try {
            raf.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadData() {
        System.out.println("Carregando Dados...");
        try {

            String[] arrdata = lerArq("Database/NetFlix.csv"); // reads data from file
            Screenplay[] screenplays = new Screenplay[arrdata.length]; // array to store Screenplay objects
            
            int id = 0;
            for (int i = 0; i < arrdata.length; i++) {  
                String[] data = arrdata[i].split(",");
                screenplays[i] = new Screenplay(false, id, data[0], data[1], data[2], data[3], Integer.parseInt(data[4]),
                        data[5].toCharArray() ); // creates a new Screenplay object
                id++; // increments id
            }

            raf.seek(0); // sets pointer to the beginning of the file
            raf.writeInt(screenplays.length); // writes the number of records to the file

            for (int i = 0; i < screenplays.length; i++) {
                byte[] ba = screenplays[i].toByteArray();
                raf.writeInt(ba.length);
                raf.write(ba);

                long pointer = raf.getFilePointer(); // stores current pointer
                raf.seek(0); // sets pointer to the beginning of the file
                raf.writeInt(screenplays[i].getId()); // writes last id to the file 
                raf.seek(pointer); // sets pointer to the last position
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void findAll() {
        System.out.println("Mostrando Registros...");

        try {
            raf.seek(0);
            int regs = raf.readInt();
            int i = 0;
            while (i <= regs) {
                int size = raf.readInt(); // read the size of the record
                System.out.println(size);
                boolean rip = raf.readBoolean(); // read if the record is removed
                System.out.println(rip);
                if(rip == false){ 
                    byte[] ba = new byte[size]; // create a byte array with the size of the record
                    raf.read(ba);
                    Screenplay screenplay = new Screenplay();
                    screenplay.fromByteArray(ba); // convert byte array to Screenplay object
                    raf.seek(raf.getFilePointer()-1);
                    System.out.println(screenplay);
                }else{
                    raf.seek(raf.getFilePointer() + size); // if the record is removed, skip it
                }
                i++;
            }

            System.out.println("\n Fim dos Registros...");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void findOne() {
        System.out.println("Ler Registro...");
    }

    public void update() {
        System.out.println("Atualizar Registro...");
    }

    public void delete() {
        System.out.println("Deletar Registro...");
    }



        public static String[] lerArq(String path) {
            String[] arrData = new String[7747]; // array to store data
            int i = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                String line;
                br.readLine(); // skip the first line
                while ((line = br.readLine()) != null) {
                    arrData[i] = line;
                    i++;
                }
            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            }
            return arrData;
        }
}