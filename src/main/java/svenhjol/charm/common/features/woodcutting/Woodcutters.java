package svenhjol.charm.common.features.woodcutting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.CommonRegistry;

@FeatureDefinition(side=Side.Common, description="A Stonecutter-style workstation for Charm Woodcutting recipes.")
public final class Woodcutters extends SidedFeature {
    private static Woodcutters INSTANCE;
    public final Registerable<Block> block; public final Registerable<Item> item; public final Registerable<MenuType<WoodcutterMenu>> menu; public final Registerable<Holder<PoiType>> poi;
    public final WoodcutterHandlers handlers; public final WoodcutterRegisters registers;
    public Woodcutters(Mod mod) { super(mod); INSTANCE=this; handlers=new WoodcutterHandlers(this); registers=new WoodcutterRegisters(this);
        block=new Registerable<>(this,()->Registry.register(BuiltInRegistries.BLOCK,id("woodcutter"),new WoodcutterBlock(Block.Properties.ofFullCopy(Blocks.STONECUTTER).setId(ResourceKey.create(Registries.BLOCK,id("woodcutter"))))));
        item=new Registerable<>(this,()->Registry.register(BuiltInRegistries.ITEM,id("woodcutter"),new BlockItem(block.get(),new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id("woodcutter"))))));
        menu=new Registerable<>(this,()->Registry.register(BuiltInRegistries.MENU,id("woodcutter"),new MenuType<>(WoodcutterMenu::new,net.minecraft.world.flag.FeatureFlags.VANILLA_SET)));
        var states = java.util.List.copyOf(block.get().getStateDefinition().getPossibleStates());
        poi=new Registerable<>(this,()->Registry.registerForHolder(BuiltInRegistries.POINT_OF_INTEREST_TYPE,registryId("woodcutter"),new PoiType(java.util.Set.copyOf(states),1,1)));
        CommonRegistry.forFeature(this).pointOfInterestBlockStates(poi, () -> states);
    }
    public static Woodcutters feature(){return INSTANCE;}
    public static final class WoodcutterBlock extends StonecutterBlock {
        public WoodcutterBlock(Properties p){super(p);}
        @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player pl,BlockHitResult h){if(!l.isClientSide())pl.openMenu(new SimpleMenuProvider((id,i,o)->new WoodcutterMenu(id,i,net.minecraft.world.inventory.ContainerLevelAccess.create(l,p)),Component.translatable("container.charm.woodcutter")));return InteractionResult.SUCCESS;}
    }
}
