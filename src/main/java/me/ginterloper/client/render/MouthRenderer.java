package me.ginterloper.client.render;

import me.ginterloper.client.VoiceStateManager;
import me.ginterloper.client.config.MouthConfig;
import me.ginterloper.client.storage.PlayerMouthStorage;
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

        MinecraftClient client = MinecraftClient.getInstance();

        UUID uuid = resolvePlayerUuid(client, state);
        if (uuid == null) {
            return;
        }

        if (!VoiceStateManager.isTalking(uuid)) {
            return;
        }

        boolean isLocalPlayer = client.player != null && uuid.equals(client.player.getUuid());
        net.minecraft.util.Identifier mouthTexture = getBaseMouthTexture(uuid, isLocalPlayer);

        float volume = VoiceStateManager.getVolume(uuid);
        mouthTexture = applyShoutVariant(mouthTexture, volume);

        float scale = MouthConfig.getMouthScale(mouthTexture);
        float offsetX = MouthConfig.getOffsetX();
        float offsetY = MouthConfig.getOffsetY();

        matrices.push();
        ModelPart head = this.getContextModel().head;
        head.applyTransform(matrices);
        matrices.translate(0.0F, -0.1F, -0.29F);
        matrices.translate(offsetX / 16F, offsetY / 16F, 0.0F);
        matrices.scale(1F / 16F, 1F / 16F, 1F / 16F);

        queue.submitCustom(
                matrices,
                RenderLayer.getEntityCutout(mouthTexture),
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
                            -scale, -scale, scale, scale,
                            vEnd,
                            vStart,
                            light
                    );
                }
        );
        matrices.pop();
    }

    private void drawQuad(Matrix4f matrix, VertexConsumer vc, Vector3f normal, float x1, float y1, float x2, float y2, float v1, float v2, int light) {
        vc.vertex(matrix, x1, y2, (float) 0.0).color(255, 255, 255, 255).texture((float) 1.0, v1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal.x, normal.y, normal.z);
        vc.vertex(matrix, x2, y2, (float) 0.0).color(255, 255, 255, 255).texture((float) 0.0, v1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal.x, normal.y, normal.z);
        vc.vertex(matrix, x2, y1, (float) 0.0).color(255, 255, 255, 255).texture((float) 0.0, v2).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal.x, normal.y, normal.z);
        vc.vertex(matrix, x1, y1, (float) 0.0).color(255, 255, 255, 255).texture((float) 1.0, v2).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normal.x, normal.y, normal.z);
    }

    private UUID resolvePlayerUuid(MinecraftClient client, PlayerEntityRenderState state) {
        String playerName = null;
        if (state.displayName != null) {
            playerName = state.displayName.getString();
            playerName = playerName.replaceAll("§.", "");
        }

        if (playerName == null || playerName.isEmpty()) {
            if (client.player != null) {
                playerName = client.player.getName().getString();
            }
        }

        if (playerName == null || playerName.isEmpty()) {
            return null;
        }

        var networkHandler = client.getNetworkHandler();
        if (networkHandler == null) return null;

        var playerEntry = networkHandler.getPlayerListEntry(playerName);
        if (playerEntry == null) {
            return null;
        }

        return playerEntry.getProfile().id();
    }

    private net.minecraft.util.Identifier getBaseMouthTexture(UUID uuid, boolean isLocalPlayer) {
        if (isLocalPlayer) {
            return MouthConfig.getMouth();
        }
        return PlayerMouthStorage.getMouth(uuid);
    }

    private net.minecraft.util.Identifier applyShoutVariant(net.minecraft.util.Identifier base, float volume) {
        float screamThreshold = 0.99F;
        if (volume < screamThreshold) {
            return base;
        }
        String namespace = base.getNamespace();
        String path = base.getPath();
        int dotIndex = path.lastIndexOf('.');
        String shoutPath;
        if (dotIndex >= 0) {
            shoutPath = path.substring(0, dotIndex) + "_shout" + path.substring(dotIndex);
        } else {
            shoutPath = path + "_shout";
        }
        return net.minecraft.util.Identifier.of(namespace, shoutPath);
    }
}