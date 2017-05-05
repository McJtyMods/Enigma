package mcjty.enigma.fxanim;

import io.netty.buffer.ByteBuf;

public interface FxAnimation {

    String getID();

    boolean isDead();

    void tick();

    void writeToBuf(ByteBuf buf);
}
