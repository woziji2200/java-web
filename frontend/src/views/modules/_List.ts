import { getUserRole } from "@/modules/request"

const AppList = [


    {icon: '', title: '转换工具', type: 'line', name: 'line'},
    {icon: 'code', title: 'Base系列', type: 'task', name: 'Base', recommend: true},

    {icon: '', title: '其他', type: 'line', name: 'line'},
    {icon: 'terminal', title: 'Bash', type: 'task', name: 'Bash', recommend: true},

    {icon: '', title: '教务系统', type: 'line', name: 'line'},
    {icon: 'id-card', title: '个人信息', type: 'task', name: 'Profile', recommend: true, single: true},
    {icon: 'chalkboard', title: '课表查看', type: 'task', name: 'ClassApp', recommend: true, single: true},

]

function _getAppList(router: boolean) {
    const _AppList = JSON.parse(JSON.stringify(AppList))
    if(router || getUserRole() === '0' || getUserRole() === 0){
        _AppList.push({icon: 'user', title: '用户管理', type: 'task', name: 'UserApp', recommend: true})
    }
    
    if(router || getUserRole() === '2' || getUserRole() === 2){
        _AppList.push({icon: 'chalkboard', title: '选课工具', type: 'task', name: 'ChooseApp', recommend: true})
    }

    if(router || getUserRole() === '1' || getUserRole() === 1){
        _AppList.push({icon: 'percent', title: '成绩录入工具', type: 'task', name: 'ScoreApp', recommend: true})
    }
    return _AppList
}

export function getAppList() {
    return _getAppList(false).map((i:any) => {
        return {
            ...i,
            pid: -1
        }
    })
}
export function getComponents(){
    let components:any = {}
    _getAppList(true).forEach((i:any) => {
        if (i.type === 'task') {
            components[i.name] = () => import(`./${i.name}.vue`)
        }
    })
    return components
}