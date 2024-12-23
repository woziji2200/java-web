<template>
    <div class="score-main">
        <el-tabs class="tabs" v-model="activeName">
            <el-tab-pane v-for="i in classData" :label="i.name" :name="i.name">
                <el-table :data="i.students" style="width: 100%">
                    <el-table-column prop="username" label="学号" width="80" />
                    <el-table-column prop="nickname" label="姓名" width="180" />
                    <el-table-column prop="score" label="成绩" width="80">
                        <template v-slot="{ row }">
                            {{ row.score === 0 ? '未录入' : row.score }}
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="180">
                        <template v-slot="{ row }">
                            <el-button type="primary" link size="small"
                                @click="changeScoreHandler(row, i)">修改成绩</el-button>
<!-- 
                            <el-popconfirm title="确定要删除吗？" @confirm="deleteClass(row)">
                                <template #reference>
                                    <el-button type="primary" link size="small">删除</el-button>
                                </template>
                            </el-popconfirm> -->
                        </template>
                    </el-table-column>
                </el-table>
            </el-tab-pane>
        </el-tabs>
    </div>

    <div :style="{ display: scoreFormVisible ? 'block' : 'none' }" class="dialog">
        <el-dialog v-model="scoreFormVisible" :title="'修改成绩'" width="70%">

            <template #default>
                <el-form label-position="right" label-width="100px">
                    <el-form-item label="新成绩">
                        <el-input v-model="scoreForm.score"></el-input>
                    </el-form-item>
                </el-form>
            </template>

            <template #footer>
                <el-button @click="scoreFormVisible = false">取消</el-button>
                <el-button type="primary" @click="changeScore">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { emitter } from '@/modules/emitter';
import { request, getUserRole } from '@/modules/request';
const userRole = getUserRole()

const classData = ref([])
const activeName = ref('')
const getClassData = async (refresh) => {
    const res = await request({
        url: '/class/choose/teacher',
        method: 'GET',
    })
    classData.value = res.data
    if(refresh)
        activeName.value = res.data[0].name
}
getClassData(true)

const scoreFormVisible = ref(false)
const scoreForm = ref({
    studentId: '',
    score: '',
    classId: ''
})
const changeScoreHandler = (row, i) => {
    console.log(row, i)
    scoreFormVisible.value = true
    scoreForm.value.studentId = row.studentId
    scoreForm.value.score = row.score
    scoreForm.value.classId = i.classId
}

const changeScore = async () => {
    try {
        console.log(scoreForm.value)
        const res = await request({
            url: '/class/choose/score',
            method: 'POST',
            data: {
                studentId: scoreForm.value.studentId,
                score: parseInt(scoreForm.value.score),
                classId: scoreForm.value.classId
            }
        })
        getClassData(false)
        scoreFormVisible.value = false
        emitter.emit('notify', { title: '成绩管理', message: '修改成功', type: 'success' })
    } catch (error) {
        emitter.emit('notify', { title: '成绩管理', message: '修改失败 ' + error.response.data.message, type: 'error' })

    }

}


</script>

<style scoped lang="scss">
.score-main {
    padding: 20px;
    color: white !important;
}

:deep(.tabs) {
    .el-tabs__item {
        color: white !important;
    }

}

:deep(.el-table *) {
    color: white;
    background-color: transparent !important;

}

:deep(.el-table) {
    color: white;
    background-color: transparent !important;
    --el-table-border-color: rgba(255, 255, 255, 0.4) !important;
}
</style>