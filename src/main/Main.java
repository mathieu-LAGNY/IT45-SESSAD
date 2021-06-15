package main;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int nb_generation = 100000;
        int taille_population = 200;
        double taux_croisement = 0.8;
        double taux_mutation = 0.5;
        int nb_apprenants;

        JavaIG javaIG;

        if (args.length == 5)  {
            nb_generation     = Integer.parseInt(args[0]);
            taille_population = Integer.parseInt(args[1]);
            taux_croisement   = Double.parseDouble(args[2]);
            taux_mutation     = Double.parseDouble(args[3]);
            nb_apprenants = Integer.parseInt(args[4]);
            javaIG = new JavaIG(nb_apprenants);
        }
        else {
            if (args.length != 0)  {
                System.out.println("Nombre d'arguments n'est pas correct.");
                System.out.println("Soit l'executable ne prend pas d'arguments soit il prend 5 arguments : ");
                System.out.println("   1. nombre de générations (entier > 0)");
                System.out.println("   2. taille de la population (entier > 0)");
                System.out.println("   3. taux de croisement (0 <= reel <= 1)");
                System.out.println("   4. taux de mutation (0 <= reel <= 1)");
                System.out.println("   5. nombre d'apprenants (=nombre de missions, =taille d'un chromosome)");
            }
            System.out.println("Parametres par default");
            javaIG = new JavaIG();
        }

        // initialise l'algorithme évolutionniste
        Ae algo = new Ae(nb_generation, taille_population, taux_croisement, taux_mutation, javaIG);

        Instant start = Instant.now();
        // lance l'algorithme évolutionniste
        Chromosome best = algo.optimiser();
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);

        // affiche les missions et interfaces
        javaIG.affichage();
        // affiche le meilleur individu trouvé
        best.affichageSolution();
        // affiche le temps d'exécution
        System.out.println("Durée d'exécution "+ timeElapsed.toMillis() +" millisecondes");

        Chromosome test = new Chromosome(best);

        while (best.getFitness() < test.getFitness()) {
            test.echange_x_genes_quelconques();
        }

        System.out.println(best.getFitness());
        System.out.println(test.getFitness());
    }

    // fonction brouillon pour tester le programme
    public void test() {
        Instant start = Instant.now();
        int nb_generation = 50;
        int taille_population = 20;
        double taux_croisement = 0.8;
        double taux_mutation = 0.5;

        // variable pour tester l'efficacité des paramètres
        int nb_test = 50;
        JavaIG javaIG = new JavaIG();

        Ae algo;
        Chromosome best;

        double[] fit = new double [nb_test];
        double moy = 0.0;

        algo = new Ae(nb_generation, taille_population, taux_croisement, taux_mutation, javaIG);
        best = algo.optimiser();
        moy += best.getFitness();
        fit[0] = best.getFitness();
        Chromosome min = best;
        for (int i = 1; i < nb_test; i++) {
            // lance l'algorithme évolutionniste
            algo = new Ae(nb_generation, taille_population, taux_croisement, taux_mutation, javaIG);
            best = algo.optimiser();
            moy += best.getFitness();
            fit[i] = best.getFitness();
            if (best.getFitness() < min.getFitness()) {
                min = best;
            }
            // affiche la fitness du meilleur individu trouvé
            // System.out.println("La meilleure solution trouvee est : ");
            // best.affichageSolution();
        }
        System.out.println(Arrays.toString(fit));

        moy /= nb_test;
        System.out.println(moy);

        double ecart = 0;
        for (double f : fit)
            ecart += Math.pow(f - moy, 2);
        ecart /= nb_test;
        ecart = Math.sqrt(ecart);
        System.out.println(ecart);

        min.affichageSolution();
        System.out.println(min.valide());
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");
    }
}
