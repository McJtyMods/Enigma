package mcjty.enigma.apiimp;

import mcjty.enigma.api.IEnigmaScript;
import mcjty.enigma.progress.PlayerProgress;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class EnigmaScript implements IEnigmaScript {

    @Override
    public void setPlayerState(EntityPlayer player, String statename, String statevalue) {
        Progress progress = ProgressHolder.getProgress(player.getEntityWorld());
        PlayerProgress playerProgress = progress.getPlayerProgress(player.getPersistentID());
        playerProgress.setState(statename, statevalue);
        ProgressHolder.save(player.getEntityWorld());
    }

    @Override
    public String getPlayerState(EntityPlayer player, String statename) {
        Progress progress = ProgressHolder.getProgress(player.getEntityWorld());
        PlayerProgress playerProgress = progress.getPlayerProgress(player.getPersistentID());
        return STRINGS.get(playerProgress.getState(statename));
    }

    @Override
    public void setState(World world, String statename, String statevalue) {
        Progress progress = ProgressHolder.getProgress(world);
        progress.setState(statename, statevalue);
        ProgressHolder.save(world);
    }

    @Override
    public String getState(World world, String statename) {
        Progress progress = ProgressHolder.getProgress(world);
        return STRINGS.get(progress.getState(statename));
    }

}
