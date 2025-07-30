package com.yuanshiguang.nervedelight;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.ModList;

public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, "nervedelight");
    
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, "nervedelight");
    
    // 农夫乐事物品引用
    private static Item COD_SLICE = null;
    private static Item SALMON_SLICE = null;
    
    public static void initFarmerDelightItems() {
        if (ModList.get().isLoaded("farmersdelight")) {
            COD_SLICE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("farmersdelight", "cod_slice"));
            SALMON_SLICE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("farmersdelight", "salmon_slice"));
        }
    }
    
    public static final RegistryObject<Block> FERMENTATION_BARREL = BLOCKS.register("fermentation_barrel", 
        () -> new FermentationBarrelBlock(BlockBehaviour.Properties.of(Material.WOOD)
            .color(MaterialColor.COLOR_BROWN)
            .strength(2.5F)
            .sound(SoundType.WOOD)
            .requiresCorrectToolForDrops()));
    
    public static final RegistryObject<BlockEntityType<FermentationBarrelBlockEntity>> FERMENTATION_BARREL_ENTITY =
        BLOCK_ENTITIES.register("fermentation_barrel", 
            () -> BlockEntityType.Builder.of(FermentationBarrelBlockEntity::new, FERMENTATION_BARREL.get()).build(null));
    
    public static class FermentationBarrelBlock extends Block implements EntityBlock {
        public FermentationBarrelBlock(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (!level.isClientSide) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof FermentationBarrelBlockEntity barrel) {
                    ItemStack heldItem = player.getItemInHand(hand);
                    
                    if (heldItem.isEmpty()) {
                        ItemStack result = barrel.takeResult();
                        if (!result.isEmpty()) {
                            player.addItem(result);
                            player.displayClientMessage(new TextComponent("message.nervedelight.barrel_take"), true);
                        } else {
                            player.displayClientMessage(new TextComponent("message.nervedelight.barrel_empty"), true);
                        }
                        return InteractionResult.SUCCESS;
                    } else if (barrel.canAddItem(heldItem)) {
                        if (barrel.addItem(heldItem)) {
                            heldItem.shrink(1);
                            player.displayClientMessage(new TextComponent("message.nervedelight.barrel_add" + heldItem.getHoverName().getString()), true);
                            if (barrel.isFull()) {
                                player.displayClientMessage(new TextComponent("message.nervedelight.barrel_full"), true);
                                barrel.startFermenting();
                            }
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new FermentationBarrelBlockEntity(pos, state);
        }

        @Override
        public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
            if (!state.is(newState.getBlock())) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof FermentationBarrelBlockEntity barrel) {
                    if (!level.isClientSide) {
                        barrel.dropAllContents();
                    }
                }
                super.onRemove(state, level, pos, newState, isMoving);
            }
        }
    }
    
    public static class FermentationBarrelBlockEntity extends BlockEntity {
        private static final int MAX_SLICES = 5;
        private static final int MAX_SUGAR = 5;
        private static final int FERMENT_TIME = 20 * 20; // 20秒
        
        private int codSliceCount = 0;
        private int salmonSliceCount = 0;
        private int sugarCount = 0;
        private int fermentTime = 0;
        private boolean isFermenting = false;
        
        public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
            super(FERMENTATION_BARREL_ENTITY.get(), pos, state);
        }
        
        public void tick() {
            if (isFermenting && !level.isClientSide) {
                fermentTime--;
                if (fermentTime <= 0) {
                    finishFermenting();
                }
                setChanged();
            }
        }
        
        public boolean canAddItem(ItemStack stack) {
            if (isFermenting) return false;
            
            Item item = stack.getItem();
            if (item == COD_SLICE) {
                return codSliceCount < MAX_SLICES;
            } else if (item == SALMON_SLICE) {
                return salmonSliceCount < MAX_SLICES;
            } else if (item == Items.SUGAR) {
                return sugarCount < MAX_SUGAR;
            }
            return false;
        }
        
        public boolean addItem(ItemStack stack) {
            if (!canAddItem(stack)) return false;
            
            Item item = stack.getItem();
            if (item == COD_SLICE) {
                codSliceCount++;
            } else if (item == SALMON_SLICE) {
                salmonSliceCount++;
            } else if (item == Items.SUGAR) {
                sugarCount++;
            }
            setChanged();
            return true;
        }
        
        public void startFermenting() {
            if ((codSliceCount > 0 || salmonSliceCount > 0) && sugarCount > 0) {
                isFermenting = true;
                fermentTime = FERMENT_TIME;
                setChanged();
            }
        }
        
        private void finishFermenting() {
            if (!level.isClientSide) {
                if (codSliceCount > 0 && sugarCount > 0) {
                    int resultCount = Math.min(codSliceCount, sugarCount);
                    ItemStack result = new ItemStack(ModItem.FERMENTATION_COD_SLICE.get(), resultCount);
                    popOutResult(result);
                    codSliceCount -= resultCount;
                    sugarCount -= resultCount;
                }
                
                if (salmonSliceCount > 0 && sugarCount > 0) {
                    int resultCount = Math.min(salmonSliceCount, sugarCount);
                    ItemStack result = new ItemStack(ModItem.FERMENTATION_SALMON_SLICE.get(), resultCount);
                    popOutResult(result);
                    salmonSliceCount -= resultCount;
                    sugarCount -= resultCount;
                }
                
                isFermenting = false;
                setChanged();
            }
        }
        
        private void popOutResult(ItemStack result) {
            BlockPos pos = getBlockPos();
            level.addFreshEntity(new ItemEntity(level, 
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 
                result));
        }

        public void dropAllContents() {
            if (codSliceCount > 0 && COD_SLICE != null) {
                popOutResult(new ItemStack(COD_SLICE, codSliceCount));
            }
            if (salmonSliceCount > 0 && SALMON_SLICE != null) {
                popOutResult(new ItemStack(SALMON_SLICE, salmonSliceCount));
            }
            if (sugarCount > 0) {
                popOutResult(new ItemStack(Items.SUGAR, sugarCount));
            }
        }
        
        public ItemStack takeResult() {
            if (!isFermenting) {
                if (codSliceCount > 0 && sugarCount > 0) {
                    int resultCount = Math.min(codSliceCount, sugarCount);
                    ItemStack result = new ItemStack(ModItem.FERMENTATION_COD_SLICE.get(), resultCount);
                    codSliceCount -= resultCount;
                    sugarCount -= resultCount;
                    setChanged();
                    return result;
                } else if (salmonSliceCount > 0 && sugarCount > 0) {
                    int resultCount = Math.min(salmonSliceCount, sugarCount);
                    ItemStack result = new ItemStack(ModItem.FERMENTATION_SALMON_SLICE.get(), resultCount);
                    salmonSliceCount -= resultCount;
                    sugarCount -= resultCount;
                    setChanged();
                    return result;
                }
            }
            return ItemStack.EMPTY;
        }
        
        public boolean isFull() {
            return (codSliceCount + salmonSliceCount) >= MAX_SLICES && sugarCount >= MAX_SUGAR;
        }
        
        public int getCodSliceCount() { return codSliceCount; }
        public int getSalmonSliceCount() { return salmonSliceCount; }
        public int getSugarCount() { return sugarCount; }
        
        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            tag.putInt("CodSliceCount", codSliceCount);
            tag.putInt("SalmonSliceCount", salmonSliceCount);
            tag.putInt("SugarCount", sugarCount);
            tag.putInt("FermentTime", fermentTime);
            tag.putBoolean("IsFermenting", isFermenting);
        }
        
        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            codSliceCount = tag.getInt("CodSliceCount");
            salmonSliceCount = tag.getInt("SalmonSliceCount");
            sugarCount = tag.getInt("SugarCount");
            fermentTime = tag.getInt("FermentTime");
            isFermenting = tag.getBoolean("IsFermenting");
        }
    }
}