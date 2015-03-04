package bspkrs.armorstatushud.fml;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import bspkrs.bspkrscore.fml.bspkrsCoreMod;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ASHGameTicker
{
    private Minecraft      mcClient;
    private static boolean isRegistered = false;

    public ASHGameTicker()
    {
        mcClient = FMLClientHandler.instance().getClient();
        isRegistered = true;
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event)
    {
        if (event.phase.equals(Phase.START))
            return;

        boolean keepTicking = !(mcClient != null && mcClient.thePlayer != null && mcClient.theWorld != null);

        if (!keepTicking && isRegistered)
        {
            if (bspkrsCoreMod.instance.allowUpdateCheck && ArmorStatusHUDMod.instance.versionChecker != null)
                if (!ArmorStatusHUDMod.instance.versionChecker.isCurrentVersion())
                    for (String msg : ArmorStatusHUDMod.instance.versionChecker.getInGameMessage())
                        mcClient.thePlayer.addChatMessage(new ChatComponentText(msg));

            FMLCommonHandler.instance().bus().unregister(this);
            isRegistered = false;
        }
    }

    public static boolean isRegistered()
    {
        return isRegistered;
    }
}