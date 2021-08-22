package com.teammetallurgy.metallurgyclassic.machines.crusher;

import com.teammetallurgy.metallurgyclassic.Constants;
import com.teammetallurgy.metallurgyclassic.MetalRegistry;
import com.teammetallurgy.metallurgyclassic.machines.abstractmachine.*;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public class CrusherBlockEntity extends AbstractMachineEntity<ItemStack, ItemStack> {

    protected final PropertyDelegate propertyDelegate;

    protected CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(CrusherComponent.getEntityType(state.getBlock()), pos, state);
        this.producer = new ItemProducer();
        this.processor = new ItemFuelProcessor();
        this.consumer = new ItemConsumer<>();
        this.addRecipes();

        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> ((ItemConsumer<ItemStack>) CrusherBlockEntity.this.consumer).initialProcessingTime;
                    case 1 -> ((ItemConsumer<ItemStack>) CrusherBlockEntity.this.consumer).currentProcessingTime;
                    case 2 -> ((ItemFuelProcessor) CrusherBlockEntity.this.processor).initialFuelBurnTime;
                    case 3 -> ((ItemFuelProcessor) CrusherBlockEntity.this.processor).currentFuelBurnTime;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ((ItemConsumer<ItemStack>) CrusherBlockEntity.this.consumer).initialProcessingTime = value;
                    case 1 -> ((ItemConsumer<ItemStack>) CrusherBlockEntity.this.consumer).currentProcessingTime = value;
                    case 2 -> ((ItemFuelProcessor) CrusherBlockEntity.this.processor).initialFuelBurnTime = value;
                    case 3 -> ((ItemFuelProcessor) CrusherBlockEntity.this.processor).currentFuelBurnTime = value;
                }
            }

            public int size() {
                return 4;
            }
        };

        this.processor.addStateChangeListener(state1 -> {
            if(world != null) {
                BlockState blockState = world.getBlockState(CrusherBlockEntity.this.getPos());
                if(blockState.getBlock() instanceof CrusherBlock) {
                    world.setBlockState(pos, blockState.with(AbstractFurnaceBlock.LIT, state1), Block.NOTIFY_ALL);
                }
            }
        });
    }


    public void tick() {
        super.tick();
    }

    private void addRecipes() {
        float multiplier = CrusherComponent.getBaseTimeSeconds(this);
        Collection<String> metals = MetalRegistry.instance().metals();
        for(String metal : metals) {
            Item dust = MetalRegistry.instance().getItem(metal, Constants.ItemDust);
            Item tinyDust = MetalRegistry.instance().getItem(metal, Constants.ItemTinyDust);
            addRecipe(MetalRegistry.instance().getItem(metal, Constants.ItemIngot), dust, 1, (int) (20 * multiplier));
            addRecipe(MetalRegistry.instance().getItem(metal, Constants.ItemRawOre), dust, 1, (int) (20 * multiplier));
            addRecipe(MetalRegistry.instance().getBlock(metal, Constants.BlockOre), dust, 2, (int) (20 * multiplier));
            addRecipe(MetalRegistry.instance().getItem(metal, Constants.ItemNugget), tinyDust, 1, (int) (20 * multiplier));
        }
    }

    private void addRecipe(Block input, Item output, int count, int time) {
        if(input == null || output == null || count == 0) return;
        ((ItemConsumer<ItemStack>)this.consumer).addRecipe(new AbstractRecipe<>(new ItemStack(input), new ItemStack(output, count), time));
    }

    private void addRecipe(Item input, Item output, int count, int time) {
        if(input == null || output == null || count == 0) return;
        ((ItemConsumer<ItemStack>)this.consumer).addRecipe(new AbstractRecipe<>(new ItemStack(input), new ItemStack(output, count), time));
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("metallurgyclassic.container.crusher." + CrusherComponent.getType(this).name);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CrusherScreenHandler(CrusherComponent.CRUSHER_SCREEN_HANDLER, syncId, playerInventory, this, propertyDelegate);
    }

    public float getFuelLevel() {
        if(propertyDelegate.get(2) == 0) {
            return 0;
        }
        return propertyDelegate.get(3) / (float) propertyDelegate.get(2);
    }

    public float getProgress() {
        if(propertyDelegate.get(1) == 0) {
            return 0;
        }
        return (propertyDelegate.get(0) - propertyDelegate.get(1)) / (float) propertyDelegate.get(0);
    }
}
