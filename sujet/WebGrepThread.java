package sujet;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

class WebGrepThread implements Runnable {
    private static AtomicInteger nbRunningThreads = new AtomicInteger(0);
    private static ReentrantLock printLock = new ReentrantLock();
    // DEBUG
    private int num;

    public WebGrepThread(int num) {
        this.num = num;
    }

    public void run() {
        while (true) {

            /*
            Par défaut, les threads tournent en permanence en attente d'une nouvelle page à visiter ou imprimer.
            Si toutes les pages ont été visitées et imprimées, et qu'aucun thread n'est en cours d'exécution, on sort de la boucle.
            */
            if (
                Main.pages
                .entrySet()
                .stream()
                .filter(e -> !e.getValue().visited.get() || !e.getValue().printed.get()) // Filter sur les pages non visitées ou non imprimées
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue))
                .size() == 0
                &&
                nbRunningThreads.get() == 0
            ) break;

            Page page = Main.pages.search(1, (key, value) -> {
                if (
                    !value.visited.get()
                    || (
                        !value.printed.get()
                        && value.visited.get()
                    )
                )
                    return value;

                return null;
            });

            if (page == null) continue;

            /* Incrementer le compteur de threads en cours, les autres threads auront ainsi un moyen de savoir si de nouvelles pages peuvent se rajouter à la liste */
            nbRunningThreads.incrementAndGet();

            // System.out.println("Thread " + num + " - " + page.href + " - Printed: " + page.printed.get() + " - Visited: " + page.visited.get() + " - PrintLock: " + printLock.isLocked());

            /* Si la page n'a pas été imprimée, et qu'elle a été visitée, et que le lock d'impression est libre, on l'imprime */
            if (
                !page.printed.get()
                && page.parsed.get()
                && printLock.tryLock()
            ) {
                // DEBUG
                // System.out.println("Thread " + num + " - Printing: " + page.href);
                try {
                    if (page.printed.compareAndSet(false, true))
                        Tools.print(page.parsedPage);
                } finally {
                    printLock.unlock();
                }
            }
            /* Si la page n'a pas été visitée, on la visite */
            else if (page.visited.compareAndSet(false, true)) {
                // DEBUG
                // System.out.println("Thread " + num + " - Visiting: " + page.href);
                try {
                    page.parsedPage = Tools.parsePage(page.href);

                    if (!page.parsedPage.hrefs().isEmpty()) {
                        /* Retirer les pages qui font référence à elles-mêmes pour ne pas parcourir la même page plusieurs fois */
                        page.parsedPage.hrefs().removeIf(s -> s.equals("") || s.contains("#"));

                        for (String href : page.parsedPage.hrefs()) {
                            try {
                                /* Par défaut, la page n'est ni visitée, ni parcourue, ni imprimée */
                                Main.pages.putIfAbsent(href, new Page(href, false, false, false));
                            } catch (Exception e) {
                                // DEBUG
                                // System.out.println("Thread " + num + " - Error while adding " + href);
                            }
                        }
                    }

                    page.parsed.compareAndSet(false, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            /* Décrementer le compteur de threads actif */
            nbRunningThreads.decrementAndGet();
        }
    }
}