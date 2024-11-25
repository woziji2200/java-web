import { app } from "..";
import sequelize from "../database/database";
import { User } from "../database/database";
import { md5 } from "js-md5";
import jsonwebtoken from "jsonwebtoken";
const SALT = process.env.SALT || 'JC,./,SD.RFasdJW498HdfFOLK';
const SECRET = process.env.SECRET || 'secret';


@app.RestfulApi()
class user {
    @app.get('/version')
    version() {
        return '1.0.0';
    }

    @app.post('/login')
    async login(
        @app.body({ name: 'username', required: true }) username: string,
        @app.body({ name: 'password', required: true }) password: string
    ) {
        const user = await User.findOne({ where: { username, password: md5(password + SALT) } });
        if (user) {
            return {
                code: 200,
                message: '登录成功',
                data: {
                    token: jsonwebtoken.sign({ id: user.dataValues.id, role: user.dataValues.role },
                        SECRET,
                        { expiresIn: '24h', algorithm: 'HS512' })
                }
            };
        } else {
            return { code: 401, message: '登录失败，用户名或密码不正确' };
        }
    }

    @app.post('/register')
    async register(){}
}