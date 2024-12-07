package com.gregtechceu.gtceu.api.ui.inject;

import com.gregtechceu.gtceu.api.ui.core.UIComponent;

/**
 * A marker interface for components which consume
 * text input when focused - this is used to prevent handled
 * screens from closing when said component is focused and the
 * inventory key is pressed
 */
public interface GreedyInputUIComponent extends UIComponent {}
