package com.map_lib.util.command.pathfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ClientPathRenderer {

    public static void updatePath(List<BlockPos> path, ParticleOptions particle) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || path.isEmpty()) return;

        spawnParticles(mc, path, particle);
    }

    private static void spawnParticles(Minecraft mc, List<BlockPos> path, ParticleOptions particle) {
        Vec3 playerPos = mc.player.position();
        double renderDistanceSqr = 32 * 32;

        for (BlockPos pos : path) {
            if (pos.distToCenterSqr(playerPos) > renderDistanceSqr) continue;

            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            var p = mc.particleEngine.createParticle(particle, x, y, z, 0, 0, 0);

            if (p != null) {
                p.setLifetime(2);
            }
        }
    }
}