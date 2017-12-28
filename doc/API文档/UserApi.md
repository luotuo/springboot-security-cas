# 用户管理接口文档

## 接口列表
```
    private long id;
    private String name="测试账户";  姓名
    private String department;      部门
    private String phone;
    private String email;
    private int state;
    private String state_str;
    private String icons;
    private String password;
    private String wechat;
    private String last_ip;
    private int has_login = 0;
    private String resume;//用户简介
    private String title; //用户头衔
```

### 1 当前用户信息
- URL
    /user/currentUserInfo
- 功能
    - 返回当前用户信息
- 参数
    - 无
- 返回值
    

### 2 用户分页列表接口
- URL
    - /user/getAll
- 功能
    - 返回用户分页列表
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
                "id": 2,
                "code": "002",
                "name": "test",
                "department": "",
                "phone": "13211323322",
                "email": "test@test.com",
                "state": 0,
                "state_str": "停用",
                "icons": null,
                "password": "111111",
                "wechat": null
            },
            {
                "id": 1,
                "code": "001",
                "name": "admin",
                "department": null,
                "phone": null,
                "email": "admin@admin.com",
                "state": 1,
                "state_str": "正常",
                "icons": null,
                "password": "",
                "wechat": null
            }
        ],
        "last": true,
        "totalPages": 1,
        "totalElements": 2,
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
        "numberOfElements": 2
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/user/getAll",
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

### 3 添加用户接口
- URL
    - /user/add
- 功能
    - 添加用户
- 参数
    - name
    - department
    - phone
    - email
    - icon
    - password
    - wechat
    - roles
    
- 返回值

