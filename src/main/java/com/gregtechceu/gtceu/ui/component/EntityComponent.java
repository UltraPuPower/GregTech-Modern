package com.gregtechceu.gtceu.ui.component;

import com.gregtechceu.gtceu.ui.base.BaseUIComponent;
import com.gregtechceu.gtceu.ui.core.Sizing;
import com.gregtechceu.gtceu.ui.core.UIGuiGraphics;
import com.gregtechceu.gtceu.ui.parsing.UIModel;
import com.gregtechceu.gtceu.ui.parsing.UIModelParsingException;
import com.gregtechceu.gtceu.ui.parsing.UIParsing;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

public class EntityComponent <E extends Entity> extends BaseUIComponent {

    protected final EntityRenderDispatcher dispatcher;
    protected final MultiBufferSource.BufferSource entityBuffers;
    protected final E entity;

    protected float mouseRotation = 0;
    protected float scale = 1;
    protected boolean lookAtCursor = false;
    protected boolean allowMouseRotation = false;
    protected boolean scaleToFit = false;
    protected boolean showNametag = false;
    protected Consumer<PoseStack> transform = matrixStack -> {};

    protected EntityComponent(Sizing sizing, E entity) {
        final var client = Minecraft.getInstance();
        this.dispatcher = client.getEntityRenderDispatcher();
        this.entityBuffers = client.renderBuffers().bufferSource();

        this.entity = entity;

        this.sizing(sizing);
    }

    @SuppressWarnings("DataFlowIssue")
    protected EntityComponent(Sizing sizing, EntityType<E> type, @Nullable CompoundTag nbt) {
        final var client = Minecraft.getInstance();
        this.dispatcher = client.getEntityRenderDispatcher();
        this.entityBuffers = client.renderBuffers().bufferSource();

        this.entity = type.create(client.level);
        if (nbt != null) entity.load(nbt);
        entity.absMoveTo(client.player.getX(), client.player.getY(), client.player.getZ());

        this.sizing(sizing);
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        var pose = graphics.pose();
        pose.pushPose();

        pose.translate(x + this.width / 2f, y + this.height / 2f, 100);
        pose.scale(75 * this.scale * this.width / 64f, -75 * this.scale * this.height / 64f, 75 * this.scale);

        pose.translate(0, entity.getBbHeight() / -2f, 0);

        this.transform.accept(pose);

        if (this.lookAtCursor) {
            float xRotation = (float) Math.toDegrees(Math.atan((mouseY - this.y - this.height / 2f) / 40f));
            float yRotation = (float) Math.toDegrees(Math.atan((mouseX - this.x - this.width / 2f) / 40f));

            if (this.entity instanceof LivingEntity living) {
                living.yHeadRotO = -yRotation;
            }

            this.entity.yRotO = -yRotation;
            this.entity.xRotO = xRotation * .65f;

            // We make sure the xRotation never becomes 0, as the lighting otherwise becomes very unhappy
            if (xRotation == 0) xRotation = .1f;
            pose.mulPose(Axis.XP.rotationDegrees(xRotation * .15f));
            pose.mulPose(Axis.YP.rotationDegrees(yRotation * .15f));
        } else {
            pose.mulPose(Axis.XP.rotationDegrees(35));
            pose.mulPose(Axis.YP.rotationDegrees(-45 + this.mouseRotation));
        }

        var dispatcher = (OwoEntityRenderDispatcherExtension) this.dispatcher;
        dispatcher.owo$setCounterRotate(true);
        dispatcher.owo$setShowNametag(this.showNametag);

        RenderSystem.setShaderLights(new Vector3f(.15f, 1, 0), new Vector3f(.15f, -1, 0));
        this.dispatcher.setRenderShadow(false);
        this.dispatcher.render(this.entity, 0, 0, 0, 0, 0, pose, this.entityBuffers, LightTexture.FULL_BRIGHT);
        this.dispatcher.setRenderShadow(true);
        this.entityBuffers.endBatch();
        Lighting.setupFor3DItems();

        pose.popPose();

        dispatcher.owo$setCounterRotate(false);
        dispatcher.owo$setShowNametag(true);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        if (this.allowMouseRotation && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.mouseRotation += deltaX;

            super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
            return true;
        } else {
            return super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
        }
    }

