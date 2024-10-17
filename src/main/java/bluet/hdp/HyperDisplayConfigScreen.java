package bluet.hdp;

import javax.annotation.Nonnull;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HyperDisplayConfigScreen extends Screen {
    public final Screen parent;
    public HyperDisplayConfigScreen (Screen parent) {
        super (Component.translatable ("hyperdisplay.config.title"));
        this.parent = parent;
    }
    @Override
    protected void init () {
        super.init ();
    }
    private ResourceLocation selected = null;
    private DisplayRenderPosition selectpos, originpos;
    boolean hold = false;
    @Override
    public void render (@Nonnull GuiGraphics graphics, int mx, int my, float ft) {
        Minecraft minecraft = Minecraft.getInstance ();
        DeltaTracker tracker = minecraft.timer;
        this.renderBackground (graphics, mx, my, ft);
        if (selected != null) {
            HyperDisplayRenderable renderable = HyperDisplayRenderer.renderables.get (selected);
            DisplayRenderPosition pos = HyperDisplayRenderer.positions.get (selected);
            if (renderable != null) {
                var st = start (renderable, pos.x (), pos.y ());
                FloatingGraphics.fill (graphics, st.x () - 1, st.y () - 1, st.x (), st.y () + renderable.height () + 1, 0xff00ffff);
                FloatingGraphics.fill (graphics, st.x () - 1, st.y () - 1, st.x () + renderable.width () + 1, st.y (), 0xff00ffff);
                FloatingGraphics.fill (graphics, st.x (), st.y () + renderable.height (), st.x () + renderable.width () + 1, st.y () + renderable.height () + 1, 0xff00ffff);
                FloatingGraphics.fill (graphics, st.x () + renderable.width (), st.y (), st.x () + renderable.width () + 1, st.y () + renderable.height () + 1, 0xff00ffff);
            }
        }
        HyperDisplayRenderer.render (graphics, tracker);
    }
    private DisplayRenderPosition start (HyperDisplayRenderable renderable, float x, float y) {
        int alignment = renderable.alignment ();
        x -= alignment % 3 * renderable.width () / 2;
        y -= ((int) (alignment / 3)) * renderable.height () / 2;
        return new DisplayRenderPosition (x, y);
    }
    @Override
    public boolean mouseClicked (double x, double y, int btn) {
        if (btn == 0) {
            selectpos = new DisplayRenderPosition ((float) x, (float) y);
            for (ResourceLocation location : HyperDisplayRenderer.positions.keySet ()) {
                if (! HyperDisplayRenderer.renderables.containsKey (location)) continue;
                DisplayRenderPosition pos = HyperDisplayRenderer.positions.get (location);
                if (pos == null) continue;
                HyperDisplayRenderable renderable = HyperDisplayRenderer.renderables.get (location);
                var st = start (renderable, pos.x (), pos.y ());
                if (x >= st.x () && y >= st.y () && x <= st.x () + renderable.width () && y <= st.y () + renderable.height ()) {
                    selected = location;
                    originpos = pos;
                    hold = true;
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public boolean keyPressed (int keycode, int scancode, int modifiers) {
        if (keycode == GLFW.GLFW_KEY_DELETE) {
            if (selected != null) {
                HyperDisplayRenderer.positions.put (selected, null);
                selected = null;
                return true;
            }
        } else if (keycode == GLFW.GLFW_KEY_O) {
            // Test
            // Probably some nicer solution later
            for (ResourceLocation location : HyperDisplayRenderer.positions.keySet ()) {
                if (HyperDisplayRenderer.positions.get (location) == null) {
                    HyperDisplayRenderer.positions.put (location, new DisplayRenderPosition (2, 2));
                    break;
                }
            }
        }
        return super.keyPressed (keycode, scancode, modifiers);
    }
    @Override
    public boolean mouseDragged (double mx, double my, int btn, double dx, double dy) {
        if (btn == 0) {
            if (hold) {
                float dfx, dfy;
                dfx = (float) mx - selectpos.x ();
                dfy = (float) my - selectpos.y ();
                var newpos = new DisplayRenderPosition (originpos.x () + dfx, originpos.y () + dfy);
                HyperDisplayRenderer.positions.put (selected, newpos);
            }
        }
        return false;
    }
    @Override
    public boolean mouseReleased (double x, double y, int btn) {
        if (btn == 0) {
            hold = false;
            return true;
        }
        return false;
    }
    @Override
    public void onClose () {
        HyperDisplayRenderer.save ();
        super.onClose ();
    }
}
