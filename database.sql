/*
 Navicat Premium Data Transfer

 Source Server         : localhost（本地）
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3306
 Source Schema         : vertx-gateway

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 01/03/2025 19:23:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gateway_api
-- ----------------------------
DROP TABLE IF EXISTS `gateway_api`;
CREATE TABLE `gateway_api`  (
                                `id` bigint(0) NOT NULL COMMENT '主键',
                                `parent_id` bigint(0) NULL DEFAULT NULL COMMENT '所属分类ID',
                                `app_id` bigint(0) NULL DEFAULT NULL COMMENT '所属应用',
                                `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'API名称',
                                `path` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由',
                                `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'API描述',
                                `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'API配置',
                                `online` int(0) NULL DEFAULT 0 COMMENT '是否运行（1表示运行，0表示暂停）',
                                `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除（1表示删除，0表示未删除）',
                                `create_user` bigint(0) NULL DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                `update_user` bigint(0) NULL DEFAULT NULL COMMENT '更新人',
                                `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '网关API配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gateway_api
-- ----------------------------
INSERT INTO `gateway_api` VALUES (1892872003078684674, 1892517675155406849, 1892512022298984449, '测试接口1', '/vertx/gateway/test1', '这是一个测试接口1。', '{\"authType\":\"JWT\",\"authHeaderName\":\"VG-AUTH\",\"accessLimitType\":\"MINUTE\",\"ipTimes\":\"120\",\"routerTimes\":\"0\",\"routeContentType\":[\"*\"],\"routeParams\":[{\"position\":\"QUERY\",\"key\":\"id\",\"type\":\"STRING\",\"required\":true,\"defaultVal\":\"123\",\"remark\":\"主键\"},{\"position\":\"QUERY\",\"key\":\"name\",\"type\":\"String\",\"required\":true,\"defaultVal\":\"hanjiang\",\"remark\":\"名称\"}],\"proxyApiType\":\"HTTP\",\"proxyApiMethod\":\"GET\",\"proxyRedirectUrl\":null,\"proxyApiUrls\":[{\"url\":\"http://127.0.0.1:8088/vertx/test\",\"weight\":1}],\"loadBalancingStrategy\":\"POLLING\",\"proxyConstParams\":[],\"proxyTimeoutTime\":10000,\"proxyRetry\":true,\"proxyRetryTimes\":1,\"proxyRetryInterval\":3000,\"proxyJumpUrl\":null,\"routePath\":\"/vertx/gateway/test1\",\"routeMethod\":\"GET\"}', 1, 0, 1111111111111111111, '2025-02-21 09:40:28', -1, '2025-03-01 19:04:38');
INSERT INTO `gateway_api` VALUES (1893150323519320066, 1893588527796031490, 1892821632830517250, '测试接口2', '/vertx/gateway/test2', NULL, '{\"authType\":\"JWT\",\"authHeaderName\":\"VG-AUTH\",\"accessLimitType\":\"MINUTE\",\"ipTimes\":\"120\",\"routerTimes\":\"0\",\"routeContentType\":[\"application/json\"],\"routeParams\":[],\"proxyApiType\":\"HTTP\",\"proxyApiMethod\":\"GET\",\"proxyRedirectUrl\":null,\"proxyApiUrls\":[{\"url\":\"http://127.0.0.1:8088//vertx/test\",\"weight\":1}],\"loadBalancingStrategy\":\"POLLING\",\"proxyConstParams\":[],\"proxyTimeoutTime\":10000,\"proxyRetry\":true,\"proxyRetryTimes\":1,\"proxyRetryInterval\":3000,\"routePath\":\"/vertx/gateway/test2\",\"routeMethod\":\"GET\"}', 0, 0, 1111111111111111111, '2025-02-22 04:06:25', 1111111111111111111, '2025-02-28 00:49:22');
INSERT INTO `gateway_api` VALUES (1893150369111404546, 1892519884739919873, 1892512022298984449, '测试接口3', NULL, NULL, NULL, 0, 0, 1111111111111111111, '2025-02-22 04:06:36', NULL, NULL);
INSERT INTO `gateway_api` VALUES (1893150407183101954, 1892519990310551553, 1892512022298984449, '测试接口4', NULL, NULL, NULL, 0, 0, 1111111111111111111, '2025-02-22 04:06:45', NULL, NULL);
INSERT INTO `gateway_api` VALUES (1893150451919548417, 1892520069578702850, 1892512022298984449, '测四接口5', NULL, NULL, NULL, 0, 0, 1111111111111111111, '2025-02-22 04:06:55', NULL, NULL);

-- ----------------------------
-- Table structure for gateway_api_classify
-- ----------------------------
DROP TABLE IF EXISTS `gateway_api_classify`;
CREATE TABLE `gateway_api_classify`  (
                                         `id` bigint(0) NOT NULL COMMENT '主键',
                                         `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '应用ID',
                                         `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编号',
                                         `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
                                         `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
                                         `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
                                         `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除（0未删除，1删除）',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gateway_api_classify
-- ----------------------------
INSERT INTO `gateway_api_classify` VALUES (1892517675155406849, 1892512022298984449, 'classify_one', '服务一', NULL, NULL, 0);
INSERT INTO `gateway_api_classify` VALUES (1892519828397834242, 1892512022298984449, 'classify_two', '服务二', NULL, NULL, 0);
INSERT INTO `gateway_api_classify` VALUES (1892519884739919873, 1892512022298984449, 'classify_three', '服务三', NULL, NULL, 0);
INSERT INTO `gateway_api_classify` VALUES (1892519990310551553, 1892512022298984449, 'classify_four', '服务四', NULL, NULL, 0);
INSERT INTO `gateway_api_classify` VALUES (1892520069578702850, 1892512022298984449, 'classify_five', '服务五', NULL, NULL, 0);
INSERT INTO `gateway_api_classify` VALUES (1893588527796031490, 1892821632830517250, 'dmeo', '普通服务', NULL, NULL, 0);

-- ----------------------------
-- Table structure for gateway_api_log
-- ----------------------------
DROP TABLE IF EXISTS `gateway_api_log`;
CREATE TABLE `gateway_api_log`  (
                                    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `api_id` bigint(0) NULL DEFAULT NULL COMMENT 'API ID',
                                    `app_id` bigint(0) NULL DEFAULT NULL COMMENT '应用ID',
                                    `req_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
                                    `req_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求路径',
                                    `req_body` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求Body',
                                    `resp_body` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '响应Body',
                                    `proxy_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '代理路径',
                                    `proxy_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '代理方法',
                                    `proxy_const_params` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '代理常量参数',
                                    `header_params` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求头参数',
                                    `path_params` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路径参数',
                                    `query_params` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求参数',
                                    `start_time` bigint(0) NULL DEFAULT NULL COMMENT '开始时间',
                                    `end_time` bigint(0) NULL DEFAULT NULL COMMENT '结束时间',
                                    `req_ip` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求IP',
                                    `status` int(0) NULL DEFAULT NULL COMMENT '状态（0失败、1成功）',
                                    `error_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误内容',
                                    `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '网关API日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gateway_api_log
-- ----------------------------
INSERT INTO `gateway_api_log` VALUES (-1119240191, 1892872003078684674, 1892512022298984449, 'GET', '/vertx/gateway/test1', '', 'Future{unresolved}', 'http://127.0.0.1:8088/vertx/test', 'GET', '[]', '', '', '?id=1234567&name=hj', 1740706949820, 1740706949824, '127.0.0.1', 1, NULL, '2025-02-28 01:42:30');
INSERT INTO `gateway_api_log` VALUES (-1102462974, 1892872003078684674, 1892512022298984449, 'GET', '/vertx/gateway/test1', '', 'Future{unresolved}', 'http://127.0.0.1:8088/vertx/test', 'GET', '[]', '', '', '?id=1234567&name=hj', 1740706950849, 1740706950854, '127.0.0.1', 1, NULL, '2025-02-28 01:42:31');
INSERT INTO `gateway_api_log` VALUES (432652289, 1892872003078684674, 1892512022298984449, 'GET', '/vertx/gateway/test1', '', 'Future{unresolved}', 'http://127.0.0.1:8088/vertx/test', 'GET', '[]', '', '', '?id=1234567&name=hj', 1740706947119, 1740706947124, '127.0.0.1', 1, NULL, '2025-02-28 01:42:27');
INSERT INTO `gateway_api_log` VALUES (894025730, 1892872003078684674, 1892512022298984449, 'GET', '/vertx/gateway/test1', '', 'Future{unresolved}', 'http://127.0.0.1:8088/vertx/test', 'GET', '[]', '', '', '?id=1234567&name=hj', 1740706948251, 1740706948255, '127.0.0.1', 1, NULL, '2025-02-28 01:42:28');
INSERT INTO `gateway_api_log` VALUES (1422508033, 1892872003078684674, 1892512022298984449, 'GET', '/vertx/gateway/test1', '', 'Future{unresolved}', 'http://127.0.0.1:8088/vertx/test', 'GET', '[]', '', '', '?id=1234567&name=hj', 1740706929735, 1740706929935, '127.0.0.1', 1, NULL, '2025-02-28 01:42:10');

-- ----------------------------
-- Table structure for gateway_application
-- ----------------------------
DROP TABLE IF EXISTS `gateway_application`;
CREATE TABLE `gateway_application`  (
                                        `id` bigint(0) NOT NULL COMMENT '主键',
                                        `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
                                        `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
                                        `protocol` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务协议（HTTP、HTTPS）',
                                        `port` int(0) NULL DEFAULT NULL COMMENT '端口',
                                        `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置',
                                        `online` int(0) NULL DEFAULT 0 COMMENT '是否运行（1表示运行，0表示暂停）',
                                        `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除（1表示删除，0表示未删除）',
                                        `create_user` bigint(0) NULL DEFAULT NULL COMMENT '创建人',
                                        `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                        `update_user` bigint(0) NULL DEFAULT NULL COMMENT '更新人',
                                        `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '网关应用配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gateway_application
-- ----------------------------
INSERT INTO `gateway_application` VALUES (1892512022298984449, '测试应用2', '这是一个测试应用。', 'HTTP', 5000, '{\"protocol\":\"HTTP\",\"corsOpen\":true,\"allowedOrigin\":\"^https?://.*\",\"allowCredentials\":false,\"maxAgeSeconds\":0,\"allowedMethods\":[\"GET\",\"POST\"],\"contentLength\":0,\"sessionTimeOut\":1800000,\"sessionCookieName\":\"Vertx-Gateway.session\",\"decoderInitialBufferSize\":128,\"maxPoolSize\":5,\"maxInitialLineLength\":4096,\"maxHeaderSize\":8192,\"keepAlive\":true,\"port\":5000}', 1, 0, 1111111111111111111, '2025-02-20 09:50:02', -1, '2025-03-01 19:04:38');
INSERT INTO `gateway_application` VALUES (1892821632830517250, '测试应用3', '这是一个测试应用3。', 'HTTP', 5001, '{\"protocol\":\"HTTP\",\"corsOpen\":true,\"allowedOrigin\":\"^https?://.*\",\"allowCredentials\":false,\"maxAgeSeconds\":0,\"allowedMethods\":[\"GET\",\"POST\",\"PUT\",\"HEAD\",\"DELETE\"],\"contentLength\":0,\"sessionTimeOut\":1800000,\"sessionCookieName\":\"Vertx-Gateway.session\",\"decoderInitialBufferSize\":128,\"maxPoolSize\":5,\"maxInitialLineLength\":4096,\"maxHeaderSize\":8192,\"keepAlive\":true,\"port\":5001}', 0, 0, 1111111111111111111, '2025-02-21 06:20:19', 1111111111111111111, '2025-02-21 06:23:10');
INSERT INTO `gateway_application` VALUES (1892822589807124482, '测试应用4', '这是测试应用4。', NULL, NULL, NULL, 0, 0, 1111111111111111111, '2025-02-21 06:24:07', NULL, NULL);
INSERT INTO `gateway_application` VALUES (1892822645964660738, '测试应用5', '这是测试应用5。', NULL, NULL, NULL, 0, 0, 1111111111111111111, '2025-02-21 06:24:20', NULL, NULL);

-- ----------------------------
-- Table structure for gateway_client
-- ----------------------------
DROP TABLE IF EXISTS `gateway_client`;
CREATE TABLE `gateway_client`  (
                                   `id` bigint(0) NOT NULL COMMENT '主键',
                                   `client_code` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编号（具有唯一性）',
                                   `client_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
                                   `remark` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '被授权方',
                                   `token_secret` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Token秘钥',
                                   `token_ttl` bigint(0) NULL DEFAULT 1800000 COMMENT 'Token有效期（单位:ms）',
                                   `client_ttl` datetime(0) NULL DEFAULT NULL COMMENT '客户端到期时间',
                                   `online` int(0) NULL DEFAULT 1 COMMENT '状态（1表示正常，0表示禁用）',
                                   `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除（1表示删除，0表示未删除）',
                                   `create_user` bigint(0) NULL DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                   `update_user` bigint(0) NULL DEFAULT NULL COMMENT '更新人',
                                   `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '授权客户端管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gateway_client
-- ----------------------------
INSERT INTO `gateway_client` VALUES (1894218976557887490, '452DDA467642E5E5A9EAE529B71B84A6', '寒江客户端', '寒江集团', '131651AFE0B396D7590A37F09AB12F22', 1800000, '2027-02-27 16:00:00', 1, 0, 1111111111111111200, '2025-02-25 02:52:52', 1111111111111111111, '2025-02-26 09:34:24');
INSERT INTO `gateway_client` VALUES (1894227064652316673, '2549B91A9CE85D8FD00B20DF3A2DDF4E', '孤影客户端', '孤影集团', '0D2CF8DE027A4F946A8D0EC3AA0DDB3D', 1800000, '2028-02-28 16:00:00', 1, 0, 1111111111111111200, '2025-02-25 03:25:00', 1111111111111111111, '2025-02-26 09:34:46');

-- ----------------------------
-- Table structure for gateway_client_auth
-- ----------------------------
DROP TABLE IF EXISTS `gateway_client_auth`;
CREATE TABLE `gateway_client_auth`  (
                                        `client_id` bigint(0) NULL DEFAULT NULL COMMENT '客户端ID',
                                        `auth_id` bigint(0) NULL DEFAULT NULL COMMENT '授权ID（应用ID、接口ID）',
                                        `type` int(0) NULL DEFAULT NULL COMMENT '类型: 0-应用，1-接口'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客户端网关授权' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gateway_client_auth
-- ----------------------------
INSERT INTO `gateway_client_auth` VALUES (1894218976557887490, 1892872003078684674, 1);
INSERT INTO `gateway_client_auth` VALUES (1894218976557887490, 1893150323519320066, 1);
INSERT INTO `gateway_client_auth` VALUES (1894227064652316673, 1892872003078684674, 1);

-- ----------------------------
-- Table structure for gateway_white_list
-- ----------------------------
DROP TABLE IF EXISTS `gateway_white_list`;
CREATE TABLE `gateway_white_list`  (
                                       `id` bigint(0) NOT NULL COMMENT '主键',
                                       `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '应用ID',
                                       `ip` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP',
                                       `remark` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
                                       `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除（0未删除，1删除）',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '网关白名单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gateway_white_list
-- ----------------------------
INSERT INTO `gateway_white_list` VALUES (1893917207294451713, 1892822645964660738, '10.10.10.10', '白名单IP', 0);
INSERT INTO `gateway_white_list` VALUES (1893917330992865282, 1892822645964660738, '11.11.11.11', '白名单IP', 0);
INSERT INTO `gateway_white_list` VALUES (1894956034536628225, 1892512022298984449, '127.0.0.1', '测试', 0);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
                             `id` bigint(0) NOT NULL COMMENT 'ID',
                             `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父ID',
                             `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单名称',
                             `icon` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
                             `alias` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单别名',
                             `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问地址',
                             `params` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求参数',
                             `code` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单编号',
                             `valid` int(0) NULL DEFAULT 1 COMMENT '是否有效（1有效，0无效）',
                             `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
                             `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除（1删除，0未删除）',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1873718566239940609, 0, '系统管理', 'Tools', '系统管理', '/system', '[]', 'system', 1, 3, 0);
INSERT INTO `sys_menu` VALUES (1873718825632477186, 1873718566239940609, '组织机构', 'MapLocation', '组织机构', '/system/org', '[]', 'system_org', 1, 1, 0);
INSERT INTO `sys_menu` VALUES (1873718995677949953, 1873718566239940609, '用户管理', 'User', '用户管理', '/system/user', '[]', 'system_user', 1, 2, 0);
INSERT INTO `sys_menu` VALUES (1873719239731916802, 1873718566239940609, '系统菜单', 'Menu', '系统菜单', '/system/menu', '[]', 'system_menu', 1, 9, 0);
INSERT INTO `sys_menu` VALUES (1880186212221272065, 1873718566239940609, '系统参数', 'Discount', '系统参数', '/system/params', '[]', 'system_params', 1, 5, 0);
INSERT INTO `sys_menu` VALUES (1884974021100544001, 1884975346991341570, '按钮管理', 'Pointer', '按钮管理', '/system/btnmanager', '[]', 'system_btnmanager', 1, 5, 0);
INSERT INTO `sys_menu` VALUES (1884974216035016705, 1884975346991341570, '接口管理', 'Connection', '接口管理', '/system/apimanager', '[]', 'system_apimanager', 1, 6, 0);
INSERT INTO `sys_menu` VALUES (1884974935785971713, 1884975346991341570, '角色管理', 'User', '角色管理', '/system/rolemanager', '[]', 'system_rolemanager', 1, 7, 0);
INSERT INTO `sys_menu` VALUES (1884975346991341570, 1873718566239940609, '系统权限', 'Lock', '系统权限', '/authmanager', '[]', 'authmanager', 1, 3, 0);
INSERT INTO `sys_menu` VALUES (1887852869190205441, 0, '首页', 'HomeFilled', '首页', '/home', '[]', 'home', 1, -1, 0);
INSERT INTO `sys_menu` VALUES (1890050211435892738, 0, '网关管理', 'Grid', '定时任务', '/gateway', '[]', 'gateway', 1, 1, 0);
INSERT INTO `sys_menu` VALUES (1890050634607611905, 1890050211435892738, '服务管理', 'Discount', '服务管理', '/gateway/classify', '[]', 'gateway_classify', 1, 1, 0);
INSERT INTO `sys_menu` VALUES (1891775935888764929, 1890050211435892738, '应用配置', 'SetUp', '应用配置', '/gateway/application', '[]', 'gateway_application', 1, 0, 0);
INSERT INTO `sys_menu` VALUES (1891776325371834370, 1890050211435892738, '路由配置', 'Help', '路由配置', '/gateway/api', '[]', 'gateway_api', 1, 2, 0);
INSERT INTO `sys_menu` VALUES (1892826857926660097, 1890050211435892738, 'IP白名单', 'DocumentRemove', 'IP白名单', '/gateway/whiteList', '[]', 'gateway_whiteList', 1, 3, 0);
INSERT INTO `sys_menu` VALUES (1892827116622942209, 1890050211435892738, '授权中心', 'Star', '授权中心', '/gateway/client', '[]', 'gateway_client', 1, 4, 0);
INSERT INTO `sys_menu` VALUES (1892827464074891265, 1890050211435892738, '日志管理', 'Tickets', '日志管理', '/gateway/log', NULL, 'gateway_log', 1, 5, 0);

-- ----------------------------
-- Table structure for sys_menu_api
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_api`;
CREATE TABLE `sys_menu_api`  (
                                 `id` bigint(0) NOT NULL COMMENT '主键',
                                 `menu_id` bigint(0) NULL DEFAULT NULL COMMENT '菜单ID',
                                 `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口名称',
                                 `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口编号',
                                 `path` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口路径',
                                 `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口方法',
                                 `valid` int(0) NULL DEFAULT 1 COMMENT '是否有效（1有效，0无效）',
                                 `type` int(0) NULL DEFAULT NULL COMMENT '类型（1自动扫描，0手动添加）',
                                 `next_time_millis` bigint(0) NULL DEFAULT NULL COMMENT '上次操作的时间戳',
                                 `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除（1删除，0未删除）',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单接口表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu_api
-- ----------------------------
INSERT INTO `sys_menu_api` VALUES (1887368830901669889, 1884974216035016705, '新增', 'post::menu-api:add', '/menu-api/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368830901669890, 1884974216035016705, '编辑', 'post::menu-api:update', '/menu-api/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368830972973057, 1884974216035016705, '删除', 'post::menu-api:delete', '/menu-api/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368830972973058, 1884974216035016705, '分页', 'get::menu-api:page', '/menu-api/page', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368830972973059, 1884974216035016705, '设置有效', 'post::menu-api:valid', '/menu-api/valid', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368830972973060, 1884974216035016705, '根据ID唯一获取', 'get::menu-api:selectOne', '/menu-api/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831040081922, 1884974216035016705, '设置无效', 'post::menu-api:unValid', '/menu-api/unValid', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831040081923, 1884974021100544001, '新增', 'post::menu-button:add', '/menu-button/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831040081924, 1884974021100544001, '编辑', 'post::menu-button:update', '/menu-button/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831102996481, 1884974021100544001, '删除', 'post::menu-button:delete', '/menu-button/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831102996482, 1884974021100544001, '分页', 'get::menu-button:page', '/menu-button/page', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831102996483, 1884974021100544001, '设置有效', 'post::menu-button:valid', '/menu-button/valid', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831102996484, 1884974021100544001, '根据ID唯一获取', 'get::menu-button:selectOne', '/menu-button/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831165911041, 1884974021100544001, '设置无效', 'post::menu-button:unValid', '/menu-button/unValid', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831165911042, 1884974935785971713, '新增', 'post::role:add', '/role/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831165911043, 1884974935785971713, '编辑', 'post::role:update', '/role/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831233019905, 1884974935785971713, '删除', 'post::role:delete', '/role/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831233019906, 1884974935785971713, '树形列表', 'get::role:tree', '/role/tree', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831233019907, 1884974935785971713, '根据ID唯一获取', 'get::role:selectOne', '/role/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831698587649, 1873719239731916802, '新增', 'post::menu:add', '/menu/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831698587650, 1873719239731916802, '编辑', 'post::menu:update', '/menu/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831698587651, 1873719239731916802, '设置有效接口', 'post::menu:set-valid', '/menu/set-valid', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831698587652, 1873719239731916802, '删除', 'post::menu:delete', '/menu/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831765696513, 1873719239731916802, '列表', 'get::menu:list', '/menu/list', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831765696514, 1873719239731916802, '树形列表', 'get::menu:tree', '/menu/tree', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831765696515, 1873719239731916802, '根据ID唯一获取', 'get::menu:selectOne', '/menu/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831765696516, 1873718825632477186, '新增', 'post::org:add', '/org/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831832805377, 1873718825632477186, '编辑', 'post::org:update', '/org/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831849582594, 1873718825632477186, '删除', 'post::org:delete', '/org/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831862165505, 1873718825632477186, '树形列表', 'get::org:tree', '/org/tree', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368831862165506, 1873718825632477186, '根据ID唯一获取', 'get::org:selectOne', '/org/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368832159961089, 1880186212221272065, '新增', 'post::params:add', '/params/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368832159961090, 1880186212221272065, '编辑', 'post::params:update', '/params/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368832159961091, 1880186212221272065, '删除', 'post::params:delete', '/params/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368832159961092, 1880186212221272065, '根据key获取value', 'get::params:fetch-value-by-key', '/params/fetch-value-by-key', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368832222875650, 1880186212221272065, '根据ID唯一获取', 'get::params:selectOne', '/params/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368832222875651, 1880186212221272065, '分页', 'get::params:page', '/params/page', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833082707972, 1873718995677949953, '新增', 'post::user:add', '/user/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833082707973, 1873718995677949953, '更新', 'post::user:update', '/user/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833145622529, 1873718995677949953, '删除', 'post::user:delete', '/user/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833145622530, 1873718995677949953, '分页', 'get::user:page', '/user/page', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833145622531, 1873718995677949953, '解封账号', 'post::user:valid', '/user/valid', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833145622532, 1873718995677949953, '根据ID集合批量获取', 'post::user:selectByIds', '/user/selectByIds', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833145622533, 1873718995677949953, '登录', 'post::user:login', '/user/login', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833212731393, 1873718995677949953, '退出', 'get::user:logout', '/user/logout', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833212731394, 1873718995677949953, '刷新Token', 'get::user:refresh-token', '/user/refresh-token', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833212731395, 1873718995677949953, '重置密码', 'post::user:resetPwd', '/user/resetPwd', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887368833212731396, 1873718995677949953, '根据ID唯一获取', 'get::user:selectOne', '/user/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887386211451527169, 1884974021100544001, '菜单按钮树形列表', 'get::menu-button:menu-button-tree', '/menu-button/menu-button-tree', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887409368325550081, 1884974216035016705, '菜单接口树形列表', 'get::menu-api:menu-button-tree', '/menu-api/menu-button-tree', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887409368719814657, 1884974935785971713, '保存权限配置', 'post::role:save-auth-config', '/role/save-auth-config', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887409368719814658, 1884974935785971713, '根据RoleId获取权限配置', 'get::role:get-auth-config', '/role/get-auth-config', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887428167535693825, 1884974935785971713, '保存用户角色', 'post::role:save-user-role', '/role/save-user-role', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887428167535693826, 1884974935785971713, '根据UserId获取用户角色', 'get::role:get-user-role', '/role/get-user-role', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887519037974904833, 1884974935785971713, '根据用户按钮权限', 'get::role:get-button-permissions', '/role/get-button-permissions', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887686303433732098, 1873719239731916802, '用户树形列表权限', 'get::menu:permission-tree', '/menu/permission-tree', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1887773594084782082, 1884974216035016705, '接口采集/校验', 'get::endpoint:scan-and-save-api', '/endpoint/scan-and-save-api', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1888504503339683842, 1873718995677949953, '获取当前用户的用户信息', 'get::user:current-user-info', '/user/current-user-info', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1888513473714442241, 1873718995677949953, '重置登录密码', 'post::user:re-pwd', '/user/re-pwd', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1889344835568640001, 1887852869190205441, '解锁全部的定时任务锁', 'get::unlock:task-lock-all', '/unlock/task/lock/all', 'GET', 1, 1, 1739695205228, 0);
INSERT INTO `sys_menu_api` VALUES (1889344835568640002, 1887852869190205441, '解锁日志轮转定时任务锁', 'get::unlock:task-lock-rotate', '/unlock/task/lock/rotate', 'GET', 1, 1, 1739695205228, 0);
INSERT INTO `sys_menu_api` VALUES (1889344835568640003, 1887852869190205441, '根据定时任务id解锁，定时任务锁', 'get::unlock:task-lock-by-id', '/unlock/task/lock/by-id', 'GET', 1, 1, 1739695205228, 0);
INSERT INTO `sys_menu_api` VALUES (1891495996602040322, 1890050634607611905, '新增', 'post::gateway:classify:add', '/gateway/classify/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891495996614623233, 1890050634607611905, '编辑', 'post::gateway:classify:update', '/gateway/classify/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891495996614623234, 1890050634607611905, '删除', 'post::gateway:classify:delete', '/gateway/classify/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891495996681732097, 1890050634607611905, '树形列表', 'get::gateway:classify:tree', '/gateway/classify/tree', 'GET', 1, 1, 1739802762233, 1);
INSERT INTO `sys_menu_api` VALUES (1891495996681732098, 1890050634607611905, '根据ID唯一获取', 'get::gateway:classify:selectOne', '/gateway/classify/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774711816193, 1890050634607611905, '树形列表', 'get::gateway:classify:page', '/gateway/classify/page', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774711816194, 1891776325371834370, '新增', 'post::gateway:api:add', '/gateway/api/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774774730753, 1891776325371834370, '编辑', 'post::gateway:api:update', '/gateway/api/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774774730754, 1891776325371834370, '删除', 'post::gateway:api:delete', '/gateway/api/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774774730755, 1891776325371834370, '根据ID唯一获取', 'get::gateway:api:selectOne', '/gateway/api/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774774730756, 1891776325371834370, '上线', 'get::gateway:api:online', '/gateway/api/online', 'GET', 1, 1, 1740129676990, 1);
INSERT INTO `sys_menu_api` VALUES (1891776774841839618, 1891776325371834370, '下线', 'get::gateway:api:offline', '/gateway/api/offline', 'GET', 1, 1, 1740129676990, 1);
INSERT INTO `sys_menu_api` VALUES (1891776774841839619, 1891776325371834370, '分页', 'get::gateway:api:page', '/gateway/api/page', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774841839620, 1891775935888764929, '新增', 'post::gateway:application:add', '/gateway/application/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774841839621, 1891775935888764929, '编辑', 'post::gateway:application:update', '/gateway/application/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774904754178, 1891775935888764929, '删除', 'post::gateway:application:delete', '/gateway/application/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774904754179, 1891775935888764929, '列表', 'get::gateway:application:list', '/gateway/application/list', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774904754180, 1891775935888764929, '根据ID唯一获取', 'get::gateway:api:selectOne', '/gateway/application/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1891776774904754181, 1891775935888764929, '上线', 'get::gateway:application:online', '/gateway/application/online', 'GET', 1, 1, 1740101271662, 1);
INSERT INTO `sys_menu_api` VALUES (1891776774971863042, 1891775935888764929, '下线', 'get::gateway:application:offline', '/gateway/application/offline', 'GET', 1, 1, 1740101271662, 1);
INSERT INTO `sys_menu_api` VALUES (1891776774971863043, 1891775935888764929, '分页', 'get::gateway:application:page', '/gateway/application/page', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1892748036154155009, 1891776325371834370, '保存配置', 'post::gateway:api:save-config', '/gateway/api/save-config', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1892748036267401217, 1891775935888764929, '保存配置', 'get::gateway:application:get-config', '/gateway/application/get-config', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1892748036330315778, 1891775935888764929, '保存配置', 'post::gateway:application:save-config', '/gateway/application/save-config', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1892815977843351554, 1891775935888764929, '下线', 'post::gateway:application:offline', '/gateway/application/offline', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1892815977843351555, 1891775935888764929, '上线', 'post::gateway:application:online', '/gateway/application/online', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1892851898596823042, 1891776325371834370, '保存配置', 'get::gateway:api:save-config', '/gateway/api/get-config', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1892866332375490561, 1891776325371834370, '新增', 'post::gateway:api:classify-tree', '/gateway/api/classify-tree', 'POST', 1, 1, 1740129475672, 1);
INSERT INTO `sys_menu_api` VALUES (1892867176680517634, 1891776325371834370, '新增', 'get::gateway:api:classify-tree', '/gateway/api/classify-tree', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893564691507699713, 1891776325371834370, '下线', 'post::gateway:api:offline', '/gateway/api/offline', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893564691558031361, 1891776325371834370, '上线', 'post::gateway:api:online', '/gateway/api/online', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893916425790758913, 1892826857926660097, '新增', 'post::gateway:white:list:add', '/gateway/white/list/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893916425811730433, 1892826857926660097, '更新', 'post::gateway:white:list:update', '/gateway/white/list/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893916425811730434, 1892826857926660097, '删除', 'post::gateway:white:list:delete', '/gateway/white/list/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893916425811730435, 1892826857926660097, '列表', 'post::gateway:white:list:list', '/gateway/white/list/list', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893955762704183297, 1892827116622942209, '新增', 'post::gateway:client:add', '/gateway/client/add', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893955762741932034, 1892827116622942209, '编辑', 'post::gateway:client:update', '/gateway/client/update', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893955762741932035, 1892827116622942209, '删除', 'post::gateway:client:delete', '/gateway/client/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893955762741932036, 1892827116622942209, '获取树形结构', 'get::gateway:client:tree', '/gateway/client/tree', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893955762741932037, 1892827116622942209, '根据ID唯一获取', 'get::gateway:client:selectOne', '/gateway/client/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1893955762741932038, 1892827116622942209, '分页', 'get::gateway:client:page', '/gateway/client/page', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1894271185039486978, 1892827464074891265, '删除', 'get::gateway:api:log:delete', '/gateway/log/delete', 'POST', 1, 1, 1740464512379, 1);
INSERT INTO `sys_menu_api` VALUES (1894271185073041409, 1892827464074891265, '根据ID唯一获取', 'get::gateway:api:log:selectOne', '/gateway/log/selectOne', 'GET', 1, 1, 1740464512379, 1);
INSERT INTO `sys_menu_api` VALUES (1894271185073041410, 1892827464074891265, '分页', 'get::gateway:api:log:page', '/gateway/log/page', 'GET', 1, 1, 1740464512379, 1);
INSERT INTO `sys_menu_api` VALUES (1894271855377346561, 1892827464074891265, '删除', 'post::gateway:log:delete', '/gateway/log/delete', 'POST', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1894271855377346562, 1892827464074891265, '根据ID唯一获取', 'get::gateway:log:selectOne', '/gateway/log/selectOne', 'GET', 1, 1, 1740464578416, 0);
INSERT INTO `sys_menu_api` VALUES (1894271855415095297, 1892827464074891265, '分页', 'get::gateway:log:page', '/gateway/log/page', 'GET', 1, 1, 1740464578416, 0);

-- ----------------------------
-- Table structure for sys_menu_button
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_button`;
CREATE TABLE `sys_menu_button`  (
                                    `id` bigint(0) NOT NULL COMMENT '主键',
                                    `menu_id` bigint(0) NULL DEFAULT NULL COMMENT '菜单ID',
                                    `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮名称',
                                    `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮编号',
                                    `valid` int(0) NULL DEFAULT 1 COMMENT '是否有效（1有效，0无效）',
                                    `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除（1删除，0未删除）',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单按钮表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu_button
-- ----------------------------
INSERT INTO `sys_menu_button` VALUES (1887060807440228354, 1873662796710297602, '查看', 'task_view', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887060880920240129, 1873662796710297602, '操作', 'task_opt', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887062629131337729, 1873663010963734530, '新增', 'question_add', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887062692809261057, 1873663010963734530, '编辑', 'question_edit', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887062738749472770, 1873663010963734530, '查看', 'question_view', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887062796966412290, 1873663010963734530, '删除', 'question_delete', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887062904395120641, 1873663010963734530, '批量导入', 'question_import', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887063111069450242, 1873663010963734530, '分类管理', 'question_classify', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887063179793121282, 1873663010963734530, '预览', 'question_preview', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887063459171516418, 1873665587134611458, '新增', 'paper_add', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887063504042180609, 1873665587134611458, '编辑', 'paper_edit', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887063541224685569, 1873665587134611458, '删除', 'paper_delete', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887063598833451009, 1873665587134611458, '预览', 'paper_preview', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887063662704312321, 1873665587134611458, '分类管理', 'paper_classify', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887063776751632386, 1873717926629552129, '新增', 'exam_add', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887063848511979522, 1873717926629552129, '编辑', 'exam_edit', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887063940761501698, 1873717926629552129, '删除', 'exam_delete', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887064016795844610, 1873717926629552129, '预览', 'exam_preview', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887064064778682369, 1873717926629552129, '查看', 'exam_view', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887064280571428865, 1873717926629552129, '发布/撤销', 'exam_publish', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887064412964634626, 1873717926629552129, '分类管理', 'exam_classify', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887064695383900162, 1873717926629552129, '进展情况', 'exam_progress', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887064794554023937, 1873718395699539970, '新增', 'train_add', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887064832663470081, 1873718395699539970, '编辑', 'train_edit', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887064863403524097, 1873718395699539970, '删除', 'train_delete', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887064911596077058, 1873718395699539970, '查看', 'train_view', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887064947889389569, 1873718395699539970, '预览', 'train_preview', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887065063341801473, 1873718395699539970, '发布/撤销', 'train_publish', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887065133713833986, 1873718395699539970, '分类管理', 'train_classify', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887065182485200897, 1873718395699539970, '进展情况', 'train_progress', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887065339096317954, 1873718825632477186, '新增', 'org_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887065378925428737, 1873718825632477186, '编辑', 'org_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887065426224594946, 1873718825632477186, '删除', 'org_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887065457656709122, 1873718825632477186, '查看', 'org_view', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887065597855514625, 1873718825632477186, '新增子项', 'org_add_child', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887065657364299778, 1873718995677949953, '新增', 'user_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887065708857769985, 1873718995677949953, '编辑', 'user_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887065737819439106, 1873718995677949953, '删除', 'user_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887065781016576002, 1873718995677949953, '查看', 'user_view', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887065892278878210, 1873718995677949953, '角色配置', 'user_role', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887065958897008642, 1873718995677949953, '重置密码', 'user_repassword', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066121455648770, 1873718995677949953, '解封账号', 'user_valid', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066272765165569, 1884974021100544001, '新增', 'btn_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066321058381825, 1884974021100544001, '编辑', 'btn_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066361856376834, 1884974021100544001, '删除', 'btn_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066407792394241, 1884974021100544001, '查看', 'btn_view', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066516273872897, 1884974021100544001, '设置有效', 'btn_valid', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066596259250178, 1884974021100544001, '设置无效', 'btn_unvalid', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066754980102146, 1884974216035016705, '新增', 'api_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066809317310465, 1884974216035016705, '编辑', 'api_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066852942266370, 1884974216035016705, '删除', 'api_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066888501575682, 1884974216035016705, '查看', 'api_view', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066942725537794, 1884974216035016705, '设置有效', 'api_valid', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887066997234712577, 1884974216035016705, '设置无效', 'api_unvalid', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887067172544036865, 1884974216035016705, '接口采集/校验', 'api_collection', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887067731896418305, 1884974935785971713, '新增', 'role_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887067767917101058, 1884974935785971713, '编辑', 'role_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887067807607799810, 1884974935785971713, '删除', 'role_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887067868345516033, 1884974935785971713, '查看', 'role_view', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887067953414389761, 1884974935785971713, '新增子项', 'role_add_child', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068013644595201, 1884974935785971713, '用户设置', 'role_user', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068081550376962, 1884974935785971713, '权限配置', 'role_auth', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068261720899586, 1879755468701204481, '新增', 'storage_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068312841076737, 1879755468701204481, '编辑', 'storage_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068342335422466, 1879755468701204481, '删除', 'storage_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068379861860354, 1879755468701204481, '查看', 'storage_view', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068468068073473, 1879755468701204481, '协议配置', 'storage_config', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068574238490625, 1879755772490448898, '删除', 'attach_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068642651783170, 1879755772490448898, '下载', 'attach_download', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068727225729026, 1880186212221272065, '新增', 'params_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068761853902849, 1880186212221272065, '编辑', 'params_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068791499243522, 1880186212221272065, '删除', 'params_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068838941016065, 1880186212221272065, '查看', 'params_view', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887068927986089986, 1880186212221272065, '新增', 'menu_add', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887068996546183169, 1873719239731916802, '新增', 'menu_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887069027089104898, 1873719239731916802, '编辑', 'menu_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887069058814820354, 1873719239731916802, '删除', 'menu_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887069129673392130, 1873719239731916802, '查看', 'menu_view', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887069231636922369, 1873719239731916802, '新增子项', 'menu_add_child', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887069393021157377, 1873719239731916802, '设置有效', 'menu_valid', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887069437107486722, 1873719239731916802, '设置无效', 'menu_unvalid', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887799715539025922, 1887852869190205441, '回到首页', 'home', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887853120185745409, 1887852869190205441, '搜索', 'search', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887853247537397761, 1887852869190205441, '全屏', 'fullscreen', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887853394933628930, 1887852869190205441, '个人信息', 'userInfo', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1887853450512351233, 1887852869190205441, '退出', 'logout', 1, 1);
INSERT INTO `sys_menu_button` VALUES (1887854002772164609, 1887852869190205441, '更多', 'more', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1888505303390588929, 1887852869190205441, '提交用户信息', 'submit_user_info', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1888505410617970689, 1887852869190205441, '提交重置密码', 'submit_repassword', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890058395890339842, 1890050634607611905, '新增', 'gateway_classify_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890058522906447874, 1890050634607611905, '编辑', 'gateway_classify_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890058711473967105, 1890050634607611905, '删除', 'gateway_classify_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890058771972608002, 1890050634607611905, '查看', 'gateway_classify_view', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890058838888534017, 1890050634607611905, '新增子项', 'gateway_classify_add_child', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890247984257028098, 1890050839264481281, '新增', 'task_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890248018310582273, 1890050839264481281, '删除', 'task_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890248069875355649, 1890050839264481281, '设置启用', 'task_enable', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890248184627318785, 1890050839264481281, '设置停用', 'task_un_enable', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890248241820848130, 1890050839264481281, '查看', 'task_view', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890248280689463298, 1890050839264481281, '编辑', 'task_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890248329901232129, 1890050839264481281, '日志', 'task_log', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890654657647824897, 1890050839264481281, '执行一个', 'task_exec_onece', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890654745023565825, 1890050839264481281, '执行日志', 'task_exec_log', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890654913672335361, 1890050839264481281, '启动任务', 'task_start', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890654970018615298, 1890050839264481281, '停止任务', 'task_stop', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1890655061672546306, 1890050839264481281, 'GlUE编辑器', 'task_glue', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892506792211525634, 1891775935888764929, '新增', 'gateway_app_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892506907840098306, 1891775935888764929, '编辑', 'gateway_app_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892506952660430849, 1891775935888764929, '删除', 'gateway_app_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892506988278460418, 1891775935888764929, '配置', 'gateway_app_config', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892510181364432898, 1891775935888764929, '部署', 'gateway_app_online', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892510223127117825, 1891775935888764929, '撤销', 'gateway_app_offline', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892866604413853698, 1891776325371834370, '新增', 'gateway_api_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892866656343531521, 1891776325371834370, '编辑', 'gateway_api_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892866702728339457, 1891776325371834370, '删除', 'gateway_api_delete', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892866767651971073, 1891776325371834370, '配置', 'gateway_api_config', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892866825243959298, 1891776325371834370, '上线', 'gateway_api_online', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1892866895842484226, 1891776325371834370, '下线', 'gateway_api_offline', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1893898757817769986, 1892826857926660097, '新增', 'gateway_white_add', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1893898789337964545, 1892826857926660097, '编辑', 'gateway_white_edit', 1, 0);
INSERT INTO `sys_menu_button` VALUES (1893898890416496641, 1892826857926660097, '删除', 'gateway_white_delete', 1, 0);

-- ----------------------------
-- Table structure for sys_organization
-- ----------------------------
DROP TABLE IF EXISTS `sys_organization`;
CREATE TABLE `sys_organization`  (
                                     `id` bigint(0) NOT NULL COMMENT 'ID',
                                     `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父ID',
                                     `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织名称',
                                     `fullname` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织全称',
                                     `code` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织编号',
                                     `type` int(0) NULL DEFAULT 1 COMMENT '组织类型（1单位，0部门）',
                                     `valid` int(0) NULL DEFAULT 1 COMMENT '是否有效（1有效，0无效）',
                                     `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
                                     `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除（1删除，0未删除）',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '组织表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_organization
-- ----------------------------
INSERT INTO `sys_organization` VALUES (1850706813621731329, NULL, '寒江科技有限公司', '寒江科技有限公司', 'HJ0001', 1, 1, 1, 0);
INSERT INTO `sys_organization` VALUES (1850706813621731330, 1850706813621731332, '人事部', '人事部', 'HJ001_B001', 0, 1, 3, 0);
INSERT INTO `sys_organization` VALUES (1850706813621731331, 1850706813621731332, '财务部', '财务部', 'HJ001_B002', 0, 0, 4, 0);
INSERT INTO `sys_organization` VALUES (1850706813621731332, 1850706813621731329, '转转子公司', '转转子公司', 'HJ0001C_001', 1, 1, 1, 0);
INSERT INTO `sys_organization` VALUES (1850706813621731333, 1850706813621731329, '合计子公司', '合计子公司', 'HJ001_C002', 1, 1, 2, 0);

-- ----------------------------
-- Table structure for sys_params
-- ----------------------------
DROP TABLE IF EXISTS `sys_params`;
CREATE TABLE `sys_params`  (
                               `id` bigint(0) NOT NULL COMMENT '主键',
                               `param_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数名称',
                               `param_key` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数Key',
                               `param_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数Value',
                               `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否已删除',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_params
-- ----------------------------
INSERT INTO `sys_params` VALUES (1880450866017300482, 'TokenTTL（毫秒）', 'TOKEN_LIVE_TIME', '10800000', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` bigint(0) NOT NULL COMMENT '主键',
                             `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父ID',
                             `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
                             `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色编号',
                             `valid` int(0) NULL DEFAULT 1 COMMENT '是否有效（1有效，0无效）',
                             `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
                             `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除（1删除，0未删除）',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1111111111111111111, 0, '超级管理员', 'administrator', 1, 0, 0);
INSERT INTO `sys_role` VALUES (1887070057067560961, 0, '管理员', 'admin', 1, 1, 1);
INSERT INTO `sys_role` VALUES (1887430996501467137, 0, '基础角色', 'base_role', 1, 99, 1);

-- ----------------------------
-- Table structure for sys_role_auth
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_auth`;
CREATE TABLE `sys_role_auth`  (
                                  `role_id` bigint(0) NOT NULL COMMENT '主键',
                                  `auth_id` bigint(0) NOT NULL COMMENT '父ID',
                                  `auth_type` int(0) NOT NULL COMMENT '权限类型（1菜单，2按钮，3接口）'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色_权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_auth
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` bigint(0) NOT NULL COMMENT '主键',
                             `code` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户编号',
                             `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户姓名',
                             `nickname` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
                             `phone` bigint(0) NULL DEFAULT NULL COMMENT '手机号码',
                             `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱号码',
                             `org_id` bigint(0) NULL DEFAULT NULL COMMENT '所属组织',
                             `account` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录账号',
                             `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录密码',
                             `valid` int(0) NULL DEFAULT 1 COMMENT '是否有效',
                             `sort` int(0) NULL DEFAULT NULL COMMENT '用户排序',
                             `del_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1111111111111111111, 'administrator', '超级管理员', '超级管理员', 17865454234, 'administrator@hj.com', 1850706813621731329, 'administrator', 'c0dcc99913baee40588da3bf134a2205', 1, 1, 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
                                  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
                                  `role_id` bigint(0) NOT NULL COMMENT '角色ID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户_角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1111111111111111111, 1111111111111111111);
INSERT INTO `sys_user_role` VALUES (1873746662934450177, 1887430996501467137);
INSERT INTO `sys_user_role` VALUES (1873746662934450177, 1887070057067560961);
INSERT INTO `sys_user_role` VALUES (1873749058125398017, 1887430996501467137);

SET FOREIGN_KEY_CHECKS = 1;
