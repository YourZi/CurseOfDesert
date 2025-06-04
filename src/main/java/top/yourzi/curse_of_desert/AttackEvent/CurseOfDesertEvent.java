package top.yourzi.curse_of_desert.AttackEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import top.yourzi.curse_of_desert.init.ModEffect;
import top.yourzi.curse_of_desert.init.ModEntities;
import top.yourzi.curse_of_desert.init.ModTags;

import java.util.ArrayList;
import java.util.List;


public class CurseOfDesertEvent {
    private final ServerLevel level;
    private final BlockPos center;
    private final List<ServerPlayer> players;
    private final ServerBossEvent bossEvent;
    
    private int currentWave;
    private final int totalWaves;
    private boolean isActive;
    private float maxHealthRecord;
    private boolean isFinishing;
    private boolean finishSuccess;
    private boolean delayFinishing;
    
    private boolean isInitializing;
    private long initStartTime;
    private long finishStartTime;
    private static final long INIT_DURATION = 7000;
    
    private long delayStartTime;
    private static final long FINISH_DELAY = 1700;
    private static final long ENTITY_SPAWN_DELAY = 100;
    private int entitySpawnCounter = 0;
    private boolean waitingForEntitySpawn = false;
    
    private static final int EVENT_RADIUS = 50;
    private static final int SPAWN_MIN_DISTANCE = 2;
    private static final int SPAWN_MAX_DISTANCE = 10;
    private static final int SPAWN_HEIGHT_SEARCH_RANGE = 30;
    

    public CurseOfDesertEvent(ServerLevel level, BlockPos center) {
        this.level = level;
        this.center = center;
        this.players = new ArrayList<>();
        this.bossEvent = new ServerBossEvent(
            Component.translatable("event.curse_of_desert.name"),
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarOverlay.PROGRESS
        );
        this.currentWave = 0;
        this.totalWaves = 7;
        this.isActive = true;
        this.maxHealthRecord = 0;
        this.isInitializing = true;
        this.initStartTime = System.currentTimeMillis();

        AABB area = new AABB(center).inflate(50);
        List<LivingEntity> existingEntities = level.getEntitiesOfClass(LivingEntity.class, area,
            entity -> entity.getType().is(ModTags.CURSE_OF_DESERT));

        if (!existingEntities.isEmpty()) {
            this.currentWave = 1;
            spawnWaveEntities();
            for (LivingEntity entity : existingEntities) {
                if (entity.isAlive()) {
                    this.maxHealthRecord += entity.getMaxHealth();
                }
            }
        }
    }

    private int tickCounter = 0;
    private static final int TICK_INTERVAL = 2;


