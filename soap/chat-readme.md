# Application de Chat basée sur SOAP

## Présentation du Projet

Cette application implémente un système de chat en temps réel utilisant le protocole **SOAP** (Simple Object Access Protocol). Elle est composée d'un service web déployé sur Apache Tomcat avec Axis et d'un client Java Swing permettant aux utilisateurs d'interagir avec le système de chat de façon intuitive.

## Structure du projet

Le projet comprend deux fichiers principaux :

- **ChatRoom.jws** : Service web SOAP qui gère la salle de chat, les utilisateurs connectés et les messages.
- **ChatClient.java** : Client Java avec interface graphique qui se connecte au service web.

## Fonctionnalités

- Connexion avec un pseudo unique
- Envoi et réception de messages en temps réel
- Liste des utilisateurs connectés
- Messages système (connexion, déconnexion, timeout)
- Session timeout pour les utilisateurs inactifs

## Prérequis

- Java 8 (JDK 1.8)
- Apache Tomcat 6.0.37
- Apache Axis
- Les bibliothèques JAR mentionnées dans le README principal du projet

## Compilation et Exécution

### 1. Vérification du serveur

Assurez-vous que Tomcat est démarré et que le service SOAP est bien déployé :
```
http://localhost:8080/axis/ChatRoom.jws?wsdl
```

Vous devriez voir le document WSDL de la salle de chat.

### 2. Compilation du client

#### Pour Windows
```batch
javac -cp ".;.\lib\axis.jar;.\lib\commons-discovery-0.2.jar;.\lib\commons-logging-1.0.4.jar;.\lib\jaxrpc.jar;.\lib\saaj.jar;.\lib\wsdl4j-1.5.1.jar;.\lib\activation.jar;.\lib\javax.mail.jar;.\lib\xerces.jar" ChatClient.java
```

#### Pour Linux/macOS
```bash
javac -cp ".:./lib/axis.jar:./lib/commons-discovery-0.2.jar:./lib/commons-logging-1.0.4.jar:./lib/jaxrpc.jar:./lib/saaj.jar:./lib/wsdl4j-1.5.1.jar:./lib/activation.jar:./lib/javax.mail.jar:./lib/xerces.jar" ChatClient.java
```

### 3. Exécution du client

#### Pour Windows
```batch
java -cp ".;.\lib\axis.jar;.\lib\commons-discovery-0.2.jar;.\lib\commons-logging-1.0.4.jar;.\lib\jaxrpc.jar;.\lib\saaj.jar;.\lib\wsdl4j-1.5.1.jar;.\lib\activation.jar;.\lib\javax.mail.jar;.\lib\xerces.jar" ChatClient
```

#### Pour Linux/macOS
```bash
java -cp ".:./lib/axis.jar:./lib/commons-discovery-0.2.jar:./lib/commons-logging-1.0.4.jar:./lib/jaxrpc.jar:./lib/saaj.jar:./lib/wsdl4j-1.5.1.jar:./lib/activation.jar:./lib/javax.mail.jar:./lib/xerces.jar" ChatClient
```

### 4. Utilisation

1. Lancez plusieurs instances du client pour simuler différents utilisateurs
2. Connectez-vous avec un pseudo unique pour chaque instance
3. Envoyez des messages qui seront visibles par tous les utilisateurs connectés
4. Vous pouvez voir la liste des utilisateurs connectés sur le panneau de droite
5. Les messages système apparaîtront en rouge
6. Vos propres messages apparaîtront en bleu

## Fonctionnement technique

### Côté serveur

Le service web SOAP (`ChatRoom.jws`) implémente plusieurs méthodes :
- `login` : Connecte un utilisateur au système
- `logout` : Déconnecte un utilisateur
- `sendMessage` : Enregistre un message envoyé par un utilisateur
- `getNewMessages` : Retourne les nouveaux messages depuis le dernier ID connu
- `getConnectedUsers` : Retourne la liste des utilisateurs connectés

Le serveur inclut un mécanisme de timeout qui déconnecte automatiquement les utilisateurs inactifs après une minute.

### Côté client

Le client (`ChatClient.java`) utilise une architecture de polling pour récupérer périodiquement :
1. Les nouveaux messages
2. La liste mise à jour des utilisateurs connectés

Cette approche remplace le modèle d'appel direct du serveur vers le client utilisé dans RMI.

## Différence avec l'implémentation RMI

Contrairement à RMI qui permet des appels bidirectionnels (serveur vers client et client vers serveur), SOAP suit un modèle requête-réponse unidirectionnel. Pour simuler les notifications en temps réel, nous utilisons un mécanisme de polling où le client interroge périodiquement le serveur pour obtenir les mises à jour.

## Extensibilité

Ce projet peut être étendu avec des fonctionnalités supplémentaires comme :
- Salles de discussion multiples
- Messages privés
- Transfert de fichiers
- Interface web HTML/JavaScript
