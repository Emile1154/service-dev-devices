
    (function() {
    'use strict';
    var cn = 'CheckBoxes', set = {}, cook = cookies(cn) || {};
    cookies.expires = 10 * 24 * 3600;

    function saveChecked() {
        cook[this.id] = this.checked;
        set[cn] = cook;
        cookies(set);
    };

    document.querySelectorAll('#ch input[type=checkbox]').forEach(function(i) {
        i.onchange = saveChecked;
        i.checked = !!cook[i.id];
    })
})();
