package me.ginterloper.renderer;

import me.ginterloper.client.MouthConfig;
import me.ginterloper.client.VoiceStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.UUID;

public class MouthRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private static final int TEXTURE_HEIGHT = 48;
    private static final int FRAME_SIZE = 16;
    private static final int FRAME_TIME = 2;

    public MouthRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices,
                       OrderedRenderCommandQueue queue,
                       int light,
                       PlayerEntityRenderState state,
                       float limbAngle,
                       float limbDistance) {

        // 1. Получаем имя игрока из displayName
        String playerName = null;
        if (state.displayName != null) {
            playerName = state.displayName.getString();
            // Убираем цветовые коды §x, если они есть (на всякий случай)
            playerName = playerName.replaceAll("§.", "");
        }


        if (playerName == null || playerName.isEmpty()) {
            var localPlayer = MinecraftClient.getInstance().player;
            if (localPlayer != null) {
                playerName = localPlayer.getName().getString();
            }
        }

        // 2. Ищем запись в таб-листе по имени
        var networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) return;

        var playerEntry = networkHandler.getPlayerListEntry(playerName);
        if (playerEntry == null) {
            // отладка: иногда имя может не сразу появиться в таб-листе
            System.out.println("Не найден игрок в таб-листе: " + playerName);
            return;
        }

        // 3. Получаем UUID
        UUID uuid = playerEntry.getProfile().id();
        if (uuid == null) return;

        // 4. Проверяем, говорит ли этот игрок
        if (!VoiceStateManager.isTalking(uuid)) {
            return;
        }

        // 5. Рендерим рот
        matrices.push();
        ModelPart head = this.getContextModel().head;
        head.applyTransform(matrices);
        matrices.translate(0.0F, -0.1F, -0.29F);
        matrices.scale(1F / 16F, 1F / 16F, 1F / 16F);

        queue.submitCustom(
                matrices,
                RenderLayer.getEntityCutout(MouthConfig.getMouth()),
                (entry, vertexConsumer) -> {

                    Matrix4f m = entry.getPositionMatrix();
                    Matrix3f n = entry.getNormalMatrix();

                    Vector3f normal = new Vector3f(0F, 0F, -1F);
                    n.transform(normal);

                    int totalFrames = TEXTURE_HEIGHT / FRAME_SIZE;

                    assert MinecraftClient.getInstance().world != null;
                    long worldTime = MinecraftClient.getInstance().world.getTime();
                    int frame = (int) ((worldTime / FRAME_TIME) % totalFrames);

                    float frameHeight = 1F / totalFrames;
                    float vStart = frame * frameHeight;
                    float vEnd = vStart + frameHeight;

                    drawQuad(
                            m, vertexConsumer, normal,
                            -4F, -4F, 4F, 4F, 0F,
                            1F, vEnd,
                            0F, vStart,
                            light
                    );
                }
        );
        matrices.pop();
    }

    private void drawQuad(Matrix4f matrix, VertexConsumer vc, Vector3f normal, float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2, int light) {
        vc.vertex(matrix, x1, y2, z).color(255, 255, 255, 255).texture(u1, v1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal.x, normal.y, normal.z);
        vc.vertex(matrix, x2, y2, z).color(255, 255, 255, 255).texture(u2, v1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal.x, normal.y, normal.z);
        vc.vertex(matrix, x2, y1, z).color(255, 255, 255, 255).texture(u2, v2).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal.x, normal.y, normal.z);
        vc.vertex(matrix, x1, y1, z).color(255, 255, 255, 255).texture(u1, v2).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal.x, normal.y, normal.z);
    }
}