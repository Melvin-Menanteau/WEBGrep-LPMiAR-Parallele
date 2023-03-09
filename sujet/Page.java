package sujet;

import java.util.concurrent.atomic.AtomicBoolean;

class Page {
    /* L'url de la page */
    final String href;
    /* Indique si la page est visité, c'est à dire si un thread à récupérer la page pour la parcourir */
    public final AtomicBoolean visited;
    /* Indique que la page à été parcourue, on à donc les informations sur les liens et le nombre d'occurences qu'elle contient */
    public final AtomicBoolean parsed;
    /* Indique que la page à été imprimée */
    public final AtomicBoolean printed;
    /* Contient les données de la page */
    public Tools.ParsedPage parsedPage = null;

    public Page(String href, Boolean visited, Boolean parsed, Boolean printed) {
        this.href = href;
        this.visited = new AtomicBoolean(visited);
        this.parsed = new AtomicBoolean(parsed);
        this.printed = new AtomicBoolean(printed);
    }
}