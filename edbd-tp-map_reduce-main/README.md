## Il est important de lire tout le document.

## Ressources

- [MR1] MapReduce: Simplified Data Processing on Large Clusters - Jeffrey Dean and Sanjay Ghemawat
- [MR2] Apache Hadoop http://hadoop.apache.org/
- [MR3] Hadoop: the definitive guide (http://grut-computing.com/HadoopBook.pdf)

## Avant commencer

La programmation en Map-Reduce utilise le langage Java - avec ses avantages et inconvénients. Il est ainsi indispensable d'effectuer ce travail en binômes, de rester bien concentrés pour bien comprendre la cause des bogues (souvent ce seront des problèmes de typage ou nommage des ressources) et ne pas perdre du temps.  
Il est vivement conseillé d'utiliser l'IDE Eclipse pour réaliser ce TP. L'archive dont vous disposerez à été pensée pour être facilement intégrée avec cet environnement de développement. Nous détaillerons la procédure de préparation de votre environnement pour cet IDE.

## Format du rendu

Il est demandé de rédiger un document expliquant en quelques lignes comment vous avez répondu à chaque question du TP. Il est important de mettre en évidence juste les points les plus importants vous ayant permis de répondre à la question. Le code Java produit est également à rendre, mais dans une archive `.zip`.


## MapReduce avec Eclipse

Voici les étapes pour configurer Eclipse afin de pouvoir utiliser Map-Reduce :
1. Dans le menu file, importer un nouveau Maven projet en utilisant le `pom.xml` du projet.

Maintenant, vous pouvez tester le programme `src/WordCount.java` à partir d’Eclipse (voir le menu "Run", ou faire `CTRL+F11` pour exécuter le fichier source courant, il est possible de lancer le debugger avec `F11`).

Après chaque exécution, les résultats sont enregistrés dans des sous-dossiers `output/wordCount-xxxx`. En cliquant sur « Refresh » (ou `F5` dans le Package Explorer) vous pouvez voir les fichiers de résultats dans Eclipse. Vous pouvez aussi les voir en allant explorer directement le dossier via l'explorateur de fichiers du système.

## MapReduce avec Eclipse - (Alternative 1)

Télécharger l'archive [TP_HMIN122M-hadoop.zip](https://www.dropbox.com/s/d68l5r2dntyr6h9/TP_HMIN122M-hadoop.zip?dl=0)

Voici les étapes pour configurer Eclipse afin de pouvoir utiliser Map-Reduce :

1. Décompressez l'archive en conservant le dossier racine `TP_HMIN122M-hadoop/`.
2. Dans le menu file, créez un nouveau « java projet » (dans java).
3. Décochez la case "Use default location".
4. Dans le champ "location" sélectionnez le chemin vers votre dossier `TP_HMIN122M-hadoop/`.
5. Cliquez sur "next".
6. Puis cliquez sur l'onglet « Libraries », et assurez-vous qu'Eclipse détecte bien 7 librairies en `.jar` dans "Classpath".  
   - Si ce n'est pas le cas, cliquez sur « Add external JARs » et naviguez pour vous rendre dans le dossier `TP_HMIN122M-hadoop/lib-dev`, puis ajoutez-y tous les `.jar` qui y sont présents.
7. Cliquez sur « finish » pour créer le projet.

## Installation (Alternative 2)

En cas de difficulté, voici une autre alternative.

1. Il est possible de directement charger le projet à partir de l'archive.
2. Dans le menu file, cliquez sur "Open projects from File System".
3. Cliquez sur "Archive...".
4. Sélectionnez `TP_HMIN122M-hadoop.zip`, puis cliquez sur "OK".
5. Dans la liste "Folder" qui s'affiche : désélectionnez la première ligne : `TP_HMIN122M-hadoop.zip_expanded`.
6. Cliquez sur "Finish".

Le projet `TP_HMIN122M-hadoop` s'ajoutera alors dans la fenêtre "Project Explorer". Les fichiers sur le disque seront localisés à la racine de votre workspace, vous pouvez les retrouver en faisant : clic droit sur votre projet → "show in" → "System Explorer".

## Problème sur les nouveaux postes informatique

L'exception levée sur les nouveaux postes informatiques est due au nouveau mécanisme d'authentification de l'UM, où votre nom utilisateur correspond à votre adresse e-mail. Kerberos, le protocole d'authentification utilisé par Hadoop, utilise des règles différentes pour les adresses email.

La solution est de modifier votre nom utilisateur lors de l'exécution du programme. Pour ce faire, il suffit d'ajouter la variable d'environnement `HADOOP_USER_NAME` avant l'exécution du programme.  
Le plus simple est de l'ajouter directement via Eclipse où l'on va modifier la configuration à l'exécution :

1. Faites un clic droit sur la racine de votre projet, situé dans l'onglet à droite.
2. Suivez "Run as" → "Run Configurations".
3. Allez à l'onglet "Environment".
4. Cliquez sur "New...".
   - Name : `HADOOP_USER_NAME`
   - Value : `user`
5. Exécutez le programme WordCount et vérifiez que tout fonctionne.

## Bug de Hadoop sous Windows

L'utilisation de Hadoop sous Windows entraîne la levée d'une exception de type IOException (Failed to set permissions of path: `\tmp\hadoop-user\mapred\staging\user722309568\.staging to 0700`). C'est un bug connu de Hadoop ([cf. HADOOP-7682](https://issues.apache.org/jira/browse/HADOOP-7682)), qu'il est possible de résoudre de la manière suivante :

1. Télécharger le jar suivant [patch-hadoop_7682-1.0.x-win/downloads](https://github.com/congainc/patch-hadoop_7682-1.0.x-win/downloads).
2. Déplacer le jar dans `TP_HMIN122M-hadoop/lib-dev`.
3. Ajouter le `.jar` au build path :
   - Dans le Package Explorer, clic droit sur le projet → "Build Path" → "Configure Build Path" → "Libraries" → "Add external jars".
   - Ajouter le `.jar` précédemment téléchargé.
4. Changer une valeur de la configuration du job avec la méthode suivante :
   ```java
   conf.set("fs.file.impl", "com.conga.services.hadoop.patch.HADOOP_7682.WinLocalFileSystem");


Où `conf` est l'objet `Configuration` envoyé au job lors de sa création.

5. Exécuter le programme et vérifier son bon fonctionnement.

## FAQ

**Le log dans la sortie standard est illisible. Je ne comprends pas ce que fait mon programme Map-Reduce !**  
Réponse : Le programme lit les fichiers contenus dans le répertoire `input-wordCount/`, puis effectue un comptage des mots (vérifiez-le !).

### Statistiques importantes lors de l'exécution d'un job Hadoop :

- **Map input records** : Le nombre de couples clé-valeur traitées avec appels à la fonction `map`.
- **Map output records** : Le nombre de couples clé-valeur produites par des appels à la fonction `map`.
- **Reduce input records** : Le nombre de couples clé-valeur traitées avec appels à la fonction `reduce`. Typiquement égal à `Map output records`, mais plus petit si certaines optimisations sont activées.
- **Reduce input groups** : Le nombre de clés distinctes traitées avec appels à la fonction `reduce`.
- **Reduce output records** : Le nombre de couples clé-valeur produites par des appels à la fonction `reduce`.

### Exemple HadoopPoem

- **Input** : Texte sur 17 lignes composé de 72 mots dont 59 distincts.
- **Map input records** : 17 (une par ligne).
- **Map output records** : 72 (une par mot).
- **Reduce input records** : 72.
- **Reduce input groups** : 59 (mots distincts).
- **Reduce output records** : 59 (calcul du nombre d’occurrences de chaque mot).

**Où sont les résultats de mon programme Map-Reduce ?**  
Voir le répertoire `output/wordCount-xxxx`.

**Y a-t-il de la parallélisation à l'état actuel ?**  
Non, on utilise Hadoop en mode Standalone pour ce TP (les plus courageux peuvent essayer la version "pseudo-distributed").

**Qu'est ce qu'on fait maintenant ?**  
Modifiez les programmes fournis pour implémenter les traitements ci-dessous. Comprenez d'abord le code.

**Important : Comment puis-je déboguer mon programme ?**  
Vous disposez d'un logger via la variable de classe `LOG`. Ces messages sont aussi enregistrés dans `TP_HMIN122M-hadoop/out.log`. Utilisez également le debugger d’Eclipse (Run/Debug ou `F11`).

---

## Exercices

### Exercice 0 - WordCount
Tester le programme WordCount.

### Exercice 1 - WordCount + Filter
Modifier la fonction reduce du programme WordCount.java pour afficher uniquement les mots ayant un nombre d’occurrences supérieur ou égal à deux.

### Exercice 3 - Group-By
Implémenter un opérateur de regroupement sur l'attribut `Customer-ID` dans GroupBy.java.  
Les données sont dans `input-groupBy` et doivent calculer le total des profits (`Profit`) par client.

### Exercice 4 - Group-By
Modifier le programme précédent :
1. Calculer les ventes par `Date` et `State`.
2. Calculer les ventes par `Date` et `Category`.
3. Calculer par commande :
   - Le nombre de produits distincts achetés.
   - Le nombre total d'exemplaires.

### Exercice 5 - Join
Créer une classe Join.java pour joindre les informations des clients et commandes dans `input-join`.  
Restituer les couples `(CUSTOMERS.name, ORDERS.comment)`.

**Note :** Copier les valeurs de l'itérateur dans un tableau temporaire et utiliser deux boucles imbriquées pour effectuer la jointure.
