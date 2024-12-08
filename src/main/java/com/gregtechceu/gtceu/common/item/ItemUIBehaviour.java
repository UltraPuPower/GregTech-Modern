package com.gregtechceu.gtceu.common.item;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.api.ui.UIContainer;
import com.gregtechceu.gtceu.api.ui.base.BaseContainerScreen;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.Containers;
import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.GridLayout;
import com.gregtechceu.gtceu.api.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.Flow;

public class ItemUIBehaviour implements IInteractionItem {



    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        if(!player.level().isClientSide) return IInteractionItem.super.use(item, level, player, usedHand);
        var container = new UIContainer(0, player.getInventory());
        var test = new BaseContainerScreen2(container, player.getInventory(), Component.empty());

        Minecraft.getInstance().setScreen(test);
        return IInteractionItem.super.use(item, level, player, usedHand);
    }

    public static class BaseContainerScreen2 extends BaseContainerScreen<FlowLayout, UIContainer> {
        UIContainer container;

        CustomItemStackHandler slot0 = new CustomItemStackHandler(new ItemStack(Items.ITEM_FRAME));
        public BaseContainerScreen2(UIContainer container, Inventory inv, Component component) {
            super(container, inv, component);
            this.container = container;
        }

        @Override
        protected @NotNull UIAdapter<FlowLayout> createAdapter() {
            return UIAdapter.create(this, Containers::horizontalFlow);
        }

        @Override
        protected void build(FlowLayout rootComponent) {

            rootComponent.child(UIComponents.box(Sizing.fixed(130), Sizing.fixed(100))
                    .positioning(Positioning.relative(50, 20)));
            rootComponent.child(UIComponents.item(new ItemStack(Items.ROTTEN_FLESH, 3))
                    .positioning(Positioning.absolute(40, 50))
                    .tooltip(Arrays.asList(Component.translatable("gtceu.alloy_smelter"))));
            var slot = UIComponents.slot(this, slot0, 0);
            //rootComponent.child(slot);
            //rootComponent.child(UIComponents.texture(GuiTextures.SLOT.imageLocation, 0, 0, 18, 18, 18, 18));
            rootComponent.surface(Surface.PANEL);
            container.setAdapter(uiAdapter);
            container.addAllSlots();
                /*rootComponent.child(UIComponents.box(Sizing.fixed(27), Sizing.fixed(48)))
                                .positioning(Positioning.relative(20, 60));*/
                /*rootComponent.child(UIComponents.fluid(new FluidStack(Fluids.WATER, 12_000))
                                .showAmount(true)
                        .positioning(Positioning.absolute(300, 50)));*/
        }
    };
}
