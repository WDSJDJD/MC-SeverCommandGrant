CommandGrant – Granular Command Permission Mod for NeoForge

CommandGrant is a server-side NeoForge mod for Minecraft 1.21.1 (NeoForge 21.1.219) that allows server administrators to grant individual players permission to use specific commands without giving them full operator status.
With simple commands like /grant <player> <command>, admins can allow a player to use /tp, /home, or other commands while keeping them restricted from OP-only commands.
All granted permissions are saved to a JSON file and persist across server restarts.
The mod also includes built-in security measures to prevent privilege escalation: it blocks direct execution of sensitive commands like /op and /stop unless explicitly granted, and it prevents players from bypassing restrictions through /execute run chains.
This makes it ideal for servers that need fine-grained control over player abilities without complex permission plugins.

CommandGrant 是一个 NeoForge 服务端模组，适用于 Minecraft 1.21.1 和 NeoForge 21.1.219。它允许管理员单独为某个玩家开放指定命令权限，而不必给予完整 OP。
通过 /grant 玩家 命令 即可授权，例如允许玩家使用 /tp，但其他需要 OP 的命令仍然不可用。权限数据保存在 JSON 文件中，重启服务器也不会丢失。
模组内置安全机制：敏感命令默认不可被授权，且能防止玩家利用 /execute run 绕过限制执行 OP 命令。
适合需要精细权限管理但不想安装复杂权限插件的服务器。


