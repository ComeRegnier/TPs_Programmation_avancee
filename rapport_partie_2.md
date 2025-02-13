# Introduction

Ce rapport explore l'utilisation de la méthode de Monte Carlo (MC) pour calculer π en exploitant le parallélisme sur des architectures à mémoire partagée et distribuée. Après une présentation de l’algorithme séquentiel, nous étudions des variantes parallèles (itération parallèle, maître-esclave) et analysons deux implémentations Java.

Enfin, nous étendons l’étude aux environnements à mémoire distribuée et au parallélisme sur plusieurs machines, en évaluant les performances des différentes approches. Ce travail vise à fournir une vue synthétique et claire des stratégies et résultats obtenus.

Ce rapport a été en partie rédigé par ChatGPT, dans le but de le simplifier et de le rendre le plus clair et concis possible.

# I. Monte Carlo pour calculer π

La méthode de Monte Carlo repose sur une estimation probabiliste pour approximer π à partir de tirages aléatoires.

Soit AD l’aire d’un  de disque de rayon ( r = 1 ) :
$$
A_{\text{D}} = \frac{\pi r^2}{4} = \frac{\pi}{4}
$$

Le disque est inscrit dans un carré de côté ( r = 1 ), dont l’aire est :
$$
A_c = r^2 = 1
$$

On considère un point ( X_p (x_p, y_p) ) généré aléatoirement dans ce carré, où ( x_p ) et ( y_p ) suivent la loi uniforme ( U(0, 1) ).

La probabilité que ( X_p ) appartienne au  de disque est donnée par :
$$
P = \frac{A_{\text{D}}}{A_c} = \frac{\pi}{4}
$$

-------------------- 

image a la main

--------------------

Pour estimer cette probabilité, on effectue n tot tirages aléatoires. Soit n cible le nombre de points qui satisfont la condition x p 2 + y p 2 ≤ 1 , c’est-à-dire les points situés dans le quart de disque.

Si n tot est suffisamment grand, par la loi des grands nombres, la fréquence observée n cible / n tot converge vers la probabilité P , soit :
P = n cible n tot ≈ π 4

On peut ainsi en déduire une approximation de π :
π ≈ 4 ⋅ n cible n tot

Ainsi, plus n tot augmente, plus l'estimation de π se précise.