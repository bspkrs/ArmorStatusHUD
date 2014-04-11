package bspkrs.armorstatushud;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import bspkrs.fml.util.DelayedGuiDisplayTicker;
import bspkrs.util.config.ConfigCategory;
import bspkrs.util.config.ConfigProperty;
import bspkrs.util.config.Configuration;
import bspkrs.util.config.gui.IConfigProperty;

public class CommandArmorStatus extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "armorstatus";
    }
    
    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "commands.armorstatus.usage";
    }
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return true;
    }
    
    @Override
    public int getRequiredPermissionLevel()
    {
        return 1;
    }
    
    @Override
    public void processCommand(ICommandSender var1, String[] var2)
    {
        ConfigCategory cc = ArmorStatusHUD.getConfig().getCategory(Configuration.CATEGORY_GENERAL);
        IConfigProperty[] props = new IConfigProperty[ConfigElement.values().length];
        for (int i = 0; i < ConfigElement.values().length; i++)
            props[i] = new ConfigProperty(cc.get(ConfigElement.values()[i].key()));
        
        try
        {
            new DelayedGuiDisplayTicker(10, new bspkrs.util.config.gui.GuiConfig(null, props, Configuration.class.getDeclaredMethod("save"), ArmorStatusHUD.getConfig(),
                    ArmorStatusHUD.class.getDeclaredMethod("syncConfig"), null));
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public int compareTo(Object object)
    {
        if (object instanceof CommandBase)
            return this.getCommandName().compareTo(((CommandBase) object).getCommandName());
        
        return 0;
    }
}
