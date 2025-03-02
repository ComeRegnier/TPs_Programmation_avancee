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

![Monte Carlo](./images/figure_monte_carlo-1.png)

--------------------

Pour estimer cette probabilité, on effectue n tot tirages aléatoires. Soit n cible le nombre de points qui satisfont la condition x p 2 + y p 2 ≤ 1 , c’est-à-dire les points situés dans le quart de disque.

Si n tot est suffisamment grand, par la loi des grands nombres, la fréquence observée n cible / n tot converge vers la probabilité P , soit :
P = n cible n tot ≈ π 4

On peut ainsi en déduire une approximation de π :
π ≈ 4 ⋅ n cible n tot

Ainsi, plus n tot augmente, plus l'estimation de π se précise.

## II. Algorithme et parallélisation

### A. Itération parallèle

L’algorithme de Monte Carlo peut être parallélisé en distribuant les tirages sur plusieurs tâches indépendantes. Voici l'algorithme séquentiel de base :

#### Algorithme de base

```c
n_cible = 0;
for (p = 0; n_tot > 0; n_tot--) {
    x_p = rand();  // Générer un nombre aléatoire entre ]0,1[
    y_p = rand();
    if ((x_p * x_p + y_p * y_p) < 1) {
        n_cible++;
    }
}
pi = 4 * n_cible / n_tot;
```

Dans cette version, tout est exécuté séquentiellement. Pour paralléliser ce code, il faut identifier les tâches et leurs dépendances.
Identification des tâches

    Tâche principale (T0) : Tirer et compter les n_tot points.

    Tâche secondaire (T1) : Calculer ππ après la collecte de n_cible :

Dépendances et parallélisme

    Dépendances :
        T1 dépend de T0 : ππ ne peut être calculé qu’après avoir obtenu n_cible.
        T0p2 dépend de T0p1 : Un point doit être généré avant sa vérification.

    Indépendances parallèles :
        Les générations de points (T0p1) peuvent être effectuées simultanément, chaque tirage étant indépendant.
        Les vérifications (T0p2) peuvent aussi être parallélisées, mais nécessitent une gestion sécurisée de n_cible pour éviter des conflits d’accès.

Algorithme parallèle

L’algorithme parallèle repose sur une fonction dédiée TirerPoint() pour générer et évaluer les points.

TirerPoint() est indépendante et permet donc d'éxecuter chaque tirage sur plusieurs threads sans dépendance entre eux.

# B. Paradigme Master/Worker

Le modèle **Master/Worker** repose sur une division du travail en unités indépendantes, chacune traitée par un **Worker** (processus ou thread). Chaque Worker effectue un certain nombre de tirages aléatoires, et un processus principal, appelé **Master**, agrège les résultats pour calculer π.

## 1. Principe du modèle

- Le travail total (`n_tot` tirages) est réparti équitablement entre `n_workers`.
- Chaque Worker exécute une fonction indépendante pour traiter sa part des tirages.
- Le Master collecte les résultats des Workers et calcule l'estimation finale de π.

Ce modèle permet d'optimiser l'utilisation des ressources tout en minimisant les conflits d'accès mémoire.

---

## 2. Algorithme Master/Worker

### **Fonctions principales**

```cpp
function TirerPoint() {
    x_p = rand();  // Générer un nombre aléatoire dans ]0,1[
    y_p = rand();
    return ((x_p * x_p + y_p * y_p) < 1);
}

function MCWorker(n_charge) {
    n_cible_partiel = 0;
    for (p = 0; n_charge > 0; n_charge--) {
        if (TirerPoint()) {
            n_cible_partiel += 1;
        }
    }
    return n_cible_partiel;
}
```
- TirerPoint() : génère un point aléatoire et vérifie s'il appartient au quart de disque.
- MCWorker(n_charge) : effectue n_charge tirages et compte ceux appartenant au quart de disque.

```cpp
n_charge = n_tot / n_workers;
ncibles = [NULL * n_workers];

parallel for (worker = 0; worker < n_workers; worker++) {
    ncibles[worker] = MCWorker(n_charge);
}

n_cible = sum(ncibles);
pi = 4 * n_cible / n_tot;
```

