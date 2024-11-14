import javax.swing.JPanel;
import java.awt.Graphics;

public class UnMobile extends JPanel implements Runnable {
	private int Largeur, Hauteur, coordMobile;
	private final int Pas = 10, Temps = 50, Cote = 40;
	private boolean dirDroite = true;  
	//True = droite, false = gauche
	private boolean enMarche = true;

	public UnMobile(int parLargeur, int parHauteur) {
		super();
		this.Largeur = parLargeur;
		this.Hauteur = parHauteur;
		setSize(parLargeur, parHauteur);
	}

	public void run() {
		while (true) {
			if(enMarche) {
				if (dirDroite) {
					coordMobile += Pas;
					if (coordMobile >= Largeur - Cote) {
						dirDroite = false;  // Change de direction
					}
				} else {
					coordMobile -= Pas;
					if (coordMobile <= 0) {
						dirDroite = true;  // Change de direction
					}
				}
			}
			repaint();

			try {
				Thread.sleep(Temps);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setEnMarche(boolean enMarche) {
		this.enMarche = enMarche;
	}

	public boolean isEnMarche() {
		return enMarche;
	}

	public void paintComponent(Graphics telContexteGraphique) {
		super.paintComponent(telContexteGraphique);
		telContexteGraphique.fillRect(coordMobile, Hauteur / 2, Cote, Cote);
	}
}