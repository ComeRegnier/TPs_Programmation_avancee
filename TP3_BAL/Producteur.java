public class Producteur extends Thread {
    private BAL bal;

    public Producteur(BAL bal) {
        this.bal = bal;
    }

    public void run() {
        try {
            for (int i = 1; i <= 5; i++) { // Déposer 5 lettres
                bal.deposer("Lettre " + i);
                Thread.sleep(1000); // Pause entre les dépôts
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