- Chaque Worker traite n_charge = n_tot / n_workers points.
- Le tableau ncibles stocke les résultats de chaque Worker.
- Une fois le calcul terminé, le Master additionne les valeurs et estime π.

## 3. Gestion des ressources et parallélisation
Élimination des conflits d'accès

Contrairement à l’itération parallèle, où plusieurs threads modifiaient une variable partagée (n_cible), ici :

    Chaque Worker possède son propre compteur local (n_cible_partiel), évitant ainsi les conflits.
    Le seul élément partagé est le tableau ncibles, mais chaque Worker écrit dans une case distincte.

Optimisation et scalabilité

    Réduction des conflits : aucun verrouillage nécessaire, améliorant l’efficacité.
    Répartition équilibrée : chaque Worker traite une charge équivalente, ce qui optimise l'utilisation des ressources.
    Adaptabilité aux architectures distribuées : le modèle fonctionne aussi bien sur des architectures multi-cœurs que sur des systèmes distribués.


# 4. Avantages du modèle Master/Worker

| **Critère**         | **Avantage** |
|---------------------|-------------|
| **Synchronisation** | Réduction des conflits grâce à des compteurs locaux |
| **Efficacité**      | Exécution parallèle optimisée sans accès concurrent à une variable critique |
| **Scalabilité**     | Supporte un grand nombre de Workers sans perte de performance |
| **Modularité**      | Adaptable aux architectures distribuées, avec des Workers sur différentes machines |

# III. Mise en œuvre sur Machine

Nous allons maintenant examiner deux implémentations pratiques de la méthode de Monte Carlo pour le calcul de π. L’objectif est d’analyser leur structure et leur approche de parallélisation :

- Identifier le modèle de programmation parallèle utilisé dans chaque code ainsi que le paradigme suivi (itération parallèle ou Master/Worker).
- Vérifier si ces implémentations correspondent aux algorithmes proposés en **partie II**.

Dans la **partie IV**, nous effectuerons une analyse détaillée de chaque code en évaluant leur **scalabilité forte et faible**.

---

## A. Analyse de *Assignment102*

L’implémentation *Assignment102* utilise l’API **Concurrent** pour paralléliser les calculs nécessaires à l’estimation de π avec la méthode de Monte Carlo. Voici les principaux éléments analysés :

### 1. Structure et API utilisée

#### Gestion des threads :
- Le code utilise `ExecutorService` avec un **pool de threads adaptatif** (`newWorkStealingPool`), exploitant efficacement les cœurs disponibles sur la machine.
- Chaque tirage (génération d’un point aléatoire) est exécuté dans une tâche indépendante via `Runnable`.

#### Synchronisation avec AtomicInteger :
- La variable partagée `nAtomSuccess`, qui compte le nombre de points dans le quart de disque, est protégée par un compteur atomique (`AtomicInteger`) pour éviter les **conflits d’accès** entre threads.

---

### 2. Modèle de programmation parallèle et paradigme

| **Aspect**        | **Détails** |
|------------------|------------|
| **Modèle utilisé** | **Itération parallèle** : chaque tirage correspond à une tâche indépendante soumise au pool de threads. |
| **Paradigme**     | Suit le modèle d’itération parallèle défini en **partie II.A**. Chaque tâche effectue un tirage de manière indépendante, sans dépendances entre elles. |

---

### 3. Lien avec notre pseudo-code

L’implémentation correspond globalement au pseudo-code d’**itération parallèle**, avec les adaptations suivantes :

- Le compteur `n_cible` est remplacé par un **AtomicInteger** pour gérer les **sections critiques**.
- Le découplage des threads est **géré par l’API `ExecutorService`**.

---


## B. Analyse de *Pi.java*

L’implémentation *Pi.java* repose sur l’utilisation des **Futures et Callables** pour paralléliser le calcul de π à l’aide de la méthode de Monte Carlo.

### 1. Qu’est-ce qu’un Future ?  
Un **Future** est un conteneur pour un résultat **calculé de manière asynchrone**. Il permet de :

