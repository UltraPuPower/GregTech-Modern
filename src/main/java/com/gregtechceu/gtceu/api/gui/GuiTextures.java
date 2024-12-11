package com.gregtechceu.gtceu.api.gui;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.ui.texture.NinePatchTexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;

import com.gregtechceu.gtceu.api.ui.texture.ResourceTexture;

/**
 * @author KilaBash
 * @date 2023/2/22
 * @implNote GuiTextures
 */
@SuppressWarnings("unused")
public class GuiTextures {

    // GREGTECH
    public static final ResourceTexture GREGTECH_LOGO = UITextures.resource(
            GTCEu.id("textures/gui/icon/gregtech_logo.png"));
    public static final ResourceTexture GREGTECH_LOGO_XMAS = UITextures.resource(
            GTCEu.id("textures/gui/icon/gregtech_logo_xmas.png"));

    // HUD
    public static final ResourceTexture TOOL_FRONT_FACING_ROTATION = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_front_facing_rotation.png"));
    public static final ResourceTexture TOOL_IO_FACING_ROTATION = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_io_facing_rotation.png"));
    public static final ResourceTexture TOOL_PAUSE = UITextures.resource(GTCEu.id("textures/gui/overlay/tool_pause.png"));
    public static final ResourceTexture TOOL_START = UITextures.resource(GTCEu.id("textures/gui/overlay/tool_start.png"));
    public static final ResourceTexture TOOL_COVER_SETTINGS = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_cover_settings.png"));
    public static final ResourceTexture TOOL_MUTE = UITextures.resource(GTCEu.id("textures/gui/overlay/tool_mute.png"));
    public static final ResourceTexture TOOL_SOUND = UITextures.resource(GTCEu.id("textures/gui/overlay/tool_sound.png"));
    public static final ResourceTexture TOOL_ALLOW_INPUT = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_allow_input.png"));
    public static final ResourceTexture TOOL_ATTACH_COVER = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_attach_cover.png"));
    public static final ResourceTexture TOOL_REMOVE_COVER = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_remove_cover.png"));
    public static final ResourceTexture TOOL_PIPE_BLOCK = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_pipe_block.png"));
    public static final ResourceTexture TOOL_PIPE_CONNECT = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_pipe_connect.png"));
    public static final ResourceTexture TOOL_WIRE_BLOCK = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_wire_block.png"));
    public static final ResourceTexture TOOL_WIRE_CONNECT = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_wire_connect.png"));
    public static final ResourceTexture TOOL_AUTO_OUTPUT = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_auto_output.png"));
    public static final ResourceTexture TOOL_DISABLE_AUTO_OUTPUT = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_disable_auto_output.png"));
    // todo switch to tool_switch_converter_eu once that gets made
    public static final ResourceTexture TOOL_SWITCH_CONVERTER_NATIVE = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_wire_block.png"));
    // todo switch to tool_switch_converter_eu once that gets made
    public static final ResourceTexture TOOL_SWITCH_CONVERTER_EU = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_wire_connect.png"));

    // BASE TEXTURES
    public static final NinePatchTexture BACKGROUND = NinePatchTexture.get(GTCEu.id("background"));
    public static final NinePatchTexture BACKGROUND_INVERSE = NinePatchTexture.get(GTCEu.id("background_inverse"));
    public static final SteamTexture BACKGROUND_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/base/background_%s.png"));
    public static final ResourceTexture CLIPBOARD_BACKGROUND = UITextures.resource(
            GTCEu.id("textures/gui/base/clipboard_background.png"));
    public static final ResourceTexture CLIPBOARD_PAPER_BACKGROUND = UITextures.resource(
            GTCEu.id("textures/gui/base/clipboard_paper_background.png"));
    public static final NinePatchTexture TITLE_BAR_BACKGROUND = NinePatchTexture.get(GTCEu.id("title_bar_background"));


