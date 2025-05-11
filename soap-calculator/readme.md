# SOAP Web Service: Implémentation d'une Calculatrice

## Aperçu du Projet

Ce projet illustre l'implémentation d'un service web SOAP basique utilisant la bibliothèque Apache Axis. Il consiste en un service de calculatrice offrant des opérations mathématiques simples (addition, soustraction), déployé sur un serveur Apache Tomcat et accessible via le protocole SOAP.

## Prérequis

- Java 8 (JDK 1.8) - *requis pour la compatibilité avec Tomcat 6*
- Apache Tomcat 6.0.37
- Apache Axis
- Bibliothèques JAR supplémentaires (détaillées ci-dessous)

## Guide d'Installation et de Configuration

### Configuration de l'Environnement

#### 1. Installation d'Apache Tomcat

1. Téléchargez [Apache Tomcat 6.0.37](https://archive.apache.org/dist/tomcat/tomcat-6/v6.0.37/bin/)
2. Extrayez l'archive dans un répertoire de votre choix, par exemple:
   ```
   /path/to/your/directory/apache-tomcat-6.0.37
   ```

#### 2. Configuration d'Apache Axis

1. Téléchargez [Apache Axis](https://axis.apache.org/axis/)
2. Dans le dossier décompressé, localisez les ressources suivantes:
   - Le dossier `webapps/axis`
   - Le dossier `lib`

3. Intégration avec Tomcat:
   - Copiez le dossier `axis` depuis `webapps` de l'archive Axis vers le répertoire `webapps` de votre installation Tomcat
   - Copiez le dossier `lib` depuis l'archive Axis vers le dossier `axis` que vous venez de copier dans `webapps`
   - Assurez-vous que le dossier `lib` se trouve au même niveau que le dossier `WEB-INF` dans votre structure `webapps/axis`

#### 3. Dépendances Supplémentaires

1. Téléchargez les bibliothèques JAR suivantes:
   - `activation.jar` - [Java Activation Framework](https://www.oracle.com/java/technologies/java-beans-activation.html)
   - `xerces.jar` - [XML Parser](https://xerces.apache.org/xerces-j/)
   - `javax.mail.jar` - [JavaMail API](https://javaee.github.io/javamail/)

2. Placez ces fichiers JAR dans le dossier `lib` du répertoire `axis` précédemment configuré

### Mise en Place du Service Web

#### 1. Création des Fichiers Source

1. Accédez au répertoire `webapps/axis` de votre installation Tomcat

2. Créez un fichier nommé `Calculator.jws` avec le contenu suivant:
   ```java
   public class Calculator {
       public int add(int p1, int p2) {
           return p1 + p2;
       }
       public int subtract(int p1, int p2) {
           return p1 - p2;
       }
   }
   ```

3. Créez un fichier nommé `CalcClient.java` avec le contenu suivant:
   ```java
   import org.apache.axis.client.Call;
   import org.apache.axis.client.Service;
   import org.apache.axis.encoding.XMLType;
   import org.apache.axis.utils.Options;
   import javax.xml.rpc.ParameterMode;
   
   public class CalcClient {
       public static void main(String [] args) throws Exception {
   
           Options options = new Options(args);
           
           // Vérification des arguments
           args = options.getRemainingArgs();
           if (args == null || args.length != 3) {
               return;
           }
   
           String method = args[0];
           if (!(method.equals("add") || method.equals("subtract"))) {
               return;
           }
           
           // Appel du service
           Integer i1 = Integer.valueOf(args[1]);
           Integer i2 = Integer.valueOf(args[2]);
           Service service = new Service();
           Call call = (Call) service.createCall();
           call.setTargetEndpointAddress(new java.net.URL("http://localhost:8080/axis/Calculator.jws"));
           call.setOperationName(method);
           call.addParameter("op1", XMLType.XSD_INT, ParameterMode.IN);
           call.addParameter("op2", XMLType.XSD_INT, ParameterMode.IN);
           call.setReturnType(XMLType.XSD_INT);
           Integer ret = (Integer) call.invoke(new Object [] { i1, i2 });
           System.out.println("Got result : " + ret);
       }
   }
   ```

### Déploiement et Test

#### 1. Démarrage du Serveur Tomcat

1. Accédez au répertoire `bin` de votre installation Tomcat
2. Exécutez le script de démarrage approprié:
   - Windows: `startup.bat`
   - Linux/macOS: `./startup.sh`
3. Vérifiez que le serveur démarre correctement (un terminal affichera les logs de démarrage)

#### 2. Vérification de l'Installation

1. Ouvrez votre navigateur et accédez à:
   ```
   http://localhost:8080
   ```
   Vous devriez voir la page d'accueil de Tomcat

2. Vérifiez le déploiement de votre service web en accédant à:
   ```
   http://localhost:8080/axis/Calculator.jws?wsdl
   ```
   Un document XML correspondant au WSDL (Web Service Description Language) de votre service devrait s'afficher

#### 3. Compilation et Exécution du Client

1. Ouvrez un terminal et naviguez vers le répertoire `webapps/axis` de votre installation Tomcat

2. Compilez le fichier client:

   **Pour Windows:**
   ```batch
   javac -cp ".;.\lib\axis.jar;.\lib\commons-discovery-0.2.jar;.\lib\commons-logging-1.0.4.jar;.\lib\jaxrpc.jar;.\lib\saaj.jar;.\lib\wsdl4j-1.5.1.jar;.\lib\activation.jar;.\lib\javax.mail.jar;.\lib\xerces.jar" CalcClient.java
   ```

   **Pour Linux/macOS:**
   ```bash
   javac -cp ".:./lib/axis.jar:./lib/commons-discovery-0.2.jar:./lib/commons-logging-1.0.4.jar:./lib/jaxrpc.jar:./lib/saaj.jar:./lib/wsdl4j-1.5.1.jar:./lib/activation.jar:./lib/javax.mail.jar:./lib/xerces.jar" CalcClient.java
   ```

3. Exécutez le client pour tester l'opération d'addition:

   **Pour Windows:**
   ```batch
   java -cp ".;.\lib\axis.jar;.\lib\commons-discovery-0.2.jar;.\lib\commons-logging-1.0.4.jar;.\lib\jaxrpc.jar;.\lib\saaj.jar;.\lib\wsdl4j-1.5.1.jar;.\lib\activation.jar;.\lib\javax.mail.jar;.\lib\xerces.jar" CalcClient add 10 20
   ```

   **Pour Linux/macOS:**
   ```bash
   java -cp ".:./lib/axis.jar:./lib/commons-discovery-0.2.jar:./lib/commons-logging-1.0.4.jar:./lib/jaxrpc.jar:./lib/saaj.jar:./lib/wsdl4j-1.5.1.jar:./lib/activation.jar:./lib/javax.mail.jar:./lib/xerces.jar" CalcClient add 10 20
   ```

4. Testez l'opération de soustraction:

   **Pour Windows:**
   ```batch
   java -cp ".;.\lib\axis.jar;.\lib\commons-discovery-0.2.jar;.\lib\commons-logging-1.0.4.jar;.\lib\jaxrpc.jar;.\lib\saaj.jar;.\lib\wsdl4j-1.5.1.jar;.\lib\activation.jar;.\lib\javax.mail.jar;.\lib\xerces.jar" CalcClient subtract 100 20
   ```

   **Pour Linux/macOS:**
   ```bash
   java -cp ".:./lib/axis.jar:./lib/commons-discovery-0.2.jar:./lib/commons-logging-1.0.4.jar:./lib/jaxrpc.jar:./lib/saaj.jar:./lib/wsdl4j-1.5.1.jar:./lib/activation.jar:./lib/javax.mail.jar:./lib/xerces.jar" CalcClient subtract 100 20
   ```

5. Vous devriez obtenir un résultat similaire à:
   ```
   Got result : 30
   ```
   pour l'addition, et:
   ```
   Got result : 80
   ```
   pour la soustraction.

## Dépannage

- **Erreur de démarrage Tomcat**: Assurez-vous d'utiliser Java 8, les versions plus récentes ne sont pas compatibles avec Tomcat 6
- **WSDL non généré**: Vérifiez que le fichier `Calculator.jws` est correctement placé et que les chemins d'accès sont corrects
- **Erreurs de compilation**: Vérifiez que toutes les bibliothèques JAR nécessaires sont présentes dans le dossier `lib`

## Ressources Supplémentaires

- [Documentation Apache Axis](https://axis.apache.org/axis/)
- [Documentation Apache Tomcat 6](https://tomcat.apache.org/tomcat-6.0-doc/)
- [Tutoriels sur les Services Web SOAP](https://www.w3schools.com/xml/xml_soap.asp)

## Conclusion

Félicitations! Vous avez correctement configuré, déployé et testé un service web SOAP simple. Ce projet démontre les principes fondamentaux des services web basés sur SOAP et constitue une base solide pour des implémentations plus complexes.