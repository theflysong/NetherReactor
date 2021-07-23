package flysong.nether_reactor;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
    }

    public NetherReactor() {
        INSTANCE = this;

        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(this::setupClient);

        BlockRegistry.REGISTER.register(bus);
        BlockItemRegistry.REGISTER.register(bus);
    }

    public static NetherReactor getInstance() {
        return INSTANCE;
    }

    private void setup(final FMLCommonSetupEvent event) {
        logger.info("Hello Minecraft!");
    }

    private void setupClient(final FMLClientSetupEvent event) {
    }

    public static Logger getLogger() {
        return INSTANCE.logger;
    }
}