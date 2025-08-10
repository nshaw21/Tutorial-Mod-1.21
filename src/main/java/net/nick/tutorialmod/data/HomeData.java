package net.nick.tutorialmod.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.UUID;

public class HomeData extends SavedData {
    private static final String DATA_NAME = "tutorialmod_homes";
    public final HashMap<UUID, Vec3> homePositions = new HashMap<>();

    public HomeData() {}

    public HomeData(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag homesList = tag.getList("homes", 10); // 10 = CompoundTag type
        for (int i = 0; i < homesList.size(); i++) {
            CompoundTag homeTag = homesList.getCompound(i);
            UUID uuid = homeTag.getUUID("uuid");
            double x = homeTag.getDouble("x");
            double y = homeTag.getDouble("y");
            double z = homeTag.getDouble("z");
            homePositions.put(uuid, new Vec3(x, y, z));
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag homesList = new ListTag();
        for (var entry : homePositions.entrySet()) {
            CompoundTag homeTag = new CompoundTag();
            homeTag.putUUID("uuid", entry.getKey());
            homeTag.putDouble("x", entry.getValue().x);
            homeTag.putDouble("y", entry.getValue().y);
            homeTag.putDouble("z", entry.getValue().z);
            homesList.add(homeTag);
        }
        tag.put("homes", homesList);
        return tag;
    }

    public static HomeData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(
                        () -> new HomeData(), // Supplier for new instances
                        (tag, provider) -> new HomeData(tag, provider), // Function for loading from NBT
                        null // DataFixerType (can be null)
                ),
                DATA_NAME
        );
    }

    public void setHome(UUID playerUUID, Vec3 position) {
        homePositions.put(playerUUID, position);
        setDirty(); // Mark as changed so it gets saved
    }

    public Vec3 getHome(UUID playerUUID) {
        return homePositions.get(playerUUID);
    }

    public boolean hasHome(UUID playerUUID) {
        return homePositions.containsKey(playerUUID);
    }
}
