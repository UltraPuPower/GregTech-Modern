package com.gregtechceu.gtceu.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.IUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMufflerMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.GridLayout;
import com.gregtechceu.gtceu.api.ui.container.StackLayout;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.Surface;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.util.SlotGenerator;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import lombok.Getter;

import java.util.stream.IntStream;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/3/8
 * @implNote MufflerPartMachine
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MufflerPartMachine extends TieredPartMachine implements IMufflerMachine, IUIMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(MufflerPartMachine.class,
            MultiblockPartMachine.MANAGED_FIELD_HOLDER);
    @Getter
    private final int recoveryChance;
    @Getter
    @Persisted
    private final CustomItemStackHandler inventory;

    public MufflerPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        this.recoveryChance = Math.max(1, tier * 10);
        this.inventory = new CustomItemStackHandler((int) Math.pow(tier + 1, 2));
    }

    //////////////////////////////////////
    // ***** Initialization ******//

    /// ///////////////////////////////////
    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    //////////////////////////////////////
    // ******** Muffler *********//

    /// ///////////////////////////////////

    @Override
    public void recoverItemsTable(ItemStack... recoveryItems) {
        int numRolls = Math.min(recoveryItems.length, inventory.getSlots());
        IntStream.range(0, numRolls).forEach(slot -> {
            if (calculateChance()) {
                ItemHandlerHelper.insertItemStacked(inventory, recoveryItems[slot].copy(), false);
            }
        });
    }

    private boolean calculateChance() {
        return recoveryChance >= 100 || recoveryChance >= GTValues.RNG.nextInt(100);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick() {
        super.clientTick();
        for (IMultiController controller : getControllers()) {
            if (controller instanceof IRecipeLogicMachine recipeLogicMachine &&
                    recipeLogicMachine.getRecipeLogic().isWorking()) {
                emitPollutionParticles();
                break;
            }
        }
    }

    //////////////////////////////////////
    // ********** GUI ***********//

    /// ///////////////////////////////////


    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        // Position all slots at 0,0 as they'll be moved to the correct position on the client.
        SlotGenerator generator = SlotGenerator.begin(menu::addSlot, 0, 0);
        generator.playerInventory(menu.getPlayerInventory());
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            generator.slot(this.inventory, i, 0, 0);
        }
    }

    @Override
    public void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter, MetaMachine holder) {
        int rowSize = (int) Math.sqrt(inventory.getSlots());
        int xOffset = rowSize == 10 ? 9 : 0;

        var menu = adapter.menu();
        UIComponentGroup rootComponent;
        adapter.rootComponent.child(rootComponent = UIContainers.group(Sizing.fixed(176 + xOffset * 2), Sizing.fixed(18 + 18 * rowSize + 94)));

        rootComponent.surface(Surface.UI_BACKGROUND);
        rootComponent.child(UIComponents.label(getBlockState().getBlock().getName())
                        .positioning(Positioning.absolute(10, 5)))
                .child(UIComponents.playerInventory(player.getInventory(), GuiTextures.SLOT)
                        .positioning(Positioning.absolute(7 + xOffset,
                                18 + 18 * rowSize + 12)));

        GridLayout grid = UIContainers.grid(Sizing.content(), Sizing.content(), rowSize, rowSize);
        for (int y = 0; y < rowSize; y++) {
            for (int x = 0; x < rowSize; x++) {
                int index = y * rowSize + x;
                StackLayout stack = UIContainers.stack(Sizing.fixed(18), Sizing.fixed(18));

                // +36 for player inventory
                stack.child(UIComponents.slot(menu.getSlot(index + 36))
                                .canInsert(true)
                                .canExtract(true)
                                .sizing(Sizing.fill()))
                        .child(UIComponents.texture(GuiTextures.SLOT)
                                .sizing(Sizing.fill()));
                grid.child(stack, x, y);
            }
        }
        rootComponent.child(grid);
    }
}
