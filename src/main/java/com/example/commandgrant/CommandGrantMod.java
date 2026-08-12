package com.example.commandgrant;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CommandGrantMod.MODID)
public class CommandGrantMod {

    public static final String MODID = "commandgrant";

    public CommandGrantMod() {
        PermissionManager.load();
        NeoForge.EVENT_BUS.register(new CommandPermissionHandler());
        NeoForge.EVENT_BUS.register(new GrantCommands());
    }
}