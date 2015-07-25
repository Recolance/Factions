package me.recolance.factions.menu;

import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.ChatMessage;
import net.minecraft.server.v1_8_R2.ContainerAnvil;
import net.minecraft.server.v1_8_R2.EntityHuman;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.PacketPlayOutOpenWindow;

import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AnvilContainer extends ContainerAnvil {

    protected AnvilContainer(EntityHuman entity) {
        super(entity.inventory, entity.world, new BlockPosition(0,0,0), entity); 
    }

    public static Inventory openAnvil(Player player) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        AnvilContainer container = new AnvilContainer(p);

        int c = p.nextContainerCounter();
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing", new Object[]{}), 0));
        p.activeContainer = container;
        p.activeContainer.windowId = c;
        p.activeContainer.addSlotListener(p);

        return container.getBukkitView().getTopInventory();
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }
}
