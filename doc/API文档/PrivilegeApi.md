# 权限管理接口文档

## 接口列表

### 权限列表接口
- URL
    - /privilege/getAll1
- 功能
    - 返回权限分页列表
- 参数
    - 无
- 返回值

```json
{
    "success": 1,
    "message": "处理成功",
    "result": {
        "content": [
            {
                "id": 1,
                "pid": 0,
                "level": 0,
                "level_str": "0级",
                "name": "aaa",
                "type": "菜单",
                "value": null,
                "url": null,
                "state": 1,
                "state_str": "正常"
            }
        ],
        "last": true,
        "totalPages": 1,
        "totalElements": 1,
        "first": true,
        "sort": [
            {
                "direction": "DESC",
                "property": "id",
                "ignoreCase": false,
                "nullHandling": "NATIVE",
                "descending": true,
                "ascending": false
            }
        ],
        "numberOfElements": 1,
        "size": 20,
        "number": 0
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/privilege/getAll1",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 12,
        data: name
    },
    crossDoname: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    },
})
```

### 权限列表树型结构接口
- URL
    - /privilege/getAll
- 功能
    - 权限列表排版后结果
- 参数
    - 无
- 返回值

```json
{
    "success": 1,
    "message": "处理成功",
    "result": [
        {
            "id": 11,
            "pid": 0,
            "level": 1,
            "level_str": "1级",
            "name": "项目管理",
            "type": "目录",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        },
        {
            "id": 4,
            "pid": 11,
            "level": 2,
            "level_str": "2级",
            "name": "项目管理",
            "type": "菜单",
            "value": "12312",
            "url": "/privilege/getPrivilegesByPid",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        },
        {
            "id": 7,
            "pid": 4,
            "level": 3,
            "level_str": "3级",
            "name": "按钮1",
            "type": "按钮",
            "value": "1233",
            "url": "/department/getAllList",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        },
        {
            "id": 8,
            "pid": 4,
            "level": 3,
            "level_str": "3级",
            "name": "按钮2",
            "type": "按钮",
            "value": "123",
            "url": "/module/count",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        },
        {
            "id": 12,
            "pid": 0,
            "level": 1,
            "level_str": "1级",
            "name": "稽查管理",
            "type": "目录",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        },
        {
            "id": 13,
            "pid": 0,
            "level": 1,
            "level_str": "1级",
            "name": "评审任务",
            "type": "目录",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        },
        {
            "id": 14,
            "pid": 0,
            "level": 1,
            "level_str": "1级",
            "name": "系统管理",
            "type": "目录",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        },
        {
            "id": 16,
            "pid": 14,
            "level": 2,
            "level_str": "2级",
            "name": "用户管理",
            "type": "菜单",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        },
        {
            "id": 17,
            "pid": 14,
            "level": 2,
            "level_str": "2级",
            "name": "角色管理",
            "type": "菜单",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        },
        {
            "id": 18,
            "pid": 14,
            "level": 2,
            "level_str": "2级",
            "name": "部门管理",
            "type": "菜单",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        },
        {
            "id": 15,
            "pid": 0,
            "level": 1,
            "level_str": "1级",
            "name": "知识库",
            "type": "目录",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统"
        }
    ]
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/privilege/getAll",
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    },
});
```

### 获取所有url列表
- URL
    - /privilege/getUrls
- 功能
    - 返回系统所有url
- 参数
    - 无
- 返回值

