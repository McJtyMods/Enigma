package mcjty.enigma.fxanim;

import io.netty.buffer.ByteBuf;

public interface FxAnimationFactory {

    FxAnimation create(ByteBuf buf);

}
