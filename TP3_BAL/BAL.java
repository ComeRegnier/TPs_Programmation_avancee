public class BAL {
    private String lettre = null;

    // Déposer une lettre dans la BAL
    public synchronized void deposer(String lettre) throws InterruptedException {
        while (this.lettre != null) { // Attendre que la BAL soit vide
            wait();
        }
        this.lettre = lettre;
        System.out.println("Producteur a déposé : " + lettre);
        notifyAll(); // Réveiller les threads en attente
    }

    // Retirer une lettre de la BAL
    public synchronized String retirer() throws InterruptedException {
        while (this.lettre == null) { // Attendre que la BAL contienne une lettre
            wait();
        }
        String tmp = this.lettre;
        this.lettre = null;
        System.out.println("Consommateur a retiré : " + tmp);
        notifyAll(); // Réveiller les threads en attente
        return tmp;
    }
}
