package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringTranslate;

import org.lwjgl.opengl.GL11;

import bspkrs.client.util.ColorThreshold;
import bspkrs.client.util.HUDUtils;
import bspkrs.util.ModVersionChecker;

public class mod_ArmorStatusHUD extends BaseMod
{
    private static String              defaultColorList     = "100,f; 80,7; 60,e; 40,6; 25,c; 10,4";
    
    @MLProp(info = "Valid alignment strings are topleft, topcenter, topright, middleleft, middlecenter, middleright, bottomleft, bottomcenter, bottomright")
    public static String               alignMode            = "bottomleft";
    // @MLProp(info="Valid list mode strings are horizontal and vertical")
    // public static String listMode = "vertical";
    @MLProp(info = "Set to true to show item names, false to disable")
    public static boolean              enableItemName       = false;
    @MLProp(info = "Set to true to show the standard inventory item overlay (damage bar)")
    public static boolean              showItemOverlay      = true;
    @MLProp(info = "This is a list of percent damage thresholds and text color codes that will be used when item damage is <= the threshold. Format used: \",\" separates the threshold and the color code, \";\" separates each pair. Valid color values are 0-9, a-f (color values can be found here: http://www.minecraftwiki.net/wiki/File:Colors.png)")
    public static String               damageColorList      = defaultColorList;
    @MLProp(info = "Valid damageDisplayType strings are value, percent, or none")
    public static String               damageDisplayType    = "value";
    @MLProp(info = "Set to true to show the max damage when damageDisplayType=value")
    public static boolean              showMaxDamage        = false;
    @MLProp(info = "Set to true to show info for your currently equipped item, false to disable")
    public static boolean              showEquippedItem     = true;
    @MLProp(info = "Horizontal offset from the edge of the screen (when using right alignments the x offset is relative to the right edge of the screen)")
    public static int                  xOffset              = 2;
    @MLProp(info = "Vertical offset from the edge of the screen (when using bottom alignments the y offset is relative to the bottom edge of the screen)")
    public static int                  yOffset              = 2;
    @MLProp(info = "Vertical offset used only for the bottomcenter alignment to avoid the vanilla HUD")
    public static int                  yOffsetBottomCenter  = 41;
    @MLProp(info = "Set to true if you want the xOffset value to be applied when using a center alignment")
    public static boolean              applyXOffsetToCenter = false;
    @MLProp(info = "Set to true if you want the yOffset value to be applied when using a middle alignment")
    public static boolean              applyYOffsetToMiddle = false;
    @MLProp(info = "Set to true to show info when chat is open, false to disable info when chat is open\n\n**ONLY EDIT WHAT IS BELOW THIS**")
    public static boolean              showInChat           = false;
    
    private ModVersionChecker          versionChecker;
    private boolean                    allowUpdateCheck;
    private final String               versionURL           = "http://bspk.rs/Minecraft/1.5.0/armorStatusHUD.version";
    private final String               mcfTopic             = "http://www.minecraftforum.net/topic/1114612-";
    
    private static RenderItem          itemRenderer         = new RenderItem();
    protected float                    zLevel               = 0.0F;
    private ScaledResolution           scaledResolution;
    private final List<ColorThreshold> colorList;
    
    public mod_ArmorStatusHUD()
    {
        colorList = new ArrayList<ColorThreshold>();
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
            ModLoader.getLogger().log(Level.WARNING, "Error encountered parsing damageColorList: " + damageColorList);
            ModLoader.getLogger().log(Level.WARNING, "Reverting to defaultColorList: " + defaultColorList);
            for (String s : defaultColorList.split(";"))
            {
                String[] ct = s.split(",");
                colorList.add(new ColorThreshold(Integer.valueOf(ct[0]), ct[1]));
            }
        }
        
        Collections.sort(colorList);
        
        allowUpdateCheck = mod_bspkrsCore.allowUpdateCheck;
        
