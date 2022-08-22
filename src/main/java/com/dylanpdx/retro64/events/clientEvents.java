package com.dylanpdx.retro64.events;

import com.dylanpdx.retro64.*;
import com.dylanpdx.retro64.gui.CharSelectScreen;
import com.dylanpdx.retro64.maps.BlockMatMaps;
import com.dylanpdx.retro64.networking.SM64PacketHandler;
import com.dylanpdx.retro64.sm64.*;
import com.dylanpdx.retro64.sm64.libsm64.LibSM64;
import com.dylanpdx.retro64.sm64.libsm64.LibSM64SurfUtils;
import com.dylanpdx.retro64.sm64.libsm64.Libsm64Library;
import com.dylanpdx.retro64.sm64.libsm64.MChar;
import com.mojang.math.Vector3f;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles events for the client side
 */
public class clientEvents {

    //public static final IIngameOverlay MCHAR_HP_ELEMENT = OverlayRegistry.registerOverlayTop("retro64_hearts", new SMC64HeartOverlay());

    //public static final IIngameOverlay MCHAR_DIALOG_ELEMENT= OverlayRegistry.registerOverlayTop("retro64_dialog", new SMC64DialogOverlay());
    static boolean clickDebounce=false;
    static boolean debug=false; // displays debug info


