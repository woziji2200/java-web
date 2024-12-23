<template>
    <div class="search">
        <el-input v-model="userSearchUsername" placeholder="搜索学工号" class="mr-3" style="width: 300px"></el-input>
        <el-input v-model="userSearchNickname" placeholder="搜索姓名" class="mr-3" style="width: 300px"></el-input>
        <select name="" id="" v-model="userSearchRole" style="width: 200px;" class="mr-3 text-sm">
            <option :value="3">全部</option>
            <option :value="2">学生</option>
            <option :value="1">教师</option>
            <option :value="0">管理员</option>
        </select>
        <el-button type="primary" @click="searchUser">搜索</el-button>
        <el-button type="primary" @click="addUserHandler">新建用户</el-button>
    </div>
    <div class="main-user">
        <el-table :data="userDataShow" style="width: 100%">
            <el-table-column prop="username" label="学/工号" width="180" v-show="!editUser"/>
            <el-table-column prop="nickname" label="姓名" width="180" />
            <el-table-column label="用户类型">
                <template v-slot="{ row }">
                    <span v-if="row.role === 2">学生</span>
                    <span v-else-if="row.role === 1">教师</span>
                    <span v-else-if="row.role === 0">管理员</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
                <template v-slot="{ row }">
                    <el-button link type="primary" size="small" @click="editUserHandle(row)">编辑</el-button>
                    <el-popconfirm title="确定要删除吗？" @confirm="deleteUser(row)">
                        <template #reference>
                            <el-button type="primary" link size="small">删除</el-button>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>

        </el-table>



    </div>

    <div :style="{ display: addUserVisible ? 'block' : 'none' }" class="dialog">
        <el-dialog v-model="addUserVisible" :title="editUser ? '编辑用户信息' : '新建用户'" width="70%">

            <template #default>
                <el-form label-position="right" label-width="100px">
                    <el-form-item label="学/工号" v-if="!editUser">
                        <el-input v-model="addUserForm.username"></el-input>
                    </el-form-item>
                    <el-form-item label="姓名">
                        <el-input v-model="addUserForm.nickname"></el-input>
                    </el-form-item>
                    <el-form-item label="用户类型">
                        <select v-model="addUserForm.role" placeholder="请选择">
                            <option label="学生" :value="2"></option>
                            <option label="教师" :value="1"></option>
                            <option label="管理员" :value="0"></option>
                        </select>
                    </el-form-item>
                </el-form>
            </template>

            <template #footer>
                <el-button @click="addUserVisible = false">取消</el-button>
                <el-button type="primary" @click="addUser">确定</el-button>
            </template>


        </el-dialog>
    </div>


</template>


<script setup>
import { ref, getCurrentInstance } from 'vue'
import { request } from '@/modules/request';
import { emitter } from '@/modules/emitter';


// import { ElMessage, ElNotification } from 'element-plus';
// const { appContext } = getCurrentInstance()
// ElMessage({zIndex: 30000})
// ElNotification({
//     zIndex: 20000
// })

const userDataAll = ref([])
const userDataShow = ref([])
const getUserData = async () => {
    const res = await request({
        url: '/userinfo/userinfo/all',
        method: 'GET',
    })
    userDataAll.value = res.data
    userDataShow.value = res.data
}
getUserData()


const deleteUser = async (row) => {
    try {
        await request({
            url: `/auth/admin/user/${row.id}`,
            method: 'DELETE',
        })
        emitter.emit('notify', { title: '用户管理', message: '删除成功', type: 'success' })

        getUserData()

    } catch (error) {
        console.log(error)
        emitter.emit('notify', { title: '用户管理', message: '删除失败' + error, type: 'error' })

    }
}

const userSearchUsername = ref('')
const userSearchNickname = ref('')
const userSearchRole = ref(3)
const searchUser = () => {
    userDataShow.value = userDataAll.value.filter((item) => {
        if (userSearchUsername.value) {
            return item.username.includes(userSearchUsername.value) && (userSearchRole.value === 3 || item.role === userSearchRole.value)
        }
        if (userSearchNickname.value) {
            return item.nickname?.includes(userSearchNickname.value) && (userSearchRole.value === 3 || item.role === userSearchRole.value)
        }
        if (userSearchRole.value !== 3) {
            return item.role === userSearchRole.value
        }
        return true
    })
}

const editUser = ref(false)
const editUserId = ref('')
const editUserHandle = (row) => {
    console.log(row);
    editUser.value = true
    addUserForm.value.nickname = row.nickname
    addUserForm.value.username = row.username
    addUserForm.value.role = row.role
    editUserId.value = row.id
    addUserVisible.value = true
}
const addUserHandler = () => {
    addUserVisible.value = true
    editUser.value = false
}
const addUserVisible = ref(false)
const addUserForm = ref({
    username: '',
    nickname: '',
    role: 2,
})
const addUser = async () => {
    try {
        if(!addUserForm.value.username || !addUserForm.value.nickname) {
            emitter.emit('notify', { title: '用户管理', message: '学/工号和姓名不能为空', type: 'error' })
            return
        }
        if(editUser.value) {
            let data = {}
            data.nickname = addUserForm.value.nickname
            data.role = parseInt(addUserForm.value.role)
            await request({
                url: `/auth/admin/user/${editUserId.value}`,
                method: 'PUT',
                data: data
            })
            emitter.emit('notify', { title: '用户管理', message: '修改成功', type: 'success' })
            getUserData()
            addUserForm.value = {
                username: '',
                nickname: '',
                role: 2
            }
            addUserVisible.value = false
            return
        }


        await request({
            url: '/auth/admin/user',
            method: 'POST',
            data: addUserForm.value
        })
        emitter.emit('notify', { title: '用户管理', message: '添加成功', type: 'success' })
        getUserData()
        addUserForm.value = {
            username: '',
            nickname: '',
            role: 2
        }
        addUserVisible.value = false
    } catch (error) {
        console.log(error)
        emitter.emit('notify', { title: '用户管理', message: error.response.data.message, type: 'error' })
    }
}

</script>

<style>
.el-overlay {
    display: flex !important;
}
</style>

<style scope lang="scss">
:deep(.el-table *) {
    color: white;
    background-color: transparent !important;

}

:deep(.el-table) {
    color: white;
    background-color: transparent !important;
    --el-table-border-color: rgba(255, 255, 255, 0.4) !important;
}

.main-user {
    padding: 10px;
}

.main-user * {
    background-color: transparent !important;

    tr {
        color: white !important;
    }
}


.search {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
    padding: 10px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.4);

    * {
        color: white;
        background-color: transparent !important;
    }

    select {
        background-color: rgba(255, 255, 255, 0.4) !important;
        color: white;
        border: none;
        outline: none;
    }
}

.dialog {
    // background-color: rgba(255, 255, 255, 0.4) !important;
    * {
        color: white;
        // background-color: rgba(182, 182, 182, 0.253);
    }

    .el-dialog {
        background-color: rgba(0, 0, 0, 0.4) !important;
        color: white;

        * {
            color: white;
            background-color: transparent !important;
        }
    }
}
</style>