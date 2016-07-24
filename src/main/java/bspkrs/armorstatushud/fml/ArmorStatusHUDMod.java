package bspkrs.armorstatushud.fml;

import bspkrs.util.Const;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = "@MOD_VERSION@", 
        dependencies = "required-after:bspkrsCore@[@BSCORE_VERSION@,)",
        useMetadata = true, guiFactory = Reference.GUI_FACTORY, 
        updateJSON = Const.VERSION_URL_BASE + Reference.MODID + Const.VERSION_URL_EXT,
        acceptedMinecraftVersions = "[@MIN_MC_VERSION@,@MAX_MC_VERSION@]")
public class ArmorStatusHUDMod
{
    @Metadata(value = Reference.MODID)
    public static ModMetadata       metadata;

    @Instance(value = Reference.MODID)
    public static ArmorStatusHUDMod instance;

    @SidedProxy(clientSide = Reference.PROXY_CLIENT, serverSide = Reference.PROXY_COMMON)
    public static CommonProxy       proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }
}
