package flysong.nether_reactor;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

@SuppressWarnings("deprecated")
public class NetherReactorCoreBlock extends Block {
    public NetherReactorCoreBlock() {
        super(AbstractBlock.Properties.create(Material.IRON));
    }

    public static class NetherReactors {
        public static BlockState[][][] REACTOR_STRUCTURE = new BlockState[][][] {
                {
                        {Blocks.GOLD_BLOCK.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState()},
                        {Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState()},
                        {Blocks.GOLD_BLOCK.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState()}
                },
                {
                        {Blocks.COBBLESTONE.getDefaultState(), Blocks.AIR.getDefaultState(), Blocks.COBBLESTONE.getDefaultState()},
                        {Blocks.AIR.getDefaultState(), NetherReactor.BlockRegistry.NETHER_REACTOR_CORE.get().getDefaultState(), Blocks.AIR.getDefaultState()},
                        {Blocks.COBBLESTONE.getDefaultState(), Blocks.AIR.getDefaultState(), Blocks.COBBLESTONE.getDefaultState()}
                },
                {
                        {Blocks.AIR.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.AIR.getDefaultState()},
                        {Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState()},
                        {Blocks.AIR.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.AIR.getDefaultState()}
                }
        };
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote || handIn != Hand.MAIN_HAND)
            return ActionResultType.PASS;
        for (int i = -1 ; i <= 1 ; i ++) { //x axis
            for (int j = -1 ; j <= 1 ; j ++) { //y axis
                for (int k = -1 ; k <= 1 ; k ++) { // z axis
                    NetherReactor.getLogger().info(worldIn.getBlockState(pos.add(i, j, k)).toString() + ":"
                            + NetherReactors.REACTOR_STRUCTURE[j + 1][k + 1][i + 1].toString());
                    if (! worldIn.getBlockState(pos.add(i, j, k)).toString().equals(
                            NetherReactors.REACTOR_STRUCTURE[j + 1][k + 1][i + 1].toString())) {
                        player.sendMessage(new StringTextComponent("Not the correct pattern!"), Util.DUMMY_UUID);
                        return ActionResultType.FAIL;
                    }
                }
            }
        }
        player.sendMessage(new StringTextComponent("Active!"), Util.DUMMY_UUID);

        //TODO
        return ActionResultType.SUCCESS;
    }
}
