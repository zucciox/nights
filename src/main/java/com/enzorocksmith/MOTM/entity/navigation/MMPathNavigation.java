//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.enzorocksmith.MOTM.entity.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class MMPathNavigation extends PathNavigation {
    private boolean avoidSun;

    public MMPathNavigation(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }

    protected PathFinder createPathFinder(int pMaxVisitedNodes) {
        this.nodeEvaluator = new MMNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
    }

    protected boolean canUpdatePath() {
        return this.mob.onGround() || this.isInLiquid() || this.mob.isPassenger();
    }

    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), (double)this.getSurfaceY(), this.mob.getZ());
    }

    public Path createPath(BlockPos pPos, int pAccuracy) {
        if (this.level.getBlockState(pPos).isAir()) {
            BlockPos $$2;
            for($$2 = pPos.below(); $$2.getY() > this.level.getMinBuildHeight() && this.level.getBlockState($$2).isAir(); $$2 = $$2.below()) {
            }

            if ($$2.getY() > this.level.getMinBuildHeight()) {
                return super.createPath($$2.above(), pAccuracy);
            }

            while($$2.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState($$2).isAir()) {
                $$2 = $$2.above();
            }

            pPos = $$2;
        }

        if (!this.level.getBlockState(pPos).isSolid()) {
            return super.createPath(pPos, pAccuracy);
        } else {
            BlockPos $$3;
            for($$3 = pPos.above(); $$3.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState($$3).isSolid(); $$3 = $$3.above()) {
            }

            return super.createPath($$3, pAccuracy);
        }
    }

    public Path createPath(Entity pEntity, int pAccuracy) {
        return this.createPath(pEntity.blockPosition(), pAccuracy);
    }

    private int getSurfaceY() {
        if (this.mob.isInWater() && this.canFloat()) {
            int $$0 = this.mob.getBlockY();
            BlockState $$1 = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)$$0, this.mob.getZ()));
            int $$2 = 0;

            while($$1.is(Blocks.WATER)) {
                ++$$0;
                $$1 = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)$$0, this.mob.getZ()));
                ++$$2;
                if ($$2 > 16) {
                    return this.mob.getBlockY();
                }
            }

            return $$0;
        } else {
            return Mth.floor(this.mob.getY() + (double)0.5F);
        }
    }

    protected void trimPath() {
        super.trimPath();
        if (this.avoidSun) {
            if (this.level.canSeeSky(BlockPos.containing(this.mob.getX(), this.mob.getY() + (double)0.5F, this.mob.getZ()))) {
                return;
            }

            for(int $$0 = 0; $$0 < this.path.getNodeCount(); ++$$0) {
                Node $$1 = this.path.getNode($$0);
                if (this.level.canSeeSky(new BlockPos($$1.x, $$1.y, $$1.z))) {
                    this.path.truncateNodes($$0);
                    return;
                }
            }
        }

    }

    protected boolean hasValidPathType(BlockPathTypes pPathType) {
        if (pPathType == BlockPathTypes.WATER) {
            return false;
        } else if (pPathType == BlockPathTypes.LAVA) {
            return false;
        } else {
            return pPathType != BlockPathTypes.OPEN;
        }
    }

    public void setCanOpenDoors(boolean pCanOpenDoors) {
        this.nodeEvaluator.setCanOpenDoors(pCanOpenDoors);
    }

    public boolean canPassDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setCanPassDoors(boolean pCanPassDoors) {
        this.nodeEvaluator.setCanPassDoors(pCanPassDoors);
    }

    public boolean canOpenDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setAvoidSun(boolean pAvoidSun) {
        this.avoidSun = pAvoidSun;
    }

    public void setCanWalkOverFences(boolean pCanWalkOverFences) {
        this.nodeEvaluator.setCanWalkOverFences(pCanWalkOverFences);
    }
}
