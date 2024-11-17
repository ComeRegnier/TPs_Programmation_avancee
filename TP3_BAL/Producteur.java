import java.util.Scanner;

public class Producteur extends Thread {
    private BAL bal;

    public Producteur(BAL bal) {
        this.bal = bal;
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            String lettre;
            do {
                System.out.print("Entrez une lettre : ");
                lettre = scanner.nextLine();
                bal.deposer(lettre);
            } while (!lettre.equals("Q"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
