package bspkrs.armorstatushud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import bspkrs.client.util.ColorThreshold;
import bspkrs.client.util.HUDUtils;

public class HUDElement
{
    public final ItemStack itemStack;
    public final int       iconW;
    public final int       iconH;
    public final int       padW;
    private int            elementW;
    private int            elementH;
    private String         itemName   = "";
    private int            itemNameW;
    private String         itemDamage = "";
    private int            itemDamageW;
    private Minecraft      mc         = Minecraft.getMinecraft();
    
    public HUDElement(ItemStack itemStack, int iconW, int iconH, int padW)
    {
        this.itemStack = itemStack;
        this.iconW = iconW;
        this.iconH = iconH;
        this.padW = padW;
        
        initSize();
    }
    
    public int width()
    {
        return elementW;
    }
    
    public int height()
    {
        return elementH;
    }
    
    private void initSize()
    {
        elementH = ArmorStatusHUD.enableItemName ? Math.max(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * 2, iconH) :
                Math.max(mc.fontRenderer.FONT_HEIGHT, iconH);
        
        if (itemStack != null)
        {
            int damage = 1;
            int maxDamage = 1;
            
            if (ArmorStatusHUD.showDamage && itemStack.isItemStackDamageable())
            {
                maxDamage = itemStack.getMaxDamage() + 1;
                damage = maxDamage - itemStack.getItemDamageForDisplay();
                
                if (ArmorStatusHUD.damageDisplayType.equalsIgnoreCase("value"))
                    itemDamage = "\247" + ColorThreshold.getColorCode(ArmorStatusHUD.colorList,
                            (ArmorStatusHUD.damageThresholdType.equalsIgnoreCase("percent") ? damage * 100 / maxDamage : damage)) + damage +
                            (ArmorStatusHUD.showMaxDamage ? "/" + maxDamage : "");
                else if (ArmorStatusHUD.damageDisplayType.equalsIgnoreCase("percent"))
                    itemDamage = "\247" + ColorThreshold.getColorCode(ArmorStatusHUD.colorList,
                            (ArmorStatusHUD.damageThresholdType.equalsIgnoreCase("percent") ? damage * 100 / maxDamage : damage)) +
                            (damage * 100 / maxDamage) + "%";
            }
            
            itemDamageW = mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemDamage));
            elementW = padW + iconW + padW + itemDamageW;
            
            if (ArmorStatusHUD.enableItemName)
            {
                itemName = itemStack.getDisplayName();
                elementW = padW + iconW + padW +
                        Math.max(mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemName)), itemDamageW);
            }
            
            itemNameW = mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemName));
        }
    }
    
    public void renderToHud(int x, int y)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT *//* GL_RESCALE_NORMAL_EXT */);
        RenderHelper.enableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        ArmorStatusHUD.itemRenderer.zLevel = 200.0F;
        
        if (ArmorStatusHUD.alignMode.toLowerCase().contains("right"))
        {
            ArmorStatusHUD.itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, x - (iconW + padW), y);
            if (ArmorStatusHUD.showItemOverlay)
                HUDUtils.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x - (iconW + padW), y);
            
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT *//* GL_RESCALE_NORMAL_EXT */);
            GL11.glDisable(GL11.GL_BLEND);
            
            int stringWidth = mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemName));
            mc.fontRenderer.drawStringWithShadow(itemName + "\247r", x - (padW + iconW + padW) - itemNameW, y, 0xffffff);
            stringWidth = mc.fontRenderer.getStringWidth(HUDUtils.stripCtrl(itemDamage));
            mc.fontRenderer.drawStringWithShadow(itemDamage + "\247r", x - (padW + iconW + padW) - itemDamageW,
                    y + (ArmorStatusHUD.enableItemName ? elementH / 2 : elementH / 4), 0xffffff);
        }
        else
        {
            ArmorStatusHUD.itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, x, y);
            if (ArmorStatusHUD.showItemOverlay)
                HUDUtils.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x, y);
            
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT *//* GL_RESCALE_NORMAL_EXT */);
            GL11.glDisable(GL11.GL_BLEND);
            
            mc.fontRenderer.drawStringWithShadow(itemName + "\247r", x + iconW + padW, y, 0xffffff);
            mc.fontRenderer.drawStringWithShadow(itemDamage + "\247r", x + iconW + padW,
                    y + (ArmorStatusHUD.enableItemName ? elementH / 2 : elementH / 4), 0xffffff);
        }
    }
}
