package com.gregtechceu.gtceu.integration.ae2.slot;

import appeng.api.stacks.GenericStack;

/**
 * @author GlodBlock
 * @apiNote A slot that can be set to keep requesting.
 * @date 2023/4/21-0:34
 */
public interface IConfigurableSlot {

    GenericStack getConfig();

    GenericStack getStock();

    void setConfig(GenericStack val);

    void setStock(GenericStack val);

    IConfigurableSlot copy();
}
