Nombre d'arguments n'est pas correct.
Soit l'executable ne prend pas d'arguments soit il prend 5 arguments : 
   1. nombre de générations (entier > 0)
   2. taille de la population (entier > 0)
   3. taux de croisement (0 <= reel <= 1)
   4. taux de mutation (0 <= reel <= 1)
   5. nombre d'apprenants (=nombre de missions, =taille d'un chromosome)

Voici les commandes à exécuter sous ubunutu:

Compiler :
  javac -classpath .:/run_dir/junit-4.12.jar:target/dependency/* -d . AgendaS.java Main.java Population.java

Exécuter sans arguments :  
  java -classpath .:/run_dir/junit-4.12.jar:target/dependency/* Main

Exécuter avec arguments :  
  java -classpath .:/run_dir/junit-4.12.jar:target/dependency/* Main nb_generation taille_population taux_croisement taux_mutation nb_apprenants

Sinon faire pour compiler et executer avec les arguments par défaut
  bash build.sh
  bash run.sh
  
Sujet:
Un SESSAD est un centre proposant des Services d'accompagnement d'Education Spécialisée A Domicile.
C'est une structure regroupant des intervenant.e.s (ou interfaces) qui ont pour rôle d'accompagner des
personnes ayant des déficiences visuelles ou auditives à des formations pour leur traduire le contenu des
formations. Lorsqu'une mission est confiée à une personne interface, elle doit se rendre depuis le SESSAD
au domicile de la personne à accompagner. À partir de ce point, les deux personnes se rendent sur le
lieu de formation. L'interface fait sa prestation de traduction pour la formation qui peut durer plusieurs
heures. Ensuite, l'interface raccompagne la personne chez elle et ensuite va réaliser sa mission suivante. A
la fin de sa journée, l'interface revient au centre SESSAD.

Les interfaces ont des compétences soit en langage des signes ou en codage LPC. Les personnes à accompagner doivent l'être par une interface qui a la bonne compétence : langage des signes ou codage LPC.
Ensuite on a des référentes pour des formations particulières, par exemple " menuiserie " qui devront être
associées prioritairement dans le cas où la personne à accompagner suit une formation menuiserie.
Les interfaces peuvent être à temps plein (35h) ou à temps partiel (24h). Elles doivent avoir si possible
une pause d'1 h à midi pour se reposer et se restaurer. L'amplitude horaire de la journée est de 12h, c'est à
dire que si la journée commence à 6h00 du matin, la journée doit se terminer avant 18h, mais une journée
de travail ne peut pas durer plus de 8h00. De plus il faut harmoniser les déplacements entre les interfaces
pour éviter que ce soit toujours les mêmes qui se déplacent loin et que d'autres aient des missions près de
chez elles.