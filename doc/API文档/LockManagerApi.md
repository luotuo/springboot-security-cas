# 接口文档

## Locker
### 主要接口
    //TODO 　LIST 列表
### 接口详情
#### 1.锁列表
- /lock/resources/list 返回资源列表
  - 参数
    无
  - http://127.0.0.1:8084/3audit/lock/resources/list
  - 返回值
#### 2.在线用户列表
 
- /lock/getAllOnlineUser 返回所有在线用户
  - 参数
    无
  - http://127.0.0.1:8084/3audit/lock/getAllOnlineUser
  - 返回值
#### 3.解锁用户（踢用户）
 
- /lock/unlockUser 根据用户id解锁用户资源
  - 参数
      -  userId 用户的id Long型
      -  sessionId 用户的sessionId
  - http://127.0.0.1:8084/3audit/lock/unlockUser?userId=12
  - 返回值
#### 4.解锁资源
 
- /lock/unlockResource 根据资源id解锁资源
  - 参数
      -  resourceId  资源唯一标识  String型
  - http://127.0.0.1:8084/3audit/lock/unlockResource?resourceId=12
  - 返回值    