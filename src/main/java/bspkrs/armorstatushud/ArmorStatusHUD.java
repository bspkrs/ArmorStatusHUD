package bspkrs.armorstatushud;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import bspkrs.armorstatushud.fml.Reference;
import bspkrs.client.util.ColorThreshold;
import bspkrs.util.CommonUtils;
import bspkrs.util.config.Configuration;
import cpw.mods.fml.common.FMLLog;

public class ArmorStatusHUD
{
    private static final String       DEFAULT_COLOR_LIST          = "100,f; 80,7; 60,e; 40,6; 25,c; 10,4";
    
    private final static boolean      enabledDefault              = true;
    public static boolean             enabled                     = enabledDefault;
    private static String             alignModeDefault            = "bottomleft";
    public static String              alignMode                   = alignModeDefault;
    private static String             listModeDefault             = "horizontal";
    public static String              listMode                    = listModeDefault;
    private static boolean            enableItemNameDefault       = false;
    public static boolean             enableItemName              = enableItemNameDefault;
    private static boolean            showDamageOverlayDefault    = true;
    public static boolean             showDamageOverlay           = showDamageOverlayDefault;
    private static boolean            showItemCountDefault        = true;
    public static boolean             showItemCount               = showItemCountDefault;
    private static String             damageColorListDefault      = DEFAULT_COLOR_LIST;
    public static String              damageColorList             = damageColorListDefault;
    private static String             damageDisplayTypeDefault    = "value";
    public static String              damageDisplayType           = damageDisplayTypeDefault;
    private static String             damageThresholdTypeDefault  = "percent";
    public static String              damageThresholdType         = damageThresholdTypeDefault;
    private static boolean            showItemDamageDefault       = true;
    public static boolean             showItemDamage              = showItemDamageDefault;
    private static boolean            showArmorDamageDefault      = true;
    public static boolean             showArmorDamage             = showArmorDamageDefault;
    private static boolean            showMaxDamageDefault        = false;
    public static boolean             showMaxDamage               = showMaxDamageDefault;
    private static boolean            showEquippedItemDefault     = true;
    public static boolean             showEquippedItem            = showEquippedItemDefault;
    private static int                xOffsetDefault              = 2;
    public static int                 xOffset                     = xOffsetDefault;
    private static int                yOffsetDefault              = 2;
    public static int                 yOffset                     = yOffsetDefault;
    private static int                yOffsetBottomCenterDefault  = 41;
    public static int                 yOffsetBottomCenter         = yOffsetBottomCenterDefault;
    private static boolean            applyXOffsetToCenterDefault = false;
    public static boolean             applyXOffsetToCenter        = applyXOffsetToCenterDefault;
    private static boolean            applyYOffsetToMiddleDefault = false;
    public static boolean             applyYOffsetToMiddle        = applyYOffsetToMiddleDefault;
    private static boolean            showInChatDefault           = false;
    public static boolean             showInChat                  = showInChatDefault;
    
    static RenderItem                 itemRenderer                = new RenderItem();
    static float                      zLevel                      = -110.0F;
    private static ScaledResolution   scaledResolution;
    static final List<ColorThreshold> colorList                   = new ArrayList<ColorThreshold>();
    private static List<HUDElement>   elements                    = new ArrayList<HUDElement>();
    private static Pattern            colorListPattern            = Pattern.compile("([0-9]+,[0-9,a-f]{1}(;[ ]*|$))+");
    
    public static void initConfig(File file)
    {
        
        if (!CommonUtils.isObfuscatedEnv())
        { // debug settings for deobfuscated execution
          //            if (file.exists())
          //                file.delete();
        }
        
        Reference.config = new Configuration(file);
        syncConfig();
    }
    
