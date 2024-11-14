# Rapport TP2
*Ce rapport a été réalisé avec l’aide de l’IA*
## Introduction

Dans le cadre de ce TP, nous avons exploré l'utilisation de threads en Java pour synchroniser deux tâches (TA et TB) en utilisant la classe `Affichage` pour gérer l'affichage à l'écran. L'objectif est de garantir l'exclusion mutuelle entre les threads, afin que leurs messages s'affichent dans des séquences prédéfinies (AAABB ou BBAAA) et non de manière entrelacée (ABABA). Pour ce faire, nous avons utilisé des sémaphores binaires pour contrôler l'accès aux sections critiques des threads.

## Analyse du Code Existant

### Classe `Affichage`

La classe `Affichage` hérite de `Thread`, et chaque instance prend une chaîne de caractères (`String`) à afficher. La méthode `run` parcourt chaque caractère et l'affiche, avec une pause de 100 millisecondes entre chaque caractère, pour simuler un délai dans l'affichage.

### Classe `semaphoreBinaire`

Le rôle de cette classe est de contrôler l'accès à la section critique, c'est-à-dire l'endroit où une tâche peut afficher son message sans interruption par une autre. Le sémaphore binaire est modélisé pour avoir deux états : "libre" ou "occupé".

#### Incrémentation et Décrémentation du Sémaphore

Lorsqu'un sémaphore binaire est initialisé, il reçoit une valeur (généralement 1 ou 0). Cette valeur représente la disponibilité de la section critique :
- **Décrémentation** : Lorsqu'une tâche veut entrer en section critique, elle décrémente la valeur du sémaphore. Si la valeur atteint 0, la tâche obtient l'accès à la section critique. Toute autre tâche essayant de décrémenter à 0 sera bloquée jusqu’à ce que le sémaphore soit réinitialisé.
- **Incrémentation** : Lorsqu’une tâche a terminé son travail en section critique, elle incrémente la valeur. Cela libère la section critique et permet à une autre tâche en attente d'y entrer.


## Classe `Main`

L'objectif de cette classe est de s'assurer que les messages "AAA" et "BB", générés respectivement par les tâches `taskA` et `taskB`, s'affichent bien en utilisant la classe semaphoreBinaire, et de vérifier qu'on obtiens pas de résultat du type "ABABA".