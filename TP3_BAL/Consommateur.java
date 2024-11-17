public class Consommateur extends Thread {
    private final BAL bal;

    public Consommateur(BAL bal) {
        this.bal = bal;
    }

    public void run() {
        try {
            char lettre;
            do {
                lettre = bal.retirer(); // Retirer une lettre
                Thread.sleep(1000);    // Pause
            } while (lettre != '*');    // Arrêter lorsque '*' est retiré
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
