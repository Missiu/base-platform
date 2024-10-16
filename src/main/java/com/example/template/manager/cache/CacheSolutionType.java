package com.example.template.manager.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 本地缓存
 * 手动加载（Cache）、自动加载（LoadingCache）、手动异步加载（AsyncCache）和自动异步加载（AsyncLoadingCache）
 * 基于大小回收，基于时间回收，基于引用回收
 * <p>
 * redis缓存
 * Cache-Aside Pattern（旁路缓存模式）
 * Read-Through Cache Pattern（读穿透模式）
 * Write-Through Cache Pattern（写穿透模式）
 * Write-Behind Pattern（异步缓存写入模式）
 * 淘汰策略：Redis支持多种淘汰策略如noeviction（不淘汰）、allkeys-lru（整体LRU）、volatile-lru（仅针对设置了过期时间的键执行LRU），以及基于随机选择或TTL的策略
 * <p>
 * 二级缓存
 * 两级缓存：第一级是本地缓存，第二级是Redis缓存。查询时先尝试本地缓存，未命中再查Redis缓存，最后才访问数据库。写入时可以考虑先更新数据库，然后清除本地缓存和Redis缓存中的对应条目，或者采用更复杂的同步机制。
 * 懒加载缓存：只在需要的时候才将数据加载到本地缓存中，而不是在数据插入时就立即缓存。这样可以节省内存空间，同时保持数据的新鲜度
 * 热点数据预热：对于一些已知的热点数据，可以在服务启动时预先加载到本地缓存中，以便快速响应用户的请求。
 * 失效传播：当数据发生更改时，可以通过某种机制（例如发布/订阅模式）通知所有相关的缓存节点，使得它们能够及时更新或失效本地缓存中的相应条目
 *
 * @author hzh
 */

@Getter
@AllArgsConstructor
public enum CacheSolutionType {

    // 本地缓存策略

    /**
     * 手动加载本地缓存
     */
    LOCAL_MANUAL("LC", "Manual"),

    /**
     * 自动加载本地缓存
     */
    LOCAL_AUTO("LC", "Auto"),

    // Redis缓存策略

    /**
     * Redis缓存 - 旁路缓存模式
     * 通过Cache-Aside Pattern，缓存未命中时从数据库加载数据并缓存。
     */
    REDIS_CACHE_ASIDE("RC", "CacheAside"),

    // 二级缓存策略

    /**
     * 二级缓存 - 两级缓存
     * 使用两级缓存（本地缓存 + Redis缓存），以本地缓存优先。
     */
    TWO_LEVEL_CACHE("TLC", "TwoLevel"),

    /**
     * 二级缓存 - 热点数据预热
     * 在启动时预先加载热点数据到本地缓存。
     */
    HOT_DATA_PRELOAD("TLC", "Preload"),

    /**
     * 二级缓存 - 缓存失效传播
     * 当数据更新时，通过发布/订阅机制传播缓存失效消息。
     */
    CACHE_INVALIDATION_PROPAGATION("TLC", "Invalidation");

    private final String type;    // 缓存类型
    private final String policy;  // 缓存策略

    /**
     * 返回缓存类型和策略组合的字符串表示形式，便于日志和调试输出。
     *
     * @return 缓存类型和策略组合的组合字符串
     */
    @Override
    public String toString() {
        return type + ": " + policy;
    }
}






