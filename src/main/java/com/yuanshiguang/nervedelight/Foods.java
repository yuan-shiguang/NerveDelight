package com.yuanshiguang.nervedelight;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Foods {
    private static final int TICKS_PER_SECOND = 20;
    
    // 42号混凝土拌意大利面
    public static final FoodProperties CONCRETE_SPAGHETTI = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(1f)
            .effect(() -> new MobEffectInstance(Effects.INDIGESTION.get(), 120 * TICKS_PER_SECOND, 0), 1.0f)
            .alwaysEat()
            .build();

    // 仰望星空派
    public static final FoodProperties STARGAZY_PIE = new FoodProperties.Builder()
            .nutrition(10)
            .saturationMod(0.8f)           
            .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 30 * TICKS_PER_SECOND, 0), 1.0F)
            .alwaysEat()
            .build();
    
    // 烤钢筋（手持时获得缓慢效果）
    public static final FoodProperties GRILLED_REBAR = new FoodProperties.Builder()
            .nutrition(1)
            .saturationMod(0.1f)
            .effect(() -> new MobEffectInstance(Effects.INDIGESTION.get(), 120 * TICKS_PER_SECOND, 0), 1.0f)
            .alwaysEat()
            .build();   
    
    // 发酵鲱鱼
    public static final FoodProperties FERMENTED_HERRING = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.1f)                       .alwaysEat()
            .build();  

    public static final FoodProperties FERMENTATION_COD_SLICE = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.8f)
            .build();

    public static final FoodProperties FERMENTATION_SALMON_SLICE = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.8f)
            .build();

    // 混凝土拌面物品类
    public static class ConcreteSpaghettiItem extends Item {
        public ConcreteSpaghettiItem() {
            super(new Properties()
                .tab(ModItem.NERVE_DELIGHT_TAB)
                .food(CONCRETE_SPAGHETTI)
                .stacksTo(1));
        }
    }

    // 仰望星空派物品类
    public static class StargazyPieItem extends Item {
        public StargazyPieItem() {
            super(new Properties()
                .tab(ModItem.NERVE_DELIGHT_TAB)
                .food(STARGAZY_PIE)
                .stacksTo(1));
        }
    }
    
    // 烤钢筋物品类
    public static class GrilledRebarItem extends Item {
        public GrilledRebarItem() {
            super(new Properties()
                .tab(ModItem.NERVE_DELIGHT_TAB)
                .food(GRILLED_REBAR)
                .stacksTo(1));
        }
    }

    // 发酵鲱鱼物品类
    public static class FermentedHerringItem extends Item {
        public FermentedHerringItem() {  
            super(new Properties()
                .tab(ModItem.NERVE_DELIGHT_TAB)
                .food(FERMENTED_HERRING)
                .stacksTo(1));
        }
    }

    // 发酵鳕鱼片物品类
    public static class FermentationCodSliceItem extends Item {
        public FermentationCodSliceItem() {  
            super(new Properties()
                .tab(ModItem.NERVE_DELIGHT_TAB)
                .food(FERMENTATION_COD_SLICE)
                .stacksTo(64));
        }
    }

    // 发酵鲑鱼片物品类
    public static class FermentationSalmonSliceItem extends Item {
        public FermentationSalmonSliceItem() {  
            super(new Properties()
                .tab(ModItem.NERVE_DELIGHT_TAB)
                .food(FERMENTATION_SALMON_SLICE)
                .stacksTo(64));
        }
    }
}