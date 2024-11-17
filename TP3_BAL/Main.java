public class Main {
    public static void main(String[] args) {
        BAL bal = new BAL();
        Producteur producteur = new Producteur(bal);
        Consommateur consommateur = new Consommateur(bal);

        producteur.start();
        consommateur.start();
    }
}