    public static void syncConfig()
    {
        String ctgyGen = Configuration.CATEGORY_GENERAL;
        
        Reference.config.load();
        
        Reference.config.addCustomCategoryComment(ctgyGen, "ATTENTION: Editing this file manually is no longer necessary. \n" +
                "Type the command '/armorstatus config' without the quotes in-game to modify these settings.");
        Reference.config.setCategoryIsHotLoadable(ctgyGen, true);
        
        LinkedHashSet<String> orderedKeys = new LinkedHashSet<String>(ConfigElement.values().length);
        
        enabled = Reference.config.getBoolean(ConfigElement.ENABLED.key(), ctgyGen, enabledDefault, ConfigElement.ENABLED.desc(),
                ConfigElement.ENABLED.languageKey());
        orderedKeys.add(ConfigElement.ENABLED.key());
        alignMode = Reference.config.getString(ConfigElement.ALIGN_MODE.key(), ctgyGen, alignModeDefault, ConfigElement.ALIGN_MODE.desc(),
                ConfigElement.ALIGN_MODE.validStrings(), ConfigElement.ALIGN_MODE.languageKey());
        orderedKeys.add(ConfigElement.ALIGN_MODE.key());
        listMode = Reference.config.getString(ConfigElement.LIST_MODE.key(), ctgyGen, listModeDefault, ConfigElement.LIST_MODE.desc(),
                ConfigElement.LIST_MODE.validStrings(), ConfigElement.LIST_MODE.languageKey());
        orderedKeys.add(ConfigElement.LIST_MODE.key());
        enableItemName = Reference.config.getBoolean(ConfigElement.ENABLE_ITEM_NAME.key(), ctgyGen, enableItemNameDefault,
                ConfigElement.ENABLE_ITEM_NAME.desc(), ConfigElement.ENABLE_ITEM_NAME.languageKey());
        orderedKeys.add(ConfigElement.ENABLE_ITEM_NAME.key());
        showDamageOverlay = Reference.config.getBoolean(ConfigElement.SHOW_DAMAGE_OVERLAY.key(), ctgyGen, showDamageOverlayDefault,
                ConfigElement.SHOW_DAMAGE_OVERLAY.desc(), ConfigElement.SHOW_DAMAGE_OVERLAY.languageKey());
        orderedKeys.add(ConfigElement.SHOW_DAMAGE_OVERLAY.key());
        showItemCount = Reference.config.getBoolean(ConfigElement.SHOW_ITEM_COUNT.key(), ctgyGen, showItemCountDefault,
                ConfigElement.SHOW_ITEM_COUNT.desc(), ConfigElement.SHOW_ITEM_COUNT.languageKey());
        orderedKeys.add(ConfigElement.SHOW_ITEM_COUNT.key());
        showArmorDamage = Reference.config.getBoolean(ConfigElement.SHOW_ARMOR_DAMAGE.key(), ctgyGen, showArmorDamageDefault,
                ConfigElement.SHOW_ARMOR_DAMAGE.desc(),
                ConfigElement.SHOW_ARMOR_DAMAGE.languageKey());
        orderedKeys.add(ConfigElement.SHOW_ARMOR_DAMAGE.key());
        showItemDamage = Reference.config.getBoolean(ConfigElement.SHOW_ITEM_DAMAGE.key(), ctgyGen, showItemDamageDefault,
                ConfigElement.SHOW_ITEM_DAMAGE.desc(),
                ConfigElement.SHOW_ITEM_DAMAGE.languageKey());
        orderedKeys.add(ConfigElement.SHOW_ITEM_DAMAGE.key());
        showMaxDamage = Reference.config.getBoolean(ConfigElement.SHOW_MAX_DAMAGE.key(), ctgyGen, showMaxDamageDefault,
                ConfigElement.SHOW_MAX_DAMAGE.desc(), ConfigElement.SHOW_MAX_DAMAGE.languageKey());
        orderedKeys.add(ConfigElement.SHOW_MAX_DAMAGE.key());
        showEquippedItem = Reference.config.getBoolean(ConfigElement.SHOW_EQUIPPED_ITEM.key(), ctgyGen, showEquippedItemDefault,
                ConfigElement.SHOW_EQUIPPED_ITEM.desc(), ConfigElement.SHOW_EQUIPPED_ITEM.languageKey());
        orderedKeys.add(ConfigElement.SHOW_EQUIPPED_ITEM.key());
        showInChat = Reference.config.getBoolean(ConfigElement.SHOW_IN_CHAT.key(), ctgyGen, showInChatDefault, ConfigElement.SHOW_IN_CHAT.desc(),
                ConfigElement.SHOW_IN_CHAT.languageKey());
        orderedKeys.add(ConfigElement.SHOW_IN_CHAT.key());
        damageColorList = Reference.config.getString(ConfigElement.DAMAGE_COLOR_LIST.key(), ctgyGen, damageColorListDefault,
                ConfigElement.DAMAGE_COLOR_LIST.desc(), ConfigElement.DAMAGE_COLOR_LIST.languageKey(), colorListPattern);
        orderedKeys.add(ConfigElement.DAMAGE_COLOR_LIST.key());
        damageDisplayType = Reference.config.getString(ConfigElement.DAMAGE_DISPLAY_TYPE.key(), ctgyGen, damageDisplayTypeDefault,
                ConfigElement.DAMAGE_DISPLAY_TYPE.desc(), ConfigElement.DAMAGE_DISPLAY_TYPE.validStrings(), ConfigElement.DAMAGE_DISPLAY_TYPE.languageKey());
        orderedKeys.add(ConfigElement.DAMAGE_DISPLAY_TYPE.key());
        damageThresholdType = Reference.config.getString(ConfigElement.DAMAGE_THRESHOLD_TYPE.key(), ctgyGen, damageThresholdTypeDefault,
                ConfigElement.DAMAGE_THRESHOLD_TYPE.desc(), ConfigElement.DAMAGE_THRESHOLD_TYPE.validStrings(), ConfigElement.DAMAGE_THRESHOLD_TYPE.languageKey());
        orderedKeys.add(ConfigElement.DAMAGE_THRESHOLD_TYPE.key());
        xOffset = Reference.config.getInt(ConfigElement.X_OFFSET.key(), ctgyGen, xOffsetDefault, Integer.MIN_VALUE, Integer.MAX_VALUE,
                ConfigElement.X_OFFSET.desc(), ConfigElement.X_OFFSET.languageKey());
        orderedKeys.add(ConfigElement.X_OFFSET.key());
        applyXOffsetToCenter = Reference.config.getBoolean(ConfigElement.APPLY_X_OFFSET_TO_CENTER.key(), ctgyGen, applyXOffsetToCenterDefault,
                ConfigElement.APPLY_X_OFFSET_TO_CENTER.desc(), ConfigElement.APPLY_X_OFFSET_TO_CENTER.languageKey());
        orderedKeys.add(ConfigElement.APPLY_X_OFFSET_TO_CENTER.key());
        yOffset = Reference.config.getInt(ConfigElement.Y_OFFSET.key(), ctgyGen, yOffsetDefault, Integer.MIN_VALUE, Integer.MAX_VALUE,
                ConfigElement.Y_OFFSET.desc(), ConfigElement.Y_OFFSET.languageKey());
        orderedKeys.add(ConfigElement.Y_OFFSET.key());
        applyYOffsetToMiddle = Reference.config.getBoolean(ConfigElement.APPLY_Y_OFFSET_TO_MIDDLE.key(), ctgyGen, applyYOffsetToMiddleDefault,
                ConfigElement.APPLY_Y_OFFSET_TO_MIDDLE.desc(), ConfigElement.APPLY_Y_OFFSET_TO_MIDDLE.languageKey());
        orderedKeys.add(ConfigElement.APPLY_Y_OFFSET_TO_MIDDLE.key());
        yOffsetBottomCenter = Reference.config.getInt(ConfigElement.Y_OFFSET_BOTTOM_CENTER.key(), ctgyGen, yOffsetBottomCenterDefault,
                Integer.MIN_VALUE, Integer.MAX_VALUE, ConfigElement.Y_OFFSET_BOTTOM_CENTER.desc(), ConfigElement.Y_OFFSET_BOTTOM_CENTER.languageKey());
        orderedKeys.add(ConfigElement.Y_OFFSET_BOTTOM_CENTER.key());
        
        Reference.config.setCategoryPropertyOrder(ctgyGen, orderedKeys);
        
        Reference.config.save();
        
        try
        {
            for (String s : damageColorList.split(";"))
            {
                String[] ct = s.split(",");
                colorList.add(new ColorThreshold(Integer.valueOf(ct[0].trim()), ct[1].trim()));
            }
        }
        catch (Throwable e)
        {
            FMLLog.log(Reference.MODID, Level.WARN, "Error encountered parsing damageColorList: " + damageColorList);
            FMLLog.log(Reference.MODID, Level.WARN, "Reverting to defaultColorList: " + DEFAULT_COLOR_LIST);
            for (String s : DEFAULT_COLOR_LIST.split(";"))
            {
                String[] ct = s.split(",");
                colorList.add(new ColorThreshold(Integer.valueOf(ct[0].trim()), ct[1].trim()));
            }
        }
        
        Collections.sort(colorList);
    }
    
