payment.onclick = function (){
        var param = {}
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "http://localhost:9000/api/v1/vk-pay/"+order_id, false);
        xhttp.setRequestHeader(header, token);
        xhttp.send();
        if (xhttp.status != 200) {
                alert( xhttp.status + ': ' + xhttp.statusText );
        }else {
                param = xhttp.responseText;
                VK.App.open('vkpay', param);
        }
};


