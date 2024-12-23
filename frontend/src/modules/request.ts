// axios

import axios from 'axios';
interface RequestConfig {
    method: 'GET' | 'POST' | 'PUT' | 'DELETE';
    url: string;
    data?: any;
    headers?: any;
    params?: any;
    noAuth?: boolean;
}
// export const baseURL = 'http://localhost:3100';
export const baseURL = "http://10.169.0.81:3100";
export const isLogin = () => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            return false;
        }
        const payload = JSON.parse(atob(token.split('.')[1]));
        if (payload.exp < Date.now() / 1000) {
            return false;
        }
        return true;
    } catch (e) {
        return false;
    }    
}

export const getUserRole = () => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            return '';
        }
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.role;
    } catch (e) {
        return '';
    }
}

export const request = async (config: RequestConfig) => {
    if (!config.noAuth && !isLogin()) {
        return Promise.reject('未登录');
    }    
    const headers = {
        'Content-Type': 'application/json',
        ...config.headers
    }
    if (!config.noAuth) {
        headers['Authorization'] = `Bearer ${localStorage.getItem('token')}`
    }
    return new Promise((resolve, reject) => {
        axios({
            method: config.method,
            url: baseURL + config.url,
            data: config.data,
            headers: headers,
            params: config.params
        }).then(res => {
            resolve(res.data);
        }).catch(err => {
            reject(err);
        })
    })
}