import { app } from "..";
import { InternetError, OptionsSetter } from "../lib/core";
import sequelize from "../database/database";
import { User } from "../database/database";
import { md5 } from "js-md5";
import jsonwebtoken from "jsonwebtoken";
export const SALT = process.env.SALT || 'JC,./,SD.RFasdJW498HdfFOLK';
export const SECRET = process.env.SECRET || 'secret';


@app.RestfulApi()
class user {
    @app.get('/version')
    version() {
        return '1.0.0';
    }

    @app.post('/login')
    async login(
        @app.body({ name: 'username', required: true }) username: string,
        @app.body({ name: 'password', required: true }) password: string,
        @app.optionsSetter() optionsSetter: OptionsSetter
    ) {
        const user = await User.findOne({ where: { username, password: md5(password + SALT) } });
        if (user) {
            return {
                code: 200,
                message: '登录成功',
                data: {
                    token: jsonwebtoken.sign(
                        { id: user.dataValues.id, role: user.dataValues.role },
                        SECRET,
                        { expiresIn: '24h', algorithm: 'HS512' }
                    )
                }
            };
        } else {
            optionsSetter.setStatusCode(401);
            return { code: 401, message: '登录失败，用户名或密码不正确' };
        }
    }

    @app.post('/register')
    async register(
        @app.body({
            name: 'username',
            required: true,
            checkFunction: value => value.length > 0 ? true : '用户名不能为空'
        }) username: string,
        @app.body({
            name: 'password',
            required: true,
            checkFunction: value => value.length >= 6 ? true : '密码不能少于6位'
        }) password: string,
        @app.optionsSetter() optionsSetter: OptionsSetter
    ) {
        if (await User.findOne({ where: { username } })) {
            optionsSetter.setStatusCode(400);
            return { code: 400, message: '注册失败，用户名已存在' };
        }
        const user = await User.create({ username, password: md5(password + SALT), role: 2 });
        console.log(user.dataValues);

        return {
            code: 200, message: '注册成功', data: {
                token: jsonwebtoken.sign(
                    { id: user.dataValues.id, role: user.dataValues.role },
                    SECRET,
                    { expiresIn: '24h', algorithm: 'HS512' }
                )
            }
        };
    }


    @app.get('/checkUser')
    async checkUser(
        @app.param({ name: 'id', required: true }) id: number,
        @app.optionsSetter() optionsSetter: OptionsSetter
    ) {
        const user = await User.findOne({ where: { id } });
        console.log(user);
        
        if (user) {
            optionsSetter.setStatusCode(200);
            return { code: 200, message: '用户存在' };
        } else {
            optionsSetter.setStatusCode(404);
            return { code: 404, message: '用户不存在' };
        }
    }

    @app.get('/getAllUsers')
    async getAllUsers() {
        const users = await User.findAll();
        return {
            code: 200,
            message: '获取成功',
            data: users.map(user => user.dataValues)
                .map(user => { delete user.password; return user; })
        };
    }


    @app.post('/changePassword')
    async changePassword(
        @app.body({ name: 'username', required: true }) username: number,
        @app.body({ name: 'oldPassword', required: true }) oldPassword: string,
        @app.body({ name: 'newPassword', required: true }) newPassword: string,
        @app.optionsSetter() optionsSetter: OptionsSetter
    ) {
        const user = await User.findOne({ where: { username, password: md5(oldPassword + SALT) } });
        if (user) {
            await user.update({ password: md5(newPassword + SALT) });
            return { code: 200, message: '修改成功' };
        } else {
            optionsSetter.setStatusCode(401);
            return { code: 401, message: '修改失败，原密码错误' };
        }
    }

    @app.get('/error')
    error() {
        throw new InternetError('服务器错误', 500);
    }
}