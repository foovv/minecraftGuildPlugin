package pl.gildie.managers;

public enum GuildPermission {
    MINE_STONE("Kopanie stone"),
    POUR_LAVA("Wylewanie lawy"),
    POUR_WATER("Wylewanie wody"),
    MINE_OBSIDIAN("Kopanie obsydianu");

    private final String name;

    GuildPermission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
