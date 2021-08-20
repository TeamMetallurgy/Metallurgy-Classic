package com.teammetallurgy.metallurgyclassic.debug;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class OreScannerItem extends Item {

    public OreScannerItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.of("Ore Scanner");
            }

            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new OreScannerScreenHandler(syncId, inv);
            }
        });

//        for(int x = 0; x < 16; x++) {
//            for(int y = 0; y < 64; y++) {
//                for(int z = 0; z < 16; z++) {
//                    BlockPos pos = new BlockPos(user.getChunkPos().x * 16 + x, y, user.getChunkPos().z * 16 + z);
//                    BlockPos pos2 = new BlockPos(user.getChunkPos().x * 16 + x, y + 64, user.getChunkPos().z * 16 + z);
//                    BlockState state = world.getBlockState(pos);
//                    if(state.toString().contains("ore") && state.toString().contains("metallurgy")) {
//                        world.setBlockState(pos2, world.getBlockState(pos));
//                    }
//                }
//            }
//        }

        return TypedActionResult.success(itemStack);
    }
}
