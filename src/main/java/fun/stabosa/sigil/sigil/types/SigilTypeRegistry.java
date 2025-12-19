package fun.stabosa.sigil.sigil.types;

import java.util.Map;
import java.util.HashMap;

public class SigilTypeRegistry {

    private static final Map<String, SigilType> TYPES = new HashMap<>();

    static {
        TYPES.put("explode", new Explode());
        TYPES.put("transmute", new Transmute());
        TYPES.put("timber", new Timber());
        TYPES.put("potion", new PotionEffect());
    }

    public static SigilType get(String name) {
        return TYPES.get(name);
    }
}
