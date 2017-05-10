package mcjty.enigma.fxanim;

import io.netty.buffer.ByteBuf;
import mcjty.enigma.fxanim.animations.ColorAnimation;
import mcjty.enigma.fxanim.animations.MoveAnimation;
import mcjty.enigma.fxanim.animations.MoveBlockAnimation;
import mcjty.enigma.fxanim.animations.RotateAnimation;
import mcjty.enigma.network.EnigmaMessages;
import mcjty.enigma.network.PacketStartFxAnimation;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcjty.enigma.fxanim.animations.ColorAnimation.FXANIM_COLOR;
import static mcjty.enigma.fxanim.animations.MoveAnimation.FXANIM_MOVE;
import static mcjty.enigma.fxanim.animations.MoveBlockAnimation.FXANIM_MOVEBLOCK;
import static mcjty.enigma.fxanim.animations.RotateAnimation.FXANIM_ROTATE;

public class FxAnimationHandler {

    private static Map<String, FxAnimationFactory> animationFactories = new HashMap<>();
    private static List<FxAnimation> activeAnimations = new ArrayList<>();

    public static void tick() {
        List<FxAnimation> active = new ArrayList<>();
        for (FxAnimation animation : activeAnimations) {
            animation.tick();
            if (!animation.isDead()) {
                active.add(animation);
            }
        }
        activeAnimations = active;
    }

    public static void startAnimationClient(FxAnimation animation) {
        activeAnimations.add(animation);
    }

    public static void startAnimationServer(EntityPlayerMP player, FxAnimation animation) {
        EnigmaMessages.INSTANCE.sendTo(new PacketStartFxAnimation(animation), player);
    }

    public static FxAnimation createAnimation(String id, ByteBuf buf) {
        return animationFactories.get(id).create(buf);
    }

    public static void registerAnimationFactory(String id, FxAnimationFactory factory) {
        animationFactories.put(id, factory);
    }

    public static void init() {
        registerAnimationFactory(FXANIM_MOVE, MoveAnimation::new);
        registerAnimationFactory(FXANIM_ROTATE, RotateAnimation::new);
        registerAnimationFactory(FXANIM_COLOR, ColorAnimation::new);
        registerAnimationFactory(FXANIM_MOVEBLOCK, MoveBlockAnimation::new);
    }
}
