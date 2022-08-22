package com.dylanpdx.retro64;

import com.dylanpdx.retro64.sm64.libsm64.MChar;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

public class mCharRenderer {

    /**
     * Renders the character of the player
     * If the player is crouching, it un-does the offset, referencing {@link net.minecraft.client.renderer.entity.player.PlayerRenderer#getRenderOffset(AbstractClientPlayer, float)}
     */
    public static void renderMChar(AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, MChar mChar){
        if (mChar == null || mChar.id == -1)
            return;

        VertexConsumer vc = buffer.getBuffer(RenType.getMcharRenderType(mChar.state.currentModel==ModelData.VIBRI.getIndex())); // lazy fix for vibri model, enable culling
        matrixStack.pushPose();
        PoseStack.Pose p = matrixStack.last();
        if (!entity.isLocalPlayer()){
            matrixStack.translate(-entity.getX(),-entity.getY(),-entity.getZ());
        }else{
            matrixStack.translate(-mChar.x(),-mChar.y(),-mChar.z());
        }

        if (entity.isCrouching())
            matrixStack.translate(0,0.125D,0);
        if (entity.isSleeping())
            matrixStack.translate(0,0.5D,0);

        if (entity.isPassenger()){
            matrixStack.translate(0,.8f,0);
        }
        if (mChar.getVertices()!=null)
        {
            //Minecraft.getInstance().textureManager.register()
            //DynamicTexture t;
            RenderSystem.setShaderTexture(0,textureManager.getTextureForModel(mChar.state.currentModel,entity).getId());
            //RenderSystem.setShaderLights(new Vector3f(0,1,0),new Vector3f(0,1,0));
            int j =0;
            for (int i = 0;i< mChar.getVertices().length-9;i+=9) {
                drawFc(vc, p,
                        new float[]{mChar.getVertices()[i], mChar.getVertices()[i + 1], mChar.getVertices()[i + 2]},
                        new float[]{mChar.getVertices()[i + 3], mChar.getVertices()[i + 4], mChar.getVertices()[i + 5]},
                        new float[]{mChar.getVertices()[i + 6], mChar.getVertices()[i + 7], mChar.getVertices()[i + 8]},
                        // colors
                        new float[]{mChar.getColors()[i], mChar.getColors()[i + 1], mChar.getColors()[i + 2]},
                        new float[]{mChar.getColors()[i + 3], mChar.getColors()[i + 4], mChar.getColors()[i + 5]},
                        new float[]{mChar.getColors()[i + 6], mChar.getColors()[i + 7], mChar.getColors()[i + 8]},
                        // UV
                        new float[]{mChar.getUVs()[j], mChar.getUVs()[j + 1]},
                        new float[]{mChar.getUVs()[j+2], mChar.getUVs()[j + 3]},
                        new float[]{mChar.getUVs()[j+4], mChar.getUVs()[j + 5]},
                        // normal
                        new float[]{mChar.getNormals()[i], mChar.getNormals()[i + 1], mChar.getNormals()[i + 2]},
                        new float[]{mChar.getNormals()[i + 3], mChar.getNormals()[i + 4], mChar.getNormals()[i + 5]},
                        new float[]{mChar.getNormals()[i + 6], mChar.getNormals()[i + 7], mChar.getNormals()[i + 8]}
                        ,packedLight,textureManager.getTextureWidth(mChar.state.currentModel),textureManager.getTextureHeight(mChar.state.currentModel), LivingEntityRenderer.getOverlayCoords(entity, 0f)
                );
                j+=6;
            }
        }
        matrixStack.popPose();
    }

    /**
     * Draws a face with the given vertices
     * @param vc The vertex consumer
     * @param p The pose stack
     * @param _1 The first vertex
     * @param _2 The second vertex
     * @param _3 The third vertex
     * @param c_1 The first color
     * @param c_2 The second color
     * @param c_3 The third color
     * @param uv_1 The first UV
     * @param uv_2 The second UV
     * @param uv_3 The third UV
     * @param light Packed light
     * @param tWidth The texture width
     * @param tHeight The texture height
     */
    public static void drawFc(VertexConsumer vc,PoseStack.Pose p,
                              float[]_1,float[]_2,float[]_3,
                              float[]c_1,float[]c_2,float[]c_3,
                              float[] uv_1,float[] uv_2,float[] uv_3,
                              float[] norm_1,float[] norm_2,float[] norm_3,
                              int light,float tWidth,float tHeight,int overlay){
        if (uv_1[0] != 1 && uv_1[1] != 1 && uv_2[0] != 1 && uv_2[1] != 1 && uv_3[0] != 1 && uv_3[1] != 1) {
            c_1 = new float[]{1, 1, 1};
            c_2 = new float[]{1, 1, 1};
            c_3 = new float[]{1, 1, 1};
        }else{
            uv_1=new float[]{140f/704f,7f/64f};
            uv_2=new float[]{140f/704f,7f/64f};
            uv_3=new float[]{140f/704f,7f/64f};
        }
        float uOffset = (.5f/tWidth);
        float vOffset = (.5f/tHeight);

        uv_1[0]+=uOffset;
        uv_1[1]+=vOffset;
        uv_2[0]+=uOffset;
        uv_2[1]+=vOffset;
        uv_3[0]+=uOffset;
        uv_3[1]+=vOffset;

        vertex(vc,p,_1[0],_1[1],_1[2],c_1[0],c_1[1],c_1[2],1,uv_1[0],uv_1[1],overlay,light,norm_1[0],norm_1[1],norm_1[2]);
        vertex(vc,p,_2[0],_2[1],_2[2],c_2[0],c_2[1],c_2[2],1,uv_2[0],uv_2[1],overlay,light,norm_2[0],norm_2[1],norm_2[2]);
        vertex(vc,p,_3[0],_3[1],_3[2],c_3[0],c_3[1],c_3[2],1,uv_3[0],uv_3[1],overlay,light,norm_3[0],norm_3[1],norm_3[2]);
    }

    static void vertex(VertexConsumer vc,PoseStack.Pose p, float pX, float pY, float pZ, float pRed, float pGreen, float pBlue, float pAlpha, float pTexU, float pTexV, int pOverlayUV, int pLightmapUV, float pNormalX, float pNormalY, float pNormalZ) {
        vc.vertex(p.pose(),pX, pY, pZ);
        vc.color(pRed, pGreen, pBlue, pAlpha);
        vc.uv(pTexU, pTexV);
        vc.overlayCoords(pOverlayUV);
        vc.uv2(pLightmapUV);
        vc.normal(pNormalX, pNormalY, pNormalZ);
        vc.endVertex();
    }
}
