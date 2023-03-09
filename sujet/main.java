package sujet;

import java.util.concurrent.ConcurrentHashMap;

class Main {
    /* Stoque la liste des pages. La clé est l'url de la page */
    public static ConcurrentHashMap<String, Page> pages = new ConcurrentHashMap<String, Page>();
    public static void main (String args[]) {
        if(args.length == 0) Tools.initialize("-cTl --threads=10 Nantes https://fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

        /*
        Ajouter la page de départ dans la liste des pages à parcourir
        Dans l'idéal il faudrait introduire cette instruction dans la fonction 'initialize' de la classe 'Tools'. Mais le sujet n'indiquait pas si des modifications pouvait être apportées à cette classe.
        */
        Main.pages.put("https://fr.wikipedia.org/wiki/Nantes", new Page("https://fr.wikipedia.org/wiki/Nantes", false, false, false));

        /* Lancer les threads */
        for (int i = 0; i < Tools.numberThreads(); i++) {
            new Thread(new WebGrepThread(i)).start();
        }
    }
}