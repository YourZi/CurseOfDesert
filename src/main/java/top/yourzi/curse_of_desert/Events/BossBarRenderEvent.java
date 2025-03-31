package top.yourzi.curse_of_desert.Events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Curseofdesert;

@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID, value = Dist.CLIENT)
public class BossBarRenderEvent {
    private static final ResourceLocation BOSS_BAR_EMPTY = new ResourceLocation(Curseofdesert.MOD_ID, "textures/gui/boss_bar_empty.png");
    private static final ResourceLocation BOSS_BAR_FULL = new ResourceLocation(Curseofdesert.MOD_ID, "textures/gui/boss_bar_full.png");
    private static final int BAR_WIDTH = 192;
    private static final int BAR_HEIGHT = 24;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        if (event.getBossEvent().getName().getString().equals(Component.translatable("event.curse_of_desert.name").getString())) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            PoseStack poseStack = guiGraphics.pose();
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int x = screenWidth / 2 - BAR_WIDTH / 2;
            int y = event.getY();

            // 设置渲染状态
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            // 渲染空的进度条背景
            RenderSystem.setShaderTexture(0, BOSS_BAR_EMPTY);
            guiGraphics.blit(BOSS_BAR_EMPTY, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);

            // 根据进度渲染填充的进度条
            float progress = event.getBossEvent().getProgress();
            int fillWidth = (int)(BAR_WIDTH * progress);
            if (fillWidth > 0) {
                RenderSystem.setShaderTexture(0, BOSS_BAR_FULL);
                guiGraphics.blit(BOSS_BAR_FULL, x, y, 0, 0, fillWidth, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
            }

            // 重置渲染状态
            RenderSystem.disableBlend();
            
            // 取消原版Boss栏的渲染
            event.setCanceled(true);
        }
    }
}