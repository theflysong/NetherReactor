package flysong.nether_reactor;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(value = NetherReactor.MODID)
public class NetherReactor {
    public static final String MODID = "nether_reactor";
    public static final String NAME = "NetherReactor";
    public static final String MC_VERSION = "1.16.5";
    public static final String MOD_VERSION = "1.1.0";
    public static final String VERSION = MC_VERSION + "-" + MOD_VERSION;

    private static NetherReactor INSTANCE = null;

    private final Logger logger = LogManager.getLogger(NetherReactor.NAME);

    public static class BlockItemRegistry {
        public static DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

        public static RegistryObject<Item> NETHER_REACTOR_CORE = REGISTER.register("nether_reactor_core", ()->new BlockItem(BlockRegistry.NETHER_REACTOR_CORE.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
    }

    public static class BlockRegistry {
        public static DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

        public static RegistryObject<Block> NETHER_REACTOR_CORE = REGISTER.register("nether_reactor_core", NetherReactorCoreBlock::new);
        public static RegistryObject<Block> GLOWING_OBSIDIAN = REGISTER.register("glowing_obsidian", ()->new Block(
                Block.Properties.create(Material.GLASS).sound(SoundType.GLASS).setLightLevel(state->5)));
    }

    public static class TileEntityRegistry {
        public static DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

        public static RegistryObject<TileEntityType<TileEntityNetherReactorCore>> NETHER_REACTOR_CORE =
                REGISTER.register("nether_reactor_core", () -> TileEntityType.Builder
                        .create(TileEntityNetherReactorCore::new, BlockRegistry.NETHER_REACTOR_CORE.get())
                        .build(null));
    }

    public NetherReactor() {
        INSTANCE = this;

        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(this::setupClient);

        BlockRegistry.REGISTER.register(bus);
        BlockItemRegistry.REGISTER.register(bus);
        TileEntityRegistry.REGISTER.register(bus);
    }

    public static NetherReactor getInstance() {
        return INSTANCE;
    }

    private void setup(final FMLCommonSetupEvent event) {
        logger.info("Hello Minecraft!");
    }

    private void setupClient(final FMLClientSetupEvent event) {
    }

    public static void genStructure(ResourceLocation name, World world, float integrity, BlockPos center) {
        if (world.isRemote || name == null)
            return;
        ServerWorld serverworld = (ServerWorld)world;
        TemplateManager templatemanager = serverworld.getStructureTemplateManager();
        Template template;
        try {
            template = templatemanager.getTemplate(name);
        } catch (ResourceLocationException e) {
            e.printStackTrace();
            return;
        }
        PlacementSettings placementsettings = new PlacementSettings()
                .setMirror(Mirror.NONE)
                .setRotation(Rotation.NONE)
                .setIgnoreEntities(false)
                .setChunk(null);
        if (integrity < 1.0F) {
            placementsettings.clearProcessors().addProcessor(
                    new IntegrityProcessor(
                            MathHelper.clamp(integrity, 0.0F, 1.0F)))
                    .setRandom(new Random(center.toLong()));
        }
        template.func_237152_b_(serverworld, center, placementsettings, new Random(center.toLong()));
    }

    public static Logger getLogger() {
        return INSTANCE.logger;
    }
}