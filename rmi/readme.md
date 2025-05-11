# Application de Chat en Java RMI

## Présentation du Projet

Ce projet est une application de chat distribuée utilisant **Java RMI** (Remote Method Invocation). Elle permet à plusieurs utilisateurs de se connecter à un salon de discussion et d'échanger des messages en temps réel. Le serveur distribue automatiquement les messages envoyés à tous les utilisateurs connectés.

## Fonctionnement Technique

L'application utilise l'architecture RMI (Remote Method Invocation) de Java qui permet l'appel de méthodes sur des objets s'exécutant dans une JVM différente, potentiellement sur une machine distante. Le fonctionnement peut être résumé ainsi :

1. Le serveur expose un objet ChatRoom via le registre RMI
2. Les clients se connectent au serveur en recherchant l'objet ChatRoom dans le registre
3. Chaque client s'abonne au salon de discussion en fournissant une implémentation de l'interface ChatUser
4. Lorsqu'un message est envoyé, le serveur le transmet à tous les clients abonnés en appelant leur méthode displayMessage()

Cette architecture permet une communication bidirectionnelle en temps réel entre le serveur et les clients.

## Structure du Projet

Le projet est organisé en plusieurs packages, chacun avec un rôle spécifique :

### Packages Principaux

- **room/** : Contient les composants côté serveur
  - `ChatRoom.java` : Interface définissant les méthodes RMI accessibles par les clients (`subscribe`, `unsubscribe`, `postMessage`)
  - `ChatRoomImpl.java` : Implémentation de l'interface qui gère les utilisateurs connectés et distribue les messages

- **user/** : Contient les composants côté client pour la communication avec le serveur
  - `ChatUser.java` : Interface définissant la méthode `displayMessage` que le serveur peut invoquer à distance
  - `ChatUserImpl.java` : Implémentation de l'interface qui affiche les messages reçus dans l'interface client

- **server/** : Contient le point d'entrée du serveur
  - `ChatServer.java` : Classe principale qui démarre le registre RMI et instancie le service ChatRoom

- **window/** : Contient l'interface graphique du client
  - `ChatWindow.java` : Fenêtre de chat avec tous les composants graphiques et la logique d'interaction

### Caractéristiques Clés

- **Communication bidirectionnelle** : Le serveur peut appeler des méthodes sur les clients connectés
- **Persistance temporaire** : Les messages sont conservés tant que le serveur est en cours d'exécution
- **Notification des événements** : Les connexions et déconnexions sont annoncées à tous les utilisateurs
- **Interface graphique intuitive** : Messages colorés et bouton de déconnexion pour une meilleure expérience utilisateur
  - `ChatUserImpl.java` : Implémentation liée à l'interface graphique pour afficher les messages reçus

- **server/** : Contient le code du serveur de chat
  - `ChatServer.java` : Initialise le registre RMI et expose le salon de discussion

- **window/** : Contient l'interface graphique
  - `ChatWindow.java` : Interface graphique Java Swing permettant aux utilisateurs d'interagir avec le système

- **bin/** : Répertoire généré contenant les fichiers compilés (.class)

## Prérequis

- JDK (Java Development Kit) 8 ou supérieur
- Connaissance de base de Java et des applications client-serveur

## Compilation et Exécution

### 1. Compilation

Pour compiler le projet, exécutez la commande suivante à partir de la racine du projet :

```bash
javac -d bin room/*.java server/*.java user/*.java window/*.java
```

Cette commande compile tous les fichiers Java et place les fichiers .class dans le répertoire `bin/`.

### 2. Démarrage du serveur

Après la compilation, démarrez le serveur RMI avec la commande :

```bash
java -cp bin server.ChatServer
```

Vous devriez voir s'afficher : `Serveur du ChatRoom démarré...`

### 3. Démarrage des clients

Ouvrez un nouveau terminal pour chaque client que vous souhaitez connecter et exécutez :

```bash
java -cp bin window.ChatWindow
```

Une fenêtre de chat s'ouvrira et vous demandera d'entrer un pseudo. Chaque client doit utiliser un pseudo unique.

## Fonctionnement

1. Le serveur démarre et expose un objet `ChatRoom` via RMI
2. Les clients se connectent au serveur et s'abonnent au salon avec un pseudo unique
3. Lorsqu'un client envoie un message, il est transmis au serveur via la méthode `postMessage`
4. Le serveur distribue le message à tous les clients connectés via leur méthode `displayMessage`
5. Quand un client se déconnecte (fermeture de la fenêtre), il est automatiquement désabonné du salon

## Résolution des problèmes courants

- Si vous rencontrez une erreur de connexion au serveur, vérifiez que le serveur est bien démarré et que `localhost` est correctement résolu
- Si plusieurs utilisateurs utilisent le même pseudo, seul le premier pourra envoyer des messages sous ce nom

## Extensions possibles

Le système peut être étendu pour ajouter :
- Support de plusieurs salons de discussion
- Messages privés entre utilisateurs
- Persistance des messages
- Transfert de fichiers