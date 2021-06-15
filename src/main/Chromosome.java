package main;

import java.util.*;

public class Chromosome {
    private int taille; // la taille du chromosome = nombre de gènes = nbFormations
    private int[] genes; // les gènes du chromosome/solution
    private int max_value; // valeur des gènes entre 0 et max_value, indices des formations dans l'instance
    private double fitness; // la valeur de la fonction objectif (fitness) de la solution

    private JavaIG instance;
    private float[] distances;
    private int penalite;

    Random rand;

    // constructeur aléatoire
    public Chromosome(JavaIG instance) {
        this.instance = instance;
        this.taille = instance.getNbFormations();
        this.penalite = 0;
        this.genes = new int[taille];
        rand = new Random();
        // un chromosome est composé de 'taille' gènes,
        // les gènes sont caratérisé par un entier compris entre 0 et 'max_value'
        this.max_value = instance.getNbInterfaces();

        distances = new float[max_value];
        for(int i=0; i<max_value; i++)
            this.distances[i] = 0;

        // genereSolutionAleatoire();
        genereSolutionSemiDirige();

        evaluer();
        genererDistances();
    }

    //  constructeur aléatoire de la solution
    private void genereSolutionAleatoire() {
        boolean recommence = true;
        while(recommence) {
            for(int i=0; i<taille; i++) {
                // on tire aléatoirement le gène suivant parmi les interfaces ayant la bonne competence
                if (i < instance.getI_fCodage())
                    genes[i] = rand.nextInt(instance.getI_iCodage());
                else genes[i] = instance.getI_iDouble() + rand.nextInt(max_value - instance.getI_iDouble());
            }
            recommence = !this.valide();
        }
    }

    //  constructeur aléatoire de la solution
    private void genereSolutionSemiDirige() {
        boolean[][] compatibilite = instance.getCompatibilite();

        boolean compatible;

        ArrayList<Integer> imissionLibre;
        ArrayList<Integer> imissionAttribue;

        int inter;
        boolean recommence = true;
        while(recommence) {
            imissionLibre = new ArrayList<>();
            imissionAttribue = new ArrayList<>();

            for (int i = 0; i < taille; i++)
                imissionLibre.add(i);
            Collections.shuffle(imissionLibre);

            for (int i1 : imissionLibre) {
                compatible = true;
                if (i1 < instance.getI_fCodage())
                    inter = rand.nextInt(instance.getI_iCodage());
                else inter = instance.getI_iDouble() + rand.nextInt(max_value - instance.getI_iDouble());

                // pour chaque mission attribuee
                for (int i2 : imissionAttribue) {
                    if (genes[i2] == inter) {
                        // test de compatibilité planning
                        if (!compatibilite[i1][i2]) {
                            compatible = false;
                            break;
                        }
                    }
                }

                if (compatible) {
                    imissionAttribue.add(i1);
                    genes[i1] = inter;
                }
            }
            // necessaire, car on fait la preselection uniquement sur les competences et la compatibilité des horaires
            recommence = !this.valide();
        }
    }

    // constructeur par recopie
    public Chromosome(Chromosome c) {
        this.rand = new Random();
        this.genes = new int[c.taille];
        System.arraycopy(c.genes, 0, this.genes, 0, c.taille);
        this.taille = c.taille;
        this.max_value = c.max_value;
        this.fitness = c.fitness;
        this.instance = c.instance;
        this.distances = new float[c.max_value];
        System.arraycopy(c.distances, 0, this.distances, 0, c.max_value);
        this.penalite = c.penalite;
    }

    // criteres :
    // - l'interface a toujours la competence necessaire pour la mission
    // - pas de chevauchements
    // - nb d'heures < 35 h
    public boolean valide() {
        ArrayList<Formation> formations = instance.getFormations();
        ArrayList<Interface> interfaces = instance.getInterfaces();

        ArrayList<Formation> formations_interface;
        ArrayList<Formation> formations_jour;

        // variables de stockage temporaires
        Formation f1;
        Formation f2;
        int htotal_interface;
        // pour chaque interface
        for (int i = 0; i < max_value; i++) {
            htotal_interface = 0;
            formations_interface = new ArrayList<>();
            // pour chaque mission
            for (int j = 0; j < taille; j++) {
                // si l'interface est liée à la mission
                if (genes[j] == i) {
                    f1 = formations.get(j);
                    // test de competence de l'interface
                    if (!interfaces.get(i).competent(f1.getCompetence()))
                        return false;
                    formations_interface.add(f1);
                }
            }
            // tri des formations de l'interface par jour et par heure de début
            quickSortFormations(formations_interface, 0, formations_interface.size()-1);
            // pour chaque jour de la semaine
            for (int j = 0; j <= 6; j++) {
                formations_jour = new ArrayList<>();
                // on selectionne les formations de la journée
                for (Formation formation : formations_interface) {
                    if (formation.getJour() == j)
                        formations_jour.add(formation);
                }
                if (!formations_jour.isEmpty()) {
                    // on a pas besoin de tester toutes les missions entres elles car elles sont triées
                    // test de compatibilite des horaires
                    for (int k = 1; k < formations_jour.size(); k ++) {
                        f1 = formations_jour.get(k-1);
                        f2 = formations_jour.get(k);
                        if (!f1.compatible(f2))
                            return false;
                    }
                    // on ajoute le total des heures de la journee entre
                    // le debut de la premiere mission et la fin de la derniere
                    // on considere qu'il s'agit du temps de travail de la journée;
                    htotal_interface += formations_jour.get(formations_jour.size()-1).getHfin() -
                            formations_jour.get(0).getHdebut();
                }
            }
            // test du temps de travail hebdomadaire
            if (htotal_interface > 35)
                return false;
        }
        return true;
    }

