package bspkrs.armorstatushud.fml.gui;

import net.minecraft.client.gui.GuiScreen;
import bspkrs.armorstatushud.ConfigElement;
import bspkrs.armorstatushud.fml.Reference;
import bspkrs.util.config.ConfigCategory;
import bspkrs.util.config.ConfigProperty;
import bspkrs.util.config.Configuration;
import bspkrs.util.config.Property;
import bspkrs.util.config.gui.GuiConfig;
import bspkrs.util.config.gui.IConfigProperty;

public class GuiASHConfig extends GuiConfig
{
    public GuiASHConfig(GuiScreen parent) throws NoSuchMethodException, SecurityException
    {
        super(parent, getProps(), true, "ArmorStatusHUD", true, GuiConfig.getAbridgedConfigPath(Reference.config.toString()));
    }
    
    private static IConfigProperty[] getProps()
    {
        ConfigCategory cc = Reference.config.getCategory(Configuration.CATEGORY_GENERAL);
        IConfigProperty[] props = new IConfigProperty[ConfigElement.values().length];
        for (int i = 0; i < ConfigElement.values().length; i++)
        {
            ConfigElement ce = ConfigElement.values()[i];
            Property prop = cc.get(ce.key());
            if (prop != null)
                props[i] = new ConfigProperty(prop, ce.propertyType());
        }
        
        return props;
    }
}
