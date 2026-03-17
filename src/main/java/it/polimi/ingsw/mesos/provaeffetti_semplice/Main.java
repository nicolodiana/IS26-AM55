package it.polimi.ingsw.mesos.provaeffetti_semplice;

public class Main {
    public static void main(String[] args) {

        Game game = new Game();
        Player nicolo = new Player("Nicolò");
        Player mario  = new Player("Mario");
        game.addPlayer(nicolo);
        game.addPlayer(mario);

        // ── PESCA CARTE PERSONAGGIO ──────────────────────────────────────────

        // Nicolò: 3 cacciatori (1 con icona), 2 artisti, 1 sciamano 3 stelle,
        //         1 costruttore (2 ppBonus), 2 inventori (icone diverse), 1 raccoglitore
        nicolo.addCard(new HunterCard("Cacciatore1", true));   // IMMEDIATO: +1 cibo
        nicolo.addCard(new HunterCard("Cacciatore2", false));
        nicolo.addCard(new HunterCard("Cacciatore3", false));
        nicolo.addCard(new ArtistCard("Artista1"));
        nicolo.addCard(new ArtistCard("Artista2"));
        nicolo.addCard(new ShamanCard("Sciamano1", 3));
        nicolo.addCard(new BuilderCard("Costruttore1", 2));
        nicolo.addCard(new InventorCard("Inventore1", "ruota"));
        nicolo.addCard(new InventorCard("Inventore2", "fuoco"));   // coppia: se ha edificio5 +1 cibo
        nicolo.addCard(new GathererCard("Raccoglitore1"));

        // Mario: 1 cacciatore, 5 artisti, 1 sciamano 1 stella, 1 raccoglitore
        mario.addCard(new HunterCard("Cacciatore4", false));
        mario.addCard(new ArtistCard("Artista3"));
        mario.addCard(new ArtistCard("Artista4"));
        mario.addCard(new ArtistCard("Artista5"));
        mario.addCard(new ArtistCard("Artista6"));
        mario.addCard(new ArtistCard("Artista7"));
        mario.addCard(new ShamanCard("Sciamano2", 1));
        mario.addCard(new GathererCard("Raccoglitore2"));

        // Nicolò ha cibo iniziale per permettere il sostentamento
        nicolo.addFood(5);
        mario.addFood(3);

        System.out.println("\n── STATO INIZIALE ──────────────────────────────────────");
        System.out.println("Nicolò: PP=" + nicolo.getPrestigePoints() + ", Cibo=" + nicolo.getFood());
        System.out.println("Mario:  PP=" + mario.getPrestigePoints()  + ", Cibo=" + mario.getFood());

        // ── EVENTO CACCIA ────────────────────────────────────────────────────
        // 1 PP per cacciatore, +1 cibo per cacciatore
        System.out.println("\n── EVENTO CACCIA ───────────────────────────────────────");
        HuntEventCard hunt = new HuntEventCard("Caccia", 1);
        game.triggerEvent(hunt);

        // ── EVENTO PITTURE RUPESTRI ──────────────────────────────────────────
        // ppGain=2, ppLoss=3, minArtists=1, artistsUpper=4, artistsLower=3
        // Nicolò: 2 artisti (< 3) → +4 PP
        // Mario:  5 artisti (> 4) → -3 PP
        System.out.println("\n── EVENTO PITTURE RUPESTRI ─────────────────────────────");
        PaintingsEventCard paintings = new PaintingsEventCard("Pitture Rupestri", 2, 3, 1, 4, 3);
        game.triggerEvent(paintings);

        // ── EVENTO SOSTENTAMENTO ─────────────────────────────────────────────
        // ppLossPerUnfed=1
        // Nicolò: 10 personaggi, 1 raccoglitore → discount=3, needed=7, ha 8 cibo
        // Mario:  8 personaggi, 1 raccoglitore  → discount=3, needed=5, ha 3 cibo → 2 non sfamati
        System.out.println("\n── EVENTO SOSTENTAMENTO ────────────────────────────────");
        SustenanceEventCard sustenance = new SustenanceEventCard("Sostentamento", 1);
        game.triggerEvent(sustenance);

        // ── EVENTO RITUALE SCIAMANICO ────────────────────────────────────────
        // ppGain=4, ppLoss=2
        // Nicolò: 3 stelle (MAX) → +4 PP
        // Mario:  1 stella  (MIN) → -2 PP
        System.out.println("\n── EVENTO RITUALE SCIAMANICO ───────────────────────────");
        RitualEventCard ritual = new RitualEventCard("Rituale Sciamanico", 4, 2);
        game.triggerRitual(ritual);

        // ── FINE PARTITA ─────────────────────────────────────────────────────
        // Inventori Nicolò: 2 inventori x 2 icone distinte = +4 PP
        // Artisti Nicolò:   2 artisti / 2 * 10 = +10 PP
        // Costruttori Nicolò: ppBonus 2 = +2 PP
        // Artisti Mario: 5 artisti / 2 * 10 = +20 PP
        System.out.println("\n── FINE PARTITA ────────────────────────────────────────");
        game.endGame();

        game.printScores();
    }
}
