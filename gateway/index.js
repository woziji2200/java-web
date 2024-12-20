import express from 'express';
import httpProxy from 'express-http-proxy';
import cors from 'cors';
import fs from 'fs';
import jwt from 'jsonwebtoken';
const SECRET = process.env.SECRET || 'secret';

const app = express();
const port = process.env.PORT || 3100;
let servers = getServersConfig()


app.use(cors());
for (let item of servers.proxies) {
    console.log('proxy:', item.path, '-->', item.servers.map(i => i.host), item.auth ? '(auth)' : '');
    if (item.auth) {
        app.use(item.path, (req, res, next) => {
            const token = req.headers.authorization?.split(' ')[1] || '';
            if (token) {
                jwt.verify(token, SECRET, { algorithms: 'HS512' }, (err, decoded) => {
                    if (err) {
                        res.status(401).json({ message: 'Unauthorized', code: 401 });
                    } else {
                        next();
                    }
                });
            } else {
                res.status(401).json({ message: 'Unauthorized', code: 401 });
            }
        });
    }

    app.use(item.path, httpProxy(
        (req) => {
            const server = selectServerHost(item.path);
            return server;
        },
        {
            proxyReqOptDecorator: function (proxyReqOpts, srcReq) {
                // if (item.auth) {
                    const token = srcReq.headers.authorization?.split(' ')[1] || '';
                    jwt.verify(token, SECRET, { algorithms: 'HS512' }, (err, decoded) => {
                        // console.log(decoded);
                        
                        proxyReqOpts.headers['x-forward-id'] = (decoded?.id === undefined) ? '-1' : decoded.id;
                        // console.log('decoded:', decoded.role);
                        
                        proxyReqOpts.headers['x-forward-role'] = (decoded?.role === undefined)? '-1' : decoded.role;
                    });
                // }
                return proxyReqOpts;
            },
            userResDecorator: function (proxyRes, proxyResData, userReq, userRes) {
                // console.log('userRes:', userRes);
                // console.log('userReq:', userReq);
                // console.log('proxyRes:', proxyRes);
                // console.log('proxyResData:', proxyResData.toString());
                return proxyResData;
            }
        }));
}

app.use(express.json());
app.post("/server/config", (req, res) => {
    try {
        const body = req.body;
        fs.writeFileSync('./config.json', JSON.stringify(body));
        res.json({ message: 'ok' });
    } catch (error) {
        res.status(400).json({ message: error.message });
        return;
    }

});

app.get("/server/config", (req, res) => {
    res.json(getServersConfig());
});
app.post('/server/join', (req, res) => {
    try {
        let hasJoined = true;
        const { path, auth, server } = req.body;
        if (!servers.proxies.find(i => i.path === path)) {
            servers.proxies.push({
                path,
                auth,
                servers: []
            });
        }
        let sameServer = servers.proxies.find(i => i.path === path).servers.find(i => i.host === server.host);
        if (!sameServer) {
            hasJoined = false;
            servers.proxies.find(i => i.path === path).servers.push(server);
            sameServer = server;
        }
        setLastLogin(server.name);
        res.json({ message: 'ok' });
        if(!hasJoined){
            fs.writeFileSync('./config.json', JSON.stringify(servers));
        }

    } catch (error) {
        res.status(400).json({ message: error.message });
        return;
    }
});

app.get('/server/select', (req, res) => {
    const { path } = req.query;
    const server = selectServerHost(path);
    if (!server) {
        res.status(404).json({ message: 'Server not found' });
        return;
    }
    res.json({ server });
});


function selectServerHost(path) {
    const server = servers.proxies.find(i => i.path === path);
    if (!server) {
        return null;
    }
    return server.servers[Math.floor(Math.random() * server.servers.length)]?.host;
}



app.listen(port, () => {
    console.log('Server started on http://localhost:' + port);
    const serversCopy = structuredClone(servers);
    setInterval(() => {
        let isFiltered = false
        for (let item of servers.proxies) {
            item.servers = item.servers.filter(i => {
                const expired = new Date().getTime() - new Date(getLastLogin(i.name)).getTime() >= 1000 * 10;
                if(expired){
                    console.log(`${new Date().toISOString()}   ${i.name} (${i.host}) disconnected`);
                    isFiltered = true;
                }
                return !expired;
            });
        }
        
        // console.log('servers:', servers);
        // console.log('serversCopy:', serversCopy);
        
        
        if (isFiltered) {
            fs.writeFileSync('./config.json', JSON.stringify(servers));
            console.log('servers updated');
        }
    }, 1000 * 2);
});


function getServersConfig() {
    return JSON.parse(fs.readFileSync('./config.json'));
}

function setLastLogin(name){
    // save to local
    const lastLogin = JSON.parse(fs.readFileSync('./lastLogin.json'));
    lastLogin[name] = new Date().toISOString();
    fs.writeFileSync('./lastLogin.json', JSON.stringify(lastLogin));
}
function getLastLogin(name){
    // save to local
    const lastLogin = JSON.parse(fs.readFileSync('./lastLogin.json'));
    return lastLogin[name];
}


function deepEqual(obj1, obj2) {
    if (obj1 === obj2) return true;
    if (typeof obj1 !== 'object' || obj1 === null || typeof obj2 !== 'object' || obj2 === null) {
        return false;
    }
    const keys1 = Object.keys(obj1);
    const keys2 = Object.keys(obj2);
    if (keys1.length !== keys2.length) {
        return false;
    }
    for (const key of keys1) {
        if (!keys2.includes(key)) {
            return false;
        }
        if (!deepEqual(obj1[key], obj2[key])) {
            return false;
        }
    }
    return true;
}

function areProxiesEqual(proxy1, proxy2) {
    if (proxy1.path !== proxy2.path || proxy1.auth !== proxy2.auth) {
        return false;
    }

    if (proxy1.servers.length !== proxy2.servers.length) {
        return false;
    }

    for (let i = 0; i < proxy1.servers.length; i++) {
        const server1 = proxy1.servers[i];
        const server2 = proxy2.servers[i];

        if (
            server1.name !== server2.name ||
            server1.host !== server2.host ||
            server1.usage !== server2.usage ||
            server1.available !== server2.available
        ) {
            return false;
        }
    }

    return true;
}

function areObjectsEqual(obj1, obj2) {
    if (obj1.proxies.length !== obj2.proxies.length) {
        return false;
    }

    for (let i = 0; i < obj1.proxies.length; i++) {
        if (!areProxiesEqual(obj1.proxies[i], obj2.proxies[i])) {
            return false;
        }
    }

    return true;
}