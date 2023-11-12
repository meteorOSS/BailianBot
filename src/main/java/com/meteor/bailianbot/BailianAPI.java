package com.meteor.bailianbot;

import com.aliyun.broadscope.bailian.sdk.AccessTokenClient;
import com.aliyun.broadscope.bailian.sdk.ApplicationClient;
import com.aliyun.broadscope.bailian.sdk.models.BaiLianConfig;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsRequest;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsResponse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BailianAPI {
    public class Context{
        private String sessionId;
        private long lastTime;

        public Context(String sessionId, long lastTime) {
            this.sessionId = sessionId;
            this.lastTime = lastTime;
        }

        public boolean isOut(){
            return (System.currentTimeMillis()-lastTime)>(2*60*60*1000);
        }
    }
    private BailianBot plugin;
    private AccessTokenClient accessTokenClient;
    private BaiLianConfig config;
    private String appId;
    public Map<String,Context> contextMap;
    public static BailianAPI STORE;
    public static void init(BailianBot plugin){
        STORE = new BailianAPI(plugin);
    }
    private BailianAPI(BailianBot plugin){
        this.plugin = plugin;
        ConfigurationSection setting = plugin.getConfig().getConfigurationSection("setting");
        try {
            this.accessTokenClient = new AccessTokenClient(setting.getString("accessKeyId"),
                    setting.getString("accessKeySecret"),setting.getString("agentKey"));
            this.appId = setting.getString("appId");
            this.contextMap = new ConcurrentHashMap<>();
            this.config = new BaiLianConfig()
                    .setApiKey(accessTokenClient.getToken());
        }catch (Exception e){
            plugin.getLogger().info("请正确填写api信息");
        }
    }

    public void request(String player,CallBack callBack,String prompt){
        Bukkit.getScheduler().runTaskAsynchronously(plugin,()->{

            String sessionId = UUID.randomUUID().toString();
            if(contextMap.containsKey(player)&&!contextMap.get(player).isOut()){
                sessionId = contextMap.get(player).sessionId;
            }

            CompletionsRequest request = new CompletionsRequest()
                    .setAppId(appId)
                    .setPrompt(prompt);

            if(sessionId!=null) request = request.setSessionId(sessionId);

            ApplicationClient client = new ApplicationClient(config);
            CompletionsResponse response = client.completions(request);
            Bukkit.getScheduler().runTask(plugin,()->callBack.call(response));

            contextMap.put(player,new Context(sessionId,System.currentTimeMillis()));
        });


    }






}
