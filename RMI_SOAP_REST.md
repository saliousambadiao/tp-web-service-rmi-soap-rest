RMI, SOAP et REST : Résumé Comparatif
1. RMI (Remote Method Invocation)
Définition et fonctionnement
RMI est une API Java permettant d'invoquer des méthodes sur des objets distants. Elle permet à un objet Java s'exécutant sur une JVM d'appeler des méthodes d'un autre objet Java s'exécutant sur une JVM distante.

Principe : Les objets distants implémentent une interface commune qui hérite de Remote. Les clients obtiennent des stubs (proxies) qui transmettent les appels au serveur.
Sérialisation : Utilise la sérialisation Java native pour transférer les objets.
Registre RMI : Service de noms permettant aux clients de découvrir les objets distants.
Étapes pour mettre en place un projet RMI
Définir les interfaces distantes : Créer des interfaces qui étendent java.rmi.Remote avec des méthodes qui déclarent java.rmi.RemoteException.
Implémenter les services : Créer des classes qui implémentent ces interfaces.
Compiler et générer les stubs (automatique avec Java 5+).
Démarrer le registre RMI : start rmiregistry ou programmatiquement.
Enregistrer les services : Lier les objets implémentant les services au registre.
Implémenter les clients : Récupérer les références distantes depuis le registre.
2. SOAP (Simple Object Access Protocol)
Définition et fonctionnement
SOAP est un protocole de communication pour l'échange de données structurées via des services web, généralement sur HTTP. Il utilise XML pour formater les messages.

Messages XML : Suit une structure précise avec enveloppe, en-tête et corps.
WSDL : Décrit les services web et leurs opérations.
Indépendant de la plateforme/langage : Fonctionne entre différents langages/systèmes.
Protocoles de transport : Principalement HTTP, mais peut utiliser SMTP, JMS, etc.
Étapes pour mettre en place un projet SOAP
Définir les interfaces de service : En Java, créer des interfaces avec annotations JAX-WS ou définir un WSDL.
Implémenter les services : Créer des classes qui implémentent ces interfaces.
Configurer un serveur d'applications : Déployer sur Tomcat, GlassFish, etc. avec Axis, CXF ou Metro.
Générer ou publier le WSDL : Automatiquement ou manuellement.
Implémenter les clients : Générer des stubs client à partir du WSDL.
Configurer la sécurité (si nécessaire) : WS-Security pour authentification, etc.
3. REST (Representational State Transfer)
Définition et fonctionnement
REST est un style d'architecture pour les systèmes distribués, utilisant généralement HTTP. Il se concentre sur les ressources identifiées par des URLs.

Ressources : Entités accessibles via URI.
Méthodes HTTP : Utilisation de GET, POST, PUT, DELETE pour les opérations CRUD.
Sans état : Chaque requête contient toutes les informations nécessaires.
Formats : Généralement JSON ou XML pour les représentations de données.
Étapes pour mettre en place un projet REST
Identifier les ressources : Définir les entités et leurs relations.
Concevoir les endpoints : Structurer les URI pour chaque ressource.
Choisir un framework : JAX-RS (Jersey, RESTEasy), Spring Boot, etc.
Implémenter les services : Créer des classes avec annotations pour les endpoints.
Configurer la sérialisation/désérialisation : Jackson, JAXB, etc.
Déployer l'application : Sur un serveur compatible.
Documenter l'API : Swagger/OpenAPI pour la documentation.
4. Comparaison
RMI
Avantages :
Intégration naturelle avec Java
Appels de méthodes transparents
Sérialisation native
Inconvénients :
Limité à Java
Problèmes potentiels avec les pare-feu
Moins adapté aux systèmes hétérogènes
SOAP
Avantages :
Standardisé et fortement typé
Indépendant des langages/plateformes
Support de fonctionnalités avancées (transactions, sécurité)
Contrat de service clair (WSDL)
Inconvénients :
Verbeux et complexe
Overhead de performance
Courbe d'apprentissage élevée
REST
Avantages :
Léger et performant
Simple à comprendre et implémenter
Compatible avec le cache HTTP
Découplage client/serveur
Scalabilité
Inconvénients :
Moins structuré que SOAP
Pas de contrat standard (bien que OpenAPI aide)
Gestion de transactions plus complexe
Recommandations selon le contexte
RMI : Applications Java internes où toutes les parties sont en Java
SOAP : Systèmes d'entreprise nécessitant des transactions complexes, une sécurité renforcée ou des contrats stricts
REST : APIs publiques, applications mobiles, microservices, situations où la légèreté et la performance sont prioritaires
Cette comparaison montre l'évolution des services distants, de l'approche spécifique à une plateforme (RMI) vers des protocoles standardisés mais complexes (SOAP), jusqu'aux architectures plus simples et légères (REST) qui dominent aujourd'hui le développement d'APIs.