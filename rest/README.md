# Application de Chat RESTful avec JAX-RS

## Présentation du Projet

Cette application implémente un service de chat en temps réel utilisant une **architecture RESTful** avec **JAX-RS** et **Jersey**. Contrairement aux implémentations précédentes en RMI et SOAP qui utilisent un modèle d'appel de procédure à distance, cette version exploite pleinement les principes REST basés sur les ressources et les opérations HTTP standard, offrant une meilleure extensibilité et compatibilité avec les protocoles web modernes.

## Fonctionnement Technique

L'application exploite l'architecture REST (REpresentational State Transfer) qui se caractérise par :

1. **Sans état** : Chaque requête HTTP du client au serveur contient toutes les informations nécessaires pour la traiter
2. **Ressources** : Les entités (utilisateurs, messages) sont représentées comme des ressources accessibles via des URL
3. **Opérations HTTP standard** : GET pour la lecture, POST pour la création, DELETE pour la suppression
4. **Représentations** : Les données sont échangées principalement au format JSON

Le workflow typique du chat est le suivant :

1. Un client s'enregistre en envoyant son nom d'utilisateur via POST
2. Le serveur ajoute l'utilisateur à la liste des utilisateurs actifs
3. Le client récupère périodiquement les nouveaux messages via GET
4. Lorsqu'un client envoie un message, il utilise POST pour le transmettre au serveur
5. Les autres clients découvrent ce nouveau message lors de leur prochaine requête GET

## Structure du Projet

L'application est organisée selon les principes de conception standard de JAX-RS :

### Composants Serveur
- **Modèle (`com.chat.model`)** :
  - `Message.java` - Classe représentant un message avec identifiant, expéditeur, contenu et horodatage

- **Service (`com.chat.service`)** :
  - `ChatService.java` - Implémente la logique métier, gère les utilisateurs et les messages de manière thread-safe

- **Ressource (`com.chat.resource`)** :
  - `ChatResource.java` - Définit les endpoints REST avec annotations JAX-RS pour exposer les services

- **Configuration (`com.chat`)** :
  - `ChatApplication.java` - Classe d'initialisation JAX-RS qui enregistre les ressources
  - `web.xml` - Configuration du servlet Jersey pour mapper les URI aux ressources JAX-RS

### Clients
- **Client Console** :
  - `ChatConsoleClient.java` - Interface en ligne de commande utilisant HttpURLConnection

- **Client GUI** :
  - `ChatGuiClient.java` - Interface graphique avec JTextPane pour le formatage des messages

## Points d'Accès API

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/api/chat/users` | POST | Enregistrer un nouvel utilisateur |
| `/api/chat/users/{username}` | DELETE | Déconnecter un utilisateur |
| `/api/chat/users` | GET | Obtenir la liste des utilisateurs connectés |
| `/api/chat/messages` | POST | Envoyer un nouveau message |
| `/api/chat/messages?since={id}` | GET | Récupérer les messages depuis un ID donné |

## Prérequis

- Java 8 (JDK 1.8) ou supérieur
- Un conteneur compatible JAX-RS (comme Tomcat avec Jersey ou WildFly avec RESTEasy)
- Maven (pour la gestion des dépendances) ou les JAR nécessaires manuellement

## Compilation et Déploiement

### Configuration de Maven (recommandée)

1. Créez un fichier `pom.xml` dans le répertoire racine avec les dépendances nécessaires pour JAX-RS.

2. Compilez le projet :
```bash
mvn clean package
```

3. Déployez le WAR généré sur votre serveur d'application.

### Compilation Manuelle

1. Compilez les fichiers sources du serveur :
```bash
mkdir -p build/classes
javac -d build/classes src/main/java/com/chat/model/*.java src/main/java/com/chat/service/*.java src/main/java/com/chat/resource/*.java src/main/java/com/chat/*.java
```

2. Créez un WAR et déployez-le manuellement sur votre serveur.

## Exécution des Clients

### Client Console

```bash
javac ChatConsoleClient.java
java ChatConsoleClient
```

### Client GUI

```bash
javac ChatGuiClient.java
java ChatGuiClient
```

## Caractéristiques Spécifiques

- **Formatage des Messages** :
  - Messages système : `SYSTEM => [HH:mm] ** message **` en rouge cramoisi (#dc143c)
  - Messages utilisateur courant : `nom => [HH:mm] : message` en vert (#008000)
  - Messages autres utilisateurs : `nom => [HH:mm] : message` en vert foncé (#073e18)

- **Interface Utilisateur** :
  - Utilisation de `JTextPane` avec styles pour le formatage rich text
  - Panneau de saisie avec bouton d'envoi
  - Liste des utilisateurs connectés sur le côté
  - Bouton de déconnexion pour quitter proprement

- **Gestion des Données** :
  - Stockage en mémoire thread-safe des messages et utilisateurs
  - Historique complet des messages conservé pendant la durée de vie du serveur
  - Gestion des IDs de message pour le suivi des nouveaux messages

## Comparaison avec RMI et SOAP

| Caractéristique | REST (JAX-RS) | SOAP | RMI |
|-----------------|--------------|------|-----|
| **Communication** | Sans état, unidirectionnelle | Sans état, unidirectionnelle | Avec état, bidirectionnelle |
| **Mécanisme de notification** | Polling côté client | Polling côté client | Callback direct serveur→client |
| **Format des données** | JSON (compact) | XML (verbeux) | Sérialisation Java |
| **Protocole** | HTTP standard | HTTP/SOAP | RMI/JRMP |
| **Interopérabilité** | Excellente (toutes plateformes) | Bonne (multi-langages) | Limitée (Java principalement) |
| **Facilité d'intégration web** | Native | Moyenne | Faible |
| **Overhead** | Faible | Élevé | Moyen |
| **Déploiement** | Simple (WAR standard) | Complexe (configuration SOAP) | Complexe (registre RMI) |

## Instructions Détaillées

### Prérequis

- Java 8 (JDK 1.8) ou supérieur
- Apache Tomcat 6.0.37 (pour la compatibilité avec Jersey 1.x)
- Maven 3.x pour la gestion des dépendances

### Dépendances Jersey

Le projet utilise les bibliothèques Jersey 1.x suivantes :
- `jersey-server`
- `jersey-servlet`
- `jersey-json`
- `jsr311-api`
- `jackson-mapper-asl`
- `jackson-core-asl`

### Déploiement

1. **Compiler et packager l'application :**
   ```bash
   mvn clean package
   ```

2. **Déployer le WAR sur Tomcat :**
   - Copiez le fichier `target/chat-rest.war` dans le répertoire `webapps` de Tomcat
   - Démarrez Tomcat si ce n'est pas déjà fait

3. **Vérifier le déploiement :**
   - Ouvrez dans un navigateur : `http://localhost:8080/chat-rest/api/chat/users`
   - Vous devriez voir un tableau JSON vide `[]` indiquant qu'aucun utilisateur n'est encore connecté
