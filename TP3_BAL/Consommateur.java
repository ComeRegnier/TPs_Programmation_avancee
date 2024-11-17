public class Consommateur extends Thread {
    private BAL bal;

    public Consommateur(BAL bal) {
        this.bal = bal;
    }

    public void run() {
        try {
            for (int i = 1; i <= 5; i++) { // Retirer 5 lettres
                String lettre = bal.retirer();
                Thread.sleep(1500); // Pause après chaque retrait
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
