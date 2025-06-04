package top.yourzi.curse_of_desert.Entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import top.yourzi.curse_of_desert.Entities.DogHeadedPriests.DogHeadedPriests;
import top.yourzi.curse_of_desert.Entities.DogHeadedWarrior.DogHeadedWarrior;

import java.util.EnumSet;
import java.util.List;
import java.util.Comparator;

public class DogHeadedPriestsAttackGoal extends Goal {
    private final DogHeadedPriests entity;
    private LivingEntity targetToHeal;
    private int healingTime = 0;
    private final int maxHealingTime = 45;
    private final double healRadius = 7.0D;
    private final float healAmount = 16.0F;

    public DogHeadedPriestsAttackGoal(DogHeadedPriests pMob) {
        this.entity = pMob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.entity.isHealing()) {
            return false;
        }
        this.targetToHeal = findTargetToHeal();
        return this.targetToHeal != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetToHeal != null && this.targetToHeal.isAlive() && this.healingTime < this.maxHealingTime && this.entity.isHealing();
    }

    @Override
    public void start() {
        this.healingTime = 0;
        this.entity.setHealing(true);
        this.entity.getNavigation().stop();
        if (this.targetToHeal != null) {
            this.entity.getLookControl().setLookAt(this.targetToHeal, 30.0F, 30.0F);
        }
    }

    @Override
    public void stop() {
        this.entity.setHealing(false);
        this.targetToHeal = null;
        this.healingTime = 0;
    }

    @Override
    public void tick() {
        if (this.targetToHeal == null || !this.targetToHeal.isAlive()) {
            this.stop();
            return;
        }

        this.entity.getLookControl().setLookAt(this.targetToHeal, 30.0F, 30.0F);
        this.healingTime++;

        if (this.healingTime >= this.maxHealingTime) {
            performHeal();
            this.stop();
        }
    }

    private LivingEntity findTargetToHeal() {
        List<LivingEntity> potentialTargets = this.entity.level().getEntitiesOfClass(LivingEntity.class,
                this.entity.getBoundingBox().inflate(this.healRadius),
                e -> (e instanceof DogHeadedWarrior || e instanceof DogHeadedPriests) && e.getHealth() < e.getMaxHealth() * 0.25F && e.isAlive());

        if (potentialTargets.isEmpty()) {
            return null;
        }

        potentialTargets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(this.entity)));
        LivingEntity closestTarget = potentialTargets.get(0);


        if (closestTarget.getY() - closestTarget.getBlockY() > 4.0D) {
            if (this.entity.getHealth() < this.entity.getMaxHealth() * 0.25F) {
                return this.entity;
            }
            return null;
        }
        return closestTarget;
    }

    private void performHeal() {
        if (this.targetToHeal != null && this.targetToHeal.isAlive()) {
            this.targetToHeal.heal(this.healAmount);
            if (this.entity.level() instanceof ServerLevel serverLevel) {
                double targetX = this.targetToHeal.getX();
                double targetY = this.targetToHeal.getY(0.8D);
                double targetZ = this.targetToHeal.getZ();
                serverLevel.sendParticles(ParticleTypes.ENCHANT, targetX, targetY, targetZ, 20, 0.5D, 0.7D, 0.5D, 0.05D);
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}