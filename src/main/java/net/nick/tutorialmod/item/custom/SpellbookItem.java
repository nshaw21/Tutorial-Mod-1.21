package net.nick.tutorialmod.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.nick.tutorialmod.datacomponent.SpellbookDataComponents;
import net.nick.tutorialmod.effect.ModEffects;
import net.nick.tutorialmod.screen.custom.SpellbookMenuProvider;

import java.util.List;

public class SpellbookItem extends Item {
    private static final List<String> SPELLS = List.of("fireball", "smite", "teleport", "infinity");

    public SpellbookItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
//                 Open the GUI instead of changing the spell
//                serverPlayer.openMenu(new SimpleMenuProvider(
//                        (pContainerId, pPlayerInventory, pPlayer) -> new SpellbookMenu(pContainerId, pPlayerInventory),
//                        Component.literal("Spellbook")
//                ));
                serverPlayer.openMenu(new SpellbookMenuProvider(stack));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        } else {
            // Cast the selected spell
            if (!level.isClientSide) {
                String spell = stack.get(SpellbookDataComponents.SELECTED_SPELL.get());
                if (spell == null || spell.isEmpty()) { // If the spell is null | there is no spell loaded
                    spell = "fireball"; // Auto set it to fireball spell
                    stack.set(SpellbookDataComponents.SELECTED_SPELL.get(), spell); // Load the spell
                }
                castSpell(spell, player, level);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
    }

    private String getNextSpell(String current) {
        int index = SPELLS.indexOf(current); // Find current spell index in the list
        if (index == -1 || index + 1 >= SPELLS.size()) { // If not found OR current is last spell
            return SPELLS.get(0); // Return the first spell (wrap around)
        }
        return SPELLS.get(index + 1); // Otherwise, return the next spell in the list
    }

    private void castSpell(String spell, Player player, Level level) {
        switch (spell) {
            case "fireball" -> {
                SmallFireball fireball = new SmallFireball(EntityType.SMALL_FIREBALL, level);
                // Set fireballs position
                fireball.setPos(
                        player.getX() + player.getLookAngle().x * 1.5,
                        player.getEyeY() + player.getLookAngle().y * 1.5,
                        player.getZ() + player.getLookAngle().z * 1.5
                );
                // Make it move
                fireball.setDeltaMovement(player.getLookAngle().scale(0.5));

                fireball.setOwner(player); // In case I want to do damage tracking

                level.addFreshEntity(fireball);
                return;
            }
            case "smite" -> {
                var hitResult = player.pick(50.0D, 0.0F, false); // 50 block range | Ray trace to see where player is looking | false, ignoring fluids (water / lava)
                var hitPos = hitResult.getLocation(); // Gives the 3D coordinates of where the player is pointing

                if (level instanceof ServerLevel serverLevel) {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel); // Spawns the lightning bolt
                    if (lightning != null) {
                        lightning.moveTo(hitPos.x, hitPos.y, hitPos.z); // Moves it to the location where we're looking
                        lightning.setCause(((ServerPlayer) player)); // Optional: tracks who caused it
                        serverLevel.addFreshEntity(lightning); // Spawns the lightning into the world
                    }
                }
                return;
            }
            case "teleport" -> {
                double maxRange = 20.0; // Max range for tp

                var lookVec = player.getLookAngle(); // Get where the player is looking

                // Ray trace to see if something is in the way
                var hitResult = player.pick(maxRange,0.0f, false);

                var teleportPos = switch (hitResult.getType()) {
                    case BLOCK, ENTITY -> {
                        //Hit something, teleport slightly before the hit position
                        var hit = hitResult.getLocation();
                        var backOff = lookVec.normalize().scale(1.0); // 1 block before
                        yield hit.subtract(backOff);
                    }
                    case MISS -> {
                        var targetX = player.getX() + lookVec.x * maxRange;
                        var targetY = player.getY() + lookVec.y * maxRange;
                        var targetZ = player.getZ() + lookVec.z * maxRange;
                        yield new Vec3(targetX, targetY, targetZ);
                    }
                };
                player.teleportTo(teleportPos.x, teleportPos.y, teleportPos.z);
                return;
            }
            case "infinity" -> {
                int infinityDuration = 200; // Activate the ability for 10 seconds

                // Apply the infinity effect
                player.addEffect(new MobEffectInstance(
                        ModEffects.INFINITY_EFFECT.getHolder().get(),
                        infinityDuration,
                        0, // Amplifier (0 = level 1)
                        false, // Ambient
                        true, // Show particles
                        true // Show icon
                ));

                player.displayClientMessage(
                        Component.literal("Infinity activated for 10 seconds!"),
                        true
                );

                // Optional: Add sound effect
                // player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
                return;
            }

            default -> player.displayClientMessage(Component.literal("No spell selected."), true);
        }
    }
}
