public class Consommateur extends Thread {
    private BAL bal;

    public Consommateur(BAL bal) {
        this.bal = bal;
    }

    public void run() {
        try {
            String lettre;
            do {
                lettre = bal.retirer();
            } while (!lettre.equals("Q"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