    public static final ResourceTexture DISPLAY = UITextures.resource(GTCEu.id("textures/gui/base/display.png"));
    public static final SteamTexture DISPLAY_STEAM = SteamTexture.fullImage(GTCEu.id("textures/gui/base/display_%s.png"));
    public static final NinePatchTexture BLANK = NinePatchTexture.get(GTCEu.id("blank"));
    public static final NinePatchTexture BLANK_TRANSPARENT = NinePatchTexture.get(GTCEu.id("blank_transparent"));
    public static final NinePatchTexture FLUID_SLOT = NinePatchTexture.get(GTCEu.id("fluid_slot"));
    public static final ResourceTexture FLUID_TANK_BACKGROUND = UITextures.resource(
            GTCEu.id("textures/gui/base/fluid_tank_background.png"));
    public static final ResourceTexture FLUID_TANK_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/base/fluid_tank_overlay.png"));
    public static final NinePatchTexture SLOT = NinePatchTexture.get(GTCEu.id("textures/gui/base/slot.png"));
    public static final NinePatchTexture SLOT_DARK = NinePatchTexture.get(GTCEu.id("slot_dark"));

    public static final ResourceTexture SLOT_DARKENED = UITextures.resource(
            GTCEu.id("textures/gui/base/darkened_slot.png"));
    public static final SteamTexture SLOT_STEAM = SteamTexture.fullImage(GTCEu.id("textures/gui/base/slot_%s.png"));
    public static final ResourceTexture TOGGLE_BUTTON_BACK = UITextures.resource(
            GTCEu.id("textures/gui/widget/toggle_button_background.png"));

    public static final ResourceTexture CLOSE_ICON = UITextures.resource(GTCEu.id("textures/gui/icon/close.png"));

    // FLUID & ITEM OUTPUT BUTTONS
    public static final ResourceTexture BLOCKS_INPUT = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_blocks_input.png"));
    public static final NinePatchTexture BUTTON = NinePatchTexture.get(GTCEu.id("button"));
    public static final ResourceTexture BUTTON_ALLOW_IMPORT_EXPORT = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_allow_import_export.png"));
    public static final ResourceTexture BUTTON_BLACKLIST = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_blacklist.png"));
    public static final ResourceTexture BUTTON_CHUNK_MODE = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_chunk_mode.png"));
    public static final ResourceTexture BUTTON_CLEAR_GRID = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_clear_grid.png"));
    public static final ResourceTexture BUTTON_FILTER_DAMAGE = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_filter_damage.png"));
    public static final ResourceTexture BUTTON_DISTINCT_BUSES = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_distinct_buses.png"));
    public static final ResourceTexture BUTTON_POWER = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_power.png"));
    public static final ResourceTexture BUTTON_FILTER_NBT = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_filter_nbt.png"));
    public static final ResourceTexture BUTTON_FLUID_OUTPUT = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_fluid_output_overlay.png"));
    public static final ResourceTexture BUTTON_ITEM_OUTPUT = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_item_output_overlay.png"));
    public static final ResourceTexture BUTTON_LOCK = UITextures.resource(GTCEu.id("textures/gui/widget/button_lock.png"));
    public static final ResourceTexture BUTTON_VOID = UITextures.resource(GTCEu.id("textures/gui/widget/button_void.png"));
    public static final ResourceTexture BUTTON_VOID_PARTIAL = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_void_partial.png"));
    public static final ResourceTexture BUTTON_VOID_MULTIBLOCK = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_void_multiblock.png"));
    public static final ResourceTexture BUTTON_LEFT = UITextures.resource(GTCEu.id("textures/gui/widget/left.png"));
    public static final ResourceTexture BUTTON_PUBLIC_PRIVATE = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_public_private.png"));
    public static final ResourceTexture BUTTON_RIGHT = UITextures.resource(GTCEu.id("textures/gui/widget/right.png"));
    public static final ResourceTexture BUTTON_SILK_TOUCH_MODE = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_silk_touch_mode.png"));
    public static final ResourceTexture BUTTON_SWITCH_VIEW = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_switch_view.png"));
    public static final ResourceTexture BUTTON_WORKING_ENABLE = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_working_enable.png"));
    public static final ResourceTexture BUTTON_INT_CIRCUIT_PLUS = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_circuit_plus.png"));
    public static final ResourceTexture BUTTON_INT_CIRCUIT_MINUS = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_circuit_minus.png"));
    public static final ResourceTexture CLIPBOARD_BUTTON = UITextures.resource(
            GTCEu.id("textures/gui/widget/clipboard_button.png"));
    public static final NinePatchTexture CLIPBOARD_TEXT_BOX = NinePatchTexture.get(GTCEu.id("clipboard_text_box"));
    public static final ResourceTexture DISTRIBUTION_MODE = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_distribution_mode.png"));
    public static final ResourceTexture BUTTON_AUTO_PULL = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_me_auto_pull.png"));
    public static final ResourceTexture LOCK = UITextures.resource(GTCEu.id("textures/gui/widget/lock.png"));
    public static final ResourceTexture LOCK_WHITE = UITextures.resource(GTCEu.id("textures/gui/widget/lock_white.png"));
    public static final ResourceTexture SWITCH = UITextures.resource(GTCEu.id("textures/gui/widget/switch.png"));
    public static final ResourceTexture SWITCH_HORIZONTAL = UITextures.resource(
            GTCEu.id("textures/gui/widget/switch_horizontal.png"));
    public static final NinePatchTexture VANILLA_BUTTON = NinePatchTexture.get(GTCEu.id("vanilla_button"));

