package minecrafthdl.block;

import minecrafthdl.MinecraftHDL;
import minecrafthdl.block.blocks.Synthesizer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static minecrafthdl.MinecraftHDL.MODID;

/**
 * Created by Francis on 10/5/2016.
 */


public final class ModBlocks {

    public static Block synthesizer;
    public static ItemBlock itemTargetBlock;



    public static void createBlocks() {
        synthesizer = new Synthesizer("synthesizer");
        ForgeRegistries.BLOCKS.register(synthesizer);

        itemTargetBlock = new ItemBlock(synthesizer);
        itemTargetBlock.setRegistryName(synthesizer.getRegistryName());

        ForgeRegistries.ITEMS.register(itemTargetBlock);

        // MinecraftHDL.MODID + ":" + block.getUnlocalizedName().substring(5), "inventory")
        ModelResourceLocation chinaModelResourceLocation = new ModelResourceLocation(MinecraftHDL.MODID + ":" + itemTargetBlock.getUnlocalizedName().substring(5), "inventory");
        final int DEFAULT_ITEM_SUBTYPE = 0;
        ModelLoader.setCustomModelResourceLocation(itemTargetBlock, DEFAULT_ITEM_SUBTYPE, chinaModelResourceLocation);
    }
}
