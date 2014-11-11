package bspkrs.armorstatushud;

import static net.minecraftforge.common.config.Property.Type.BOOLEAN;
import static net.minecraftforge.common.config.Property.Type.INTEGER;
import static net.minecraftforge.common.config.Property.Type.STRING;
import net.minecraftforge.common.config.Property;

public enum ConfigElement
{
    ENABLED("enabled", "bspkrs.ash.configgui.enabled", "Enables or disables the Armor Status HUD display.", BOOLEAN),
    ALIGN_MODE("alignMode", "bspkrs.ash.configgui.alignMode",
            "Sets the position of the HUD on the screen. Valid alignment strings are topleft, topcenter, topright, middleleft, middlecenter, middleright, bottomleft, bottomcenter, bottomright",
            STRING, new String[] { "topleft", "topcenter", "topright", "middleleft", "middlecenter", "middleright", "bottomleft", "bottomcenter", "bottomright" }),
    LIST_MODE("listMode", "bspkrs.ash.configgui.listMode",
            "Sets the direction to display status items. Valid list mode strings are horizontal and vertical", STRING, new String[] { "vertical", "horizontal" }),
    ENABLE_ITEM_NAME("enableItemName", "bspkrs.ash.configgui.enableItemName",
            "Set to true to show item names, false to disable", BOOLEAN),
    SHOW_DAMAGE_OVERLAY("showDamageOverlay", "bspkrs.ash.configgui.showDamageOverlay",
            "Set to true to show the standard inventory item overlay (damage bar)", BOOLEAN),
    SHOW_ITEM_COUNT("showItemCount", "bspkrs.ash.configgui.showItemCount",
            "Set to true to show the item count overlay", BOOLEAN),
    DAMAGE_COLOR_LIST("damageColorList", "bspkrs.ash.configgui.damageColorList",
            "This is a list of percent damage thresholds and text color codes that will be used when item damage is <= the threshold. " +
                    "Format used: \",\" separates the threshold and the color code, \";\" separates each pair. Valid color values are 0-9, a-f " +
                    "(color values can be found here: http://www.minecraftwiki.net/wiki/File:Colors.png)", STRING),
    DAMAGE_DISPLAY_TYPE("damageDisplayType", "bspkrs.ash.configgui.damageDisplayType",
            "Valid damageDisplayType strings are value, percent, or none", STRING, new String[] { "value", "percent", "none" }),
    DAMAGE_THRESHOLD_TYPE("damageThresholdType", "bspkrs.ash.configgui.damageThresholdType",
            "The type of threshold to use when applying the damageColorList thresholds. Valid values are \"percent\" and \"value\".",
            STRING, new String[] { "value", "percent" }),
    SHOW_ITEM_DAMAGE("showItemDamage", "bspkrs.ash.configgui.showItemDamage",
            "Set to true to show held item damage values, false to disable", BOOLEAN),
    SHOW_ARMOR_DAMAGE("showArmorDamage", "bspkrs.ash.configgui.showArmorDamage",
            "Set to true to show armor damage values, false to disable", BOOLEAN),
    SHOW_MAX_DAMAGE("showMaxDamage", "bspkrs.ash.configgui.showMaxDamage",
            "Set to true to show the max damage when damageDisplayType=value", BOOLEAN),
    SHOW_EQUIPPED_ITEM("showEquippedItem", "bspkrs.ash.configgui.showEquippedItem",
            "Set to true to show info for your currently equipped item, false to disable", BOOLEAN),
    X_OFFSET("xOffset", "bspkrs.ash.configgui.xOffset",
            "Horizontal offset from the edge of the screen (when using right alignments the x offset is relative to the right edge of the screen)", INTEGER),
    Y_OFFSET("yOffset", "bspkrs.ash.configgui.yOffset",
            "Vertical offset from the edge of the screen (when using bottom alignments the y offset is relative to the bottom edge of the screen)", INTEGER),
    Y_OFFSET_BOTTOM_CENTER("yOffsetBottomCenter", "bspkrs.ash.configgui.yOffsetBottomCenter",
            "Vertical offset used only for the bottomcenter alignment to avoid the vanilla HUD", INTEGER),
    APPLY_X_OFFSET_TO_CENTER("applyXOffsetToCenter", "bspkrs.ash.configgui.applyXOffsetToCenter",
            "Set to true if you want the xOffset value to be applied when using a center alignment", BOOLEAN),
    APPLY_Y_OFFSET_TO_MIDDLE("applyYOffsetToMiddle", "bspkrs.ash.configgui.applyYOffsetToMiddle",
            "Set to true if you want the yOffset value to be applied when using a middle alignment", BOOLEAN),
    SHOW_IN_CHAT("showInChat", "bspkrs.ash.configgui.showInChat",
            "Set to true to show info when chat is open, false to disable info when chat is open", BOOLEAN);

    private String        key;
    private String        langKey;
    private String        desc;
    private Property.Type propertyType;
    private String[]      validStrings;

    private ConfigElement(String key, String langKey, String desc, Property.Type propertyType, String[] validStrings)
    {
        this.key = key;
        this.langKey = langKey;
        this.desc = desc;
        this.propertyType = propertyType;
        this.validStrings = validStrings;
    }

    private ConfigElement(String key, String langKey, String desc, Property.Type propertyType)
    {
        this(key, langKey, desc, propertyType, new String[0]);
    }

    public String key()
    {
        return key;
    }

    public String languageKey()
    {
        return langKey;
    }

    public String desc()
    {
        return desc;
    }

    public Property.Type propertyType()
    {
        return propertyType;
    }

    public String[] validStrings()
    {
        return validStrings;
    }
}