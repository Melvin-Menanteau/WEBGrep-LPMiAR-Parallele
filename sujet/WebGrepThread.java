package sujet;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

class WebGrepThread implements Runnable {
    private static AtomicInteger nbRunningThreads = new AtomicInteger(0);
    private static ReentrantLock printLock = new ReentrantLock();
    private int num;

    public WebGrepThread(int num) {
        this.num = num;
    }

    public void run() {
        while (true) {
            // System.out.println("Thread " + num + " - " + nbRunningThreads.get());

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

            nbRunningThreads.incrementAndGet();
            
            // // DEBUG
            // // Main.stats.put(num, Main.stats.getOrDefault(num, 0) + 1);

            // if (Main.stats.get(num) == null) Main.stats.put(num, 1);
            // else Main.stats.put(num, Main.stats.get(num) + 1);

            // // DEBUG
            // // System.out.println(
            // //     Main.stats
            // //     .entrySet()
            // //     .stream()
            // //     .sorted(Map.Entry.<Integer, Integer>comparingByKey())
            // //     .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue))
            // // );

            // System.out.println("Thread " + num + " - " + page.href + " - Printed: " + page.printed.get() + " - Visited: " + page.visited.get() + " - PrintLock: " + printLock.isLocked());

            if (
                !page.printed.get()
                && page.parsed.get()
                && printLock.tryLock()
            ) {
                System.out.println("Thread " + num + " - Printing: " + page.href);
                try {
                    if (page.printed.compareAndSet(false, true))
                        Tools.print(page.parsedPage);
                } finally {
                    printLock.unlock();
                }
            }
            /* Si la page n'a pas été visitée */
            else if (page.visited.compareAndSet(false, true)) {
                System.out.println("Thread " + num + " - Visiting: " + page.href);
                try {
                    page.parsedPage = Tools.parsePage(page.href);

                    if (!page.parsedPage.hrefs().isEmpty()) {
                        /* Remove empty links and # (self) hrefs */
                        page.parsedPage.hrefs().removeIf(s -> s.equals("") || s.contains("#"));

                        for (String href : page.parsedPage.hrefs()) {
                            try {
                                /* Page is not visited by default */
                                Page p = Main.pages.putIfAbsent(href, new Page(href, false, false, false));

                                // DEBUG
                                // if (p != null)
                                //     System.out.println("Thread " + num + " added: " + href);
                            } catch (Exception e) {
                                // System.out.println("Thread " + num + " - Error while adding " + href);
                            }
                        }
                    }

                    page.parsed.compareAndSet(false, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            //     System.out.println("Thread " + num + " - " + page.visited.get() + " - " + page.printed.get() + " - " + page.href );

            }

            nbRunningThreads.decrementAndGet();
        }
    }
}