    // on considere une solution valide
    // pas d'interface sur des missions simultanées
    public void genererDistances() {
        ArrayList<Formation> formations = instance.getFormations();
        ArrayList<ArrayList<Float>> coord = instance.getCoord();
        ArrayList<Interface> interfaces = instance.getInterfaces();
        // centre sessad
        ArrayList<Float> sessad = coord.get(0);

        ArrayList<Formation> formations_interface;
        ArrayList<Formation> formations_jour;

        float home_sessad;
        int spe1;
        int spe2;
        // pour chaque interface
        for (int i = 0; i < max_value; i++) {
            formations_interface = new ArrayList<>();
            // pour chaque mission
            for (int j = 0; j < taille; j++) {
                // si l'interface est liée à la mission
                if (genes[j] == i) {
                    formations_interface.add(formations.get(j));
                }
            }
            // tri des formations de l'interface par jour et par heure de début
            quickSortFormations(formations_interface, 0, formations_interface.size()-1);
            // pour chaque jour de la semaine
            for (int j = 0; j <= 6; j++) {
                formations_jour = new ArrayList<>();
                // on selectionne les formations de la journée
                for (Formation formation : formations_interface) {
                    if (formation.getJour() == j)
                        formations_jour.add(formation);
                }
                if (!formations_jour.isEmpty()) {
                    home_sessad = distanceEntreAB(interfaces.get(i).getCoordinates(),sessad);
                    distances[i] += home_sessad * 2; // aller + retour au sessad
                    // trajet entre le sessad et le centre consacré à la spéciaité de la première formation de la journée
                    distances[i] += distanceEntreAB(sessad, coord.get(formations_jour.get(0).getSpecialite()));
                    // trajets entre les différents centres des missions
                    for (int k = 1; k < formations_jour.size(); k ++) {
                        spe1 = formations_jour.get(k-1).getSpecialite();
                        spe2 = formations_jour.get(k).getSpecialite();
                        if (spe1 != spe2)
                            distances[i] += distanceEntreAB(coord.get(spe1), coord.get(spe2));
                    }
                    // trajet entre le dernier centre et le sessad
                    distances[i] += distanceEntreAB(coord.get(formations_jour.get(formations_jour.size()-1).getSpecialite()), sessad);
                }
            }
        }
    }

    public void calculerPenalite() {
        ArrayList<Formation> formations = instance.getFormations();
        ArrayList<Interface> interfaces = instance.getInterfaces();

        // pour chaque interface
        for (int i = 0; i < max_value; i++)
            // pour chaque mission
            for (int j = 0; j < taille; j++)
                // si l'interface est liée à la mission mais n'a pas cette specialite
                if (genes[j] == i && interfaces.get(i).specialise(formations.get(j).getSpecialite()))
                    this.penalite += 1;
    }

    public float distanceEntreAB(ArrayList<Float> a, ArrayList<Float> b) {
        return (float) Math.sqrt(Math.pow(a.get(0)-b.get(0),2)+Math.pow(a.get(1)-b.get(1),2));
    }

