package com.yuanshiguang.nervedelight;

import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.*;
import javax.annotation.Nullable;
import java.util.List;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "nervedelight");

    public static final CreativeModeTab NERVE_DELIGHT_TAB = new CreativeModeTab("nervedelight") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(CONCRETE_SPAGHETTI.get());
        }
    };

    // 原有食物物品
    public static final RegistryObject<Item> CONCRETE_SPAGHETTI =
            ITEMS.register("concrete_spaghetti", Foods.ConcreteSpaghettiItem::new);
            
    public static final RegistryObject<Item> STARGAZY_PIE =
            ITEMS.register("stargazy_pie", Foods.StargazyPieItem::new);
            
    public static final RegistryObject<Item> GRILLED_REBAR =
            ITEMS.register("grilled_rebar", Foods.GrilledRebarItem::new);

    public static final RegistryObject<Item> FERMENTED_HERRING =
            ITEMS.register("fermented_herring", Foods.FermentedHerringItem::new);
    public static final RegistryObject<Item> FERMENTATION_COD_SLICE =
            ITEMS.register("fermentation_cod_slice", Foods.FermentationCodSliceItem::new);
    public static final RegistryObject<Item> FERMENTATION_SALMON_SLICE =
            ITEMS.register("fermentation_salmon_slice", Foods.FermentationSalmonSliceItem::new);
            
    public static final RegistryObject<Item> CANNED = ITEMS.register("canned", 
        () -> new Item(new Item.Properties().tab(NERVE_DELIGHT_TAB).stacksTo(16)));

    // 发酵桶物品
    public static final RegistryObject<Item> FERMENTATION_BARREL = ITEMS.register("fermentation_barrel",
        () -> new BlockItem(Blocks.FERMENTATION_BARREL.get(),
            new Item.Properties().tab(NERVE_DELIGHT_TAB)) {
        
        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, 
                                  List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(new TranslatableComponent("tooltip.nervedelight.fermentation_barrel"));
            super.appendHoverText(stack, level, tooltip, flag); // 调用父类方法以确保其他 Tooltip 逻辑正常
        }
    });
}