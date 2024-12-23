<template>
    <div @contextmenu.prevent="">
        <el-config-provider :size="size" :z-index="zIndex">
            <router-view></router-view>
        </el-config-provider>
    </div>
</template>
<script>
import { defineComponent } from 'vue'
import { ElConfigProvider } from 'element-plus'
import { ElNotification } from 'element-plus';
import { emitter } from './modules/emitter';
emitter.on('notify', (msg) => {
    console.log(msg);
    ElNotification({
        title: (msg).title,
        message: (msg).message,
        type: (msg).type
    })
})
export default defineComponent({
    components: {
        ElConfigProvider,
    },
    setup() {


        return {
            zIndex: 23000,
            // size: 'small',
        }
    },
})
</script>
<style lang="scss">
@import url(./modules/drag.css);

.el-notification {
    background-color: rgba(255, 255, 255, 0) !important;
    backdrop-filter: blur(20px) !important;
    color: white !important;
    border: solid 1px rgba(0, 0, 0, 0.113) !important;
    box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.643) !important;
    display: flex !important;
    // position: relative !important;
}

.el-message {
    // background-color: rgba(255, 255, 255, 0) !important;
    // backdrop-filter: blur(20px) !important;
    color: white !important;
    border: solid 1px rgba(0, 0, 0, 0.113) !important;
    box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.643) !important;
    display: inline-flex !important;
    // position: absolute !important;
}

.el-notification__title {
    color: rgb(238, 238, 238) !important;
}

.el-notification__content {
    color: rgb(188, 188, 188) !important;
}

.mx-menu-ghost-host {
    z-index: 10000 !important;
}

.scroll-container:hover::-webkit-scrollbar-thumb {
    background: #a3a3a3b8;
}

.scroll-container:hover::-webkit-scrollbar-track {
    background: #ffffff1e;
}

/* 滚动条整体 */
.scroll-container::-webkit-scrollbar {
    height: 3px;
    width: 3px;
    cursor: pointer;
    transition: all 0.3s;
}

/* 两个滚动条交接处 -- x轴和y轴 */
.scroll-container::-webkit-scrollbar-corner {
    background-color: transparent;
}

/* 滚动条滑块 */
.scroll-container::-webkit-scrollbar-thumb {
    border-radius: 10px;
    // -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
    background: #a3a3a3b8;

}

/* 滚动条轨道 */
.scroll-container::-webkit-scrollbar-track {
    // -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
    border-radius: 10px;
    background: #ffffff1e;
}

/* 滚动条两端按钮 */
.scroll-container::-webkit-scrollbar-button {}
</style>