        if (allowUpdateCheck)
            versionChecker = new ModVersionChecker(getName(), getVersion(), versionURL, mcfTopic, ModLoader.getLogger());
    }
    
    @Override
    public String getName()
    {
        return "ArmorStatusHUD";
    }
    
    @Override
    public String getVersion()
    {
        return "v1.6(1.5.0)";
    }
    
    @Override
    public String getPriorities()
    {
        return "required-after:mod_bspkrsCore";
    }
    
    @Override
    public void load()
    {
        if (allowUpdateCheck)
            versionChecker.checkVersionWithLogging();
        ModLoader.setInGameHook(this, true, false);
    }
    
    @Override
    public boolean onTickInGame(float f, Minecraft mc)
    {
        if ((mc.inGameHasFocus || mc.currentScreen == null || (mc.currentScreen instanceof GuiChat && showInChat)) && !mc.gameSettings.showDebugInfo && !mc.gameSettings.keyBindPlayerList.pressed)
        {
            scaledResolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            displayArmorStatus(mc);
        }
        
        if (allowUpdateCheck)
        {
            if (!versionChecker.isCurrentVersion())
                for (String msg : versionChecker.getInGameMessage())
                    mc.thePlayer.addChatMessage(msg);
            allowUpdateCheck = false;
        }
        
        return true;
    }
    
    private int getX(int width)
    {
        if (alignMode.toLowerCase().contains("center"))
            return scaledResolution.getScaledWidth() / 2 - width / 2 + (applyXOffsetToCenter ? xOffset : 0);
        else if (alignMode.toLowerCase().contains("right"))
            return scaledResolution.getScaledWidth() - width - xOffset;
        else
            return xOffset;
    }
    
    private int getY(int rowCount, int height)
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
    
    private void displayArmorStatus(Minecraft mc)
    {
        
        if (playerHasArmorEquipped(mc.thePlayer) || (showEquippedItem && canDisplayItem(mc.thePlayer.getCurrentEquippedItem())))
        {
            int yOffset = enableItemName ? 18 : 16;
            
            int yBase = getY(countOfDisplayableItems(mc.thePlayer), yOffset);
            
            for (int i = 3; i >= -1; i--)
            {
                ItemStack item = null;
                if (i == -1 && showEquippedItem)
                    item = mc.thePlayer.getCurrentEquippedItem();
                else if (i != -1)
                    item = mc.thePlayer.inventory.armorInventory[i];
                else
                    item = null;
                
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                
                if (canDisplayItem(item))
                {
                    int xBase = 0;
                    int damage;
                    int maxDamage;
                    String itemDamage = "";
                    
                    if (item.isItemStackDamageable())
                    {
                        maxDamage = item.getMaxDamage() + 1;
                        damage = maxDamage - item.getItemDamage();
                        if (damageDisplayType.equalsIgnoreCase("value"))
                            itemDamage = "\247" + ColorThreshold.getColorCode(colorList, damage * 100 / maxDamage) + damage + (showMaxDamage ? "/" + maxDamage : "");
                        else if (damageDisplayType.equalsIgnoreCase("percent"))
                            itemDamage = "\247" + ColorThreshold.getColorCode(colorList, damage * 100 / maxDamage) + (damage * 100 / maxDamage) + "%";
                    }
                    
                    // if(item.itemID == Item.bow.shiftedIndex)
                    // itemDamage = "(" +
                    // HUDUtils.countInInventory(mc.thePlayer,
                    // Item.arrow.shiftedIndex) + ") " + itemDamage;
                    
                    xBase = getX(18 + 4 + mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemDamage)));
                    
                    String itemName = "";
                    
                    if (enableItemName)
                    {
                        itemName = StringTranslate.getInstance().translateNamedKey(item.getItem().getUnlocalizedName());
                        xBase = getX(18 + 4 + mc.fontRenderer.getStringWidth(itemName));
                    }
                    
                    // GL11.glEnable(GL11.GL_SCISSOR_TEST);
                    // GL11.glPushMatrix();
                    // GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT *//* GL_RESCALE_NORMAL_EXT */);
                    RenderHelper.enableStandardItemLighting();
                    RenderHelper.enableGUIStandardItemLighting();
                    itemRenderer.zLevel = 200.0F;
                    
                    if (alignMode.toLowerCase().contains("right"))
                    {
                        xBase = getX(0);
                        this.itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, item, xBase - 18, yBase);
                        if (showItemOverlay)
                            HUDUtils.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, item, xBase - 18, yBase);
                        
                        RenderHelper.disableStandardItemLighting();
                        GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT *//* GL_RESCALE_NORMAL_EXT */);
                        
                        // GL11.glPopMatrix();
                        // GL11.glDisable(GL11.GL_SCISSOR_TEST);
                        
                        int stringWidth = mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemName));
                        mc.fontRenderer.drawStringWithShadow(itemName + "\247r", xBase - 20 - stringWidth, yBase, 0xffffff);
                        stringWidth = mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemDamage));
                        mc.fontRenderer.drawStringWithShadow(itemDamage + "\247r", xBase - 20 - stringWidth, yBase + (enableItemName ? 9 : 4), 0xffffff);
                    }
                    else
                    {
                        this.itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, item, xBase, yBase);
                        if (showItemOverlay)
                            HUDUtils.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, item, xBase, yBase);
                        
                        RenderHelper.disableStandardItemLighting();
                        GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT *//* GL_RESCALE_NORMAL_EXT */);
                        
                        // GL11.glPopMatrix();
                        // GL11.glDisable(GL11.GL_SCISSOR_TEST);
                        
                        mc.fontRenderer.drawStringWithShadow(itemName + "\247r", xBase + 20, yBase, 0xffffff);
                        mc.fontRenderer.drawStringWithShadow(itemDamage + "\247r", xBase + 20, yBase + (enableItemName ? 9 : 4), 0xffffff);
                    }
                    
                    yBase += yOffset;
                }
            }
        }
    }
}
