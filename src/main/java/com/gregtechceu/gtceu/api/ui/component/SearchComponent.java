package com.gregtechceu.gtceu.api.ui.component;

import com.gregtechceu.gtceu.api.ui.container.FlowLayout;
import com.gregtechceu.gtceu.api.ui.container.ScrollContainer;
import com.gregtechceu.gtceu.api.ui.core.Sizing;
import com.gregtechceu.gtceu.api.ui.core.UIComponent;
import com.gregtechceu.gtceu.api.ui.core.UIGuiGraphics;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.ISearch;
import com.lowdragmc.lowdraglib.utils.SearchEngine;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;

public class SearchComponent<T> extends FlowLayout {

    public final SearchEngine<T> engine;
    public final IComponentSearch<T> search;
    public final ScrollContainer<?> popUp;
    public final TextBoxComponent textBoxComponent;
    private int capacity = 10;
    protected boolean isShow;
    @Setter
    protected boolean showUp = false;

    public SearchComponent(Sizing horizontalSizing, Sizing verticalSizing, IComponentSearch<T> search) {
        this(horizontalSizing, verticalSizing, search, false);
    }

    public SearchComponent(Sizing horizontalSizing, Sizing verticalSizing, IComponentSearch<T> search, boolean isServer) {
        super(horizontalSizing, verticalSizing, Algorithm.VERTICAL);
        this.child(textBoxComponent = (TextBoxComponent) new TextBoxComponent(horizontalSizing) {

            @Override
            public void onFocusGained(FocusSource source, UIComponent lastFocus) {
                if (lastFocus != null && lastFocus.parent() == this.parent()) {
                    return;
                }
                super.onFocusGained(source, lastFocus);
            }
        }.verticalSizing(verticalSizing));
        this.children(popUp = new ScrollContainer<>(ScrollContainer.ScrollDirection.VERTICAL, horizontalSizing,
                verticalSizing) {

            @Override
            public void onFocusGained(FocusSource source, UIComponent lastFocus) {
                if (lastFocus != null && lastFocus.parent() == this.parent()) {
                    return;
                }
                super.onFocusGained(source, lastFocus);
            }
            @Override
            public void onFocusChanged(@Nullable Widget lastFocus, Widget focus) {
                if (lastFocus != null && focus != null && lastFocus.parent == focus.parent) {
                    return;
                }
                super.onFocusChanged(lastFocus, focus);
                setShow(isFocus());
            }
        });
        // TODO implement
        //popUp.setVisible(false);
        //popUp.setActive(true);
        this.search = search;
        this.engine = new SearchEngine<>(search, (r) -> {
            int size = popUp.getAllWidgetSize();
            popUp.setSize(new Size(getSize().width, Math.min(size + 1, capacity) * 15));
            if (showUp) {
                popUp.setSelfPosition(new Position(0, -Math.min(size + 1, capacity) * 15));
            } else {
                popUp.setSelfPosition(new Position(0, height));
            }
            popUp.waitToAdded(new ButtonWidget(0, size * 15, width,
                    15, new TextTexture(search.resultDisplay(r)).setWidth(width).setType(TextTexture.TextType.ROLL),
                    cd -> {
                        search.selectResult(r);
                        setShow(false);
                        textBoxComponent.setCurrentString(search.resultDisplay(r));
                    }).setHoverBorderTexture(-1, -1));
            if (isServer) {
                writeUpdateInfo(-2, buf -> search.serialize(r, buf));
            }
        });

        textBoxComponent.setTextResponder(s -> {
            popUp.clearAllWidgets();
            popUp.setSize(new Size(getSize().width, 0));
            if (showUp) {
                popUp.setSelfPosition(new Position(0, 0));
            } else {
                popUp.setSelfPosition(new Position(0, height));
            }
            setShow(true);
            this.engine.searchWord(s);
            if (isServer) {
                writeUpdateInfo(-1, buffer -> {
                });
            }
        });
    }

    @Override
    public void receiveMessage(int id, FriendlyByteBuf buffer) {
        if (id == -1) {
            popUp.clearAllWidgets();
            popUp.setSize(new Size(getSize().width, 0));
            if (showUp) {
                popUp.setSelfPosition(new Position(0, 0));
            } else {
                popUp.setSelfPosition(new Position(0, getSize().height));
            }
        } else if (id == -2) {
            T r = search.deserialize(buffer);
            int size = popUp.children().size();
            int width = width();
            popUp.setSize(new Size(getSize().width, Math.min(size + 1, capacity) * 15));
            if (showUp) {
                popUp.setSelfPosition(new Position(0, -Math.min(size + 1, capacity) * 15));
            } else {
                popUp.setSelfPosition(new Position(0, getSize().height));
            }
            popUp.addWidget(new ButtonWidget(0, size * 15, width,
                    15, new TextTexture(search.resultDisplay(r)).setWidth(width).setType(TextTexture.TextType.ROLL),
                    cd -> {
                        search.selectResult(r);
                        setShow(false);
                        textBoxComponent.setCurrentString(search.resultDisplay(r));
                    }).setHoverBorderTexture(-1, -1));
        } else {
            super.receiveMessage(id, buffer);
        }
    }

    public SearchComponent<T> setCapacity(int capacity) {
        this.capacity = capacity;
        popUp.setSize(new Size(getSize().width, Math.min(popUp.getAllWidgetSize(), capacity) * 15));
        if (showUp) {
            popUp.setSelfPosition(new Position(0, -Math.min(popUp.getAllWidgetSize(), capacity) * 15));
        } else {
            popUp.setSelfPosition(new Position(0, getSize().height));
        }
        return this;
    }

    public SearchComponent<T> setCurrentString(String currentString) {
        textBoxComponent.setCurrentString(currentString);
        return this;
    }

    public String getCurrentString() {
        return textBoxComponent.getCurrentString();
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
        popUp.setVisible(isShow);
        popUp.setActive(isShow);
    }

    @Override
    public void drawInForeground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        boolean lastVisible = popUp.isVisible();
        popUp.setVisible(false);
        super.drawInForeground(graphics, mouseX, mouseY, partialTicks);
        popUp.setVisible(lastVisible);

        if (isShow) {
            graphics.m_280168_().m_252880_(0, 0, 200);
            popUp.drawInBackground(graphics, mouseX, mouseY, partialTicks);
            popUp.drawInForeground(graphics, mouseX, mouseY, partialTicks);
            graphics.m_280168_().m_252880_(0, 0, -200);
        }
    }

    @Override
    public void draw(UIGuiGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(graphics, mouseX, mouseY, partialTicks, delta);
    }

    @Override
    public void drawInBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        boolean lastVisible = popUp.isVisible();
        popUp.setVisible(false);
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        popUp.setVisible(lastVisible);
    }

    public interface IComponentSearch<T> extends ISearch<T> {

        String resultDisplay(T value);

        void selectResult(T value);

        /**
         * just used for server side
         */
        default void serialize(T value, FriendlyByteBuf buf) {
            buf.writeUtf(resultDisplay(value));
        }

        /**
         * just used for server side
         */
        default T deserialize(FriendlyByteBuf buf) {
            return (T) buf.readUtf();
        }

    }

}
