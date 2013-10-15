package bspkrs.armorstatushud.fml;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import bspkrs.bspkrscore.fml.bspkrsCoreMod;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ASHGameTicker implements ITickHandler
{
    private EnumSet<TickType> tickTypes        = EnumSet.noneOf(TickType.class);
    private Minecraft         mc;
    private boolean           allowUpdateCheck = bspkrsCoreMod.instance.allowUpdateCheck;
    
    public ASHGameTicker(EnumSet<TickType> tickTypes)
    {
        this.tickTypes = tickTypes;
        mc = Minecraft.getMinecraft();
    }
    
    @Override
    public void tickStart(EnumSet<TickType> tickTypes, Object... tickData)
    {
        tick(tickTypes, true);
    }
    
    @Override
    public void tickEnd(EnumSet<TickType> tickTypes, Object... tickData)
    {
        tick(tickTypes, false);
    }
    
    private void tick(EnumSet<TickType> tickTypes, boolean isStart)
    {
        for (TickType tickType : tickTypes)
        {
            if (!onTick(tickType, isStart))
            {
                this.tickTypes.remove(tickType);
                this.tickTypes.removeAll(tickType.partnerTicks());
            }
        }
    }
    
    public boolean onTick(TickType tick, boolean isStart)
    {
        if (isStart)
        {
            return true;
        }
        
        if (allowUpdateCheck && mc != null && mc.thePlayer != null)
        {
            if (ArmorStatusHUDMod.versionChecker != null)
                if (!ArmorStatusHUDMod.versionChecker.isCurrentVersion())
                    for (String msg : ArmorStatusHUDMod.versionChecker.getInGameMessage())
                        mc.thePlayer.addChatMessage(msg);
            
            return false;
        }
        
        return allowUpdateCheck;
    }
    
    @Override
    public EnumSet<TickType> ticks()
    {
        return tickTypes;
    }
    
    @Override
    public String getLabel()
    {
        return "ASHGameTicker";
    }
    
}
