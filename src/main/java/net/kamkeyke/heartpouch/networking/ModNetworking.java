package net.kamkeyke.heartpouch.networking;

import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.networking.packet.C2SSetActiveHeartstone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1.0";
    private static int packetId = 0;

    private static int id(){ return packetId++; }

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(HeartPouch.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register(){
        INSTANCE.registerMessage(id(),
                C2SSetActiveHeartstone.class,
                C2SSetActiveHeartstone::toBytes,
                C2SSetActiveHeartstone::new,
                C2SSetActiveHeartstone::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static <MSG> void sendToServer(MSG message){
        INSTANCE.sendToServer(message);
    }
    public static <MSG> void sendToClient(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    public static <MSG> void sendToAllPlayers(MSG message){
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
    public static <MSG> void sendNearPlayer(MSG message, ServerPlayer player, double radius){
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        player.getX(), player.getY(), player.getZ(),
                        radius, player.level().dimension()
                )
        ), message);
    }
}

