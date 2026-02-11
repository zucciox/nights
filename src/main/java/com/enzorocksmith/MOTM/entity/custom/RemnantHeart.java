package com.enzorocksmith.MOTM.entity.custom;


import com.enzorocksmith.MOTM.nightprogression.NightProgression;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RemnantHeart extends Mob implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public int hordeSpawned;
    //public static final EntityDataAccessor<Integer> hordeSpawned = SynchedEntityData.defineId(RemnantHeart.class, EntityDataSerializers.INT);

    // Triggerable Raw Animations

    /*
    private final RawAnimation unarmedstrike = RawAnimation.begin().thenPlay("strike");
    private final RawAnimation shoot = RawAnimation.begin().thenPlay("shoot");
    private final RawAnimation death = RawAnimation.begin().thenPlay("death");
     */

    public RemnantHeart(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        NightProgression.heartLoc = this.position();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D);
    }

    // Register animations controllers
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    // loop or play animations based on the entity's state
    private PlayState predicate(AnimationState<RemnantHeart> animationState) {
        if (this.isDeadOrDying()) {
            // triggerAnim("deathctrl", "death");
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation (RawAnimation.begin().then("idle", Animation. LoopType.LOOP));
        return PlayState.CONTINUE;
    }


    @Override
    public void move(MoverType pType, Vec3 pPos) {
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemnantHeart.RemovalReason.KILLED);
            NightProgression.heart = null;
            NightProgression.spawnRing.clear();
        }
    }

    /*
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(hordeSpawned, 0);
    }
    */

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("hordeSpawned", hordeSpawned);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("hordeSpawned")) {
            hordeSpawned = tag.getInt("hordeSpawned");
        }
    }
}
