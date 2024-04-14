package Controller;

import Model.Diretorio;
import Model.Indexes;
import Model.BTree;
import Model.Node;
import Model.Screenplay;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MenuActions {
    RandomAccessFile raf;
    RandomAccessFile braf;
    Scanner scanner;

    Indexes indexes;

    BTree btree;

    public void startApp() throws Exception {
        raf = new RandomAccessFile("./Database/Screenplay.db", "rw");
        braf = new RandomAccessFile("./Database/BTree.db", "rw");
        scanner = new Scanner(System.in);

        indexes = new Indexes();
        btree = new BTree(4);
    }

    public void finishApp() {
        try {
            raf.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadData() throws Exception {
        System.out.println("\nCarregando Dados...");
        try {
            raf.setLength(0); // clears the file

            String[] arrdata = lerArq("Database/NetFlix.csv"); // reads data from file
            Screenplay[] screenplays = new Screenplay[arrdata.length]; // array to store Screenplay objects

            int id = 0;
            for (int i = 0; i < arrdata.length; i++) {
                String[] data = arrdata[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length < 7) {
                    String[] newData = new String[7];
                    System.arraycopy(data, 0, newData, 0, data.length);
                    Arrays.fill(newData, data.length, newData.length, "");
                    data = newData;
                }
                screenplays[i] = new Screenplay(false, id, data[0], data[1], data[2], data[3], data[4],
                        Integer.parseInt(data[5]),
                        data[6].toCharArray()); // creates a new Screenplay object
                id++; // increments id

            }
            raf.seek(0); // sets pointer to the beginning of the file
            raf.writeInt(screenplays.length); // writes the number of records to the file

            for (int i = 0; i < screenplays.length; i++) {
                byte[] ba = screenplays[i].toByteArray();
                long RecordStart = raf.getFilePointer(); // stores the position of the record
                raf.writeInt(ba.length);
                raf.write(ba);

                long pointer = raf.getFilePointer(); // stores current pointer
                raf.seek(0); // sets pointer to the beginning of the file
                raf.writeInt(screenplays[i].getId()); // writes last id to the file
                raf.seek(pointer); // sets pointer to the last position

                Indexes.Indexify(screenplays[i].getId(), RecordStart);
            }

            // adds records to btree
            try {
                raf.seek(4); // sets pointer to the first record
                while (true) {
                    long pointer = raf.getFilePointer(); // stores current pointer
                    int size = raf.readInt(); // read the size of the record
                    boolean rip = raf.readBoolean(); // read if the record is removed
                    if (rip == false) {
                        byte[] ba = new byte[size]; // create a byte array with the size of the record
                        raf.read(ba);
                        Screenplay screenplay = new Screenplay();
                        screenplay.fromByteArray(ba); // convert byte array to Screenplay object
                        raf.seek(raf.getFilePointer() - 1);
                        btree.insert(screenplay.getId(), pointer); // adds record to btree
                    } else {
                        raf.seek(raf.getFilePointer() + size - 1); // if the record is removed, skip it
                    }
                }

            } catch (EOFException e) {
                System.out.println("\nFim dos Registros...");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        btree.store(braf);
        btree.traverse();
        System.out.println("Arvore salva");
    }

    public void findAll() throws IOException {
        System.out.println("\nMostrando Registros...");
        try {
            raf.seek(4); // sets pointer to the first record
            while (true) {
                int size = raf.readInt(); // read the size of the record
                boolean rip = raf.readBoolean(); // read if the record is removed
                if (rip == false) {
                    byte[] ba = new byte[size]; // create a byte array with the size of the record
                    raf.read(ba);
                    Screenplay screenplay = new Screenplay();
                    screenplay.fromByteArray(ba); // convert byte array to Screenplay object
                    raf.seek(raf.getFilePointer() - 1);
                    System.out.println(screenplay);
                } else {
                    raf.seek(raf.getFilePointer() + size - 1); // if the record is removed, skip it
                }
            }

        } catch (EOFException e) {
            System.out.println("\nFim dos Registros...");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("Carregando arvore...");
        btree.load(braf);
        System.out.println("\n Arvore: ");
        btree.traverse();
    }

    public void create() throws IOException {
        System.out.println("\nCriar Registro...");

        // begining of input intake

        System.out.println("\nDigite o tipo da Peça: ");
        String type = scanner.nextLine();

        System.out.println("\nDigite o nome da Peça: ");
        String name = scanner.nextLine();

        System.out.println("\nDigite o nome do Diretor: ");
        String director = scanner.nextLine();

        System.out.println("\nDigite os nomes do Elenco: ");
        String cast = scanner.nextLine();

        System.out.println("\nDigite a data de Adição (yyyy-mm-dd): ");
        String dateadded = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        while (true) { // loop to validate date format
            dateadded = scanner.nextLine();
            try {
                dateFormat.parse(dateadded);
                break;
            } catch (ParseException e) {
                System.out.println("Formato de data inválido. Digite novamente (yyyy-mm-dd): ");
            }
        }

        System.out.println("\nDigite o ano de Lançamento: ");
        int releasedate = 0;

        while (true) { // loop to validate year format
            try {
                releasedate = scanner.nextInt();
                if (releasedate >= 1000 && releasedate <= 9999) {
                    break;
                } else {
                    System.out.println("Ano inválido. Digite novamente: ");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ano inválido. Digite novamente: ");
                scanner.nextLine(); // clear the input buffer
            }
        }

        scanner.nextLine(); // clear the input buffer
        System.out.println("\nDigite a Classificação: ");
        String input = scanner.nextLine();
        char[] rating = new char[5];
        Arrays.fill(rating, ' '); // fill the array with empty spaces
        for (int i = 0; i < input.length() && i < 5; i++) {
            rating[i] = input.charAt(i);
        }

        // begining of record creation

        try {
            raf.seek(0);
            int regs = raf.readInt();
            regs++; // increments the number of records
            raf.seek(raf.length()); // sets pointer to the end of the file
            long pointer = raf.getFilePointer(); // stores current pointer
            Screenplay screenplay = new Screenplay(false, regs, type, name, director, cast, dateadded, releasedate,
                    rating);
            byte[] ba = screenplay.toByteArray();
            raf.writeInt(ba.length);
            raf.write(ba);
            raf.seek(0); // sets pointer to the beginning of the file
            raf.writeInt(regs); // updates the number of records
            System.out.println("\nRegistro criado. ID: " + screenplay.getId());
            System.out.println("Adicionando ao arquivo de indices...");
            btree.load(braf);
            btree.insert(screenplay.getId(), pointer);
            btree.store(braf);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public void findOne() throws IOException {
        System.out.println("\nLendo Registro...");

        int seek = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println("\nDigite o id do registro: ");
            if (scanner.hasNextInt()) {
                seek = scanner.nextInt();
                if (seek >= 0) {
                    validInput = true;
                } else {
                    System.out.println("Entrada inválida. Digite novamente.");
                }
            } else {
                System.out.println("Entrada inválida. Digite novamente.");
                scanner.nextLine(); // clear the input buffer
            }
        }

        while (!validInput) {
            System.out.println("\nDigite o id do registro: ");
            if (scanner.hasNextInt()) {
                seek = scanner.nextInt();
                validInput = true;
            } else {
                System.out.println("Entrada inválida. Digite novamente.");
                scanner.nextLine(); // clear the input buffer
            }
        }

        System.out.println("Carregando arvore...");
        btree.load(braf);
        System.out.println("\n Arvore: \n");
        Node node = btree.search(seek);
        if (node != null) {
            for (int i = 0; i < node.keycount; i++) {
                if (node.keys[i] == seek) {
                    System.out.println("Registro encontrado na arvore\n");
                    System.out.println("Posição: " + node.addresses[i] + "\n");
                }
            }
        } else {
            System.out.println("Registro não encontrado na arvore");
        }

        try {
            raf.seek(0);
            int regs = raf.readInt();
            Boolean found = false;

            while (true) {
                int size = raf.readInt(); // read the size of the record
                boolean rip = raf.readBoolean(); // read if the record is removed

                if (rip == false) {

                    byte[] ba = new byte[size]; // create a byte array with the size of the record
                    raf.read(ba);
                    Screenplay screenplay = new Screenplay();
                    screenplay.fromByteArray(ba); // convert byte array to Screenplay object
                    raf.seek(raf.getFilePointer() - 1);
                    if (screenplay.getId() == seek) {
                        System.out.println(screenplay);
                        found = true;
                        break;
                    }

                } else {
                    raf.seek(raf.getFilePointer() + size - 1); // if the record is removed, skip it
                }
            }
            if (found == false) {
                System.out.println("\nRegistro não encontrado...");
            }
            System.out.println("\nFim dos Registros...");

        } catch (EOFException e) {
            System.out.println("\nRegistro não encontrado...");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void update() throws IOException {
        System.out.println("\nAtualizar Registro...");

        int seek = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println("\nDigite o id do registro: ");
            if (scanner.hasNextInt()) {
                seek = scanner.nextInt();
                if (seek >= 0) {
                    validInput = true;
                } else {
                    System.out.println("Entrada inválida. Digite novamente.");
                }
            } else {
                System.out.println("Entrada inválida. Digite novamente.");
                scanner.nextLine(); // clear the input buffer
            }
        }

        while (!validInput) {
            System.out.println("\nDigite o id do registro: ");
            if (scanner.hasNextInt()) {
                seek = scanner.nextInt();
                validInput = true;
            } else {
                System.out.println("Entrada inválida. Digite novamente.");
                scanner.nextLine(); // clear the input buffer
            }
        }

        // seeks the record to be updated and list it. Then, asks for the new data and
        // updates the record

        long pointer = 0;
        try {
            raf.seek(0);
            int regs = raf.readInt();
            int i = 0;
            Boolean found = false;
            long pointer_rip = 0;

            while (true) {
                pointer = raf.getFilePointer(); // stores the position of the record
                int size = raf.readInt(); // read the size of the record
                boolean rip = raf.readBoolean(); // read if the record is removed
                pointer_rip = raf.getFilePointer() - 1; // stores the pointer to the rip field

                if (rip == false) {

                    byte[] ba = new byte[size]; // create a byte array with the size of the record
                    raf.read(ba);
                    Screenplay screenplay = new Screenplay();
                    screenplay.fromByteArray(ba); // convert byte array to Screenplay object
                    raf.seek(raf.getFilePointer() - 1);
                    if (screenplay.getId() == seek && rip == false) {

                        // list the record to be updated

                        System.out.println("\n registro encontrado: " + screenplay);
                        found = true;

                        // begining of input intake

                        scanner.nextLine(); // clear the input buffer
                        System.out.println("\nDigite o tipo da Peça: ");
                        String type = scanner.nextLine();

                        System.out.println("\nDigite o nome da Peça: ");
                        String name = scanner.nextLine();

                        System.out.println("\nDigite o nome do Diretor: ");
                        String director = scanner.nextLine();

                        System.out.println("\nDigite os nomes do Elenco: ");
                        String cast = scanner.nextLine();

                        System.out.println("\nDigite a data de Adição (yyyy-mm-dd): ");
                        String dateadded = "";

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dateFormat.setLenient(false);

                        while (true) { // loop to validate date format
                            dateadded = scanner.nextLine();
                            try {
                                dateFormat.parse(dateadded);
                                break;
                            } catch (ParseException e) {
                                System.out.println("Formato de data inválido. Digite novamente (yyyy-mm-dd): ");
                            }
                        }

                        System.out.println("\nDigite o ano de Lançamento: ");
                        int releasedate = 0;

                        while (true) { // loop to validate year format
                            try {
                                releasedate = scanner.nextInt();
                                if (releasedate >= 1000 && releasedate <= 9999) {
                                    break;
                                } else {
                                    System.out.println("Ano inválido. Digite novamente: ");
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Ano inválido. Digite novamente: ");
                                scanner.nextLine(); // clear the input buffer
                            }
                        }

                        scanner.nextLine(); // clear the input buffer
                        System.out.println("\nDigite a Classificação: ");
                        String input = scanner.nextLine();
                        char[] rating = new char[5];
                        Arrays.fill(rating, ' '); // fill the array with empty spaces
                        for (int j = 0; j < input.length() && j < 5; j++) {
                            rating[j] = input.charAt(j);
                        }

                        // begining of record update

                        Screenplay screenplay2 = new Screenplay(false, seek, type, name, director, cast, dateadded,
                                releasedate, rating);
                        byte[] ba2 = screenplay2.toByteArray();

                        if (size != ba2.length) {

                            raf.seek(pointer_rip);
                            raf.writeBoolean(true); // set rip to true
                            raf.seek(raf.length()); // sets pointer to the end of the file
                            pointer = raf.getFilePointer(); // stores current pointer

                            screenplay2.setId(seek); // sets the id of the new record
                            raf.writeInt(ba2.length);
                            raf.write(ba2);
                            raf.seek(0); // sets pointer to the beginning of the file

                            System.out.println("\nRegistro atualizado" + screenplay2);

                            break;

                        } else {
                            raf.seek(raf.getFilePointer() - size + 1);
                            raf.write(ba2);
                            System.out.println("\nRegistro atualizado" + screenplay2);
                            break;
                        }
                    }

                } else {
                    raf.seek(raf.getFilePointer() + size - 1); // if the record is removed, skip it
                }
            }

            if (found == false) {
                System.out.println("\nRegistro não encontrado...");
            }
            System.out.println("\nFim dos Registros...");

        } catch (EOFException e) {
            System.out.println("\nRegistro não encontrado...");
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
        System.out.println("Carregando arvore...");
        btree.load(braf);
        System.out.println("Atualizando arvore...");
        btree.delete(seek);
        System.out.println("Arvore deletada");
        btree.insert(seek, pointer);

    }

    public void delete() throws IOException {
        System.out.println("\nDeletar Registro...");

        int seek = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println("\nDigite o id do registro: ");
            if (scanner.hasNextInt()) {
                seek = scanner.nextInt();
                if (seek >= 0) {
                    validInput = true;
                } else {
                    System.out.println("Entrada inválida. Digite novamente.");
                }
            } else {
                System.out.println("Entrada inválida. Digite novamente.");
                scanner.nextLine(); // clear the input buffer
            }
        }

        while (!validInput) {
            System.out.println("\nDigite o id do registro: ");
            if (scanner.hasNextInt()) {
                seek = scanner.nextInt();
                validInput = true;
            } else {
                System.out.println("Entrada inválida. Digite novamente.");
                scanner.nextLine(); // clear the input buffer
            }
        }

        btree.load(braf);
        btree.traverse();
        btree.delete(seek);

        try {
            raf.seek(0);
            int regs = raf.readInt();
            Boolean found = false;
            long pointer_rip = 0;

            while (true) {
                int size = raf.readInt(); // read the size of the record
                boolean rip = raf.readBoolean(); // read if the record is removed
                pointer_rip = raf.getFilePointer() - 1; // stores the pointer to the rip field

                if (rip == false) {

                    byte[] ba = new byte[size]; // create a byte array with the size of the record
                    raf.read(ba);
                    Screenplay screenplay = new Screenplay();
                    screenplay.fromByteArray(ba); // convert byte array to Screenplay object
                    raf.seek(raf.getFilePointer() - 1);
                    if (screenplay.getId() == seek) {
                        System.out.println(screenplay);
                        found = true;
                        raf.seek(pointer_rip);
                        raf.writeBoolean(true); // set rip to true
                        break;
                    }

                } else {
                    raf.seek(raf.getFilePointer() + size - 1); // if the record is removed, skip it
                }
            }
            if (found == false) {
                System.out.println("\nRegistro não encontrado...");
            } else {
                System.out.println("\nRegistro deletado...");
            }
            System.out.println("\nFim dos Registros...");

        } catch (EOFException e) {
            System.out.println("\nRegistro não encontrado...");
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
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

    public void Hash() {
        Diretorio dir = new Diretorio();

        try {

            for (int i = 0; i < 7700; i++) {
                // System.out.println("Inserindo registro de ID:" + i);
                dir.add(i);
            }
            // System.out.println("Digite um id:");
            // int id = scanner.nextInt();
            // dir.add(id);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}