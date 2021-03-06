package com.chaos.ekinomy.util.config

import com.chaos.ekinomy.Ekinomy
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = Ekinomy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
class EkinomyConfig(builder: ForgeConfigSpec.Builder) {
    val initialBalance: ForgeConfigSpec.LongValue
    val mobKillReward: ForgeConfigSpec.LongValue
    val playerKilledPenalty: ForgeConfigSpec.LongValue

    val storeLog: ForgeConfigSpec.BooleanValue
    val launchWeb: ForgeConfigSpec.BooleanValue
    val webPort: ForgeConfigSpec.IntValue

    init {
        builder.comment("Ekinomy Config").push(Ekinomy.MODID)

        builder.push("economy")

        initialBalance = makeOption(
            builder,
            "This sets the initial balance for new players and the target balance for command /ekinomy reset <player>.\nThe default value is 100.",
        ) {
            it.defineInRange("initial_balance", 100, 0, Long.MAX_VALUE)
        } as ForgeConfigSpec.LongValue

        mobKillReward = makeOption(
            builder,
            "This defines how much money will be dropped from killing a hostile mob.\nThe default value is 50."
        ) {
            it.defineInRange("mob_kill_reward", 50, 0, Long.MAX_VALUE)
        } as ForgeConfigSpec.LongValue

        playerKilledPenalty = makeOption(
            builder,
            "this defines how much money will be lost when a player is killed by another player.\nThe default value is 50."
        ) {
            it.defineInRange("player_killed_penalty", 50, 0, Long.MAX_VALUE)
        } as ForgeConfigSpec.LongValue

        builder.pop()
        builder.push("website")

        storeLog = makeOption(
            builder,
            "This determines whether needs to store log of all economy actions happens on player or not, this option is the prerequisite of online website."
        ) {
            it.define("store_log", false)
        } as ForgeConfigSpec.BooleanValue

        launchWeb = makeOption(
            builder,
            "This determines whether online website of ekinomy mod will be launched after server start up or not."
        ) {
            it.define("launch_web", false)
        } as ForgeConfigSpec.BooleanValue

        webPort = makeOption(
            builder,
            "This determines what port will online website will be hosted on, launch_web option is prerequisite of this option."
        ) {
            it.defineInRange("web_port", 9487, 0, 10000)
        } as ForgeConfigSpec.IntValue

        builder.pop()
        builder.pop()
    }

    private inline fun <T> makeOption(
        builder: ForgeConfigSpec.Builder,
        comment: String,
        crossinline definition: (builder: ForgeConfigSpec.Builder) -> ForgeConfigSpec.ConfigValue<T>
    ): ForgeConfigSpec.ConfigValue<T> = definition.invoke(builder.comment(comment))
}