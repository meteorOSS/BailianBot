package com.meteor.bailianbot;

import com.aliyun.broadscope.bailian.sdk.models.CompletionsResponse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class BailianBot extends JavaPlugin implements Listener {
    private Metrics metrics;
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        BailianAPI.init(this);
        getServer().getPluginManager().registerEvents(this,this);
        metrics = new Metrics(this,20237);
        getLogger().info("插件文档地址: doc.zsenhe.com");
        getLogger().info("插件交流群: 653440235");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("ciao~");
    }



    @EventHandler
    void onChat(AsyncPlayerChatEvent playerChatEvent){
        String message = playerChatEvent.getMessage();
        if(message.contains(getConfig().getString("message.sign"))){
            Player player = playerChatEvent.getPlayer();
            BailianAPI.STORE.request(player.getName(), new CallBack() {
                @Override
                public void call(CompletionsResponse completionsResponse) {
                    String text = completionsResponse.getData().getText();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',getConfig().getString("message.prefix")));
                    player.sendMessage(text.replaceAll("<sup>(.*?)<\\/sup>",""));
                }
            },message.replace(getConfig().getString("message.sign"),""));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.isOp()){
            reloadConfig();
            BailianAPI.init(this);
            sender.sendMessage(getName()+"-> 重载配置文件完成!");
        }
        return true;
    }
}
