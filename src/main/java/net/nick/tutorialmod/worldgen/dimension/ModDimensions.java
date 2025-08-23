package net.nick.tutorialmod.worldgen.dimension;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.nick.tutorialmod.TutorialMod;

import java.util.List;
import java.util.OptionalLong;

public class ModDimensions {
    // These are like "ID cards" for your dimension
    public static final ResourceKey<LevelStem> POCKET_KEY = ResourceKey.create(Registries.LEVEL_STEM,   // The dimension's blueprint
            ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID, "pocketdim"));
    public static final ResourceKey<Level> POCKETDIM_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,   // The actual dimension
            ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID, "pocketdim"));
    public static final ResourceKey<DimensionType> POCKET_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,  // What type of dimension it is
            ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID, "pocketdim_type"));

    // World Settings
    public static void bootstrapType(BootstrapContext<DimensionType> context) {
        context.register(POCKET_DIM_TYPE, new DimensionType(
                OptionalLong.of(12000), // fixedTime (specific time of day)
                false, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                1.0, // coordinateScale
                true, // bedWorks (If false, bed blows up when trying to sleep)
                false, // respawnAnchorWorks
                0, // minY
                256, // height
                256, // logicalHeight (Max height which chorus fruits and nether portals can take you, can't be heigher than height)
                BlockTags.INFINIBURN_OVERWORLD, // normal overworld blocks
                BuiltinDimensionTypes.OVERWORLD_EFFECTS, // determines the dimension effect (overworld has clouds and sky, sun, moon)
                1.0f, // ambient light level ( 0 = normal | 1 = no ambient lighting)
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0),0)));
    }

    // World Generation
    public static void bootstrapStem(BootstrapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        // Uses all these biomes in the dimension
        NoiseBasedChunkGenerator noiseBasedChunkGenerator = new NoiseBasedChunkGenerator(
                MultiNoiseBiomeSource.createFromList(
                        new Climate.ParameterList<>(List.of(Pair.of(
                                    Climate.parameters(0.1F, 0.2F, 0.0F, 0.2F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrThrow(Biomes.BIRCH_FOREST)),
                            Pair.of(
                                    Climate.parameters(0.3F, 0.6F, 0.1F, 0.1F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrThrow(Biomes.OCEAN)),
                            Pair.of(
                                    Climate.parameters(0.4F, 0.3F, 0.2F, 0.1F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrThrow(Biomes.DARK_FOREST))

                        ))),
                noiseGenSettings.getOrThrow(NoiseGeneratorSettings.AMPLIFIED));

        LevelStem stem = new LevelStem(dimTypes.getOrThrow(ModDimensions.POCKET_DIM_TYPE), noiseBasedChunkGenerator);

        context.register(POCKET_KEY, stem);
    }
}
