package mcjty.enigma.progress;

public class MobConfig {
    private final String mobId;
    private final Integer hp;
    private final String namedItem;

    public MobConfig(String mobId, Integer hp, String namedItem) {
        this.mobId = mobId;
        this.hp = hp;
        this.namedItem = namedItem;
    }

    public String getMobId() {
        return mobId;
    }

    public Integer getHp() {
        return hp;
    }

    public String getNamedItem() {
        return namedItem;
    }
}
