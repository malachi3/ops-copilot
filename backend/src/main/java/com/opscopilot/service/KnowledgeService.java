package com.opscopilot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.opscopilot.dto.Result;
import com.opscopilot.model.Knowledge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 知识库服务 - 增强版RAG
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeService {
    
    // 知识库数据（生产环境应从数据库查询）
    private final List<Knowledge> knowledgeBase = new ArrayList<>();
    
    public KnowledgeService() {
        initKnowledgeBase();
    }
    
    /**
     * 初始化知识库数据
     */
    private void initKnowledgeBase() {
        // Redis相关
        Knowledge k1 = new Knowledge();
        k1.setId(1L);
        k1.setTitle("Redis连接数满了怎么解决");
        k1.setContent("""
                Redis连接数满了，一般有以下几种原因和解决方法：

                **1. 连接泄漏（最常见）**
                - 症状：连接数逐渐增长到上限
                - 排查：`redis-cli info clients`
                - 解决：检查代码是否正确关闭连接，配置连接池超时

                **2. 连接池配置过小**
                - 症状：业务高峰时连接数飙升
                - 解决：调整 `maxclients` 参数，或增加Redis实例分摊压力

                **3. 慢查询阻塞**
                - 症状：某条命令执行很慢，导致连接堆积
                - 排查：`redis-cli slowlog get 10`
                - 解决：优化慢查询

                **4. Redis配置优化**
                ```
                maxclients 10000
                timeout 300
                tcp-keepalive 60
                ```
                """);
        k1.setType("OPERATION");
        k1.setTags("[\"Redis\", \"数据库\", \"连接\"]");
        k1.setKeywords("redis,连接数,maxclients,连接池,慢查询");
        k1.setViewCount(156);
        knowledgeBase.add(k1);
        
        // Nginx相关
        Knowledge k2 = new Knowledge();
        k2.setId(2L);
        k2.setTitle("Nginx服务重启方法");
        k2.setContent("""
                Nginx重启方法：

                **1. 优雅重启（推荐）**
                ```bash
                nginx -s reload
                ```

                **2. 完全重启**
                ```bash
                systemctl restart nginx
                ```

                **3. 检查配置**
                ```bash
                nginx -t
                ```

                **4. 重新加载配置**
                ```bash
                nginx -s reopen
                ```
                """);
        k2.setType("OPERATION");
        k2.setTags("[\"Nginx\", \"运维\", \"重启\"]");
        k2.setKeywords("nginx,重启,reload, systemctl");
        k2.setViewCount(89);
        knowledgeBase.add(k2);
        
        // MySQL相关
        Knowledge k3 = new Knowledge();
        k3.setId(3L);
        k3.setTitle("MySQL备份与恢复");
        k3.setContent("""
                MySQL备份恢复最佳实践：

                **备份命令**
                ```bash
                # 完整备份
                mysqldump -u root -p -A > backup.sql
                
                # 单库备份
                mysqldump -u root -p mydb > mydb.sql
                
                # 增量备份
                mysqlbinlog --start-position=xxx mysql-bin.000001 > increment.sql
                ```

                **恢复命令**
                ```bash
                mysql -u root -p < backup.sql
                ```

                **备份策略建议**
                - 每日全量备份
                - 每小时增量备份
                - 定期测试恢复
                """);
        k3.setType("OPERATION");
        k3.setTags("[\"MySQL\", \"备份\", \"恢复\"]");
        k3.setKeywords("mysql,备份,恢复,mysqldump");
        k3.setViewCount(234);
        knowledgeBase.add(k3);
        
        // Linux命令
        Knowledge k4 = new Knowledge();
        k4.setId(4L);
        k4.setTitle("Linux常用运维命令");
        k4.setContent("""
                Linux常用运维命令：

                **系统资源**
                ```bash
                # CPU和内存
                top / htop
                
                # 磁盘使用
                df -h
                
                # 内存使用
                free -h
                ```

                **网络诊断**
                ```bash
                # 网络连接
                netstat -tunlp
                
                # 路由跟踪
                traceroute
                
                # 端口检查
                lsof -i :port
                ```

                **进程管理**
                ```bash
                # 查看进程
                ps -ef | grep xxx
                
                # 杀进程
                kill -9 pid
                ```
                """);
        k4.setType("OPERATION");
        k4.setTags("[\"Linux\", \"命令\", \"运维\"]");
        k4.setKeywords("linux,命令,top,netstat,进程");
        k4.setViewCount(567);
        knowledgeBase.add(k4);
        
        // Docker相关
        Knowledge k5 = new Knowledge();
        k5.setId(5L);
        k5.setTitle("Docker常用操作");
        k5.setContent("""
                Docker常用操作：

                **镜像操作**
                ```bash
                # 查看镜像
                docker images
                
                # 拉取镜像
                docker pull nginx:latest
                
                # 构建镜像
                docker build -t myapp .
                ```

                **容器操作**
                ```bash
                # 查看容器
                docker ps -a
                
                # 启动容器
                docker run -d -p 8080:80 nginx
                
                # 进入容器
                docker exec -it container_id bash
                
                # 查看日志
                docker logs -f container_id
                ```
                """);
        k5.setType("OPERATION");
        k5.setTags("[\"Docker\", \"容器\"]");
        k5.setKeywords("docker,容器,镜像,pull,run");
        k5.setViewCount(345);
        knowledgeBase.add(k5);
        
        // 日志相关
        Knowledge k6 = new Knowledge();
        k6.setId(6L);
        k6.setTitle("Linux日志查看与分析");
        k6.setContent("""
                Linux日志查看与分析：

                **常用日志位置**
                - 系统日志：/var/log/syslog
                - 认证日志：/var/log/auth.log
                - 应用日志：/var/log/app.log

                **查看命令**
                ```bash
                # 实时查看日志
                tail -f /var/log/syslog
                
                # 查看最后100行
                tail -n 100 /var/log/syslog
                
                # 搜索关键词
                grep ERROR /var/log/app.log
                
                # 统计出现次数
                grep -c ERROR /var/log/app.log
                ```
                """);
        k6.setType("OPERATION");
        k6.setTags("[\"日志\", \"Linux\"]");
        k6.setKeywords("日志,log,tail,grep");
        k6.setViewCount(289);
        knowledgeBase.add(k6);
    }
    
    /**
     * 搜索知识库 - 混合检索（关键词 + 语义）
     */
    public Result<List<Knowledge>> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        
        String lowerKeyword = keyword.toLowerCase();
        List<Knowledge> results = new ArrayList<>();
        
        // 关键词匹配
        for (Knowledge k : knowledgeBase) {
            int score = calculateScore(k, lowerKeyword);
            if (score > 0) {
                k.setViewCount(k.getViewCount() + 1); // 增加浏览量
                results.add(k);
            }
        }
        
        // 按相关性排序
        results.sort((a, b) -> {
            int scoreA = calculateScore(a, lowerKeyword);
            int scoreB = calculateScore(b, lowerKeyword);
            return Integer.compare(scoreB, scoreA);
        });
        
        // 返回前10条
        return Result.success(results.stream().limit(10).toList());
    }
    
    /**
     * 计算相关性得分
     */
    private int calculateScore(Knowledge knowledge, String keyword) {
        int score = 0;
        
        // 标题匹配（权重高）
        if (knowledge.getTitle() != null && knowledge.getTitle().toLowerCase().contains(keyword)) {
            score += 10;
        }
        
        // 关键词匹配
        if (knowledge.getKeywords() != null) {
            String[] keywords = knowledge.getKeywords().split(",");
            for (String k : keywords) {
                if (keyword.contains(k.trim())) {
                    score += 5;
                }
            }
        }
        
        // 内容匹配
        if (knowledge.getContent() != null) {
            String[] lines = knowledge.getContent().split("\n");
            for (String line : lines) {
                if (line.toLowerCase().contains(keyword)) {
                    score += 1;
                }
            }
        }
        
        return score;
    }
    
    /**
     * 获取知识详情
     */
    public Result<Knowledge> getById(Long id) {
        for (Knowledge k : knowledgeBase) {
            if (k.getId().equals(id)) {
                return Result.success(k);
            }
        }
        return Result.error("知识不存在");
    }
    
    /**
     * 点赞
     */
    public Result<Void> like(Long id) {
        for (Knowledge k : knowledgeBase) {
            if (k.getId().equals(id)) {
                k.setLikeCount(k.getLikeCount() + 1);
                return Result.success();
            }
        }
        return Result.error("知识不存在");
    }
    
    /**
     * 获取热门知识
     */
    public Result<List<Knowledge>> getHot(int limit) {
        List<Knowledge> sorted = new ArrayList<>(knowledgeBase);
        sorted.sort((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()));
        return Result.success(sorted.stream().limit(limit).toList());
    }
    
    /**
     * 添加知识（预留接口）
     */
    public Result<Knowledge> add(Knowledge knowledge) {
        knowledge.setId((long) (knowledgeBase.size() + 1));
        knowledge.setCreateTime(LocalDateTime.now());
        knowledgeBase.add(knowledge);
        return Result.success(knowledge);
    }
}