    public static final ResourceTexture ENERGY_DETECTOR_COVER_MODE_BUTTON = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_detector_cover_energy_mode.png"));
    public static final ResourceTexture INVERT_REDSTONE_BUTTON = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_detector_cover_inverted.png"));

    public static final ResourceTexture IO_CONFIG_FLUID_MODES_BUTTON = UITextures.resource(
            GTCEu.id("textures/gui/icon/io_config/output_config_fluid_modes.png"));
    public static final ResourceTexture IO_CONFIG_ITEM_MODES_BUTTON = UITextures.resource(
            GTCEu.id("textures/gui/icon/io_config/output_config_item_modes.png"));
    public static final ResourceTexture IO_CONFIG_COVER_SLOT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/icon/io_config/cover_slot_overlay.png"));
    public static final ResourceTexture IO_CONFIG_COVER_SETTINGS = UITextures.resource(
            GTCEu.id("textures/gui/icon/io_config/cover_settings.png"));

    public static final ResourceTexture PATTERN_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/widget/pattern_overlay.png"));
    public static final ResourceTexture REFUND_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/widget/refund_overlay.png"));
    // INDICATORS & ICONS
    public static final ResourceTexture INDICATOR_NO_ENERGY = UITextures.resource(
            GTCEu.id("textures/gui/base/indicator_no_energy.png"));
    public static final SteamTexture INDICATOR_NO_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/base/indicator_no_steam_%s.png"));
    public static final ResourceTexture TANK_ICON = UITextures.resource(GTCEu.id("textures/gui/base/tank_icon.png"));

    // WIDGET UI RELATED
    public static final ResourceTexture SLIDER_BACKGROUND = UITextures.resource(
            GTCEu.id("textures/gui/widget/slider_background.png"));
    public static final ResourceTexture SLIDER_BACKGROUND_VERTICAL = UITextures.resource(
            GTCEu.id("textures/gui/widget/slider_background_vertical.png"));
    public static final ResourceTexture SLIDER_ICON = UITextures.resource(GTCEu.id("textures/gui/widget/slider.png"));
    public static final ResourceTexture MAINTENANCE_BUTTON = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_maintenance.png"));
    public static final ResourceTexture MAINTENANCE_ICON = UITextures.resource(
            GTCEu.id("textures/block/overlay/machine/overlay_maintenance.png"));
    public static final ResourceTexture BUTTON_MINER_MODES = UITextures.resource(
            GTCEu.id("textures/gui/widget/button_miner_modes.png"));

    // ORE PROCESSING
    public static final ResourceTexture OREBY_BASE = UITextures.resource(GTCEu.id("textures/gui/arrows/oreby-base.png"));
    public static final ResourceTexture OREBY_CHEM = UITextures.resource(GTCEu.id("textures/gui/arrows/oreby-chem.png"));
    public static final ResourceTexture OREBY_SEP = UITextures.resource(GTCEu.id("textures/gui/arrows/oreby-sep.png"));
    public static final ResourceTexture OREBY_SIFT = UITextures.resource(GTCEu.id("textures/gui/arrows/oreby-sift.png"));
    public static final ResourceTexture OREBY_SMELT = UITextures.resource(GTCEu.id("textures/gui/arrows/oreby-smelt.png"));

