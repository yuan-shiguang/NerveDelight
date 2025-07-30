package com.yuanshiguang.nervedelight;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("nervedelight")
public class NerveDelight {
    public static final String MOD_ID = "nervedelight";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public NerveDelight() {
        
        // 初始化农夫乐事物品引用
        Blocks.initFarmerDelightItems();
        
        // 注册所有内容
        registerDeferredRegistries();
        
        LOGGER.info("NerveDelight mod initialized!");
    }

    private void registerDeferredRegistries() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        Blocks.initFarmerDelightItems();
        ModItem.ITEMS.register(modEventBus);
        Blocks.BLOCKS.register(modEventBus);
        Blocks.BLOCK_ENTITIES.register(modEventBus);
        Effects.EFFECTS.register(modEventBus);
    }
}