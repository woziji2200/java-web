import { md5 } from "js-md5";
import { app, gateway } from "..";
import { User } from "../database/database";
import { SALT } from "./user";
import { OptionsSetter } from "../lib/core";


@app.RestfulApi("/admin")
class admin {
    @app.get('/test')
    test() {
        return 'test';
    }

    @app.post('/user')
    async addUser(
        @app.body({ name: 'username', required: true }) username: string,
        @app.body({ name: 'role', required: true }) role: string,
        @app.body({ name: 'nickname', required: true }) nickname: string,
        @app.optionsSetter() optionsSetter: OptionsSetter
    ) {
        try {
            const userTemp = await User.findOne({ where: { username } });
            if (userTemp) {
                optionsSetter.setStatusCode(400);
                return { code: 400, message: '添加失败，用户名已存在' };
            }
            const user = await User.create({ username, role, password: md5('123456' + SALT) });
            let userInfoUrl = await fetch(gateway + '/server/select?path=/userinfo')
            userInfoUrl = (await userInfoUrl.json()).server
            await fetch(userInfoUrl + '/userinfo', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'x-forward-id': user.dataValues.id.toString(),
                    'x-forward-role': '2'
                },
                body: JSON.stringify({ nickname })
            })

            return { code: 200, message: '添加成功', data: user };
        } catch (error) {
            optionsSetter.setStatusCode(400);
            return { code: 400, message: '添加失败' };
        }
    }


    @app.put('/user/:id')
    async updateUser(
        @app.urlParam({ name: 'id' }) id: string,
        @app.body({ name: 'nickname' }) nickname: string,
        @app.body({ name: 'role' }) role: string,
        @app.optionsSetter() optionsSetter: OptionsSetter
    ) {
        try {
            const user = await User.findByPk(parseInt(id));
            if (!user) {
                optionsSetter.setStatusCode(400);
                return { code: 400, message: '修改失败，用户不存在' };
            }
            if (nickname) {
                let userInfoUrl = await fetch(gateway + '/server/select?path=/userinfo')
                userInfoUrl = (await userInfoUrl.json()).server
                await fetch(userInfoUrl + '/userinfo', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'x-forward-id': user.dataValues.id.toString(),
                        'x-forward-role': '2'
                    },
                    body: JSON.stringify({ nickname })
                })
            }
            if (role) {
                await User.update({ role }, { where: { id } })
            }
            await user.save();
            return { code: 200, message: '修改成功' };
        } catch (error) {
            console.log(error);

            optionsSetter.setStatusCode(400);
            return { code: 400, message: '修改失败', data: { error } };
        }
    }

    @app.delete('/user/:id')
    async deleteUser(
        @app.optionsSetter() optionsSetter: OptionsSetter,
        @app.urlParam({ name: 'id' }) id: string
    ) {
        try {
            const user = await User.findByPk(id)
            if (user) {
                await user.destroy()
                return { code: 200, message: "删除成功" }
            } else {
                optionsSetter.setStatusCode(400)
                return { code: 400, message: '删除失败，用户不存在' };
            }
        } catch (error) {
            optionsSetter.setStatusCode(400)
            return { code: 400, message: '删除失败' };
        }
    }

}