    // PRIMITIVE
    public static final NinePatchTexture PRIMITIVE_BACKGROUND = NinePatchTexture.get(GTCEu.id("primitive_background"));
    public static final NinePatchTexture PRIMITIVE_SLOT = NinePatchTexture.get(GTCEu.id("primitive_slot"));
    public static final ResourceTexture PRIMITIVE_FURNACE_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/primitive/overlay_primitive_furnace.png"));
    public static final ResourceTexture PRIMITIVE_DUST_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/primitive/overlay_primitive_dust.png"));
    public static final ResourceTexture PRIMITIVE_INGOT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/primitive/overlay_primitive_ingot.png"));
    public static final ResourceTexture PRIMITIVE_LARGE_FLUID_TANK = UITextures.resource(
            GTCEu.id("textures/gui/primitive/primitive_large_fluid_tank.png"));
    public static final ResourceTexture PRIMITIVE_LARGE_FLUID_TANK_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/primitive/primitive_large_fluid_tank_overlay.png"));
    public static final ResourceTexture PRIMITIVE_BLAST_FURNACE_PROGRESS_BAR = UITextures.resource(
            GTCEu.id("textures/gui/primitive/progress_bar_primitive_blast_furnace.png"));

    // SLOT OVERLAYS
    public static final ResourceTexture ATOMIC_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/atomic_overlay_1.png"));
    public static final ResourceTexture ATOMIC_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/atomic_overlay_2.png"));
    public static final ResourceTexture ARROW_INPUT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/arrow_input_overlay.png"));
    public static final ResourceTexture ARROW_OUTPUT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/arrow_output_overlay.png"));
    public static final ResourceTexture BATTERY_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/battery_overlay.png"));
    public static final ResourceTexture BEAKER_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/beaker_overlay_1.png"));
    public static final ResourceTexture BEAKER_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/beaker_overlay_2.png"));
    public static final ResourceTexture BEAKER_OVERLAY_3 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/beaker_overlay_3.png"));
    public static final ResourceTexture BEAKER_OVERLAY_4 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/beaker_overlay_4.png"));
    public static final ResourceTexture BENDER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/bender_overlay.png"));
    public static final ResourceTexture BOX_OVERLAY = UITextures.resource(GTCEu.id("textures/gui/overlay/box_overlay.png"));
    public static final ResourceTexture BOXED_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/boxed_overlay.png"));
    public static final ResourceTexture BREWER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/brewer_overlay.png"));
    public static final ResourceTexture CANNER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/canner_overlay.png"));
    public static final ResourceTexture CHARGER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/charger_slot_overlay.png"));
    public static final ResourceTexture CANISTER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/canister_overlay.png"));
    public static final SteamTexture CANISTER_OVERLAY_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/overlay/canister_overlay_%s.png"));
    public static final ResourceTexture CENTRIFUGE_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/centrifuge_overlay.png"));
    public static final ResourceTexture CIRCUIT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/circuit_overlay.png"));
    public static final SteamTexture COAL_OVERLAY_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/overlay/coal_overlay_%s.png"));
    public static final ResourceTexture COMPRESSOR_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/compressor_overlay.png"));
    public static final SteamTexture COMPRESSOR_OVERLAY_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/overlay/compressor_overlay_%s.png"));
    public static final ResourceTexture CRACKING_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/cracking_overlay_1.png"));
    public static final ResourceTexture CRACKING_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/cracking_overlay_2.png"));
    public static final ResourceTexture CRUSHED_ORE_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/crushed_ore_overlay.png"));
    public static final SteamTexture CRUSHED_ORE_OVERLAY_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/overlay/crushed_ore_overlay_%s.png"));
    public static final ResourceTexture CRYSTAL_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/crystal_overlay.png"));
    public static final ResourceTexture CUTTER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/cutter_overlay.png"));
    public static final ResourceTexture DARK_CANISTER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/dark_canister_overlay.png"));
    public static final ResourceTexture DUST_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/dust_overlay.png"));
    public static final SteamTexture DUST_OVERLAY_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/overlay/dust_overlay_%s.png"));
    public static final ResourceTexture EXTRACTOR_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/extractor_overlay.png"));
    public static final SteamTexture EXTRACTOR_OVERLAY_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/overlay/extractor_overlay_%s.png"));
    public static final ResourceTexture FILTER_SLOT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/filter_slot_overlay.png"));
    public static final ResourceTexture FURNACE_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/furnace_overlay_1.png"));
    public static final ResourceTexture FURNACE_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/furnace_overlay_2.png"));
    public static final SteamTexture FURNACE_OVERLAY_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/overlay/furnace_overlay_%s.png"));
    public static final ResourceTexture HAMMER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/hammer_overlay.png"));
    public static final SteamTexture HAMMER_OVERLAY_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/overlay/hammer_overlay_%s.png"));
    public static final ResourceTexture HEATING_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/heating_overlay_1.png"));
    public static final ResourceTexture HEATING_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/heating_overlay_2.png"));
    public static final ResourceTexture IMPLOSION_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/implosion_overlay_1.png"));
    public static final ResourceTexture IMPLOSION_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/implosion_overlay_2.png"));
    public static final ResourceTexture IN_SLOT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/in_slot_overlay.png"));
    public static final SteamTexture IN_SLOT_OVERLAY_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/overlay/in_slot_overlay_%s.png"));
    public static final ResourceTexture INGOT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/ingot_overlay.png"));
    public static final ResourceTexture INT_CIRCUIT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/int_circuit_overlay.png"));
    public static final ResourceTexture LENS_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/lens_overlay.png"));
    public static final ResourceTexture LIGHTNING_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/lightning_overlay_1.png"));
    public static final ResourceTexture LIGHTNING_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/lightning_overlay_2.png"));
    public static final ResourceTexture MOLD_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/mold_overlay.png"));
    public static final ResourceTexture MOLECULAR_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/molecular_overlay_1.png"));
    public static final ResourceTexture MOLECULAR_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/molecular_overlay_2.png"));
    public static final ResourceTexture MOLECULAR_OVERLAY_3 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/molecular_overlay_3.png"));
    public static final ResourceTexture MOLECULAR_OVERLAY_4 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/molecular_overlay_4.png"));
    public static final ResourceTexture OUT_SLOT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/out_slot_overlay.png"));
    public static final SteamTexture OUT_SLOT_OVERLAY_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/overlay/out_slot_overlay_%s.png"));
    public static final ResourceTexture PAPER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/paper_overlay.png"));
    public static final ResourceTexture PRINTED_PAPER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/printed_paper_overlay.png"));
    public static final ResourceTexture PIPE_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/pipe_overlay_2.png"));
    public static final ResourceTexture PIPE_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/pipe_overlay_1.png"));
    public static final ResourceTexture PRESS_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/press_overlay_1.png"));
    public static final ResourceTexture PRESS_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/press_overlay_2.png"));
    public static final ResourceTexture PRESS_OVERLAY_3 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/press_overlay_3.png"));
    public static final ResourceTexture PRESS_OVERLAY_4 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/press_overlay_4.png"));
    public static final ResourceTexture SAWBLADE_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/sawblade_overlay.png"));
    public static final ResourceTexture SOLIDIFIER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/solidifier_overlay.png"));
    public static final ResourceTexture STRING_SLOT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/string_slot_overlay.png"));
    public static final ResourceTexture TOOL_SLOT_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/tool_slot_overlay.png"));
    public static final ResourceTexture TURBINE_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/turbine_overlay.png"));
    public static final ResourceTexture VIAL_OVERLAY_1 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/vial_overlay_1.png"));
    public static final ResourceTexture VIAL_OVERLAY_2 = UITextures.resource(
            GTCEu.id("textures/gui/overlay/vial_overlay_2.png"));
    public static final ResourceTexture WIREMILL_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/wiremill_overlay.png"));
    public static final ResourceTexture POSITIVE_MATTER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/positive_matter_overlay.png"));
    public static final ResourceTexture NEUTRAL_MATTER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/neutral_matter_overlay.png"));
    public static final ResourceTexture DATA_ORB_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/data_orb_overlay.png"));
    public static final ResourceTexture SCANNER_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/scanner_overlay.png"));
    public static final ResourceTexture DUCT_TAPE_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/duct_tape_overlay.png"));
    public static final ResourceTexture RESEARCH_STATION_OVERLAY = UITextures.resource(
            GTCEu.id("textures/gui/overlay/research_station_overlay.png"));

