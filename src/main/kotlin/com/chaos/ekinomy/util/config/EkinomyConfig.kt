package com.chaos.ekinomy.util.config

import com.chaos.ekinomy.Ekinomy
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = Ekinomy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
class EkinomyConfig(builder: ForgeConfigSpec.Builder) {
    val initialBalance: ForgeConfigSpec.LongValue

    init {
        builder.comment("Ekinomy Config").push(Ekinomy.MODID)

        initialBalance = makeOption(
            builder,
            "This sets the initial balance for new players and the target balance for command /ekinomy reset <player>.\nThe default value is 100.00.",
        ) {
            it.defineInRange("initial_balance", 100, Long.MIN_VALUE, Long.MAX_VALUE)
        } as ForgeConfigSpec.LongValue

        builder.pop()
    }

    private inline fun <T> makeOption(
        builder: ForgeConfigSpec.Builder,
        comment: String,
        crossinline definition: (builder: ForgeConfigSpec.Builder) -> ForgeConfigSpec.ConfigValue<T>
    ): ForgeConfigSpec.ConfigValue<T> = definition.invoke(builder.comment(comment))
}