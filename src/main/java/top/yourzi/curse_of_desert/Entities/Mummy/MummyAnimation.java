package top.yourzi.curse_of_desert.Entities.Mummy;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.yourzi.curse_of_desert.Interpolations.BezierInterpolations;

@OnlyIn(Dist.CLIENT)
public class MummyAnimation {

        public MummyAnimation() {
        }

        public static final AnimationDefinition walk = AnimationDefinition.Builder.withLength(0.96F).looping()
                .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.5F), BezierInterpolations.BEZIER),
                        new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F), BezierInterpolations.BEZIER),
                        new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.96F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.5F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("arm_right", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-45+0.53F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.08F, KeyframeAnimations.degreeVec(-45-0.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.24F, KeyframeAnimations.degreeVec(-45+4.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.44F, KeyframeAnimations.degreeVec(-45-7.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.76F, KeyframeAnimations.degreeVec(-45+7.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.96F, KeyframeAnimations.degreeVec(-45+0.53F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("arm_left", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-45+0.53F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.08F, KeyframeAnimations.degreeVec(-45-0.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.24F, KeyframeAnimations.degreeVec(-45+4.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.44F, KeyframeAnimations.degreeVec(-45-7.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.76F, KeyframeAnimations.degreeVec(-45+7.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.96F, KeyframeAnimations.degreeVec(-45+0.53F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("leg_right", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-17.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.24F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.48F, KeyframeAnimations.degreeVec(16.2F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.96F, KeyframeAnimations.degreeVec(-17.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("leg_right", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 2.0F, -1.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.24F, KeyframeAnimations.posVec(0.0F, 0.0F, -2.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.96F, KeyframeAnimations.posVec(0.0F, 2.0F, -1.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("leg_left", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(16.2F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.36F, KeyframeAnimations.degreeVec(-17.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.6F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.96F, KeyframeAnimations.degreeVec(16.2F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("leg_left", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.32F, KeyframeAnimations.posVec(0.0F, 2.0F, -1.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, 0.0F, -2.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.96F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("body_all", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(10.1483F, -9.9615F, 4.5383F), BezierInterpolations.BEZIER),
                        new Keyframe(0.4F, KeyframeAnimations.degreeVec(7.4718F, 7.6518F, -9.9574F), BezierInterpolations.BEZIER),
                        new Keyframe(0.68F, KeyframeAnimations.degreeVec(9.9627F, -4.1328F, -3.9244F), BezierInterpolations.BEZIER),
                        new Keyframe(0.96F, KeyframeAnimations.degreeVec(10.1483F, -9.9615F, 4.5383F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("body_all", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(1.0F, -1.1F, -1.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.24F, KeyframeAnimations.posVec(-1.0F, -0.6F, -2.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.48F, KeyframeAnimations.posVec(-2.0F, -1.3F, -2.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.72F, KeyframeAnimations.posVec(0.0F, -0.8F, -2.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.96F, KeyframeAnimations.posVec(1.0F, -1.1F, -1.0F), BezierInterpolations.BEZIER)
                ))
                .build();

        public static final AnimationDefinition idle = AnimationDefinition.Builder.withLength(3.84F).looping()
                .addAnimation("arm_right", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(35.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(2.08F, KeyframeAnimations.degreeVec(47.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(3.84F, KeyframeAnimations.degreeVec(35.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("arm_left", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(35.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(2.08F, KeyframeAnimations.degreeVec(47.5F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(3.84F, KeyframeAnimations.degreeVec(35.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("body_all", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(1.92F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(3.84F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("body_all", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -0.6F, -3.0F), BezierInterpolations.BEZIER),
                        new Keyframe(1.92F, KeyframeAnimations.posVec(0.0F, -1.0F, -3.0F), BezierInterpolations.BEZIER),
                        new Keyframe(3.84F, KeyframeAnimations.posVec(0.0F, -0.6F, -3.0F), BezierInterpolations.BEZIER)
                ))
                .build();

        public static final AnimationDefinition attack = AnimationDefinition.Builder.withLength(0.64F)
                .addAnimation("arm_right", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.12F, KeyframeAnimations.degreeVec(-89.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.32F, KeyframeAnimations.degreeVec(-16.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.48F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("arm_left", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.12F, KeyframeAnimations.degreeVec(-91.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.32F, KeyframeAnimations.degreeVec(-17.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.48F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("body_all", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.12F, KeyframeAnimations.degreeVec(-10.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.24F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.48F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .addAnimation("body_all", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.12F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), BezierInterpolations.BEZIER),
                        new Keyframe(0.24F, KeyframeAnimations.posVec(0.0F, 0.0F, -1.67F), BezierInterpolations.BEZIER),
                        new Keyframe(0.48F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), BezierInterpolations.BEZIER)
                ))
                .build();
}