    // PROGRESS BARS
    public static final ResourceTexture PROGRESS_BAR_ARC_FURNACE = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_arc_furnace.png"));
    public static final ResourceTexture PROGRESS_BAR_ARROW = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_arrow.png"));
    public static final SteamTexture PROGRESS_BAR_ARROW_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/progress_bar/progress_bar_arrow_%s.png"));
    public static final ResourceTexture PROGRESS_BAR_ARROW_MULTIPLE = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_arrow_multiple.png"));
    public static final ResourceTexture PROGRESS_BAR_ASSEMBLER = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_assembler.png"));

    public static final ResourceTexture PROGRESS_BAR_ASSEMBLY_LINE = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_assembly_line.png"));
    public static final ResourceTexture PROGRESS_BAR_ASSEMBLY_LINE_ARROW = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_assembly_line_arrow.png"));
    public static final ResourceTexture PROGRESS_BAR_BATH = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_bath.png"));
    public static final ResourceTexture PROGRESS_BAR_BENDING = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_bending.png"));
    public static final SteamTexture PROGRESS_BAR_BOILER_EMPTY = SteamTexture
            .fullImage(GTCEu.id("textures/gui/progress_bar/progress_bar_boiler_empty_%s.png"));
    public static final SteamTexture PROGRESS_BAR_BOILER_FUEL = SteamTexture
            .fullImage(GTCEu.id("textures/gui/progress_bar/progress_bar_boiler_fuel_%s.png"));
    public static final ResourceTexture PROGRESS_BAR_BOILER_HEAT = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_boiler_heat.png"));
    public static final ResourceTexture PROGRESS_BAR_CANNER = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_canner.png"));
    public static final ResourceTexture PROGRESS_BAR_CIRCUIT = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_circuit_assembler.png"));
    public static final ResourceTexture PROGRESS_BAR_CIRCUIT_ASSEMBLER = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_circuit_assembler.png"));
    public static final ResourceTexture PROGRESS_BAR_COKE_OVEN = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_coke_oven.png"));
    public static final ResourceTexture PROGRESS_BAR_COMPRESS = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_compress.png"));
    public static final SteamTexture PROGRESS_BAR_COMPRESS_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/progress_bar/progress_bar_compress_%s.png"));
    public static final ResourceTexture PROGRESS_BAR_CRACKING = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_cracking.png"));
    public static final ResourceTexture PROGRESS_BAR_CRACKING_INPUT = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_cracking_2.png"));
    public static final ResourceTexture PROGRESS_BAR_CRYSTALLIZATION = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_crystallization.png"));
    public static final ResourceTexture PROGRESS_BAR_DISTILLATION_TOWER = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_distillation_tower.png"));
    public static final ResourceTexture PROGRESS_BAR_EXTRACT = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_extract.png"));
    public static final SteamTexture PROGRESS_BAR_EXTRACT_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/progress_bar/progress_bar_extract_%s.png"));
    public static final ResourceTexture PROGRESS_BAR_EXTRUDER = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_extruder.png"));
    public static final ResourceTexture PROGRESS_BAR_FUSION = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_fusion.png"));
    public static final ResourceTexture PROGRESS_BAR_GAS_COLLECTOR = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_gas_collector.png"));
    public static final ResourceTexture PROGRESS_BAR_HAMMER = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_hammer.png"));
    public static final SteamTexture PROGRESS_BAR_HAMMER_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/progress_bar/progress_bar_hammer_%s.png"));
    public static final ResourceTexture PROGRESS_BAR_HAMMER_BASE = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_hammer_base.png"));
    public static final SteamTexture PROGRESS_BAR_HAMMER_BASE_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/progress_bar/progress_bar_hammer_base_%s.png"));
    public static final ResourceTexture PROGRESS_BAR_LATHE = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_lathe.png"));
    public static final ResourceTexture PROGRESS_BAR_LATHE_BASE = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_lathe_base.png"));
    public static final ResourceTexture PROGRESS_BAR_MACERATE = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_macerate.png"));
    public static final SteamTexture PROGRESS_BAR_MACERATE_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/progress_bar/progress_bar_macerate_%s.png"));
    public static final ResourceTexture PROGRESS_BAR_MAGNET = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_magnet.png"));
    public static final ResourceTexture PROGRESS_BAR_MASS_FAB = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_mass_fab.png"));
    public static final ResourceTexture PROGRESS_BAR_MIXER = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_mixer.png"));
    public static final ResourceTexture PROGRESS_BAR_PACKER = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_packer.png"));
    public static final ResourceTexture PROGRESS_BAR_RECYCLER = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_recycler.png"));
    public static final ResourceTexture PROGRESS_BAR_REPLICATOR = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_replicator.png"));
    public static final ResourceTexture PROGRESS_BAR_SIFT = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_sift.png"));
    public static final ResourceTexture PROGRESS_BAR_SLICE = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_slice.png"));
    public static final SteamTexture PROGRESS_BAR_SOLAR_STEAM = SteamTexture
            .fullImage(GTCEu.id("textures/gui/progress_bar/progress_bar_solar_%s.png"));
    public static final ResourceTexture PROGRESS_BAR_UNLOCK = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_unlock.png"));
    public static final ResourceTexture PROGRESS_BAR_UNPACKER = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_unpacker.png"));
    public static final ResourceTexture PROGRESS_BAR_WIREMILL = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_wiremill.png"));
    public static final ResourceTexture PROGRESS_BAR_RESEARCH_STATION_1 = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_research_station_1.png"));
    public static final ResourceTexture PROGRESS_BAR_RESEARCH_STATION_2 = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_research_station_2.png"));
    public static final ResourceTexture PROGRESS_BAR_RESEARCH_STATION_BASE = UITextures.resource(
            GTCEu.id("textures/gui/progress_bar/progress_bar_research_station_base.png"));

