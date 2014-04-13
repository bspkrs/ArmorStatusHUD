package bspkrs.armorstatushud.fml.gui;

import java.lang.reflect.Method;

import net.minecraft.client.gui.GuiScreen;
import bspkrs.armorstatushud.ArmorStatusHUD;
import bspkrs.armorstatushud.ConfigElement;
import bspkrs.util.config.ConfigCategory;
import bspkrs.util.config.ConfigProperty;
import bspkrs.util.config.Configuration;
import bspkrs.util.config.gui.GuiConfig;
import bspkrs.util.config.gui.IConfigProperty;

public class GuiASHConfig extends GuiConfig
{
    public GuiASHConfig(GuiScreen parent) throws NoSuchMethodException, SecurityException
    {
        super(parent, getProps(), Configuration.class.getDeclaredMethod("save"), ArmorStatusHUD.getConfig(),
                ArmorStatusHUD.class.getDeclaredMethod("syncConfig"), null);
    }
    
    public GuiASHConfig(GuiScreen par1GuiScreen, IConfigProperty[] properties, Method saveAction, Object configObject, Method afterSaveAction, Object afterSaveObject)
    {
        super(par1GuiScreen, properties, saveAction, configObject, afterSaveAction, afterSaveObject);
    }
    
    private static IConfigProperty[] getProps()
    {
        ConfigCategory cc = ArmorStatusHUD.getConfig().getCategory(Configuration.CATEGORY_GENERAL);
        IConfigProperty[] props = new IConfigProperty[ConfigElement.values().length];
        for (int i = 0; i < ConfigElement.values().length; i++)
        {
            ConfigElement ce = ConfigElement.values()[i];
            props[i] = new ConfigProperty(cc.get(ce.key()), ce.propertyType());
        }
        
        return props;
    }
}
