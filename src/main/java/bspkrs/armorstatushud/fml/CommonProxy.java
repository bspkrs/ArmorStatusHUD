package bspkrs.armorstatushud.fml;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {}

    public void init(FMLInitializationEvent event)
    {
        FMLLog.log(Reference.MODID, Level.ERROR, "***********************************************************************************");
        FMLLog.log(Reference.MODID, Level.ERROR, "* ArmorStatusHUD is a CLIENT-ONLY mod. Installing it on your server is pointless. *");
        FMLLog.log(Reference.MODID, Level.ERROR, "***********************************************************************************");
    }
}
