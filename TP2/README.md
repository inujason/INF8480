# SDGP

SDGP est un projet qui consiste à faire des calculs à l'aide de serveur de calculs.
Le projet est un simple programme qui permet d'envoyer des opérations sur un serveur, de recevoir les resultats et de d'afficher la somme des différents résultats. Pour ce faire nous avons un service de nom disponible sur un cloud.

## Getting Started

Les intructions suivantes vous permettront d'installer le programme.

### Prerequisites

What things you need to install the software and how to install them

```
Rien du tout
```

### Installing

#### Environnement du serveur de noms

Ouvrir un terminal et tappez (vous devez etre aux reseaux polymtl):

Connectez vous au nuage dans le reseau de lecole
```
ssh -i cloudKey ubuntu@132.204.12.104
```

#### Serveur de noms

Veuillez telecharger ou installer notre projet dans un serveur (environnement) distant de celui du client si il n'est pas deja installe
sinon veuillez seulement passez a l'etape B)

A) Entrez dans le Fichier INF8480_TP2 jusqu'au Overlord
```
cd INF8480_TP1
cd Overlord
```
Charger le projet dans l'environnement

```
scp -i cloudKey -r ../../INF8480 ubuntu@132.207.12.104:
```
B)Retournez au terminal de lenvironnement serveur
Entrez au repertoire Overlord, compilez et activez le rmiregistry
```
ant
cd bin
rmiregistry 5001&
```
Roulez le server
```
cd ..
./serveurService
```

#### Serveur

Telecharger le projet dans un repertoire que vous desirez et soyez dans le reseau de Poly
Aller dans le repertoire Overload, compilez et activez le rmiregistry pour le Serveur

Entrez au repertoire Overlord, compilez et activez le rmiregistry
```
ant
cd bin
rmiregistry&
```
Roulez le server
```
cd ..
./serveur
```
Le server peut aussi etre rouler avec un taux de malice compris entre 0 et 100
```
cd ..
./serveur 65
```

#### Client

Telecharger le projet dans un repertoire que vous desirez et soyez dans le reseau de Poly
Aller dans le repertoire Overload, compilez
Tapez la commande que vous souhaitee
```
./repartiteur (nomFichier) [-s]
```

## Built With

* [Ant](http://ant.apache.org/)

## Contributing

Voici notre [git](https://github.com/inujason/INF8480_TP1) pour obtenir le code:


## Versioning

On utilise [git](https://github.com/) pour se charger du versionnement du code.

## Authors

* **Hardy Voudou** - *Overlord* -
* **Jason Li** - *Overlord* -
Vous aimez notre README?
Contribuez et laissez un like ^____~
