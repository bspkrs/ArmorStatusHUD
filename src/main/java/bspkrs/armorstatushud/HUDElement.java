package bspkrs.armorstatushud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import bspkrs.client.util.ColorThreshold;
import bspkrs.client.util.HUDUtils;

public class HUDElement
{
    public final ItemStack  itemStack;
    public final int        iconW;
    public final int        iconH;
    public final int        padW;
    private int             elementW;
    private int             elementH;
    private String          itemName   = "";
    private int             itemNameW;
    private String          itemDamage = "";
    private int             itemDamageW;
    private final boolean   isArmor;
    private final Minecraft mc         = Minecraft.getMinecraft();

    public HUDElement(ItemStack itemStack, int iconW, int iconH, int padW, boolean isArmor)
    {
        this.itemStack = itemStack;
        this.iconW = iconW;
        this.iconH = iconH;
        this.padW = padW;
        this.isArmor = isArmor;

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
        elementH = ArmorStatusHUD.enableItemName ? Math.max(Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 2, iconH) :
                Math.max(mc.fontRendererObj.FONT_HEIGHT, iconH);

        if (itemStack != null)
        {
            int damage = 1;
            int maxDamage = 1;

            if (((isArmor && ArmorStatusHUD.showArmorDamage) || (!isArmor && ArmorStatusHUD.showItemDamage)) && itemStack.isItemStackDamageable())
            {
                maxDamage = itemStack.getMaxDamage() + 1;
                damage = maxDamage - itemStack.getItemDamage();

                if (ArmorStatusHUD.damageDisplayType.equalsIgnoreCase("value"))
                    itemDamage = "\247" + ColorThreshold.getColorCode(ArmorStatusHUD.colorList,
                            (ArmorStatusHUD.damageThresholdType.equalsIgnoreCase("percent") ? (damage * 100) / maxDamage : damage)) + damage +
                            (ArmorStatusHUD.showMaxDamage ? "/" + maxDamage : "");
                else if (ArmorStatusHUD.damageDisplayType.equalsIgnoreCase("percent"))
                    itemDamage = "\247" + ColorThreshold.getColorCode(ArmorStatusHUD.colorList,
                            (ArmorStatusHUD.damageThresholdType.equalsIgnoreCase("percent") ? (damage * 100) / maxDamage : damage)) +
                            ((damage * 100) / maxDamage) + "%";
            }

            itemDamageW = mc.fontRendererObj.getStringWidth(HUDUtils.stripCtrl(itemDamage));
            elementW = padW + iconW + padW + itemDamageW;

            if (ArmorStatusHUD.enableItemName)
            {
                itemName = itemStack.getDisplayName();
                elementW = padW + iconW + padW +
                        Math.max(mc.fontRendererObj.getStringWidth(HUDUtils.stripCtrl(itemName)), itemDamageW);
            }

            itemNameW = mc.fontRendererObj.getStringWidth(HUDUtils.stripCtrl(itemName));
        }
    }

    public void renderToHud(int x, int y)
    {
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
        RenderHelper.enableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        itemRenderer.zLevel = 200.0F;

        if (ArmorStatusHUD.alignMode.toLowerCase().contains("right"))
        {
            itemRenderer.renderItemAndEffectIntoGUI(itemStack, x - (iconW + padW), y);
            HUDUtils.renderItemOverlayIntoGUI(mc.fontRendererObj, itemStack, x - (iconW + padW), y, ArmorStatusHUD.showDamageOverlay, ArmorStatusHUD.showItemCount);

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
            GL11.glDisable(GL11.GL_BLEND);

            mc.fontRendererObj.drawStringWithShadow(itemName + "\247r", x - (padW + iconW + padW) - itemNameW, y, 0xffffff);
            mc.fontRendererObj.drawStringWithShadow(itemDamage + "\247r", x - (padW + iconW + padW) - itemDamageW,
                    y + (ArmorStatusHUD.enableItemName ? elementH / 2 : elementH / 4), 0xffffff);
        }
        else
        {
            itemRenderer.renderItemAndEffectIntoGUI(itemStack, x, y);
            HUDUtils.renderItemOverlayIntoGUI(mc.fontRendererObj, itemStack, x, y, ArmorStatusHUD.showDamageOverlay, ArmorStatusHUD.showItemCount);

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
            GL11.glDisable(GL11.GL_BLEND);

            mc.fontRendererObj.drawStringWithShadow(itemName + "\247r", x + iconW + padW, y, 0xffffff);
            mc.fontRendererObj.drawStringWithShadow(itemDamage + "\247r", x + iconW + padW,
                    y + (ArmorStatusHUD.enableItemName ? elementH / 2 : elementH / 4), 0xffffff);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
