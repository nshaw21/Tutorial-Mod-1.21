package net.nick.tutorialmod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.nick.tutorialmod.block.ModBlocks;
import net.nick.tutorialmod.worldgen.dimension.ModDimensions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PocketPortalBlock extends Block {
    // Static map to store player return positions
    private static final Map<UUID, Vec3> playerReturnPositions = new HashMap<>();
    private static final String NBT_KEY_RETURN_POS = "PocketReturnPos";
    
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

            // Determine destination position
            Vec3 destinationPos;
            if (targetLevelKey == ModDimensions.POCKETDIM_LEVEL_KEY) {
                // Going to pocket dimension - save current position and go to pocket room
                savePlayerReturnPosition(serverPlayer);
                destinationPos = new Vec3(0.5, 100, 0.5);
                
                // Generate the pocket room if it doesn't exist
                generatePocketRoom(targetLevel, new BlockPos(0, 100, 0));
            } else {
                // Going back to overworld - restore saved position or use spawn
                destinationPos = getPlayerReturnPosition(serverPlayer, targetLevel);
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
    
    /**
     * Generates a white pocket room if it doesn't already exist
     * @param level The pocket dimension level
     * @param center The center position (where player spawns)
     */
    private void generatePocketRoom(ServerLevel level, BlockPos center) {
        // Multiple checks to ensure room doesn't already exist
        
        // Check 1: Floor directly below spawn point
        if (level.getBlockState(center.below()).getBlock() == ModBlocks.POCKET_WALL_BLOCK.get()) {
            return; // Room already exists, don't regenerate
        }
        
        // Check 2: Multiple wall positions to be absolutely sure
        int roomSize = 15;
        BlockPos[] checkPositions = {
            center.offset(roomSize/2, 0, 0),     // East wall
            center.offset(-roomSize/2, 0, 0),    // West wall
            center.offset(0, 0, roomSize/2),     // South wall
            center.offset(0, 0, -roomSize/2),    // North wall
            center.offset(0, roomSize-2, 0)      // Ceiling
        };
        
        for (BlockPos checkPos : checkPositions) {
            if (level.getBlockState(checkPos).getBlock() == ModBlocks.POCKET_WALL_BLOCK.get()) {
                return; // Room structure detected, don't regenerate
            }
        }
        
        // Check 3: Look for any glowstone lights (unique to our room)
        int lightOffset = roomSize/2 - 1;
        BlockPos lightCheck = center.offset(-lightOffset, roomSize - 3, -lightOffset);
        if (level.getBlockState(lightCheck).getBlock() == Blocks.GLOWSTONE) {
            return; // Room lighting detected, don't regenerate
        }
        
        // Room dimensions already defined above
        // int roomSize = 15; // Already defined
        int floorY = center.getY() - 1; // Floor is 1 block below spawn point
        
        BlockState wallBlock = ModBlocks.POCKET_WALL_BLOCK.get().defaultBlockState();
        BlockState floorBlock = ModBlocks.POCKET_WALL_BLOCK.get().defaultBlockState(); // Use same block for floor
        BlockState airBlock = Blocks.AIR.defaultBlockState();

        /*
        This identifies wall positions:
        •  Left/Right walls: x = -7 or x = +7
        •  Front/Back walls: z = -7 or z = +7
        •  Floor: y = -2 (relative to spawn point)
        •  Ceiling: y = +13 (relative to spawn point)
         */

        // Generate the room
        for (int x = -roomSize/2; x <= roomSize/2; x++) {           // X-axis: -7 to +7 (15 blocks wide)
            for (int y = -2; y <= roomSize - 2; y++) {              // Y-axis: -2 to +13 (16 blocks tall)
                for (int z = -roomSize/2; z <= roomSize/2; z++) {   // Z-axis: -7 to +7 (15 blocks deep)
                    BlockPos pos = center.offset(x, y, z);
                    
                    // Determine what block to place
                    boolean isWall = (x == -roomSize/2 || x == roomSize/2 ||    // Left or right edge
                                     z == -roomSize/2 || z == roomSize/2 ||     // Front or back edge
                                     y == -2 || y == roomSize - 2);             // Floor or ceiling
                    
                    if (isWall) {
                        if (y == -2) {
                            // Floor - use unbreakable white blocks
                            level.setBlockAndUpdate(pos, floorBlock);
                        } else {
                            // Walls and ceiling - use unbreakable white blocks
                            level.setBlockAndUpdate(pos, wallBlock);
                        }
                    } else {
                        // Inside the room - make sure it's air
                        level.setBlockAndUpdate(pos, airBlock);
                    }
                }
            }
        }
        
        // Add some light sources (glowstone in corners)
        BlockState lightBlock = Blocks.GLOWSTONE.defaultBlockState();
        // lightOffset already defined above: roomSize/2 - 1
        
        // Place lights in the ceiling corners
        level.setBlockAndUpdate(center.offset(-lightOffset, roomSize - 3, -lightOffset), lightBlock);   // Northwest corner
        level.setBlockAndUpdate(center.offset(lightOffset, roomSize - 3, -lightOffset), lightBlock);    // Northeast corner
        level.setBlockAndUpdate(center.offset(-lightOffset, roomSize - 3, lightOffset), lightBlock);    // Southwest corner
        level.setBlockAndUpdate(center.offset(lightOffset, roomSize - 3, lightOffset), lightBlock);     // Southeast corner
    }
    
    /**
     * Saves the player's current position so they can return to it later
     * @param player The player entering the pocket dimension
     */
    private void savePlayerReturnPosition(ServerPlayer player) {
        Vec3 currentPos = player.position();
        
        // Save to both memory map and player NBT data for persistence
        playerReturnPositions.put(player.getUUID(), currentPos);
        
        // Also save to player's persistent data
        CompoundTag playerData = player.getPersistentData();
        CompoundTag returnPosData = new CompoundTag();
        returnPosData.putDouble("x", currentPos.x);
        returnPosData.putDouble("y", currentPos.y);
        returnPosData.putDouble("z", currentPos.z);
        playerData.put(NBT_KEY_RETURN_POS, returnPosData);
    }
    
    /**
     * Gets the player's saved return position, or their spawn point if none saved
     * @param player The player leaving the pocket dimension
     * @param targetLevel The overworld level
     * @return The position to teleport the player to
     */
    private Vec3 getPlayerReturnPosition(ServerPlayer player, ServerLevel targetLevel) {
        UUID playerId = player.getUUID();
        
        // Try to get from memory first
        Vec3 savedPos = playerReturnPositions.get(playerId);
        if (savedPos != null) {
            // Clear the saved position after using it
            playerReturnPositions.remove(playerId);
            return savedPos;
        }
        
        // Try to get from persistent NBT data
        CompoundTag playerData = player.getPersistentData();
        if (playerData.contains(NBT_KEY_RETURN_POS)) {
            CompoundTag returnPosData = playerData.getCompound(NBT_KEY_RETURN_POS);
            double x = returnPosData.getDouble("x");
            double y = returnPosData.getDouble("y");
            double z = returnPosData.getDouble("z");
            
            // Clear the saved data after using it
            playerData.remove(NBT_KEY_RETURN_POS);
            
            return new Vec3(x, y, z);
        }
        
        // No saved position - use player's spawn point
        BlockPos spawnPos = player.getRespawnPosition();
        if (spawnPos != null) {
            // Player has a bed/respawn anchor - use that
            return new Vec3(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        } else {
            // No personal spawn - use world spawn
            BlockPos worldSpawn = targetLevel.getSharedSpawnPos();
            return new Vec3(worldSpawn.getX() + 0.5, worldSpawn.getY(), worldSpawn.getZ() + 0.5);
        }
    }
}