- **Soumettre une tâche** : lorsqu’un thread exécute une tâche, son résultat est **stocké** dans un Future.
- **Récupérer le résultat** : l’appel à `.get()` récupère la valeur, mais **bloque** tant que le calcul n’est pas terminé.  
  → Cela introduit une **barrière implicite** qui synchronise les résultats des différents threads.
- **Vérifier l’état d’exécution** : un `Future` peut indiquer si une tâche est **terminée ou a échoué**.

**Pourquoi l’utiliser ici ?**  
Les **Futures** garantissent que chaque résultat partiel est **prêt avant l’agrégation**, permettant une synchronisation **optimale** entre les threads.

---

![Diagramme de Classe](./images/DiagrammeClasse.png)

---

### 2. Modèle de programmation parallèle et paradigme

| **Aspect**        | **Détails** |
|------------------|------------|
| **Modèle utilisé** | **Master/Worker** : le Master crée des Workers pour effectuer les calculs et regroupe leurs résultats à l’aide des **Futures**. |
| **Paradigme**     | Basé sur des **tâches**, avec une gestion explicite via **Callables**. |

---

### 3. Structure et API utilisée

#### 🔹 Parallélisation avec des Callables :
- Chaque **Worker** est un `Callable<Long>` qui exécute un sous-ensemble du calcul total.
- Il **compte les points dans le quart de disque** pour un certain nombre d’itérations.
- Ces Callables sont exécutés par un **pool de threads fixe** (`FixedThreadPool`).

#### 🔹 Gestion des résultats avec des Futures :
- Lorsqu’un `Callable` est soumis au pool de threads, il retourne un **Future<Long>**.
- L’appel à `.get()` bloque le thread principal jusqu’à ce que **tous les résultats** soient prêts.
- Une fois collectés, ces résultats sont **agrégés** pour calculer π.

---

### 4. Lien avec notre pseudo-code

L'algorithme suit fidèlement le modèle **Master/Worker** décrit en **partie II.B** :

| **Élément**       | **Correspondance dans le code** |
|------------------|--------------------------------|
| **Master**       | Gère la répartition des tâches et l’agrégation des résultats. |
| **Workers**      | Exécutés via des `Callables`, chaque Worker applique la méthode `MCWorker()`. |
| **Division**     | Le Master répartit **équitablement** les tirages (`n_charge`) entre les Workers. |

---

### 5. Comparaison avec *Assignment102*

| **Critère**           | **Pi.java (Master/Worker)** | **Assignment102 (Itération parallèle)** |
|----------------------|---------------------------|----------------------------------|
| **Isolation des calculs** | Chaque Worker gère ses propres données. | Accès concurrent à une variable atomique. |
| **Synchronisation** | Réduite à l’agrégation finale (moins coûteux). | Synchronisation fréquente via `AtomicInteger`. |
| **Gestion des threads** | Callables et `FixedThreadPool`. | `ExecutorService` avec `Runnable`. |
| **Performance attendue** | Plus efficace pour un grand nombre de threads. | Risque de **goulots d’étranglement** dû aux accès atomiques. |

---

🔎 **Conclusion générale**  
L’implémentation *Pi.java* est **plus efficace** qu’*Assignment102* car elle :
1. **Réduit la synchronisation coûteuse** (seule l’agrégation des résultats est bloquante).
2. **Minimise les accès concurrents** grâce à l’**isolation des Workers**.
3. **Optimise la gestion des threads** via les **Futures**, permettant une meilleure scalabilité.

On peut donc **s’attendre à de meilleures performances**, **surtout sur des machines multicœurs** et avec **un grand nombre de points et de threads**. 

# IV. Évaluations et tests de performances

La partie suivante contient des éléments d'un autre rapport que j'écris en parallèle de celui-ci, qui concerne le module de Qualité de Développement en troisième année de BUT informatique.

L'ordinateur qui a réalisé ces calculs possède les spécifications suivantes :

- **Processeur** : 11th Gen Intel(R) Core(TM) i7-11800H @ 2.30GHz
- **8 cœurs physiques**
- **16 cœurs logiques**

Notez que les résultats des tests qui suivent ne seront pas les mêmes selon l'architecture matérielle sur lesquels ils ont été effectués.

