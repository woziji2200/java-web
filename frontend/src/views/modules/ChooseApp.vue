<template>

    <div class="class-tools">
        <el-input v-model="classId" placeholder="选择课程"></el-input>
        <el-button type="primary" @click="addClass">确定</el-button>
    </div>
    <div class="main-class">
        <el-table :data="classDataShow" style="width: 100%">
            <el-table-column prop="classId" label="课程号" width="80" v-show="!editUser" />

            <el-table-column prop="name" label="课程名" width="180" v-show="!editUser" />
            <!-- <el-table-column prop="credit" label="学分" width="80" /> -->
            <!-- <el-table-column prop="teacherName" label="教师名" width="180" /> -->
            <el-table-column prop="score" label="成绩" width="80" >
                <template v-slot="{ row }">
                    {{ row.score === 0 ? '未录入' : row.score }}
                </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
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
        url: '/class/choose',
        method: 'GET',
    })
    classData.value = res.data
    classDataShow.value = res.data
}
getClassData()
const classId = ref('')
const addClass = async () => {
    // const res = await request({
    //     url: '/class/choose',
    //     method: 'PUT',
    //     data: {
    //         classId: classId.value
    //     }
    // })
    // getClassData()
    try {
        const res = await request({
            url: '/class/choose',
            method: 'PUT',
            data: {
                classId: classId.value
            }
        })
        getClassData()
        emitter.emit('notify', { title: '课程管理', message: '选课成功', type: 'success' })
    } catch (error) {
        emitter.emit('notify', { title: '课程管理', message: '选课失败 ' + error.response.data.message, type: 'error' })
        
    }
}

const deleteClass = async (row) => {
    try {
        const res = await request({
            url: `/class/choose?classId=${row.classId}`,
            method: 'DELETE',
            
        })
        getClassData()
        emitter.emit('notify', { title: '课程管理', message: '删除成功', type: 'success' })
    } catch (error) {
        console.log(error)
        emitter.emit('notify', { title: '课程管理', message: '删除失败 ' + error.response.data.message, type: 'error' })
    }
}



const addClassVisible = ref(false)
const addClassForm = ref({
    name: '',
    teacherUsername: '',
    credit: 0,
})
</script>

<style scoped lang="scss">
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
    display: flex;
    gap: 20px;
}


</style>

<style>

.class-tools * {
    color: white;
    background-color: transparent !important;

}
</style>