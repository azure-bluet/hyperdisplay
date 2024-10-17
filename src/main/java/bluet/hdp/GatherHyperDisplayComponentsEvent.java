package bluet.hdp;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public class GatherHyperDisplayComponentsEvent extends Event {
    private final Map <ResourceLocation, HyperDisplayRenderable> components;
    public Map <ResourceLocation, HyperDisplayRenderable> components () {
        return this.components;
    }
    public GatherHyperDisplayComponentsEvent () {
        this.components = new HashMap <> ();
    }
}
