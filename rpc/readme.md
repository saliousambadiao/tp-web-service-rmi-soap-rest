Pour executer ce projet voici les etapes a suivre:

- 1. Telecharger le code JavaServer.java
- 2. Telecharger le code JavaCLient.java
- 3. Telecharger les fichiers jar, suivants: 
- - xmlrpc-2.0.jar
- - commons-codec-1.17.2.jar
- Les fichiers JavaServer.java et JavaCLient.java peuvent signaler des erreurs mais ne vous inquitez pas, cela n'empeche pas le code de s'executer.
- 4. Taper la commande suivante pour compiler les fichiers java:
- ``` javac -cp ".;xmlrpc-2.0.jar;commons-codec-1.17.2.jar" *.java ```
- - Cette commande genere deux fichiers .classe
- 5. Taper la commande suivante pour executer le serveur:
- ``` java -cp ".;xmlrpc-2.0.jar;commons-codec-1.17.2.jar" JavaServer ```
- 6. Taper enfin la commande suivante pour executer le client:
- ``` java -cp ".;xmlrpc-2.0.jar;commons-codec-1.17.2.jar" JavaClient ```

- Notez que le dossier .vscode (pour vscode) contient un fichier settings.json, qui est necessaire pour les configurations du projet. Ce fichier indique, le classpath. A NE PAS SUPPRIMER