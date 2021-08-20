package com.teammetallurgy.metallurgyclassic.debug;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.MOD_ID;
import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;

public class OreScannerKeybind {
    public static ScreenHandlerType<OreScannerScreenHandler> ORE_SCANNER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("ore_scanner"), OreScannerScreenHandler::new);

    public static void setup() {
        ScreenRegistry.register(ORE_SCANNER_SCREEN_HANDLER, OreScannerScreen::new);
        var keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                MOD_ID + ".openscreen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category." + MOD_ID
                ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(keyBinding.wasPressed()) {
                System.out.println("Key was pressed");

                client.player.openHandledScreen(new NamedScreenHandlerFactory() {
                    @Override
                    public Text getDisplayName() {
                        return Text.of("Ore Scanner");
                    }

                    @Nullable
                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return new OreScannerScreenHandler(ORE_SCANNER_SCREEN_HANDLER, syncId);
                    }
                });
            }
        });
    }
}
