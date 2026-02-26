package net.kamkeyke.heartpouch.networking;

import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.networking.packet.C2SSetActiveHeartstone;
import net.kamkeyke.raccooncore.networking.RaccoonNetworking;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1.0";

    public static final RaccoonNetworking INSTANCE = new RaccoonNetworking(HeartPouch.MODID, PROTOCOL_VERSION);

    public static void register(){
        C2SSetActiveHeartstone.register(INSTANCE);
    }
}

