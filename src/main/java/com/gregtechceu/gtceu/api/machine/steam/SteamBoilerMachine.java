package com.gregtechceu.gtceu.api.machine.steam;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.ui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IDataInfoProvider;
import com.gregtechceu.gtceu.api.machine.feature.IExplosionMachine;
import com.gregtechceu.gtceu.api.machine.feature.IUIMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.logic.OCParams;
import com.gregtechceu.gtceu.api.recipe.logic.OCResult;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.api.ui.UIContainerMenu;
import com.gregtechceu.gtceu.api.ui.component.UIComponents;
import com.gregtechceu.gtceu.api.ui.container.UIComponentGroup;
import com.gregtechceu.gtceu.api.ui.container.UIContainers;
import com.gregtechceu.gtceu.api.ui.core.Positioning;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.Surface;
import com.gregtechceu.gtceu.api.ui.core.UIAdapter;
import com.gregtechceu.gtceu.api.ui.serialization.SyncedProperty;
import com.gregtechceu.gtceu.api.ui.texture.ProgressTexture;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.PortableScannerBehavior;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTTransferUtils;

import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/3/14
 * @implNote SteamBoilerMachine
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class SteamBoilerMachine extends SteamWorkableMachine
                                         implements IUIMachine, IExplosionMachine, IDataInfoProvider {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(SteamBoilerMachine.class,
            SteamWorkableMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    public final NotifiableFluidTank waterTank;
    @Persisted
    @DescSynced
    @Getter
    private int currentTemperature;
    @Persisted
    @Getter
    private int timeBeforeCoolingDown;
    @Getter
    private boolean hasNoWater;
    @Nullable
    protected TickableSubscription temperatureSubs, autoOutputSubs;
    @Nullable
    protected ISubscription steamTankSubs;

    public SteamBoilerMachine(IMachineBlockEntity holder, boolean isHighPressure, Object... args) {
        super(holder, isHighPressure, args);
        this.waterTank = createWaterTank(args);
        this.waterTank.setFilter(fluid -> fluid.getFluid().is(GTMaterials.Water.getFluidTag()));
    }

    //////////////////////////////////////
    // ***** Initialization *****//
    //////////////////////////////////////
    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected NotifiableFluidTank createSteamTank(Object... args) {
        return new NotifiableFluidTank(this, 1, 16 * FluidType.BUCKET_VOLUME, IO.OUT);
    }

    protected NotifiableFluidTank createWaterTank(@SuppressWarnings("unused") Object... args) {
        return new NotifiableFluidTank(this, 1, 16 * FluidType.BUCKET_VOLUME, IO.IN);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::updateAutoOutputSubscription));
        }
        updateSteamSubscription();
        steamTankSubs = steamTank.addChangedListener(this::updateAutoOutputSubscription);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (steamTankSubs != null) {
            steamTankSubs.unsubscribe();
            steamTankSubs = null;
        }
    }

    @Override
    public void setOutputFacing(@NotNull Direction outputFacing) {
        // no op - boilers do not have output facings
    }

    //////////////////////////////////////
    // ******* Auto Output *******//
    //////////////////////////////////////

    @Override
    public void onNeighborChanged(Block block, BlockPos fromPos, boolean isMoving) {
        super.onNeighborChanged(block, fromPos, isMoving);
        updateAutoOutputSubscription();
    }

    protected void updateAutoOutputSubscription() {
        if (Direction.stream().filter(direction -> direction != getFrontFacing() && direction != Direction.DOWN)
                .anyMatch(direction -> GTTransferUtils.hasAdjacentFluidHandler(getLevel(), getPos(), direction))) {
            autoOutputSubs = subscribeServerTick(autoOutputSubs, this::autoOutput);
        } else if (autoOutputSubs != null) {
            autoOutputSubs.unsubscribe();
            autoOutputSubs = null;
        }
    }

    protected void autoOutput() {
        if (getOffsetTimer() % 5 == 0) {
            steamTank.exportToNearby(Direction.stream()
                    .filter(direction -> direction != getFrontFacing() && direction != Direction.DOWN)
                    .filter(direction -> GTTransferUtils.hasAdjacentFluidHandler(getLevel(), getPos(), direction))
                    .toArray(Direction[]::new));
            updateAutoOutputSubscription();
        }
    }

    //////////////////////////////////////
    // ****** Recipe Logic ******//
    //////////////////////////////////////

    protected void updateSteamSubscription() {
        if (currentTemperature > 0) {
            temperatureSubs = subscribeServerTick(temperatureSubs, this::updateCurrentTemperature);
        } else if (temperatureSubs != null) {
            temperatureSubs.unsubscribe();
            temperatureSubs = null;
        }
    }

    protected void updateCurrentTemperature() {
        if (recipeLogic.isWorking()) {
            if (getOffsetTimer() % 12 == 0) {
                if (currentTemperature < getMaxTemperature())
                    if (isHighPressure) {
                        currentTemperature++;
                    } else if (getOffsetTimer() % 24 == 0) {
                        currentTemperature++;
                    }
            }
        } else if (timeBeforeCoolingDown == 0) {
            if (currentTemperature > 0) {
                currentTemperature -= getCoolDownRate();
                timeBeforeCoolingDown = getCooldownInterval();
            }
        } else--timeBeforeCoolingDown;

        if (getOffsetTimer() % 10 == 0) {
            if (currentTemperature >= 100) {
                int fillAmount = (int) (getBaseSteamOutput() * ((float) currentTemperature / getMaxTemperature()) / 2);
                boolean hasDrainedWater = !waterTank.drainInternal(1, FluidAction.EXECUTE).isEmpty();
                var filledSteam = 0L;
                if (hasDrainedWater) {
                    filledSteam = steamTank.fillInternal(
                            GTMaterials.Steam.getFluid(fillAmount),
                            FluidAction.EXECUTE);
                }
                if (this.hasNoWater && hasDrainedWater) {
                    doExplosion(2.0f);
                } else this.hasNoWater = !hasDrainedWater;
                if (filledSteam == 0 && hasDrainedWater && getLevel() instanceof ServerLevel serverLevel) {
                    final float x = getPos().getX() + 0.5F;
                    final float y = getPos().getY() + 0.5F;
                    final float z = getPos().getZ() + 0.5F;

                    serverLevel.sendParticles(ParticleTypes.CLOUD,
                            x + getFrontFacing().getStepX() * 0.6,
                            y + getFrontFacing().getStepY() * 0.6,
                            z + getFrontFacing().getStepZ() * 0.6,
                            7 + GTValues.RNG.nextInt(3),
                            getFrontFacing().getStepX() / 2.0,
                            getFrontFacing().getStepY() / 2.0,
                            getFrontFacing().getStepZ() / 2.0, 0.1);

                    if (ConfigHolder.INSTANCE.machines.machineSounds) {
                        getLevel().playSound(null, x, y, z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0f,
                                1.0f);
                    }

                    // bypass capability check for special case behavior
                    steamTank.drainInternal(FluidType.BUCKET_VOLUME * 4, FluidAction.EXECUTE);
                }
            } else this.hasNoWater = false;
        }
        updateSteamSubscription();
    }

    protected int getCooldownInterval() {
        return isHighPressure ? 40 : 45;
    }

    @SuppressWarnings("MethodMayBeStatic")
    protected int getCoolDownRate() {
        return 1;
    }

    public int getMaxTemperature() {
        return isHighPressure ? 1000 : 500;
    }

    private double getTemperaturePercent() {
        return currentTemperature / (getMaxTemperature() * 1.0);
    }

    protected abstract long getBaseSteamOutput();

    @Nullable
    public static GTRecipe recipeModifier(MetaMachine machine, @NotNull GTRecipe recipe, @NotNull OCParams params,
                                          @NotNull OCResult result) {
        if (machine instanceof SteamBoilerMachine boilerMachine) {
            recipe = recipe.copy();
            result.init(0, recipe.duration, params.getOcAmount());
            if (boilerMachine.isHighPressure)
                result.setDuration(result.getDuration() / 2);
            // recipe.duration *= 12; // maybe?
            return recipe;
        }
        return null;
    }

    @Override
    public boolean onWorking() {
        boolean value = super.onWorking();
        if (currentTemperature < getMaxTemperature()) {
            currentTemperature = Math.max(1, currentTemperature);
            updateSteamSubscription();
        }
        return value;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        this.timeBeforeCoolingDown = getCooldownInterval();
    }

    //////////////////////////////////////
    // ******* Interaction *******//
    //////////////////////////////////////

    @Override
    protected InteractionResult onSoftMalletClick(Player playerIn, InteractionHand hand, Direction gridSide,
                                                  BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }

    //////////////////////////////////////
    // ********** GUI ***********//
    //////////////////////////////////////

    @Override
    public void loadServerUI(Player player, UIContainerMenu<MetaMachine> menu, MetaMachine holder) {
        for (int i = 0; i < this.waterTank.getTanks(); i++) {
            SyncedProperty<FluidStack> prop = menu.createProperty(FluidStack.class, "water." + i,
                    this.waterTank.getFluidInTank(i));
            CustomFluidTank tank = this.waterTank.getStorages()[i];
            tank.addOnContentsChanged(() -> prop.set(tank.getFluid()));
        }

        var progressProperty = menu.createProperty(double.class, "progress", recipeLogic.getProgressPercent());
        recipeLogic.addProgressPercentListener(progressProperty::set);
    }

    @Override
    public void loadClientUI(Player player, UIAdapter<UIComponentGroup> adapter, MetaMachine holder) {
        var menu = adapter.menu();
        UIComponentGroup group;
        adapter.rootComponent.child(group = UIContainers.group(Sizing.fixed(176), Sizing.fixed(166)));

        group.surface(isHighPressure ? Surface.UI_BACKGROUND_STEEL : Surface.UI_BACKGROUND_BRONZE);
        group.child(UIComponents.label(getBlockState().getBlock().getName())
                .positioning(Positioning.absolute(6, 6)))
                .child(UIComponents.progress(menu.<Double>getProperty("progress")::get)
                        .fillDirection(ProgressTexture.FillDirection.DOWN_TO_UP)
                        .progressTexture(GuiTextures.PROGRESS_BAR_BOILER_EMPTY.get(isHighPressure), GuiTextures.PROGRESS_BAR_BOILER_HEAT)
                        .dynamicHoverTips(pct -> Component.translatable("gtceu.multiblock.large_boiler.temperature",
                                currentTemperature + 274, getMaxTemperature() + 274))
                        .positioning(Positioning.absolute(96, 26))
                        .sizing(Sizing.fixed(10), Sizing.fixed(54)))
                .child(UIComponents.tank(waterTank.getStorages()[0], 0)
                        .canInsert(true)
                        .canExtract(false)
                        .showAmount(false)
                        .fillDirection(ProgressTexture.FillDirection.DOWN_TO_UP)
                        .positioning(Positioning.absolute(83, 26))
                        .sizing(Sizing.fixed(10), Sizing.fixed(54)))
                .child(UIComponents.texture(GuiTextures.PROGRESS_BAR_BOILER_EMPTY.get(isHighPressure))
                        .positioning(Positioning.absolute(83, 26))
                        .sizing(Sizing.fixed(10), Sizing.fixed(54)))
                .child(UIComponents.tank(steamTank.getStorages()[0], 0)
                        .canInsert(false)
                        .canExtract(true)
                        .showAmount(false)
                        .fillDirection(ProgressTexture.FillDirection.DOWN_TO_UP)
                        .positioning(Positioning.absolute(70, 26))
                        .sizing(Sizing.fixed(10), Sizing.fixed(54)))
                .child(UIComponents.texture(GuiTextures.PROGRESS_BAR_BOILER_EMPTY.get(isHighPressure))
                        .positioning(Positioning.absolute(70, 26))
                        .sizing(Sizing.fixed(10), Sizing.fixed(54)))
                .child(UIComponents.texture(GuiTextures.CANISTER_OVERLAY_STEAM.get(isHighPressure))
                        .positioning(Positioning.absolute(43, 44)))
                .child(UIComponents.playerInventory(player.getInventory(), GuiTextures.SLOT_STEAM.get(isHighPressure))
                        .positioning(Positioning.absolute(7, 84)));
    }

    //////////////////////////////////////
    // ********* Client *********//
    //////////////////////////////////////

    @Override
    public void animateTick(RandomSource random) {
        if (isActive()) {
            final BlockPos pos = getPos();
            float x = pos.getX() + 0.5F;
            float z = pos.getZ() + 0.5F;

            final var facing = getFrontFacing();
            final float horizontalOffset = random.nextFloat() * 0.6F - 0.3F;
            final float y = pos.getY() + random.nextFloat() * 0.375F;

            if (facing.getAxis() == Direction.Axis.X) {
                if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) x += 0.52F;
                else x -= 0.52F;
                z += horizontalOffset;
            } else if (facing.getAxis() == Direction.Axis.Z) {
                if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) z += 0.52F;
                else z -= 0.52F;
                x += horizontalOffset;
            }
            randomDisplayTick(random, x, y, z);
        }
    }

    protected void randomDisplayTick(RandomSource random, float x, float y, float z) {
        getLevel().addParticle(isHighPressure ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
        getLevel().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
    }

    @NotNull
    @Override
    public List<Component> getDataInfo(PortableScannerBehavior.DisplayMode mode) {
        if (mode == PortableScannerBehavior.DisplayMode.SHOW_ALL ||
                mode == PortableScannerBehavior.DisplayMode.SHOW_MACHINE_INFO) {
            return Collections.singletonList(Component.translatable("gtceu.machine.steam_boiler.heat_amount",
                    FormattingUtil.formatNumbers((int) (getTemperaturePercent() * 100))));
        }
        return new ArrayList<>();
    }

}
