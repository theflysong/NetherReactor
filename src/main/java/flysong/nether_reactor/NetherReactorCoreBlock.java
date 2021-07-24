package flysong.nether_reactor;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecated")
public class NetherReactorCoreBlock extends Block {
    public static IntegerProperty STAGE = IntegerProperty.create("stage", 0, 1);

    public NetherReactorCoreBlock() {
        super(AbstractBlock.Properties.create(Material.IRON));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.get(STAGE) == 1)
            return 5;
        return 0;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityNetherReactorCore();
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
        if (pos.getY() <= 5 || pos.getY() >= 217) {
            player.sendMessage(new TranslationTextComponent("msg.nether_reactor.hor"), Util.DUMMY_UUID);
            return ActionResultType.FAIL;
        }
        for (int i = -1 ; i <= 1 ; i ++) { //x axis
            for (int j = -1 ; j <= 1 ; j ++) { //y axis
                for (int k = -1 ; k <= 1 ; k ++) { // z axis
                    if (! worldIn.getBlockState(pos.add(i, j, k)).toString().equals(
                            NetherReactors.REACTOR_STRUCTURE[j + 1][k + 1][i + 1].toString())) {
                        player.sendMessage(new TranslationTextComponent("msg.nether_reactor.ncp"), Util.DUMMY_UUID);
                        return ActionResultType.FAIL;
                    }
                }
            }
        }
        player.sendMessage(new TranslationTextComponent("msg.nether_reactor.active"), Util.DUMMY_UUID);
        for (int i = -1 ; i <= 1 ; i ++) {
            for (int j = -1 ; j <= 1 ; j ++) {
                worldIn.setBlockState(pos.add(i, -1, j), NetherReactor.BlockRegistry.GLOWING_OBSIDIAN.get().getDefaultState());
            }
        }

        worldIn.setBlockState(pos.add(1, 0, 1), NetherReactor.BlockRegistry.GLOWING_OBSIDIAN.get().getDefaultState());
        worldIn.setBlockState(pos.add(-1, 0, 1), NetherReactor.BlockRegistry.GLOWING_OBSIDIAN.get().getDefaultState());
        worldIn.setBlockState(pos.add(1, 0, -1), NetherReactor.BlockRegistry.GLOWING_OBSIDIAN.get().getDefaultState());
        worldIn.setBlockState(pos.add(-1, 0, -1), NetherReactor.BlockRegistry.GLOWING_OBSIDIAN.get().getDefaultState());
        worldIn.setBlockState(pos.add(0, 1, 0), NetherReactor.BlockRegistry.GLOWING_OBSIDIAN.get().getDefaultState());
        worldIn.setBlockState(pos.add(1, 1, 0), NetherReactor.BlockRegistry.GLOWING_OBSIDIAN.get().getDefaultState());
        worldIn.setBlockState(pos.add(0, 1, 1), NetherReactor.BlockRegistry.GLOWING_OBSIDIAN.get().getDefaultState());
        worldIn.setBlockState(pos.add(-1, 1, 0), NetherReactor.BlockRegistry.GLOWING_OBSIDIAN.get().getDefaultState());
        worldIn.setBlockState(pos.add(0, 1, -1), NetherReactor.BlockRegistry.GLOWING_OBSIDIAN.get().getDefaultState());

        worldIn.setBlockState(pos, state.with(STAGE, 1));

        _generatorStructure(worldIn, pos);

        ((TileEntityNetherReactorCore)worldIn.getTileEntity(pos)).started = true;
        ((TileEntityNetherReactorCore)worldIn.getTileEntity(pos)).tick = 0;
        return ActionResultType.SUCCESS;
    }

    private void _generatorStructure(World worldIn, BlockPos pos) {
        NetherReactor.genStructure(new ResourceLocation(NetherReactor.MODID, "netherrack"), worldIn,
                1.0f, pos.add(-8, -1, -8));
        NetherReactor.genStructure(new ResourceLocation(NetherReactor.MODID, "floor_netherrack"), worldIn,
                1.0f, pos.add(-8, -3, -8));
    }
}
