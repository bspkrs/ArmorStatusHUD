package bspkrs.armorstatushud;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import bspkrs.client.util.ColorThreshold;
import bspkrs.client.util.HUDUtils;
import bspkrs.util.BSLog;
import bspkrs.util.CommonUtils;
import bspkrs.util.Configuration;
import bspkrs.util.Const;

public class ArmorStatusHUD
{
    public static final String                VERSION_NUMBER       = "v1.14(" + Const.MCVERSION + ")";
    
    private static final String               DEFAULT_COLOR_LIST   = "100,f; 80,7; 60,e; 40,6; 25,c; 10,4";
    
    public static String                      alignMode            = "bottomleft";
    // @BSProp(info="Valid list mode strings are horizontal and vertical")
    // public static String listMode = "vertical";
    public static boolean                     enableItemName       = false;
    public static boolean                     showItemOverlay      = true;
    public static String                      damageColorList      = DEFAULT_COLOR_LIST;
    public static String                      damageDisplayType    = "value";
    public static boolean                     showMaxDamage        = false;
    public static boolean                     showEquippedItem     = true;
    public static int                         xOffset              = 2;
    public static int                         yOffset              = 2;
    public static int                         yOffsetBottomCenter  = 41;
    public static boolean                     applyXOffsetToCenter = false;
    public static boolean                     applyYOffsetToMiddle = false;
    public static boolean                     showInChat           = false;
    
    private static RenderItem                 itemRenderer         = new RenderItem();
    protected static float                    zLevel               = -110.0F;
    private static ScaledResolution           scaledResolution;
    private static final List<ColorThreshold> colorList            = new ArrayList<ColorThreshold>();
    private static Configuration              config;
    
    public static void loadConfig(File file)
    {
        String ctgyGen = Configuration.CATEGORY_GENERAL;
        
        if (!CommonUtils.isObfuscatedEnv())
        { // debug settings for deobfuscated execution
          //            if (file.exists())
          //                file.delete();
        }
        
        config = new Configuration(file);
        
        config.load();
        
        alignMode = config.getString("alignMode", ctgyGen, alignMode,
                "Valid alignment strings are topleft, topcenter, topright, middleleft, middlecenter, middleright, bottomleft, bottomcenter, bottomright");
        // @BSProp(info="Valid list mode strings are horizontal and vertical")
        // public static String listMode = "vertical";
        enableItemName = config.getBoolean("enableItemName", ctgyGen, enableItemName,
                "Set to true to show item names, false to disable");
        showItemOverlay = config.getBoolean("showItemOverlay", ctgyGen, showItemOverlay,
                "Set to true to show the standard inventory item overlay (damage bar)");
        damageColorList = config.getString("damageColorList", ctgyGen, damageColorList,
                "This is a list of percent damage thresholds and text color codes that will be used when item damage is <= the threshold. " +
                        "Format used: \",\" separates the threshold and the color code, \";\" separates each pair. Valid color values are 0-9, a-f " +
                        "(color values can be found here: http://www.minecraftwiki.net/wiki/File:Colors.png)");
        damageDisplayType = config.getString("damageDisplayType", ctgyGen, damageDisplayType,
                "Valid damageDisplayType strings are value, percent, or none");
        showMaxDamage = config.getBoolean("showMaxDamage", ctgyGen, showMaxDamage,
                "Set to true to show the max damage when damageDisplayType=value");
        showEquippedItem = config.getBoolean("showEquippedItem", ctgyGen, showEquippedItem,
                "Set to true to show info for your currently equipped item, false to disable");
        xOffset = config.getInt("xOffset", ctgyGen, xOffset, 0, Integer.MAX_VALUE,
                "Horizontal offset from the edge of the screen (when using right alignments the x offset is relative to the right edge of the screen)");
        yOffset = config.getInt("yOffset", ctgyGen, yOffset, 0, Integer.MAX_VALUE,
                "Vertical offset from the edge of the screen (when using bottom alignments the y offset is relative to the bottom edge of the screen)");
        yOffsetBottomCenter = config.getInt("yOffsetBottomCenter", ctgyGen, yOffsetBottomCenter, 0, Integer.MAX_VALUE,
                "Vertical offset used only for the bottomcenter alignment to avoid the vanilla HUD");
        applyXOffsetToCenter = config.getBoolean("applyXOffsetToCenter", ctgyGen, applyXOffsetToCenter,
                "Set to true if you want the xOffset value to be applied when using a center alignment");
        applyYOffsetToMiddle = config.getBoolean("applyYOffsetToMiddle", ctgyGen, applyYOffsetToMiddle,
                "Set to true if you want the yOffset value to be applied when using a middle alignment");
        showInChat = config.getBoolean("showInChat", ctgyGen, showInChat,
                "Set to true to show info when chat is open, false to disable info when chat is open");
        
        config.save();
        
        try
        {
            for (String s : damageColorList.split(";"))
            {
                String[] ct = s.split(",");
                colorList.add(new ColorThreshold(Integer.valueOf(ct[0].trim()), ct[1]));
            }
        }
        catch (Throwable e)
        {
            BSLog.warning("Error encountered parsing damageColorList: " + damageColorList);
            BSLog.warning("Reverting to defaultColorList: " + DEFAULT_COLOR_LIST);
            for (String s : DEFAULT_COLOR_LIST.split(";"))
            {
                String[] ct = s.split(",");
                colorList.add(new ColorThreshold(Integer.valueOf(ct[0]), ct[1]));
            }
        }
        
        Collections.sort(colorList);
    }
    
