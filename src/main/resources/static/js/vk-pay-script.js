var params = {}
var xhr = new XMLHttpRequest();
xhr.open('POST', 'http://localhost:8080/api/v1/vk-pay/4', false);
xhr.send();
if (xhr.status != 200) {
        alert( xhr.status + ': ' + xhr.statusText );
}
params = xhr.responseText;
VK.App.open('vkpay', params);


