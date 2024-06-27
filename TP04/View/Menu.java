package View;

import Controller.MenuActions;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Menu extends MenuActions {

  private int selectedOption;
  private Scanner scanner;

  public int getSelectedOption() {
    return selectedOption;
  }

  public Menu() {
    selectedOption = 0;
    scanner = new Scanner(System.in);
  }

  public void setOption() {
    System.out.println("");
    System.out.println("Escolha uma opção: ");
    System.out.println("1: Carregar Dados");
    System.out.println("2: Listar Registros");
    System.out.println("3: Criar Registro");
    System.out.println("4: Procurar Registro");
    System.out.println("5: Atualizar Registro");
    System.out.println("6: Deletar Registro");
    System.out.println("7: Comprimir");
    System.out.println("8: Descomprimir");
    System.out.println("9: KMP");
    System.out.println("0: Sair");
    int userEntry = Integer.parseInt(scanner.nextLine());
    while (userEntry < 0 || userEntry > 9) {
      System.out.println("Opção Inválida, tente novamente");
      userEntry = Integer.parseInt(scanner.nextLine());
    }
    selectedOption = userEntry;
  }

  public void execute() {
    try {
      selectedOption = 0;
      this.startApp();
      this.setOption();
      this.executeSelectedOption();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public void executeSelectedOption() throws Exception {
    switch (this.selectedOption) {
      case 1:
        this.loadData();
        this.execute();
        break;
      case 2:
        this.findAll();
        this.execute();
        break;
      case 3:
        this.create();
        this.execute();
        break;
      case 4:
        this.findOne();
        this.execute();
        break;
      case 5:
        this.update();
        this.execute();
        break;
      case 6:
        this.delete();
        this.execute();
        break;
      case 7:

        this.Compress();
        this.execute();
        break;
      case 8:

        this.decompress();
        this.execute();
        break;

      case 9:
        this.KMP_Starter();
        this.execute();
      default:
        this.finishApp();
        scanner.close();
        break;
    }
  }
}