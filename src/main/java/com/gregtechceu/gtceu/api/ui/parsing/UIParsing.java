package com.gregtechceu.gtceu.api.ui.parsing;

import com.gregtechceu.gtceu.api.ui.component.*;
import com.gregtechceu.gtceu.api.ui.container.*;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;

import com.gregtechceu.gtceu.api.ui.texture.ResourceTexture;
import com.gregtechceu.gtceu.api.ui.texture.UITexture;
import com.gregtechceu.gtceu.api.ui.texture.UITextures;
import com.gregtechceu.gtceu.api.ui.texture.NinePatchTexture;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A utility class containing the component factory registry
 * as well as some utility functions to ease model parsing
 */
@SuppressWarnings({ "DeprecatedIsStillUsed", "unused" })
public class UIParsing {

    private static final Map<String, Function<Element, UIComponent>> COMPONENT_FACTORIES = new HashMap<>();
    private static final Map<String, Function<Element, UITexture>> TEXTURE_FACTORIES = new HashMap<>();

    /**
     * @deprecated In order to more properly separate factories added by different
     *             mods, use {@link #registerComponentFactory(ResourceLocation, Function)}, which takes a ResourceLocation
     *             instead
     */
    @ApiStatus.Internal
    public static void registerComponentFactory(String componentTagName, Function<Element, UIComponent> factory) {
        if (COMPONENT_FACTORIES.containsKey(componentTagName)) {
            throw new IllegalStateException(
                    "A component factory with name " + componentTagName + " is already registered");
        }

        COMPONENT_FACTORIES.put(componentTagName, factory);
    }

    /**
     * Register a factory used to create UIComponents from XML elements.
     * Most factories will only consider the tag name of the element,
     * but more context can be extracted from the passed element
     *
     * @param componentId The identifier under which to register the component,
     *                    which (separated by a period instead of a colon) is used
     *                    as the tag name for which this factory gets invoked
     * @param factory     The factory to register
     */
    public static void registerComponentFactory(ResourceLocation componentId, Function<Element, UIComponent> factory) {
        registerComponentFactory(componentId.getNamespace() + "." + componentId.getPath(), factory);
    }

    /**
     * @deprecated In order to more properly separate factories added by different
     *             mods, use {@link #registerTextureFactory(ResourceLocation, Function)}, which takes a ResourceLocation
     *             instead
     */
    @ApiStatus.Internal
    public static void registerTextureFactory(String componentTagName, Function<Element, UITexture> factory) {
        if (TEXTURE_FACTORIES.containsKey(componentTagName)) {
            throw new IllegalStateException(
                    "A component factory with name " + componentTagName + " is already registered");
        }

        TEXTURE_FACTORIES.put(componentTagName, factory);
    }

    /**
     * Register a factory used to create UIComponents from XML elements.
     * Most factories will only consider the tag name of the element,
     * but more context can be extracted from the passed element
     *
     * @param componentId The identifier under which to register the component,
     *                    which (separated by a period instead of a colon) is used
     *                    as the tag name for which this factory gets invoked
     * @param factory     The factory to register
     */
    public static void registerTextureFactory(ResourceLocation componentId, Function<Element, UITexture> factory) {
        registerTextureFactory(componentId.getNamespace() + "." + componentId.getPath(), factory);
    }

    /**
     * Get the appropriate component factory for the given
     * XML element. An exception is thrown if none is registered
     *
     * @param element The element representing the component to be parsed
     * @return The matching factory
     * @throws UIModelParsingException If there is no registered factory
     *                                 capable of parsing the given element
     */
    public static Function<Element, UIComponent> getComponentFactory(Element element) {
        var factory = COMPONENT_FACTORIES.get(element.getNodeName());
        if (factory == null) {
            throw new UIModelParsingException("Unknown component type: " + element.getNodeName());
        }

        return factory;
    }

    /**
     * Get the appropriate texture factory for the given
     * XML element. An exception is thrown if none is registered
     *
     * @param element The element representing the component to be parsed
     * @return The matching factory
     * @throws UIModelParsingException If there is no registered factory
     *                                 capable of parsing the given element
     */
    public static Function<Element, UITexture> getTextureFactory(Element element) {
        var factory = TEXTURE_FACTORIES.get(element.getNodeName());
        if (factory == null) {
            throw new UIModelParsingException("Unknown component type: " + element.getNodeName());
        }

        return factory;
    }