    public static boolean onTickInGame(Minecraft mc)
    {
        if ((mc.inGameHasFocus || mc.currentScreen == null || (mc.currentScreen instanceof GuiChat && showInChat)) && !mc.gameSettings.showDebugInfo && !mc.gameSettings.keyBindPlayerList.pressed)
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
    
    private static void displayArmorStatus(Minecraft mc)
    {
        
        if (playerHasArmorEquipped(mc.thePlayer) || (showEquippedItem && canDisplayItem(mc.thePlayer.getCurrentEquippedItem())))
        {
            int yOffset = enableItemName ? 18 : 16;
            
            int yBase = getY(countOfDisplayableItems(mc.thePlayer), yOffset);
            
            for (int i = 3; i >= -1; i--)
            {
                ItemStack itemStack = null;
                if (i == -1 && showEquippedItem)
                    itemStack = mc.thePlayer.getCurrentEquippedItem();
                else if (i != -1)
                    itemStack = mc.thePlayer.inventory.armorInventory[i];
                else
                    itemStack = null;
                
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                
                if (canDisplayItem(itemStack))
                {
                    int xBase = 0;
                    int damage = 1;
                    int maxDamage = 1;
                    String itemDamage = "";
                    
                    if (itemStack.isItemStackDamageable())
                    {
                        maxDamage = itemStack.getMaxDamage() + 1;
                        damage = maxDamage - itemStack.getItemDamageForDisplay();
                        
                        if (damageDisplayType.equalsIgnoreCase("value"))
                            itemDamage = "\247" + ColorThreshold.getColorCode(colorList, damage * 100 / maxDamage) + damage + (showMaxDamage ? "/" + maxDamage : "");
                        else if (damageDisplayType.equalsIgnoreCase("percent"))
                            itemDamage = "\247" + ColorThreshold.getColorCode(colorList, damage * 100 / maxDamage) + (damage * 100 / maxDamage) + "%";
                    }
                    
                    xBase = getX(18 + 4 + mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemDamage)));
                    
                    String itemName = "";
                    
                    if (enableItemName)
                    {
                        itemName = itemStack.getDisplayName();
                        xBase = getX(18 + 4 + mc.fontRenderer.getStringWidth(itemName));
                    }
                    
                    GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT *//* GL_RESCALE_NORMAL_EXT */);
                    RenderHelper.enableStandardItemLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    itemRenderer.zLevel = 200.0F;
                    
                    if (alignMode.toLowerCase().contains("right"))
                    {
                        xBase = getX(0);
                        // func_110434_K == getTextureManager
                        itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, xBase - 18, yBase);
                        if (showItemOverlay)
                            HUDUtils.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, xBase - 18, yBase);
                        
                        RenderHelper.disableStandardItemLighting();
                        GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT *//* GL_RESCALE_NORMAL_EXT */);
                        
                        int stringWidth = mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemName));
                        mc.fontRenderer.drawStringWithShadow(itemName + "\247r", xBase - 20 - stringWidth, yBase, 0xffffff);
                        stringWidth = mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemDamage));
                        mc.fontRenderer.drawStringWithShadow(itemDamage + "\247r", xBase - 20 - stringWidth, yBase + (enableItemName ? 9 : 4), 0xffffff);
                    }
                    else
                    {
                        itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, xBase, yBase);
                        if (showItemOverlay)
                            HUDUtils.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, xBase, yBase);
                        
                        RenderHelper.disableStandardItemLighting();
                        GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT *//* GL_RESCALE_NORMAL_EXT */);
                        
                        mc.fontRenderer.drawStringWithShadow(itemName + "\247r", xBase + 20, yBase, 0xffffff);
                        mc.fontRenderer.drawStringWithShadow(itemDamage + "\247r", xBase + 20, yBase + (enableItemName ? 9 : 4), 0xffffff);
                    }
                    
                    yBase += yOffset;
                }
            }
        }
    }
}
