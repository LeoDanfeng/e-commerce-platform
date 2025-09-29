# 多模块电商系统 (Multi-Module E-commerce System)

基于Spring Boot和Spring Cloud Alibaba构建的分布式电商系统，展示了现代Java企业级应用的最佳实践。

## 项目概述

这是一个完整的多模块电商系统，采用微服务架构设计，包含用户管理、商品管理、购物车、订单处理和支付等核心功能模块，采用领域驱动设计思想进行服务拆分。该项目演示了如何在实际企业环境中构建可扩展、高可用的分布式系统。

### 技术栈

- **后端框架**: Spring Boot 3.x, Spring Cloud Alibaba
- **数据库**: MySQL 8.x
- **ORM框架**: Spring Data JPA
- **服务发现**: Nacos
- **负载均衡**: Spring Cloud LoadBalancer
- **服务调用**: OpenFeign
- **API文档**: SpringDoc OpenAPI
- **测试框架**: JUnit 5, Mockito
- **构建工具**: Maven
- **其他**: Lombok, Validation

### 系统架构

项目采用前后端分离的微服务架构，各服务间通过RESTful API通信：

- **用户服务 (user-service)**: 用户注册、登录、信息管理
- **商品服务 (product-service)**: 商品信息管理、库存控制
- **购物车服务 (cart-service)**: 购物车管理
- **订单服务 (order-service)**: 订单创建、状态管理
- **支付服务 (payment-service)**: 支付流程处理

### 功能特性

1. **用户管理**
    - 用户注册与登录
    - 用户信息维护
    - JWT Token认证机制

2. **商品管理**
    - 商品增删改查
    - 分类查询
    - 库存管理

3. **购物车功能**
    - 添加/删除商品
    - 数量调整

4. **订单处理**
    - 订单创建
    - 订单状态跟踪
    - 分页查询

5. **支付系统**
    - 支付宝集成
    - 支付状态回调处理

### 数据库设计

每个服务都有独立的数据源，实现了数据隔离和服务解耦。

### 部署说明

支持容器化部署(Docker,Kubernetes)，配置了开发(dev)和生产(prod)环境。

**核心职责**:
- 使用Spring Boot构建独立的服务模块，实现RESTful API
- 基于Spring Cloud Alibaba实现服务注册与发现、远程调用
- 使用Spring Data JPA进行数据持久化操作
- 实现JWT Token安全认证机制
- 编写单元测试和集成测试，保证代码质量
- 配置多环境部署方案，支持Docker容器化部署

**项目亮点**:
- 采用微服务架构，实现了高内聚低耦合的设计原则
- 实现了完整的电商核心流程：浏览商品 → 加入购物车 → 创建订单 → 完成支付
- 使用Nacos进行服务治理，提高了系统的可维护性和可观测性
- 全面的测试覆盖，包括单元测试、集成测试和接口测试