    /**
     * Extract all children of the given element which match the expected type
     *
     * @param type The type of child nodes to extract
     * @param <T>  The class to cast the extracted nodes to
     * @return A list of all children of {@code element} which have a type of {@code type}
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node> List<T> allChildrenOfType(Element element, short type) {
        var list = new ArrayList<T>();
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            var child = element.getChildNodes().item(i);
            if (child.getNodeType() != type) continue;
            list.add((T) child);
        }
        return list;
    }

    /**
     * Extract all child elements of the given element into a map from tag
     * name to element. An exception is thrown if a tag name appears twice
     *
     * @return All element children of {@code element} mapped from
     *         tag name to element
     * @throws UIModelParsingException If two or more children share the same tag name
     */
    public static Map<String, Element> childElements(Element element) {
        var children = element.getChildNodes();
        var map = new HashMap<String, Element>();

        for (int i = 0; i < children.getLength(); i++) {
            var child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) continue;

            if (map.containsKey(child.getNodeName())) {
                throw new UIModelParsingException(
                        "Duplicate child " + child.getNodeName() + " in element " + element.getNodeName());
            }

            map.put(child.getNodeName(), (Element) child);
        }

        return map;
    }

    /**
     * Tries to interpret the text content of the
     * given node as a signed integer
     *
     * @throws UIModelParsingException If the text content does not
     *                                 represent a valid signed integer
     */
    public static int parseSignedInt(Node node) {
        return parseInt(node, true);
    }

    /**
     * Tries to interpret the text content of the
     * given node as an unsigned integer
     *
     * @throws UIModelParsingException If the text content does not
     *                                 represent a valid unsigned integer
     */
    public static int parseUnsignedInt(Node node) {
        return parseInt(node, false);
    }

    /**
     * Tries to interpret the text content of the
     * given node as a floating-point number
     *
     * @throws UIModelParsingException If the text content does not
     *                                 represent a valid floating point number
     */
    public static float parseFloat(Node node) {
        var data = node.getTextContent().strip();
        if (data.matches("-?\\d+(\\.\\d+)?")) {
            return Float.parseFloat(data);
        } else {
            throw new UIModelParsingException("Invalid value '" + data + "', expected a floating point number");
        }
    }

    /**
     * Tries to interpret the text content of the
     * given node as a double-precision floating-point number
     *
     * @throws UIModelParsingException If the text content does not
     *                                 represent a valid floating point number
     */
    public static double parseDouble(Node node) {
        var data = node.getTextContent().strip();
        if (data.matches("-?\\d+(\\.\\d+)?")) {
            return Double.parseDouble(data);
        } else {
            throw new UIModelParsingException(
                    "Invalid value '" + data + "', expected a double-precision floating point number");
        }
    }

    /**
     * Interprets the text content of the
     * given node as a boolean - more specifically this
     * method returns {@code true} if and only if the text content
     * equals {@code true}, without respecting letter case
     */
    public static boolean parseBool(Node node) {
        return node.getTextContent().strip().equalsIgnoreCase("true");
    }

    /**
     * Tries to interpret the text content of the
     * given node as an identifier
     *
     * @throws UIModelParsingException If the text content does not
     *                                 represent a valid identifier
     */
    public static ResourceLocation parseResourceLocation(Node node) {
        try {
            return new ResourceLocation(node.getTextContent().strip());
        } catch (ResourceLocationException exception) {
            throw new UIModelParsingException("Invalid identifier '" + node.getTextContent() + "'", exception);
        }
    }

    /**
     * Interprets the text content of the
     * given element as text. If the {@code translate}
     * attribute is set to {@code true}, the content is
     * interpreted as a translation key - otherwise it is
     * returned literally
     */
    public static Component parseComponent(Element element) {
        return element.getAttribute("translate").equalsIgnoreCase("true") ?
                Component.translatable(element.getTextContent()) :
                Component.literal(element.getTextContent());
    }

    public static <E extends Enum<E>> Function<Element, E> parseEnum(Class<E> enumClass) {
        return element -> {
            var name = element.getTextContent().strip().toUpperCase(Locale.ROOT).replace('-', '_');
            for (var value : enumClass.getEnumConstants()) {
                if (Objects.equals(name, value.name())) return value;
            }

            throw new UIModelParsingException("No such constant " + name + " in enum " + enumClass.getSimpleName());
        };
    }

    /**
     * Parse the property indicated by {@code key} into an object of type {@code T}
     *
     * @param properties The map containing all available properties
     * @param key        The key of the property to parse
     * @param parser     The parsing function to use
     * @param <T>        The type of object to parse
     * @return An optional containing the parsed property, or an empty optional
     *         if the requested property was not contained in the given map
     */
    public static <T, E extends Node> Optional<T> get(Map<String, E> properties, String key, Function<E, T> parser) {
        if (!properties.containsKey(key)) return Optional.empty();
        return Optional.of(parser.apply(properties.get(key)));
    }

    /**
     * Parse the property indicated by {@code key} into an object of type {@code T}
     * and apply the given function if it was present
     *
     * @param properties The map containing all available properties
     * @param key        The key of the property to parse
     * @param parser     The parsing function to use
     * @param consumer   The function to apply if the property was present
     *                   in the map and successfully parsed
     * @param <T>        The type of object to parse
     */
    public static <T, E extends Node> void apply(Map<String, E> properties, String key, Function<E, T> parser,
                                                 Consumer<T> consumer) {
        if (!properties.containsKey(key)) return;
        consumer.accept(parser.apply(properties.get(key)));
    }

    /**
     * Verify that all the given attributes are present
     * on the given element and throw if one is missing
     *
     * @param element    The element to verify
     * @param attributes The attributes to verify
     */
    public static void expectAttributes(Element element, String... attributes) {
        for (var attr : attributes) {
            if (!element.hasAttribute(attr)) {
                throw new UIModelParsingException(
                        "Element '" + element.getNodeName() + "' is missing attribute '" + attr + "'");
            }
        }
    }

    /**
     * Verify that all the given elements are present
     * as children of the given element and throw if one is missing
     *
     * @param element  The element to verify
     * @param children The children of that element
     * @param expected The expected child elements
     */
    public static void expectChildren(Element element, Map<String, Element> children, String... expected) {
        for (var childName : expected) {
            if (!children.containsKey(childName)) {
                throw new UIModelParsingException(
                        "Element '" + element.getNodeName() + "' is missing element '" + childName + "'");
            }
        }
    }

    protected static int parseInt(Node node, boolean allowNegative) {
        var data = node.getTextContent().strip();
        if (data.matches((allowNegative ? "-?" : "") + "\\d+")) {
            return Integer.parseInt(data);
        } else {
            throw new UIModelParsingException(
                    "Invalid value '" + data + "', expected " + (allowNegative ? "" : "positive") + " integer");
        }
    }

    static {
        // Layout
        registerComponentFactory("flow-layout", FlowLayout::parse);
        registerComponentFactory("grid-layout", GridLayout::parse);
        registerComponentFactory("stack-layout", element -> UIContainers.stack(Sizing.content(), Sizing.content()));

        // Container
        registerComponentFactory("root", element -> UIContainers.root(Sizing.content(), Sizing.content()));
        registerComponentFactory("scroll", ScrollContainer::parse);
        registerComponentFactory("collapsible", CollapsibleContainer::parse);
        registerComponentFactory("draggable", element -> UIContainers.draggable(Sizing.content(), Sizing.content(), null));

        // Textures
        registerComponentFactory("sprite", SpriteComponent::parse);
        registerComponentFactory("texture", TextureComponent::parse);
        registerComponentFactory("nine-patch-texture", NinePatchTextureComponent::parse);

        // Game Objects
        registerComponentFactory("entity", EntityComponent::parse);
        registerComponentFactory("item", element -> UIComponents.item(ItemStack.EMPTY));
        registerComponentFactory("fluid", element -> UIComponents.fluid(FluidStack.EMPTY));
        registerComponentFactory("block", BlockComponent::parse);

        registerComponentFactory("player-inventory", PlayerInventoryComponent::parse);
        registerComponentFactory("slot", SlotComponent::parse);
        registerComponentFactory("tank", TankComponent::parse);

        // Widgets
        registerComponentFactory("label", element -> UIComponents.label(Component.empty()));
        registerComponentFactory("box", element -> UIComponents.box(Sizing.content(), Sizing.content()));
        registerComponentFactory("button", element -> UIComponents.button(Component.empty(), (ButtonComponent button) -> {}));
        registerComponentFactory("checkbox", element -> UIComponents.checkbox(Component.empty()));
        registerComponentFactory("text-box", element -> UIComponents.textBox(Sizing.content()));
        registerComponentFactory("text-area", element -> UIComponents.textArea(Sizing.content(), Sizing.content()));
        registerComponentFactory("slider", element -> UIComponents.slider(Sizing.content()));
        registerComponentFactory("discrete-slider", DiscreteSliderComponent::parse);
        registerComponentFactory("dropdown", element -> UIComponents.dropdown(Sizing.content()));
        registerComponentFactory("color-picker", element -> new ColorPickerComponent());
        registerComponentFactory("slim-slider", SlimSliderComponent::parse);
        registerComponentFactory("small-checkbox", element -> new SmallCheckboxComponent());


        // Textures
        registerTextureFactory("resource", ResourceTexture::parse);
        registerTextureFactory("item", element -> UITextures.item(ItemStack.EMPTY));
        registerTextureFactory("progress", element -> UITextures.progress(null, null));
        registerTextureFactory("nine-patch", NinePatchTexture::parse);
        registerTextureFactory("group", element -> UITextures.group());
        registerTextureFactory("text", element -> UITextures.text(Component.empty()));
    }
}
