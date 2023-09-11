//----------------------------------------------
// WebSocket Chat
// ADVJ Lab 2022/23
//----------------------------------------------

//--- state variables
let nickname = null;
let connected = false;

// variables for interface elements
const statusDot = document.getElementById("divStatusDot");
const spanStatusMsg = document.getElementById("spanStatusMsg");
const inputNickname = document.getElementById("inputNickname");
const btnConnect = document.getElementById("btnConnect");
const msgList = document.getElementById("divMessages");
const ulMsgList = document.getElementById("ulMsgList");
const ulUserList = document.getElementById("ulUserList")
const inputMsg = document.getElementById("inputMsg");
const btnSend = document.getElementById("btnSend");

// initialise
updateState(connected);

//--- WEBSOCKET HANDLERS ---

// connect websocket
function connect() {
    const webAppPath = location.host + location.pathname.slice(0, location.pathname.lastIndexOf('/'));
    spanStatusMsg.innerHTML = "connecting ...";

    const url = "ws://" + webAppPath + "/ws/" + nickname
    console.log("Connect to: " + url)
    socket = new WebSocket(url);

    // add socket event handlers
    socket.onopen = socketOnOpen;
    socket.onmessage = socketOnMessage;
    socket.onclose = socketOnClose;
    window.onclose = socketOnClose;
}

function socketOnOpen(e) {
    console.log(e);
    connected = true;
    updateState(connected);
}

function socketOnMessage(e) {
    console.log("received:", e.data);
    const data = JSON.parse(e.data);

    // JSON message format { cmd: "newMsg", user: "nickname", msg: "message" }
    if (data.cmd == "newMsg") {
        ulMsgList.innerHTML += "<li class='msg'>&nbsp;<b>" + escapeHTML(data.nick) + "</b>: "
            + escapeHTML(data.msg) + "</li>";
    }

    // JSON message format { cmd: "newUser", user: "nickname", userList: ["user1", "user2", ...] }
    else if (data.cmd == "newUser") {
        ulMsgList.innerHTML += "<li class='join'>&nbsp;<i>" + escapeHTML(data.user) + " has joined</i></li>";
        updateUserList(data.userList);

    } else if (data.cmd == "delUser") {
        ulMsgList.innerHTML += "<li class='gone'>&nbsp;<i>" + escapeHTML(data.user) + " has left</i></li>";
        updateUserList(data.userList);
    }

    msgList.scrollTop = msgList.scrollHeight;
}

function socketOnClose(e) {
    console.log(e);
    connected = false;
    e.code === 4000 ? updateState(connected, e.reason) : updateState(connected);
}

function disconnect() {
    socket.close();
    socket = undefined;
    ulUserList.innerHTML = "none";

}

//--- BUTTON HANDLERS ---

btnConnect.onclick = async function () {
    console.log("Trigger connection/disconnection")
    const res = await validateNickname()
    if (res.available && !connected) {
        console.log("Validation ok, it will connect")
        connect();
    } else {
        console.log("Validation failed, it will not connect -----")
        updateState(connected, res.reason)
        disconnect()
    }

}
btnSend.onclick = function () {
    const message = inputMsg.value;
    if (message == '') return;
    json = JSON.stringify({cmd: "newMsg", nick: nickname, msg: message});
    console.log("sending:", json);
    socket.send(json);
    inputMsg.value = "";  // clear message input field
}
// Click on enter to send message
inputMsg.addEventListener("keyup", function (event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        btnSend.click();
    }
})


//--- UTILITY FUNCTIONS ---

// check if nickname is valid
async function validateNickname() {

    // check if nickname input field is empty
    const trimmedName = inputNickname.value.trim();
    if (trimmedName.length == 0) {
        spanStatusMsg.innerHTML = "no connection (missing nickname)";
        return {
            available: false,
            reason: "no connection (missing nickname)",
        };
    }
    nickname = trimmedName;

    const webAppPath = location.host + location.pathname.slice(0, location.pathname.lastIndexOf('/'));
    const url1 = `http://${webAppPath}/api/checkName?nickname=${nickname}`
    const url2 = "/checkName?nickname=" + nickname
    const url = url1
    console.log("Use url: " + url)

    const response = await fetch(url)
    const result = await response.json()
    if (!result.available) {
        updateState(connected, result.reason);
    }
    return result

}

// update visibility and content of HTML elements depending on connection state
function updateState(connected, notConnectedMsg = "") {
    inputMsg.disabled = !connected;
    btnSend.disabled = !connected;
    btnConnect.innerHTML = connected ? "disconnect" : "connect";
    spanStatusMsg.innerHTML = connected ? "connected" : "not connected " + notConnectedMsg;
    ulMsgList.innerHTML = connected ? "" : "none";
    if (connected) {
        statusDot.classList.add("green");
        // Disable nickname input field after successful connection
        inputNickname.disabled = true;
    } else {
        statusDot.classList.remove("green");
        // Enable nickname input field after disconnection
        inputNickname.disabled = false;
        // Clear nickname input field after disconnection
        inputNickname.value = "";
    }
}

// update user list
function updateUserList(users) {
    console.log("Users: " + users)
    ulUserList.innerHTML = "";
    users.forEach(u => {
        ulUserList.innerHTML += "<li class='user'>" + escapeHTML(u) + "</li>"
    })
}

// HTML escaping function
var textarea = document.createElement('textarea');

function escapeHTML(html) {
    textarea.textContent = html; // take advantage of automatic escaping
    return textarea.innerHTML;
}
