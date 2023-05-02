# Redis 实战
### Redis 实现 session 共享
- 
### Redis 实现缓存
- **缓存穿透**：指客户端请求的数据在缓存中和数据库中都不存在，这样缓存永远不会生效，这些请求都会打到数据库，给数据库带来巨大压力。解决方案：
   1. 缓存空对象
      - 实现简单，维护简单；额外的内存消耗，可能造成短期的不一致（使用）
   2. 布隆过滤
      - 内存占用较少，没有多余key；实现复杂，存在误判可能
   3. 增强id的复杂度，避免被猜测id规律
   4. 做好数据的基础格式校验
   5. 加强用户权限校验
   6. 做好热点参数的限流
- **缓存雪崩：**指同一时段大量的缓存key同时失效或者Redis服务宕机，导致大量请求到达数据库，带来巨大压力。解决方案：
   1. 给不同的key的TTL添加随机值（随机数）
   2. 利用Redis**集群**提高服务的可用性（哨兵监控）
   3. 给缓存业务添加降级限流策略（微服务组件等）
   4. 给业务添加多级缓存（浏览器、nignx、redis、jvm、数据库）
- **缓存击穿：**也叫热点key问题，是一个被**高并发访问**并且**缓存重建业务较复杂**的key突然失效了，无数请求访问会瞬间给数据库带来巨大的冲击。解决方案：
   1. 互斥锁（避免了其他线程同时访问）（setnx）
      1. 没有额外的内存消耗，保证一致性，实现简单；
      2. 线程需等待，性能受影响，可能有思索风险；
   2. 逻辑过期
      1. 线程无需等待，性能较好；
      2. 不保证一致性，有额外内存消耗，实现复杂；

