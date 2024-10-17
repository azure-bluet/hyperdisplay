package bluet.hdp;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

public class FloatingGraphics {
    // Bad implementation, might be changed if a better solution is found
    public static void fill(GuiGraphics graphics, float startx, float starty, float endx, float endy, int color) {
        Matrix4f matrix4f = graphics.pose () .last () .pose ();
        float s;
        if (startx > endx) {
            s = startx;
            startx = endx;
            endx = s;
        }
        if (starty > endy) {
            s = starty;
            starty = endy;
            endy = s;
        }
        VertexConsumer vertexconsumer = graphics.bufferSource () .getBuffer (RenderType.gui ());
        vertexconsumer.addVertex(matrix4f, startx, starty, 0f).setColor(color);
        vertexconsumer.addVertex(matrix4f, startx, endy, 0f).setColor(color);
        vertexconsumer.addVertex(matrix4f, endx, endy, 0f).setColor(color);
        vertexconsumer.addVertex(matrix4f, endx, starty, 0f).setColor(color);
        graphics.flush ();
    }
}
