/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package com.mineblock11.skinshuffle.mixin.render;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.util.SkinShuffleClientPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> extends EntityRenderer<T> {

    @Shadow protected abstract boolean isVisible(LivingEntity entity);
    @Shadow protected EntityModel<?> model;
    @Shadow protected abstract float getAnimationCounter(T entity, float tickDelta);

    private LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", cancellable = true, at = @At("HEAD"))
    private void fakeHasLabel(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity instanceof DummyClientPlayerEntity) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;isSpectator()Z"
            )
    )
    private void renderPreviousSkin(T entity, float yaw, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof SkinShuffleClientPlayer player)) return;
        float interpolationTime = SkinShuffleConfig.get().skinInterpolationTime;
        if (interpolationTime <= 0.0F) return;
        float progress = player.skinShuffle$getSkinInterpolationProgress(interpolationTime);
        if (progress >= 1.0F) return;
        MinecraftClient client = MinecraftClient.getInstance();
        Identifier texture = player.skinShuffle$getPreviousSkinTextures().texture();
        boolean bl = isVisible(entity);
        boolean bl2 = !bl && !entity.isInvisibleTo(client.player);
        RenderLayer renderLayer = bl
                ? RenderLayer.getItemEntityTranslucentCull(texture)
                        : client.hasOutline(entity)
                                ? RenderLayer.getOutline(texture)
                                : null;
        if (renderLayer == null) return;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        int p = LivingEntityRenderer.getOverlay(entity, getAnimationCounter(entity, delta));
        model.render(matrices, vertexConsumer, light, p, 1.0F, 1.0F, 1.0F, (1.0F - progress) * (bl2 ? 0.15F : 1.0F));
    }
}
