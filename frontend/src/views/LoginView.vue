<template>
    <div class="background">
        <div class="background-image h-screen w-screen" :style="background"></div>

        <div class="main">

            <div class="avatar">
                <i class="fa fa-user" />
            </div>

            <div class="text-white text-4xl mt-10 login" v-if="loading">
                {{ username }}
                <div class="loading"></div>
            </div>

            <form class="form" v-else onsubmit="return false">
                <input v-if="!changePwd" v-model="username" @focus="inputFocus = true" @blur="inputFocus = false"
                    type="text" placeholder="请输入用户名" />
                <input v-if="!changePwd" v-model="password" @focus="inputFocus = true" @blur="inputFocus = false"
                    type="password" placeholder="请输入密码" />

                <input v-if="changePwd" v-model="username2" @focus="inputFocus = true" @blur="inputFocus = false"
                    type="text" placeholder="请输入用户名" />
                <input v-if="changePwd" v-model="passwordOld" @focus="inputFocus = true" @blur="inputFocus = false"
                    type="password" placeholder="请输入原密码" />
                <input v-if="changePwd" v-model="passwordNew" @focus="inputFocus = true" @blur="inputFocus = false"
                    type="password" placeholder="请输入新密码" />
                <input v-if="changePwd" v-model="passwordNew2" @focus="inputFocus = true" @blur="inputFocus = false"
                    type="password" placeholder="请确认新密码" />


                <button @click="login" v-if="!changePwd">登录</button>
                <button @click="changePwd2" v-if="changePwd">修改密码</button>
                <span @click="changePwd = !changePwd">{{ changePwd ? "返回登录" : "修改密码" }}</span>
            </form>

            <div></div>
        </div>
    </div>


</template>

<script setup>
import Input from "@/components/Input.vue";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { request } from "@/modules/request";


// import { ElNotification } from 'element-plus';
const router = useRouter();

const loading = ref(false);
const username = ref("");
const password = ref("");
const inputFocus = ref(false);

const background = computed(() => {
    // console.log((inputFocus.value || username.value != '' || password.value != '') ? "filter:brightness(0.3) blur(10px);" : "filter:brightness(0.5);");
    return (inputFocus.value || username.value != '' || password.value != '') ? "filter:brightness(0.3) blur(10px);" : "filter:brightness(0.5);";
});

const login = () => {
    if (username.value == '' || password.value == '') {
        // alert('用户名或密码不能为空');
        ElNotification({
            title: '错误',
            message: '用户名或密码不能为空',
            type: 'error'
        });
        return;
    }
    loading.value = true;
    request({
        url: "/auth/login",
        method: "post",
        data: {
            username: username.value,
            password: password.value,
        },
        noAuth: true,
    }).then((res) => {
        localStorage.setItem("token", res.data.token);
        setTimeout(() => {
            router.push("/");
        }, 2000);
        ElNotification({
            title: '成功',
            message: '登录成功',
            type: 'success'
        });
        // router.push("/");
    }).catch((err) => {

        setTimeout(() => {
            ElNotification({
                title: '错误',
                message: '登录失败，请检查用户名和密码',
                type: 'error'
            });
            loading.value = false;
        }, 2000);

    });



};

const changePwd = ref(false);
const username2 = ref("");
const passwordOld = ref("");
const passwordNew = ref("");
const passwordNew2 = ref("");
const changePwd2 = () => {
    if (username2.value == '' || passwordOld.value == '' || passwordNew.value == '' || passwordNew2.value == '') {
        // alert('用户名或密码不能为空');
        ElNotification({
            title: '错误',
            message: '用户名或密码不能为空',
            type: 'error'
        });
        return;
    }
    if (passwordNew.value != passwordNew2.value) {
        ElNotification({
            title: '错误',
            message: '两次输入的密码不一致',
            type: 'error'
        });
        return;
    }
    request({
        url: "/auth/changePassword",
        method: "post",
        data: {
            username: username2.value,
            oldPassword: passwordOld.value,
            newPassword: passwordNew.value,
        },
        noAuth: true,
    }).then((res) => {
        ElNotification({
            title: '成功',
            message: '修改密码成功',
            type: 'success'
        });
        changePwd.value = false;
    }).catch((err) => {
        ElNotification({
            title: '错误',
            message: '修改密码失败，请检查用户名和密码',
            type: 'error'
        });
    });
};



</script>

<style scoped lang="scss">
.v-enter-active,
.v-leave-active {
    position: absolute;
    transition: opacity 0.5s ease;
}

.v-enter-from,
.v-leave-to {
    opacity: 0;
}


.background {
    position: relative;
    overflow: hidden;

    .background-image {
        position: absolute;
        background: url('../assets/bg.webp');
        width: 100vw;
        height: 100vh;
        background-size: cover;
        background-position: center;
        background-repeat: no-repeat;
        // filter: brightness(0.5) blur(10px);
        z-index: -1000;
        transition: filter 0.5s;
    }
}

.main {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    flex-direction: column;

    .login {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        margin-bottom: 40px;

        .loading {
            margin-top: 40px;
            position: relative;
            width: 50px;
            height: 50px;
            border: 4px solid #ffffff;
            border-top-color: rgba(255, 255, 255, 0.2);
            border-right-color: rgba(255, 255, 255, 0.2);
            border-bottom-color: rgba(255, 255, 255, 0.2);
            border-radius: 100%;

            animation: circle infinite 0.75s linear;
        }

        @keyframes circle {
            0% {
                transform: rotate(0);
            }

            100% {
                transform: rotate(360deg);
            }
        }
    }

    .avatar {
        font-size: 70px;
        color: #ffffff;
        background-color: #383838;
        border-radius: 50%;
        width: 150px;
        height: 150px;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .form {
        margin-top: 40px;
        display: flex;
        flex-direction: column;
        margin-bottom: 50px;

        input {
            width: 300px;
            height: 40px;
            border: none;
            border-radius: 5px;
            padding: 0 10px;
            font-size: 16px;
            outline: none;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
            margin-bottom: 20px;
            background-color: #ffffff3f;
            color: #ffffff;

            &::placeholder {
                color: #ffffff7e;
            }
        }

        button {
            width: 300px;
            height: 40px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            background-color: #ffffff3f;
            color: #ffffff;
            cursor: pointer;
            transition: background-color 0.5s;

            &:hover {
                background-color: #ffffff5f;
            }
        }

        span {
            color: #ffffff88;
            cursor: pointer;
            margin-top: 20px;
            text-align: right;
            font-size: 14px;
        }
    }
}
</style>