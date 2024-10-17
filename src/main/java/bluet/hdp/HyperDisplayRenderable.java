package bluet.hdp;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public interface HyperDisplayRenderable {
    public float width (); public float height ();
    // 0 1 2
    // 3 4 5
    // 6 7 8
    default int alignment () {
        return 0;
    }
    default DisplayRenderPosition defaultpos () {
        return new DisplayRenderPosition (2f, 2f);
    }
    public void render (GuiGraphics graphics, DeltaTracker tracker, DisplayRenderPosition pos);
    public static interface RenderableComponent extends HyperDisplayRenderable {
        public Component component ();
        default Font font () {
            return Minecraft.getInstance () .font;
        }
        default float height () {
            return this.font () .lineHeight;
        }
        default float width () {
            return this.font () .width (this.component () .getVisualOrderText ());
        }
        default void render (GuiGraphics graphics, DeltaTracker tracker, DisplayRenderPosition pos) {
            float co = this.width () / 2, lo = this.height () / 2;
            int a = this.alignment ();
            graphics.drawString (this.font (), this.component () .getVisualOrderText (), pos.x () - co * (a % 3), pos.y () - lo * ((int) (a / 3)), 0xffffff, false);
        }
    }
    public static class SimpleTwoSegmentText implements RenderableComponent {
        private final String a, b;
        public SimpleTwoSegmentText (String a, String b) {
            this.a = a;
            this.b = b;
        }
        public Component component () {
            return Component.literal (a) .withColor (HyperDisplayConfig.first) .append (Component.literal (b) .withColor (HyperDisplayConfig.second));
        }
    }
    public static class TestRandomText implements RenderableComponent {
        private double next = 0;
        private int cnt = 0;
        public Component component () {
            return Component.literal (String.format ("%s", next > .7d ? "AA" : (next > .3d ? "BBBBB" : "CCCCCCCCC"))) .withColor (0xff0000);
        }
        public int alignment () {
            return 4;
        }
        public void render (GuiGraphics graphics, DeltaTracker tracker, DisplayRenderPosition pos) {
            if (cnt == 0) {
                next = Math.random ();
                cnt = 200;
            } else cnt --;
            float co = this.width () / 2, lo = this.height () / 2;
            int a = this.alignment ();
            graphics.drawString (this.font (), this.component () .getVisualOrderText (), pos.x () - co * (a % 3), pos.y () - lo * ((int) (a / 3)), 0xffffff, false);
        }
    }
    public static class SimpleKeyDisplay implements HyperDisplayRenderable {
        public float height () {
            return 42f;
        }
        public float width () {
            return 42f;
        }
        public int alignment () {
            return 4;
        }
        private static void render (GuiGraphics graphics, DisplayRenderPosition pos, float xo, float yo, boolean p) {
            FloatingGraphics.fill (graphics, pos.x () + xo, pos.y () + yo, pos.x () + xo + 12f, pos.y () + yo + 12f, color (p));
        }
        private static int color (boolean p) {
            if (p) return 0x80ffffff;
            else return 0x80808080;
        }
        public void render (GuiGraphics graphics, DeltaTracker tracker, DisplayRenderPosition pos) {
            var opt = Minecraft.getInstance () .options;
            render (graphics, pos, -6f, -21f, opt.keyUp.isDown ());
            render (graphics, pos, -21f, -6f, opt.keyLeft.isDown ());
            render (graphics, pos, -6f, -6f, opt.keyDown.isDown ());
            render (graphics, pos, 9f, -6f, opt.keyRight.isDown ());
            FloatingGraphics.fill (graphics, pos.x () - 21f, pos.y () + 9f, pos.x () + 21f, pos.y () + 21f, color (opt.keyJump.isDown ()));
        }
    }
}
