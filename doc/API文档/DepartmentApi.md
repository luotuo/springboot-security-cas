# 部门管理接口文档

## 接口列表

### 部门树型结构接口
- URL
    - /department/getAll
- 功能
    - 返回排版后部门结构
- 参数
    - 无
- 返回值

```json
{
    "success": 1,
    "message": "处理成功",
    "result": [
        {
            "id": 10,
            "pid": 0,
            "name": "项目管理",
            "level": 1,
            "pName":""
        },
        {
            "id": 11,
            "pid": 10,
            "name": "新建项目",
            "level": 2,
            "pName": "项目管理"
        },
        {
            "id": 12,
            "pid": 10,
            "name": "bbb",
            "level": 2,
            "pName": "项目管理"
        },
        {
            "id": 13,
            "pid": 0,
            "name": "aaa",
            "level": 1,
            "pName":""
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
    url: "http://127.0.0.1:8085/3audit/department/getAll",
    xhrFields: {
        withCredentials: true
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

### 部门列表结构接口
- URL
    - /department/getAllList
- 功能
    - 返回列表结构的部门列表
- 参数
    - 无
- 返回值

```json
{
    "success": 1,
    "message": "处理成功",
    "result": [
        {
            "id": 10,
            "pid": 0,
            "name": "项目管理",
            "level": 1
        },
        {
            "id": 11,
            "pid": 10,
            "name": "新建项目",
            "level": 2
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
    url: "http://127.0.0.1:8085/3audit/department/getAllList",
    xhrFields: {
        withCredentials: true
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

### 添加部门接口
- URL
    - /department/add
- 功能
    - 添加部门
- 参数
    - pid
    - name
    - level
- 返回值

```json
{
    "success": 1,
    "message": "添加成功！",
    "result": [
        {
            "id": 13,
            "pid": 10,
            "name": "aaa",
            "level": 2
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
    url: "http://127.0.0.1:8085/3audit/department/add",
    xhrFields: {
        withCredentials: true
    },
    data: {
        pid:10,
        level:2,
        name:'aaa'
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

### 编辑部门接口
- URL
    - /department/edit
- 功能
    - 编辑部门
- 参数
    - id
- 返回值

```json
{
    "success": 1,
    "message": "添加成功！",
    "result": [
        {
            "id": 13,
            "pid": 10,
            "name": "aaa",
            "level": 2
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
    url: "http://127.0.0.1:8085/3audit/department/add",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 13
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

### 删除部门接口
- URL
    - /department/delete
- 功能
    - 编辑部门
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
    url: "http://127.0.0.1:8085/3audit/department/delete",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 13
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


### 更新部门接口
- URL
    - /department/update
- 功能
    - 更新部门
- 参数
    - id
    - pid
    - name
    - level
- 返回值

```json
{
    "success": 1,
    "message": "更新成功！",
    "result": {
        "id": 12,
        "pid": 10,
        "name": "bbb",
        "level": 2
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/department/update",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 12,
        pid:10,
        level:2,
        name:'bbb'
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

### 通过pid获取部门接口
- URL
    - /department/getByPid
- 功能
    - 通过pid获取部门
- 参数
    - pid
- 返回值

```json
{
    "success": 1,
    "message": "获取成功",
    "result": [
        {
            "id": 11,
            "pid": 10,
            "name": "新建项目",
            "level": 2
        },
        {
            "id": 12,
            "pid": 10,
            "name": "bbb",
            "level": 2
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
    url: "http://127.0.0.1:8085/3audit/department/getByPid",
    xhrFields: {
        withCredentials: true
    },
    data: {
        pid:10
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
            
