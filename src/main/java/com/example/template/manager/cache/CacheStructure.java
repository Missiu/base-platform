package com.example.template.manager.cache;

/**
 * CacheStructure 定义了支持的缓存数据结构。
 * 这些结构与 Redis 中的常见数据结构一致，也适用于本地缓存的扩展。
 *
 * @author hzh
 */
public class CacheStructure {

    /**
     * String：最基本的数据类型，用于存储字符串、整数或浮点数。
     * Redis的字符串是二进制安全的，可以存储任何格式的数据。
     */
    public static final String STRING = "STRING";

    /**
     * List：列表类型的有序集合，允许在列表的两端进行快速插入和删除操作。
     * 适合用作队列或栈的数据结构。
     */
    public static final String LIST = "LIST";

    /**
     * Set：无序且不重复的元素集合。非常适合于成员关系判断，例如检查用户是否属于某个组。
     */
    public static final String SET = "SET";

    /**
     * Sorted Set (ZSet)：类似于Set，但每个成员关联一个分数，使得集合中的元素可以根据分数排序。
     * 适用于排行榜或其他需要按某种属性排序的场景。
     */
    public static final String SORTED_SET = "ZSET";

    /**
     * Hash：哈希表结构，由字段和关联值组成。非常适合存储对象，其中字段代表对象的属性名，而值则是属性值。
     */
    public static final String HASH = "HASH";

    /**
     * Bitmaps：用于高效处理大量布尔值数据的工具，不是严格意义上的独立数据结构。
     */
    public static final String BITMAP = "BITMAP";

    /**
     * HyperLogLogs：用于近似计算集合基数的特殊工具，适合高效地计算不精确的基数估算。
     */
    public static final String HYPERLOGLOG = "HYPERLOGLOG";

    /**
     * Geospatial Indexes (地理位置索引)：允许存储地理坐标，并执行半径查询或距离计算。
     * 特别适合位置相关的应用。
     */
    public static final String GEO = "GEO";
}
