package com.example.commandgrant;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Locale;
import java.util.Set;

public class GrantCommands {

    // 不允许通过 /grant 直接授权的命令（防止普通玩家获得权限管理能力）
    private static final Set<String> BLOCKED_COMMANDS = Set.of(
            "grant", "revoke", "perms"
    );

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("grant")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("command", StringArgumentType.word())
                                .executes(ctx -> {
                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                    String command = StringArgumentType.getString(ctx, "command")
                                            .toLowerCase(Locale.ROOT);

                                    if (BLOCKED_COMMANDS.contains(command)) {
                                        ctx.getSource().sendFailure(
                                                Component.literal("该命令不允许被单独授权。")
                                        );
                                        return 0;
                                    }

                                    PermissionManager.grant(target.getUUID(), command);
                                    ctx.getSource().sendSuccess(() ->
                                                    Component.literal("已授予 " + target.getScoreboardName() + " 命令权限：/" + command),
                                            true);
                                    return 1;
                                })
                        )
                )
        );

        dispatcher.register(Commands.literal("revoke")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("command", StringArgumentType.word())
                                .executes(ctx -> {
                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                    String command = StringArgumentType.getString(ctx, "command")
                                            .toLowerCase(Locale.ROOT);

                                    PermissionManager.revoke(target.getUUID(), command);
                                    ctx.getSource().sendSuccess(() ->
                                                    Component.literal("已移除 " + target.getScoreboardName() + " 的命令权限：/" + command),
                                            true);
                                    return 1;
                                })
                        )
                )
        );

        dispatcher.register(Commands.literal("perms")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                            Set<String> commands = PermissionManager.list(target.getUUID());

                            String list = commands.isEmpty()
                                    ? "无"
                                    : String.join(", ", commands);

                            ctx.getSource().sendSuccess(() ->
                                            Component.literal(target.getScoreboardName() + " 拥有权限：" + list),
                                    false);
                            return 1;
                        })
                )
        );
    }
}