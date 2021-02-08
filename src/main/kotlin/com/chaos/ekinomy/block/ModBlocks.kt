package com.chaos.ekinomy.block

import com.chaos.ekinomy.Ekinomy
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.KDeferredRegister

object ModBlocks {
    // use of the new KDeferredRegister
    val REGISTRY = KDeferredRegister(ForgeRegistries.BLOCKS, Ekinomy.MODID)

//    val EXAMPLE_BLOCK by REGISTRY.registerObject("example_block") {
//        Block(AbstractBlock.Properties.create(Material.BAMBOO).setLightLevel { 15 }.hardnessAndResistance(3.0f))
//    }
}