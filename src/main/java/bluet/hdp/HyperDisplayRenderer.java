package bluet.hdp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber (bus = Bus.FORGE, value = Dist.CLIENT)
public class HyperDisplayRenderer {
    public static final Map <ResourceLocation, DisplayRenderPosition> positions = new HashMap <> ();
    public static File cfg () {
        Minecraft minecraft = Minecraft.getInstance ();
        var gamedir = minecraft.gameDirectory;
        String cfgps = gamedir.getPath () + File.separator + "hyperdisplay.cfg.1";
        return new File (cfgps);
    }
    public static void load () {
        File cfg = cfg ();
        try {
            Scanner scanner = new Scanner (cfg);
            while (scanner.hasNextLine ()) {
                var line = scanner.nextLine ();
                var spl = line.split (" ");
                if (spl.length == 1) {
                    var location = ResourceLocation.parse (spl [0]);
                    positions.put (location, null);
                } else if (spl.length == 3) {
                    var location = ResourceLocation.parse (spl [0]);
                    float x, y;
                    try {
                        x = Float.valueOf (spl [1]);
                        y = Float.valueOf (spl [2]);
                        positions.put (location, new DisplayRenderPosition (x, y));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                } else continue;
            }
            scanner.close ();
        } catch (FileNotFoundException exception) {
            return; //
        }
    }
    public static void save () {
        File cfg = cfg ();
        try {
            PrintStream stream = new PrintStream (cfg);
            for (ResourceLocation location : positions.keySet ()) {
                var pos = positions.get (location);
                if (pos == null) stream.println (location.toString ());
                else stream.println (String.format ("%s %f %f", location.toString (), pos.x (), pos.y ()));
            }
            stream.close ();
        } catch (IOException e) {
            return; //
        }
    }
    public static final Map <ResourceLocation, HyperDisplayRenderable> renderables = new HashMap <> ();
    public static void render (GuiGraphics graphics, DeltaTracker tracker) {
        renderables.clear ();
        GatherHyperDisplayComponentsEvent event = MinecraftForge.EVENT_BUS.fire (new GatherHyperDisplayComponentsEvent ());
        for (ResourceLocation location : event.components () .keySet ()) renderables.put (location, event.components () .get (location));
        for (ResourceLocation location : renderables.keySet ()) {
            HyperDisplayRenderable renderable = renderables.get (location);
            if (! positions.containsKey (location)) positions.put (location, renderable.defaultpos ());
            DisplayRenderPosition pos = positions.get (location);
            if (pos != null)
            renderable.render (graphics, tracker, pos);
        }
    }
    @SubscribeEvent
    public static void addbuiltin (GatherHyperDisplayComponentsEvent event) {
        event.components () .put (
            ResourceLocation.fromNamespaceAndPath (HyperDisplay.MODID, "version"),
            new HyperDisplayRenderable.SimpleTwoSegmentText ("Hyper Display ", HyperDisplayConstants.VERSION_TAG)
        );
        event.components () .put (
            ResourceLocation.fromNamespaceAndPath (HyperDisplay.MODID, "key_display"),
            new HyperDisplayRenderable.SimpleKeyDisplay ()
        );
    }
    @SubscribeEvent
    public static void regcmd (RegisterClientCommandsEvent event) {
        event.getDispatcher () .register (
            Commands.literal (HyperDisplay.MODID) .then (
                Commands.literal ("setcolor") .then (
                    Commands.literal ("first") .then (
                        Commands.argument ("color", StringArgumentType.string ()) .executes (
                            ctx -> {
                                String str = StringArgumentType.getString (ctx, "color");
                                int c = HyperDisplayConfig.getcolor (str);
                                if (c == -1) {
                                    Minecraft.getInstance () .player .sendSystemMessage (Component.translatable ("hyperdisplay.invalidcolor") .withColor (0xff0000));
                                    return 0;
                                } else {
                                    HyperDisplayConfig.first = c;
                                    return 1;
                                }
                            }
                        )
                    )
                ) .then (
                    Commands.literal ("second") .then (
                        Commands.argument ("color", StringArgumentType.string ()) .executes (
                            ctx -> {
                                String str = StringArgumentType.getString (ctx, "color");
                                int c = HyperDisplayConfig.getcolor (str);
                                if (c == -1) {
                                    Minecraft.getInstance () .player .sendSystemMessage (Component.translatable ("hyperdisplay.invalidcolor") .withColor (0xff0000));
                                    return 0;
                                } else {
                                    HyperDisplayConfig.second = c;
                                    return 1;
                                }
                            }
                        )
                    )
                )
            )
        );
    }
}