    private void quickSortFormations(ArrayList<Formation> formations, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(formations, begin, end);

            quickSortFormations(formations, begin, partitionIndex-1);
            quickSortFormations(formations, partitionIndex+1, end);
        }
    }

    private int partition(ArrayList<Formation> arr, int begin, int end) {
        Formation pivot = arr.get(end);
        int i = (begin-1);
        for (int j = begin; j < end; j++) {
            if (arr.get(j).precede(pivot)) {
                i++;

                Formation swapTemp = arr.get(i);
                arr.set(i, arr.get(j));
                arr.set(j, swapTemp);
            }
        }
        Formation swapTemp = arr.get(i + 1);
        arr.set(i + 1, arr.get(end));
        arr.set(end, swapTemp);

        return i+1;
    }

    public void evaluer() {
        calculerPenalite();
        float moy;
        float somme = 0;
        for (float distance : distances)
            somme += distance;
        moy = somme / distances.length;

        double ecart = 0;
        for (float distance : distances)
            ecart += Math.pow(distance - moy, 2);
        ecart /= distances.length;
        ecart = Math.sqrt(ecart);

        float f = somme / taille;

        this.fitness = 0.5 * (moy + ecart) + 0.5 * f * penalite;
    }
    // fonction d'évaluation du chromosome (c-à-d calcul la fitness)
    //   Elle doit etre lancée à la creation des solution et apres
    //   l'exécution des operateurs de mutation et de croisement

    public double getFitness() {
        return fitness;
    }

    public int[] getGenes() {
        return genes.clone();
    }

    public void setGene(int i, int value) {
        genes[i] = value;
    }

    public void affichageSolution() {
        ArrayList<Formation> formations = instance.getFormations();
        ArrayList<Interface> interfaces = instance.getInterfaces();

        ArrayList<Formation> formations_interface;
        ArrayList<Formation> formations_jour;

        // pour chaque interface
        for (int i = 0; i < max_value; i++) {
            formations_interface = new ArrayList<>();
            System.out.println(interfaces.get(i).toString());
            // pour chaque mission
            for (int j = 0; j < taille; j++) {
                // si l'interface est liée à la mission
                if (genes[j] == i) {
                    formations_interface.add(formations.get(j));
                }
            }
            // tri des formations de l'interface par jour et par heure de début
            quickSortFormations(formations_interface, 0, formations_interface.size()-1);
            // pour chaque jour de la semaine
            for (int j = 0; j <= 6; j++) {
                formations_jour = new ArrayList<>();
                // on selectionne les formations de la journée
                for (Formation formation : formations_interface) {
                    if (formation.getJour() == j)
                        formations_jour.add(formation);
                }
                for (Formation formation : formations_jour) {
                    formation.affichageTableau();
                }
            }
        }
        System.out.println("fitness = " + fitness + "\n");
    }

    // OPERATEURS DE MUTATION
    // on échange les 2 gènes
    public  void echange_2_genes(int gene1, int gene2) {
        int inter    = genes[gene1];
        genes[gene1] = genes[gene2];
        genes[gene2] = inter;
    }

    void echange_2_genes_quelconques() {
        boolean[][] compatibilite = instance.getCompatibilite();

        int i1 = rand.nextInt(taille); // indice de la mission tirée au sort
        boolean compatible = true;

        ArrayList<Integer> imission = new ArrayList<>();
        for (int i=0; i < taille; i++)
            imission.add(i);
        imission.remove(i1);
        Collections.shuffle(imission);

        int i2; // indice de la deuxieme mission
        int k;

        while (compatible && !imission.isEmpty()) {
            i2 = imission.remove(0);
            // pour chaque mission
            k = 0;
            while (compatible && k < taille) {
                // test de selection des formations correspondant à l'interface de la formation tirée
                if (genes[k] == genes[i1]) {
                    // test de compatibilité planning
                    if (!compatibilite[k][i1])
                        compatible = false;
                }
                // test de selection des formations correspondant à l'interface de la deuxieme formation tirée
                if (genes[k] == genes[i2]) {
                    // test de compatibilité planning
                    if (!compatibilite[k][i2])
                        compatible = false;
                }
                k++;
            }
            if (compatible) {
                echange_2_genes(i1, i2);
            }
        }
    }

    // on decide arbitrairement x < taille/5 afin de limiter la complexité
    void echange_x_genes_quelconques() {
        int x = rand.nextInt(taille); // indice de la mission tirée au sort
        while (x > 0) {
            echange_2_genes_quelconques();
            x--;
        }
    }

    // OPERATEURS DE CROISEMENT
    // opérateur de croisement compatible avec le planning
    public void croisementPC(Chromosome enfant2) {
        boolean[][] compatibilite = instance.getCompatibilite();

        Chromosome enfant1 = this;

        // copie des genes des parents
        int[] c_p1 = enfant1.getGenes();
        int[] c_p2 = enfant2.getGenes();

        int nbEchange = rand.nextInt(taille /2);

        ArrayList<Integer> imission = new ArrayList<>();
        for (int i=0; i < taille; i++)
            imission.add(i);

        int[] genes2 = enfant2.getGenes();

        int i; // indice de la mission tirée au sort
        int j;
        boolean compatible;

        while (nbEchange > 0) {
            i = imission.remove(rand.nextInt(imission.size()));
            compatible = true;

            // pour chaque mission
            j = 0;
            while (compatible && j < taille) {
                // test de selection de formation correspondant à l'interface de la formation tirée
                if (genes[j] == genes2[i] || genes2[j] == genes[i]) {
                    // test de compatibilité planning
                    if (!compatibilite[j][i])
                        compatible = false;
                }
                j++;
            }
            if (compatible) {
                enfant1.setGene(i, c_p2[i]);
                enfant2.setGene(i, c_p1[i]);
            }
            nbEchange --;
        }
    }
}
