package com.gregtechceu.gtceu.common.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class FluidParser {

    private static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(Component.translatable("argument.Fluid.tag.disallowed"));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_FLUID = new DynamicCommandExceptionType((value) -> {
        return Component.translatable("argument.fluid.id.invalid", value);
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((value) -> {
        return Component.translatable("arguments.fluid.tag.unknown", value);
    });
    private static final char SYNTAX_START_NBT = '{';
    private static final char SYNTAX_TAG = '#';
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final HolderLookup<Fluid> fluids;
    private final StringReader reader;
    private final boolean allowTags;
    private Either<Holder<Fluid>, HolderSet<Fluid>> result;
    @Nullable
    private CompoundTag nbt;
    /**
     * Builder to be used when creating a list of suggestions
     */
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

    private FluidParser(HolderLookup<Fluid> fluids, StringReader reader, boolean allowTags) {
        this.fluids = fluids;
        this.reader = reader;
        this.allowTags = allowTags;
    }

    public static FluidResult parseForFluid(HolderLookup<Fluid> lookup, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();

        try {
            FluidParser fluidParser = new FluidParser(lookup, reader, false);
            fluidParser.parse();
            Holder<Fluid> holder = fluidParser.result.left().orElseThrow(() -> {
                return new IllegalStateException("Parser returned unexpected tag name");
            });
            return new FluidResult(holder, fluidParser.nbt);
        } catch (CommandSyntaxException commandsyntaxexception) {
            reader.setCursor(i);
            throw commandsyntaxexception;
        }
    }

    public static Either<FluidParser.FluidResult, FluidParser.TagResult> parseForTesting(HolderLookup<Fluid> lookup, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();

        try {
            FluidParser Fluidparser = new FluidParser(lookup, reader, true);
            Fluidparser.parse();
            return Fluidparser.result.mapBoth((holder) -> new FluidResult(holder, Fluidparser.nbt),
                    (holderSet) -> new TagResult(holderSet, Fluidparser.nbt));
        } catch (CommandSyntaxException commandsyntaxexception) {
            reader.setCursor(i);
            throw commandsyntaxexception;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Fluid> lookup, SuggestionsBuilder builder, boolean allowTags) {
        StringReader stringreader = new StringReader(builder.getInput());
        stringreader.setCursor(builder.getStart());
        FluidParser Fluidparser = new FluidParser(lookup, stringreader, allowTags);

        try {
            Fluidparser.parse();
        } catch (CommandSyntaxException ignored) {
        }

        return Fluidparser.suggestions.apply(builder.createOffset(stringreader.getCursor()));
    }

    private void readFluid() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        ResourceLocation id = ResourceLocation.read(this.reader);
        Optional<? extends Holder<Fluid>> optional = this.fluids.get(ResourceKey.create(Registries.FLUID, id));
        this.result = Either.left(optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return ERROR_UNKNOWN_FLUID.createWithContext(this.reader, id);
        }));
    }

    private void readTag() throws CommandSyntaxException {
        if (!this.allowTags) {
            throw ERROR_NO_TAGS_ALLOWED.createWithContext(this.reader);
        } else {
            int i = this.reader.getCursor();
            this.reader.expect(SYNTAX_TAG);
            this.suggestions = this::suggestTag;
            ResourceLocation id = ResourceLocation.read(this.reader);
            Optional<? extends HolderSet<Fluid>> optional = this.fluids.get(TagKey.create(Registries.FLUID, id));
            this.result = Either.right(optional.orElseThrow(() -> {
                this.reader.setCursor(i);
                return ERROR_UNKNOWN_TAG.createWithContext(this.reader, id);
            }));
        }
    }

    private void readNbt() throws CommandSyntaxException {
        this.nbt = (new TagParser(this.reader)).readStruct();
    }

    private void parse() throws CommandSyntaxException {
        if (this.allowTags) {
            this.suggestions = this::suggestFluidIdOrTag;
        } else {
            this.suggestions = this::suggestFluid;
        }

        if (this.reader.canRead() && this.reader.peek() == SYNTAX_TAG) {
            this.readTag();
        } else {
            this.readFluid();
        }

        this.suggestions = this::suggestOpenNbt;
        if (this.reader.canRead() && this.reader.peek() == SYNTAX_START_NBT) {
            this.suggestions = SUGGEST_NOTHING;
            this.readNbt();
        }

    }

    private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf(SYNTAX_START_NBT));
        }

        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(this.fluids.listTagIds().map(TagKey::location), builder,
                String.valueOf(SYNTAX_TAG));
    }

    private CompletableFuture<Suggestions> suggestFluid(SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(this.fluids.listElementIds().map(ResourceKey::location), builder);
    }

    private CompletableFuture<Suggestions> suggestFluidIdOrTag(SuggestionsBuilder builder) {
        this.suggestTag(builder);
        return this.suggestFluid(builder);
    }

    public record FluidResult(Holder<Fluid> fluid, @Nullable CompoundTag nbt) {

    }

    public record TagResult(HolderSet<Fluid> tag, @Nullable CompoundTag nbt) {

    }

}
