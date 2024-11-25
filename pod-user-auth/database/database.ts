import { Sequelize, DataTypes } from "sequelize";
import path from "path";

// console.log(path.join(__dirname, 'database', 'database.sqlite'));

const sequelize = new Sequelize({ storage: path.join(__dirname, 'database.sqlite'), dialect: 'sqlite' });
export const User = sequelize.define('users', {
    id: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true
    },
    username: {
        type: DataTypes.STRING,
        allowNull: false
    },
    password: {
        type: DataTypes.STRING,
        allowNull: false
    },
    role: {
        type: DataTypes.INTEGER,
        allowNull: false
    }
});
sequelize.sync();
export default sequelize;