    // JEI
    public static final ResourceTexture INFO_ICON = UITextures.resource(GTCEu.id("textures/gui/widget/information.png"));
    public static final ResourceTexture MULTIBLOCK_CATEGORY = UITextures.resource(
            GTCEu.id("textures/gui/icon/coke_oven.png"));

    public static final ResourceTexture ARC_FURNACE_RECYCLING_CATEGORY = UITextures.resource(
            GTCEu.id("textures/gui/icon/category/arc_furnace_recycling.png"));
    public static final ResourceTexture MACERATOR_RECYCLING_CATEGORY = UITextures.resource(
            GTCEu.id("textures/gui/icon/category/macerator_recycling.png"));
    public static final ResourceTexture EXTRACTOR_RECYCLING_CATEGORY = UITextures.resource(
            GTCEu.id("textures/gui/icon/category/extractor_recycling.png"));

    // Covers
    public static final ResourceTexture COVER_MACHINE_CONTROLLER = UITextures.resource(
            GTCEu.id("textures/items/metaitems/cover.controller.png"));

    // Terminal
    public static final ResourceTexture ICON_REMOVE = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/remove_hover.png"));
    public static final ResourceTexture ICON_UP = UITextures.resource(GTCEu.id("textures/gui/terminal/icon/up_hover.png"));
    public static final ResourceTexture ICON_DOWN = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/down_hover.png"));
    public static final ResourceTexture ICON_RIGHT = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/right_hover.png"));
    public static final ResourceTexture ICON_LEFT = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/left_hover.png"));
    public static final ResourceTexture ICON_ADD = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/add_hover.png"));

