<template>

    <div class="class-tools" v-if="userRole === 0">
        <el-button type="primary" @click="addClassVisible = true" v-show="userRole === 0">新建课程</el-button>
    </div>
    <div class="main-class">
        <el-table :data="classDataShow" style="width: 100%">
            <el-table-column prop="id" label="课程号" width="80" v-show="!editUser" />

            <el-table-column prop="name" label="课程名" width="180" v-show="!editUser" />
            <el-table-column prop="teacherName" label="教师名" width="180" />
            <el-table-column prop="credit" label="学分" width="80" />
            <el-table-column label="操作" width="180" v-if="userRole === 0">
                <template v-slot="{ row }">
                    <!-- <el-button link type="primary" size="small" @click="editUserHandle(row)">编辑</el-button> -->
                    <el-popconfirm title="确定要删除吗？" @confirm="deleteClass(row)">
                        <template #reference>
                            <el-button type="primary" link size="small">删除</el-button>
                        </template>
                    </el-popconfirm>
                </template>
            </el-table-column>

        </el-table>
    </div>


    <div :style="{ display: addClassVisible ? 'block' : 'none' }" class="dialog">
        <el-dialog v-model="addClassVisible" :title="'新建课程'" width="70%">

            <template #default>
                <el-form label-position="right" label-width="100px">
                    <!-- <el-form-item label="学/工号" v-if="!editUser">
                        <el-input v-model="addUserForm.username"></el-input>
                    </el-form-item>
                    <el-form-item label="姓名">
                        <el-input v-model="addUserForm.nickname"></el-input>
                    </el-form-item> -->

                    <el-form-item label="课程名">
                        <el-input v-model="addClassForm.name"></el-input>
                    </el-form-item>
                    <el-form-item label="教师工号">
                        <el-input v-model="addClassForm.teacherUsername"></el-input>
                    </el-form-item>
                    <el-form-item label="学分">
                        <el-input v-model="addClassForm.credit"></el-input>
                    </el-form-item>
                </el-form>
            </template>

            <template #footer>
                <el-button @click="addClassVisible = false">取消</el-button>
                <el-button type="primary" @click="addClass">确定</el-button>
            </template>
        </el-dialog>
    </div>

</template>

<script setup>
import { ref } from 'vue'
import { emitter } from '@/modules/emitter';
import { request, getUserRole } from '@/modules/request';
const userRole = getUserRole()
// console.log(userRole)
const classData = ref([])
const classDataShow = ref([])
const getClassData = async () => {
    const res = await request({
        url: '/class/class',
        method: 'GET',
    })
    classData.value = res.data
    classDataShow.value = res.data
}
getClassData()


const deleteClass = async (row) => {
    try {
        const res = await request({
            url: `/class/class/${row.id}`,
            method: 'DELETE',
        })
        getClassData()
        // emitter.emit('notify', { title: '用户管理', message: '删除成功', type: 'success' })
        emitter.emit('notify', { title: '课程管理', message: '删除成功', type: 'success' })
    } catch (error) {
        console.log(error)
        // emitter.emit('notify', { title: '用户管理', message: '删除失败' + error, type: 'error' })
        emitter.emit('notify', { title: '课程管理', message: '删除失败 ' + error.response.data.message, type: 'error' })
    }
}

const addClass = async () => {
    try {
        const res = await request({
            url: '/class/class',
            method: 'POST',
            data: addClassForm.value,
        })
        getClassData()
        addClassVisible.value = false
        // emitter.emit('notify', { title: '用户管理', message: '添加成功', type: 'success' })
        emitter.emit('notify', { title: '课程管理', message: '添加成功', type: 'success' })
    } catch (error) {
        console.log(error)
        // emitter.emit('notify', { title: '用户管理', message: '添加失败' + error, type: 'error' })
        emitter.emit('notify', { title: '课程管理', message: '添加失败 ' + error.response.data.message, type: 'error' })
    }
}



const addClassVisible = ref(false)
const addClassForm = ref({
    name: '',
    teacherUsername: '',
    credit: 0,
})
</script>

<style scoped>
:deep(.el-table *) {
    color: white;
    background-color: transparent !important;

}

:deep(.el-table) {
    color: white;
    background-color: transparent !important;
    --el-table-border-color: rgba(255, 255, 255, 0.4) !important;
}

.main-class * {
    background-color: transparent !important;

    tr {
        color: white !important;
    }
}

.main-class {
    padding: 20px;
}
.class-tools {
    padding: 20px;
}
</style>