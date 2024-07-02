/*
 * ALL RIGHTS RESERVED
 *
 * Copyright (c) 2024 Calum H. (IMB11) and enjarai
 *
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mineblock11.skinshuffle.mixin.render;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.util.SkinShuffleClientPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Shadow protected abstract boolean isVisible(LivingEntity entity);
    @Shadow protected EntityModel<?> model;
    @Shadow protected abstract float getAnimationCounter(LivingEntity entity, float tickDelta);

    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", cancellable = true, at = @At("HEAD"))
    private <T extends LivingEntity> void fakeHasLabel(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity instanceof DummyClientPlayerEntity) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"
            )
    )
    /*? if <1.20.4 {*/
    /*private void renderSmoothSkinChange(LivingEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof SkinShuffleClientPlayer player && !(entity instanceof DummyClientPlayerEntity)) {
            Identifier prevTextures = player.skinShuffle$getPrevSkinTextures();
            if (prevTextures != null) {
                float alpha = 1.0F - (Util.getMeasuringTimeMs() - player.skinShuffle$getSkinChangeTime()) / (SkinShuffleConfig.get().smoothSkinChangeSeconds * 1000.0F);
                if (alpha > 0.0F && alpha <= 1.0F) {
                    boolean isFullVisible = isVisible(entity);
                    boolean isHalfVisible = !isFullVisible && !entity.isInvisibleTo(MinecraftClient.getInstance().player);
                    if (isFullVisible || isHalfVisible) {
                        if (isHalfVisible) alpha *= 0.15F;
                        model.render(
                                matrices,
                                vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(prevTextures)),
                                light,
                                LivingEntityRenderer.getOverlay(entity, getAnimationCounter(entity, tickDelta)),
                                0.0F, 0.0F, 0.0F,
                                alpha
                        );
                    }
                } else {
                    player.skinShuffle$clearPrevSkinTextures();
                }
            }
        }
    }
    *//*?} else if <1.21 {*/
    /*private void renderSmoothSkinChange(LivingEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof SkinShuffleClientPlayer player && !(entity instanceof DummyClientPlayerEntity)) {
            net.minecraft.client.util.SkinTextures prevTextures = player.skinShuffle$getPrevSkinTextures();
            if (prevTextures != null) {
                float alpha = 1.0F - (Util.getMeasuringTimeMs() - player.skinShuffle$getSkinChangeTime()) / (SkinShuffleConfig.get().smoothSkinChangeSeconds * 1000.0F);
                if (alpha > 0.0F && alpha <= 1.0F) {
                    boolean isFullVisible = isVisible(entity);
                    boolean isHalfVisible = !isFullVisible && !entity.isInvisibleTo(MinecraftClient.getInstance().player);
                    if (isFullVisible || isHalfVisible) {
                        Identifier texture = prevTextures.texture();
                        if (isHalfVisible) alpha *= 0.15F;
                        model.render(
                                matrices,
                                vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(texture)),
                                light,
                                LivingEntityRenderer.getOverlay(entity, getAnimationCounter(entity, tickDelta)),
                                0.0F, 0.0F, 0.0F,
                                alpha
                        );
                    }
                } else {
                    player.skinShuffle$clearPrevSkinTextures();
                }
            }
        }
    }
    *//*?} else {*/
    private void renderSmoothSkinChange(LivingEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof SkinShuffleClientPlayer player && !(entity instanceof DummyClientPlayerEntity)) {
            net.minecraft.client.util.SkinTextures prevTextures = player.skinShuffle$getPrevSkinTextures();
            if (prevTextures != null) {
                float alpha = 1.0F - (Util.getMeasuringTimeMs() - player.skinShuffle$getSkinChangeTime()) / (SkinShuffleConfig.get().smoothSkinChangeSeconds * 1000.0F);
                if (alpha > 0.0F && alpha <= 1.0F) {
                    boolean isFullVisible = isVisible(entity);
                    boolean isHalfVisible = !isFullVisible && !entity.isInvisibleTo(MinecraftClient.getInstance().player);
                    if (isFullVisible || isHalfVisible) {
                        Identifier texture = prevTextures.texture();
                        if (isHalfVisible) alpha *= 0.15F;
                        model.render(
                                matrices,
                                vertexConsumers.getBuffer(RenderLayer.getItemEntityTranslucentCull(texture)),
                                light,
                                LivingEntityRenderer.getOverlay(entity, getAnimationCounter(entity, tickDelta)),
                                ((int) (alpha * 255.0F) << 24)
                                        | 0xFFFFFF
                        );
                    }
                } else {
                    player.skinShuffle$clearPrevSkinTextures();
                }
            }
        }
    }
    /*?}*/
}
