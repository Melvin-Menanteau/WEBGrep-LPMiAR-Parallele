package sujet;

import java.util.concurrent.atomic.AtomicBoolean;

class Page {
    final String href;
    public final AtomicBoolean visited;
    public final AtomicBoolean printed;
    public Tools.ParsedPage parsedPage = null;

    public Page(String href, Boolean visited, Boolean printed) {
        this.href = href;
        this.visited = new AtomicBoolean(visited);
        this.printed = new AtomicBoolean(printed);
    }

    public void setVisited() {
        if (!visited.get()) {
            visited.set(true);
        }
    }

    public void setPrinted() {
        if (!printed.get()) {
            printed.set(true);
        }
    }
}