    public static final ResourceTexture ICON_NEW_PAGE = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/system/memory_card_hover.png"));
    public static final ResourceTexture ICON_LOAD = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/folder_hover.png"));
    public static final ResourceTexture ICON_SAVE = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/system/save_hover.png"));
    public static final ResourceTexture ICON_LOCATION = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/guide_hover.png"));
    public static final ResourceTexture ICON_VISIBLE = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/appearance_hover.png"));
    public static final ResourceTexture ICON_CALCULATOR = UITextures.resource(
            GTCEu.id("textures/gui/terminal/icon/calculator_hover.png"));
    public static final ResourceTexture UI_FRAME_SIDE_UP = UITextures.resource(
            GTCEu.id("textures/gui/terminal/frame_side_up.png"));
    public static final ResourceTexture UI_FRAME_SIDE_DOWN = UITextures.resource(
            GTCEu.id("textures/gui/terminal/frame_side_down.png"));

    // Texture Areas
    public static final ResourceTexture BUTTON_FLUID = UITextures.resource(
            GTCEu.id("textures/block/cover/cover_interface_fluid_button.png"));
    public static final ResourceTexture BUTTON_ITEM = UITextures.resource(
            GTCEu.id("textures/block/cover/cover_interface_item_button.png"));
    public static final ResourceTexture BUTTON_ENERGY = UITextures.resource(
            GTCEu.id("textures/block/cover/cover_interface_energy_button.png"));
    public static final ResourceTexture BUTTON_MACHINE = UITextures.resource(
            GTCEu.id("textures/block/cover/cover_interface_machine_button.png"));
    public static final ResourceTexture BUTTON_INTERFACE = UITextures.resource(
            GTCEu.id("textures/block/cover/cover_interface_computer_button.png"));
    public static final ResourceTexture COVER_INTERFACE_MACHINE_ON_PROXY = UITextures.resource(
            GTCEu.id("textures/block/cover/cover_interface_machine_on_proxy.png"));
    public static final ResourceTexture COVER_INTERFACE_MACHINE_OFF_PROXY = UITextures.resource(
            GTCEu.id("textures/blocks/cover/cover_interface_machine_off_proxy.png"));
    public static final ResourceTexture SCENE = UITextures.resource(GTCEu.id("textures/gui/widget/scene.png"));
    public static final NinePatchTexture DISPLAY_FRAME = NinePatchTexture.get(GTCEu.id("display_frame"));
    public static final ResourceTexture INSUFFICIENT_INPUT = UITextures.resource(
            GTCEu.id("textures/gui/base/indicator_no_energy.png"));
    public static final NinePatchTexture ENERGY_BAR_BACKGROUND = NinePatchTexture.get(GTCEu.id("progress_bar_boiler_empty_steel"));
    public static final NinePatchTexture ENERGY_BAR_BASE = NinePatchTexture.get(GTCEu.id("progress_bar_boiler_heat"));
    public static final ResourceTexture LIGHT_ON = UITextures.resource(GTCEu.id("textures/gui/widget/light_on.png"));
    public static final ResourceTexture LIGHT_OFF = UITextures.resource(GTCEu.id("textures/gui/widget/light_off.png"));
    public static final ResourceTexture UP = UITextures.resource(GTCEu.id("textures/gui/base/up.png"));
    public static final ResourceTexture[] TIER = new ResourceTexture[9];
    static {
        final var offset = 1f / TIER.length;
        for (int i = 0; i < TIER.length; i++) {
            TIER[i] = UITextures.resource(GTCEu.id("textures/gui/overlay/tier.png")).getSubTexture(0, i * offset, 1,
                    offset);
        }
    }

