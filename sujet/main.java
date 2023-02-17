package sujet;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

class Main {
    public static ConcurrentHashMap<String, Page> pages = new ConcurrentHashMap<String, Page>();
    public static ConcurrentHashMap<Integer, Integer> stats = new ConcurrentHashMap<Integer, Integer>();
    public static void main (String args[]) {
        if(args.length == 0) Tools.initialize("-cTl --threads=100 Nantes https://fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

        Main.pages.put("https://fr.wikipedia.org/wiki/Nantes", new Page("https://fr.wikipedia.org/wiki/Nantes", false, false));

        // pages.put("http://www.google.com", new Page("http://www.google.com", false, false));
        // pages.put("http://www.bing.com", new Page("http://www.bing.com", false, false));
        // pages.put("http://www.yahoo.com", new Page("http://www.yahoo.com", false, false));
        // pages.put("http://www.ask.com", new Page("http://www.ask.com", false, false));
        // pages.put("http://www.duckduckgo.com", new Page("http://www.duckduckgo.com", false, false));

        for (int i = 0; i < Tools.numberThreads(); i++) {
            new Thread(new WebGrepThread(i)).start();
        }
    }
}