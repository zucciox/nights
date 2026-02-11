package com.enzorocksmith.MOTM.entity.custom;


import com.enzorocksmith.MOTM.blockMap.BlockBreakEntry;
import com.enzorocksmith.MOTM.blockMap.BlockBreakMap;
import com.enzorocksmith.MOTM.entity.navigation.FollowMMGoal;
import com.enzorocksmith.MOTM.entity.navigation.MMPathNavigation;
import com.enzorocksmith.MOTM.entity.navigation.MoonMobFollowTargetGoal;
import com.enzorocksmith.MOTM.utilities.MOTMUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CrescentKnight extends MoonMob implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public int hordeSpawned;
    public int attackCoolDown = 100;
    public int breakCoolDown;
    public int attackVariant;
    int triggerEffectTime;
    boolean isAttacking;
    Random random;

    // Triggerable Raw Animations

    private final RawAnimation slam = RawAnimation.begin().thenPlay("slam");
    private final RawAnimation punch = RawAnimation.begin().thenPlay("punch");
    private final RawAnimation sweep = RawAnimation.begin().thenPlay("sweep");
    private final RawAnimation death = RawAnimation.begin().thenPlay("death");
    private final RawAnimation hurt = RawAnimation.begin().thenPlay("hurt");

    public CrescentKnight(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        random = new Random();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MoonMobFollowTargetGoal(this, 1, true));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(3, new FollowMMGoal(this, 1, 5, 30));
    }

    protected PathNavigation createNavigation(Level world) {
        return new MMPathNavigation(this, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, .2)
                .add(Attributes.ATTACK_DAMAGE, 2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 5);
    }

    // Register animations controllers
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "hurt", state -> PlayState.STOP)
                .triggerableAnim("hurt", hurt));
        controllers.add(new AnimationController<>(this, "sweep", 5, state -> PlayState.STOP)
                .triggerableAnim("sweep", sweep));
        controllers.add(new AnimationController<>(this, "slam", 5, state -> PlayState.STOP)
                .triggerableAnim("slam", slam));
        controllers.add(new AnimationController<>(this, "punch", 5, state -> PlayState.STOP)
                .triggerableAnim("punch", punch));
        controllers.add(new AnimationController<>(this, "death", state -> PlayState.STOP)
                .triggerableAnim("death", death));
    }

    // loop or play animations based on the entity's state
    private PlayState predicate(AnimationState<CrescentKnight> animationState) {
        if (this.isDeadOrDying()) {
            triggerAnim("death", "death");
            return PlayState.CONTINUE;
        }
        if (this.getDeltaMovement() != Vec3.ZERO) {
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
            this.meleeAttack();
            this.tryBlockBreak();
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
            this.remove(CrescentKnight.RemovalReason.KILLED);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("hordeSpawned", hordeSpawned);
        tag.putInt("attackCoolDown", attackCoolDown);
        tag.putInt("breakCoolDown", breakCoolDown);
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
        if (tag.contains("breakCoolDown")) {
            attackCoolDown = tag.getInt("breakCoolDown");
        }
    }

    public LivingEntity targetSel() {

        /*
        if (NightProgression.heart != null && MOTMUtils.vecDist(NightProgression.heart.position(), this.position()) <= 10) {
                return NightProgression.heart;
        }
         */

        if (this.getLastHurtByMob() != null) {
            return this.getLastHurtByMob();
        }

        else if (this.level().getNearestPlayer(this, 50) != null) {
            return this.level().getNearestPlayer(this, 50);
        }

        // else if (NightProgression.heart != null) return NightProgression.heart;

        return null;
    }

    public void meleeAttack() {

        if (this.level().isClientSide()) return;

        //if (this.getNavigation().isStuck()) MOTMUtils.printToChat("is stuck");

        if ((MOTMUtils.vecDist(this.getTarget().position(), this.position()) <= 2.5) && attackCoolDown <= 0 && this.hasLineOfSight(this.getTarget())) {
            attackCoolDown = 60 + random.nextInt(-20, 20);
            isAttacking = true;

            attackVariant = random.nextInt(1, 4);
            int tickDelay = 0;

            switch (attackVariant) {
                case 1: // Slam
                    tickDelay = 28;
                    triggerAnim("slam", "slam");
                    break;
                case 2: // Sweep
                    triggerAnim("sweep", "sweep");
                    tickDelay = 22;
                    break;
                case 3: // Punch
                    triggerAnim("punch", "punch");
                    tickDelay = 18;
                    break;
            }
            triggerEffectTime = tickCount + tickDelay;
        }

        // Trigger Attack Effect
        if (!this.isDeadOrDying() && tickCount == triggerEffectTime && (MOTMUtils.vecDist(this.getTarget().position(), this.position()) <= 2.75) && this.hasLineOfSight(this.getTarget())) {
            this.getTarget().hurt(
                    new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MOB_ATTACK)),
                    10
            );

            switch (attackVariant) {
                case 2, 3:
                    this.getTarget().addDeltaMovement(this.getLookAngle().add(0, -getLookAngle().y + .5, 0));
                    break;
                case 1:
                    MobEffectInstance effect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 1, false, false, false);
                    this.getTarget().addEffect(effect);
                    break;
            }

            triggerEffectTime = Integer.MAX_VALUE;
            isAttacking = false;
        }
        
        attackCoolDown--;
    }

    public BlockPos[] getBlocksInFront() {
        Vec3 eyePos = this.getEyePosition(1).add(0, -1, 0);
        Vec3 viewVec = new Vec3(this.getViewVector(1).x, 0, this.getViewVector(1).z);
        Vec3 adjustedVec = eyePos.add(viewVec.x * 1, viewVec.y * 1, viewVec.z * 1);

        Double heightDiff = this.getTarget().position().y - this.position().y;
        BlockPos middle = this.level().clip(new ClipContext(eyePos, adjustedVec, ClipContext.Block.OUTLINE, net.minecraft.world.level.ClipContext.Fluid.NONE, this)).getBlockPos();
        BlockPos upper = BlockPos.containing(new Vec3(middle.getX(), middle.getY() + 1, middle.getZ()));
        BlockPos lower = BlockPos.containing(new Vec3(middle.getX(), middle.getY() - 1, middle.getZ()));

        if (heightDiff >= 1.5) {
            BlockPos head = BlockPos.containing(getEyePosition().x, getEyePosition().y+1, getEyePosition().z);
            BlockPos upperPlusOne = BlockPos.containing(new Vec3(middle.getX(), middle.getY() + 2, middle.getZ()));
            return new BlockPos[]{middle, upper, upperPlusOne, head};
        }

        if (heightDiff <= -1.5) {
            BlockPos feet = BlockPos.containing(this.position().x, this.position().y-1, this.position().z);
            BlockPos lowerMinusOne = BlockPos.containing(new Vec3(middle.getX(), middle.getY() - 2, middle.getZ()));
            return new BlockPos[]{middle, lower, upper, feet, lowerMinusOne};
        }

        return new BlockPos[]{middle, lower, upper};
    }

    //public BlockPos[] getBlocksInPath;

    public void tryBlockBreak() {

        if (level().isClientSide()) return;
        BlockPos[] blocks = getBlocksInFront();


        ServerLevel level = (ServerLevel) level();
        if (this.getNavigation().getPath() != null) {
        //Vec3 pos = this.moveControl.getWantedX();
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), 1, 0, 0, 0, 0);
}

        //MOTMUtils.printToChat("Break cooldown: " + breakCoolDown);


        if (breakCoolDown <= 0 && (this.level().getBlockState(blocks[0]).isSolid()) && !this.isAttacking) {

            this.triggerAnim("punch", "punch");
            triggerEffectTime = tickCount + 18;
            breakCoolDown = 100;
        }

        if (this.tickCount == triggerEffectTime) {

            for (BlockPos block : blocks) {

                float blockBlastRes = MOTMUtils.getBlastRes(level(), block, this);

                if (this.level().getBlockState(block).isSolid() && blockBlastRes <= 5) {

                    if (!BlockBreakMap.map.containsKey(block)) {
                        BlockState blockState = this.level().getBlockState(block);
                        List<BlockPos> dummy = new ArrayList<>();
                        //MOTMUtils.printToChat("block not in map; resistance is " + blockBlastRes);
                        BlockBreakMap.map.put(block, new BlockBreakEntry((float) Math.pow(blockBlastRes, 2f)));
                    }

                    if (BlockBreakMap.map.get(block).coolDown <= 0) {
                        BlockBreakEntry entry = BlockBreakMap.map.get(block);
                        entry.dura = entry.dura - (200);
                        if (entry.dura <= 0) {
                            level().destroyBlock(block, true);
                            this.level().playSound(null, block.getX(), block.getY(), block.getZ(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.AMBIENT, 0.7F, 1.0F);
                            //level().setBlock(block, Blocks.AIR.defaultBlockState(), 0);
                            BlockBreakMap.map.remove(block);
                        } else {
                            this.level().playSound(null, block.getX(), block.getY(), block.getZ(), SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.AMBIENT, 0.7F, 1.0F);
                            BlockBreakMap.map.get(block).coolDown=100;
                        }
                    }
                }
            }
        }

        breakCoolDown--;
    }


    public static boolean crescentKnightSpawnRules(EntityType<CrescentKnight> entityType, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {

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
