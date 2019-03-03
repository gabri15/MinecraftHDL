package minecrafthdl.block.blocks;

import GraphBuilder.GraphBuilder;
import minecrafthdl.MHDLException;
import minecrafthdl.MinecraftHDL;
import minecrafthdl.block.BasicBlock;
import minecrafthdl.gui.MinecraftHDLGuiHandler;
import minecrafthdl.synthesis.Circuit;
import minecrafthdl.synthesis.IntermediateCircuit;
import minecrafthdl.synthesis.LogicGates;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Francis on 10/28/2016.
 */
public class Synthesizer extends BasicBlock {

    public static String file_to_gen;
    public static int check_threshold = 100;

    public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");

    private int check_counter = 0;
    private boolean to_check = false;
    private Circuit c_check = null;
    private BlockPos p_check = null;

    public Synthesizer(String unlocalizedName) {
        super(unlocalizedName);
        this.setRegistryName(unlocalizedName);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TRIGGERED, false));
        this.setTickRandomly(true);
        System.out.println("hello");
    }



    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote){
            playerIn.openGui(MinecraftHDL.instance, MinecraftHDLGuiHandler.SYNTHESISER_GUI_ID, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
        }

        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if(!worldIn.isRemote) {
            if(!state.getValue(TRIGGERED)){
                if (worldIn.getRedstonePower(pos.north(), EnumFacing.NORTH) > 0) {
                    //Negative Z is receiving power
                    worldIn.setBlockState(pos, state.withProperty(TRIGGERED, true));

                    if (Synthesizer.file_to_gen != null){
                        synth_gen(worldIn, pos);

                    }
                }else if (worldIn.getRedstonePower(pos.east(), EnumFacing.EAST) > 0) {
                    //Negative X is receiving power
                    worldIn.setBlockState(pos, state.withProperty(TRIGGERED, true));

                    if (Synthesizer.file_to_gen != null){
                        synth_gen(worldIn, pos);

                    }

                }else if (worldIn.getRedstonePower(pos.south(), EnumFacing.SOUTH) > 0) {
                    //Positive Z is receiving power
                    worldIn.setBlockState(pos, state.withProperty(TRIGGERED, true));

                    if (Synthesizer.file_to_gen != null){
                        synth_gen(worldIn, pos);

                    }
                }else if (worldIn.getRedstonePower(pos.west(), EnumFacing.WEST) > 0) {
                    //Positive X is receiving power
                    worldIn.setBlockState(pos, state.withProperty(TRIGGERED, true));

                    if (Synthesizer.file_to_gen != null){
                        synth_gen(worldIn, pos);
                    }
                }else if (worldIn.getRedstonePower(pos.up(), EnumFacing.UP) > 0) {
                    //Positive Y is receiving power
                    worldIn.setBlockState(pos, state.withProperty(TRIGGERED, true));
                    LogicGates.XOR().placeInWorld(worldIn, pos, EnumFacing.NORTH);
                }else if (worldIn.getRedstonePower(pos.down(), EnumFacing.DOWN) > 0) {
                    //Negative Y is receiving power
                } else {
                    worldIn.setBlockState(pos, state.withProperty(TRIGGERED, false));
                }
            } else {
                if (!worldIn.isBlockPowered(pos)) {
                    worldIn.setBlockState(pos, state.withProperty(TRIGGERED, false));
                }
            }

            worldIn.notifyNeighborsOfStateChange(pos, this,true);
        }
    }



    private void synth_gen(World worldIn, BlockPos pos){
        try {

            IntermediateCircuit ic = new IntermediateCircuit();
            ic.loadGraph(GraphBuilder.buildGraph(Synthesizer.file_to_gen));
            ic.buildGates();
            ic.routeChannels();
            this.c_check = ic.genCircuit();
            c_check.placeInWorld(worldIn, pos, EnumFacing.NORTH);
            this.to_check = true;
            this.p_check = pos;

        } catch (Exception e){
            Minecraft.getMinecraft().player.sendChatMessage("An error occurred while generating the circuit, check the logs! Sorry!");
            e.printStackTrace();
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TRIGGERED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TRIGGERED, (meta) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TRIGGERED) ? 1 : 0;
    }

}
