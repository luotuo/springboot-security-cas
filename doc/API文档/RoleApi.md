# 角色管理接口文档

## 接口列表

### 角色分页列表接口
- URL
    - /role/getAll
- 功能
    - 返回角色分页列表
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
                "id": 4,
                "code": "004",
                "name": "项目经理"
            },
            {
                "id": 3,
                "code": "003",
                "name": "稽查经理"
            },
            {
                "id": 2,
                "code": "002",
                "name": "项目管理员"
            },
            {
                "id": 1,
                "code": "001",
                "name": "超级管理员"
            }
        ],
        "last": true,
        "totalPages": 1,
        "totalElements": 4,
        "first": true,
        "size": 20,
        "number": 0,
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
        "numberOfElements": 4
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/role/getAll",
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

### 全部角色列表接口，不分页
- URL
    - /role/getAllNoPage
- 功能
    - 返回无分页的角色列表
- 参数
    - 无
- 返回值

```json
{
    "success": 1,
    "message": "处理成功",
    "result": [
        {
            "id": 1,
            "code": "001",
            "name": "超级管理员"
        },
        {
            "id": 2,
            "code": "002",
            "name": "项目管理员"
        },
        {
            "id": 3,
            "code": "003",
            "name": "稽查经理"
        },
        {
            "id": 4,
            "code": "004",
            "name": "项目经理"
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
    url: "http://127.0.0.1:8085/3audit/role/getAllNoPage",
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


### 添加角色
- URL
    - /role/add
- 功能
    - 添加角色
- 参数
    - code
    - name
- 返回值

```json
{
    "success": 1,
    "message": "保存成功",
    "result": {
        "id": 5,
        "code": "005",
        "name": "稽查组长"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/role/add",
    xhrFields: {
        withCredentials: true
    },
    data: {
        code: "005",
        name: '稽查组长'
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

### 编辑角色
- URL
    - /role/edit
- 功能
    - 编辑角色
- 参数
    - id
- 返回值

```json
{
    "success": 1,
    "message": "选择成功",
    "result": {
        "id": 3,
        "code": "003",
        "name": "稽查经理"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/role/edit",
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

### 通过角色id删除角色
- URL
    - /role/deleteById
- 功能
    - 通过角色id删除角色
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
    url: "http://127.0.0.1:8085/3audit/role/deleteById",
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
    }
});
```

### 通过角色编码删除角色
- URL
    - /role/deleteByCode
- 功能
    - 通过角色编码删除角色
- 参数
    - code
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
    url: "http://127.0.0.1:8085/3audit/role/deleteByCode",
    xhrFields: {
        withCredentials: true
    },
    data: {
        code: 005
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

### 通过角色id更新角色
- URL
    - /role/update
- 功能
    - 通过角色id更新角色
- 参数
    - id
    - code
    - name
- 返回值

```json
{
    "success": 1,
    "message": "删除成功",
    "result": {
        "id": 4,
        "code": "005",
        "name": "稽查组长"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/role/update",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 4,
        code: "005",
        name: '稽查组长'
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


### 通过角色id编辑角色权限
- URL
    - /role/setPrivileges
- 功能
    - 通过角色id和pid编辑角色权限
- 参数
    - id
    - privileges
    - add 0表示删除，1表示新增
- 返回值

```json
{
    "success": 1,
    "message": "编辑成功",
    "result": {
        "id": 2,
        "code": "002",
        "name": "项目管理员"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/role/setPrivileges",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 4,
        privileges: "1,4",
        add: 0
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

### 通过角色id获取角色权限
- URL
    - /role/getPrivileges
- 功能
    - 通过角色id获取角色权限
- 参数
    - id
- 返回值

```json
{
    "success": 1,
    "message": "编辑成功",
    "result": [
        {
            "id": 1,
            "role_id": 4,
            "role_name": "稽查组长",
            "privilege_id": 1,
            "privilege_name": "ccc"
        },
        {
            "id": 2,
            "role_id": 4,
            "role_name": "稽查组长",
            "privilege_id": 4,
            "privilege_name": "bbb"
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
    url: "http://127.0.0.1:8085/3audit/role/getPrivileges",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 4
    },
    crossDomain: true,
    success: function(response) {
        alert("success：	" + JSON.stringify(response));
    },
    fail: function(request, status, error) {
        alert("error" + error);
    }
});
```