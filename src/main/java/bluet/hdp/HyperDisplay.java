package bluet.hdp;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod (HyperDisplay.MODID)
@EventBusSubscriber (bus = Bus.MOD, value = Dist.CLIENT)
public class HyperDisplay {
    public static final String MODID = "hyperdisplay";
    public static final Logger logger = LogUtils.getLogger ();
    private static FMLJavaModLoadingContext context;
    public HyperDisplay (FMLJavaModLoadingContext contextin) {
        context = contextin;
        context.registerConfig (ModConfig.Type.CLIENT, HyperDisplayConfig.spec);
    }
    @SubscribeEvent
    public static void config_screen (FMLClientSetupEvent event) {
        context.registerExtensionPoint (
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory ((mc, mls) -> {
                if (mc.level != null) return new HyperDisplayConfigScreen (mls);
                else return null;
            })
        );
    }
    @SubscribeEvent
    public static void load_complete (FMLLoadCompleteEvent event) {
        LayeredDraw draw = new LayeredDraw () .add (
            (graphics, tracker) -> {
                if (! (Minecraft.getInstance () .screen instanceof HyperDisplayConfigScreen || Minecraft.getInstance () .getDebugOverlay () .showDebugScreen ()))
                HyperDisplayRenderer.render (graphics, tracker);
            }
        );
        Minecraft minecraft = Minecraft.getInstance ();
        minecraft.gui.layers.add (draw, () -> !minecraft.options.hideGui);
        HyperDisplayRenderer.load ();
    }
}
