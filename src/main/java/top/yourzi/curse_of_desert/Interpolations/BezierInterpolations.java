package top.yourzi.curse_of_desert.Interpolations;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.Keyframe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;


@OnlyIn(Dist.CLIENT)
public class BezierInterpolations {
    public static final AnimationChannel.Interpolation BEZIER = (vec3f, progress, keyframes, currentIndex, nextIndex, weight) -> {
        // 获取相邻关键帧用于计算控制点
        Keyframe[] frames = keyframes;
        int lastSafeIndex = Math.max(0, currentIndex - 1);
        int nextSafeIndex = Math.min(frames.length - 1, nextIndex + 1);

        // 获取四个控制点
        Vector3f p0 = frames[lastSafeIndex].target();    // 前一个关键点
        Vector3f p1 = frames[currentIndex].target();     // 当前关键点
        Vector3f p2 = frames[nextIndex].target();        // 下一个关键点
        Vector3f p3 = frames[nextSafeIndex].target();    // 后一个关键点

        // 自动生成控制点（基于Catmull-Rom风格推导）
        Vector3f control1 = new Vector3f(p1).add(
                new Vector3f(p2).sub(p0).mul(0.25f)
        );
        Vector3f control2 = new Vector3f(p2).sub(
                new Vector3f(p3).sub(p1).mul(0.25f)
        );

        // 三次贝塞尔曲线计算公式
        float t = progress;
        float u = 1.0f - t;
        float uu = u * u;
        float uuu = uu * u;
        float tt = t * t;
        float ttt = tt * t;

        // 混合计算
        vec3f.set(
                uuu * p1.x() + 3 * uu * t * control1.x() + 3 * u * tt * control2.x() + ttt * p2.x(),
                uuu * p1.y() + 3 * uu * t * control1.y() + 3 * u * tt * control2.y() + ttt * p2.y(),
                uuu * p1.z() + 3 * uu * t * control1.z() + 3 * u * tt * control2.z() + ttt * p2.z()
        );

        // 应用权重缩放
        return vec3f.mul(weight);
    };
}
