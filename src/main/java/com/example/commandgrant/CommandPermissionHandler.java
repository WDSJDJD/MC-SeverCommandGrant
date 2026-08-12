package com.example.commandgrant;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.CommandEvent;

import java.util.Set;

public class CommandPermissionHandler {

    // execute 内部 run 子命令不允许的命令（包含 execute 自身防止嵌套绕过）
    private static final Set<String> BLOCKED_EXECUTE_SUBCOMMANDS = Set.of(
            "grant", "revoke", "perms", "op", "deop", "stop", "execute"
    );

    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        CommandSourceStack source = event.getParseResults().getContext().getSource();

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // 管理员直接放行
        if (source.hasPermission(4)) {
            return;
        }

        String input = event.getParseResults().getReader().getString();
        String commandRoot = extractCommandRoot(input);

        if (commandRoot == null) {
            return;
        }

        // 如果是 execute 命令，检查内部 run 子命令
        if ("execute".equals(commandRoot)) {
            String innerRoot = extractInnerCommandRoot(input);
            if (innerRoot != null && BLOCKED_EXECUTE_SUBCOMMANDS.contains(innerRoot)) {
                // 内部命令被禁止，不提升权限，原版权限系统会拒绝执行
                source.sendFailure(Component.literal("你没有权限执行该命令。"));
                event.setCanceled(true);
                return;
            }
        }

        // 检查玩家是否拥有该命令的授权（直接命令如 op 也在此检查，有授权则提升权限执行）
        if (!PermissionManager.hasPermission(player.getUUID(), commandRoot)) {
            return;
        }

        // 玩家拥有授权：取消原版执行，用高权限 CommandSourceStack 重新执行
        event.setCanceled(true);

        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        CommandSourceStack elevatedSource = source.withPermission(4);

        String command = input.trim();
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        try {
            CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
            dispatcher.execute(command, elevatedSource);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal(e.getMessage()));
        }
    }

    private String extractCommandRoot(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        String s = input.trim();
        if (s.startsWith("/")) {
            s = s.substring(1);
        }

        String[] parts = s.split("\\s+");
        return parts.length > 0 ? parts[0].toLowerCase() : null;
    }

    /**
     * 从 execute 命令中提取 run 子命令后的第一个词（命令根）
     * 例如：execute as @p run op 自己  ->  op
     */
    private String extractInnerCommandRoot(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        String s = input.trim();
        if (s.startsWith("/")) {
            s = s.substring(1);
        }

        String[] parts = s.split("\\s+");
        // 查找最后一个 "run" 的位置
        int lastRunIndex = -1;
        for (int i = 0; i < parts.length; i++) {
            if ("run".equalsIgnoreCase(parts[i])) {
                lastRunIndex = i;
            }
        }

        if (lastRunIndex == -1 || lastRunIndex + 1 >= parts.length) {
            return null;
        }

        return parts[lastRunIndex + 1].toLowerCase();
    }
}