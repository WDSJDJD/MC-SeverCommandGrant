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

它允许服务器管理员**单独为某个玩家开放指定命令权限**，而不必给予该玩家完整的 OP 权限。

例如：你可以让普通玩家使用 `/tp`、`/home` 等命令，但禁止他们使用 `/gamemode`、`/give` 等其他 OP 命令。

---

## ✨ 特性

- ✅ 单独授权任意命令（例如 `/tp`、`/sethome`）
- ✅ 权限持久化保存，重启服务器不丢失
- ✅ 管理员可随时查看和撤销已授权命令
- ✅ 内置安全黑名单，防止提权
- ✅ 防止通过 `/execute run` 绕过限制执行敏感命令
- ✅ 纯服务端模组，客户端无需安装

---

## 📥 安装

1. 下载最新的 `.jar` 文件（从 Release 页面或自行构建）
2. 将文件放入服务器根目录的 `mods` 文件夹
3. 启动服务器

> 注意：本模组**只需要安装在服务端**，客户端无需安装。

---

## 🛠️ 管理员命令

| 命令 | 说明 | 示例 |
|------|------|------|
| `/grant <玩家> <命令>` | 授予玩家指定命令权限 | `/grant Steve tp` |
| `/revoke <玩家> <命令>` | 移除玩家指定命令权限 | `/revoke Steve tp` |
| `/perms <玩家>` | 查看玩家已授权的命令列表 | `/perms Steve` |

> 注意：`<命令>` 填写的是**命令根**，例如 `tp`、`home`、`gamemode`，不需要加斜杠。  
> 玩家必须**在线**才能被授权。

---

## 🔐 安全机制

CommandGrant 在设计上考虑了权限安全，防止普通玩家提权：

### 1. 默认禁止授权敏感命令
以下命令**不能通过 `/grant` 授权**：
- `grant`
- `revoke`
- `perms`
- `op`
- `deop`
- `stop`

### 2. 防止 `execute` 绕过
如果玩家拥有 `execute` 权限，但未拥有 `op` 权限，那么以下命令会被**直接拦截**：
```mcfunction
/execute run op 自己
/execute as @p run op 自己
/execute run execute run op 自己
任何通过 execute run 执行敏感命令的尝试都会被拒绝。

3. 手动修改配置文件的风险
虽然本模组没有在运行时对直接执行 op 等命令做额外限制（如果你通过 /grant 或手动修改 JSON 授予了 op，玩家就能执行 op），但强烈建议不要将 op、deop、stop 等命令授权给不信任的玩家。
默认情况下，这些命令无法通过 /grant 授予，只能手动编辑配置文件。

📁 配置文件
授权数据保存在服务器目录下的：

text
config/commandgrant.json
格式示例：

json
{
  "334e0c66-c44b-4694-8a22-a72c437a3d2f": [
    "tp",
    "execute"
  ],
  "18f8c698-49c4-47e3-845c-b6da23c45832": [
    "tp"
  ]
}
键为玩家的 UUID

值为该玩家被授权命令的数组

修改后需要重启服务器才会生效（或使用 /grant / /revoke 命令即时更新）

🔨 构建
如果你想从源码构建，需要：

JDK 21

NeoForge MDK 1.21.1 (21.1.219)

克隆仓库后，在项目根目录执行：

bash
./gradlew build
构建产物位于：

text
build/libs/
通常文件名类似 commandgrant-1.0.0.jar，将该文件放入服务器 mods 文件夹即可。

📜 许可证
本项目采用 MIT License 开源，你可以自由使用、修改和分发，但请保留原作者信息。

🙏 致谢
感谢 NeoForge 团队提供的模组开发框架。

🐛 问题反馈
如果你遇到问题或有建议，请提交 Issue。


------------------------------------------------------------------------------------------------------


CommandGrant is a **server-side NeoForge mod** for Minecraft 1.21.1 and NeoForge 21.1.219.  
It allows server administrators to grant individual players permission to use specific commands without giving them full operator status.

For example, you can let a normal player use `/tp` or `/home`, while keeping them restricted from other OP-only commands like `/gamemode` or `/give`.

---

## ✨ Features

- ✅ Grant permission for any command individually (e.g., `/tp`, `/sethome`)
- ✅ Permissions persist across server restarts
- ✅ Admins can view and revoke granted commands at any time
- ✅ Built-in security blacklist to prevent privilege escalation
- ✅ Prevents bypassing restrictions through `/execute run` chains
- ✅ Server-side only; no client installation required

---

## 📥 Installation

1. Download the latest `.jar` file (from the Releases page or build it yourself)
2. Place the file into the server's `mods` folder
3. Start the server

> Note: This mod **only needs to be installed on the server**. Clients do not need it.

---

## 🛠️ Admin Commands

| Command | Description | Example |
|---------|-------------|---------|
| `/grant <player> <command>` | Grant a player permission to use a specific command | `/grant Steve tp` |
| `/revoke <player> <command>` | Remove a player's permission for a command | `/revoke Steve tp` |
| `/perms <player>` | View a player's granted commands | `/perms Steve` |

> Note: `<command>` should be the **root command** (e.g., `tp`, `home`, `gamemode`) without a slash.  
> The player must be **online** to be granted permissions.

---

## 🔐 Security Mechanisms

CommandGrant is designed with permission security in mind to prevent privilege escalation.

### 1. Restricted commands by default
The following commands **cannot** be granted via `/grant`:
- `grant`
- `revoke`
- `perms`

Other sensitive commands like `op`, `deop`, and `stop` can technically be granted, but this is **not recommended** unless you absolutely trust the player.

### 2. Prevents `execute` bypass
If a player has the `execute` permission but not the `op` permission, the following attempts will be **blocked**:
```mcfunction
/execute run op <player>
/execute as @p run op <player>
/execute run execute run op <player>
Any attempt to run a sensitive command through execute run is denied, even if the player has execute permission.

3. Manual config file edits
Although the mod does not block direct execution of commands like /op if the player has been explicitly granted that permission, we strongly advise against granting op, deop, or stop to untrusted players.
By default, grant, revoke, and perms cannot be granted through the /grant command; they can only be added by manually editing the JSON file.

📁 Configuration File
Granted permissions are stored in:

text
config/commandgrant.json
Example format:

json
{
  "334e0c66-c44b-4694-8a22-a72c437a3d2f": [
    "tp",
    "execute"
  ],
  "18f8c698-49c4-47e3-845c-b6da23c45832": [
    "tp"
  ]
}
Keys are player UUIDs

Values are arrays of granted command roots

Changes require a server restart to take effect (or use /grant / /revoke for immediate updates)

🔨 Building from Source
To build from source, you need:

JDK 21

NeoForge MDK 1.21.1 (21.1.219)

Clone the repository and run:

bash
./gradlew build
The output jar will be in:

text
build/libs/
Typically named something like commandgrant-1.0.0.jar. Place that file into the server's mods folder.

📜 License
This project is licensed under the MIT License. You are free to use, modify, and distribute it, but please retain the original author information.

🙏 Acknowledgments
Thanks to the NeoForge team for providing the modding framework.

🐛 Issues
If you encounter any problems or have suggestions, please submit an Issue.
