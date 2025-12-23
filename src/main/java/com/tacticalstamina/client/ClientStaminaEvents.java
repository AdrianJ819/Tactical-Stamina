package com.tacticalstamina.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.tacticalstamina.TacticalStaminaMod;
import com.tacticalstamina.capabilities.StaminaCapability;
import com.tacticalstamina.config.StaminaConfig; 
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = TacticalStaminaMod.MODID, value = Dist.CLIENT)
public class ClientStaminaEvents {

    private static float displayedStamina = 100.0f;
    private static float displayedPenalty = 0.0f;
    private static float displayedHunger = 0.0f;

    @SubscribeEvent
    public static void onRenderGuiOverlayPost(RenderGuiOverlayEvent.Post event) {
        if (!StaminaConfig.COMMON.enableStamina.get()) return;

        if (event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
            renderStaminaHUD(event.getGuiGraphics());
        }
    }

    private static void renderStaminaHUD(GuiGraphics gfx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || (mc.gameMode != null && !mc.gameMode.canHurtPlayer())) return;

        mc.player.getCapability(StaminaCapability.INSTANCE).ifPresent(cap -> {
            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();

            float hungerPenaltyVal = 0.0f;
            float maxHungerPen = StaminaConfig.COMMON.maxHungerPenalty.get().floatValue();

            // VANILLA HUNGER CHECK
            int foodLevel = mc.player.getFoodData().getFoodLevel();
            int hungerThreshold = StaminaConfig.COMMON.hungerPenaltyThreshold.get();
        
            if (foodLevel <= hungerThreshold && hungerThreshold > 0) {
                hungerPenaltyVal = ((float)(hungerThreshold - foodLevel) / (float)hungerThreshold) * maxHungerPen;
            }

            displayedStamina += (cap.stamina - displayedStamina) * 0.2f;
            displayedPenalty += (cap.fatiguePenalty - displayedPenalty) * 0.1f;
            displayedHunger += (hungerPenaltyVal - displayedHunger) * 0.1f;

            if (Math.abs(cap.stamina - displayedStamina) < 0.05f) displayedStamina = cap.stamina;
            if (Math.abs(cap.fatiguePenalty - displayedPenalty) < 0.05f) displayedPenalty = cap.fatiguePenalty;
            if (Math.abs(hungerPenaltyVal - displayedHunger) < 0.05f) displayedHunger = hungerPenaltyVal;

            float baseMax = 100.0f; 
 
            int sBarW = StaminaConfig.CLIENT.barWidth.get();
            int sBarH = StaminaConfig.CLIENT.barHeight.get();

            int offsetX = StaminaConfig.CLIENT.barXOffset.get();
            int sBarX = (width / 2) - (sBarW / 2) + offsetX;
            int offsetY = StaminaConfig.CLIENT.barYOffset.get();
            int sBarY = height - offsetY; 

            int bgCol = 0xFF000000 | StaminaConfig.CLIENT.colorBackground.get();
            int safeCol = 0xFF000000 | StaminaConfig.CLIENT.colorSafe.get();
            int critCol = 0xFF000000 | StaminaConfig.CLIENT.colorCritical.get();
            int tirelessCol = 0xFF000000 | StaminaConfig.CLIENT.colorTireless.get();
            int stripeCol = 0xFF000000 | StaminaConfig.CLIENT.colorStripes.get();
            
            int energyCol = 0xFF000000 | StaminaConfig.CLIENT.colorPenaltyHunger.get();
            int sepCol = 0xFF000000;

            gfx.fill(sBarX - 1, sBarY - 1, sBarX + sBarW + 1, sBarY + sBarH + 1, 0xFF000000);
            gfx.fill(sBarX, sBarY, sBarX + sBarW, sBarY + sBarH, bgCol);

            float fatiguePct = displayedPenalty / baseMax;
            int fatiguePx = (int)(sBarW * fatiguePct);

            float hungerPct = displayedHunger / baseMax;
            int hungerPx = (int)(sBarW * hungerPct);

            int currentRightEdge = sBarW;

            if (fatiguePx > 0) {
                int startX = sBarW - fatiguePx;
                drawStripesHUD(gfx, sBarX + startX, sBarY, fatiguePx, sBarH, stripeCol);
                gfx.fill(sBarX + startX, sBarY, sBarX + startX + 1, sBarY + sBarH, sepCol);
                currentRightEdge -= fatiguePx;
            }

            if (hungerPx > 0) {
                int startX = currentRightEdge - hungerPx;
                drawStripesHUD(gfx, sBarX + startX, sBarY, hungerPx, sBarH, energyCol); 
                gfx.fill(sBarX + startX, sBarY, sBarX + startX + 1, sBarY + sBarH, sepCol);
                currentRightEdge -= hungerPx;
            }

            int colorTop;
            int colorBottom;

            if (mc.player.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED)) { 
                 colorBottom = tirelessCol;
                 colorTop = tirelessCol + 0x002222; 
            } else if (displayedStamina <= 25.0f) {
                 colorBottom = critCol;
                 colorTop = critCol + 0x222222;
            } else {
                 colorBottom = safeCol;
                 colorTop = safeCol + 0x222222;
            }

            float fillPct = Math.min(1.0f, Math.max(0.0f, displayedStamina / baseMax));
            int fillW = (int) (sBarW * fillPct);
            
            if (fillW > 0) {
                int maxFillW = currentRightEdge;
                int renderW = Math.min(fillW, maxFillW);
                if (renderW > 0) {
                    gfx.fillGradient(sBarX, sBarY, sBarX + renderW, sBarY + sBarH, colorTop, colorBottom);
                    if (renderW < currentRightEdge) {
                        gfx.fill(sBarX + renderW, sBarY, sBarX + renderW + 1, sBarY + sBarH, sepCol);
                    }
                }
            }
        });
    }

    private static void drawStripesHUD(GuiGraphics gfx, int x, int y, int w, int h, int colorRGB) {
        if (w <= 0) return;
        gfx.enableScissor(x, y, x + w, y + h);
        RenderSystem.enableBlend();

        int bandWidth = 2; int gap = 2;
        int totalHeight = h + w + 20;
        int r = (colorRGB >> 16) & 0xFF;
        int g = (colorRGB >> 8) & 0xFF; int b = colorRGB & 0xFF; int a = 200;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = gfx.pose().last().pose();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int i = -20; i < totalHeight; i += (bandWidth + gap)) {
            float yStart = y + i;
            buffer.vertex(matrix, x, yStart, 0).color(r, g, b, a).endVertex();
            buffer.vertex(matrix, x, yStart + bandWidth, 0).color(r, g, b, a).endVertex();
            buffer.vertex(matrix, x + w * 2, yStart - w + bandWidth, 0).color(r, g, b, a).endVertex();
            buffer.vertex(matrix, x + w * 2, yStart - w, 0).color(r, g, b, a).endVertex();
        }
        tesselator.end();
        RenderSystem.disableBlend();
        gfx.disableScissor();
    }
}