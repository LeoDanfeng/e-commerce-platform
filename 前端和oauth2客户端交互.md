## 前端使用 Gateway 授权后的访问令牌和刷新令牌

### 1. **令牌存储**
```javascript
// 存储令牌到 localStorage 或 sessionStorage
localStorage.setItem('access_token', accessToken);
localStorage.setItem('refresh_token', refreshToken);
```


### 2. **请求拦截器配置**
```javascript
// 在 axios 或其他 HTTP 客户端中配置请求拦截器
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('access_token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
```


### 3. **响应拦截器处理令牌过期**
```javascript
// 处理 401 状态码（令牌过期）
axios.interceptors.response.use(
    response => response,
    async error => {
        if (error.response?.status === 401) {
            const refreshToken = localStorage.getItem('refresh_token');
            if (refreshToken) {
                try {
                    // 调用刷新令牌接口
                    const response = await axios.post('/api/auth/refresh', {
                        refresh_token: refreshToken
                    });
                    
                    const { access_token, refresh_token } = response.data;
                    localStorage.setItem('access_token', access_token);
                    localStorage.setItem('refresh_token', refresh_token);
                    
                    // 重新发送原始请求
                    error.config.headers.Authorization = `Bearer ${access_token}`;
                    return axios.request(error.config);
                } catch (refreshError) {
                    // 刷新失败，跳转到登录页
                    localStorage.removeItem('access_token');
                    localStorage.removeItem('refresh_token');
                    window.location.href = '/login';
                }
            }
        }
        return Promise.reject(error);
    }
);
```


### 4. **前端登录流程**
```javascript
// 1. 重定向到网关 OAuth2 端点
window.location.href = 'http://gateway-host/';

// 2. 网关处理完认证后重定向回前端回调地址
// 3. 前端从回调 URL 中获取令牌信息
```


### 5. **令牌使用示例**
```javascript
// 发送 API 请求时自动携带令牌
const response = await axios.get('/api/products');
// 请求头会自动包含: Authorization: Bearer <access_token>
```


### 6. **安全建议**
- 使用 `HTTPS` 传输令牌
- 设置令牌过期时间
- 定期刷新访问令牌
- 在用户登出时清除令牌
- 考虑使用 `HttpOnly` Cookie 存储刷新令牌以增强安全性