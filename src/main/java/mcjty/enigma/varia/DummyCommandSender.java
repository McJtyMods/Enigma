package mcjty.enigma.varia;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DummyCommandSender implements ICommandSender {

    private final World world;
    private final EntityPlayer player;

    public DummyCommandSender(World world, EntityPlayer player) {
        this.world = world;
        this.player = player;
    }

    @Override
    public String getName() {
        return "dummy";
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString("dummy");
    }

    @Override
    public void sendMessage(ITextComponent component) {
        System.out.println(component.getFormattedText());
    }

    @Override
    public boolean canUseCommand(int permLevel, String commandName) {
        return true;
    }

    @Override
    public BlockPos getPosition() {
        return new BlockPos(0, 0, 0);
    }

    @Override
    public Vec3d getPositionVector() {
        return new Vec3d(0, 0, 0);
    }

    @Override
    public World getEntityWorld() {
        return world;
    }

    @Nullable
    @Override
    public Entity getCommandSenderEntity() {
        return player;
    }

    @Override
    public boolean sendCommandFeedback() {
        return false;
    }

    @Override
    public void setCommandStat(CommandResultStats.Type type, int amount) {

    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return world.getMinecraftServer();
    }
}
