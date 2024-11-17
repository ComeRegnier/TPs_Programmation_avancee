public class BAL {
    private final char[] tampon;
    private int tete = 0;       // Index de retrait
    private int queue = 0;      // Index d'insertion
    private int nbLettres = 0;  // Nombre de lettres dans le tampon

    public BAL(int taille) {
        tampon = new char[taille];
    }

    // Méthode pour déposer une lettre dans le tampon
    public synchronized void deposer(char lettre) throws InterruptedException {
        while (nbLettres == tampon.length) { // Attendre si le tampon est plein
            System.out.println("Tampon plein, producteur en attente...");
            wait();
        }
        tampon[queue] = lettre;
        queue = (queue + 1) % tampon.length;
        nbLettres++;
        System.out.println("Producteur a déposé : " + lettre);
        notifyAll(); // Réveiller les threads en attente
    }

    // Méthode pour retirer une lettre du tampon
    public synchronized char retirer() throws InterruptedException {
        while (nbLettres == 0) { // Attendre si le tampon est vide
            System.out.println("Tampon vide, consommateur en attente...");
            wait();
        }
        char lettre = tampon[tete];
        tete = (tete + 1) % tampon.length;
        nbLettres--;
        System.out.println("Consommateur a retiré : " + lettre);
        notifyAll(); // Réveiller les threads en attente
        return lettre;
    }
}
