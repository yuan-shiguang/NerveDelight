package com.yuanshiguang.nervedelight;

import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

@Mod.EventBusSubscriber(modid = "nervedelight", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Effects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "nervedelight");

    public static final RegistryObject<MobEffect> INDIGESTION = 
            EFFECTS.register("indigestion", IndigestionEffect::new);

    public static class IndigestionEffect extends MobEffect {
        public IndigestionEffect() {
            super(MobEffectCategory.HARMFUL, 0x8B4513);
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity instanceof Player player && !player.level.isClientSide) {
                player.hurt(DamageSource.MAGIC, 1.0F);
            }
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return duration % 20 == 0;
        }

        @Override
        public Component getDisplayName() {
            return new TextComponent("effect.nervedelight.indigestion");
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && 
            event.player.getMainHandItem().getItem() == ModItem.GRILLED_REBAR.get()) {
            event.player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                40, 1, false, false, true
            ));
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getPlayer() != null && 
            event.getPlayer().hasEffect(INDIGESTION.get()) && 
            event.getItemStack().isEdible()) {
            
            event.setCanceled(true);
            event.getPlayer().displayClientMessage(
                new TextComponent("message.nervedelight.indigestion_warning"),
                true
            );
        }
    }
}