package com.enzorocksmith.MOTM.entity.custom;


import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.blockMap.BlockBreakEntry;
import com.enzorocksmith.MOTM.blockMap.BlockBreakMap;
import com.enzorocksmith.MOTM.entity.ModEntities;
import com.enzorocksmith.MOTM.entity.navigation.FollowMMGoal;
import com.enzorocksmith.MOTM.entity.navigation.MoonMobFollowTargetGoal;
import com.enzorocksmith.MOTM.nightprogression.NightProgression;
import com.enzorocksmith.MOTM.utilities.MOTMUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Slinger extends MoonMob implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public int hordeSpawned;
    public int attackCoolDown;
    public int breakCoolDown;
    public int groupSize;
    public int seeTime;
    public int strafingTime;
    public int spawnProjectileTime;
    boolean strafingClockwise = true;
    boolean strafingBackwards = false;
    double targetDistance;
    Random random;

    // Triggerable Raw Animations

    private final RawAnimation attack = RawAnimation.begin().thenPlay("sling");
    private final RawAnimation death = RawAnimation.begin().thenPlay("death");
    private final RawAnimation hurt = RawAnimation.begin().thenPlay("hurt");

    public Slinger(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.attackCoolDown = 100;
        random = new Random();
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(0, new FloatGoal(this));
        //this.goalSelector.addGoal(1, new MoonMobFollowTargetGoal(this, 1, true));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(3, new FollowMMGoal(this, 1, 5, 30));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, .2)
                .add(Attributes.ATTACK_DAMAGE, 2);
    }

    // Register animations controllers
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller",  this::predicate));
        controllers.add(new AnimationController<>(this, "hurt", state -> PlayState.STOP)
                .triggerableAnim("hurt", hurt));
        controllers.add(new AnimationController<>(this,"sling",  state -> PlayState.STOP)
                .triggerableAnim("sling", attack));
        controllers.add(new AnimationController<>(this, "death", state -> PlayState.STOP)
                .triggerableAnim("death", death));
    }

    // loop or play animations based on the entity's state
    private PlayState predicate(AnimationState<Slinger> animationState) {
        if (this.isDeadOrDying()) {
            triggerAnim("death", "death");
            return PlayState.CONTINUE;
        }

        if (animationState.isMoving()) {



            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {
        super.tick();

        this.setTarget(targetSel());
        if (this.getTarget() != null) {
            this.rangedAttack();
        }
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("hordeSpawned", hordeSpawned);
        tag.putInt("attackCoolDown", attackCoolDown);
        tag.putInt("breakCoolDown", breakCoolDown);
        tag.putInt("groupSize", groupSize);
        tag.putInt("seeTime", seeTime);
        tag.putInt("strafingTime", strafingTime);
        tag.putInt("spawnProjectileTime", spawnProjectileTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("hordeSpawned")) {
            hordeSpawned = tag.getInt("hordeSpawned");
        }
        if (tag.contains("attackCoolDown")) {
            attackCoolDown = tag.getInt("attackCoolDown");
        }
        if (tag.contains("groupSize")) {
            groupSize = tag.getInt("groupSize");
        }
        if (tag.contains("attackCoolDown")) {
            attackCoolDown = tag.getInt("attackCoolDown");
        }
        if (tag.contains("seeTime")) {
            seeTime = tag.getInt("seeTime");
        }
        if (tag.contains("strafingTime")) {
            strafingTime = tag.getInt("strafingTime");
        }
        if (tag.contains("spawnProjectileTime")) {
            spawnProjectileTime = tag.getInt("spawnProjectileTime");
        }
    }

    public LivingEntity targetSel() {

        /*
        if (NightProgression.heart != null && MOTMUtils.vecDist(NightProgression.heart.position(), this.position()) <= 10) {
                return NightProgression.heart;
        }
         */

        if (this.getLastHurtByMob() != null && !(this.getLastHurtByMob() instanceof MoonMob)) {
            return this.getLastHurtByMob();
        }

        else if (this.level().getNearestPlayer(this, 50) != null) {
            return this.level().getNearestPlayer(this, 50);
        }

        // else if (NightProgression.heart != null) return NightProgression.heart;

        return null;
    }

    public void rangedAttack() {

        if (level().isClientSide) return;

        int attackRadius = 10;
        int attackRadiusSqr = attackRadius*attackRadius;
        this.lookAt(this.getTarget(), 30.0F, 30.0F);

        LivingEntity livingentity = this.getTarget();
        if (livingentity != null) {
            double d0 = this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            targetDistance = d0;
            boolean flag = true; // this.getSensing().hasLineOfSight(livingentity);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (!(d0 > (double)attackRadiusSqr) && this.seeTime >= 20) {
                this.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.getNavigation().moveTo(livingentity, 1);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double)this.getRandom().nextFloat() < 0.3) {
                    strafingClockwise = !strafingClockwise;
                }

                if ((double)this.getRandom().nextFloat() < 0.3) {
                    strafingBackwards = !strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d0 > (double)(attackRadiusSqr * 0.75F)) {
                    strafingBackwards = false;
                } else if (d0 < (double)(attackRadiusSqr * 0.25F)) {
                    strafingBackwards = true;
                }

                this.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
                Entity entity = this.getControlledVehicle();
                if (entity instanceof Mob) {
                    Mob mob = (Mob)entity;
                }

            } else {
                this.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            }


            if (--this.attackCoolDown <= 0 && this.seeTime >= -60) {

                //this.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this, (item) -> item instanceof BowItem));

                this.triggerAnim("sling", "sling");
                spawnProjectileTime = tickCount + 27;

                attackCoolDown = 100 + random.nextInt(-20, 20);

            }

            if (tickCount == spawnProjectileTime && !this.isDeadOrDying()) {

                if (level().isClientSide) return;

                MoonRockProjectile moonRock = new MoonRockProjectile(ModEntities.MOONROCKPROJECTILE.get(), level());
                moonRock.moveTo(this.getEyePosition());
                moonRock.shootFromRotation(this, this.getXRot(), this.getYRot(), 0.0F, 2.0F, 1.0F);
                level().addFreshEntity(moonRock);

                spawnProjectileTime = Integer.MAX_VALUE;
            }
        }
    }

    public static boolean slingerSpawnRules(EntityType<Slinger> entityType, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {

        if (pLevel.dayTime() < 13000) {
            return false;
        }

        switch (pLevel.getMoonPhase()) {
            case 0, 1, 2, 3, 4, 5, 6, 7: {
                return true;
            }
        }
        return false;

    }
}