    // Lamp item overlay
    public static final ResourceTexture LAMP_NO_BLOOM = UITextures.resource(
            GTCEu.id("textures/gui/item_overlay/lamp_no_bloom.png"));
    public static final ResourceTexture LAMP_NO_LIGHT = UITextures.resource(
            GTCEu.id("textures/gui/item_overlay/lamp_no_light.png"));

    // ME hatch/bus
    public static final ResourceTexture NUMBER_BACKGROUND = UITextures.resource(
            GTCEu.id("textures/gui/widget/number_background.png"));
    public static final ResourceTexture CONFIG_ARROW = UITextures.resource(
            GTCEu.id("textures/gui/widget/config_arrow.png"));
    public static final ResourceTexture CONFIG_ARROW_DARK = UITextures.resource(
            GTCEu.id("textures/gui/widget/config_arrow_dark.png"));
    public static final ResourceTexture SELECT_BOX = UITextures.resource(GTCEu.id("textures/gui/widget/select_box.png"));

    // HPCA Component icons
    public static final ResourceTexture HPCA_COMPONENT_OUTLINE = UITextures.resource(
            GTCEu.id("textures/gui/widget/hpca/component_outline.png"));
    public static final ResourceTexture HPCA_ICON_EMPTY_COMPONENT = UITextures.resource(
            GTCEu.id("textures/gui/widget/hpca/empty_component.png"));
    public static final ResourceTexture HPCA_ICON_ADVANCED_COMPUTATION_COMPONENT = UITextures.resource(
            GTCEu.id("textures/gui/widget/hpca/advanced_computation_component.png"));
    public static final ResourceTexture HPCA_ICON_BRIDGE_COMPONENT = UITextures.resource(
            GTCEu.id("textures/gui/widget/hpca/bridge_component.png"));
    public static final ResourceTexture HPCA_ICON_COMPUTATION_COMPONENT = UITextures.resource(
            GTCEu.id("textures/gui/widget/hpca/computation_component.png"));
    public static final ResourceTexture HPCA_ICON_ACTIVE_COOLER_COMPONENT = UITextures.resource(
            GTCEu.id("textures/gui/widget/hpca/active_cooler_component.png"));
    public static final ResourceTexture HPCA_ICON_HEAT_SINK_COMPONENT = UITextures.resource(
            GTCEu.id("textures/gui/widget/hpca/heat_sink_component.png"));
    public static final ResourceTexture HPCA_ICON_DAMAGED_ADVANCED_COMPUTATION_COMPONENT = UITextures.resource(
            GTCEu.id("textures/gui/widget/hpca/damaged_advanced_computation_component.png"));
    public static final ResourceTexture HPCA_ICON_DAMAGED_COMPUTATION_COMPONENT = UITextures.resource(
            GTCEu.id("textures/gui/widget/hpca/damaged_computation_component.png"));
}