    public static boolean onTickInGame(Minecraft mc)
    {
        if (enabled && (mc.inGameHasFocus || mc.currentScreen == null || (mc.currentScreen instanceof GuiChat && showInChat))
                && !mc.gameSettings.showDebugInfo)
        {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            scaledResolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            displayArmorStatus(mc);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        
        return true;
    }
    
    private static int getX(int width)
    {
        if (alignMode.toLowerCase().contains("center"))
            return scaledResolution.getScaledWidth() / 2 - width / 2 + (applyXOffsetToCenter ? xOffset : 0);
        else if (alignMode.toLowerCase().contains("right"))
            return scaledResolution.getScaledWidth() - width - xOffset;
        else
            return xOffset;
    }
    
    private static int getY(int rowCount, int height)
    {
        if (alignMode.toLowerCase().contains("middle"))
            return (scaledResolution.getScaledHeight() / 2) - ((rowCount * height) / 2) + (applyYOffsetToMiddle ? yOffset : 0);
        else if (alignMode.equalsIgnoreCase("bottomleft") || alignMode.equalsIgnoreCase("bottomright"))
            return scaledResolution.getScaledHeight() - (rowCount * height) - yOffset;
        else if (alignMode.equalsIgnoreCase("bottomcenter"))
            return scaledResolution.getScaledHeight() - (rowCount * height) - yOffsetBottomCenter;
        else
            return yOffset;
    }
    
    public static boolean playerHasArmorEquipped(EntityPlayer player)
    {
        return player.inventory.armorItemInSlot(0) != null || player.inventory.armorItemInSlot(1) != null || player.inventory.armorItemInSlot(2) != null || player.inventory.armorItemInSlot(3) != null;
    }
    
    public static int countOfDisplayableItems(EntityPlayer player)
    {
        int i = 0;
        i += canDisplayItem(player.inventory.armorItemInSlot(0)) ? 1 : 0;
        i += canDisplayItem(player.inventory.armorItemInSlot(1)) ? 1 : 0;
        i += canDisplayItem(player.inventory.armorItemInSlot(2)) ? 1 : 0;
        i += canDisplayItem(player.inventory.armorItemInSlot(3)) ? 1 : 0;
        i += showEquippedItem && canDisplayItem(player.getCurrentEquippedItem()) ? 1 : 0;
        return i;
    }
    
    public static boolean canDisplayItem(ItemStack item)
    {
        return item != null;
    }
    
    private static void getHUDElements(Minecraft mc)
    {
        elements.clear();
        
        for (int i = 3; i >= -1; i--)
        {
            ItemStack itemStack = null;
            if (i == -1 && showEquippedItem)
                itemStack = mc.thePlayer.getCurrentEquippedItem();
            else if (i != -1)
                itemStack = mc.thePlayer.inventory.armorInventory[i];
            
            if (itemStack != null)
                elements.add(new HUDElement(itemStack, 16, 16, 2, i > -1));
        }
    }
    
    private static int getElementsWidth()
    {
        int r = 0;
        for (HUDElement he : elements)
            r += he.width();
        
        return r;
    }
    
    private static void displayArmorStatus(Minecraft mc)
    {
        getHUDElements(mc);
        
        if (elements.size() > 0)
        {
            int yOffset = enableItemName ? 18 : 16;
            
            if (listMode.equalsIgnoreCase("vertical"))
            {
                int yBase = getY(elements.size(), yOffset);
                
                for (HUDElement e : elements)
                {
                    e.renderToHud((alignMode.toLowerCase().contains("right") ? getX(0) : getX(e.width())), yBase);
                    yBase += yOffset;
                }
            }
            else if (listMode.equalsIgnoreCase("horizontal"))
            {
                int totalWidth = getElementsWidth();
                int yBase = getY(1, yOffset);
                int xBase = getX(totalWidth);
                int prevX = 0;
                
                for (HUDElement e : elements)
                {
                    e.renderToHud(xBase + prevX + (alignMode.toLowerCase().contains("right") ? e.width() : 0), yBase);
                    prevX += e.width();
                }
            }
        }
    }
}