    public E entity() {
        return this.entity;
    }

    public EntityComponent<E> allowMouseRotation(boolean allowMouseRotation) {
        this.allowMouseRotation = allowMouseRotation;
        return this;
    }

    public boolean allowMouseRotation() {
        return this.allowMouseRotation;
    }

    public EntityComponent<E> lookAtCursor(boolean lookAtCursor) {
        this.lookAtCursor = lookAtCursor;
        return this;
    }

    public boolean lookAtCursor() {
        return this.lookAtCursor;
    }

    public EntityComponent<E> scale(float scale) {
        this.scale = scale;
        return this;
    }

    public float scale() {
        return this.scale;
    }

    public EntityComponent<E> scaleToFit(boolean scaleToFit) {
        this.scaleToFit = scaleToFit;

        if (scaleToFit) {
            float xScale = .5f / entity.getBbWidth();
            float yScale = .5f / entity.getBbHeight();

            this.scale(Math.min(xScale, yScale));
        }

        return this;
    }

    public boolean scaleToFit() {
        return this.scaleToFit;
    }

    public EntityComponent<E> transform(Consumer<PoseStack> transform) {
        this.transform = transform;
        return this;
    }

    public Consumer<PoseStack> transform() {
        return transform;
    }

    public EntityComponent<E> showNametag(boolean showNametag) {
        this.showNametag = showNametag;
        return this;
    }

    public boolean showNametag() {
        return showNametag;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }

    public static RenderablePlayerEntity createRenderablePlayer(GameProfile profile) {
        return new RenderablePlayerEntity(profile);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "scale", UIParsing::parseFloat, this::scale);
        UIParsing.apply(children, "look-at-cursor", UIParsing::parseBool, this::lookAtCursor);
        UIParsing.apply(children, "mouse-rotation", UIParsing::parseBool, this::allowMouseRotation);
        UIParsing.apply(children, "scale-to-fit", UIParsing::parseBool, this::scaleToFit);
    }

    public static EntityComponent<?> parse(Element element) {
        UIParsing.expectAttributes(element, "type");
        var entityId = UIParsing.parseResourceLocation(element.getAttributeNode("type"));
        var entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId).orElseThrow(() -> new UIModelParsingException("Unknown entity type " + entityId));

        CompoundTag nbt = null;
        if (element.hasAttribute("nbt")) {
            try {
                nbt = TagParser.parseTag(element.getAttribute("nbt"));
            } catch (CommandSyntaxException cse) {
                throw new UIModelParsingException("Invalid NBT compound", cse);
            }
        }

        return new EntityComponent<>(Sizing.content(), entityType, nbt);
    }

    public static class RenderablePlayerEntity extends LocalPlayer {

        protected ResourceLocation skinTextureId = null;
        protected String model = null;

        protected RenderablePlayerEntity(GameProfile profile) {
            super(Minecraft.getInstance(),
                    Minecraft.getInstance().level,
                    new ClientPacketListener(Minecraft.getInstance(),
                            null,
                            new Connection(PacketFlow.CLIENTBOUND),
                            null,
                            profile,
                            Minecraft.getInstance().getTelemetryManager().createWorldSessionManager(false, Duration.ZERO, "tetris")
                    ),
                    null, null, false, false
            );

            this.minecraft.getSkinManager().registerSkins(this.getGameProfile(), (type, identifier, texture) -> {
                if (type != MinecraftProfileTexture.Type.SKIN) return;

                this.skinTextureId = identifier;
                this.model = texture.getMetadata("model");
                if (this.model == null) this.model = "default";

            }, true);
        }

        @Override
        public boolean isSkinLoaded() {
            return skinTextureId != null;
        }

        @Override
        public ResourceLocation getSkinTextureLocation() {
            return this.skinTextureId != null ? this.skinTextureId : super.getSkinTextureLocation();
        }

        @Override
        public boolean isModelPartShown(PlayerModelPart modelPart) {
            return true;
        }

        @Override
        public String getModelName() {
            return this.model != null ? this.model : super.getModelName();
        }

        @Nullable
        @Override
        protected PlayerInfo getPlayerInfo() {
            return null;
        }
    }
}
