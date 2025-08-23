package net.nick.tutorialmod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.nick.tutorialmod.worldgen.dimension.ModDimensions;

public class PocketPortalBlock extends Block {
    public PocketPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level level, BlockPos pPos,
                                               Player player, BlockHitResult pHitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // Access Minecraft server
            MinecraftServer server = serverPlayer.server;

            // Check if player is already in the pocket dimension
            ResourceKey<Level> targetLevelKey = serverPlayer.level().dimension() == ModDimensions.POCKETDIM_LEVEL_KEY
                    ? Level.OVERWORLD : ModDimensions.POCKETDIM_LEVEL_KEY;

            // Get target world
            ServerLevel targetLevel = server.getLevel(targetLevelKey);
            if (targetLevel == null) {
                return InteractionResult.FAIL;
            }

            // Determine destination position (Gets the actual world object to teleport to)
            Vec3 destinationPos;
            if (targetLevelKey == ModDimensions.POCKETDIM_LEVEL_KEY) { // If in the overworld
                // Going to pocket dimension - spawn at a safe location
                destinationPos = new Vec3(0.5, 100, 0.5); // Spawn here in pocketDimension
            } else {
                // Going back to overworld - spawn at world spawn or player's bed
                destinationPos = new Vec3(
                    targetLevel.getSharedSpawnPos().getX() + 0.5,
                    targetLevel.getSharedSpawnPos().getY(),
                    targetLevel.getSharedSpawnPos().getZ() + 0.5
                );
            }

            // Create dimension transition with proper destination
            DimensionTransition transition = new DimensionTransition(
                    targetLevel,    // Which world
                    destinationPos, // Where to spawn (x , y, z coordinates)
                    Vec3.ZERO, // velocity (.zero means none)
                    serverPlayer.getYRot(), // yaw rotation (looking direction)
                    serverPlayer.getXRot(), // pitch rotation
                    DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET) // Play sound and make portal ticket (for safety)
            );

            // Actually move the player
            serverPlayer.changeDimension(transition);


            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }
}
