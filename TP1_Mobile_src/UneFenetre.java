import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UneFenetre extends JFrame {
    private UnMobile Mobile1;
    private UnMobile Mobile2;
    private JButton BoutonArretMarche1 ;
    private JButton BoutonArretMarche2 ;

    public UneFenetre(int parLargeur, int parHauteur) {
        setTitle("Mobile Test");
        setSize(parLargeur, parHauteur);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setLayout(new GridLayout(4, 1));

        //Premier Mobile
        UnMobile Mobile1 = new UnMobile(parLargeur, parHauteur);
        BoutonArretMarche1 = new JButton("Arrêt") ;
        BoutonArretMarche1.setPreferredSize(new Dimension(100, 40));
        add(Mobile1) ;
        add(BoutonArretMarche1);
        Thread threadmobile1 = new Thread(Mobile1) ;
        threadmobile1.start();

        //Second Mobile
        UnMobile Mobile2 = new UnMobile(parLargeur, parHauteur);
        BoutonArretMarche2 = new JButton("Arrêt") ;
        BoutonArretMarche1.setPreferredSize(new Dimension(100, 40));
        add(Mobile2) ;
        add(BoutonArretMarche2);
        Thread threadmobile2 = new Thread(Mobile2) ;
        threadmobile2.start();

        //Configuration des boutons
        BoutonArretMarche1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Mobile1.isEnMarche()) {
                    // Arrêt
                    Mobile1.setEnMarche(false);
                    BoutonArretMarche1.setText("Start");
                } else {
                    Mobile1.setEnMarche(true);
                    BoutonArretMarche1.setText("Stop");
                }
            }
        });

        BoutonArretMarche2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Mobile2.isEnMarche()) {
                    // Arrêt
                    Mobile2.setEnMarche(false);
                    BoutonArretMarche2.setText("Start");
                } else {
                    Mobile2.setEnMarche(true);
                    BoutonArretMarche2.setText("Stop");
                }
            }
        });

        setVisible(true);
    }
}