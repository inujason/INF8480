# Overlord

Overlord est un projet qui consiste à invoquer des appels de manière distante à un server par le client.
Le projet est un simple programme qui permet le sauvegarde et le versionnement de fichier texte.
Nécessairement, nous avons un mécanisme de protection contre les conflits qui évite l'écriture.

## Getting Started

Les intructions suivantes vous permettront d'installer le programme.

### Prerequisites

What things you need to install the software and how to install them

```
Rien du tout
```

### Installing

#### Environnement du serveur

Ouvrir un terminal et tappez (vous devez etre aux reseaux polymtl):

Connectez vous au nuage dans le reseau de lecole
```
ssh -i cloudKey ubuntu@132.204.12.104
```

#### Serveur

Veuillez telecharger ou installer notre projet dans un serveur (environnement) distant de celui du client si il n'est pas deja installe
sinon veuillez seulement passez a l'etape B)

A) Entrez dans le Fichier INF8480_TP1 jusqu'au Overlord
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
rmiregistry&
```
Roulez le server
```
cd ..
./server&
```

#### Client

Telecharger le projet dans un repertoire que vous desirez et soyez dans le reseau de Poly
Aller dans le repertoire Overload, compilez et activez le rmiregistry pour le client
```
ant
cd bin
rmiregistry&
cd ..
```
Tapez la commande que vous souhaitee
```
./client COMMAND (nomFichier)
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

