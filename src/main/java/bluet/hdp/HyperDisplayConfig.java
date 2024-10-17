package bluet.hdp;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber (modid = HyperDisplay.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HyperDisplayConfig {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder ();
    private static final ForgeConfigSpec.ConfigValue <String> firstcolor = builder.comment ("The first color of display") .define ("firstcolor", "ffff80");
    private static final ForgeConfigSpec.ConfigValue <String> secondcolor = builder.comment ("The second color of display") .define ("secondcolor", "00ff80");
    public static final ForgeConfigSpec spec = builder.build ();
    public static int first = 0xffff80, second = 0x00ff80;
    @SubscribeEvent
    public static void load (ModConfigEvent event) {
        int c = getcolor (firstcolor.get ());
        if (c != -1) first = c;
        c = getcolor (secondcolor.get ());
        if (c != -1) second = c;
    }
    public static int getcolor (String col) {
        if (col.length () != 6) return -1;
        String s = "0123456789abcdef";
        int i, r = 0;
        for (i=0; i<6; i++) {
            int j = s.indexOf (col.charAt (i));
            if (j == -1) return -1;
            else r = r * 16 + j;
        }
        return r;
    }
}
