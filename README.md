# Redis 实战
### Redis 实现共享 session 登录
- 发送验证码，保存验证码到 redis（key，value）
   - 手机号用作 key，验证码为 value。存入 redis 中。（保存验证码，采用String数据类型）
   - 以随机 token 为 key 获取用户数据
- 短信验证码校验（提交手机号和验证码）
   - 以手机号为 key，读取验证码，校验验证码
   - 根据手机号查询用户，判断是否存在。存在保存到 redis 中，不存在则新建到数据库中
     - String 数据类型：以 JSON 字符串来保存，比较直观，但不好修改（保存对象时）
     - Hash 结构将**对象**中的每个字段独立存储，可以针对单个字段做 CRUD，并且内存占用更少
   - 以随机 token 为 key 存储**用户**数据(对象)（uuid 生成 token）【采用 Hash 数据类型】
   - 返回 token 给客户端(浏览器)
- 选择合适的数据结构
- 选择合适的 key
- 选择合适的存储粒度
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
- **缓存雪崩：** 指同一时段大量的缓存key同时失效或者Redis服务宕机，导致大量请求到达数据库，带来巨大压力。解决方案：
   1. 给不同的key的TTL添加随机值（随机数）
   2. 利用Redis**集群**提高服务的可用性（哨兵监控）
   3. 给缓存业务添加降级限流策略（微服务组件等）
   4. 给业务添加多级缓存（浏览器、nignx、redis、jvm、数据库）
- **缓存击穿：** 也叫热点key问题，是一个被**高并发访问**并且**缓存重建业务较复杂**的key突然失效了，无数请求访问会瞬间给数据库带来巨大的冲击。解决方案：
   1. 互斥锁（避免了其他线程同时访问）（setnx）
      1. 没有额外的内存消耗，保证一致性，实现简单；
      2. 线程需等待，性能受影响，可能有死锁风险；
   2. 逻辑过期
      1. 线程无需等待，性能较好；
      2. 不保证一致性，有额外内存消耗，实现复杂；
### Redis 实现点赞关注

##### 完善点赞信息

- 同一个用户只能点赞一次，再次点赞则取消点赞（**set** 集合）
- 如果当前用户已经点赞，则点赞按钮高亮显示（前端判断字段Blog类的isLike属性）

##### 点赞排行榜

- 类似微信朋友圈点赞，查询前几名点赞信息，需从set中查询，但set无法排序
- list 按添加顺序排序(按索引查找或首位查找)，set 无序(根据元素查找)、**zset**(sortedSet 根据 score 排序) 根据元素查找
- 注意：mysql 在用 in 查询的时候不会根据我们给的顺序排，默认从小到大，这就需要我们自定义 查询功能：order by field (id , 5, 1)[告诉它用什么顺序排]

~~~java
String key = BLOG_LIKED_KEY + id;
        // 1.查询top5的点赞用户 zrange key 0 4
        Set<String> top5 = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if (top5 == null || top5.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        // 2.解析出其中的用户id
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        // 3.根据用户id查询用户 WHERE id IN ( 5 , 1 ) ORDER BY FIELD(id, 5, 1)
        List<UserDTO> userDTOS = userService.query()
                .in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        // 4.返回
        return Result.ok(userDTOS);
~~~

##### 共同关注

- 使用Redis完成共同关注前，先实现关注取关功能。当前用户id：userId、要关注的用户id：followUserId

- 判断当前用户是否关注，在数据库中查询返回 Boolean，根据 Boolean + followUserId 实现关注取关

- **实现共同关注：**利用 Redis 恰当的数据结构。当前用户id 和 博主id关注的一个**交集**（set）

  - 改造关注接口：关注一个博主，不仅保存到数据中，也将其保存到redis中（key：当前用户id，value：当前用户关注的所有博主）利用 **set**数据结构的 **intersect** 求交集

  ~~~java
  // 1. 获取登录用户Id
          Long userId = UserHolder.getUser().getId();
          // 2. 在redis中求交集
          String key = "follows:" + userId;
          String key2 = "follows:" + id;
          Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, key2);
          if (intersect == null || intersect.isEmpty()) {
              // 无交集返回一个空集
              return Result.ok(Collections.emptyList());
          }
          // 3. 解析 id 集合
          List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());
          // 4. 根据 id 查询用户
          userService.listByIds(ids)
                  .stream()
                  .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                  .collect(Collectors.toList());
          return Result.ok();
  ~~~

