public class Producteur extends Thread {
    private final BAL bal;

    public Producteur(BAL bal) {
        this.bal = bal;
    }

    public void run() {
        try {
            for (char lettre = 'A'; lettre <= 'Z'; lettre++) {
                bal.deposer(lettre); // Déposer les lettres de A à Z
                Thread.sleep(500);  // Pause
            }
            bal.deposer('*'); // Déposer le caractère spécial pour signaler la fin
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
