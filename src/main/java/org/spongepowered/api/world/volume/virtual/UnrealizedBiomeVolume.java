package org.spongepowered.api.world.volume.virtual;

import org.spongepowered.api.world.biome.Biome;
import org.spongepowered.api.world.volume.biome.BiomeVolume;

public interface UnrealizedBiomeVolume<B extends BiomeVolume> extends Virtualized<Biome, B> {

    interface Streamable<B extends Streamable<B, BU>, BU extends BiomeVolume.Streamable<BU>>
        extends UnrealizedBiomeVolume<BU>,
        Virtualized.Streamable<Biome, B, BU>
    {

    }

    interface Unmodifiable<U extends Unmodifiable<U, BU>, BU extends BiomeVolume.Unmodifiable<BU>>
        extends UnrealizedBiomeVolume<BU>,
        Streamable<U, BU>,
        Virtualized.Unmodifiable<Biome, U, BU> {

    }

    interface Modifiable<B extends Modifiable<B, MB>, MB extends BiomeVolume.Modifiable<MB>>
    extends UnrealizedBiomeVolume<MB>,
        Streamable<B, MB>,
        Virtualized.Mutable<Biome, B, MB> {

    }

    interface Mutable extends Modifiable<Mutable, BiomeVolume.Mutable> {

    }

    interface Immutable extends Unmodifiable<Immutable, BiomeVolume.Immutable> {

    }


}
