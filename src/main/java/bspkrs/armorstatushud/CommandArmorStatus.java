package bspkrs.armorstatushud;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 1;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
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
}
