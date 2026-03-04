package com.map_lib.util.command.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SpeakerSoundInstance extends AbstractTickableSoundInstance {
    private final Entity sourceEntity;
    private final float maxRange;
    private final float baseVolume;
    private final float fixedPitch;

    public SpeakerSoundInstance(Entity sourceEntity, double x, double y, double z, ResourceLocation soundLocation, SoundSource source, float range, boolean loop, float volume, float pitch) {
        super(SoundEvent.createVariableRangeEvent(soundLocation), source, SoundInstance.createUnseededRandom());

        this.sourceEntity = sourceEntity;

        if (sourceEntity != null) {
            this.x = sourceEntity.getX();
            this.y = sourceEntity.getY();
            this.z = sourceEntity.getZ();
        } else {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        this.maxRange = range;
        this.baseVolume = volume;
        this.fixedPitch = pitch;

        this.looping = loop;
        this.delay = 0;
        this.volume = 0.001f;
        this.pitch = pitch;
        this.relative = false;
        this.attenuation = Attenuation.NONE;
    }

    @Override
    public void tick() {
        if (this.sourceEntity != null) {
            if (this.sourceEntity.isRemoved()) {
                this.stop();
                return;
            }
            this.x = this.sourceEntity.getX();
            this.y = this.sourceEntity.getY();
            this.z = this.sourceEntity.getZ();
        }

        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) return;

        double dx = this.x - clientPlayer.getX();
        double dy = this.y - clientPlayer.getY();
        double dz = this.z - clientPlayer.getZ();
        double distSqr = dx * dx + dy * dy + dz * dz;
        double distance = Math.sqrt(distSqr);

        if (distance > this.maxRange) {
            this.volume = 0.0f;
        } else {
            float linearFactor = 1.0f - (float) (distance / this.maxRange);
            this.volume = Mth.clamp(linearFactor * this.baseVolume, 0.0f, this.baseVolume);
        }

        this.pitch = this.fixedPitch;
    }
}