```json
{
    "success": 1,
    "message": "选择成功",
    "result": {
        "id": 11,
        "name": "abvd",
        "department": "",
        "phone": "13211232232",
        "email": "abc@abc.com",
        "state": 1,
        "state_str": "正常",
        "icons": "ad/adfa/a.jpg",
        "password": "111111",
        "wechat": "aaa",
        "roles": "项目管理员,稽查组长"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/user/add",
    xhrFields: {
        withCredentials: true
    },
    data: {
        name: "abvd",
        department: "",
        phone: "13211232232",
        email: "abc@abc.com",
        icon: "ad/adfa/a.jpg",
        password: "111111",
        wechat: "aaa",
        roles: "项目管理员,稽查组长"
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

### 4 编辑用户接口
- URL
    - /user/edit
- 功能
    - 编辑用户
- 参数
    - id
- 返回值

```json
{
    "success": 1,
    "message": "选择成功",
    "result": {
        "id": 11,
        "name": "abvd",
        "department": "",
        "phone": "13211232232",
        "email": "abc@abc.com",
        "state": 1,
        "state_str": "正常",
        "icons": "ad/adfa/a.jpg",
        "password": "111111",
        "wechat": "aaa",
        "roles": "项目管理员,稽查组长"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/user/edit",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 11
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

### 5 通过用户id删除用户接口
- URL
    - /user/deleteById
- 功能
    - 通过用户id删除用户
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
    url: "http://127.0.0.1:8085/3audit/user/deleteById",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 8
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

### 6 通过用户编号删除用户接口
- URL
    - /user/deleteByCode
- 功能
    - 通过用户编号删除用户
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
    url: "http://127.0.0.1:8085/3audit/user/deleteByCode",
    xhrFields: {
        withCredentials: true
    },
    data: {
        code: "008"
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

### 7 更新用户接口
- URL
    - /user/update
- 功能
    - 更新用户
- 参数
    - id
    - name
    - department
    - phone
    - email
    - icon
    - password
    - wechat
    - roles
- 返回值

```json
{
    "success": 1,
    "message": "保存成功",
    "result": {
        "id": 11,
        "name": "abvd",
        "department": "",
        "phone": "13211232232",
        "email": "abc@abc.com",
        "state": 1,
        "state_str": "正常",
        "icons": "ad/adfa/a.jpg",
        "password": "111111",
        "wechat": "aaa",
        "roles": "项目管理员,稽查组长"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/user/update",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 11,
        name: "abvd",
        department: "",
        phone: "13211232232",
        email: "abc@abc.com",
        icon: "ad/adfa/a.jpg",
        password: "111111",
        wechat: "aaa",
        roles: "项目管理员,稽查组长"
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

### 8 更新用户状态接口
- URL
    - /user/changeState
- 功能
    - 更新用户状态
- 参数
    - id
    - state
- 返回值

```json
{
    "success": 1,
    "message": "处理成功",
    "result": {
        "id": 11,
        "name": "abvd",
        "department": "",
        "phone": "13211232232",
        "email": "abc@abc.com",
        "state": 0,
        "state_str": "停用",
        "icons": "ad/adfa/a.jpg",
        "password": "96e79218965eb72c92a549dd5a330112",
        "wechat": "aaa"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/user/deleteById",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 9,
        state: 0
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

### 9 设置用户权限接口
- URL
    - /user/setPrivileges
- 功能
    - 设置用户权限
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
        "id": 11,
        "name": "abvd",
        "department": "",
        "phone": "13211232232",
        "email": "abc@abc.com",
        "state": 0,
        "state_str": "停用",
        "icons": "ad/adfa/a.jpg",
        "password": "96e79218965eb72c92a549dd5a330112",
        "wechat": "aaa"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/user/setPrivileges",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 9,
        privileges: '1,4,22',
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

### 10 获取用户的所有权限接口
- URL
    - /user/getPrivileges
- 功能
    - 获取用户的所有权限
- 参数
    - 无
- 返回值

```json
{
    "success": 1,
    "message": "获取成功",
    "result": [
        {
            "id": 3,
            "user_id": 9,
            "privilege_id": 1,
            "privilege_name": "ccc"
        },
        {
            "id": 4,
            "user_id": 9,
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
    url: "http://127.0.0.1:8085/3audit/user/getPrivileges",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 9,
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

### 11 设置用户角色接口
- URL
    - /user/setRoles
- 功能
    - 设置用户角色
- 参数
    - id
    - roles
- 返回值

```json
{
    "success": 1,
    "message": "编辑成功",
    "result": {
        "id": 11,
        "name": "abvd",
        "department": "",
        "phone": "13211232232",
        "email": "abc@abc.com",
        "state": 0,
        "state_str": "停用",
        "icons": "ad/adfa/a.jpg",
        "password": "96e79218965eb72c92a549dd5a330112",
        "wechat": "aaa"
    }
}
```
- 请求方式
    - GET/POST
- 实例

```javascript
$.ajax({
    type: "GET",
    url: "http://127.0.0.1:8085/3audit/user/setRoles",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 9,
        roles: '2,4'
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

### 12 获取用户角色接口
- URL
    - /user/getRoles
- 功能
    - 获取用户角色
- 参数
    - id
- 返回值

```json
{
    "success": 1,
    "message": "获取成功",
    "result": [
        {
            "id": 7,
            "user_id": 9,
            "role_id": 2,
            "role_name": "项目管理员"
        },
        {
            "id": 8,
            "user_id": 9,
            "role_id": 4,
            "role_name": "稽查组长"
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
    url: "http://127.0.0.1:8085/3audit/user/getRoles",
    xhrFields: {
        withCredentials: true
    },
    data: {
        id: 9,
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

### 13 查询用户分页列表接口
- URL
    - /user/search
- 功能
    - 查询用户分页列表
- 参数 所有参数可有可无
    - page
    - number
    - name
    - phone
    - department (部门名称)
    - role (角色id)
    - state (0或1)
- 返回值

```json
{
    "success": 1,
    "message": "处理成功",
    "result": {
        "content": [
            {
                "id": 11,
                "name": "abvd",
                "department": "",
                "phone": "13211232232",
                "email": "abc@abc.com",
                "state": 1,
                "state_str": "正常",
                "icons": "ad/adfa/a.jpg",
                "password": "111111",
                "wechat": "aaa",
                "roles": "项目管理员,稽查组长"
            },
            {
                "id": 9,
                "name": "abc",
                "department": "",
                "phone": "1234567544433",
                "email": "aaa@aaa.com",
                "state": 1,
                "state_str": "正常",
                "icons": null,
                "password": "abcdef",
                "wechat": null,
                "roles": ""
            },
            {
                "id": 1,
                "name": "admin",
                "department": "",
                "phone": "",
                "email": "admin@admin.com",
                "state": 1,
                "state_str": "正常",
                "icons": null,
                "password": "",
                "wechat": null,
                "roles": ""
            }
        ],
        "last": true,
        "totalPages": 1,
        "totalElements": 3,
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
        "numberOfElements": 3,
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
    url: "http://127.0.0.1:8085/3audit/user/search",
    xhrFields: {
        withCredentials: true
    },
    data:{
      role:4  
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

### 14 上传用户头像接口
- URL
    - /user/uploadUserIcon
- 功能
    - 上传用户头像接口
- 参数 所有参数可有可无
    - file
- 返回值

```json
{
    "success": 1,
    "message": "头像上产成功",
    "result": "http://otbvev7op.bkt.clouddn.com/6867da02-c48b-47fc-a480-5a3074a37899"
}
```
- 请求方式
    - GET/POST
- 实例

```html
<form enctype="multipart/form-data" method="post" id="fileForm" name="fileForm">
    <input type="file" value="xuanzewenjian" id="uploadfile" name="file" onchange="uploadImage()"/>
</form>
```

```javascript
function uploadImage() {
    var form = $("#fileForm");
    var options = {
        url: 'http://127.0.0.1:8085/3audit/user/uploadUserIcon',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        dataType: 'json',
        success: function(data) {
            alert("success：	" + JSON.stringify(data));
        },
        error: function(err) {
            alert('net wrong');
        }
    };
    form.ajaxSubmit(options);
}
```

### 15 更新用户头像接口
- URL
    - /user/updateUserIcon
- 功能
    - 更新用户头像接口
- 参数 所有参数可有可无
    - file
- 返回值

```json
{
    "success": 1,
    "message": "头像上产成功",
    "result": "http://otbvev7op.bkt.clouddn.com/55872b98-b531-4d68-8bf1-03f36818f84c"
}
```
- 请求方式
    - GET/POST
- 实例

```html
<form enctype="multipart/form-data" method="post" id="fileForm" name="fileForm">
    <input type="file" value="xuanzewenjian" id="uploadfile" name="file" onchange="uploadImage()"/>
</form>
```

```javascript
function uploadImage() {
    var form = $("#fileForm");
    var options = {
        url: 'http://127.0.0.1:8085/3audit/user/uploadUserIcon',
        xhrFields: {
            withCredentials: true
        },
        data:{
            id:11
        },
        crossDomain: true,
        dataType: 'json',
        success: function(data) {
            alert("success：	" + JSON.stringify(data));
        },
        error: function(err) {
            alert('net wrong');
        }
    };
    form.ajaxSubmit(options);
}
```

### 16 通过用户角色获取用户接口
- URL
    - /user/getUserByRoleName
- 功能
    - 通过用户角色获取用户
- 参数
    - id
- 返回值

```json
{
    "success": 1,
    "message": "处理成功",
    "result": [
        {
            "id": 11,
            "name": "abvd",
            "department": "",
            "phone": "13211232232",
            "email": "abc@abc.com",
            "state": 1,
            "state_str": "正常",
            "icons": "ad/adfa/a.jpg",
            "password": "111111",
            "wechat": "aaa"
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
    url: "http://127.0.0.1:8085/3audit/user/getUserByRoleName",
    xhrFields: {
        withCredentials: true
    },
    data: {
        roleName: '稽查组长'
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

### 17 修改用户密码
- URL 
   /user/setPassword
- 说明 用户能够自己修改自己的密码   
- 参数
   *  oldPassword   旧密码
   *  newPassword   新密码

- http://118.190.132.68:8084/3audit/user/setPassword?oldPassword&newPassword
### 18 重置密码
- URL 
   /user/resetPassword
- 说明 管理员重置用户密码   
- 参数
   *  userId   用户id
- http://118.190.132.68:8084/3audit/user/resetPassword?userId=12

### 19 登录/登出
- URL
  /login
  /logout

- http://118.190.132.68:8084/3audit/login
- http://118.190.132.68:8084/3audit/logout

### 20 通过部门获取用户
- URL 
   /user/getUsersByDepartment
- 说明 通过部门获取用户   
- 参数
   *  department   部门名称 不必须 默认值为稽查部
- http://118.190.132.68:8084/3audit/user/getUsersByDepartment


### 21 用户解绑微信
- URL 
   /user/unbind
- 说明 用户解绑微信   
- 参数
- http://118.190.132.68:8084/3audit/user/unbind

### 22 管理员解绑用户微信
- URL 
   /user/unbindByAdmin
- 说明 管理员解绑用户微信   
- 参数
    *  id   用户id 必须
- http://118.190.132.68:8084/3audit/user/unbindByAdmin