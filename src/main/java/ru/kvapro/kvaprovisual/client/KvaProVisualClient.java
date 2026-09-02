package ru.kvapro.kvaprovisual.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class KvaProVisualClient implements ClientModInitializer {
    private static KeyBinding configKeyBinding;

    @Override
    public void onInitializeClient() {
        configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kvaprovisual.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.kvaprovisual.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            renderESP(client);
            
            ClientPlayerEntity player = client.player;
            if (player != null && player.isSprinting()) {
                player.getWorld().addParticle(new DustParticleEffect(new Vector3f(0f, 0.8f, 1f), 1f), player.getX(), player.getY() + 0.1, player.getZ(), 0, 0, 0);
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() && entity instanceof LivingEntity target) {
                for (int i = 0; i < 8; i++) {
                    world.addParticle(ParticleTypes.HEART, entity.getX(), entity.getY() + 1, entity.getZ(), 0, 0.1, 0);
                }
            }
            return ActionResult.PASS;
        });
    }

    private static void renderESP(MinecraftClient client) {
        Entity target = client.targetedEntity;
        if (target == null || !target.isAlive()) return;
        double time = (double) target.getWorld().getTime() * 0.15;
        for (int i = 0; i < 4; i++) {
            double angle = time + (i * (Math.PI / 2));
            target.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME, target.getX() + Math.cos(angle), target.getY() + 0.5, target.getZ() + Math.sin(angle), 0, 0.02, 0);
        }
    }
}
