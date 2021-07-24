package flysong.nether_reactor;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Random;

public class TileEntityNetherReactorCore extends TileEntity implements ITickableTileEntity {
    public int tick = 0;
    public boolean started = false;

    public TileEntityNetherReactorCore() {
        super(NetherReactor.TileEntityRegistry.NETHER_REACTOR_CORE.get());
    }

    @Override
    public void tick() {
        if (! started) return;
        tick ++;
        if (world.isRemote)
            return;
        if (tick % 40 == 0)
            dropItems();
        if (tick % 80 == 0)
            spawnMobs();
        if (tick == 900)
            end();
    }

    private void end() {
        started = false;
        Random random = new Random();

        for (int i = -8 ; i <= 8 ; i ++) {
            for (int j = -8 ; j <= 8 ; j ++) {
                for (int k = -2 ; k <= 31 ; k ++) {
                    if (world.getBlockState(pos.add(i, k, j)).toString().equals(Blocks.NETHERRACK.getDefaultState().toString())) {
                        if (random.nextInt(8) == 0) {
                            world.setBlockState(pos.add(i, k, j), Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        }

        ((ServerWorld)world).setDayTime(18000);

        for (int i = -1 ; i <= 1 ; i ++) {
            for (int j = -1 ; j <= 1 ; j ++) {
                world.setBlockState(pos.add(i, -1, j), Blocks.OBSIDIAN.getDefaultState());
            }
        }

        world.setBlockState(pos.add(1, 0, 1), Blocks.OBSIDIAN.getDefaultState());
        world.setBlockState(pos.add(-1, 0, 1), Blocks.OBSIDIAN.getDefaultState());
        world.setBlockState(pos.add(1, 0, -1), Blocks.OBSIDIAN.getDefaultState());
        world.setBlockState(pos.add(-1, 0, -1), Blocks.OBSIDIAN.getDefaultState());
        world.setBlockState(pos.add(0, 1, 0), Blocks.OBSIDIAN.getDefaultState());
        world.setBlockState(pos.add(1, 1, 0), Blocks.OBSIDIAN.getDefaultState());
        world.setBlockState(pos.add(0, 1, 1), Blocks.OBSIDIAN.getDefaultState());
        world.setBlockState(pos.add(-1, 1, 0), Blocks.OBSIDIAN.getDefaultState());
        world.setBlockState(pos.add(0, 1, -1), Blocks.OBSIDIAN.getDefaultState());
        world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
    }

    private double getRandomDouble(Random random) {
        return random.nextDouble() * 16 - 8;
    }

    private void dropItems() {
        LootTable table = ServerLifecycleHooks.getCurrentServer().getLootTableManager().getLootTableFromLocation(
                new ResourceLocation(NetherReactor.MODID, "nether_reactor_items"));
        Random random = new Random();
        for (ItemStack item : table.generate(new LootContext.Builder((ServerWorld)world)
                .withParameter(LootParameters.BLOCK_STATE, this.getBlockState())
                .withParameter(LootParameters.ORIGIN, new Vector3d(this.pos.getX(), this.pos.getY(), this.pos.getZ()))
                .withParameter(LootParameters.TOOL, ItemStack.EMPTY)
                .build(LootParameterSets.BLOCK))) {
            world.addEntity(new ItemEntity(world, this.pos.getX() + getRandomDouble(random), this.pos.getY() - 1 , this.pos.getZ() + getRandomDouble(random), item));
        }
    }

    private void spawnMobs() {
        EntityType<?>[] entities = new EntityType<?>[] {
                EntityType.BLAZE,
                EntityType.SKELETON,
                EntityType.ZOMBIE,
                EntityType.ZOMBIFIED_PIGLIN,
                EntityType.HOGLIN,
                EntityType.WITHER_SKELETON
        };
        Random random = new Random();
        entities[random.nextInt(5)].spawn((ServerWorld) world, null, null,
                new BlockPos(this.pos.getX() + random.nextInt(16) - 8, this.pos.getY() - 1, this.pos.getZ() + random.nextInt(16) - 8),
                SpawnReason.SPAWNER, false, false);
    }
}
