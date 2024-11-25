import fw from "./lib/core";
import { utils } from "./lib";
import path from "path";

const port = parseInt(process.env.PORT || "") || 3001;
const localhost = process.env.LOCALHOST || "localhost";
const gateway = process.env.GATEWAY || "http://localhost:3100";
const podName = `user-node-${Math.floor(Math.random() * 1000)}`;

export const app = new fw();
app.registerRouter(path.join(__dirname, "router"));
app.beforeRequest(utils.cors())
app.beforeRequest(utils.ignoreEndSlash)
app.onError(utils.errorHandler);
app.listen(port, () => {
    console.log(`Server started on port ${port}`);
});

setInterval(() => {
    fetch(`${gateway}/server/join`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            path: '/auth',
            auth: false,
            server: {
                "name": podName,
                "host": `http://${localhost}:${port}`,
                "usage": "1",
                "available": true
            }
        }),
    }).catch(err => {
        console.log('Error:拒绝加入worker节点', err);
    });
}, 3000);

