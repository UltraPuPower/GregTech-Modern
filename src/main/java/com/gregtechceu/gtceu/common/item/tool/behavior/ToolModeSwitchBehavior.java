package com.gregtechceu.gtceu.common.item.tool.behavior;

import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.item.tool.behavior.IToolBehavior;
import com.gregtechceu.gtceu.api.item.tool.behavior.ToolBehaviorType;
import com.gregtechceu.gtceu.data.tag.GTDataComponents;
import com.gregtechceu.gtceu.data.tools.GTToolBehaviors;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.gregtechceu.gtceu.api.item.tool.ToolHelper.getBehaviorsComponent;

public class ToolModeSwitchBehavior implements IToolBehavior<ToolModeSwitchBehavior> {

    public static final ToolModeSwitchBehavior INSTANCE = new ToolModeSwitchBehavior(ModeType.BOTH);

    public static final Codec<ToolModeSwitchBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WrenchModeType.CODEC.lenientOptionalFieldOf("mode_type", WrenchModeType.BOTH)
                    .forGetter(val -> val.modeType))
            .apply(instance, ToolModeSwitchBehavior::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolModeSwitchBehavior> STREAM_CODEC = StreamCodec
            .composite(
                    ModeType.STREAM_CODEC, ToolModeSwitchBehavior::getModeType,
                    ToolModeSwitchBehavior::new);

    @Override
    public void addBehaviorNBT(@NotNull ItemStack stack, @NotNull CompoundTag tag) {
        var toolTypes = ToolHelper.getToolTypes(stack);
        if (toolTypes.contains(GTToolType.WRENCH)) {
            tag.putByte("Mode", (byte) WrenchModeType.BOTH.ordinal());
        }
        IToolBehavior.super.addBehaviorNBT(stack, tag);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> onItemRightClick(@NotNull Level world, @NotNull Player player,
                                                                        @NotNull InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {

            var toolTypes = ToolHelper.getToolTypes(itemStack);
            if (toolTypes.contains(GTToolType.WRENCH)) {
                tagCompound.putByte("Mode",
                        (byte) ((tagCompound.getByte("Mode") + 1) % WrenchModeType.values().length));
                player.displayClientMessage(Component.translatable("metaitem.machine_configuration.mode",
                        WrenchModeType.values()[tagCompound.getByte("Mode")].getName()), true);
            }
            return InteractionResultHolder.success(itemStack);
        }

        return IToolBehavior.super.onItemRightClick(world, player, hand);
    }

    @Override
    public ToolBehaviorType<ToolModeSwitchBehavior> getType() {
        return GTToolBehaviors.MODE_SWITCH;
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull List<Component> tooltip,
                               @NotNull TooltipFlag flag) {
        var tagCompound = getBehaviorsTag(stack);

        var toolTypes = ToolHelper.getToolTypes(stack);
        if (toolTypes.contains(GTToolType.WRENCH)) {
            tooltip.add(Component.translatable("metaitem.machine_configuration.mode",
                    WrenchModeType.values()[tagCompound.getByte("Mode")].getName()));
        }
    }

    @Getter
    public enum WrenchModeType {

        ITEM("item", Component.translatable("gtceu.mode.item")),
        FLUID("fluid", Component.translatable("gtceu.mode.fluid")),
        BOTH("both", Component.translatable("gtceu.mode.both"));

        public static final Codec<ModeType> CODEC = StringRepresentable.fromEnum(ModeType::values);
        public static final StreamCodec<ByteBuf, ModeType> STREAM_CODEC = ByteBufCodecs.BYTE
                .map(aByte -> ModeType.values()[aByte], val -> (byte) val.ordinal());

        private final Component name;

        WrenchModeType(Component name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return id;
        }
    }
}
