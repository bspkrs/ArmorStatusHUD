package bspkrs.armorstatushud;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import bspkrs.armorstatushud.fml.gui.GuiASHConfig;
import bspkrs.fml.util.DelayedGuiDisplayTicker;

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
        try
        {
            new DelayedGuiDisplayTicker(10, new GuiASHConfig(null));
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
