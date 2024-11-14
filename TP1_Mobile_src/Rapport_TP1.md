# Rapport du Programme "TpMobile" : Déplacement d'un Mobile dans une Fenêtre en Java
*Ce rapport a été réalisé avec l’aide de l’IA*
## Introduction
Dans ce projet, nous avons développé une application Java utilisant l'interface graphique `Swing` pour simuler le déplacement de deux objets mobiles à l'écran. Les mobiles se déplacent de gauche à droite dans une fenêtre, rebondissant lorsqu'ils atteignent les limites. Deux boutons permettent de démarrer ou d'arrêter individuellement chaque mobile.

## Analyse du Code

### 1. Classe `TpMobile`
- Cette classe principale initialise une fenêtre `UneFenetre` avec une taille définie de 500x400 pixels.

### 2. Classe `UneFenetre`
- `UneFenetre` hérite de `JFrame` pour gérer la fenêtre de l'application.
- Deux objets de la classe `UnMobile` sont créés, chacun associé à un bouton de contrôle (`BoutonArretMarche1` et `BoutonArretMarche2`) permettant de démarrer ou d'arrêter le déplacement.
- Chaque mobile est démarré sur un thread indépendant, garantissant leur mouvement simultané sans bloquer l'interface.

### 3. Classe `UnMobile`
- `UnMobile` est une sous-classe de `JPanel` et implémente `Runnable` pour permettre le mouvement autonome du mobile via un thread.
- La méthode `run` gère le déplacement du mobile en modifiant sa position à intervalles réguliers (50 ms). Le mobile change de direction lorsqu’il atteint les bords de la fenêtre.
- Le mouvement du mobile est contrôlé par la variable `enMarche` (démarré/arrêté par les boutons associés), et la direction (droite/gauche) est déterminée par `dirDroite`.

## Fonctionnalités Principales
- **Affichage Graphique et Mouvement** : Les mobiles sont représentés par des carrés de 40x40 pixels se déplaçant horizontalement. La méthode `paintComponent` gère l'affichage visuel.
- **Contrôle des Mobiles** : Chaque mobile possède un bouton permettant de démarrer ou d'arrêter son mouvement de façon indépendante. Les états des boutons alternent entre "Arrêt" et "Démarrer".

