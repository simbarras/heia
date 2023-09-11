document.addEventListener('DOMContentLoaded', filterActivation, false);

function filterActivation () {
    let filterItems = document.getElementsByClassName("filter-item");

    function filterClicked() {
        let selectItems = document.getElementsByClassName("icon-filter");
        Array.from(selectItems).forEach(function ( item) {
            item.classList.remove("filter-active");
        });

        try{
            let url_string = (window.location.href).toLowerCase();
            let url = new URL(url_string);
            var filterType = window.location.pathname.split('/');
            filterType = filterType[4].toLowerCase();
            console.log(filterType)
        }catch (err){
            console.log("Error with parsing of url");
        }

        document.getElementById(filterType).classList.remove("bi-filter");
        document.getElementById(filterType).classList.add("bi-filter-circle");
        document.getElementById(filterType).classList.add("filter-active");
    }

    Array.from(filterItems).forEach(function(filterItem) {
        filterItem.addEventListener('click', filterClicked(event));
    });
}

function downloadObjectAsJson(requestURL, exportName){
    fetch(requestURL)
        .then(res => res.json())
        .then((out) => {
            let dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(out));
            let downloadAnchorNode = document.createElement('a');
            downloadAnchorNode.setAttribute("href",     dataStr);
            downloadAnchorNode.setAttribute("download", exportName + ".json");
            document.body.appendChild(downloadAnchorNode); // required for firefox
            downloadAnchorNode.click();
            downloadAnchorNode.remove();
        })
        .catch(err => { throw err });
}

function downloadObjectAsXML(requestURL, exportName){
    fetch(requestURL)
        .then(res => res.text())
        .then((out) => {
            let element = document.createElement('a');
            element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(out));
            element.setAttribute('download', exportName);
            element.style.display = 'none';
            document.body.appendChild(element);
            element.click();
            document.body.removeChild(element);
        })
        .catch(err => { throw err });
}
