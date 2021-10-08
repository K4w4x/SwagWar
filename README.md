# Swagwar
> Un CoreWar jouable dans le terminal en mode texte

## Table des matières
- [Swagwar](#swagwar)
  - [Table des matières](#table-des-matières)
  - [Présentation](#présentation)
  - [Fonctionnalités](#fonctionnalités)
  - [Captures](#captures)
  - [Manuel](#manuel)
  - [Statut](#statut)
  - [Contact](#contact)


## Présentation
> Fait dans le cadre d'un projet d'université, le SwagWar permet d'émuler une partie de CoreWar.

Le concept du Corewar, jeu de programmation introduit en 1984, est de faire s’affronter
dans une grille des programmes appelés "Warriors". Ces derniers sont consitués d’un ensemble
de commandes écrites dans un langage exclusif au Corewar appelé "Redcode". Les joueurs sont
alors invités à créer des programmes performants dans ce jeu. La partie débute en installant
les Warriors aléatoirement dans la grille et en les faisant exécuter des instructions selon une
queue, jusqu’à ce qu’il ne reste plus qu’un joueur.

Le but de ce projet était de proposer une émulation complète et fonctionnelle du jeu de Corewar
et d'y implémenter un algorithme génétique permettant de créer des Warriors fonctionnels.

## Fonctionnalités
La liste de fonctionnalités du SwagWar est la suivante :
- Émuler les différentes commandes de Redcode, hors les commandes de P-Space
- Afficher le cours de la partie dans le terminal en différenciant les joueurs par couleur
- Lire et comprendre des fichiers de Warrior, les implémenter et les faire jouer
- Permettre à l'utilisateur de rentrer ses propres commandes de Redcode depuis le terminal
- Créer des Warriors de par un algorithme génétique 
- Permettre à l'utilisateur de régler la taille de la grille et divers paramètres 


## Captures
![Example screenshot](./img/screenshot.png)
<!-- If you have screenshots you'd like to share, include them here. -->


## Manuel
Pour lancer le programme sous Windows, double-cliquer sur start.bat.

Si vous possédez [ANT](http://ant.apache.org/), pour lancer le programme, utiliser :
```sh
ant run
```
Sinon, utiliser la commande :
```sh
jar -jar "dist/SwagWar.jar"
```
Les étapes à suivre seront indiquées dans le terminal.

Pour utiliser l'algorithnme génétique, il est nécessaire d'utiliser exactement dix Warriors.

## Statut
Le projet est terminé et n'est plus mis à jour.

## Contact
Le projet fut réalisé par l'équipe suivante :
- Violette Dev [@Wormy-iwtd](https://github.com/Wormy-iwtd)
- Kawax [@K4w4x](https://github.com/K4w4x)
- EdoudZz [@EdoudZz](https://github.com/EdoudZz)
- NMW-Dev [@NMW-Dev](https://github.com/NMW-Dev)
