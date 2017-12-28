# 接口文档
** 本文档供前端和后台调用查找功能说明。
### 带有权限管理和CAS的API使用方法
* Ajax请求请携带cookie
    - 示例
    
```javascript
xhrFields: {
    withCredentials: true
}
```
* Ajax请求请允许跨域
    - 示例
    
```javascript
    crossDomain: true
```

* Ajax过CAS并获取认证cookie方式
- 1.登录请求
```javascript
$(function() {
    $.ajax({
        type: "POST",
        url: "http://127.0.0.1:8085/3audit/login",
        beforeSend: function(xhr) {
            xhr.setRequestHeader('JWCQ', 'JWCQ');
        },
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function(response) {},
        fail: function(request, status, error) {
            alert("error" + error);
        },
        complete: function(xhr, textStatus) {},
    });
});
```
    - 说明
    
    通过请求http://127.0.0.1:8085/3audit/login，使服务器返回cas-server login的url，并使浏览器跳转到cas-server的loginurl,
    此请求，服务器会返回status等于203的response，所以需要浏览器能够根据返回内容进行跳转
    
```javascript

$(document).ajaxComplete(function(e, xhr, settings) {
    if (xhr.status === 201) {
        _location = xhr.getResponseHeader("Location");
        _location = _location.substring(_location.lastIndexOf("/") + 1);
        console.log(_location);
    } else if (xhr.status === 203) {
        rText = xhr.responseText;
        console.log(rText);
        var obj = JSON.parse(rText);
        url = obj["result"];
        console.log(url);
        window.location.href = url;
    }
});
```
    - 注意
    
    请携带请求头
    
```javascript
beforeSend: function(xhr) {
    xhr.setRequestHeader('JWCQ', 'JWCQ');
}
```

###  上传用户头像的方法
* 由于ajaxFileUpload对于MultipartFile支持不好，并且会有各种各样的问题，例如X-Frame-Options：deny，所有摒弃使用ajaxFileUpload，使用jquery-form.js
** [jquery-form.js](http://118.190.132.68:3000/qiaorong/3audit/src/master/src/main/webapp/WEB-INF/js/jquery-form.js)
* jquery-form.js简单使用
html中有如下form

```html
<form enctype="multipart/form-data" method="post" id="fileForm" name="fileForm">
    <input type="file" value="选择图片" id="uploadfile" name="file" onchange="uploadImage()"/>
</form>
```
相关js代码

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
* 注意html代码中name="file"表明参数名称！
# 一些约定

-    用户名字为唯一，不能重复。