```json
{
    "success": 1,
    "message": "处理成功",
    "result": [
        "/category/create",
        "/category/list",
        "/category/delete",
        "/category/search",
        "/category/update",
        "/category/edit",
        "/center/create",
        "/center/list",
        "/center/delete",
        "/center/search",
        "/center/update",
        "/",
        "/center/edit",
        "/department/getAll",
        "/department/add",
        "/department/delete",
        "/department/getAllList",
        "/department/edit",
        "/login",
        "/module/count",
        "/module/create",
        "/module/list",
        "/module/delete",
        "/module/search",
        "/module/update",
        "/module/edit",
        "/privilege/getAll",
        "/privilege/getAll1",
        "/privilege/add",
        "/privilege/delete",
        "/privilege/getUrls",
        "/privilege/edit",
        "/problem/create",
        "/problem/list",
        "/problem/delete",
        "/problem/search",
        "/problem/update",
        "/problem/getCategory",
        "/problem/edit",
        "/project/list",
        "/reference/create",
        "/reference/list",
        "/reference/delete",
        "/reference/search",
        "/reference/update",
        "/reference/edit",
        "/role/getAll",
        "/role/add",
        "/role/update",
        "/role/deleteById",
        "/role/edit",
        "/role/deleteByCode",
        "/role/addPrivilege",
        "/role/editPrivilege",
        "/role/getAllNoPage",
        "/sponsor/create",
        "/sponsor/list",
        "/sponsor/delete",
        "/sponsor/search",
        "/sponsor/update",
        "/sponsor/edit",
        "/sponsor/getcode",
        "/static/center/abstract/get",
        "/static/center/abstract/create",
        "/static/center/abstract/update",
        "/static/center/abstract/delete",
        "/static/stage/abstract/get",
        "/static/stage/abstract/create",
        "/static/stage/abstract/update",
        "/static/stage/abstract/delete",
        "/static/company/info/get",
        "/static/company/info/create",
        "/static/company/info/update",
        "/static/company/info/delete",
        "/listTasksByProjectId",
        "/listTasks",
        "/listTaskDetail",
        "/saveTask",
        "/generateOriginalReport",
        "/generateCenterReport",
        "/test/test",
        "/test/testone/id",
        "/test/testtwo",
        "/user/getAll",
        "/user/add",
        "/user/update",
        "/user/deleteById",
        "/user/edit",
        "/user/deleteByCode",
        "/user/getUserIcon",
        "/user/changeState",
        "/v2/api-docs",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-resources",
        "/project/edit",
        "/project/create",
        "/project/getState",
        "/project/search",
        "/project/setState",
        "/project/update",
        "/role/setPrivilege",
        "/role/getPrivilege",
        "/user/getRole",
        "/user/setPrivilege",
        "/user/getPrivilege",
        "/user/setRoles",
        "/user/search",
        "/department/update",
        "/privilege/update",
        "/user/setPrivileges",
        "/user/getPrivileges",
        "/user/getRoles"
    ]
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/privilege/getUrls",
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    },
});
```

### 新增权限
- URL
    - /privilege/add
- 功能
    - 新增权限
- 参数
    - pid
    - name
    - type // 目录：level=1，菜单：level=2，按钮：level=3
    - value
    - state
    - platform
    
- 返回值

```json
{
    "success": 1,
    "message": "添加成功",
    "result": {
        "id": 7,
        "pid": 1,
        "level": 3,
        "level_str": "3级",
        "name": "ccc",
        "type": "按钮",
        "value": "",
        "url": "/privilege/add",
        "state": 1,
        "state_str": "正常",
        "platform": "稽查系统"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/privilege/add",
    xhrFields: {
        withCredentials: true
    },
    data: {
        pid: 1,
        name: "ccc",
        type: "按钮",
        value: "",
        url: "/privilege/add",
        state: 1,
        platform:"稽查系统"
    },
    crossDomain: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    },
});
```

### 编辑权限
- URL
    - /privilege/edit
- 功能
    - 编辑权限
- 参数
    - id
- 返回值

```json
{
    "success": 1,
    "message": "选择成功",
    "result": {
        "id": 1,
        "pid": 1,
        "level": 1,
        "level_str": "2级",
        "name": "ccc",
        "type": "按钮",
        "value": "",
        "url": "/privilege/add",
        "state": 1,
        "state_str": "正常",
        "platform": "稽查系统"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/privilege/edit",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 3
    },
    crossDomain: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    },
});
```

### 通过id删除权限
- URL
    - /privilege/delete
- 功能
    - 通过id删除权限
- 参数
    - id
