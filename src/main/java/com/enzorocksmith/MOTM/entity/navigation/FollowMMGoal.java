//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.enzorocksmith.MOTM.entity.navigation;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import com.enzorocksmith.MOTM.entity.custom.MoonMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class FollowMMGoal extends Goal {
    private final Mob mob;
    private final Predicate<Mob> followPredicate;
    @Nullable
    private Mob followingMob;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private float oldWaterCost;
    private final float areaSize;

    public FollowMMGoal(Mob pMob, double pSpeedModifier, float pStopDistance, float pAreaSize) {
        this.mob = pMob;
        this.followPredicate = (p_25278_) -> p_25278_ != null; //&& pMob.getClass() != p_25278_.getClass();
        this.speedModifier = pSpeedModifier;
        this.navigation = pMob.getNavigation();
        this.stopDistance = pStopDistance;
        this.areaSize = pAreaSize;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));

        /*
        if (!(pMob.getNavigation() instanceof GroundPathNavigation) && !(pMob.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
         */
    }

    public boolean canUse() {
        List<MoonMob> $$0 = this.mob.level().getEntitiesOfClass(MoonMob.class, this.mob.getBoundingBox().inflate((double)this.areaSize), this.followPredicate);
        if (!$$0.isEmpty()) {
            for(MoonMob $$1 : $$0) {
                if (!$$1.isInvisible()) {
                    this.followingMob = $$1;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean canContinueToUse() {
        return this.followingMob != null && !this.navigation.isDone() && this.mob.distanceToSqr(this.followingMob) > (double)(this.stopDistance * this.stopDistance);
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.followingMob = null;
        this.navigation.stop();
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        if (this.followingMob != null && !this.mob.isLeashed()) {
            this.mob.getLookControl().setLookAt(this.followingMob, 10.0F, (float)this.mob.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                double $$0 = this.mob.getX() - this.followingMob.getX();
                double $$1 = this.mob.getY() - this.followingMob.getY();
                double $$2 = this.mob.getZ() - this.followingMob.getZ();
                double $$3 = $$0 * $$0 + $$1 * $$1 + $$2 * $$2;
                if (!($$3 <= (double)(this.stopDistance * this.stopDistance))) {
                    this.navigation.moveTo(this.followingMob, this.speedModifier);
                } else {
                    this.navigation.stop();
                    LookControl $$4 = this.followingMob.getLookControl();
                    if ($$3 <= (double)this.stopDistance || $$4.getWantedX() == this.mob.getX() && $$4.getWantedY() == this.mob.getY() && $$4.getWantedZ() == this.mob.getZ()) {
                        double $$5 = this.followingMob.getX() - this.mob.getX();
                        double $$6 = this.followingMob.getZ() - this.mob.getZ();
                        this.navigation.moveTo(this.mob.getX() - $$5, this.mob.getY(), this.mob.getZ() - $$6, this.speedModifier);
                    }

                }
            }
        }
    }
}