### A. Programme de calcul de performance

Le script **PerformanceTester.java** teste différentes implémentations de Monte Carlo pour calculer π en s'appuyant sur une interface standardisée **MonteCarloImplementation**. Chaque implémentation doit fournir deux méthodes : `execute(int totalPoints, int numCores)` et `getName()`.

Le programme récupère les données de tests (nombre de cœurs, de points, et répétitions) depuis un fichier CSV. Il exécute ensuite les tests sur chaque implémentation puis enregistre les résultats (temps d'exécution, approximation de π, erreur relative) dans un fichier `resultats.csv`.

L'outil est modulaire, ce qui permet d'ajouter facilement de nouvelles versions de Monte Carlo à tester. Il automatise également l'évaluation des performances pour différents scénarios et configurations.

---

### B. Tests de Scalabilité

Les tests de scalabilité servent à évaluer la performance des différentes implémentations en fonction du nombre de cœurs utilisés, selon deux approches : **scalabilité forte** et **scalabilité faible**.

##### 1. **Scalabilité Forte**

La **scalabilité forte** mesure la capacité d'un programme à réduire son temps d'exécution lorsque le nombre de cœurs augmente, tout en maintenant la charge de travail totale constante. Elle évalue comment le programme exploite efficacement les ressources supplémentaires.

On la mesure à l’aide du **speedup**, défini comme :

\[
\text{Speedup} = \frac{\text{Temps\_1\_cœur}}{\text{Temps\_N\_cœurs}}
\]

Un **speedup idéal** serait linéaire, c'est-à-dire un gain proportionnel au nombre de cœurs.

##### **Scénarios de test pour la scalabilité forte :**

| Nombre de processeurs | Nombre total de points | Points par processeur |
|-----------------------|------------------------|-----------------------|
| 1                     | 1,000,000              | 1,000,000             |
| 2                     | 1,000,000              | 500,000               |
| 4                     | 1,000,000              | 250,000               |
| 8                     | 1,000,000              | 125,000               |
| 16                    | 1,000,000              | 62,500                |
| 1                     | 10,000,000             | 10,000,000            |
| 2                     | 10,000,000             | 5,000,000             |
| 4                     | 10,000,000             | 2,500,000             |
| 8                     | 10,000,000             | 1,250,000             |
| 16                    | 10,000,000             | 625,000               |
| 1                     | 100,000,000            | 100,000,000           |
| 2                     | 100,000,000            | 50,000,000            |
| 4                     | 100,000,000            | 25,000,000            |
| 8                     | 100,000,000            | 12,500,000            |
| 16                    | 100,000,000            | 6,250,000             |

Dans ce cas, on garde le nombre total de points constant et on varie le nombre de processeurs pour observer la réduction du temps d'exécution. Le **speedup** est mesuré pour chaque configuration.

##### 2. **Scalabilité Faible**

La **scalabilité faible** évalue la capacité d'un programme à maintenir un temps d'exécution constant lorsque le nombre de cœurs et la charge de travail totale augmentent proportionnellement. Cela simule un scénario où chaque cœur traite une part fixe de travail supplémentaire.

Elle est également mesurée à l’aide du **speedup**, calculé de la même manière que pour la scalabilité forte, mais ici avec une charge croissante. Un **speedup idéal** serait constant, ce qui se traduirait par une droite horizontale.

##### **Scénarios de test pour la scalabilité faible :**

| Nombre de processeurs | Nombre total de points | Points par processeur |
|-----------------------|------------------------|-----------------------|
| 1                     | 1,000,000              | 1,000,000             |
| 2                     | 2,000,000              | 1,000,000             |
| 4                     | 4,000,000              | 1,000,000             |
| 8                     | 8,000,000              | 1,000,000             |
| 16                    | 16,000,000             | 1,000,000             |
| 1                     | 10,000,000             | 10,000,000            |
| 2                     | 20,000,000             | 10,000,000            |
| 4                     | 40,000,000             | 10,000,000            |
| 8                     | 80,000,000             | 10,000,000            |
| 16                    | 160,000,000            | 10,000,000            |
| 1                     | 100,000,000            | 100,000,000           |
| 2                     | 200,000,000            | 100,000,000           |
| 4                     | 400,000,000            | 100,000,000           |
| 8                     | 800,000,000            | 100,000,000           |
| 16                    | 1,600,000,000          | 100,000,000           |

Dans ce cas, on augmente proportionnellement la charge de travail avec le nombre de cœurs pour tester la capacité du programme à maintenir un temps d'exécution constant.

---
### C. Résultats de l'implémentation Assignment102

Pour évaluer la scalabilité de l'implémentation **Assignment102**, le code a été modifié afin de permettre un contrôle précis du nombre de processeurs utilisés. Cela remplace l'utilisation dynamique de `Runtime.getRuntime().availableProcessors()` et nous permet de fixer le nombre de cœurs pour chaque test via l'initialisation de la classe `PiMonteCarlo`.

#### 1. Résultats de la Scalabilité Forte

Les tests de **scalabilité forte** ont été effectués en lançant plusieurs simulations avec des configurations variées de nombre de cœurs et de points. Chaque test a été répété cinq fois pour calculer la moyenne des résultats.

| Nombre de cœurs | Points lancés | Points par cœur | Temps d'exécution (ms) | Approximation de π | Erreur              |
|-----------------|---------------|-----------------|------------------------|-------------------|---------------------|
| 1               | 1,000,000     | 1,000,000       | 92.0                   | 3.1414976         | 3.03 × 10⁻⁵        |
| 2               | 1,000,000     | 500,000         | 84.2                   | 3.1398992         | 5.39 × 10⁻⁴        |
| 4               | 1,000,000     | 250,000         | 92.6                   | 3.14152           | 2.31 × 10⁻⁵        |
| 8               | 1,000,000     | 125,000         | 126.2                  | 3.1420408         | 1.43 × 10⁻⁴        |
| 16              | 1,000,000     | 62,500          | 127.2                  | 3.1411            | 1.57 × 10⁻⁴        |
| 1               | 10,000,000    | 10,000,000      | 836.8                  | 3.14186112        | 8.55 × 10⁻⁵        |
| 2               | 10,000,000    | 5,000,000       | 206.4                  | 3.14125008        | 1.09 × 10⁻⁴        |
| 4               | 10,000,000    | 2,500,000       | 819.8                  | 3.14155624        | 1.16 × 10⁻⁵        |
| 8               | 10,000,000    | 1,250,000       | 869.0                  | 3.1418644         | 8.65 × 10⁻⁵        |
| 16              | 10,000,000    | 625,000         | 925.4                  | 3.14146304        | 4.13 × 10⁻⁵        |
| 1               | 100,000,000   | 100,000,000     | 8,788.0                | 3.141590984       | 5.31 × 10⁻⁷        |
| 2               | 100,000,000   | 50,000,000      | 8,358.2                | 3.141534816       | 1.84 × 10⁻⁵        |
| 4               | 100,000,000   | 25,000,000      | 8,743.0                | 3.141645944       | 1.70 × 10⁻⁵        |
| 8               | 100,000,000   | 12,500,000      | 8,728.4                | 3.141609744       | 5.44 × 10⁻⁶        |
| 16              | 100,000,000   | 6,250,000       | 8,625.2                | 3.1416518         | 1.88 × 10⁻⁵        |

En utilisant un programme Python pour calculer le **speedup** et générer un graphique, les résultats de scalabilité forte montrent un **speedup** initial qui commence à 1, mais diminue ensuite avant de se stabiliser sous 1 : 

---

![speedup assignment102](./images/scalabilite_forte_Assignment102_10000000.png)

---

##### Analyse des résultats de scalabilité forte :

- **Surcharge de synchronisation** : L’utilisation de `AtomicInteger` pour gérer les ressources partagées introduit de la latence, ralentissant les threads au fur et à mesure que leur nombre augmente.
- **Overhead lié aux threads** : La gestion des threads devient coûteuse au-delà d'un certain nombre de cœurs, annulant les gains attendus de la parallélisation.
- **Tâches trop petites** : Lorsque la charge de travail par processeur devient trop petite, l’overhead de la synchronisation dépasse les bénéfices de la parallélisation.

Ces facteurs expliquent la baisse du **speedup** et suggèrent que l'implémentation n'est pas optimale au-delà d'un certain nombre de cœurs.

#### 2. Résultats de la Scalabilité Faible

Les tests de **scalabilité faible** ont également été effectués pour observer l'impact de l'augmentation du nombre de points, tout en augmentant proportionnellement le nombre de cœurs.

| Nombre de cœurs | Points lancés | Points par cœur | Temps d'exécution (ms) | Approximation de π | Erreur              |
|-----------------|---------------|-----------------|------------------------|-------------------|---------------------|
| 1               | 1,000,000     | 1,000,000       | 120.2                  | 3.1432696         | 5.34 × 10⁻⁴        |
| 2               | 2,000,000     | 1,000,000       | 205.0                  | 3.141348          | 7.79 × 10⁻⁵        |
| 4               | 4,000,000     | 1,000,000       | 354.6                  | 3.1415638         | 9.18 × 10⁻⁶        |
| 8               | 8,000,000     | 1,000,000       | 773.6                  | 3.1415113         | 2.59 × 10⁻⁵        |
| 16              | 16,000,000    | 1,000,000       | 1456.8                 | 3.1415671         | 8.13 × 10⁻⁶        |
| 1               | 10,000,000    | 10,000,000      | 943.8                  | 3.14194448        | 1.12 × 10⁻⁴        |
| 2               | 20,000,000    | 10,000,000      | 1826.0                 | 3.14176164        | 5.38 × 10⁻⁵        |
| 4               | 40,000,000    | 10,000,000      | 3875.0                 | 3.14177182        | 5.70 × 10⁻⁵        |
| 8               | 80,000,000    | 10,000,000      | 10112.6                | 3.14151361        | 2.52 × 10⁻⁵        |
| 16              | 160,000,000   | 10,000,000      | 20315.6                | 3.141623355       | 9.77 × 10⁻⁶        |
| 1               | 100,000,000   | 100,000,000     | 11313.8                | 3.141555048       | 1.20 × 10⁻⁵        |

Les tests de scalabilité faible montrent une dégradation du **speedup**, particulièrement lorsqu’on double le nombre de points. Ce comportement est attendu, étant donné la limitation d'optimisation observée lors des tests de scalabilité forte : 

---

![speedup assignment 102](./images/scalabilite_faible_Assignment102_10000000.png)

---

##### Analyse des résultats de scalabilité faible :

Le **speedup** est loin d'être linéaire et semble diminuer proportionnellement au nombre de points ajoutés. Chaque fois que l’on double le nombre de points, le **speedup** est réduit de moitié.

Cette tendance n'est pas surprenante, étant donné les résultats obtenus en scalabilité forte. Le **speedup** presque linéaire de cette dernière suggère que l'ajout de cœurs n’a pas d'impact significatif sur la performance du programme. Par conséquent, doubler le nombre de points mène logiquement à un temps d'exécution deux fois plus long.

---

### D. Résultats Pi.Java

Aucune modification n'a été nécessaire pour la classe Pi.Java, car elle intègre déjà une option permettant de limiter le nombre de workers.

#### Tableau des résultats de scalabilité forte

Les tests de scalabilité forte ont été répétés 5 fois pour calculer une moyenne. Voici les résultats obtenus :

| Nombre de cœurs | Points lancés | Points par cœur | Temps d'exécution (ms) | Approximation de π | Erreur       |
|-----------------|---------------|-----------------|------------------------|--------------------|--------------|
| 1               | 1,000,000     | 1,000,000       | 56.8                   | 3.1431048          | 4.81 × 10⁻⁴ |
| 2               | 1,000,000     | 500,000         | 27.0                   | 3.141724           | 4.18 × 10⁻⁵ |
| 4               | 1,000,000     | 250,000         | 14.4                   | 3.1408             | 2.52 × 10⁻⁴ |
| 8               | 1,000,000     | 125,000         | 11.6                   | 3.1418616          | 8.56 × 10⁻⁵ |
| 16              | 1,000,000     | 62,500          | 8.0                    | 3.1423224          | 2.32 × 10⁻⁴ |
| 1               | 10,000,000    | 10,000,000      | 414.0                  | 3.14151048         | 2.62 × 10⁻⁵ |
| 2               | 10,000,000    | 5,000,000       | 217.8                  | 3.1416816          | 2.83 × 10⁻⁵ |
| 4               | 10,000,000    | 2,500,000       | 122.4                  | 3.14141312         | 5.71 × 10⁻⁵ |
| 8               | 10,000,000    | 1,250,000       | 68.4                   | 3.14099672         | 1.90 × 10⁻⁴ |
| 16              | 10,000,000    | 625,000         | 59.0                   | 3.14134416         | 7.91 × 10⁻⁵ |
| 1               | 100,000,000   | 100,000,000     | 4335.0                 | 3.141607864        | 4.84 × 10⁻⁶ |
| 2               | 100,000,000   | 50,000,000      | 2421.2                 | 3.141605256        | 4.01 × 10⁻⁶ |
| 4               | 100,000,000   | 25,000,000      | 1250.4                 | 3.141652968        | 1.92 × 10⁻⁵ |
| 8               | 100,000,000   | 12,500,000      | 677.4                  | 3.141578304        | 4.57 × 10⁻⁶ |
| 16              | 100,000,000   | 6,250,000       | 412.0                  | 3.141576256        | 5.22 × 10⁻⁶ |

#### Courbe de scalabilité forte

---

![speedup pi](./images/scalabilite_forte_Pi.java_100000000.png)

---


La courbe de scalabilité forte suit une trajectoire presque linéaire sur une large plage de points, avant de légèrement dévier au-delà de 8 cœurs. Cependant, le speedup reste croissant, ce qui témoigne de l'efficacité de la parallélisation.

Avec 8 cœurs physiques (2 cœurs logiques chacun), l'implémentation atteint une performance équivalente à environ 11 cœurs logiques, confirmant l'efficacité de la parallélisation dans ce code.

#### Tableau des résultats de scalabilité faible

Les tests de scalabilité faible ont également été réalisés, avec les résultats suivants :

| Nombre de cœurs | Points lancés | Points par cœur | Temps d'exécution (ms) | Approximation de π | Erreur       |
|-----------------|---------------|-----------------|------------------------|--------------------|--------------|
| 1               | 1,000,000     | 1,000,000       | 63.0                   | 3.1423984          | 2.56 × 10⁻⁴ |
| 2               | 2,000,000     | 1,000,000       | 56.2                   | 3.1412836          | 9.84 × 10⁻⁵ |
| 4               | 4,000,000     | 1,000,000       | 64.6                   | 3.1418568          | 8.41 × 10⁻⁵ |
| 8               | 8,000,000     | 1,000,000       | 109.6                  | 3.1417325          | 4.45 × 10⁻⁵ |
| 16              | 16,000,000    | 1,000,000       | 156.4                  | 3.14142355         | 5.38 × 10⁻⁵ |
| 1               | 10,000,000    | 10,000,000      | 530.8                  | 3.14203488         | 1.41 × 10⁻⁴ |
| 2               | 20,000,000    | 10,000,000      | 552.0                  | 3.14175008         | 5.01 × 10⁻⁵ |
| 4               | 40,000,000    | 10,000,000      | 534.4                  | 3.14163064         | 1.21 × 10⁻⁵ |
| 8               | 80,000,000    | 10,000,000      | 579.4                  | 3.14150764         | 2.71 × 10⁻⁵ |
| 16              | 160,000,000   | 10,000,000      | 776.2                  | 3.1415749          | 5.65 × 10⁻⁶ |
| 1               | 100,000,000   | 100,000,000     | 4662.4                 | 3.141613336        | 6.58 × 10⁻⁶ |
| 2               | 200,000,000   | 100,000,000     | 5105.6                 | 3.14153096         | 1.96 × 10⁻⁵ |
| 4               | 400,000,000   | 100,000,000     | 5256.0                 | 3.141570872        | 6.93 × 10⁻⁶ |
| 8               | 800,000,000   | 100,000,000     | 7791.6                 | 3.141575614        | 5.42 × 10⁻⁶ |
| 16              | 1,600,000,000 | 100,000,000     | 8710.6                 | 3.141579264        | 4.26 × 10⁻⁶ |

#### Courbe de scalabilité faible

---

![speedup pi](./images/scalabilite_faible_Pi.java_100000000.png)

---


Le speedup décroît lentement à mesure que le nombre de processeurs augmente. Bien que cette décroissance soit modérée par rapport à l'Assignment102, le speedup passe de 1 (avec un seul processeur) à environ 0,75 avec 16 processeurs.

Cette baisse indique que le code Pi.java perd en efficacité parallèle avec l'augmentation des ressources disponibles, mais cette perte reste contenue. Comparé à Assignment102, où la scalabilité chute bien plus rapidement, les résultats de Pi.java restent globalement satisfaisants.

# V. Mise en œuvre en mémoire distribuée

Les analyses précédentes ont montré que le paradigme Master/Worker offre une meilleure parallélisation par rapport à l’approche d'Assignment102. Nous allons maintenant transposer cet algorithme sur une architecture à mémoire distribuée.

### Paradigme Master/Worker et Architecture Client/Serveur

Le modèle Master/Worker est souvent opposé au paradigme Client/Serveur. Dans le cadre de cette architecture, le Maître agit comme un client, tandis que les Workers jouent le rôle de serveurs. Nous allons implémenter cette approche sur une architecture distribuée avec des échanges via des sockets Java. Bien que le code de base fonctionne déjà, la partie calcul de Pi n’est pas encore intégrée.

### Diagramme d'exécution du code

Dans cette architecture, un *Master Socket* initialise l’expérience Monte Carlo. Il répartit les tâches en attribuant un nombre de points à chaque *Worker Socket*. Chaque Worker calcule les résultats (actuellement une valeur approximative) et renvoie ces résultats au Master.

### Diagramme de classes UML

Les échanges entre le *Master Socket* et les *Worker Sockets* s'appuient sur les classes de la bibliothèque `java.net`. Les flux de données sont gérés par `InputStreamReader` et `OutputStreamWriter`, tandis que `PrintWriter` et `BufferedWriter` sont utilisés pour envoyer des messages, et `BufferedReader` pour les lire.

Pour exécuter le programme, il est nécessaire de lancer différentes instances de `WorkerSocket` et `MasterSocket`. Lors du lancement de `WorkerSocket`, il faut spécifier le port pour l’envoi et la réception des flux de données. De son côté, le *MasterSocket* demande à l'utilisateur d'entrer le nombre de Workers et leurs ports.

**Note :** Les ports saisis lors du lancement du *Master* n'ont pas d'importance, car le programme affecte automatiquement les ports 25545, 25546, etc., jusqu'à avoir suffisamment de ports pour chaque Worker.

### A. Implémentation du calcul par méthode

#### Nouvelle classe WorkerSocket

Une méthode `performMonteCarloComputation` a été ajoutée à la classe `WorkerSocket`. Elle est appelée lorsque le Worker reçoit une demande de calcul, ce qui lui permet de traiter sa part des points Monte Carlo et de renvoyer le résultat au Master.

### B. Implémentation du calcul en utilisant Pi.java

Bien qu’une méthode dédiée au calcul Monte Carlo soit fonctionnelle, une approche plus intéressante consiste à réutiliser l'algorithme Pi.java. Ainsi, la classe *Master* de Pi.java est intégrée directement dans le `WorkerSocket`.

Cela donne lieu à une architecture Master/Worker multi-niveaux :

1. **Niveau 1 :** Un *Master Socket* répartit les tâches entre plusieurs `Worker Sockets` sur une architecture à mémoire distribuée.
2. **Niveau 2 :** Chaque `Worker Socket` devient un Master d'une architecture Master/Worker à mémoire partagée, en intégrant l'implémentation de Pi.java.

Ce modèle, appelé Programmation Multi-Niveaux, exploite les avantages de deux types de parallélisme : 
- Le parallélisme sur mémoire distribuée au niveau supérieur.
- Le parallélisme sur mémoire partagée au niveau inférieur.