- 返回值

```json
{
    "success": 1,
    "message": "删除成功",
    "result": 1
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/privilege/delete",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 3
    },
    crossDomain: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    },
});
```

### 权限更新接口
- URL
    - /privilege/update
- 功能
    - 通过id更新权限
- 参数
    - id
    - pid
    - name
    - type
    - value
    - state
    - platform
    
- 返回值

```json
{
    "success": 1,
    "message": "更新成功！",
    "result": {
        "id": 1,
        "pid": 1,
        "level": 2,
        "level_str": "2级",
        "name": "ccc",
        "type": "菜单",
        "value": "",
        "url": "/privilege/add",
        "state": 0,
        "state_str": "停用",
        "platform": "稽查系统"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/privilege/update",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 1,
        pid: 1,
        name: "ccc",
        type: "菜单",
        value: "",
        url: "/privilege/add",
        state: 0,
        platform: "稽查系统"
    },
    crossDomain: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    },
});
```

### 获取所有平台和菜单接口
- URL
    - /privilege/getPlatformsAndMenus
- 功能
    - 获取所有平台和菜单接口
- 参数
    - 无
    
- 返回值

```json
{
    "success": 1,
    "message": "获取成功！",
    "result": [
        {
            "id": 4,
            "platform": "稽查系统",
            "menu": "bbb"
        },
        {
            "id": 5,
            "platform": "稽查系统",
            "menu": "ccc"
        }
    ]
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/privilege/getPlatformsAndMenus",
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    },
});
```

### 获取pid下某个用户的所有权限列表
- URL
    - /privilege/getPrivilegesByPidAndUserId
- 功能
    - 获取pid下某个用户的所有权限列表
- 参数
    - pid
    - userId
- 返回值

```json
{
    "success": 1,
    "message": "获取成功！",
    "result": [
        {
            "id": 1,
            "pid": 1,
            "level": 1,
            "level_str": "2级",
            "name": "ccc",
            "type": "按钮",
            "value": "",
            "url": "/privilege/add",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统",
            "checked": true
        },
        {
            "id": 5,
            "pid": 1,
            "level": 2,
            "level_str": "2级",
            "name": "ccc",
            "type": "菜单",
            "value": "",
            "url": "/privilege/add",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统",
            "checked": false
        },
        {
            "id": 6,
            "pid": 1,
            "level": 2,
            "level_str": "2级",
            "name": "ccc",
            "type": "按钮",
            "value": "",
            "url": "/privilege/add",
            "state": 0,
            "state_str": "停用",
            "platform": "稽查系统",
            "checked": false
        }
    ]
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/privilege/getPrivilegesByPidAndUserId",
    xhrFields: {
        withCredentials: true
    },
    data: {
        pid: 1,
        userId:2
    },
    crossDomain: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    },
});
```

### 获取pid下某个角色的所有权限列表
- URL
    - /privilege/getPrivilegesByPidAndRoleId
- 功能
    - 获取pid下某个角色的所有权限列表
- 参数
    - pid
    - roleId
- 返回值

```json
{
    "success": 1,
    "message": "获取成功！",
    "result": [
        {
            "id": 16,
            "pid": 14,
            "level": 2,
            "level_str": "2级",
            "name": "用户管理",
            "type": "菜单",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统",
            "checked": true
        },
        {
            "id": 17,
            "pid": 14,
            "level": 2,
            "level_str": "2级",
            "name": "角色管理",
            "type": "菜单",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统",
            "checked": false
        },
        {
            "id": 18,
            "pid": 14,
            "level": 2,
            "level_str": "2级",
            "name": "部门管理",
            "type": "菜单",
            "value": "",
            "url": "",
            "state": 1,
            "state_str": "正常",
            "platform": "稽查系统",
            "checked": false
        }
    ]
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/privilege/getPrivilegesByPidAndRoleId",
    xhrFields: {
        withCredentials: true
    },
    data: {
        pid: 1,
        roleId:2
    },
    crossDomain: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    },
});
```