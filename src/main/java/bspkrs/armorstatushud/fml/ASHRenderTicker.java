package bspkrs.armorstatushud.fml;

import bspkrs.armorstatushud.ArmorStatusHUD;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ASHRenderTicker
{
    private Minecraft      mcClient;
    private static boolean isRegistered = false;

    public ASHRenderTicker()
    {
        mcClient = FMLClientHandler.instance().getClient();
        isRegistered = true;
    }

    @SubscribeEvent
    public void onTick(RenderTickEvent event)
    {
        if (event.phase.equals(Phase.START))
            return;

        if (!ArmorStatusHUD.onTickInGame(mcClient))
        {
            MinecraftForge.EVENT_BUS.unregister(this);
            isRegistered = false;
        }
    }

    public static boolean isRegistered()
    {
        return isRegistered;
    }
}
