package com.andreybali.lego;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BlockIterator;

import java.util.HashMap;

public class LookAt implements Listener {
    private final Lego plugin;
    public LookAt(Lego plugin) {
        this.plugin = plugin;
    }
    HashMap<Player, Material> bsType = new HashMap<>();
    HashMap<Player, Location> bsLoc = new HashMap<>();
    HashMap<Player, BlockData> bsData = new HashMap<>();


    public final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);

        Block lastBlock = iter.next();
        Block notLastBlock = lastBlock;
        while (iter.hasNext()) {
            notLastBlock = lastBlock;
            lastBlock = iter.next();
            if (lastBlock.isEmpty() || lastBlock.isLiquid()) {
                continue;
            }
            break;
        }
        return notLastBlock;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(!bsType.containsKey(event.getPlayer())){
            Block b = getTargetBlock(event.getPlayer(), 200);
            bsType.put(event.getPlayer(),b.getType());
            bsLoc.put(event.getPlayer(),b.getLocation());
            bsData.put(event.getPlayer(),b.getBlockData());
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event){
        Block block1 = getTargetBlock(event.getPlayer(),200);
        Location newLoc = block1.getLocation();
        int nx = (newLoc.getBlockX())/8; if(nx<=0) nx--;
        int ny = (newLoc.getBlockY())/6; if(ny<=0) ny--;
        int nz = (newLoc.getBlockZ())/8; if(nz<=0) nz--;
        newLoc.setX(nx*8);
        newLoc.setY(ny*6);
        newLoc.setZ(nz*8);
        Block block = newLoc.getBlock();
        if(block.getLocation().equals(bsLoc.get(event.getPlayer()))) return;

        Location bsOldLoc = bsLoc.get(event.getPlayer());
        Material bsOldType = bsType.get(event.getPlayer());
        BlockData bsOldbd = bsData.get(event.getPlayer());

        Block oldBlock = bsOldLoc.getBlock();
        oldBlock.setType(bsOldType);
        oldBlock.setBlockData(bsOldbd);


        Material inHand = event.getPlayer().getInventory().getItemInMainHand().getType();
        String blockof = null;
        if(inHand.equals(Material.RED_CONCRETE)) blockof = "red";
        if(inHand.equals(Material.BLUE_CONCRETE)) blockof = "blue";
        if(inHand.equals(Material.YELLOW_CONCRETE)) blockof = "yellow";
        if(inHand.equals(Material.WHITE_CONCRETE)) blockof = "white";
        if(blockof == null) return;

        Location ol = bsLoc.get(event.getPlayer());
        Material om = bsType.get(event.getPlayer());
        BlockData od = bsData.get(event.getPlayer());

        bsLoc.put(event.getPlayer(), block.getLocation());
        bsType.put(event.getPlayer(), block.getType());
        bsData.put(event.getPlayer(), block.getBlockData());

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        String command = "setblock "+x+" "+y+" "+z+" minecraft:structure_block[mode=load]{author:\"AndreyBali\",ignoreEntities:1b,integrity:1.0f,metadata:\"\",mirror:\"NONE\",mode:\"LOAD\",name:\"minecraft:"+blockof+"_2_3_2\",posX:0,posY:1,posZ:0,powered:0b,rotation:\"NONE\",seed:0L,showair:0b,showboundingbox:1b,sizeX:8,sizeY:7,sizeZ:8}";
        Bukkit.getServer().getScheduler().runTask(plugin, ()->{
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),command);
        });
        Bukkit.getServer().getScheduler().runTask(plugin, ()->{
            Block aldBlock = ol.getBlock();
            aldBlock.setType(om);
            aldBlock.setBlockData(od);
        });
    }
    @EventHandler
    public void PlayerClickEvent(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_AIR&& event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Material inHand = event.getPlayer().getInventory().getItemInMainHand().getType();

        String blockof = null;
        if(inHand.equals(Material.RED_CONCRETE)) blockof = "red";
        if(inHand.equals(Material.BLUE_CONCRETE)) blockof = "blue";
        if(inHand.equals(Material.YELLOW_CONCRETE)) blockof = "yellow";
        if(inHand.equals(Material.WHITE_CONCRETE)) blockof = "white";
        if(blockof == null) return;

        Block block1 = getTargetBlock(event.getPlayer(),200);
        Location newLoc = block1.getLocation();
        int nx = (newLoc.getBlockX())/8; if(nx<0) nx--;
        int ny = (newLoc.getBlockY())/6; if(ny<0) ny--;
        int nz = (newLoc.getBlockZ())/8; if(nz<0) nz--;
        newLoc.setX(nx*8);
        newLoc.setY(ny*6+1);
        newLoc.setZ(nz*8);
        Block redstoneBlock = newLoc.getBlock();
        String command = "setblock "+newLoc.getBlockX()+" "+(newLoc.getBlockY()-1)+" "+newLoc.getBlockZ()+" minecraft:structure_block[mode=load]{author:\"AndreyBali\",ignoreEntities:1b,integrity:1.0f,metadata:\"\",mirror:\"NONE\",mode:\"LOAD\",name:\"minecraft:"+blockof+"_2_3_2\",posX:0,posY:1,posZ:0,powered:0b,rotation:\"NONE\",seed:0L,showair:0b,showboundingbox:1b,sizeX:8,sizeY:7,sizeZ:8}";
        Bukkit.getServer().getScheduler().runTask(plugin, ()->{
            redstoneBlock.setType(Material.AIR);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"setblock "+newLoc.getBlockX()+" "+(newLoc.getBlockY()-1)+" "+newLoc.getBlockZ()+" air");
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),command);
            redstoneBlock.setType(Material.REDSTONE_BLOCK);
        });
    }
}