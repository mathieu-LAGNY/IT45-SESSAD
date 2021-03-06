package main;

import java.util.Arrays;
import java.util.Random;

public class Ae {
    private final int nbgenerations;       // nombre de générations après quoi la recherche est arrétée
    private final int taille_pop;          // nombre d'individus dans la population
    private final double taux_croisement;  // taux de croisement : valeur entre 0 et 1
    private final double taux_mutation;    // taux de mutation : valeur entre 0 et 1
    private Population pop;         // liste des individus de la population

    private Random rand = new Random();

// initialisation des paramètres de l'AG et génération de la population initiale
    public Ae(int nbg, int tp, double tcroisement, double tmutation, JavaIG instance)
    {
        nbgenerations     = nbg;
        taille_pop        = tp;
        taux_croisement   = tcroisement;
        taux_mutation     = tmutation;
        pop = new Population(taille_pop, instance);
    }

// procédure principale de la recherche
    public Chromosome optimiser() {
        Chromosome best;
        Chromosome fils1 = null;
        Chromosome fils2 = null;
        Chromosome pere1;
        Chromosome pere2;
        double best_fitness;

        Population initiale = new Population(pop);

        // évaluation des individus de la population initiale
        pop.evaluer();

        // on ordonne les individus selon leur fitness
        pop.ordonner();

        best_fitness = pop.getBestFitness();
        best  = pop.getBestChromosome();

        //tant que le nombre de générations limite n'est pas atteint
        for(int g=0; g<nbgenerations; g++) {
            if (pop.nb_solutions_similaires(best) == taille_pop) {
                System.out.println("Population saturée, génération " + g + "\n");
                break;
            }

            boolean invalide = true;
            while (invalide) {
                //sélection de deux individus de la population courante
                pere1 = pop.selection_roulette();
                pere2 = pop.selection_roulette();

                boolean p1_is_p2 = Arrays.equals(pere1.getGenes(), pere2.getGenes());
                while (p1_is_p2) {
                    pere2 = pop.selection_roulette();
                    p1_is_p2 = Arrays.equals(pere1.getGenes(), pere2.getGenes());
                }

                // ont copie les peres dans les fils
                fils1 = new Chromosome(pere1);
                fils2 = new Chromosome(pere2);

                // On effectue un croisementavec une probabilité "taux_croisement"
                if (rand.nextInt(1000) / 1000.0 < taux_croisement)
                    fils1.croisementPC(fils2);

                // On effectue la mutation d'un enfant avec une probabilité "taux_mutation"
                if (rand.nextInt(1000) / 1000.0 < taux_mutation)
                    fils1.echange_x_genes_quelconques();

                // On effectue la mutation de l'autre enfant avec une probabilité "taux_mutation"
                if (rand.nextInt(1000) / 1000.0 < taux_mutation)
                    fils2.echange_x_genes_quelconques();

                invalide = !(fils1.valide() && fils2.valide());
            }

            // évaluation des deux nouveaux individus générés
            fils1.evaluer();
            fils2.evaluer();

            // Insertion des nouveaux individus dans la population
            pop.remplacement_roulette(fils1);
            pop.remplacement_roulette(fils2);

            // On ordonne la population selon la fitness
            pop.ordonner();

            // Si l'un des nouveaux individus-solutions est le meilleur jamais rencontré
            if (pop.getBestFitness() < best_fitness) {
                best_fitness = pop.getBestFitness();
                System.out.println("Amelioration de la meilleure solution a la generation " + g + " : " + best_fitness + "\n");
                best = pop.getBestChromosome();
            }
            // on affiche les statistiques de la population
            // System.out.println("Génération " + g);
            // affichage();
        }

        //  on affiche les statistiques de la population initiale
        System.out.println("Génération initiale " + initiale.valide());
        initiale.statistiques();
        //  on affiche la consanguinité de la population
        initiale.similitude();

        //retourner le meilleur individu rencontré pendant la recherche
        return best;
    }

    public void affichage() {
        //  on affiche les statistiques de la population
        pop.statistiques();
        //  on affiche la consanguinité de la population
        pop.similitude();
    }
}