##### 关注推送

- 关注推送也叫 Feed 流，直译为投喂。为用户持续的提供“沉浸式”的体验，通过无限下拉刷新获取新的信息。

- 不需要我们用户再去推送信息，而是系统分析用户到底想要什么，然后直接把内容推送给用户，从而使用户能够更加的节约时间，不用主动去寻找。Feed流的实现有两种模式：Feed流产品有两种常见模式：

- Timeline：不做内容筛选，简单的按照内容发布时间排序，常用于好友或关注。例如朋友圈

  * 优点：信息全面，不会有缺失。并且实现也相对简单
  * 缺点：信息噪音较多，用户不一定感兴趣，内容获取效率低

- 智能排序：利用智能算法屏蔽掉违规的、用户不感兴趣的内容。推送用户感兴趣信息来吸引用户

  * 优点：投喂用户感兴趣信息，用户粘度很高，容易沉迷
  * 缺点：如果算法不精准，可能起到反作用
  * 本例中的个人页面，是基于关注的好友来做Feed流，因此采用Timeline的模式。该模式的实现方案有三种：

- 我们本次针对好友的操作，采用的就是Timeline的方式，只需要拿到我们关注用户的信息，然后按照时间排序即可，因此采用Timeline的模式。该模式的实现方案有三种：

  * 拉模式
  * 推模式
  * 推拉结合

  **拉模式**：也叫做读扩散

  该模式的核心含义就是：当张三和李四和王五发了消息后，都会保存在自己的邮箱中，假设赵六要读取信息，那么他会从读取他自己的收件箱，此时系统会从他关注的人群中，把他关注人的信息全部都进行拉取，然后在进行排序

  优点：比较节约空间，因为赵六在读信息时，并没有重复读取，而且读取完之后可以把他的收件箱进行清楚。

  缺点：比较延迟，当用户读取数据时才去关注的人里边去读取数据，假设用户关注了大量的用户，那么此时就会拉取海量的内容，对服务器压力巨大。

  **推模式**：也叫做写扩散

  推模式是没有写邮箱的，当张三写了一个内容，此时会主动的把张三写的内容发送到他的粉丝收件箱中去，假设此时李四再来读取，就不用再去临时拉取了

  优点：时效快，不用临时拉取

  缺点：内存压力大，假设一个大V写信息，很多人关注他， 就会写很多分数据到粉丝那边去

  **推拉结合模式**：也叫做读写混合，兼具推和拉两种模式的优点

  推拉模式是一个折中的方案，站在发件人这一段，如果是个普通的人，那么我们采用写扩散的方式，直接把数据写入到他的粉丝中去，因为普通的人他的粉丝关注量比较小，所以这样做没有压力，如果是大V，那么他是直接将数据先写入到一份到发件箱里边去，然后再直接写一份到活跃粉丝收件箱里边去，现在站在收件人这端来看，如果是活跃粉丝，那么大V和普通的人发的都会直接写入到自己收件箱里边来，而如果是普通的粉丝，由于他们上线不是很频繁，所以等他们上线时，再从发件箱里边去拉信息。

##### 基于推模式实现关注推送功能

- 修改新增探店笔记的业务，在保存blog到数据库的同时，推送到粉丝的收件箱
- 收件箱满足可以根据时间戳排序，必须用Redis的数据结构实现（list、sortedSet）
- 查询收件箱数据时，可以实现分页查询（传统分页按角标：list、滚动分页按分数：sortedSet）
