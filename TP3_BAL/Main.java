public class Main {
    public static void main(String[] args) {
        int tailleTampon = 5; // Taille du tampon
        BAL bal = new BAL(tailleTampon);

        Producteur producteur = new Producteur(bal);
        Consommateur consommateur = new Consommateur(bal);

        producteur.start();
        consommateur.start();
    }
}
