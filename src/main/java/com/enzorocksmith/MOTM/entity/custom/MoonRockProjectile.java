package com.enzorocksmith.MOTM.entity.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Random;

public class MoonRockProjectile extends AbstractArrow {
    LivingEntity shooter;

    public MoonRockProjectile(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MoonRockProjectile(EntityType<? extends AbstractArrow> pEntityType, LivingEntity pShooter, Level pLevel) {
        super(pEntityType, pShooter, pLevel);
        shooter = pShooter;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() instanceof MoonMob) {
            return;
        }

        super.onHitEntity(result);

        Random random = new Random();
        MobEffectInstance effect = new MobEffectInstance(MobEffects.WEAKNESS, 35, 1, false, false, false);
        LivingEntity entity = (LivingEntity) result.getEntity();

        switch (random.nextInt(0, 2)) {
            case 0: effect = new MobEffectInstance(MobEffects.BLINDNESS, 35, 1, false, false, false); break;
            case 1: effect = new MobEffectInstance(MobEffects.WEAKNESS, 35, 1, false, false, false); break;
            case 2: effect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 1, false, false, false); break;
            }

        entity.addEffect(effect);
        this.kill();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide() && level().getBlockState(result.getBlockPos()).getBlock() instanceof AbstractGlassBlock)
        {
            level().destroyBlock(result.getBlockPos(), false);
        }
        this.kill();
    }

    @Override
    protected ItemStack getPickupItem() {
        return null;
    }
}