    public void gameTick(){
        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            if (client.player==null){
                // If the player is null, we're probably not in-game. There's probably a simpler way of checking for this
                if (SM64EnvManager.selfMChar!=null)
                {
                    LibSM64.MCharDelete(SM64EnvManager.selfMChar.id);
                    SM64EnvManager.selfMChar=null;
                }
            }else{
                /// Handle toggling modes
                if (Keybinds.getMToggle().isDown() && !clickDebounce){
                    //if (NetworkHooks.isVanillaConnection(tick.player.connection.getConnection()))
                    //    return; // don't allow toggling in vanilla servers
                    RemoteMCharHandler.toggleMChar(client.player);
                    clickDebounce=true;
                }else if (!Keybinds.getMToggle().isDown() && clickDebounce)
                    clickDebounce=false;

                /// Handle character select menu
                if (RemoteMCharHandler.getIsMChar(client.player) && SM64EnvManager.selfMChar != null && SM64EnvManager.selfMChar.state != null){
                    if (Keybinds.getMMenu().isDown() && Minecraft.getInstance().level!=null)
                        Minecraft.getInstance().setScreen(new CharSelectScreen());
                    if (Minecraft.getInstance().player.isSpectator())
                        RemoteMCharHandler.mCharOff(Minecraft.getInstance().player);
                    if (Minecraft.getInstance().player.getAbilities().flying)
                        Minecraft.getInstance().player.getAbilities().flying = false;
                    var elytraCheck = Registry.ITEM.getKey(client.player.getInventory().armor.get(2).getItem()).toString().equals("minecraft:elytra");
                    var eState = (SM64MCharStateFlags.MCHAR_CAP_ON_HEAD.getValue() | SM64MCharStateFlags.MCHAR_WING_CAP.getValue());
                    if (elytraCheck && (SM64EnvManager.selfMChar.state.flags & eState) != eState)
                    {
                        LibSM64.MCharChangeState(SM64EnvManager.selfMChar.id, eState);
                        //LibSM64.MCharChangeAction(SM64EnvManager.selfMChar.id, SM64MCharAction.ACT_PUTTING_ON_CAP.id);
                    }
                    else if (!elytraCheck && (SM64EnvManager.selfMChar.state.flags & eState) == eState)
                    {
                        //LibSM64.MCharChangeAction(SM64EnvManager.selfMChar.id, SM64MCharAction.ACT_PUTTING_ON_CAP.id);
                        LibSM64.MCharChangeState(SM64EnvManager.selfMChar.id, (SM64MCharStateFlags.MCHAR_CAP_ON_HEAD.getValue() | SM64MCharStateFlags.MCHAR_NORMAL_CAP.getValue()));
                    }
                }
            }
        });
    }

    //public void onRenderGameUI(RenderGameOverlayEvent.PreLayer event){
    //    if (RemoteMCharHandler.getIsMChar(Minecraft.getInstance().player) && (event.getOverlay() == ForgeIngameGui.PLAYER_HEALTH_ELEMENT || event.getOverlay() == ForgeIngameGui.FOOD_LEVEL_ELEMENT || event.getOverlay() == ForgeIngameGui.ARMOR_LEVEL_ELEMENT)){
    //        event.setCanceled(true);
    //    }
    //}

    //public void onRenderGameUI(RenderGameOverlayEvent.Post event){
    //    if (isDebug()){
    //        DrawDebugUI(event);
    //    }
    //}

    private void DrawDebugUI() {
        var font = Minecraft.getInstance().font;
        var plr = Minecraft.getInstance().player;
        if (RemoteMCharHandler.getIsMChar(plr)){
            String debugText="";
            debugText+=String.format("currentAction: %s (%s)\n", SM64EnvManager.selfMChar.state.action, SM64MCharAction.getAllActions(SM64EnvManager.selfMChar.state.action));
            debugText+=String.format("currentActionFlags: %s\n", SM64MCharActionFlags.getAllFlags(SM64EnvManager.selfMChar.state.action));
            debugText+=String.format("stateFlags: %s (%s)\n", SM64EnvManager.selfMChar.state.flags, SM64MCharStateFlags.getAllFlags(SM64EnvManager.selfMChar.state.flags));
            debugText+=String.format("faceAngle: %s\n", SM64EnvManager.selfMChar.state.faceAngle);
            debugText+=String.format("vtx: %s\n", SM64EnvManager.selfMChar.getVertices().length);

            debugText+=String.format("health: %s\n", SM64EnvManager.selfMChar.state.health);
            var anim = SM64EnvManager.selfMChar.animInfo;
            debugText+=String.format("anim id: %s; accel: %s; frame: %s; angle: %s\n",anim.animID,anim.animAccel,anim.animFrame, SM64EnvManager.selfMChar.animYRot);
            debugText+=String.format("velocity: X %s; Y %s; Z %s\n", SM64EnvManager.selfMChar.state.velocity[0], SM64EnvManager.selfMChar.state.velocity[1], SM64EnvManager.selfMChar.state.velocity[2]);

            if (plr.isPassenger()){
                debugText+="MC passenger: "+plr.getVehicle().yRotO+"\n";
            }
            var dat=debugText.split("\n");
            for (int i = 0;i<dat.length;i++){
                //font.draw(event.getMatrixStack(),dat[i],10,10+(10*i),0xffffffff);
            }
        }
    }


    public static boolean isDebug(){
        return debug;
    }

    /**
     * SM64Lib tick
     */
    public static void mCharTick(){
        if (SM64EnvManager.selfMChar == null || SM64EnvManager.selfMChar.id == -1)
            return;
        LocalPlayer plr = Minecraft.getInstance().player;
        ClientLevel world = Minecraft.getInstance().level;
        if (plr.isOnFire())
            plr.clearFire();
        var mchar = SM64EnvManager.selfMChar;

        if (world.isClientSide && Minecraft.getInstance().getSingleplayerServer()!=null && !Minecraft.getInstance().getSingleplayerServer().isPublished() && Minecraft.getInstance().isPaused())
            return;

        //if (NetworkHooks.isVanillaConnection(Minecraft.getInstance().player.connection.getConnection()))
        //{
        //    RemoteMCharHandler.mCharOff(plr);
        //    return;
        //}
        world.getProfiler().push("retro64_mchar_tick");

        // potion effects handling
        float joystickMult=1;
        boolean poisoned=false;
        for (var effect : plr.getActiveEffects()){
            switch (Registry.MOB_EFFECT.getKey(effect.getEffect()).toString()){
                case "minecraft:speed":
                    float extraMult = (effect.getAmplifier()+1)*0.1f;
                    joystickMult+=extraMult;
                    break;
                case "minecraft:slowness":
                    float slownessMult = (effect.getAmplifier()+1)*-0.15f;
                    joystickMult+=slownessMult;
                    if (joystickMult<0)
                        joystickMult=0;
                    break;
                case "minecraft:levitation":
                    mchar.setVelocity(new Vector3f(mchar.velocity().x(),
                            Math.min(mchar.velocity().y()+0.04f,.2f),
                            mchar.velocity().z()));
                    LibSM64.MCharChangeAction(mchar.id,SM64MCharAction.ACT_FREEFALL);
                    break;
                case "minecraft:slow_falling":
                    mchar.setVelocity(new Vector3f(mchar.velocity().x(),
                            Math.max(mchar.velocity().y(),-.2f),
                            mchar.velocity().z()));
                    break;
                case "minecraft:poison":
                    poisoned=true;
                    break;

            }
        }
        // update movement
        updatePlayerMovement(plr,joystickMult);

        /*if (Keybinds.getDebugToggle().consumeClick()){
            debug = !debug;
        }*/

        // sleep handling
        updatePlayerSleep(plr,mchar);

        updatePlayerRiding(plr);

        updateWorldGeometry(plr, world, false);

        Libsm64Library.INSTANCE.sm64_mChar_set_water_level(SM64EnvManager.selfMChar.id, (int) (Utils.findWaterLevel(world,plr.blockPosition())*LibSM64.SCALE_FACTOR)+90);
        Libsm64Library.INSTANCE.sm64_mChar_set_gas_level(SM64EnvManager.selfMChar.id, !poisoned?-10000:10000);

        updatePlayerAttack(plr, world);

        SM64EnvManager.selfMChar.state.currentModel= SM64EnvManager.playerModel;

        SM64EnvManager.sm64Update();

        if (!plr.isPassenger())
            updatePlayerPosition(plr);

        updatePlayerBreath(plr);

        updatePlayerDeath(plr);
        world.getProfiler().pop();
    }

    /**
     * Update player position, relay it to the server.
     * Teleports either mChar -> player or player -> mChar depending on distance
     * @param plr player
     */
    private static void updatePlayerPosition(LocalPlayer plr) {
        // Teleport the MChar to the player if it's too far away
        if (new Vec3(SM64EnvManager.selfMChar.x(), SM64EnvManager.selfMChar.y(), SM64EnvManager.selfMChar.z()).distanceTo(plr.position())>(2+ SM64EnvManager.selfMChar.velocityMagnitude()) && !plr.isSleeping()){
            SM64EnvManager.selfMChar.teleport(plr.position());
        }else{ // constantly update the real player's position
            plr.moveTo(SM64EnvManager.selfMChar.x(), SM64EnvManager.selfMChar.y(), SM64EnvManager.selfMChar.z());
            plr.setDeltaMovement(0,-0.01f,0);
        }
        // tell the server about the player's position. In the future this should be checked to prevent exploits

        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
        friendlyByteBuf.writeUUID(plr.getUUID());
        friendlyByteBuf.writeDouble(SM64EnvManager.selfMChar.x());
        friendlyByteBuf.writeDouble(SM64EnvManager.selfMChar.y());
        friendlyByteBuf.writeDouble(SM64EnvManager.selfMChar.z());
        try {
            friendlyByteBuf.writeByteArray(SM64EnvManager.selfMChar.animInfo.serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }
        friendlyByteBuf.writeShort(SM64EnvManager.selfMChar.animXRot);
        friendlyByteBuf.writeShort(SM64EnvManager.selfMChar.animYRot);
        friendlyByteBuf.writeShort(SM64EnvManager.selfMChar.animZRot);
        friendlyByteBuf.writeInt(SM64EnvManager.selfMChar.state.action);
        friendlyByteBuf.writeInt(SM64EnvManager.selfMChar.state.currentModel);
        ClientPlayNetworking.send(SM64PacketHandler.MCHAR_PACKET, friendlyByteBuf);
    }

    /**
     * Check if mChar has died
     * @param plr the player
     */
    private static void updatePlayerDeath(LocalPlayer plr) {
        // update death
        if (SM64EnvManager.selfMChar.deathTime!=0 && Util.getEpochMillis()- SM64EnvManager.selfMChar.deathTime>5000){
            var act = SM64EnvManager.selfMChar.state.action;
            if ((SM64MCharActionFlags.ACT_FLAG_INTANGIBLE.value & act) != 0)
                RemoteMCharHandler.mCharOff(plr,true);
        }
    }

    /**
     * Update the player's air remaining. If in R64 mode, mChar's health is synced to player's breath
     * @param plr the player
     */
    private static void updatePlayerBreath(LocalPlayer plr) {
        if (plr.isUnderWater()){
            var hpPct = (SM64EnvManager.selfMChar.state.health-255)/2176f;
            plr.setAirSupply((int) (300*hpPct));
        }
    }

    /**
     * Update the player's attack status. Reads from libsm64 and relays it to MC
     * @param plr The player
     * @param world The world
     */
    private static void updatePlayerAttack(LocalPlayer plr, ClientLevel world) {
        if (SM64EnvManager.selfMChar!=null && selfAttacking(SM64EnvManager.selfMChar)){
            if(SM64EnvManager.attackDebounce < System.currentTimeMillis())
            {
                SM64EnvManager.attackDebounce = System.currentTimeMillis() + 500;
                for (Entity e: world.getEntities(null, AABB.ofSize(plr.position(),3,2,3))) {
                    if (e instanceof LivingEntity && e != plr && e.isAlive()){
                        Minecraft.getInstance().gameMode.attack(plr,e);
                        Utils.attackPacketApplyKnockback(e, SM64EnvManager.selfMChar.state.faceAngle);
                        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
                        friendlyByteBuf.writeInt(e.getId());
                        friendlyByteBuf.writeFloat(SM64EnvManager.selfMChar.state.faceAngle);
                        ClientPlayNetworking.send(SM64PacketHandler.ATTACK_PACKET, friendlyByteBuf);
                    }
                }
            }
        }
    }

    /**
     * Updates the world geometry and relay it to libsm64 so that collisions can be calculated
     * @param plr the player
     * @param world the world
     * @param optifineFix is Optifine detected and shaders are enabled?
     */
    private static void updateWorldGeometry(LocalPlayer plr, ClientLevel world, boolean optifineFix) {
        BlockPos playerPos= plr.blockPosition();

        // get area around player
        Iterable<BlockPos> nearbyBlockPos = BlockPos.betweenClosed(playerPos.offset(-1,-2,-1),playerPos.offset(1,2,1));
        ArrayList<surfaceItem> surfaces = new ArrayList<>(); // surfaces to send to libsm64
        var worldBorder=world.getWorldBorder();
        for (BlockPos bp:nearbyBlockPos) {
            BlockPos nearbyBlock=bp.immutable();

            if (nearbyBlock.getX() > worldBorder.getMaxX() || nearbyBlock.getX() < worldBorder.getMinX() || nearbyBlock.getZ() > worldBorder.getMaxZ() || nearbyBlock.getZ() < worldBorder.getMinZ())
            {
                surfaces.add(new surfaceItem(new Vec3(nearbyBlock.getX(),nearbyBlock.getY(),nearbyBlock.getZ()),true,false,SM64SurfaceType.SURFACE_DEFAULT, SM64TerrainType.Stone));
                continue;
            }
            if (world.getBlockState(nearbyBlock).isAir())
                continue;
            BlockState nearbyBlockState = world.getBlockState(nearbyBlock);
            List<Vec3> collisionVertices = new ArrayList<>();

            BakedModel blockModel = Minecraft.getInstance().getModelManager().getModel(BlockModelShaper.stateToModelLocation(nearbyBlockState));

            List<Vec3> blockModelQuads = Utils.getAllQuads(blockModel,nearbyBlockState, Minecraft.getInstance().level.random);

            for (Vec3 quad :blockModelQuads) {
                collisionVertices.add(quad.add(nearbyBlock.getX(),nearbyBlock.getY(),nearbyBlock.getZ()));
            }

            if (!nearbyBlockState.isAir()){
                var blockName = Registry.BLOCK.getKey(nearbyBlockState.getBlock()).toString();
                if (nearbyBlockState.getMaterial().isSolid()){
                    //// SOLID ////

                    if (optifineFix){
                        // If optifine is installed + shaders enabled, replace all collisions with squares
                        collisionVertices.clear();
                        surfaces.add(new surfaceItem(new Vec3(nearbyBlock.getX(),nearbyBlock.getY(),nearbyBlock.getZ()),true,false,BlockMatMaps.getSolidMat(blockName), SM64TerrainType.Stone));
                    }

                    if (BlockMatMaps.replaceCollisionMat(blockName)){
                        // force cube collision box
                        collisionVertices.clear();
                        surfaces.add(new surfaceItem(new Vec3(nearbyBlock.getX(),nearbyBlock.getY(),nearbyBlock.getZ()),true,false,BlockMatMaps.getSolidMat(blockName), SM64TerrainType.Stone));
                    } else if (BlockMatMaps.flatCollisionMat(blockName)){
                        // force flat collision box
                        collisionVertices.clear();
                        var cols=LibSM64SurfUtils.surfsToVec(LibSM64SurfUtils.plane(nearbyBlock.getX(),nearbyBlock.getY(),nearbyBlock.getZ(),0,BlockMatMaps.getSolidMat(blockName).value,SM64TerrainType.Stone));
                        collisionVertices.addAll(Arrays.asList(cols));
                        surfaces.add(new surfaceItem(collisionVertices,BlockMatMaps.getSolidMat(blockName), SM64TerrainType.Stone));
                    } else if (nearbyBlockState.getBlock().getFriction()>=0.7f){
                        // if block is slippery, set it to ice
                        surfaces.add(new surfaceItem(collisionVertices,SM64SurfaceType.SURFACE_ICE, SM64TerrainType.Snow));
                    } else if (nearbyBlockState.getBlock().getSpeedFactor()<0.6f) {
                        // if block is slow (soul sand?), set it to quicksand
                        surfaces.add(new surfaceItem(collisionVertices,SM64SurfaceType.SURFACE_SHALLOW_QUICKSAND, SM64TerrainType.Sand));
                    } else if (nearbyBlockState.getBlock().hasDynamicShape()) {
                        // blocks with dynamic shapes which are also solid get set to cubes
                        collisionVertices.clear();
                        surfaces.add(new surfaceItem(new Vec3(nearbyBlock.getX(),nearbyBlock.getY(),nearbyBlock.getZ()),true,false,BlockMatMaps.getSolidMat(blockName), SM64TerrainType.Stone));
                    } else {
                        var stype = nearbyBlockState.getBlock().getSoundType(nearbyBlockState);
                        var stSound = stype.getStepSound().getLocation().toString();
                        short type = SM64TerrainType.Stone;
                        // Determine what material a block is by it's sound type
                        switch (stSound) {
                            case "minecraft:block.gravel.step":
                            case "minecraft:block.sand.step":
                                type = SM64TerrainType.Sand;
                                break;
                            case "minecraft:block.grass.step":
                                type = SM64TerrainType.Grass;
                                break;
                            case "minecraft:block.wood.step":
                                type = SM64TerrainType.Spooky;
                                break;
                            default:
                                break;
                        }
                        surfaces.add(new surfaceItem(collisionVertices,BlockMatMaps.getSolidMat(blockName), type));
                    }
                } else {
                    //// NON-SOLID ////
                    if (BlockMatMaps.replaceCollisionMat(blockName)){
                        // this forces non-solid blocks to be cubes
                        collisionVertices.clear();
                        surfaces.add(new surfaceItem(new Vec3(nearbyBlock.getX(),nearbyBlock.getY(),nearbyBlock.getZ()),true,false,BlockMatMaps.getNonsolidMat(blockName), SM64TerrainType.Stone));
                    } else if (BlockMatMaps.flatCollisionMat(blockName)){
                        // this forces non-solid blocks to be flat
                        collisionVertices.clear();
                        surfaces.add(new surfaceItem(new Vec3(nearbyBlock.getX(),nearbyBlock.getY(),nearbyBlock.getZ()),false,true,BlockMatMaps.getNonsolidMat(blockName), SM64TerrainType.Stone));
                    }
                }
            }
        }

        // convert to array and pass to libsm64
        surfaceItem[] surfItems = new surfaceItem[surfaces.size()];
        surfaces.toArray(surfItems);
        SM64EnvManager.updateSurfs(surfItems);
    }

    /**
     * Check if the player is riding an entity (i.e pig, horse, etc) and set the player's animation accordingly
     * @param plr The player to check
     */
    private static void updatePlayerRiding(LocalPlayer plr) {
        // check if player is riding a mob, if so set mChar to ACT_GRABBED
        if (plr.isPassenger()){
            var pos = plr.getVehicle().position();
            SM64EnvManager.selfMChar.teleport(pos);
            var ang = (float)(Math.toRadians(plr.getVehicle().getYRot()/-9.5f));//(short)(plr.getVehicle().yRotO*(32768*Math.PI));
            Libsm64Library.INSTANCE.sm64_mChar_set_angle(SM64EnvManager.selfMChar.id, ang);
            if (SM64EnvManager.selfMChar.state.action!= SM64MCharAction.ACT_GRABBED.id)
                LibSM64.MCharChangeAction(SM64EnvManager.selfMChar.id,SM64MCharAction.ACT_GRABBED.id);
        }else if (!plr.isPassenger() && SM64EnvManager.selfMChar.state.action==SM64MCharAction.ACT_GRABBED.id){
            LibSM64.MCharChangeAction(SM64EnvManager.selfMChar.id,SM64MCharAction.ACT_IDLE.id);
        }
    }

    /**
     * Updates the player's controls, pass that to libsm64
     * @param plr the player
     * @param joystickMult the joystick multiplier, used for potion effects
     */
    private static void updatePlayerMovement(LocalPlayer plr,float joystickMult) {
        var cam_fwd = plr.getLookAngle().yRot((float)Math.toRadians(90));//Objects.requireNonNull(Minecraft.getInstance().getCameraEntity()).getForward();
        var cam_pos=Minecraft.getInstance().getCameraEntity().position();
        SM64EnvManager.updateControls(cam_fwd,cam_pos, joystickMult
        ,Keybinds.getActKey().isDown(),plr.input.jumping,plr.input.shiftKeyDown,plr.input.up,plr.input.left,plr.input.down,plr.input.right);
    }

    /**
     * Check if the player is sleeping in MC and relay that to libsm64
     * @param plr the player to check
     * @param mchar the mchar to update
     */
    private static void updatePlayerSleep(LocalPlayer plr,MChar mchar) {
        if (plr.isSleeping() && mchar.state.action!=SM64MCharAction.ACT_SLEEPING.id && mchar.state.action!=SM64MCharAction.ACT_START_SLEEPING.id){
            if (plr.getSleepingPos().isPresent() && plr.getSleepTimer()>1){
                var bedPos = plr.getSleepingPos().get().immutable();
                var bedPos2 = bedPos.offset(plr.getBedOrientation().getOpposite().getNormal());
                // get midpoint of bedPos and bedPos2
                var midpoint = new Vec3(
                        (bedPos.getX()+bedPos2.getX())/2f,(bedPos.getY()+bedPos2.getY())/2f,(bedPos.getZ()+bedPos2.getZ())/2f);
                midpoint = midpoint.add(.5f,0,.5f);
                var sleepDir= plr.getBedOrientation().toYRot()+75;
                var ang = (float)(Math.toRadians(sleepDir/9.5f));
                Libsm64Library.INSTANCE.sm64_mChar_set_angle(mchar.id, ang);
                SM64EnvManager.selfMChar.teleport(midpoint);

                LibSM64.MCharChangeAction(SM64EnvManager.selfMChar.id, SM64MCharAction.ACT_SLEEPING);
                Libsm64Library.INSTANCE.sm64_mChar_set_action_state(mchar.id,(short)1);
            }
        }
    }

    /**
     * Check if the player is attacking
     * @param mchar the player's mChar
     * @return true if the player is attacking
     */
    public static boolean selfAttacking(MChar mchar){
        if (mchar.state.testActionFlag(SM64MCharActionFlags.ACT_FLAG_ATTACKING)){
            return mchar.state.action == SM64MCharAction.ACT_PUNCHING.id ||
                    mchar.state.action == SM64MCharAction.ACT_MOVE_PUNCHING.id ||
                    mchar.state.action == SM64MCharAction.ACT_JUMP_KICK.id ||
                    mchar.state.action == SM64MCharAction.ACT_GROUND_POUND_LAND.id ||
                    mchar.state.action == SM64MCharAction.ACT_SLIDE_KICK.id ||
                    mchar.state.action == SM64MCharAction.ACT_SLIDE_KICK_SLIDE.id ||
                    mchar.state.action == SM64MCharAction.ACT_DIVE.id ||
                    mchar.state.action == SM64MCharAction.ACT_DIVE_SLIDE.id;

        }else{
            return false;
        }

    }
}
