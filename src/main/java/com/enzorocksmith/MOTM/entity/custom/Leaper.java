package com.enzorocksmith.MOTM.entity.custom;


import com.enzorocksmith.MOTM.blockMap.BlockBreakEntry;
import com.enzorocksmith.MOTM.blockMap.BlockBreakMap;
import com.enzorocksmith.MOTM.entity.navigation.FollowMMGoal;
import com.enzorocksmith.MOTM.entity.navigation.MMPathNavigation;
import com.enzorocksmith.MOTM.entity.navigation.MoonMobFollowTargetGoal;
import com.enzorocksmith.MOTM.utilities.MOTMUtils;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
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
import java.util.Random;

public class Leaper extends MoonMob implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public int hordeSpawned;
    public int attackCoolDown = 100;
    public int breakCoolDown;
    public int jumpCoolDown;
    public int triggerJumpTime;
    public int attackVariant;
    int triggerEffectTime;
    boolean isAttacking;
    boolean protectJump;
    Random random;

    // Triggerable Raw Animations

    private final RawAnimation attack = RawAnimation.begin().thenPlay("attack");
    private final RawAnimation jump = RawAnimation.begin().thenPlay("jump");
    private final RawAnimation death = RawAnimation.begin().thenPlay("death");
    private final RawAnimation hurt = RawAnimation.begin().thenPlay("hurt");

    public Leaper(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        random = new Random();
        //this.setMaxUpStep(10);
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
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, .35)
                .add(Attributes.ATTACK_DAMAGE, 2);
                //.add(Attributes.JUMP_STRENGTH, 2);

    }

    // Register animations controllers
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "hurt", state -> PlayState.STOP)
                .triggerableAnim("hurt", hurt));
        controllers.add(new AnimationController<>(this, "death", state -> PlayState.STOP)
                .triggerableAnim("death", death));
        controllers.add(new AnimationController<>(this, "attack", state -> PlayState.STOP)
                .triggerableAnim("attack", attack));
        controllers.add(new AnimationController<>(this, "jump", state -> PlayState.STOP)
                .triggerableAnim("jump", jump));
    }

    // loop or play animations based on the entity's state
    private PlayState predicate(AnimationState<Leaper> animationState) {
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

        if (!this.level().isClientSide) {
            ServerLevel level = (ServerLevel) level();
            if (this.getNavigation().getPath() != null)  level.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), 1, 0, 0, 0, 0);

        }

        this.setTarget(targetSel());
        if (this.getTarget() != null) {
            this.meleeAttack();
            this.tryJump();
            //this.tryBlockBreak();
            this.checkCollide();
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
            this.remove(Leaper.RemovalReason.KILLED);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("hordeSpawned", hordeSpawned);
        tag.putInt("attackCoolDown", attackCoolDown);
        tag.putInt("breakCoolDown", breakCoolDown);
        tag.putInt("jumpCoolDown", jumpCoolDown);
        tag.putInt("triggerJumpTime", triggerJumpTime);
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
        if (tag.contains("jumpCoolDown")) {
            jumpCoolDown = tag.getInt("jumpCoolDown");
        }
        if (tag.contains("triggerJumpTime")) {
            triggerJumpTime = tag.getInt("triggerJumpTime");
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

        if ((MOTMUtils.vecDist(this.getTarget().position(), this.position()) <= 10) && attackCoolDown <= 0 && this.hasLineOfSight(this.getTarget())) {

            this.triggerAnim("attack", "attack");

            attackCoolDown = 60 + random.nextInt(-20, 20);

            //int tickDelay = 0;

            triggerEffectTime = tickCount + 5;
        }

        // Trigger Attack Effect
        if (!this.isDeadOrDying() && tickCount == triggerEffectTime && /*(MOTMUtils.vecDist(this.getTarget().position(), this.position()) <= 2.75) &&  */this.hasLineOfSight(this.getTarget())) {

            Vec3 launchAngle = this.getTarget().getEyePosition().subtract(this.position()).normalize().multiply(1.5f, 0, 1.5f).add(0, distanceTo(getTarget())/10, 0);
            this.addDeltaMovement(launchAngle);
            this.protectJump = true;
            this.isAttacking = true;

            triggerEffectTime = Integer.MAX_VALUE;
            //isAttacking = false;
        }

        attackCoolDown--;
    }

    public void checkCollide(){

        if (this.onGround() && !this.protectJump) {
            if (this.isAttacking) {
                this.isAttacking = false;
                this.stopTriggeredAnimation("attack", "attack");
            }
        }

        if (this.isAttacking) protectJump = false;

        if (this.level().isClientSide()) return;

        List<Entity> collisions = this.level().getEntities(this, this.getBoundingBox().inflate(0.5D));
        for (Entity other : collisions) {
            if (other != this && this.getBoundingBox().intersects(other.getBoundingBox()) && other == this.getTarget() && this.isAttacking) {
                MOTMUtils.printToChat("collided with target");

                this.stopTriggeredAnimation("attack", "attack");

                this.getTarget().hurt(
                        new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MOB_ATTACK)),
                        5
                );

                this.getTarget().addDeltaMovement(this.getTarget().getEyePosition().subtract(this.position()).normalize().multiply(1, 0, 1).add(0, .5, 0));

                this.isAttacking = false;
            }
        }

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


    public static boolean leaperSpawnRules(EntityType<Leaper> entityType, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {

        if (pLevel.dayTime() < 13000) {
            return false;
        }

        switch (pLevel.getMoonPhase()) {
            case 0, 1, 2, 3, 5, 6, 7: {
                return true;
            }
        }
        return false;

    }

    public void tryJump(){

        double heightDiff = this.moveControl.getWantedY() - this.getY();

        if (heightDiff >= 2 && heightDiff < 11 && this.jumpCoolDown <= 0 && !this.isAttacking) {
            this.triggerAnim("jump", "jump");
            triggerJumpTime = this.tickCount + 5;
            MOTMUtils.printToChat("Tried jump");
        }

        if (this.triggerJumpTime == this.tickCount) {
            this.addDeltaMovement(new Vec3(0, heightDiff, 0));
            jumpCoolDown = 100;
        }

        this.jumpCoolDown--;
    }

}
