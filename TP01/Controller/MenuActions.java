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

            for (int i = 0; i < arrdata.length; i++) {
                String[] data = arrdata[i].split(",");
                screenplays[i] = new Screenplay(data[0], data[1], data[2], data[3], Integer.parseInt(data[4]),
                        data[5].toCharArray() ); // creates a new Screenplay object
            }

            for (int i = 0; i < screenplays.length; i++) {
                byte[] ba = screenplays[i].toByteArray();
                raf.writeInt(ba.length);
                raf.write(ba);
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void findAll() {
        System.out.println("Mostrando Registros...");

        try {
            raf.seek(0);
            while (true) {
                int size = raf.readInt();
                byte[] ba = new byte[size];
                raf.read(ba);
                Screenplay s = new Screenplay();
                s.fromByteArray(ba);
                System.out.println(s);
            }
        } catch (Exception e) {
            System.out.println("\n Fim dos Registros...");
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