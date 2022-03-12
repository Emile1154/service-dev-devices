function check(field, n) {
    var input = document.getElementById('mark');
    if(n === 1){
        field[0].checked = true;
        input.value = n;
        return
    }
    let d = false;
    for(i=0; i<n-1; i++){
        if(field[i].checked){

        }else{
            d=true;
        }
    }
    for (i=0; i<n; i++) {
        field[i].checked = d;
    }
    input.value = n;
}