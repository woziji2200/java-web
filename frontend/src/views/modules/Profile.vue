<template>
    <div class="main">
        <div class="title">滑稽果教务系统</div>

        <div class="profile">
            <div class="avatar">
                <div class="avatar-default" v-if="!userInfo.avatar">
                    <i class="fa fa-user" />
                </div>
                <img v-else :src="baseURL + '/userinfo' + userInfo.avatar" alt="">

            </div>
            <div class="wel">
                <div style="" class="text-3xl mb-4">
                    <span>欢迎您，</span>
                    <span>{{ userInfo.nickname }}</span>
                    <el-tag class="ml-3" effect="dark"> {{ ['管理员', '教师', '学生'][userRole] }} </el-tag>
                </div>
                <div>
                    <Button title="上传新头像" class="mr-4" @click="uploadAvatar"></Button>
                    <Button title="退出登录" @click="logout"></Button>
                    <input type="file" style="display: none" id="file" />

                </div>
            </div>
        </div>

    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ButtonVue from '../../components/Button.vue'
import { useRouter } from 'vue-router';
import { getUserRole, request, baseURL } from '@/modules/request';
import { emitter } from '@/modules/emitter';
const router = useRouter()
const userRole = getUserRole()
const userInfo = ref({
    nickname: '',
    avatar: ''
})
request({
    url: '/userinfo/userinfo',
    method: 'GET',
}).then((res: any) => {
    userInfo.value = res.data
})

const logout = () => {
    localStorage.removeItem('token')
    router.push('/login')
    window.location.reload()
}

const uploadAvatar = () => {
    const file = document.getElementById('file') as HTMLInputElement
    file.click()
    file.onchange = () => {
        if (!file.files) return
        const formData = new FormData()
        formData.append('avatar', file.files![0])
        request({
            url: '/userinfo/avatar',
            method: 'POST',
            data: formData,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }).then((res: any) => {
            request({
                url: '/userinfo/userinfo',
                method: 'GET',
            }).then((res: any) => {
                userInfo.value = res.data
            }).catch((error: any) => {
                emitter.emit('notify', { title: '用户信息', message: '上传失败 ' + error.response.data.message, type: 'error' })
            })
        })
    }
}
</script>

<style scoped lang="scss">
.main {
    padding: 40px;
}

.title {
    color: white;
    font-size: 40px;
}

.wel {
    color: white;
    /* font-size: 26px; */
    margin-top: 30px;
    margin-left: 20px;
}

.avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    /* background-color: white; */
    margin-top: 20px;

    img {
        width: 100%;
        height: 100%;
        border-radius: 50%;

    }
}

.profile {
    display: flex;
    margin-top: 20px;
    margin-bottom: 50px;
}

.avatar-default {
    font-size: 40px;
    color: #ffffff;
    background-color: #383838;
    border-radius: 50%;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>