package sujet;

import java.util.concurrent.atomic.AtomicBoolean;

class Page {
    final String href;
    public final AtomicBoolean visited;
    public final AtomicBoolean parsed;
    public final AtomicBoolean printed;
    public Tools.ParsedPage parsedPage = null;

    public Page(String href, Boolean visited, Boolean parsed, Boolean printed) {
        this.href = href;
        this.visited = new AtomicBoolean(visited);
        this.parsed = new AtomicBoolean(parsed);
        this.printed = new AtomicBoolean(printed);
    }
}