    public void tick() {
        if (!isActive) {
            bossEvent.setVisible(false);
            return;
        };
        bossEvent.setVisible(true);
    
        if (++tickCounter >= 2) {
            tickCounter = 0;
            updatePlayers();
    
            if (isInitializing) {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - initStartTime;
                
                if (elapsedTime >= INIT_DURATION) {
                    isInitializing = false;
                    bossEvent.setProgress(1.0F);
                } else {
                    float progress = (float) elapsedTime / INIT_DURATION;
                    bossEvent.setProgress(progress);
                    return;
                }
            }
    
            if (isActive && currentWave <= 0){
                startNextWave();
                // 刚开始第一波时，不要立即检查是否清理完毕
                // 因为实体还在生成队列中，尚未真正生成
            } else if (isCurrentWaveCleared() && entitySpawnQueue.isEmpty()) {
                // 只有当实体生成队列为空（所有实体都已生成）且当前波次已清理完毕时，才进入下一波
                if (currentWave < totalWaves) {
                    float currentProgress = bossEvent.getProgress();
                    if (currentProgress < 1.0F) {
                        bossEvent.setProgress(Math.min(1.0F, currentProgress + 0.030F));
                    } else {
                        startNextWave();
                    }
                } else {
                    bossEvent.setProgress(0);
                    if (!delayFinishing){
                        finishSuccess = true;
                        delayFinishing = true;
                        delayStartTime = System.currentTimeMillis();
                    } else {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - delayStartTime >= FINISH_DELAY) {
                            finish(true);
                        }
                    }
                }
            }
            if (!isCurrentWaveCleared()){
                updateBossBar();
            }
            
            processEntitySpawnQueue();
        }
    }
    
    /**
     * 处理实体生成队列，实现延迟生成
     */
    private void processEntitySpawnQueue() {
        if (!entitySpawnQueue.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEntitySpawnTime >= ENTITY_SPAWN_DELAY) {
                EntitySpawnTask task = entitySpawnQueue.poll();
                if (task != null) {
                    spawnEntityWithEffects(task.entityType, task.spawnPos);
                    lastEntitySpawnTime = currentTime;
                }
            }
        }
    }

    /**
     * 更新Boss栏进度
     * 根据当前存活实体的生命值计算并更新Boss栏进度
     */
    private void updateBossBar() {
        AABB area = new AABB(center).inflate(50);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
            entity -> entity.getType().is(ModTags.CURSE_OF_DESERT));

        float maxHealthSum = 0;
        float currentHealthSum = 0;
        List<LivingEntity> aliveEntities = new ArrayList<>();

        for (LivingEntity entity : entities) {
            if(entity.isAlive()){
                maxHealthSum += entity.getMaxHealth();
                currentHealthSum += entity.getHealth();
                aliveEntities.add(entity);
            }
        }
        
        if (maxHealthSum > maxHealthRecord) {
            maxHealthRecord = maxHealthSum;
        }
        
        if (maxHealthRecord > 0) {
            bossEvent.setProgress(currentHealthSum / maxHealthRecord);
        } else {
            bossEvent.setProgress(0);
        }
    }

    /**
     * 更新玩家列表并处理玩家状态
     * 检查玩家存活状态，更新Boss栏显示，应用法老凝视效果
     */
    private void updatePlayers() {
        AABB box = new AABB(center).inflate(50);
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, box);

        if (nearbyPlayers.isEmpty()) {
            finish(false);
            return;
        }

        for (ServerPlayer player : nearbyPlayers) {
            if (!player.isAlive()) {
                if (!delayFinishing){
                    finishSuccess = false;
                    delayFinishing = true;
                    delayStartTime = System.currentTimeMillis();
                } else {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - delayStartTime >= FINISH_DELAY) {
                        finish(false);
                    }
                }
                return;
            }
        }

        for (ServerPlayer player : new ArrayList<>(players)) {
            if (!nearbyPlayers.contains(player)) {
                bossEvent.removePlayer(player);
                players.remove(player);
            }
        }

        for (ServerPlayer player : nearbyPlayers) {
            if (!players.contains(player)) {
                bossEvent.addPlayer(player);
                players.add(player);
            }
        }

        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, box);
        int effectLevel = level.getDifficulty() == Difficulty.HARD ? 1 : 0;

        for (LivingEntity entity : nearbyEntities) {
            if (entity.getType().is(ModTags.CURSE_OF_DESERT)) {
                continue;
            }

            if (isActive) {
                entity.addEffect(new MobEffectInstance(ModEffect.PHARAOH_GAZE.get(), 20, effectLevel, false, false));
            }
        }
    }

    /**
     * 结束事件
     * @param success 事件是否成功完成
     */
    public void finish(boolean success) {
        isFinishing = true;
        finishSuccess = success;
        finishStartTime = System.currentTimeMillis();
        isActive = false;

        // 移除所有玩家的Boss栏显示
        for (ServerPlayer player : new ArrayList<>(players)) {
            bossEvent.removePlayer(player);
        }
        players.clear();
        maxHealthRecord = 0;
    }

    /**
     * 检查当前波次是否已清理完毕
     * @return 如果当前波次的所有实体都已被击败则返回true
     */
    private boolean isCurrentWaveCleared() {
        // 获取事件区域内所有事件生物
        AABB area = new AABB(center).inflate(50);
        List<LivingEntity> allEntities = level.getEntitiesOfClass(LivingEntity.class, area,
            entity -> entity.getType().is(ModTags.CURSE_OF_DESERT));
        
        // 检查所有事件生物是否死亡
        return allEntities.stream().noneMatch(LivingEntity::isAlive);
    }

    /**
     * 开始下一波次
     * 重置相关状态并生成新的实体
     */
    private void startNextWave() {
        currentWave++;
        maxHealthRecord = 0;
        spawnWaveEntities();
    }

    private java.util.Queue<EntitySpawnTask> entitySpawnQueue = new java.util.LinkedList<>();
    private long lastEntitySpawnTime = 0;
    
    private static class EntitySpawnTask {
        final EntityType<?> entityType;
        final BlockPos spawnPos;
        
        EntitySpawnTask(EntityType<?> entityType, BlockPos spawnPos) {
            this.entityType = entityType;
            this.spawnPos = spawnPos;
        }
    }
    
    /**
     * 生成指定类型和数量的实体
     * @param entityType 实体类型
     * @param number 生成数量
     */
    private void spawnEntities(EntityType<?> entityType, int number) {
        for (int i = 0; i < number; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = SPAWN_MIN_DISTANCE + level.random.nextDouble() * (SPAWN_MAX_DISTANCE - SPAWN_MIN_DISTANCE);
            double x = center.getX() + Math.cos(angle) * distance;
            double z = center.getZ() + Math.sin(angle) * distance;
            
            BlockPos spawnPos = findValidSpawnPosition(new BlockPos((int)x, center.getY(), (int)z));
            if (spawnPos != null) {
                entitySpawnQueue.offer(new EntitySpawnTask(entityType, spawnPos));
            }
        }
    }

    /**
     * 查找有效的生成位置
     * @param startPos 起始搜索位置
     * @return 有效的生成位置，如果未找到则返回null
     */
    private BlockPos findValidSpawnPosition(BlockPos pos) {
        BlockPos playerPos = pos;
        for (int i = 0; i < 50; i++) { // 尝试50次寻找位置
            int x = playerPos.getX() + level.random.nextInt(SPAWN_MAX_DISTANCE * 2) - SPAWN_MAX_DISTANCE;
            int z = playerPos.getZ() + level.random.nextInt(SPAWN_MAX_DISTANCE * 2) - SPAWN_MAX_DISTANCE;

            // 确保在最小生成距离之外
            if (Math.sqrt(playerPos.distSqr(new BlockPos(x, playerPos.getY(), z))) < SPAWN_MIN_DISTANCE) {
                continue;
            }

            for (int yOffset = SPAWN_HEIGHT_SEARCH_RANGE; yOffset >= -SPAWN_HEIGHT_SEARCH_RANGE; yOffset--) {
                BlockPos potentialPos = new BlockPos(x, playerPos.getY() + yOffset, z);
                if (level.getBlockState(potentialPos.below()).isSolidRender(level, potentialPos.below()) &&
                    level.isEmptyBlock(potentialPos) &&
                    level.isEmptyBlock(potentialPos.above())) {
                    return potentialPos;
                }
            }
        }
        // 如果在多次尝试后仍未找到合适的位置，则在玩家附近强制生成
        BlockPos forcedPos = playerPos.above(2);
        if (level.isEmptyBlock(forcedPos) && level.isEmptyBlock(forcedPos.above())){
            return forcedPos;
        } else {
            // 如果上方两格也不行，尝试在玩家周围随机一个近距离位置，忽略一些碰撞检测，确保生成
            for (int i = 0; i < 10; i++) { // 尝试10次在近处生成
                int forcedX = playerPos.getX() + level.random.nextInt(5) - 2; // -2 to +2 offset
                int forcedZ = playerPos.getZ() + level.random.nextInt(5) - 2; // -2 to +2 offset
                BlockPos nearPlayerPos = new BlockPos(forcedX, playerPos.getY(), forcedZ);
                // 简化检测，只要脚下是固体，头顶是空气即可
                if (level.getBlockState(nearPlayerPos.below()).isSolidRender(level, nearPlayerPos.below()) && level.isEmptyBlock(nearPlayerPos.above())) {
                    return nearPlayerPos;
                }
            }
            return playerPos; 
        }
    }

    /**
     * 检查位置是否适合生成实体
     * @param pos 待检查的位置
     * @return 如果位置有效则返回true
     */
    private boolean isValidSpawnPosition(BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP) &&
               level.getBlockState(pos).isAir() &&
               level.getBlockState(pos.above()).isAir();
    }

    /**
     * 在指定位置生成实体并添加特效
     * @param entityType 实体类型
     * @param pos 生成位置
     */
    private void spawnEntityWithEffects(EntityType<?> entityType, BlockPos pos) {
        Entity entity = entityType.create(level);
        if (entity != null) {
            entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            level.addFreshEntity(entity);
            spawnSandParticles(pos);
        }
    }

    /**
     * 生成沙暴粒子效果
     * @param pos 粒子生成位置
     */
    private void spawnSandParticles(BlockPos pos) {
        for (int i = 0; i < 30; i++) {
            double horizontalDirection = level.random.nextBoolean() ? 1 : -1;
            boolean moveEastWest = level.random.nextBoolean();
            
            double velocityY = (level.random.nextDouble() - 0.5) * 0.002;
            double horizontalSpeed = 1.5 + level.random.nextDouble() * 150;
            double velocityX, velocityZ;
            
            if (moveEastWest) {
                velocityX = horizontalDirection * horizontalSpeed;
                velocityZ = (level.random.nextDouble() - 0.5) * 0.5;
            } else {
                velocityX = (level.random.nextDouble() - 0.5) * 0.5;
                velocityZ = horizontalDirection * horizontalSpeed;
            }
            
            level.sendParticles(
                new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SANDSTONE.defaultBlockState()),
                pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 2,
                pos.getY() + 0.5 + level.random.nextDouble() * 3,
                pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 2,
                1,
                velocityX,
                velocityY,
                velocityZ,
                0.1
            );
        }
        
        for (int i = 0; i < 15; i++) {
            double horizontalDirection = level.random.nextBoolean() ? 1 : -1;
            double velocityX = horizontalDirection * (2.0 + level.random.nextDouble() * 2.0);
            double velocityY = (level.random.nextDouble() - 0.5) * 0.15;
            double velocityZ = (level.random.nextDouble() - 0.5) * 0.8;
            
            level.sendParticles(
                new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState()),
                pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 3,
                pos.getY() + 0.5 + level.random.nextDouble() * 4,
                pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 3,
                1,
                velocityX,
                velocityY,
                velocityZ,
                0.05
            );
        }
    }

    public boolean isActive() { return isActive; }
    public boolean isFinishing() { return isFinishing; }
    public boolean delayFinishing() { return delayFinishing; }
    public boolean getFinishSuccess() { return finishSuccess; }
    public int getCurrentWave() { return currentWave; }
    public BlockPos getCenter() { return center; }
    public int getTotalWaves() { return totalWaves; }

    private void spawnWaveEntities() {
        switch (currentWave) {
            case 1:
                spawnEntities(ModEntities.MUMMY.get(), 4);
                spawnEntities(ModEntities.SCARAB_BEETLE.get(), 3);
                break;
            case 2:
                spawnEntities(ModEntities.MUMMY.get(), 3);
                break;
            case 3:
                spawnEntities(ModEntities.MUMMY.get(), 1);
                break;
            case 4:
                spawnEntities(ModEntities.MUMMY.get(), 1);
                break;
            case 5:
                spawnEntities(ModEntities.MUMMY.get(), 1);
                break;
            case 6:
                spawnEntities(ModEntities.MUMMY.get(), 1);
                break;
            case 7:
                spawnEntities(ModEntities.MUMMY.get(), 1);
                break;
        }

    }
}