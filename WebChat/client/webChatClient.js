"use strict"

// If the page hasn't loaded you can't submit the message. Find a way to display a loading symbol or something until it has completely loaded.

/* Formula for finding the name of a specific cookie. Source: W3 schools.
TODO: Consider switching to AJAX/Javascript method if too much of a pain to
continue testing. */
let getCookie = function(cname) {
    let name = cname + "=";
    let ca = document.cookie.split(";");
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

let getSocket = function() {
    let url = "ws://"+location.host;
    let socket = new WebSocket(url);
    return socket;
}

let displayUserProfile = function() {
    let username = getCookie("username");
    username = username.toUpperCase();
    let userProfileName = document.createElement("p");
    userProfileName.innerText = username;
    let usernameContainer = document.getElementById("profileHeaderContainer");
    usernameContainer.appendChild(userProfileName);
}

displayUserProfile();

window.onload = function() {

    let sendButton = document.getElementById("send");
    let textField = document.getElementById("content");

    let socket = getSocket();

    socket.onopen = function(event) {

        /* NOTE: The server does not currently return any message when it receives
        the group request. This is something to add to the back end. */

        sendGroupRequest();
        addGroupsToDisplay();

        /* TODO: Wrap the sendButton event listener into a single function and call
        it here. This will eliminate the need to declare sendButton above. */

        textField.onclick = function(event) {
            this.defaultValue = "Write a message...";
            if (this.previousValue === undefined) {
                this.value = "";
            } else {
                this.value = this.previousValue;
            }
            this.originalColor = this.style.color;
            this.style.color = "#676A68";
        }

        textField.onblur = function(event) {
            this.previousValue = this.value;
            this.style.color = this.originalColor;
        }

        sendButton.onclick = function(event) {
            sendMessage();
            return false;
        }

    }
    
    /* testing
    let messageDisplay = document.getElementById("messageDisplay");
    messageDisplay.style.backgroundColor = "grey";
    let messageContainer = document.createElement("div");
    messageContainer.style.backgroundColor = "green";
    messageContainer.style.height = "50px";
    messageContainer.style.marginLeft = "40px";
    messageDisplay.appendChild(messageContainer);
    */

    socket.onmessage = function(event) {
        let message = event.data;
        display(message);
    }

// ------------------------------------------------------
// Refactored functions:
// ------------------------------------------------------

    let sendGroupRequest = function() {
        let group = getCookie("group");
        group = group.toLowerCase();
        let groupRequest = "join" + " " + group;
        console.log(groupRequest);
        socket.send(groupRequest);
    }

    let display = function(data) {
        data = JSON.parse(data);
        let message = data.message;
        let sender = data.user;
        let tag = getContentTag(message, sender);
        tag.innerText = message;
        updateView();
    }

    let getContentTag = function(message, sender) {

        let messageDisplay = document.getElementById("messageDisplay");

        let messageContainer = document.createElement("messageContainer");
        messageContainer.className = "messageContainer";

        let username = document.createElement("p");
        username.className = username;
        username.style.color = "#CDCDCD";
        username.style.fontSize = "small";

        let span = document.createElement("span");
        span.className = "span";
        span.innerText = " " + "time";
        span.style.color = "#CDCDCD";
        span.style.fontWeight = "300";
        span.style.fontSize = "small";

        username.innerText = sender;
        username.appendChild(span);
        console.log(username.innerHTML);

        /*
        let bubbleSpanContainer = document.createElement("div");
        bubbleSpanContainer.className = "bubbleSpanContainer";
        bubbleSpanContainer.style.backgroundColor = "grey";
        */

        let messageBubble = document.createElement("span");
        messageBubble.className = "messageBubble";

        let contentTag = document.createElement("p");
        contentTag.className = "messageContent";

        styleMessageContainer(messageContainer);
        styleMessageBubble(messageContainer, messageBubble, message);
        
        styleContentTag(contentTag);

        messageDisplay.appendChild(messageContainer);
        //messageContainer.appendChild(bubbleSpanContainer);
        //bubbleSpanContainer.appendChild(username);
        //bubbleSpanContainer.appendChild(messageBubble);
        messageContainer.appendChild(messageBubble);
        
        messageBubble.appendChild(contentTag);

        return contentTag;
    }

    let getFormattedMessage = function(contentTag) {
        let username = getCookie("username");
        let message = username + " " + contentTag.value;
        return message;
    }

    let sendMessage = function() {
        let contentTag = document.getElementById("content");
        let message = getFormattedMessage(contentTag);
        socket.send(message);
        contentTag.value = "";
    }

    let updateView = function() {
        let messageDisplay = document.getElementById("messageDisplay");
        messageDisplay.scrollTop = messageDisplay.scrollHeight;
    }
    
    let styleContentTag = function(element) {
        element.style.padding = "25px";
        element.style.color = "#FDFDFD";
    }

    let styleMessageBubble = function(messageContainer, element, message) {
        element.style.fontSize = "small";
        element.style.backgroundColor = "#1AC16F";
        element.style.borderRadius = "21px";
        element.style.minHeight = element.lineHeight;
        element.style.marginLeft = "50px";
        element.style.marginRight = "auto";
    }

    let styleMessageContainer = function(element) {
        //element.style.backgroundColor = "grey";
        element.style.marginTop = "20px";
        element.style.width = "60%";
        element.style.display = "flex";
        element.style.flexDirection = "row";
        element.style.marginBottom = "20px";
    }

    let addGroupsToDisplay = function() {
        let group = getCookie("group");
        group = group.toLowerCase();
        
        let newGroupContainer = document.getElementById("newGroup");
        newGroupContainer.style.backgroundColor = "#1AC16F";
        let newGroup = document.createElement("p");
        newGroup.style.color = "#FDFDFD";
        newGroup.style.paddingLeft = "30px";
        newGroup.style.paddingBottom = "10px";
        newGroup.style.paddingTop = "10px";
        newGroupContainer.appendChild(newGroup);
        newGroup.innerText = group;
    }
}

/*
<div id="group">
    <p>group</p>
</div>
#group, .removable {
    color: #CDCDCD;
    padding-left: 30px;
    padding-bottom: 20px;
}

<div id="containerWindow">
            <div id="navigationPanel">
                <div id="usernameContainer"></div>
                <script type="text/Javascript" src="webChatClient.js"></script>
                <div id="group">
                    <p>group</p>
                </div>
                <div class="spacer"></div>
                <div class="removable">
                    <p>robin.mango</p>
                </div>
                <div class="removable">
                    <p>kelsi_lewis</p>
                </div>
                <div class="removable">
                    <p>cali149</p>
                </div>
            </div>
*/