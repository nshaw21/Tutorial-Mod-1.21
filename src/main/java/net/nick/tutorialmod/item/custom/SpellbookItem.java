package net.nick.tutorialmod.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.nick.tutorialmod.datacomponent.SpellbookDataComponents;

import java.util.List;

public class SpellbookItem extends Item {
    private static final List<String> SPELLS = List.of("fireball", "smite", "teleport");

    public SpellbookItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                String currentSpell = stack.get(SpellbookDataComponents.SELECTED_SPELL.get());
                if (currentSpell == null) currentSpell = "fireball";

                String nextSpell = getNextSpell(currentSpell);
                stack.set(SpellbookDataComponents.SELECTED_SPELL.get(), nextSpell);

                // Displays it right above hot-bar
                player.displayClientMessage(Component.literal("Selected spell: " + nextSpell), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        } else {
            if (!level.isClientSide) {
                String spell = stack.get(SpellbookDataComponents.SELECTED_SPELL.get());
                if (spell == null || spell.isEmpty()) {
                    spell = "fireball";
                    stack.set(SpellbookDataComponents.SELECTED_SPELL.get(), spell);
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
            }
            case "teleport" ->
                player.teleportTo(player.getX(), player.getY() + 10, player.getZ());

            default -> player.displayClientMessage(Component.literal("No spell selected."), true);
        }
    }
}
