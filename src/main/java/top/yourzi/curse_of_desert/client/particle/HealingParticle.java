package top.yourzi.curse_of_desert.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HealingParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected HealingParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        
        this.sprites = spriteSet;
        this.lifetime = 30; // 粒子存在时间
        this.gravity = 0.0F; // 不受重力影响
        this.hasPhysics = false; // 不与方块碰撞
        
        // 设置粒子大小
        this.quadSize = 0.15F;
        
        // 设置初始透明度
        this.alpha = 1.0F;
        
        // 设置初始纹理
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            // 更新纹理动画
            this.setSpriteFromAge(this.sprites);
            
            // 粒子上升
            this.yd = 0.01D;
            
            // 禁用水平移动
            this.xd = 0.0D;
            this.zd = 0.0D;
            
            // 移动粒子
            this.move(0.0D, this.yd, 0.0D);
            
            // 随时间淡出
            if (this.age > this.lifetime / 2) {
                this.alpha = 0.8F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime;
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    // 粒子工厂，用于创建粒子实例
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HealingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}